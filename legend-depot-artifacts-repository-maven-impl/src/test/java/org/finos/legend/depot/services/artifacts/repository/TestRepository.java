//  Copyright 2021 Goldman Sachs
//
//  Licensed under the Apache License, Version 2.0 (the "License");
//  you may not use this file except in compliance with the License.
//  You may obtain a copy of the License at
//
//       http://www.apache.org/licenses/LICENSE-2.0
//
//  Unless required by applicable law or agreed to in writing, software
//  distributed under the License is distributed on an "AS IS" BASIS,
//  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//  See the License for the specific language governing permissions and
//  limitations under the License.
//

package org.finos.legend.depot.services.artifacts.repository;

import org.finos.legend.depot.services.api.artifacts.repository.ArtifactNotFoundException;
import org.finos.legend.depot.services.api.artifacts.repository.ArtifactRepository;
import org.finos.legend.depot.domain.artifacts.repository.ArtifactDependency;
import org.finos.legend.depot.domain.artifacts.repository.ArtifactType;
import org.finos.legend.depot.services.artifacts.repository.maven.TestMavenArtifactsRepository;
import org.finos.legend.sdlc.domain.model.version.VersionId;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.List;
import java.util.Set;

public class TestRepository
{
    public static final String GROUP_ID = "examples.metadata";
    private ArtifactRepository repository = new TestMavenArtifactsRepository();

    @Test
    public void canResolveVersionsRanges()
    {
        List<VersionId> versions = null;
        try
        {
            versions = repository.findVersions("examples.metadata", "test");
        }
        catch (Exception e)
        {
            Assertions.fail();
        }

        Assertions.assertNotNull(versions);
        Assertions.assertEquals(2, versions.size());
    }

    @Test
    public void canResolveVersionsRangesForProjectsWithBranches()
    {
        List<VersionId> versions = null;
        try
        {
            versions = repository.findVersions(GROUP_ID, "test");
        }
        catch (Exception e)
        {
            Assertions.fail();
        }

        Assertions.assertNotNull(versions);
        Assertions.assertEquals(2, versions.size());
    }

    @Test
    public void returnsNoVersionsIfTheyDontExists()
    {
        try
        {
            repository.findVersions(GROUP_ID, "test");
        }
        catch (Exception e)
        {
            Assertions.assertTrue(e instanceof ArtifactNotFoundException);
        }
    }


    @Test
    public void canGetDependencies()
    {
        Set<ArtifactDependency> dependencies = repository.findDependencies(GROUP_ID, "test", "1.0.0");
        Assertions.assertEquals(1, dependencies.size());
        Assertions.assertEquals(GROUP_ID, dependencies.stream().findFirst().get().getGroupId());
        Assertions.assertEquals("test-dependencies", dependencies.stream().findFirst().get().getArtifactId());
        Assertions.assertEquals("1.0.0", dependencies.stream().findFirst().get().getVersion());
    }

    @Test
    public void canGetFileDependenciesByType()
    {
        Set<ArtifactDependency> dependencies = repository.findDependenciesByArtifactType(ArtifactType.ENTITIES,GROUP_ID, "test", "1.0.0");
        Assertions.assertEquals(1, dependencies.size());
        Assertions.assertEquals(GROUP_ID, dependencies.stream().findFirst().get().getGroupId());
        Assertions.assertEquals("test-dependencies-entities", dependencies.stream().findFirst().get().getArtifactId());
        Assertions.assertEquals("1.0.0", dependencies.stream().findFirst().get().getVersion());

        Set<ArtifactDependency> dependencySet = repository.findDependenciesByArtifactType(ArtifactType.VERSIONED_ENTITIES,GROUP_ID, "test", "1.0.0");
        Assertions.assertEquals(1, dependencySet.size());
        Assertions.assertEquals(GROUP_ID, dependencySet.stream().findFirst().get().getGroupId());
        Assertions.assertEquals("test-dependencies-versioned-entities", dependencySet.stream().findFirst().get().getArtifactId());
        Assertions.assertEquals("1.0.0", dependencySet.stream().findFirst().get().getVersion());
    }

    @Test
    public void canFindFilesByTpe()
    {
       List<File> files = repository.findFiles(ArtifactType.ENTITIES,GROUP_ID,"test","1.0.0");
       Assertions.assertNotNull(files);
       Assertions.assertEquals(1,files.size());
       Assertions.assertEquals("test-entities-1.0.0.jar",files.get(0).getName());

        List<File> filesForVersionedEntities = repository.findFiles(ArtifactType.VERSIONED_ENTITIES,GROUP_ID,"test","1.0.0");
        Assertions.assertNotNull(filesForVersionedEntities);
        Assertions.assertEquals(1,filesForVersionedEntities.size());
        Assertions.assertEquals("test-versioned-entities-1.0.0.jar",filesForVersionedEntities.get(0).getName());
    }
}
