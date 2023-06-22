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

package org.finos.legend.depot.store.artifacts.purge.services;

import org.finos.legend.depot.artifacts.repository.domain.ArtifactType;
import org.finos.legend.depot.artifacts.repository.services.RepositoryServices;
import org.finos.legend.depot.domain.VersionedData;
import org.finos.legend.depot.domain.api.MetadataEventResponse;
import org.finos.legend.depot.domain.project.ProjectVersion;
import org.finos.legend.depot.domain.project.StoreProjectData;
import org.finos.legend.depot.domain.project.StoreProjectVersionData;
import org.finos.legend.depot.services.api.projects.ManageProjectsService;
import org.finos.legend.depot.store.artifacts.api.ProjectArtifactsHandler;
import org.finos.legend.depot.store.artifacts.purge.api.ArtifactsPurgeService;
import org.finos.legend.depot.store.artifacts.services.ProjectArtifactHandlerFactory;
import org.finos.legend.depot.store.metrics.services.QueryMetricsHandler;
import org.finos.legend.depot.tracing.services.TracerFactory;
import org.finos.legend.depot.tracing.services.prometheus.PrometheusMetricsFactory;
import org.slf4j.Logger;

import javax.inject.Inject;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.finos.legend.depot.domain.DatesHandler.toDate;
import static org.finos.legend.depot.tracing.resources.ResourceLoggingAndTracing.DELETE_VERSION;
import static org.finos.legend.depot.tracing.resources.ResourceLoggingAndTracing.EVICT_VERSION;
import static org.finos.legend.depot.tracing.resources.ResourceLoggingAndTracing.DEPRECATE_VERSION;


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
    private final RepositoryServices repository;
    private final QueryMetricsHandler metrics;

    @Inject
    public ArtifactsPurgeServiceImpl(ManageProjectsService projects, RepositoryServices repository, QueryMetricsHandler metrics)
    {
        this.projects = projects;
        this.metrics = metrics;
        this.repository = repository;
    }


    private Set<ArtifactType> getSupportedArtifactTypes()
    {
        return ProjectArtifactHandlerFactory.getSupportedTypes();
    }

    private void decorateSpanWithVersionInfo(String groupId,String artifactId, String versionId)
    {
        Map<String, String> tags = new HashMap<>();  
        tags.put(GROUP_ID, groupId);
        tags.put(ARTIFACT_ID, artifactId);
        tags.put(VERSION_ID, versionId);
        TracerFactory.get().addTags(tags);
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

    //TODO: whenever we delete versions we need a way to recompute/deal with the dependencies cache for that project version

    @Override
    public void delete(String groupId, String artifactId, String versionId)
    {
        decorateSpanWithVersionInfo(groupId, artifactId, versionId);
        TracerFactory.get().executeWithTrace(DELETE_VERSION, () ->
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
        });
    }

    @Override
    public void evict(String groupId, String artifactId, String versionId)
    {
        decorateSpanWithVersionInfo(groupId, artifactId, versionId);
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
            return projects.createOrUpdate(projectData);
        });
    }

    @Override
    public MetadataEventResponse deprecate(String groupId, String artifactId, String versionId)
    {
        decorateSpanWithVersionInfo(groupId, artifactId, versionId);
        return TracerFactory.get().executeWithTrace(DEPRECATE_VERSION, () ->
        {
            MetadataEventResponse response = new MetadataEventResponse();
            StoreProjectVersionData projectData = getProjectVersion(groupId, artifactId, versionId);
            projectData.getVersionData().setDeprecated(true);
            response.addMessage(String.format("%s-%s-%s deprecated", groupId, artifactId, versionId));
            projects.createOrUpdate(projectData);
            return response;
        });
    }

    @Override
    public MetadataEventResponse deprecateVersionsNotInRepository()
    {
        MetadataEventResponse response = new MetadataEventResponse();
        repository.findVersionsMismatches().parallelStream().forEach(versionMismatch ->
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
        });
        return response;
    }

    @Override
    public MetadataEventResponse evictOldestProjectVersions(String groupId, String artifactId, int versionsToKeep)
    {
        projects.checkExists(groupId, artifactId);
        return TracerFactory.get().executeWithTrace(EVICT_OLDEST, () ->
        {
            MetadataEventResponse response = new MetadataEventResponse();
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
    public MetadataEventResponse evictLeastRecentlyUsed(int ttlForVersionsInDays, int ttlForSnapshotsInDays)
    {
        Set<ProjectVersion> evictProjectVersions = new HashSet<>();
        LocalDateTime currentDateTime = LocalDateTime.now();
        MetadataEventResponse response = new MetadataEventResponse();
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
    public MetadataEventResponse evictVersionsNotUsed()
    {
        Set<ProjectVersion> evictProjectVersions = new HashSet<>();
        MetadataEventResponse response = new MetadataEventResponse();
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
