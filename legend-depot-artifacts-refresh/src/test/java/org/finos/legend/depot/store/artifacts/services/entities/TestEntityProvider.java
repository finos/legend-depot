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

package org.finos.legend.depot.store.artifacts.services.entities;

import org.apache.maven.model.Model;
import org.finos.legend.depot.artifacts.repository.api.ArtifactRepository;
import org.finos.legend.depot.artifacts.repository.domain.ArtifactDependency;
import org.finos.legend.depot.artifacts.repository.domain.ArtifactType;

import org.finos.legend.depot.artifacts.repository.maven.impl.TestMavenArtifactsRepository;
import org.finos.legend.depot.store.artifacts.api.entities.EntityArtifactsProvider;
import org.finos.legend.sdlc.domain.model.entity.Entity;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.util.List;
import java.util.Set;

public class TestEntityProvider
{

    public static final String TEST_GROUP_ID = "examples.metadata";
    private ArtifactRepository repository = new TestMavenArtifactsRepository();
    private EntityArtifactsProvider artifactProvider = new EntityProvider();

    List<File> getFiles(String group, String artifact, String version)
    {
        return repository.findFiles(ArtifactType.ENTITIES, group, artifact, version);
    }


    @Test
    public void canResolvePOM()
    {
        Model pom = repository.getPOM(TEST_GROUP_ID, "test-dependencies-entities", "1.0.0");
        Assert.assertNotNull(pom);
        Assert.assertNotNull(pom.getParent());

    }

    @Test
    public void canResolveEntitiesInJar()
    {

        List<Entity> entities = artifactProvider.loadArtifacts(getFiles(TEST_GROUP_ID, "test", "2.0.0"));
        Assert.assertNotNull(entities);
        Assert.assertEquals(18, entities.size());

    }


    @Test
    public void canResolveDependencies()
    {
        Set<ArtifactDependency> dependencies = repository.findDependencies(ArtifactType.ENTITIES, TEST_GROUP_ID, "test", "2.0.0");
        Assert.assertEquals(2, dependencies.size());
        List<File> files = repository.findDependenciesFiles(ArtifactType.ENTITIES, TEST_GROUP_ID, "test", "2.0.0");
        Assert.assertNotNull(files);
        Assert.assertEquals(2, files.size());
    }

}
