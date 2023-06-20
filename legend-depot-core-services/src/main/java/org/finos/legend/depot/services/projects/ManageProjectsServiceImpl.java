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

package org.finos.legend.depot.services.projects;

import org.finos.legend.depot.domain.api.MetadataEventResponse;
import org.finos.legend.depot.domain.project.StoreProjectData;
import org.finos.legend.depot.domain.project.StoreProjectVersionData;
import org.finos.legend.depot.services.api.projects.ManageProjectsService;
import org.finos.legend.depot.services.projects.configuration.ProjectsConfiguration;
import org.finos.legend.depot.store.admin.api.metrics.QueryMetricsStore;
import org.finos.legend.depot.store.api.projects.UpdateProjects;
import org.finos.legend.depot.store.api.projects.UpdateProjectsVersions;
import org.finos.legend.depot.store.notifications.queue.api.Queue;

import javax.inject.Inject;
import java.util.List;

public class ManageProjectsServiceImpl extends ProjectsServiceImpl implements ManageProjectsService
{

    private final UpdateProjectsVersions projectsVersions;
    private final UpdateProjects projects;

    @Inject
    public ManageProjectsServiceImpl(UpdateProjectsVersions projectsVersions, UpdateProjects projects, QueryMetricsStore metrics, Queue queue, ProjectsConfiguration configuration)
    {
        super(projectsVersions,projects, metrics, queue, configuration);
        this.projects = projects;
        this.projectsVersions = projectsVersions;
    }

    @Override
    public List<StoreProjectVersionData> getAll()
    {
        return projectsVersions.getAll();
    }

    @Override
    public StoreProjectVersionData createOrUpdate(StoreProjectVersionData projectData)
    {
        return projectsVersions.createOrUpdate(projectData);
    }

    @Override
    public StoreProjectData createOrUpdate(StoreProjectData projectData)
    {
        return projects.createOrUpdate(projectData);
    }

    @Override
    public long delete(String groupId, String artifactId)
    {
        projects.delete(groupId, artifactId);
        return projectsVersions.delete(groupId, artifactId);
    }

    @Override
    public long delete(String groupId, String artifactId, String versionId)
    {
        return projectsVersions.delete(groupId, artifactId, versionId);
    }

    @Override
    public StoreProjectVersionData excludeProjectVersion(String groupId, String artifactId, String versionId, String exclusionReason)
    {
        StoreProjectVersionData storeProjectVersionData = new StoreProjectVersionData(groupId, artifactId, versionId);
        storeProjectVersionData.getVersionData().setExcluded(true);
        storeProjectVersionData.getVersionData().setExclusionReason(exclusionReason);
        return this.createOrUpdate(storeProjectVersionData);
    }
}
