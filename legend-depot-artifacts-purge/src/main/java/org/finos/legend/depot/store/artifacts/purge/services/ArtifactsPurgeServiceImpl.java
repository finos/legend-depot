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
import org.finos.legend.depot.artifacts.repository.domain.VersionMismatch;
import org.finos.legend.depot.artifacts.repository.services.RepositoryServices;
import org.finos.legend.depot.domain.api.MetadataEventResponse;
import org.finos.legend.depot.domain.project.StoreProjectVersionData;
import org.finos.legend.depot.services.api.projects.ManageProjectsService;
import org.finos.legend.depot.store.artifacts.api.ProjectArtifactsHandler;
import org.finos.legend.depot.store.artifacts.purge.api.ArtifactsPurgeService;
import org.finos.legend.depot.store.artifacts.services.ArtifactHandlerFactory;
import org.finos.legend.depot.tracing.services.TracerFactory;
import org.finos.legend.depot.tracing.services.prometheus.PrometheusMetricsFactory;
import org.slf4j.Logger;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.finos.legend.depot.tracing.resources.ResourceLoggingAndTracing.EVICT_VERSION;


public class ArtifactsPurgeServiceImpl implements ArtifactsPurgeService
{
    private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(ArtifactsPurgeServiceImpl.class);
    private static final String SEPARATOR = "-";
    private static final String GROUP_ID = "groupId";
    private static final String ARTIFACT_ID = "artifactId";
    private static final String VERSION_ID = "versionId";

    public static final String VERSION_PURGE_COUNTER = "versionPurge";
    private static final String EVICT_OLDEST = "evict_old";

    private final ManageProjectsService projects;
    private final RepositoryServices repository;


    @Inject
    public ArtifactsPurgeServiceImpl(ManageProjectsService projects, RepositoryServices repository)
    {
        this.projects = projects;
        this.repository = repository;
    }


    private Set<ArtifactType> getSupportedArtifactTypes()
    {
        return ArtifactHandlerFactory.getSupportedTypes();
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

    @Override
    public void delete(String groupId, String artifactId, String versionId)
    {
        decorateSpanWithVersionInfo(groupId, artifactId, versionId);
        TracerFactory.get().executeWithTrace(EVICT_VERSION, () ->
        {
            getSupportedArtifactTypes().forEach(artifactType ->
            {
                ProjectArtifactsHandler artifactHandler = ArtifactHandlerFactory.getArtifactHandler(artifactType);
                if (artifactHandler != null)
                {
                    artifactHandler.delete(groupId, artifactId, versionId);
                }
            });
            PrometheusMetricsFactory.getInstance().incrementCount(VERSION_PURGE_COUNTER);
            return projects.delete(groupId, artifactId, versionId);
        });
    }

    private void evict(String groupId, String artifactId, String versionId)
    {
        decorateSpanWithVersionInfo(groupId, artifactId, versionId);
        TracerFactory.get().executeWithTrace(EVICT_VERSION, () ->
        {
            getSupportedArtifactTypes().forEach(artifactType ->
            {
                ProjectArtifactsHandler artifactHandler = ArtifactHandlerFactory.getArtifactHandler(artifactType);
                if (artifactHandler != null)
                {
                    artifactHandler.delete(groupId, artifactId, versionId);
                }
            });
            StoreProjectVersionData projectData = getProjectVersion(groupId, artifactId, versionId);
            projectData.setEvicted(true);
            PrometheusMetricsFactory.getInstance().incrementCount(VERSION_PURGE_COUNTER);
            return projects.createOrUpdate(projectData);
        });
    }

    @Override
    public MetadataEventResponse deleteVersionsNotInRepository()
    {
        List<VersionMismatch> versionMismatch = repository.findVersionsMismatches();
        return deleteVersionsNotInRepository(versionMismatch);
    }

    public MetadataEventResponse deleteVersionsNotInRepository(List<VersionMismatch> versionMismatch)
    {
        MetadataEventResponse response = new MetadataEventResponse();
        versionMismatch.forEach(mismatch ->
                {
                      mismatch.versionsNotInRepository.forEach(version ->
                      {
                          delete(mismatch.groupId, mismatch.artifactId, version);
                          response.addMessage(String.format("%s-%s-%s deleted", mismatch.groupId, mismatch.artifactId, version));
                      });
                }
        );
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
    public MetadataEventResponse evictLeastRecentlyUsedVersions(int numberOfDays)
    {
        throw new UnsupportedOperationException("not implemented yet");
    }

}
