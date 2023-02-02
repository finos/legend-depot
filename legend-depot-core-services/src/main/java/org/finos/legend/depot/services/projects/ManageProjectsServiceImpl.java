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
import org.finos.legend.depot.store.api.projects.UpdateProjects;
import org.finos.legend.depot.store.api.projects.UpdateProjectsVersions;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;

public class ManageProjectsServiceImpl extends ProjectsServiceImpl implements ManageProjectsService
{

    private final UpdateProjectsVersions projectsVersions;
    private final UpdateProjects projects;

    @Inject
    public ManageProjectsServiceImpl(UpdateProjectsVersions projectsVersions, UpdateProjects projects, @Named("dependencyCache") DependenciesCache dependenciesCache)
    {
        super(projectsVersions,projects,dependenciesCache);
        this.projects = projects;
        this.projectsVersions = projectsVersions;
    }

    public ManageProjectsServiceImpl(UpdateProjectsVersions projectsVersions, UpdateProjects projects)
    {
        super(projectsVersions, projects, new DependenciesCache(projectsVersions));
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
    public MetadataEventResponse delete(String groupId, String artifactId)
    {
        projects.delete(groupId, artifactId);
        return projectsVersions.delete(groupId, artifactId);
    }

    @Override
    public MetadataEventResponse delete(String groupId, String artifactId, String versionId)
    {
        return projectsVersions.deleteByVersionId(groupId, artifactId, versionId);
    }

}
