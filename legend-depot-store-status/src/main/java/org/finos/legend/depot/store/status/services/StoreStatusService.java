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

import org.finos.legend.depot.domain.entity.VersionRevision;
import org.finos.legend.depot.domain.version.VersionValidator;
import org.finos.legend.depot.store.api.entities.Entities;
import org.finos.legend.depot.store.api.projects.Projects;
import org.finos.legend.depot.store.artifacts.api.ArtifactsRefreshService;
import org.finos.legend.depot.store.artifacts.api.status.RefreshStatusService;
import org.finos.legend.depot.store.artifacts.domain.status.RefreshStatus;
import org.finos.legend.depot.store.metrics.api.ManageQueryMetrics;
import org.finos.legend.depot.store.metrics.domain.VersionQuerySummary;
import org.finos.legend.depot.store.status.domain.StoreStatus;
import org.slf4j.Logger;

import javax.inject.Inject;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class StoreStatusService
{
    private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(StoreStatusService.class);
    private final Projects projectApi;
    private final Entities entities;
    private final ArtifactsRefreshService artifactsRefreshService;
    private final RefreshStatusService statusService;
    private final ManageQueryMetrics queryMetrics;

    @Inject
    public StoreStatusService(Projects projectApi, Entities versions, ArtifactsRefreshService artifactsRefreshService, RefreshStatusService statusService, ManageQueryMetrics queryMetrics)
    {
        this.projectApi = projectApi;
        this.entities = versions;
        this.artifactsRefreshService = artifactsRefreshService;
        this.statusService = statusService;
        this.queryMetrics = queryMetrics;
    }

    public StoreStatus getStatus()
    {
        StoreStatus status = new StoreStatus();
        projectApi.getAll().forEach(p ->
        {
            StoreStatus.ProjectSummary summry = new StoreStatus.ProjectSummary(p.getProjectId(), p.getGroupId(), p.getArtifactId(), p.getVersions().size(), "/projects/");
            status.addProject(summry);
        });

        Collections.sort(status.projects);
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
        List<String> versions = projectApi.getVersions(groupId, artifactId);

        versions.forEach(v ->
        {
            StoreStatus.VersionStatus versionStatus = new StoreStatus.VersionStatus(groupId, artifactId, v);
            RefreshStatus updateStatus = statusService.get(VersionRevision.VERSIONS, groupId, artifactId, v);
            Optional<VersionQuerySummary> versionQueryCounter = queryMetrics.getSummary(groupId, artifactId, v);
            if (versionQueryCounter.isPresent())
            {
                versionStatus.queryCount = versionQueryCounter.get().getQueryCount();
                versionStatus.lastQueried = versionQueryCounter.get().getLastQueryTime();
            }
            versionStatus.lastUpdated = updateStatus.getLastRun();
            versionStatus.updating = updateStatus.isRunning();
            versionStatus.url = String.format("/projects/%s/%s/versions/%s", groupId, artifactId, v);
            projectStatus.addVersionStatus(versionStatus);
        });

        StoreStatus.MasterRevisionStatus masterRevisionStatus = new StoreStatus.MasterRevisionStatus(groupId, artifactId);
        RefreshStatus revisionsUpdateStatus = statusService.get(VersionRevision.REVISIONS, groupId, artifactId, VersionValidator.MASTER_SNAPSHOT);
        masterRevisionStatus.lastUpdated = revisionsUpdateStatus.getLastRun();
        masterRevisionStatus.updating = revisionsUpdateStatus.isRunning();
        masterRevisionStatus.url = String.format("/projects/%s/%s/revisions/latest", groupId, artifactId);
        projectStatus.setMasterRevisionStatus(masterRevisionStatus);

        return projectStatus;
    }


    public List<VersionQuerySummary> summaryByProjectVersion()
    {
        return queryMetrics.getSummaryByProjectVersion();
    }


}
