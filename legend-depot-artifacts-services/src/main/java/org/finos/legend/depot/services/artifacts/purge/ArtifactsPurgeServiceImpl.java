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

package org.finos.legend.depot.services.artifacts.purge;

import org.eclipse.collections.impl.parallel.ParallelIterate;
import org.finos.legend.depot.domain.VersionedData;
import org.finos.legend.depot.domain.notifications.MetadataNotificationResponse;
import org.finos.legend.depot.domain.artifacts.repository.ArtifactType;
import org.finos.legend.depot.domain.project.ProjectVersion;
import org.finos.legend.depot.services.api.artifacts.handlers.ProjectArtifactHandlerFactory;
import org.finos.legend.depot.services.api.artifacts.handlers.ProjectArtifactsHandler;
import org.finos.legend.depot.services.api.artifacts.purge.ArtifactsPurgeService;
import org.finos.legend.depot.services.api.metrics.query.QueryMetricsService;
import org.finos.legend.depot.services.api.projects.ManageProjectsService;
import org.finos.legend.depot.services.api.artifacts.reconciliation.VersionsReconciliationService;
import org.finos.legend.depot.store.model.projects.StoreProjectData;
import org.finos.legend.depot.store.model.projects.StoreProjectVersionData;
import org.finos.legend.depot.core.services.tracing.TracerFactory;
import org.finos.legend.depot.core.services.metrics.PrometheusMetricsFactory;
import org.slf4j.Logger;

import javax.inject.Inject;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.finos.legend.depot.domain.DatesHandler.toDate;
import static org.finos.legend.depot.core.services.tracing.ResourceLoggingAndTracing.DELETE_VERSION;
import static org.finos.legend.depot.core.services.tracing.ResourceLoggingAndTracing.DEPRECATE_VERSION;
import static org.finos.legend.depot.core.services.tracing.ResourceLoggingAndTracing.EVICT_VERSION;


public class ArtifactsPurgeServiceImpl implements ArtifactsPurgeService
{
    private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(ArtifactsPurgeServiceImpl.class);
    private static final String SEPARATOR = "-";
    private static final String GROUP_ID = "groupId";
    private static final String ARTIFACT_ID = "artifactId";
    private static final String VERSION_ID = "versionId";

    public static final String VERSION_PURGE_COUNTER = "versionPurge";
    public static final String VERSION_DELETE_COUNTER = "versionDeletion";
    private static final String EVICT_OLDEST = "evict_old";

    private final ManageProjectsService projects;
    private final VersionsReconciliationService versionsMismatchService;
    private final QueryMetricsService metrics;

    @Inject
    public ArtifactsPurgeServiceImpl(ManageProjectsService projects, VersionsReconciliationService versionsMismatchService, QueryMetricsService metrics)
    {
        this.projects = projects;
        this.metrics = metrics;
        this.versionsMismatchService = versionsMismatchService;
    }

    protected QueryMetricsService getQueryMetricsService()
    {
        return metrics;
    }

    private Set<ArtifactType> getSupportedArtifactTypes()
    {
        return ProjectArtifactHandlerFactory.getSupportedTypes();
    }

    private Map<String, String> decorateSpanWithVersionInfo(String groupId,String artifactId, String versionId)
    {
        Map<String, String> tags = new HashMap<>();  
        tags.put(GROUP_ID, groupId);
        tags.put(ARTIFACT_ID, artifactId);
        tags.put(VERSION_ID, versionId);
        return tags;
    }

    private StoreProjectVersionData getProjectVersion(String groupId, String artifactId, String versionId)
    {
        Optional<StoreProjectVersionData> found = projects.find(groupId, artifactId, versionId);
        if (!found.isPresent())
        {
            throw new IllegalArgumentException("can't find project for " + groupId + SEPARATOR + artifactId);
        }
        return found.get();
    }

    @Override
    public void delete(String groupId, String artifactId, String versionId)
    {
        TracerFactory.get().executeWithTrace(DELETE_VERSION,  () ->
        {
            getSupportedArtifactTypes().forEach(artifactType ->
            {
                ProjectArtifactsHandler artifactHandler = ProjectArtifactHandlerFactory.getArtifactHandler(artifactType);
                if (artifactHandler != null)
                {
                    artifactHandler.delete(groupId, artifactId, versionId);
                }
            });
            PrometheusMetricsFactory.getInstance().incrementCount(VERSION_DELETE_COUNTER);
            LOGGER.info(String.format("%s-%s-%s artifacts deleted", groupId, artifactId, versionId));
            return projects.delete(groupId, artifactId, versionId);
        },decorateSpanWithVersionInfo(groupId, artifactId, versionId));
    }

    @Override
    public void evict(String groupId, String artifactId, String versionId)
    {
        TracerFactory.get().executeWithTrace(EVICT_VERSION, () ->
        {
            getSupportedArtifactTypes().forEach(artifactType ->
            {
                ProjectArtifactsHandler artifactHandler = ProjectArtifactHandlerFactory.getArtifactHandler(artifactType);
                if (artifactHandler != null)
                {
                    artifactHandler.delete(groupId, artifactId, versionId);
                }
            });
            StoreProjectVersionData projectData = getProjectVersion(groupId, artifactId, versionId);
            LOGGER.info(String.format("%s-%s-%s artifacts deleted", groupId, artifactId, versionId));
            projectData.setEvicted(true);
            LOGGER.info(String.format("%s-%s-%s evicted", groupId, artifactId, versionId));
            PrometheusMetricsFactory.getInstance().incrementCount(VERSION_PURGE_COUNTER);
            metrics.delete(groupId, artifactId, versionId);
            return projects.createOrUpdate(projectData);
        },decorateSpanWithVersionInfo(groupId, artifactId, versionId));
    }

    @Override
    public MetadataNotificationResponse deprecate(String groupId, String artifactId, String versionId)
    {
        return TracerFactory.get().executeWithTrace(DEPRECATE_VERSION, () ->
        {
            MetadataNotificationResponse response = new MetadataNotificationResponse();
            StoreProjectVersionData projectData = getProjectVersion(groupId, artifactId, versionId);
            projectData.getVersionData().setDeprecated(true);
            response.addMessage(String.format("%s-%s-%s deprecated", groupId, artifactId, versionId));
            projects.createOrUpdate(projectData);
            return response;
        },decorateSpanWithVersionInfo(groupId, artifactId, versionId));
    }

    @Override
    public MetadataNotificationResponse deprecateVersionsNotInRepository()
    {
        MetadataNotificationResponse response = new MetadataNotificationResponse();
        ParallelIterate.forEach(versionsMismatchService.findVersionsMismatches(),versionMismatch ->
        {
            if (!versionMismatch.versionsNotInRepository.isEmpty())
            {
                versionMismatch.versionsNotInRepository.forEach(versionId ->
                {
                    LOGGER.info(String.format("Deprecating project version: %s-%s-%s", versionMismatch.groupId, versionMismatch.artifactId, versionId));
                    deprecate(versionMismatch.groupId, versionMismatch.artifactId, versionId);
                    response.addMessage(String.format("Deprecated project version: %s-%s-%s", versionMismatch.groupId, versionMismatch.artifactId, versionId));
                });
            }
        },10);
        return response;
    }

    @Override
    public MetadataNotificationResponse evictOldestProjectVersions(String groupId, String artifactId, int versionsToKeep)
    {
        projects.checkExists(groupId, artifactId);
        return TracerFactory.get().executeWithTrace(EVICT_OLDEST, () ->
        {
            MetadataNotificationResponse response = new MetadataNotificationResponse();
            List<String> versionIds = projects.getVersions(groupId, artifactId);
            int numberOfVersions = versionIds.size();
            try
            {
                while (versionIds.size() > versionsToKeep)
                {
                    String versionId = versionIds.get(0);
                    evict(groupId, artifactId, versionId);
                    versionIds.remove(versionId);
                    response.addMessage(String.format("%s-%s-%s evicted", groupId, artifactId, versionId));
                }
                response.addMessage(String.format("%s-%s evicted %s versions", groupId, artifactId, numberOfVersions - versionIds.size()));
            }
            catch (Exception e)
            {
                String errorMessage = String.format(" Error evicting old versions %s-%s %s",groupId,artifactId,e.getMessage());
                LOGGER.error(errorMessage);
                response.addError(errorMessage);
                PrometheusMetricsFactory.getInstance().incrementErrorCount(VERSION_PURGE_COUNTER);
            }
            return response;
        });
    }

    @Override
    public MetadataNotificationResponse evictLeastRecentlyUsed(int ttlForVersionsInDays, int ttlForSnapshotsInDays)
    {
        Set<ProjectVersion> evictProjectVersions = new HashSet<>();
        LocalDateTime currentDateTime = LocalDateTime.now();
        MetadataNotificationResponse response = new MetadataNotificationResponse();
        try
        {
            LOGGER.info("Started finding eviction candidates for snapshot versions");
            metrics.findSnapshotVersionMetricsBefore(toDate(currentDateTime.minusDays(ttlForSnapshotsInDays)))
                    .parallelStream().forEach(pv -> evictProjectVersions.add(new ProjectVersion(pv.getGroupId(), pv.getArtifactId(), pv.getVersionId())));
            LOGGER.info("Started finding eviction candidates for non-snapshot versions");
            metrics.findReleasedVersionMetricsBefore(toDate(currentDateTime.minusDays(ttlForVersionsInDays)))
                    .parallelStream().forEach(pv -> evictProjectVersions.add(new ProjectVersion(pv.getGroupId(), pv.getArtifactId(), pv.getVersionId())));
            LOGGER.info("Completed finding eviction candidates");
        }
        catch (Exception e)
        {
            LOGGER.error(String.format("Error while applying retention policy: %s", e.getMessage()));
        }
        evictProjectVersions.parallelStream().forEach(pv ->
        {
            evict(pv.getGroupId(), pv.getArtifactId(), pv.getVersionId());
            response.addMessage(String.format("Evicted project version: %s", pv.getGav()));
        });
        return response;
    }

    @Override
    public MetadataNotificationResponse evictVersionsNotUsed()
    {
        Set<ProjectVersion> evictProjectVersions = new HashSet<>();
        MetadataNotificationResponse response = new MetadataNotificationResponse();
        try
        {
            LOGGER.info("Started finding versions not being used for eviction");
            List<StoreProjectData> allProjects = projects.getAllProjectCoordinates();
            allProjects.parallelStream().forEach(project ->
            {
                Set<String> allVersions = projects.find(project.getGroupId(), project.getArtifactId()).stream()
                        .filter(versionData -> !versionData.isEvicted() && !versionData.getVersionData().isExcluded())
                        .map(VersionedData::getVersionId).collect(Collectors.toSet());
                Set<String> versionsUsed = metrics.findMetricsForProjectCoordinates(project.getGroupId(), project.getArtifactId()).stream().map(metric -> metric.getVersionId()).collect(Collectors.toSet());
                allVersions.removeAll(versionsUsed);
                allVersions.parallelStream().forEach(version -> evictProjectVersions.add(new ProjectVersion(project.getGroupId(), project.getArtifactId(), version)));
            });
            LOGGER.info("Completed finding versions not being used");
        }
        catch (Exception e)
        {
            LOGGER.error(String.format("Error while evicting versions not being used: %s", e.getMessage()));
        }
        evictProjectVersions.parallelStream().forEach(pv ->
        {
            evict(pv.getGroupId(), pv.getArtifactId(), pv.getVersionId());
            response.addMessage(String.format("Evicted project version: %s", pv.getGav()));
        });
        return response;
    }

}
