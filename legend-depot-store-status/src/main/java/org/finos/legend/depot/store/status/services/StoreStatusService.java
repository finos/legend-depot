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

package org.finos.legend.depot.store.status.services;

import org.finos.legend.depot.domain.version.VersionValidator;
import org.finos.legend.depot.store.api.entities.Entities;
import org.finos.legend.depot.store.admin.api.artifacts.RefreshStatusStore;
import org.finos.legend.depot.store.admin.domain.artifacts.RefreshStatus;
import org.finos.legend.depot.store.api.projects.Projects;
import org.finos.legend.depot.store.api.projects.ProjectsVersions;
import org.finos.legend.depot.store.metrics.domain.VersionQuerySummary;
import org.finos.legend.depot.store.metrics.services.QueryMetricsHandler;
import org.finos.legend.depot.store.status.domain.StoreStatus;
import org.finos.legend.sdlc.domain.model.version.VersionId;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class StoreStatusService
{
    private final ProjectsVersions projectVersionsApi;
    private final Projects projectsApi;
    private final Entities entities;
    private final RefreshStatusStore statusService;
    private final QueryMetricsHandler queryMetricsHandler;

    @Inject
    public StoreStatusService(ProjectsVersions projectVersionsApi, Projects projectsApi, Entities versions, RefreshStatusStore statusService, QueryMetricsHandler queryMetrics)
    {
        this.projectVersionsApi = projectVersionsApi;
        this.projectsApi = projectsApi;
        this.entities = versions;
        this.statusService = statusService;
        this.queryMetricsHandler = queryMetrics;
    }

    public List<StoreStatus.ProjectSummary> getStatus()
    {
        List<StoreStatus.ProjectSummary> status = new ArrayList();
        projectsApi.getAll().forEach(p ->
        {
            StoreStatus.ProjectSummary summry = new StoreStatus.ProjectSummary(p.getProjectId(), p.getGroupId(), p.getArtifactId(), projectVersionsApi.getVersionCount(p.getGroupId(), p.getArtifactId()));
            status.add(summry);
        });
        return status;
    }

    public StoreStatus.DocumentCounts getDocumentCounts()
    {
        return new StoreStatus.DocumentCounts(entities.getVersionEntityCount(), entities.getRevisionEntityCount());
    }

    public StoreStatus.DocumentCounts getDocumentCounts(String groupId, String artifactId, String versionId)
    {
        return new StoreStatus.DocumentCounts(entities.getVersionEntityCount(groupId, artifactId, versionId), 0);
    }

    public StoreStatus.DocumentCounts getRevisionDocumentCounts(String groupId, String artifactId)
    {
        return new StoreStatus.DocumentCounts(0, entities.getRevisionEntityCount(groupId, artifactId));
    }

    public StoreStatus.ProjectStatus getProjectStatus(String groupId, String artifactId)
    {
        StoreStatus.ProjectStatus projectStatus = new StoreStatus.ProjectStatus();
        List<String> versions = projectVersionsApi.find(groupId, artifactId).stream().filter(v -> !v.getVersionId().equals("master-SNAPSHOT") && !v.getVersionData().isExcluded()).map(v -> v.getVersionId()).collect(Collectors.toList());

        versions.forEach(v ->
        {
            StoreStatus.VersionStatus versionStatus = new StoreStatus.VersionStatus(groupId, artifactId, v);
            RefreshStatus updateStatus = statusService.get(groupId, artifactId, v).orElse(new RefreshStatus(groupId, artifactId, v));
            Optional<VersionQuerySummary> versionQueryCounter = queryMetricsHandler.getSummary(groupId, artifactId, v);
            if (versionQueryCounter.isPresent())
            {
                versionStatus.queryCount = versionQueryCounter.get().getQueryCount();
                versionStatus.lastQueried = versionQueryCounter.get().getLastQueryTime();
            }
            versionStatus.lastUpdated = updateStatus.getLastRun();
            versionStatus.updating = updateStatus.isRunning();
            projectStatus.addVersionStatus(versionStatus);
        });

        StoreStatus.MasterRevisionStatus masterRevisionStatus = new StoreStatus.MasterRevisionStatus(groupId, artifactId);
        RefreshStatus revisionsUpdateStatus = statusService.get(groupId, artifactId, VersionValidator.MASTER_SNAPSHOT).orElse(new RefreshStatus(groupId, artifactId, VersionValidator.MASTER_SNAPSHOT));;
        masterRevisionStatus.lastUpdated = revisionsUpdateStatus.getLastRun();
        masterRevisionStatus.updating = revisionsUpdateStatus.isRunning();
        projectStatus.setMasterRevisionStatus(masterRevisionStatus);

        return projectStatus;
    }


    public List<VersionQuerySummary> summaryByProjectVersion()
    {
        return queryMetricsHandler.getSummaryByProjectVersion();
    }


}
