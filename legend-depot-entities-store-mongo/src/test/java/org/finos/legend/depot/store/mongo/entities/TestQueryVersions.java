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

import org.finos.legend.depot.store.model.entities.StoredEntity;
import org.finos.legend.depot.store.mongo.TestStoreMongo;
import org.finos.legend.depot.store.mongo.entities.test.EntitiesMongoTestUtils;
import org.finos.legend.sdlc.domain.model.entity.Entity;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class TestQueryVersions extends TestStoreMongo
{

    private EntitiesMongo versionsMongo = new EntitiesMongo(mongoProvider);
    private EntitiesMongoTestUtils entityUtils = new EntitiesMongoTestUtils(mongoProvider);

    @BeforeEach
    public void setupMetadata()
    {

        entityUtils.loadEntities(this.getClass().getClassLoader().getResource("data/versioned-entities.json"));
        entityUtils.loadEntities(this.getClass().getClassLoader().getResource("data/revision-entities.json"));
    }


    @Test
    public void canQueryEntityMetadataByProjectVersion()
    {
        List<Entity> entityList = versionsMongo.getAllEntities("examples.metadata", "test", "2.2.0");
        Assertions.assertNotNull(entityList);
        Assertions.assertEquals(3, entityList.size());
        List<String> paths = new ArrayList<>();
        entityList.forEach(entity -> paths.add(entity.getPath()));
        Assertions.assertEquals(3, paths.size());
        Assertions.assertTrue(paths.contains("examples::metadata::test::TestProfile"));
        Assertions.assertTrue(paths.contains("examples::metadata::test::ClientBasic"));

    }

    @Test
    public void canQueryEntityMetadataByProjectVersionPath()
    {
        Entity entity = (Entity) versionsMongo.getEntity("examples.metadata", "test", "2.2.0", "examples::metadata::test::TestProfile").get();
        Assertions.assertNotNull(entity);
        Assertions.assertEquals("examples::metadata::test::TestProfile", entity.getPath());
        Assertions.assertEquals("meta::pure::metamodel::extension::Profile", entity.getClassifierPath());
        Assertions.assertEquals("examples::metadata::test", entity.getContent().get("package"));

    }

    @Test
    public void canQueryEntityMetadataByProjectVersionPackageAll()
    {
        List<Entity> entities = versionsMongo.getEntitiesByPackage("examples.metadata", "test", "2.2.0", null,  null, false);
        Assertions.assertNotNull(entities);
        Assertions.assertEquals(3, entities.size());
        for (Entity entity : entities)
        {
            Assertions.assertTrue(entity.getContent().get("package").toString().startsWith("examples::metadata::test"));
        }
    }

    @Test
    public void canQueryEntityMetadataByProjectVersionPackage()
    {
        List<Entity> entities = versionsMongo.getEntitiesByPackage("examples.metadata", "test", "2.2.0", "examples::metadata::test",  null, false);
        Assertions.assertNotNull(entities);
        Assertions.assertEquals(2, entities.size());
        for (Entity entity : entities)
        {
            Assertions.assertEquals("examples::metadata::test", entity.getContent().get("package"));
        }
    }

    @Test
    public void canQueryEntityMetadataByProjectVersionAndSubPackage()
    {
        List<Entity> entities = versionsMongo.getEntitiesByPackage("examples.metadata", "test", "2.2.0", "examples::metadata::test",  null, true);
        Assertions.assertNotNull(entities);
        Assertions.assertEquals(3, entities.size());
        for (Entity entity : entities)
        {
            Assertions.assertTrue(entity.getContent().get("package").toString().startsWith("examples::metadata::test"));
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
                "examples::metadata::test",
                classifiers, true);
        Assertions.assertNotNull(entities);
        Assertions.assertEquals(2, entities.size());
        for (Entity entity : entities)
        {
            Assertions.assertEquals("meta::pure::metamodel::extension::Profile", entity.getClassifierPath());
        }
    }

    @Test
    public void getMasterVersionWithoutVersionInPath()
    {
        List<StoredEntity> entities = versionsMongo.getStoredEntities("examples.metadata", "test");
        Assertions.assertNotNull(entities);
        Assertions.assertEquals(7, entities.size());

        List<Entity> withoutVersions = versionsMongo.getAllEntities("examples.metadata", "test", "2.2.0");
        Assertions.assertNotNull(withoutVersions);
        Set<String> allPaths = withoutVersions.stream().map(Entity::getPath).collect(Collectors.toSet());
        Assertions.assertTrue(allPaths.stream().noneMatch(ent -> ent.contains("v2_2_0")));
    }


}
