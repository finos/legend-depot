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
import org.finos.legend.depot.domain.entity.StoredEntityOverview;
import org.finos.legend.depot.store.mongo.TestStoreMongo;
import org.finos.legend.sdlc.domain.model.entity.Entity;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.finos.legend.depot.domain.version.VersionValidator.BRANCH_SNAPSHOT;

public class TestQueryRevisions extends TestStoreMongo
{
    private EntitiesMongo revisionsMongo = new EntitiesMongo(mongoProvider);

    @Before
    public void setupMetadata()
    {
        setUpEntitiesDataFromFile(this.getClass().getClassLoader().getResource("data/revision-entities.json"));
    }


    @Test
    public void canQueryAllEntityMetadataByProjectVersion()
    {
        List<Entity> entityList = revisionsMongo.getAllEntities("examples.metadata", "test",BRANCH_SNAPSHOT("master"));
        Assert.assertNotNull(entityList);
        Assert.assertEquals(8, entityList.size());
        List<String> paths = new ArrayList<>();
        entityList.forEach(entity -> paths.add(entity.getPath()));
        Assert.assertEquals(8, paths.size());
        Assert.assertTrue(paths.contains("examples::metadata::test::TestProfile"));
        Assert.assertTrue(paths.contains("examples::metadata::test::ClientBasic"));
    }

    @Test
    public void canQueryEntityMetadataByProjectVersion()
    {
        List<Entity> entityList = revisionsMongo.getAllEntities("examples.metadata", "test", BRANCH_SNAPSHOT("master"));
        Assert.assertNotNull(entityList);
        Assert.assertEquals(8, entityList.size());
    }

    @Test
    public void canQueryEntityMetadataByProjectVersionVersionInPath()
    {
        List<Entity> entityList = revisionsMongo.getEntities("examples.metadata", "test", BRANCH_SNAPSHOT("master"),true);
        Assert.assertNotNull(entityList);
        Assert.assertEquals(4, entityList.size());
    }

    @Test
    public void canQueryEntityMetadataByProjectVersionPath()
    {
        Entity entity = revisionsMongo.getEntity("examples.metadata", "test", BRANCH_SNAPSHOT("master"),"examples::metadata::test::TestProfile").get();
        Assert.assertNotNull(entity);
        Assert.assertEquals("examples::metadata::test::TestProfile", entity.getPath());
        Assert.assertEquals("meta::pure::metamodel::extension::Profile", entity.getClassifierPath());
        Assert.assertEquals("examples::metadata::test", entity.getContent().get("package"));

    }

    @Test
    public void canQueryEntityMetadataByProjectVersionPackage()
    {
        List<Entity> entities = revisionsMongo.getEntitiesByPackage("examples.metadata", "test", BRANCH_SNAPSHOT("master"),"examples::metadata::test", false, null, false);
        Assert.assertNotNull(entities);
        Assert.assertEquals(3, entities.size());
        for (Entity entity : entities)
        {
            Assert.assertEquals("examples::metadata::test", entity.getContent().get("package"));
        }
    }


    @Test
    public void canQueryEntityMetadataByProjectSubPackage()
    {
        List<Entity> entities = revisionsMongo.getEntitiesByPackage("examples.metadata", "test", BRANCH_SNAPSHOT("master"),"examples::metadata::test", false, null, true);
        Assert.assertNotNull(entities);
        Assert.assertEquals(4, entities.size());
        for (Entity entity : entities)
        {
            Assert.assertTrue(entity.getContent().get("package").toString().startsWith("examples::metadata::test"));
        }
    }

    @Test
    public void canQueryEntityMetadataByProjectVersionPackages()
    {
        List<Entity> entities = revisionsMongo.getEntitiesByPackage("examples.metadata", "test", BRANCH_SNAPSHOT("master"),"examples::metadata::test::vX_X_X::examples::metadata::test", true, null, false);
        Assert.assertNotNull(entities);
        Assert.assertEquals(3, entities.size());
        for (Entity entity : entities)
        {
            Assert.assertEquals("examples::metadata::test::vX_X_X::examples::metadata::test", entity.getContent().get("package"));
        }
    }


    @Test
    public void canQueryEntityMetadataByProjectVersionPackageVersionedSubpackages()
    {
        List<Entity> entities = revisionsMongo.getEntitiesByPackage("examples.metadata", "test", BRANCH_SNAPSHOT("master"),"examples::metadata::test::vX_X_X::examples::metadata::test", true, null, true);
        Assert.assertNotNull(entities);
        Assert.assertEquals(4, entities.size());
        for (Entity entity : entities)
        {
            Assert.assertTrue(entity.getContent().get("package").toString().startsWith("examples::metadata::test::vX_X_X::examples::metadata::test"));
        }
    }

    @Test
    public void canQueryByClassifier()
    {
    //    {
//        setUpEntitiesDataFromFile(ENTITIES_FILE);
        List<StoredEntity> entities = revisionsMongo.findLatestEntitiesByClassifier("meta::pure::metamodel::type::Class", null, null, true, true);
        Assert.assertNotNull(entities);
        Assert.assertEquals(2, entities.size());
        for (StoredEntity entity : entities)
        {
            Assert.assertEquals("meta::pure::metamodel::type::Class", ((StoredEntityOverview)entity).getClassifierPath());
        }
    }
}
