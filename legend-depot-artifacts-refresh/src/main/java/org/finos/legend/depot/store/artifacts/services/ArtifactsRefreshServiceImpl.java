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

package org.finos.legend.depot.store.artifacts.services;

import org.eclipse.collections.impl.parallel.ParallelIterate;
import org.finos.legend.depot.store.artifacts.repository.api.ArtifactRepositoryException;
import org.finos.legend.depot.store.artifacts.repository.services.RepositoryServices;
import org.finos.legend.depot.domain.api.MetadataEventResponse;
import org.finos.legend.depot.domain.notifications.MetadataNotification;
import org.finos.legend.depot.store.model.projects.StoreProjectData;
import org.finos.legend.depot.store.model.projects.StoreProjectVersionData;
import org.finos.legend.depot.domain.version.VersionAlias;
import org.finos.legend.depot.domain.version.VersionValidator;
import org.finos.legend.depot.services.api.projects.ProjectsService;
import org.finos.legend.depot.store.artifacts.api.ArtifactsRefreshService;
import org.finos.legend.depot.store.artifacts.api.ParentEvent;
import org.finos.legend.depot.store.notifications.queue.api.Queue;
import org.finos.legend.depot.tracing.services.TracerFactory;
import org.finos.legend.sdlc.domain.model.version.VersionId;
import org.slf4j.Logger;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;



public class ArtifactsRefreshServiceImpl implements ArtifactsRefreshService
{

    private static final String ALL = "all";
    private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(ArtifactsRefreshServiceImpl.class);
    private static final String REFRESH_ALL_VERSIONS_FOR_ALL_PROJECTS = "refreshAllVersionsForAllProjects";
    private static final String REFRESH_ALL_SNAPSHOT_FOR_ALL_PROJECTS = "refreshSnapshotsForAllProjects";
    private static final String REFRESH_ALL_VERSIONS_FOR_PROJECT = "refreshAllVersionsForProject";
    private static final String REFRESH_PROJECT_VERSION_ARTIFACTS = "refreshProjectVersionArtifacts";
    private static final String ALL_SNAPSHOT = "all-SNAPSHOT";
    private static final String GROUP_ID = "groupId";
    private static final String ARTIFACT_ID = "artifactId";
    private static final String VERSION_ID = "versionId";


    private final ProjectsService projects;
    private final RepositoryServices repositoryServices;
    private final Queue workQueue;


    @Inject
    public ArtifactsRefreshServiceImpl(ProjectsService projects, RepositoryServices repositoryServices, Queue refreshWorkQueue)
    {
        this.projects = projects;
        this.repositoryServices = repositoryServices;
        this.workQueue = refreshWorkQueue;
    }

    @Override
    public MetadataEventResponse  refreshAllVersionsForAllProjects(boolean fullUpdate,boolean allVersions,boolean transitive,String parentEventId)
    {
        String parentEvent = ParentEvent.build(ALL, ALL, ALL,parentEventId);
        MetadataNotification allVersionAllProjects = new MetadataNotification(ALL,ALL,ALL,ALL,fullUpdate,transitive,parentEvent);
        return executeWithTrace(REFRESH_ALL_VERSIONS_FOR_ALL_PROJECTS,allVersionAllProjects, () ->
                {
                    MetadataEventResponse result = new MetadataEventResponse();
                    String message = String.format("Executing: [%s-%s-%s], parentEventId :[%s], full/allVersions/transitive :[%s/%s/%s]",ALL,ALL,ALL,parentEvent,fullUpdate,allVersions,transitive);
                    result.addMessage(message);
                    LOGGER.info(message);
                    ParallelIterate.forEach(projects.getAllProjectCoordinates(),project -> result.combine(refreshAllVersionsForProject(project.getGroupId(),project.getArtifactId(),fullUpdate,allVersions,transitive,parentEvent)));
                    return result;
                }
        );
    }

    private MetadataEventResponse executeWithTrace(String label, MetadataNotification event, Supplier<MetadataEventResponse> supplier)
    {

        Map<String, String> tags = new HashMap<>();
        tags.put(GROUP_ID, event.getGroupId());
        tags.put(ARTIFACT_ID, event.getArtifactId());
        tags.put(VERSION_ID, event.getVersionId());
        return TracerFactory.get().executeWithTrace(label,supplier,tags);
    }

    @Override
    public MetadataEventResponse refreshDefaultSnapshotsForAllProjects(boolean fullUpdate, boolean transitive, String parentEventId)
    {
        String parentEvent = ParentEvent.build(ALL, ALL, ALL_SNAPSHOT,parentEventId);
        MetadataNotification masterSnapshotAllProjects = new MetadataNotification(ALL,ALL,ALL,ALL_SNAPSHOT,fullUpdate,transitive,parentEvent);
        return executeWithTrace(REFRESH_ALL_SNAPSHOT_FOR_ALL_PROJECTS,masterSnapshotAllProjects, () ->
                {
                    MetadataEventResponse result = new MetadataEventResponse();
                    String message = String.format("Executing: [%s-%s-%s], parentEventId :[%s], full/transitive :[%s/%s]",ALL,ALL,ALL_SNAPSHOT,parentEvent,fullUpdate,transitive);
                    result.addMessage(message);
                    LOGGER.info(message);
                    ParallelIterate.forEach(projects.getAllProjectCoordinates(),project -> result.combine(refreshAllDefaultSNAPSHOTVersionsForProject(project,fullUpdate,transitive,parentEvent)));
                    return result;
                }
        );
    }

    @Override
    public MetadataEventResponse refreshAllVersionsForProject(String groupId, String artifactId, boolean fullUpdate,boolean allVersions,boolean transitive,String parentEventId)
    {
        String parentEvent = ParentEvent.build(groupId, artifactId, ALL, parentEventId);
        StoreProjectData projectData = getProject(groupId, artifactId);
        MetadataNotification allVersionForProject = new MetadataNotification(projectData.getProjectId(),groupId,artifactId,ALL,fullUpdate,transitive,parentEvent);
        return executeWithTrace(REFRESH_ALL_VERSIONS_FOR_PROJECT, allVersionForProject, () ->
        {
            MetadataEventResponse result = new MetadataEventResponse();
            String message = String.format("Executing: [%s-%s-%s], parentEventId :[%s], full/allVersions/transitive :[%s/%s/%s]", groupId, artifactId, ALL, parentEvent, fullUpdate, allVersions, transitive);
            result.addMessage(message);
            LOGGER.info(message);
            result.combine(refreshAllDefaultSNAPSHOTVersionsForProject(projectData, fullUpdate, transitive, parentEvent));
            result.combine(refreshAllVersionsForProject(projectData, allVersions, transitive, parentEvent));
            return result;
        });
    }

    private MetadataEventResponse refreshAllDefaultSNAPSHOTVersionsForProject(StoreProjectData projectData, boolean fullUpdate, boolean transitive, String parentEvent)
    {
        String parentEventId = ParentEvent.build(projectData.getGroupId(), projectData.getArtifactId(), ALL_SNAPSHOT, parentEvent);
        MetadataEventResponse response = new MetadataEventResponse();
        Optional<StoreProjectVersionData> storeProjectVersionData = this.projects.find(projectData.getGroupId(), projectData.getArtifactId(), VersionAlias.HEAD.getName());
        if (storeProjectVersionData.isPresent() && !storeProjectVersionData.get().isEvicted())
        {
            String message = String.format("Executing: [%s-%s-%s], parentEventId :[%s], full/transitive :[%s/%s]", projectData.getGroupId(), projectData.getArtifactId(), storeProjectVersionData.get().getVersionData(), parentEvent, fullUpdate, transitive);
            LOGGER.info(message);
            response.addMessage(queueWorkToRefreshProjectVersion(projectData, storeProjectVersionData.get().getVersionId(), fullUpdate,transitive, parentEventId));
        }
        return response;
    }


    @Override
    public MetadataEventResponse refreshVersionForProject(String groupId, String artifactId, String versionId,boolean fullUpdate,boolean transitive,String parentEventId)
    {
        String parentEvent = ParentEvent.build(groupId, artifactId, versionId, parentEventId);
        StoreProjectData projectData = getProject(groupId, artifactId);
        MetadataNotification versionForProject = new MetadataNotification(projectData.getProjectId(),groupId,artifactId,versionId,fullUpdate,transitive,parentEvent);
        return executeWithTrace(REFRESH_PROJECT_VERSION_ARTIFACTS, versionForProject, () ->
        {
            MetadataEventResponse result = new MetadataEventResponse();
            String message = String.format("Executing: [%s-%s-%s], parentEventId :[%s], full/transitive :[%s/%s]",groupId,artifactId,versionId,parentEvent,fullUpdate,transitive);
            result.addMessage(message);
            LOGGER.info(message);
            result.addMessage(queueWorkToRefreshProjectVersion(projectData, versionId, fullUpdate, transitive, parentEvent));
            return result;
        });
    }

    private MetadataEventResponse refreshAllVersionsForProject(StoreProjectData projectData, boolean allVersions, boolean transitive, String parentEvent)
    {
        String parentEventId = ParentEvent.build(projectData.getGroupId(), projectData.getArtifactId(), ALL, parentEvent);
        MetadataEventResponse response = new MetadataEventResponse();

        String projectArtifacts = String.format("%s: [%s-%s]", projectData.getProjectId(), projectData.getGroupId(), projectData.getArtifactId());
        if (this.repositoryServices.areValidCoordinates(projectData.getGroupId(), projectData.getArtifactId()))
        {
            LOGGER.info("Fetching {} versions from repository", projectArtifacts);
            List<VersionId> repoVersions;
            try
            {
                repoVersions = this.repositoryServices.findVersions(projectData.getGroupId(), projectData.getArtifactId());
            }
            catch (ArtifactRepositoryException e)
            {
                response.addError(e.getMessage());
                return response;
            }

            if (repoVersions != null && !repoVersions.isEmpty())
            {
                List<VersionId> candidateVersions;
                List<StoreProjectVersionData> projectVersions = projects.find(projectData.getGroupId(), projectData.getArtifactId());
                List<String> storeVersions = projectVersions.stream().filter(pv -> !VersionValidator.isSnapshotVersion(pv.getVersionId())).map(pv -> pv.getVersionId()).collect(Collectors.toList());
                if (!allVersions && storeVersions.size() > 0)
                {
                    candidateVersions = calculateCandidateVersions(repoVersions, storeVersions);
                }
                else
                {
                    candidateVersions  = repoVersions;
                }
                if (!candidateVersions.isEmpty())
                {
                    String versionInfoMessage = String.format("%s found [%s] versions to update: %s", projectArtifacts, candidateVersions.size(), candidateVersions);
                    LOGGER.info(versionInfoMessage);
                    response.addMessage(versionInfoMessage);
                    candidateVersions.forEach(v -> response.addMessage(queueWorkToRefreshProjectVersion(projectData, v.toVersionIdString(), true, transitive, parentEventId)));
                    LOGGER.info("Finished processing all versions {}{}", projectData.getGroupId(), projectData.getArtifactId());
                }
            }
        }
        else
        {
            String badCoordinatesMessage = String.format("invalid coordinates : [%s-%s] ", projectData.getGroupId(), projectData.getArtifactId());
            LOGGER.error(badCoordinatesMessage);
            response.logError(badCoordinatesMessage);
        }
        return response;
    }

    List<VersionId> calculateCandidateVersions(List<VersionId> repoVersions, List<String> versions)
    {
        return repoVersions.stream().filter(v -> !versions.contains(v.toVersionIdString())).collect(Collectors.toList());
    }

    private String queueWorkToRefreshProjectVersion(StoreProjectData projectData, String versionId, boolean fullUpdate, boolean transitive, String parentEvent)
    {
        return String.format("queued: [%s-%s-%s], parentEventId :[%s], full/transitive :[%s/%s],event id :[%s] ",
                projectData.getGroupId(),projectData.getArtifactId(),versionId,parentEvent,fullUpdate,transitive,this.workQueue.push(new MetadataNotification(projectData.getProjectId(),projectData.getGroupId(),projectData.getArtifactId(),versionId,fullUpdate,transitive,parentEvent)));
    }

    private StoreProjectData getProject(String groupId, String artifactId)
    {
        Optional<StoreProjectData> found = projects.findCoordinates(groupId, artifactId);
        if (!found.isPresent())
        {
            throw new IllegalArgumentException("can't find project for " + groupId + "-" + artifactId);
        }
        return found.get();
    }

}
