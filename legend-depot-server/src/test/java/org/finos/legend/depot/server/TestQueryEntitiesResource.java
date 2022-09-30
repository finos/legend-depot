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

package org.finos.legend.depot.server;

import org.finos.legend.depot.server.resources.entities.EntitiesResource;
import org.finos.legend.depot.services.TestBaseServices;
import org.finos.legend.depot.store.metrics.QueryMetricsContainer;
import org.finos.legend.depot.store.metrics.store.mongo.MongoQueryMetrics;
import org.finos.legend.sdlc.domain.model.entity.Entity;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.finos.legend.depot.domain.version.VersionValidator.MASTER_SNAPSHOT;

public class TestQueryEntitiesResource extends TestBaseServices
{
    private EntitiesResource entitiesResource = new EntitiesResource(entitiesService);
    private MongoQueryMetrics queryMetrics = new MongoQueryMetrics(mongoProvider);

    @Before
    public void setupMetadata()
    {
        super.setUpData();
        QueryMetricsContainer.flush();
        queryMetrics.getCollection().drop();
        loadEntities("PROD-A", "2.3.0");
        loadEntities("PROD-A", MASTER_SNAPSHOT);
    }

    @After
    public void tearDown()
    {
        queryMetrics.getCollection().drop();
        QueryMetricsContainer.flush();
    }

    @Test
    public void canGetEntitiesForProjectAndVersion()
    {
        List<Entity> entityList = entitiesResource.getEntities("examples.metadata", "test", "2.3.0", false);
        Assert.assertNotNull(entityList);
        Assert.assertEquals(7, entityList.size());
    }

    @Test
    public void canGetEntityByPathForProjectAndVersion()
    {
        Entity entity = entitiesResource.getEntity("examples.metadata", "test", "2.3.0", "examples::metadata::test::TestProfile").get();
        Assert.assertNotNull(entity);
        Assert.assertEquals("meta::pure::metamodel::extension::Profile", entity.getClassifierPath());

    }

    @Test
    public void canGetEntitiesByPackageForProjectAndVersion()
    {
        List<Entity> entityList = entitiesResource.getEntities("examples.metadata", "test", "2.3.0", "examples::metadata::test", false, null, true);
        Assert.assertNotNull(entityList);
        Assert.assertEquals(4, entityList.size());

    }


    @Test
    public void canGetMetrics() throws InterruptedException
    {
        Assert.assertTrue(queryMetrics.getAllStoredEntities().isEmpty());
        Assert.assertEquals(0, QueryMetricsContainer.getMetrics("examples.metadata", "test", "2.3.0").size());

        entitiesResource.getEntities("examples.metadata", "test", "2.3.0", "examples::metadata::test", false, null, true);

        Assert.assertEquals(1, QueryMetricsContainer.getMetrics("examples.metadata", "test", "2.3.0").size());
        Date lastQueryTime = QueryMetricsContainer.getMetrics("examples.metadata", "test", "2.3.0").get(0).getLastQueryTime();
        Assert.assertNotNull(lastQueryTime);
        TimeUnit.SECONDS.sleep(30);

        entitiesResource.getEntities("example.services.test", "test", "1.0.1", false);

        QueryMetricsContainer.getMetrics("examples.metadata", "test", "2.3.0").get(0).getLastQueryTime();

        queryMetrics.persistMetrics();

        Assert.assertEquals(2, queryMetrics.getAllStoredEntities().size());
    }


    @Test
    public void canGetEntityByElementPath()
    {
        Entity entity = entitiesResource.getEntity("examples.metadata", "test", "2.3.0", "examples::metadata::test::TestProfile").get();
        Assert.assertNotNull(entity);
        Assert.assertEquals("meta::pure::metamodel::extension::Profile", entity.getClassifierPath());
    }

    @Test
    public void canGetEntityByPathWithVersion()
    {
        Entity entity = entitiesResource.getEntity("examples.metadata", "test", "2.3.0", "examples::metadata::test::TestProfile").get();
        Assert.assertNotNull(entity);
        Assert.assertEquals("meta::pure::metamodel::extension::Profile", entity.getClassifierPath());
    }

    @Test
    public void canGetEntitiesByPackage()
    {
        List<Entity> entityList = entitiesResource.getEntities("examples.metadata", "test", "2.3.0", "examples::metadata::test", false, null, true);
        Assert.assertNotNull(entityList);
        Assert.assertEquals(4, entityList.size());

    }
}
