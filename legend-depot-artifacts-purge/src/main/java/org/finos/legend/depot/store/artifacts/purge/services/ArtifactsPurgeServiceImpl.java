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
import org.finos.legend.depot.domain.api.MetadataEventResponse;
import org.finos.legend.depot.domain.project.ProjectData;
import org.finos.legend.depot.services.api.projects.ManageProjectsService;
import org.finos.legend.depot.store.artifacts.api.ProjectArtifactsHandler;
import org.finos.legend.depot.store.artifacts.purge.api.ArtifactsPurgeService;
import org.finos.legend.depot.store.artifacts.services.ArtifactHandlerFactory;
import org.finos.legend.depot.tracing.services.TracerFactory;
import org.finos.legend.depot.tracing.services.prometheus.PrometheusMetricsFactory;
import org.finos.legend.sdlc.domain.model.version.VersionId;
import org.slf4j.Logger;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.finos.legend.depot.tracing.resources.ResourceLoggingAndTracing.PURGE_VERSION;


public class ArtifactsPurgeServiceImpl implements ArtifactsPurgeService
{
    private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(ArtifactsPurgeServiceImpl.class);
    private static final String SEPARATOR = "-";
    private static final String GROUP_ID = "groupId";
    private static final String ARTIFACT_ID = "artifactId";
    private static final String VERSION_ID = "versionId";

    public static final String VERSION_PURGE_COUNTER = "versionPurge";
    private static final String PURGE_OLDEST = "purge_old";

    private final ManageProjectsService projects;


    @Inject
    public ArtifactsPurgeServiceImpl(ManageProjectsService projects)
    {
        this.projects = projects;
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

    private ProjectData getProject(String groupId, String artifactId)
    {
        Optional<ProjectData> found = projects.find(groupId, artifactId);
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
        TracerFactory.get().executeWithTrace(PURGE_VERSION, () ->
        {
            getSupportedArtifactTypes().forEach(artifactType ->
            {
                ProjectArtifactsHandler artifactHandler = ArtifactHandlerFactory.getArtifactHandler(artifactType);
                if (artifactHandler != null)
                {
                    artifactHandler.delete(groupId, artifactId, versionId);
                }
            });
            ProjectData project = getProject(groupId, artifactId);
            project.removeVersion(versionId);
            PrometheusMetricsFactory.getInstance().incrementCount(VERSION_PURGE_COUNTER);
            return projects.createOrUpdate(project);
        });
    }

    @Override
    public MetadataEventResponse deleteOldestProjectVersions(String groupId, String artifactId, int versionsToKeep)
    {
        return deleteOldestProjectVersions(getProject(groupId, artifactId),versionsToKeep);
    }

    private MetadataEventResponse deleteOldestProjectVersions(ProjectData project, int versionsToKeep)
    {
        return TracerFactory.get().executeWithTrace(PURGE_OLDEST, () ->
        {
            MetadataEventResponse response = new MetadataEventResponse();
            List<VersionId> versionIds = project.getVersionsOrdered();
            int numberOfVersions = versionIds.size();
            try
            {
                while (versionIds.size() > versionsToKeep)
                {
                    VersionId versionId = versionIds.get(0);
                    delete(project.getGroupId(), project.getArtifactId(), versionId.toVersionIdString());
                    versionIds.remove(versionId);
                    project.removeVersion(versionId.toVersionIdString());
                    response.addMessage(String.format("%s-%s-%s purge", project.getGroupId(), project.getArtifactId(), versionId.toVersionIdString()));
                    PrometheusMetricsFactory.getInstance().incrementCount(VERSION_PURGE_COUNTER);
                }
                projects.createOrUpdate(project);
                response.addMessage(String.format("%s-%s purged %s versions", project.getGroupId(), project.getArtifactId(), numberOfVersions - versionIds.size()));
            }
            catch (Exception e)
            {
                 String errorMessage = String.format(" Error purging old versions %s-%s %s",project.getGroupId(),project.getArtifactId(),e.getMessage());
                 LOGGER.error(errorMessage);
                 response.addError(errorMessage);
                 PrometheusMetricsFactory.getInstance().incrementErrorCount(VERSION_PURGE_COUNTER);
            }
            return response;
        });
    }

    @Override
    public MetadataEventResponse deleteLeastRecentlyUsedVersions(int numberOfDays)
    {
        throw new UnsupportedOperationException("not implemented yet");
    }

}
