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

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.maven.model.Model;
import org.eclipse.collections.impl.parallel.ParallelIterate;
import org.finos.legend.depot.artifacts.repository.api.ArtifactRepositoryException;
import org.finos.legend.depot.artifacts.repository.domain.ArtifactDependency;
import org.finos.legend.depot.artifacts.repository.domain.ArtifactType;
import org.finos.legend.depot.artifacts.repository.domain.VersionMismatch;
import org.finos.legend.depot.artifacts.repository.services.RepositoryServices;
import org.finos.legend.depot.domain.api.MetadataEventResponse;
import org.finos.legend.depot.domain.project.StoreProjectData;
import org.finos.legend.depot.domain.project.ProjectVersion;
import org.finos.legend.depot.domain.project.Property;
import org.finos.legend.depot.domain.project.StoreProjectVersionData;
import org.finos.legend.depot.domain.project.ProjectVersionData;
import org.finos.legend.depot.domain.project.IncludeProjectPropertiesConfiguration;
import org.finos.legend.depot.services.api.projects.ManageProjectsService;
import org.finos.legend.depot.store.admin.api.artifacts.ArtifactsFilesStore;
import org.finos.legend.depot.store.admin.api.artifacts.RefreshStatusStore;
import org.finos.legend.depot.store.admin.domain.artifacts.ArtifactFile;
import org.finos.legend.depot.store.admin.domain.artifacts.RefreshStatus;
import org.finos.legend.depot.store.artifacts.api.ArtifactsRefreshService;
import org.finos.legend.depot.store.artifacts.api.ProjectArtifactsHandler;
import org.finos.legend.depot.store.notifications.api.Queue;
import org.finos.legend.depot.store.notifications.domain.MetadataNotification;
import org.finos.legend.depot.tracing.services.TracerFactory;
import org.finos.legend.depot.tracing.services.prometheus.PrometheusMetricsFactory;
import org.finos.legend.sdlc.domain.model.version.VersionId;
import org.slf4j.Logger;

import javax.inject.Inject;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static org.finos.legend.depot.domain.version.VersionValidator.MASTER_SNAPSHOT;

public class ArtifactsRefreshServiceImpl implements ArtifactsRefreshService
{

    private static final String ALL = "all";
    private static final String MISSING = "missing";
    private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(ArtifactsRefreshServiceImpl.class);
    private static final String REFRESH_ALL_VERSIONS_FOR_ALL_PROJECTS = "refreshAllVersionsForAllProjects";
    private static final String REFRESH_MASTER_SNAPSHOT_FOR_ALL_PROJECTS = "refreshMasterSnapshotForAllProjects";
    private static final String REFRESH_ALL_VERSIONS_FOR_PROJECT = "refreshAllVersionsForProject";
    private static final String REFRESH_PROJECTS_WITH_MISSING_VERSIONS = "refreshProjectsWithMissingVersions";
    private static final String REFRESH_PROJECT_VERSION_ARTIFACTS = "refreshProjectVersionArtifacts";
    private static final String SEPARATOR = "-";
    private static final String GROUP_ID = "groupId";
    private static final String ARTIFACT_ID = "artifactId";
    private static final String VERSION_ID = "versionId";

    public static final String VERSION_REFRESH_COUNTER = "versionRefresh";
    public static final String VERSION_REFRESH_DURATION = "versionRefresh_duration";
    public static final String VERSION_REFRESH_DURATION_HELP = "version refresh duration";
    public static final String TOTAL_NUMBER_OF_VERSIONS_REFRESH = "total number of versions refresh";
    public static final String EVENT_ID = "eventId:";

    private final ManageProjectsService projects;
    private final RefreshStatusStore store;
    private final RepositoryServices repositoryServices;
    private final ArtifactsFilesStore artifacts;
    private final Queue workQueue;
    private final List<String> projectProperties;


    @Inject
    public ArtifactsRefreshServiceImpl(ManageProjectsService projects, RefreshStatusStore store, RepositoryServices repositoryServices, ArtifactsFilesStore artifacts, Queue refreshWorkQueue, IncludeProjectPropertiesConfiguration includePropertyConfig)
    {
        this.projects = projects;
        this.store = store;
        this.repositoryServices = repositoryServices;
        this.artifacts = artifacts;
        this.workQueue = refreshWorkQueue;
        this.projectProperties = includePropertyConfig.getProperties();

        try
        {
            MessageDigest.getInstance("SHA-256");
        }
        catch (NoSuchAlgorithmException e)
        {
            LOGGER.info(e.getLocalizedMessage());
        }
    }

    private Logger getLOGGER()
    {
        return LOGGER;
    }

    private Set<ArtifactType> getSupportedArtifactTypes()
    {
        return ArtifactHandlerFactory.getSupportedTypes();
    }

    private MetadataEventResponse handleRefresh(String groupId, String artifactId, String version, String parentEventId, Supplier<MetadataEventResponse> functionToExecute)
    {
        MetadataEventResponse response = new MetadataEventResponse();
        getLOGGER().info("Starting [{}-{}-{}] refresh", groupId, artifactId, version);
        RefreshStatus storeStatus = store.get(groupId, artifactId, version);
        if (storeStatus.isRunning())
        {
            String skip  = String.format("Other instance is running, skipping [%s-%s-%s] refresh",  groupId, artifactId, version);
            getLOGGER().info(skip);
            response.addMessage(skip);
            return response;
        }
        try
        {
            if (parentEventId != null)
            {
                storeStatus.setParentEventId(parentEventId);
            }
            PrometheusMetricsFactory.getInstance().incrementCount(VERSION_REFRESH_COUNTER);
            storeStatus = store.createOrUpdate(storeStatus.startRunning());
            response = functionToExecute.get();
        }
        catch (Exception e)
        {
            String message = String.format("Error refreshing [%s-%s-%s] : %s",groupId,artifactId,version,e.getMessage());
            response.addError(message);
            LOGGER.error("Error refreshing [{}-{}-{}] : {} ",groupId,artifactId,version,e);
        }
        finally
        {
            storeStatus.setTraceId(TracerFactory.get().getActiveSpanTraceId());
            store.createOrUpdate(storeStatus.stopRunning(response));
            if (response.hasErrors())
            {
                PrometheusMetricsFactory.getInstance().incrementErrorCount(VERSION_REFRESH_COUNTER);
            }
            getLOGGER().info("Finished [{}-{}-{}] refresh", groupId, artifactId, version);
        }
        return response;
    }

    private void decorateSpanWithVersionInfo(String groupId,String artifactId, String versionId)
    {
        Map<String, String> tags = new HashMap<>();  
        tags.put(GROUP_ID, groupId);
        tags.put(ARTIFACT_ID, artifactId);
        tags.put(VERSION_ID, versionId);
        TracerFactory.get().addTags(tags);
    }


    private List<StoreProjectData> getProjects()
    {
        List<StoreProjectData> all = projects.getAllProjectCoordinates();
        getLOGGER().info("[{}] projects found ", all.size());
        return all;
    }

    private StoreProjectData getProject(String groupId, String artifactId)
    {
        Optional<StoreProjectData> found = projects.findCoordinates(groupId, artifactId);
        if (!found.isPresent())
        {
            throw new IllegalArgumentException("can't find project for " + groupId + SEPARATOR + artifactId);
        }
        return found.get();
    }


    @Override
    public MetadataEventResponse  refreshAllVersionsForAllProjects(boolean fullUpdate,boolean transitive,String parentEventId)
    {
        return executeWithTrace(REFRESH_ALL_VERSIONS_FOR_ALL_PROJECTS,ALL, ALL, ALL, () ->
                {
                    MetadataEventResponse result = new MetadataEventResponse();
                    String parentEvent = buildParentEventId(ALL, ALL, ALL,parentEventId);
                    String message = String.format("Executing: [%s-%s-%s], parentEventId :[%s], full/transitive :[%s/%s]",ALL,ALL,ALL,parentEvent,fullUpdate,transitive);
                    result.addMessage(message);
                    LOGGER.info(message);
                    ParallelIterate.forEach(getProjects(),project -> result.combine(refreshAllVersionsForProject(project,fullUpdate,transitive,parentEvent)));
                    return result;
                }
        );
    }
    
    @Override
    public MetadataEventResponse refreshMasterSnapshotForAllProjects(boolean fullUpdate,boolean transitive,String parentEventId)
    {
        return executeWithTrace(REFRESH_MASTER_SNAPSHOT_FOR_ALL_PROJECTS, ALL, ALL, MASTER_SNAPSHOT, () ->
                {
                    MetadataEventResponse result = new MetadataEventResponse();
                    String parentEvent =  buildParentEventId(ALL, ALL, MASTER_SNAPSHOT,parentEventId);
                    String message = String.format("Executing: [%s-%s-%s], parentEventId :[%s], full/transitive :[%s/%s]",ALL,ALL,MASTER_SNAPSHOT,parentEvent,fullUpdate,transitive);
                    result.addMessage(message);
                    LOGGER.info(message);
                    ParallelIterate.forEach(getProjects(), project -> result.addMessage(queueWorkToRefreshProjectVersion(project, MASTER_SNAPSHOT, fullUpdate,transitive, parentEvent)));
                    return result;
                }
        );
    }

    @Override
    public MetadataEventResponse refreshAllVersionsForProject(String groupId, String artifactId, boolean fullUpdate,boolean transitive,String parentEventId)
    {
        return executeWithTrace(REFRESH_ALL_VERSIONS_FOR_PROJECT, groupId, artifactId, ALL, () ->
        {
            MetadataEventResponse result = new MetadataEventResponse();
            result.combine(validateInput(groupId, artifactId, ALL));
            if (!result.hasErrors())
            {
                String parentEvent = buildParentEventId(groupId, artifactId, ALL, parentEventId);
                String message = String.format("Executing: [%s-%s-%s], parentEventId :[%s], full/transitive :[%s/%s]",groupId,artifactId,ALL,parentEvent,fullUpdate,transitive);
                result.addMessage(message);
                LOGGER.info(message);
                StoreProjectData projectData = getProject(groupId, artifactId);
                result.combine(refreshAllVersionsForProject(projectData, fullUpdate, transitive, parentEvent));
                result.addMessage(queueWorkToRefreshProjectVersion(projectData, MASTER_SNAPSHOT, fullUpdate, transitive, parentEvent));
            }
            return result;
        });
    }

    private String buildParentEventId(String groupId, String artifactId, String versionId,String parentEventId)
    {
        return parentEventId != null ? parentEventId : groupId + SEPARATOR + artifactId + SEPARATOR + versionId;
    }

    private String queueWorkToRefreshProjectVersion(StoreProjectData projectData, String versionId, boolean fullUpdate, boolean transitive, String parentEvent)
    {
        return EVENT_ID + this.workQueue.push(new MetadataNotification(projectData.getProjectId(),projectData.getGroupId(),projectData.getArtifactId(),versionId,fullUpdate,transitive,parentEvent));
    }

    @Override
    public MetadataEventResponse refreshVersionForProject(String groupId, String artifactId, String versionId, boolean fullUpdate, boolean transitive,String parentEventId)
    {
        MetadataEventResponse response = new MetadataEventResponse();
        response.combine(validateInput(groupId,artifactId,versionId));
        if (response.hasErrors())
        {
            return response;
        }
        return refreshVersionForProject(getProject(groupId, artifactId), versionId, fullUpdate, transitive, parentEventId);
    }

    @Override
    public MetadataEventResponse refresh(MetadataNotification evt)
    {
        MetadataEventResponse response = new MetadataEventResponse();
        String message = String.format("Executing: [%s-%s-%s], eventId:[%s] parentEventId :[%s], full/transitive :[%s/%s], retries:[%s]",evt.getGroupId(),evt.getArtifactId(),
                evt.getVersionId(),evt.getEventId(),evt.getParentEventId(),evt.isFullUpdate(),evt.isTransitive(),evt.getRetries());
        response.addMessage(message);
        return response.combine(refreshVersionForProject(evt.getGroupId(),evt.getArtifactId(), evt.getVersionId(),evt.isFullUpdate(),evt.isTransitive(),evt.getParentEventId()));
    }

    private MetadataEventResponse validateInput(String groupId, String artifactId, String versionId)
    {
        MetadataEventResponse response = new MetadataEventResponse();
        if (!projects.findCoordinates(groupId, artifactId).isPresent())
        {
            response.addError(String.format("No Project found for %s-%s", groupId, artifactId));
        }
        else
        {
            if (!MASTER_SNAPSHOT.equals(versionId) && !ALL.equals(versionId))
            {
                try
                {
                    List<VersionId> versionsInRepo = this.repositoryServices.findVersions(groupId, artifactId);
                    if (versionsInRepo.isEmpty() || !versionsInRepo.contains(VersionId.parseVersionId(versionId)))
                    {
                        response.addError(String.format("Version %s does not exists for %s-%s", versionId, groupId, artifactId));
                        return response;
                    }
                }
                catch (ArtifactRepositoryException e)
                {
                    throw new RuntimeException(e);
                }
            }
        }
        return response;
    }


    private MetadataEventResponse refreshVersionForProject(StoreProjectData projectData, String versionId, boolean fullUpdate,boolean transitive,String parentEventId)
    {
        return executeWithTrace(REFRESH_PROJECT_VERSION_ARTIFACTS, projectData.getGroupId(), projectData.getArtifactId(), versionId, parentEventId, () ->
        {
            long refreshStartTime = System.currentTimeMillis();
            LOGGER.info("Started updating project data [{}{}{}]", projectData.getGroupId(), projectData.getArtifactId(), versionId);
            MetadataEventResponse response = validateInput(projectData.getGroupId(),projectData.getArtifactId(),versionId);
            String message = String.format("Executing: [%s-%s-%s], parentEventId :[%s], full/transitive :[%s/%s]",projectData.getGroupId(),projectData.getArtifactId(),versionId,parentEventId,fullUpdate,transitive);
            response.addMessage(message);
            LOGGER.info(message);
            List<ProjectVersion> newDependencies = calculateDependencies(projectData.getGroupId(), projectData.getArtifactId(), versionId);
            response.combine(validateDependencies(newDependencies, versionId));
            if (!response.hasErrors())
            {
                getSupportedArtifactTypes().forEach(artifactType -> response.combine(executeVersionRefresh(artifactType, projectData, versionId, fullUpdate)));
                LOGGER.info("Finished processing artifacts for [{}{}{}]", projectData.getGroupId(), projectData.getArtifactId(), versionId);
                if (!response.hasErrors())
                {
                    updateProjectData(projectData, versionId, newDependencies);
                    LOGGER.info("Finished updating project data [{}{}{}]", projectData.getGroupId(), projectData.getArtifactId(), versionId);
                    if (transitive)
                    {
                        LOGGER.info("Started updating {} dependencies for [{}{}{}]", newDependencies.size(), projectData.getGroupId(), projectData.getArtifactId(), versionId);
                        response.combine(refreshDependencies(projectData,versionId, newDependencies,fullUpdate,transitive,parentEventId));
                        LOGGER.info("Finished updating {} dependencies for [{}{}{}]",newDependencies.size(), projectData.getGroupId(), projectData.getArtifactId(), versionId);
                    }
                    else
                    {
                        newDependencies.stream().forEach(dep ->
                        {
                            try
                            {
                                projects.checkExists(dep.getGroupId(), dep.getArtifactId(), dep.getVersionId());
                            }
                            catch (IllegalArgumentException exception)
                            {
                                String missingDepError = String.format("Dependency %s-%s-%s not found in store", dep.getGroupId(), dep.getArtifactId(), dep.getVersionId());
                                response.addError(missingDepError);
                                LOGGER.error(missingDepError);
                            }
                        });
                    }
                }
            }
            long refreshEndTime = System.currentTimeMillis();
            PrometheusMetricsFactory.getInstance().observeHistogram(VERSION_REFRESH_DURATION,refreshStartTime,refreshEndTime);
            return response;
        });
    }

    private MetadataEventResponse validateDependencies(List<ProjectVersion> newDependencies, String versionId)
    {
        MetadataEventResponse response = new MetadataEventResponse();
        newDependencies.stream().forEach(dep ->
        {
            if (!versionId.equals(MASTER_SNAPSHOT) && dep.getVersionId().equals(MASTER_SNAPSHOT))
            {
                String illegalDepError = String.format("Snapshot dependency %s-%s-%s not allowed in versions", dep.getGroupId(), dep.getArtifactId(), dep.getVersionId());
                response.addError(illegalDepError);
                LOGGER.error(illegalDepError);
            }
        });
        return response;
    }

    private void updateProjectData(StoreProjectData projectData, String versionId, List<ProjectVersion> newDependencies)
    {
        Optional<StoreProjectVersionData> projectVersionData = projects.find(projectData.getGroupId(), projectData.getArtifactId(), versionId);
        StoreProjectVersionData data = projectVersionData.isPresent() ? projectVersionData.get() : new StoreProjectVersionData(projectData.getGroupId(), projectData.getArtifactId(), versionId);
        ProjectVersionData versionData = data.getVersionData();
        versionData.setDependencies(newDependencies);
        versionData.setProperties(extractProjectPropertiesForVersion(data));
        data.setVersionData(versionData);
        data.setEvicted(false);
        projects.createOrUpdate(data);
    }

    private List<ProjectVersion> calculateDependencies(String groupId, String artifactId, String versionId)
    {
        List<ProjectVersion> versionDependencies = new ArrayList<>();
        Set<ArtifactDependency> dependencies = this.repositoryServices.findDependencies(groupId, artifactId, versionId);
        LOGGER.info("Found [{}] dependencies for [{}{}{}]", dependencies.size(), groupId, artifactId, versionId);
        dependencies.forEach(dependency ->  versionDependencies.add(new ProjectVersion(dependency.getGroupId(), dependency.getArtifactId(), dependency.getVersion())));
        return versionDependencies;
    }

    private MetadataEventResponse executeWithTrace(String label, String groupId, String artifactId, String version, Supplier<MetadataEventResponse> functionToExecute)
    {
        return executeWithTrace(label, groupId, artifactId, version, null, functionToExecute);
    }

    private MetadataEventResponse executeWithTrace(String label, String groupId, String artifactId, String version, String parentEventId, Supplier<MetadataEventResponse> functionToExecute)
    {
        return TracerFactory.get().executeWithTrace(label, () ->
        {
            decorateSpanWithVersionInfo(groupId, artifactId, version);
            return handleRefresh(groupId, artifactId, version, parentEventId, functionToExecute);
        });
    }

    private List<Property> extractProjectPropertiesForVersion(StoreProjectVersionData projectData)
    {
        List<Property> projectPropertyList = new ArrayList<>();
        Model model = this.repositoryServices.getPOM(projectData.getGroupId(), projectData.getArtifactId(), projectData.getVersionId());
        if (model != null)
        {
            Enumeration<?> propertyNames = model.getProperties().keys();

            while (propertyNames.hasMoreElements())
            {
                String propertyName = propertyNames.nextElement().toString();
                if (projectProperties.contains(propertyName) || projectProperties.stream().anyMatch(propertyName::matches))
                {
                    projectPropertyList.add(new Property(propertyName, model.getProperties().getProperty(propertyName)));
                }
            }
        }
        return projectPropertyList;
    }

    private MetadataEventResponse refreshDependencies(StoreProjectData projectData, String versionId, List<ProjectVersion> dependencies, boolean fullUpdate, boolean transitive, String parentEventId)
    {
        MetadataEventResponse response = new MetadataEventResponse();
        dependencies.stream().forEach(dependency ->
        {
            Optional<StoreProjectData> dependent = projects.findCoordinates(dependency.getGroupId(), dependency.getArtifactId());
            if (dependent.isPresent())
            {
                StoreProjectData dependentProject = dependent.get();
                String projectCoordinates = String.format("[%s-%s-%s]", projectData.getGroupId(), projectData.getArtifactId(), versionId);
                String dependencyCoordinates = String.format("[%s-%s-%s]", dependency.getGroupId(), dependency.getArtifactId(), dependency.getVersionId());
                if (projects.find(dependency.getGroupId(), dependency.getArtifactId(), dependency.getVersionId()).isPresent())
                {
                    response.addMessage(String.format("Skipping update dependency %s -> %s, already in store", projectCoordinates, dependencyCoordinates));
                }
                else
                {
                    response.addMessage(String.format("Processing dependency %s -> %s", projectCoordinates, dependencyCoordinates));
                    response.addMessage(queueWorkToRefreshProjectVersion(dependentProject, dependency.getVersionId(), fullUpdate, transitive, parentEventId));
                }
            }
            else
            {
                response.addError(String.format("Could not find dependent project: [%s-%s]", dependency.getGroupId(), dependency.getArtifactId()));
                return;
            }
        });
        return response;
    }

    private MetadataEventResponse executeVersionRefresh(ArtifactType artifactType, StoreProjectData projectData, String versionId, boolean fullUpdate)
    {
        MetadataEventResponse response = new MetadataEventResponse();
        ProjectArtifactsHandler refreshHandler = ArtifactHandlerFactory.getArtifactHandler(artifactType);
        if (refreshHandler != null)
        {
            List<File> files = findArtifactFiles(artifactType, projectData, versionId, fullUpdate);
            if (files != null && !files.isEmpty())
            {
                response.addMessage(String.format("[%s] files found [%s] artifacts to process [%s-%s-%s], fullUpdate: %s",files.size(),artifactType,projectData.getGroupId(),projectData.getArtifactId(),versionId,fullUpdate));
                response.combine(refreshHandler.refreshProjectVersionArtifacts(projectData, versionId, files));
            }
            else
            {
                response.addMessage(String.format("No %s artifacts to process [%s-%s-%s], fullUpdate: %s",artifactType,projectData.getGroupId(),projectData.getArtifactId(),versionId,fullUpdate));
            }
        }
        else
        {
            response.addError(String.format("handler not found for artifact type %s, please check your configuration",artifactType));
        }
        return response;
    }

    private MetadataEventResponse refreshAllVersionsForProject(StoreProjectData projectData, boolean fullUpdate, boolean transitive, String parentEvent)
    {
        String parentEventId = buildParentEventId(projectData.getGroupId(), projectData.getArtifactId(), ALL, parentEvent);
        MetadataEventResponse response = new MetadataEventResponse();
        decorateSpanWithVersionInfo(projectData.getGroupId(), projectData.getArtifactId(), ALL);
        String projectArtifacts = String.format("%s: [%s-%s]", projectData.getProjectId(), projectData.getGroupId(), projectData.getArtifactId());
        if (this.repositoryServices.areValidCoordinates(projectData.getGroupId(), projectData.getArtifactId()))
        {
            getLOGGER().info("Fetching {} versions from repository", projectArtifacts);
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
                Optional<VersionId> latestVersion = projects.getLatestVersion(projectData.getGroupId(), projectData.getArtifactId());
                if (!fullUpdate && latestVersion.isPresent())
                {
                    candidateVersions = calculateCandidateVersions(repoVersions, latestVersion.get());
                }
                else
                {
                    candidateVersions  = repoVersions;
                }
                String versionInfoMessage = String.format("%s found [%s] versions to update: %s", projectArtifacts, candidateVersions.size(), candidateVersions);
                getLOGGER().info(versionInfoMessage);
                response.addMessage(versionInfoMessage);
                candidateVersions.forEach(v -> response.addMessage(queueWorkToRefreshProjectVersion(projectData, v.toVersionIdString(), fullUpdate, transitive, parentEventId)));
                LOGGER.info("Finished processing all versions {}{}", projectData.getGroupId(), projectData.getArtifactId());
            }
        }
        else
        {
            String badCoordinatesMessage = String.format("invalid coordinates : [%s-%s] ", projectData.getGroupId(), projectData.getArtifactId());
            getLOGGER().error(badCoordinatesMessage);
            response.logError(badCoordinatesMessage);
        }
        return response;

    }

    List<VersionId> calculateCandidateVersions(List<VersionId> repoVersions, VersionId latest)
    {
        return repoVersions.stream().filter(v -> v.compareTo(latest) > 0).collect(Collectors.toList());
    }

    private List<File> findArtifactFiles(ArtifactType type, StoreProjectData projectData, String versionId, boolean includeUnchangedFiles)
    {
        List<File> filesFromRepo = this.repositoryServices.findFiles(type, projectData.getGroupId(), projectData.getArtifactId(), versionId);
        return filesFromRepo.stream().filter(file -> includeUnchangedFiles || artifactFileHasChangedOrNotBeenProcessed(file)).collect(Collectors.toList());
    }

    private boolean artifactFileHasChangedOrNotBeenProcessed(File file)
    {
        String filePath = file.getPath();
        Optional<ArtifactFile> artifactDetails = this.artifacts.find(filePath);
        try
        {
            String fileCheckSum = DigestUtils.sha256Hex(new FileInputStream(filePath));
            if (!artifactDetails.isPresent() || !MessageDigest.isEqual(fileCheckSum.getBytes(), artifactDetails.get().getCheckSum().getBytes()))
            {
                LOGGER.info("loading artifacts from updated file: {}", filePath);
                LOGGER.info("file check sum: {}", fileCheckSum);
                this.artifacts.createOrUpdate(new ArtifactFile(filePath, fileCheckSum));
                return true;
            }
        }
        catch (IOException e)
        {
            LOGGER.error(e.getMessage());
            return true;
        }
        return false;
    }


    @Override
    public MetadataEventResponse refreshProjectsWithMissingVersions(String parentEvent)
    {
        return executeWithTrace(REFRESH_PROJECTS_WITH_MISSING_VERSIONS,ALL, ALL, MISSING, () ->
        {
            MetadataEventResponse response = new MetadataEventResponse();
            String parentEventId = buildParentEventId(ALL, ALL,MISSING,parentEvent);
            String infoMessage = String.format("Executing: [%s-%s-%s], parentEventId :[%s], full/transitive :[%s/%s]",ALL,ALL,MISSING,parentEventId,true,false);
            response.addMessage(infoMessage);
            LOGGER.info(infoMessage);
            List<VersionMismatch> projectsWithMissingVersions = this.repositoryServices.findVersionsMismatches().stream().filter(r -> !r.versionsNotInStore.isEmpty()).collect(Collectors.toList());
            String countInfo = String.format("Starting fixing [%s] projects with missing versions",projectsWithMissingVersions.size());
            LOGGER.info(countInfo);
            response.addMessage(countInfo);
            AtomicInteger totalMissingVersions = new AtomicInteger();
            projectsWithMissingVersions.forEach(vm ->
                    {
                        vm.versionsNotInStore.forEach(missingVersion ->
                                {
                                    try
                                    {
                                        response.addMessage(queueWorkToRefreshProjectVersion(getProject(vm.groupId, vm.artifactId), missingVersion, true,false,parentEventId));
                                        String message = String.format("queued fixing missing version: %s-%s-%s ", vm.groupId, vm.artifactId, missingVersion);
                                        LOGGER.info(message);
                                    }
                                    catch (Exception e)
                                    {
                                        String message = String.format("queuing failed for missing version: %s-%s-%s ", vm.groupId, vm.artifactId, missingVersion);
                                        LOGGER.error(message);
                                        response.addError(message);
                                    }
                                    totalMissingVersions.getAndIncrement();
                                }
                        );
                    }
            );
            LOGGER.info("Fixed [{}] missing versions",totalMissingVersions);
            return response;
        });
    }

    @Override
    public long deleteOldRefreshStatuses(int days)
    {
        LocalDateTime timeToLive = LocalDateTime.now().minusDays(days);
        List<RefreshStatus> refreshStatuses = this.store.find(null,null,null,null,null,null,null,timeToLive);
        refreshStatuses.forEach(status -> this.store.delete(status.getId()));
        LOGGER.info("deleted [{}] statuses older than [{}] days",refreshStatuses.size(),days);
        return refreshStatuses.size();
    }
}
