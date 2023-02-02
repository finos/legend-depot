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

package org.finos.legend.depot.services.entities;

import org.eclipse.collections.impl.utility.ListIterate;
import org.finos.legend.depot.domain.entity.StoredEntity;
import org.finos.legend.depot.domain.project.ProjectVersion;
import org.finos.legend.depot.domain.version.Scope;
import org.finos.legend.depot.services.api.entities.EntitiesService;
import org.finos.legend.depot.services.api.entities.EntityClassifierService;
import org.finos.legend.depot.services.api.projects.ProjectsService;
import org.finos.legend.sdlc.domain.model.version.VersionId;
import org.slf4j.Logger;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class EntityClassifierServiceImpl implements EntityClassifierService
{
    private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(EntityClassifierServiceImpl.class);
    private final EntitiesService entities;
    private final ProjectsService projects;

    @Inject
    public EntityClassifierServiceImpl(ProjectsService projects, EntitiesService versions)
    {
        this.projects = projects;
        this.entities = versions;
    }

    private List<ProjectVersion> getProjectsInfo(int page, int pageSize)
    {
        return ListIterate.collect(projects.getProjects(page, pageSize), projectData ->
        {
            Optional<VersionId> latestVersion = projects.getLatestVersion(projectData.getGroupId(), projectData.getArtifactId());
            return new ProjectVersion(projectData.getGroupId(), projectData.getArtifactId(), latestVersion.isPresent() ? latestVersion.get().toVersionIdString() : null);
        }).select(info -> info.getVersionId() != null);
    }

    @Override
    public List<StoredEntity> getEntitiesByClassifierPath(String classifierPath, String search, Integer limit, Scope scope, boolean summary, boolean versioned)
    {
        if (Scope.SNAPSHOT.equals(scope))
        {
            return this.entities.findLatestEntitiesByClassifier(classifierPath, search, limit, summary, versioned);
        }
        List<StoredEntity> result = new ArrayList<>();
        int PAGE_SIZE = 100;
        int currentPage = 1;
        List<ProjectVersion> projectVersions = this.getProjectsInfo(currentPage, PAGE_SIZE);
        while (!projectVersions.isEmpty())
        {
            List<StoredEntity> entities = this.entities.findReleasedEntitiesByClassifier(classifierPath, search, projectVersions, limit, summary, versioned);
            result.addAll(entities);
            if (limit != null && result.size() >= limit)
            {
                break;
            }
            currentPage++;
            projectVersions = this.getProjectsInfo(currentPage, PAGE_SIZE);
        }
        if (limit != null)
        {
            result = result.stream().limit(limit).collect(Collectors.toList());
        }
        return result;
    }
}
