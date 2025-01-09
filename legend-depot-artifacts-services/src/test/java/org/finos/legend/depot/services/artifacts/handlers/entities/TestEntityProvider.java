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

package org.finos.legend.depot.services.artifacts.handlers.entities;

import org.apache.maven.model.Model;
import org.finos.legend.depot.services.api.artifacts.repository.ArtifactRepository;
import org.finos.legend.depot.domain.artifacts.repository.ArtifactDependency;
import org.finos.legend.depot.domain.artifacts.repository.ArtifactType;
import org.finos.legend.depot.services.artifacts.handlers.entities.EntityProvider;
import org.finos.legend.depot.services.artifacts.repository.maven.TestMavenArtifactsRepository;
import org.finos.legend.depot.services.api.artifacts.handlers.entties.EntityArtifactsProvider;
import org.finos.legend.sdlc.domain.model.entity.Entity;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.List;
import java.util.Set;

public class TestEntityProvider
{

    public static final String TEST_GROUP_ID = "examples.metadata";
    private final ArtifactRepository repository = new TestMavenArtifactsRepository();
    private final EntityArtifactsProvider artifactProvider = new EntityProvider();

    List<File> getFiles(String group, String artifact, String version)
    {
        return repository.findFiles(ArtifactType.ENTITIES, group, artifact, version);
    }


    @Test
    public void canResolvePOM()
    {
        Model pom = repository.getPOM(TEST_GROUP_ID, "test-dependencies-entities", "1.0.0");
        Assertions.assertNotNull(pom);
        Assertions.assertNotNull(pom.getParent());

    }

    @Test
    public void canResolveEntitiesInJar()
    {

        List<Entity> entities = artifactProvider.extractArtifacts(getFiles(TEST_GROUP_ID, "test", "2.0.0"));
        Assertions.assertNotNull(entities);
        Assertions.assertEquals(9, entities.size());

    }


    @Test
    public void canResolveDependencies()
    {
        Set<ArtifactDependency> dependencies = repository.findDependenciesByArtifactType(ArtifactType.ENTITIES, TEST_GROUP_ID, "test", "2.0.0");
        Assertions.assertEquals(1, dependencies.size());
        List<File> files = repository.findDependenciesFiles(ArtifactType.ENTITIES, TEST_GROUP_ID, "test", "2.0.0");
        Assertions.assertNotNull(files);
        Assertions.assertEquals(1, files.size());

        Set<ArtifactDependency> dependenciesForVersionedEntities = repository.findDependenciesByArtifactType(ArtifactType.VERSIONED_ENTITIES, TEST_GROUP_ID, "test", "2.0.0");
        Assertions.assertEquals(1, dependenciesForVersionedEntities.size());
        List<File> versionedEntitiesFiles = repository.findDependenciesFiles(ArtifactType.ENTITIES, TEST_GROUP_ID, "test", "2.0.0");
        Assertions.assertNotNull(versionedEntitiesFiles);
        Assertions.assertEquals(1, versionedEntitiesFiles.size());


    }

    @Test
    public void canResolveJar()
    {
        File jarFile = repository.getJarFile(TEST_GROUP_ID, "test-dependencies-entities", "1.0.0");
        Assertions.assertNotNull(jarFile);
    }

    @Test
    public void canHandleJarNotPresent()
    {
        File jarFile = repository.getJarFile(TEST_GROUP_ID, "test-non-existing-entities", "1.0.0");
        Assertions.assertNull(jarFile);
    }
}
