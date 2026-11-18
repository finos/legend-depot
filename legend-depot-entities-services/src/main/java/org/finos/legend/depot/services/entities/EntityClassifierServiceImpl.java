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

import org.finos.legend.depot.domain.entity.DepotEntity;
import org.finos.legend.depot.domain.entity.DepotEntityOverview;
import org.finos.legend.depot.domain.project.ProjectVersion;
import org.finos.legend.depot.domain.version.Scope;
import org.finos.legend.depot.services.api.entities.EntityClassifierService;
import org.finos.legend.depot.services.api.projects.ProjectsService;
import org.finos.legend.depot.store.api.entities.Entities;
import org.finos.legend.depot.store.model.projects.StoreProjectData;

import jakarta.inject.Inject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class EntityClassifierServiceImpl implements EntityClassifierService
{
    private final Entities entities;
    private final ProjectsService projects;
    private static final int PAGE_SIZE = 100;

    @Inject
    public EntityClassifierServiceImpl(ProjectsService projects, Entities versions)
    {
        this.projects = projects;
        this.entities = versions;
    }

    private List<ProjectVersion> getLatestProjectVersionByPage(int page, int pageSize, List<StoreProjectData> allProjects)
    {
        int beginIndex = page * pageSize - pageSize;
        int lastIndex = page * pageSize - 1;
        if (beginIndex >= allProjects.size() || beginIndex < 0)
        {
            return Collections.emptyList();
        }
        else if (lastIndex >= allProjects.size())
        {
            lastIndex = allProjects.size() - 1;
        }
        return allProjects.subList(beginIndex, lastIndex).stream()
                .filter(project -> project.getLatestVersion() != null)
                .map(project -> new ProjectVersion(project.getGroupId(), project.getArtifactId(), project.getLatestVersion()))
                .collect(Collectors.toList());
    }

    @Override
    public List<DepotEntity> getEntitiesByClassifierPath(String classifierPath, String search, Integer limit, Scope scope, boolean latestVersion)
    {
        if (Scope.SNAPSHOT.equals(scope))
        {
            return this.findClassifierEntities(classifierPath, scope, search, limit);
        }
        List<DepotEntity> result = new ArrayList<>();
        int currentPage = 1;
        List<StoreProjectData> allProjects = projects.getAllProjectCoordinates();
        List<ProjectVersion> projectVersions = this.getLatestProjectVersionByPage(currentPage, PAGE_SIZE, allProjects);
        while (!projectVersions.isEmpty())
        {
            List<DepotEntity> entities = this.findClassifierEntitiesByVersions(classifierPath, projectVersions, search, limit);
            result.addAll(entities);
            if (limit != null && result.size() >= limit)
            {
                break;
            }
            currentPage++;
            projectVersions = this.getLatestProjectVersionByPage(currentPage, PAGE_SIZE, allProjects);
        }
        if (limit != null)
        {
            result = result.stream().limit(limit).collect(Collectors.toList());
        }
        return result;
    }

    @Override
    public List<DepotEntity> findClassifierEntities(String classifier, Scope scope)
    {
        if (scope.equals(Scope.SNAPSHOT))
        {
            return entities.findLatestClassifierEntities(classifier);
        }
        return entities.findReleasedClassifierEntities(classifier);
    }

    @Override
    public List<DepotEntity> findClassifierEntitiesByVersions(String classifier, List<ProjectVersion> projectVersions)
    {
        return entities.findClassifierEntitiesByVersions(classifier, projectVersions);
    }

    @Override
    public List<DepotEntityOverview> findClassifierSummaries(String classifier, Scope scope)
    {
        if (scope.equals(Scope.SNAPSHOT))
        {
            return entities.findLatestClassifierSummaries(classifier);
        }
        return entities.findReleasedClassifierSummaries(classifier);
    }

    @Override
    public List<DepotEntityOverview> findClassifierSummariesByVersions(String classifier, List<ProjectVersion> projectVersions)
    {
        return entities.findClassifierSummariesByVersions(classifier, projectVersions);
    }

    @Override
    public List<DepotEntity> findClassifierEntities(String classifier, Scope scope, String search, Integer limit)
    {
        if (scope.equals(Scope.SNAPSHOT))
        {
            return entities.findLatestClassifierEntities(classifier, search, limit);
        }
        return entities.findReleasedClassifierEntities(classifier, search, limit);
    }

    @Override
    public List<DepotEntity> findClassifierEntitiesByVersions(String classifier, List<ProjectVersion> projectVersions, String search, Integer limit)
    {
        return entities.findClassifierEntitiesByVersions(classifier, projectVersions, search, limit);
    }
}
