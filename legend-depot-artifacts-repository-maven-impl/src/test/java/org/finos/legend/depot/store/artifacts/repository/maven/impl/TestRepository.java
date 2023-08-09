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

package org.finos.legend.depot.store.artifacts.repository.maven.impl;

import org.finos.legend.depot.store.artifacts.repository.api.ArtifactNotFoundException;
import org.finos.legend.depot.store.artifacts.repository.api.ArtifactRepository;
import org.finos.legend.depot.store.artifacts.repository.domain.ArtifactDependency;
import org.finos.legend.depot.store.artifacts.repository.domain.ArtifactType;
import org.finos.legend.sdlc.domain.model.version.VersionId;
import org.junit.Assert;
import org.junit.Test;

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
            Assert.fail();
        }

        Assert.assertNotNull(versions);
        Assert.assertEquals(2, versions.size());
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
            Assert.fail();
        }

        Assert.assertNotNull(versions);
        Assert.assertEquals(2, versions.size());
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
            Assert.assertTrue(e instanceof ArtifactNotFoundException);
        }
    }


    @Test
    public void canGetDependencies()
    {
        Set<ArtifactDependency> dependencies = repository.findDependencies(GROUP_ID, "test", "1.0.0");
        Assert.assertEquals(1, dependencies.size());
        Assert.assertEquals(GROUP_ID, dependencies.stream().findFirst().get().getGroupId());
        Assert.assertEquals("test-dependencies", dependencies.stream().findFirst().get().getArtifactId());
        Assert.assertEquals("1.0.0", dependencies.stream().findFirst().get().getVersion());
    }

    @Test
    public void canGetFileDependenciesByType()
    {
        Set<ArtifactDependency> dependencies = repository.findDependenciesByArtifactType(ArtifactType.ENTITIES,GROUP_ID, "test", "1.0.0");
        Assert.assertEquals(1, dependencies.size());
        Assert.assertEquals(GROUP_ID, dependencies.stream().findFirst().get().getGroupId());
        Assert.assertEquals("test-dependencies-entities", dependencies.stream().findFirst().get().getArtifactId());
        Assert.assertEquals("1.0.0", dependencies.stream().findFirst().get().getVersion());

        Set<ArtifactDependency> dependencySet = repository.findDependenciesByArtifactType(ArtifactType.VERSIONED_ENTITIES,GROUP_ID, "test", "1.0.0");
        Assert.assertEquals(1, dependencySet.size());
        Assert.assertEquals(GROUP_ID, dependencySet.stream().findFirst().get().getGroupId());
        Assert.assertEquals("test-dependencies-versioned-entities", dependencySet.stream().findFirst().get().getArtifactId());
        Assert.assertEquals("1.0.0", dependencySet.stream().findFirst().get().getVersion());
    }

    @Test
    public void canFindFilesByTpe()
    {
       List<File> files = repository.findFiles(ArtifactType.ENTITIES,GROUP_ID,"test","1.0.0");
       Assert.assertNotNull(files);
       Assert.assertEquals(1,files.size());
       Assert.assertEquals("test-entities-1.0.0.jar",files.get(0).getName());

        List<File> filesForVersionedEntities = repository.findFiles(ArtifactType.VERSIONED_ENTITIES,GROUP_ID,"test","1.0.0");
        Assert.assertNotNull(filesForVersionedEntities);
        Assert.assertEquals(1,filesForVersionedEntities.size());
        Assert.assertEquals("test-versioned-entities-1.0.0.jar",filesForVersionedEntities.get(0).getName());
    }
}
