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

import org.finos.legend.depot.domain.entity.DepotEntity;
import org.finos.legend.depot.domain.entity.DepotEntityOverview;
import org.finos.legend.depot.store.mongo.TestStoreMongo;
import org.finos.legend.depot.store.mongo.entities.test.EntitiesMongoTestUtils;
import org.finos.legend.sdlc.domain.model.entity.Entity;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.finos.legend.depot.domain.version.VersionValidator.BRANCH_SNAPSHOT;

public class TestQueryRevisions extends TestStoreMongo
{
    private EntitiesMongo revisionsMongo = new EntitiesMongo(mongoProvider);
    private EntitiesMongoTestUtils entityUtils = new EntitiesMongoTestUtils(mongoProvider);

    @BeforeEach
    public void setupMetadata()
    {
        entityUtils.loadEntities(this.getClass().getClassLoader().getResource("data/revision-entities.json"));
    }


    @Test
    public void canQueryAllEntityMetadataByProjectVersion()
    {
        List<Entity> entityList = revisionsMongo.getAllEntities("examples.metadata", "test",BRANCH_SNAPSHOT("master"));
        Assertions.assertNotNull(entityList);
        Assertions.assertEquals(4, entityList.size());
        List<String> paths = new ArrayList<>();
        entityList.forEach(entity -> paths.add(entity.getPath()));
        Assertions.assertEquals(4, paths.size());
        Assertions.assertTrue(paths.contains("examples::metadata::test::TestProfile"));
        Assertions.assertTrue(paths.contains("examples::metadata::test::ClientBasic"));
    }

    @Test
    public void canQueryEntityMetadataByProjectVersion()
    {
        List<Entity> entityList = revisionsMongo.getAllEntities("examples.metadata", "test", BRANCH_SNAPSHOT("master"));
        Assertions.assertNotNull(entityList);
        Assertions.assertEquals(4, entityList.size());
    }

    @Test
    public void canQueryEntityMetadataByProjectVersionPath()
    {
        Entity entity = (Entity) revisionsMongo.getEntity("examples.metadata", "test", BRANCH_SNAPSHOT("master"),"examples::metadata::test::TestProfile").get();
        Assertions.assertNotNull(entity);
        Assertions.assertEquals("examples::metadata::test::TestProfile", entity.getPath());
        Assertions.assertEquals("meta::pure::metamodel::extension::Profile", entity.getClassifierPath());
        Assertions.assertEquals("examples::metadata::test", entity.getContent().get("package"));

    }

    @Test
    public void canQueryEntityMetadataByProjectVersionPackage()
    {
        List<Entity> entities = revisionsMongo.getEntitiesByPackage("examples.metadata", "test", BRANCH_SNAPSHOT("master"),"examples::metadata::test", null, false);
        Assertions.assertNotNull(entities);
        Assertions.assertEquals(3, entities.size());
        for (Entity entity : entities)
        {
            Assertions.assertEquals("examples::metadata::test", entity.getContent().get("package"));
        }
    }


    @Test
    public void canQueryEntityMetadataByProjectSubPackage()
    {
        List<Entity> entities = revisionsMongo.getEntitiesByPackage("examples.metadata", "test", BRANCH_SNAPSHOT("master"),"examples::metadata::test", null, true);
        Assertions.assertNotNull(entities);
        Assertions.assertEquals(4, entities.size());
        for (Entity entity : entities)
        {
            Assertions.assertTrue(entity.getContent().get("package").toString().startsWith("examples::metadata::test"));
        }
    }

    @Test
    public void canQueryByClassifier()
    {
        List<DepotEntity> entities = revisionsMongo.findLatestClassifierSummaries("meta::pure::metamodel::type::Class");
        Assertions.assertNotNull(entities);
        Assertions.assertEquals(2, entities.size());
        for (DepotEntity entity : entities)
        {
            Assertions.assertEquals("meta::pure::metamodel::type::Class", ((DepotEntityOverview)entity).getClassifierPath());
        }
    }
}
