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

package org.finos.legend.depot.server.resources;

import org.finos.legend.depot.server.resources.projects.ProjectsResource;
import org.finos.legend.depot.services.TestBaseServices;
import org.finos.legend.depot.services.projects.ProjectsServiceImpl;
import org.finos.legend.depot.store.metrics.api.QueryMetricsRegistry;
import org.finos.legend.depot.services.projects.configuration.ProjectsConfiguration;
import org.finos.legend.depot.store.notifications.queue.api.Queue;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

import static org.mockito.Mockito.mock;

public class TestProjectsResource extends TestBaseServices
{
    private final QueryMetricsRegistry metrics = mock(QueryMetricsRegistry.class);
    private final Queue queue = mock(Queue.class);
    private ProjectsResource projectsVersionsResource = new ProjectsResource(new ProjectsServiceImpl(projectsVersionsStore, projectsStore, metrics, queue, new ProjectsConfiguration("master")));

    @Test
    public void canQueryVersionsForProjectGA()
    {
        List<String> versionSet = projectsVersionsResource.getVersions("examples.metadata", "test", false);
        Assert.assertNotNull(versionSet);
        Assert.assertEquals(2, versionSet.size());
    }

    @Test
    public void canQueryVersionsForProject()
    {
        List<String> versionSet = projectsVersionsResource.getVersions("examples.metadata", "test",false);
        Assert.assertNotNull(versionSet);
        Assert.assertEquals(2, versionSet.size());
    }
}