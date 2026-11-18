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

import org.finos.legend.depot.domain.project.ProjectVersion;
import org.finos.legend.depot.server.resources.versions.ProjectsVersionsResource;
import org.finos.legend.depot.services.TestBaseServices;
import org.finos.legend.depot.services.projects.ProjectsServiceImpl;
import org.finos.legend.depot.services.api.projects.configuration.ProjectsConfiguration;
import org.finos.legend.depot.services.api.metrics.query.QueryMetricsRegistry;
import org.finos.legend.depot.services.api.notifications.queue.Queue;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import jakarta.ws.rs.core.Response;
import java.util.Map;
import java.util.Optional;

import static org.finos.legend.depot.domain.version.VersionValidator.BRANCH_SNAPSHOT;
import static org.mockito.Mockito.mock;

public class TestProjectsVersionsResource extends TestBaseServices
{
    private final QueryMetricsRegistry metrics = mock(QueryMetricsRegistry.class);
    private final Queue queue = mock(Queue.class);
    private ProjectsVersionsResource projectsVersionsResource = new ProjectsVersionsResource(new ProjectsServiceImpl(projectsVersionsStore, projectsStore, metrics, queue, new ProjectsConfiguration("master")));



    @Test
    public void canQueryLatestProjectVersionData()
    {
        Response responseOne = projectsVersionsResource.getProjectVersion("examples.metadata", "test","latest");
        Optional<ProjectsVersionsResource.ProjectVersionDTO> versionData = (Optional<ProjectsVersionsResource.ProjectVersionDTO>) responseOne.getEntity();
        Assertions.assertTrue(versionData.isPresent());
        Assertions.assertEquals(versionData.get().getGroupId(), "examples.metadata");
        Assertions.assertEquals(versionData.get().getArtifactId(), "test");
        Assertions.assertEquals(versionData.get().getVersionId(), "2.3.1");
        Assertions.assertEquals(versionData.get().getVersionData().getDependencies().get(0), new ProjectVersion("examples.metadata", "test-dependencies", "1.0.0"));

        Map<String, String> manifestProperties = versionData.get().getVersionData().getManifestProperties();
        Assertions.assertNotNull(manifestProperties);
        Assertions.assertEquals(manifestProperties.get("commit-author"), "test-author");
        Assertions.assertEquals(manifestProperties.get("commit-timestamp"), "2023-04-11T14:48:27+00:00");

        Response responseTwo = projectsVersionsResource.getProjectVersion("somethig.random", "test","latest");
        Optional<ProjectsVersionsResource.ProjectVersionDTO> versionData1 = (Optional<ProjectsVersionsResource.ProjectVersionDTO>) responseTwo.getEntity();
        Assertions.assertFalse(versionData1.isPresent());
    }

    @Test
    public void canQueryHeadProjectVersionData()
    {
        Response responseOne = projectsVersionsResource.getProjectVersion("examples.metadata", "test","head");
        Optional<ProjectsVersionsResource.ProjectVersionDTO> versionData = (Optional<ProjectsVersionsResource.ProjectVersionDTO>) responseOne.getEntity();
        Assertions.assertTrue(versionData.isPresent());
        Assertions.assertEquals(versionData.get().getGroupId(), "examples.metadata");
        Assertions.assertEquals(versionData.get().getArtifactId(), "test");
        Assertions.assertEquals(versionData.get().getVersionId(), BRANCH_SNAPSHOT("master"));

        Response responseTwo = projectsVersionsResource.getProjectVersion("somethig.random", "test","head");
        Optional<ProjectsVersionsResource.ProjectVersionDTO> versionData1 = (Optional<ProjectsVersionsResource.ProjectVersionDTO>) responseTwo.getEntity();
        Assertions.assertFalse(versionData1.isPresent());
    }
}
