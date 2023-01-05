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
import org.finos.legend.depot.domain.project.ProjectData;
import org.finos.legend.depot.services.api.projects.ManageProjectsService;
import org.finos.legend.depot.store.api.projects.UpdateProjects;

import javax.inject.Inject;
import javax.inject.Named;

public class ManageProjectsServiceImpl extends ProjectsServiceImpl implements ManageProjectsService
{

    private final UpdateProjects projects;

    @Inject
    public ManageProjectsServiceImpl(UpdateProjects projects, @Named("dependencyCache") DependenciesCache dependenciesCache)
    {
        super(projects,dependenciesCache);
        this.projects = projects;
    }

    public ManageProjectsServiceImpl(UpdateProjects projects)
    {
        super(projects,new DependenciesCache(projects));
        this.projects = projects;
    }

    @Override
    public ProjectData createOrUpdate(ProjectData projectData)
    {
        return projects.createOrUpdate(projectData);
    }

    @Override
    public MetadataEventResponse delete(String groupId, String artifactId)
    {
        return projects.delete(groupId, artifactId);
    }

    @Override
    public MetadataEventResponse delete(String projectId)
    {
        return projects.deleteByProjectId(projectId);
    }

}
