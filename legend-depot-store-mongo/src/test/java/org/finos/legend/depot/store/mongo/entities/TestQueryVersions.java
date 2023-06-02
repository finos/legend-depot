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

package org.finos.legend.depot.store.mongo.entities;

import org.finos.legend.depot.domain.entity.StoredEntity;
import org.finos.legend.depot.store.mongo.TestStoreMongo;
import org.finos.legend.sdlc.domain.model.entity.Entity;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.finos.legend.depot.domain.version.VersionValidator.MASTER_SNAPSHOT;


public class TestQueryVersions extends TestStoreMongo
{

    private EntitiesMongo versionsMongo = new EntitiesMongo(mongoProvider);

    @Before
    public void setupMetadata()
    {
        setUpEntitiesDataFromFile(this.getClass().getClassLoader().getResource("data/versioned-entities.json"));
        setUpEntitiesDataFromFile(this.getClass().getClassLoader().getResource("data/revision-entities.json"));
    }


    @Test
    public void canQueryEntityMetadataByProjectVersion()
    {
        List<Entity> entityList = versionsMongo.getAllEntities("examples.metadata", "test", "2.2.0");
        Assert.assertNotNull(entityList);
        Assert.assertEquals(3, entityList.size());
        List<String> paths = new ArrayList<>();
        entityList.forEach(entity -> paths.add(entity.getPath()));
        Assert.assertEquals(3, paths.size());
        Assert.assertTrue(paths.contains("examples::metadata::test::TestProfile"));
        Assert.assertTrue(paths.contains("examples::metadata::test::ClientBasic"));

    }

    @Test
    public void canQueryEntityMetadataByProjectVersionPath()
    {
        Entity entity = versionsMongo.getEntity("examples.metadata", "test", "2.2.0", "examples::metadata::test::TestProfile").get();
        Assert.assertNotNull(entity);
        Assert.assertEquals("examples::metadata::test::TestProfile", entity.getPath());
        Assert.assertEquals("meta::pure::metamodel::extension::Profile", entity.getClassifierPath());
        Assert.assertEquals("examples::metadata::test", entity.getContent().get("package"));

    }

    @Test
    public void canQueryEntityMetadataByProjectVersionPackage()
    {
        List<Entity> entities = versionsMongo.getEntitiesByPackage("examples.metadata", "test", "2.2.0", "examples::metadata::test", false, null, false);
        Assert.assertNotNull(entities);
        Assert.assertEquals(2, entities.size());
        for (Entity entity : entities)
        {
            Assert.assertEquals("examples::metadata::test", entity.getContent().get("package"));
        }
    }

    @Test
    public void canQueryEntityMetadataByProjectVersionAndSubPackage()
    {
        List<Entity> entities = versionsMongo.getEntitiesByPackage("examples.metadata", "test", "2.2.0", "examples::metadata::test", false, null, true);
        Assert.assertNotNull(entities);
        Assert.assertEquals(3, entities.size());
        for (Entity entity : entities)
        {
            Assert.assertTrue(entity.getContent().get("package").toString().startsWith("examples::metadata::test"));
        }
    }

    @Test
    public void canQueryEntityMetadataByClassifierPath()
    {
        Set<String> classifiers = new HashSet<>();
        classifiers.add("meta::pure::metamodel::extension::Profile");
        List<Entity> entities = versionsMongo.getEntitiesByPackage(
                "examples.metadata", "test",
                "2.2.0",
                "examples::metadata::test", false,
                classifiers, true);
        Assert.assertNotNull(entities);
        Assert.assertEquals(2, entities.size());
        for (Entity entity : entities)
        {
            Assert.assertEquals("meta::pure::metamodel::extension::Profile", entity.getClassifierPath());
        }
    }


    @Test
    public void getEntitiesWithVersionInPath()
    {
        List<StoredEntity> entities = versionsMongo.getStoredEntities("examples.metadata", "test");
        Assert.assertNotNull(entities);

        Set<String> paths = entities.stream().map(en -> en.getEntity().getPath()).collect(Collectors.toSet());

        List<Entity> withoutVersions = versionsMongo.getEntities("examples.metadata", "test", MASTER_SNAPSHOT, true);
        Assert.assertNotNull(withoutVersions);
        Set<String> allPaths = withoutVersions.stream().map(Entity::getPath).collect(Collectors.toSet());

        Assert.assertTrue(allPaths.stream().anyMatch(ent -> ent.contains("vX_X_X")));
    }

    @Test
    public void getMasterVersionWithoutVersionInPath()
    {
        List<StoredEntity> entities = versionsMongo.getStoredEntities("examples.metadata", "test");
        Assert.assertNotNull(entities);
        Assert.assertEquals(11, entities.size());

        List<Entity> withoutVersions = versionsMongo.getEntities("examples.metadata", "test", "2.2.0", false);
        Assert.assertNotNull(withoutVersions);
        Set<String> allPaths = withoutVersions.stream().map(Entity::getPath).collect(Collectors.toSet());
        Assert.assertTrue(allPaths.stream().noneMatch(ent -> ent.contains("v2_2_0")));
    }


}
