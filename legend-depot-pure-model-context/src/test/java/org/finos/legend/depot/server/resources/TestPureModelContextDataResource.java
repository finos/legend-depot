//  Copyright 2022 Goldman Sachs
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

package org.finos.legend.depot.server.resources;

import org.finos.legend.depot.server.resources.pure.model.context.PureModelContextResource;
import org.finos.legend.depot.services.TestBaseServices;
import org.finos.legend.depot.services.api.entities.EntitiesService;
import org.finos.legend.depot.services.api.projects.ManageProjectsService;
import org.finos.legend.depot.services.entities.EntitiesServiceImpl;
import org.finos.legend.depot.services.projects.ManageProjectsServiceImpl;
import org.finos.legend.depot.services.api.projects.configuration.ProjectsConfiguration;
import org.finos.legend.depot.services.pure.model.context.PureModelContextServiceImpl;
import org.finos.legend.depot.services.pure.model.context.TestPureModelContextService;
import org.finos.legend.depot.store.api.entities.Entities;
import org.finos.legend.depot.services.api.metrics.query.QueryMetricsRegistry;
import org.finos.legend.depot.services.metrics.query.InMemoryQueryMetricsRegistry;
import org.finos.legend.depot.store.mongo.entities.EntitiesMongo;
import org.finos.legend.depot.store.mongo.entities.test.EntitiesMongoTestUtils;
import org.finos.legend.depot.services.api.notifications.queue.Queue;
import org.finos.legend.engine.protocol.pure.v1.model.context.PureModelContextData;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.core.Response;

import static org.mockito.Mockito.mock;


public class TestPureModelContextDataResource extends TestBaseServices
{
    private final QueryMetricsRegistry metrics = new InMemoryQueryMetricsRegistry();
    private final Queue queue = mock(Queue.class);
    private final ManageProjectsService projectsService = new ManageProjectsServiceImpl(projectsVersionsStore,projectsStore,metrics,queue,new ProjectsConfiguration("master"));
    private final Entities entitiesStore = new EntitiesMongo(mongoProvider);
    private final EntitiesService entitiesService = new EntitiesServiceImpl(entitiesStore, projectsService);

    private final PureModelContextResource resource = new PureModelContextResource(new PureModelContextServiceImpl(entitiesService,projectsService));

    @Before
    public void setUpData()
    {
        super.setUpData();
        setUpProjectsVersionsFromFile(TestPureModelContextService.projects);
        new EntitiesMongoTestUtils(mongoProvider).loadEntities(TestPureModelContextService.entities_16538);
    }

    @Test
    public void loadPMCD()
    {
        Response data = resource.getPureModelContextData("test.legend", "blank-prod", "2.0.0", null, false,null);
        Assert.assertFalse(((PureModelContextData)data.getEntity()).getElements().isEmpty());
        Assert.assertEquals(((PureModelContextData)data.getEntity()).getElements().size(), 2);

    }
}
