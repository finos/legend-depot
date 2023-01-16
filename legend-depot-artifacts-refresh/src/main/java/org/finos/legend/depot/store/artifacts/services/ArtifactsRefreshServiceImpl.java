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
import org.finos.legend.depot.domain.project.IncludeProjectPropertiesConfiguration;
import org.finos.legend.depot.domain.project.ProjectData;
import org.finos.legend.depot.domain.project.ProjectProperty;
import org.finos.legend.depot.domain.project.ProjectVersion;
import org.finos.legend.depot.domain.project.ProjectVersionDependency;
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
        getLOGGER().info("Starting [{}{}{}] refresh", groupId, artifactId, version);
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
            storeStatus.getResponse().addError(message);
            response.addMessage(message);
            PrometheusMetricsFactory.getInstance().incrementErrorCount(VERSION_REFRESH_COUNTER);
            LOGGER.error("Error refreshing [{}{}{}] : {} ",groupId,artifactId,version,e);
        }
        finally
        {
            store.createOrUpdate(storeStatus.stopRunning(response));
            getLOGGER().info("Finished [{}{}{}] refresh", groupId, artifactId, version);
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


    private List<ProjectData> getProjects()
    {
        List<ProjectData> all = projects.getAll();
        getLOGGER().info("[{}] projects found ", all.size());
        return all;
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
    public MetadataEventResponse  refreshAllVersionsForAllProjects(boolean fullUpdate,boolean transitive,String parentEventId)
    {
        return executeWithTrace(REFRESH_ALL_VERSIONS_FOR_ALL_PROJECTS,ALL, ALL, ALL, () ->
                {
                    MetadataEventResponse result = new MetadataEventResponse();
                    String parentEvent = buildParentEventId(ALL, ALL, ALL,parentEventId);
                    result.addMessage(String.format("Executing :[%s-%s-%s]",ALL,ALL,ALL));
                    result.addMessage(String.format("Parent event :[%s], full/transitive :[%s/%s]",parentEvent,fullUpdate,transitive));
                    LOGGER.info("Executing {},{}{}{}",REFRESH_ALL_VERSIONS_FOR_ALL_PROJECTS,ALL, ALL, ALL);
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
                    result.addMessage(String.format("Executing :[%s-%s-%s]",ALL,ALL,ALL));
                    result.addMessage(String.format("Parent event :[%s], full/transitive :[%s/%s]",parentEvent,fullUpdate,transitive));
                    LOGGER.info("Executing {},{}{}{}",REFRESH_MASTER_SNAPSHOT_FOR_ALL_PROJECTS,ALL, ALL, MASTER_SNAPSHOT);
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
                result.addMessage(String.format("Parent event [%s], full/transitive [%s/%s]", parentEvent, fullUpdate, transitive));
                result.combine(refreshAllVersionsForProject(getProject(groupId, artifactId), fullUpdate, transitive, parentEvent));
                result.combine(refreshVersionForProject(groupId, artifactId, MASTER_SNAPSHOT, fullUpdate, transitive, parentEvent));
            }
            return result;
        });
    }

    private String buildParentEventId(String groupId, String artifactId, String versionId,String parentEventId)
    {
        return parentEventId != null ? parentEventId : groupId + SEPARATOR + artifactId + SEPARATOR + versionId;
    }

    private String queueWorkToRefreshProjectVersion(ProjectData projectData, String versionId, boolean fullUpdate,boolean transitive, String parentEvent)
    {
        return this.workQueue.push(new MetadataNotification(projectData.getProjectId(),projectData.getGroupId(),projectData.getArtifactId(),versionId,fullUpdate,transitive,parentEvent));
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

    private MetadataEventResponse validateInput(String groupId, String artifactId, String versionId)
    {
        MetadataEventResponse response = new MetadataEventResponse();
        if (!this.projects.find(groupId, artifactId).isPresent())
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


    private MetadataEventResponse refreshVersionForProject(ProjectData project, String versionId, boolean fullUpdate,boolean transitive,String parentEventId)
    {
        return executeWithTrace(REFRESH_PROJECT_VERSION_ARTIFACTS, project.getGroupId(), project.getArtifactId(), versionId, parentEventId, () ->
        {
            MetadataEventResponse response = new MetadataEventResponse();
            long refreshStartTime = System.currentTimeMillis();
            response.combine(validateInput(project.getGroupId(),project.getArtifactId(),versionId));
            response.addMessage(String.format("Processing [%s-%s-%s] fullUpdate/transitive? [%s/%s] parent:[%s]", project.getGroupId(), project.getArtifactId(), versionId, fullUpdate, transitive,parentEventId));
            getSupportedArtifactTypes().forEach(artifactType -> response.combine(executeVersionRefresh(artifactType, project, versionId,fullUpdate)));
            if (!response.hasErrors())
            {
                List<ProjectVersionDependency> newDependencies = calculateDependencies(project.getGroupId(), project.getArtifactId(), versionId);
                if (transitive)
                {
                    response.combine(refreshDependencies(newDependencies,fullUpdate,transitive,parentEventId));
                    LOGGER.info("Finished updating {} dependencies for [{}{}{}]", project.getDependencies(versionId).size(), project.getGroupId(), project.getArtifactId(), versionId);
                }
                else
                {
                    newDependencies.stream().forEach(dep ->
                    {
                        try
                        {
                            projects.checkExists(dep.getDependency().getGroupId(), dep.getDependency().getArtifactId(), dep.getDependency().getVersionId());
                        }
                        catch (IllegalArgumentException exception)
                        {
                            String missingDepError = String.format("Dependency %s-%s-%s not found in store", dep.getDependency().getGroupId(), dep.getDependency().getArtifactId(), dep.getDependency().getVersionId());
                            response.addError(missingDepError);
                            LOGGER.error(missingDepError);
                        }
                    });
                }
                if (!response.hasErrors())
                {
                    Optional<ProjectData> latestProjectData = projects.find(project.getGroupId(), project.getArtifactId());
                    refreshProjectData(versionId, newDependencies, latestProjectData);
                }
            }
            long refreshEndTime = System.currentTimeMillis();
            PrometheusMetricsFactory.getInstance().observeHistogram(VERSION_REFRESH_DURATION,refreshStartTime,refreshEndTime);
            return response;
        });
    }

    private void refreshProjectData(String versionId, List<ProjectVersionDependency> newDependencies, Optional<ProjectData> latestProjectData)
    {
        latestProjectData.ifPresent(p ->
        {
            p.addVersion(versionId);
            refreshProjectProperties(p, versionId, extractProjectPropertiesForVersion(p, versionId));
            p.getDependencies(versionId).forEach(p::removeDependency);
            p.addDependencies(newDependencies);
            projects.createOrUpdate(p);
        });
    }

    private List<ProjectVersionDependency> calculateDependencies(String groupId, String artifactId, String versionId)
    {
        List<ProjectVersionDependency> versionDependencies = new ArrayList<>();
        Set<ArtifactDependency> dependencies = this.repositoryServices.findDependencies(groupId, artifactId, versionId);
        LOGGER.info("Found [{}] dependencies for [{}{}{}]", dependencies.size(), groupId, artifactId, versionId);
        dependencies.forEach(dependency ->  versionDependencies.add(new ProjectVersionDependency(groupId, artifactId, versionId,
                new ProjectVersion(dependency.getGroupId(), dependency.getArtifactId(), dependency.getVersion()))));
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

    void refreshProjectProperties(ProjectData project, String versionId, List<ProjectProperty> projectPropertyList)
    {
        if (versionId.equals(MASTER_SNAPSHOT))
       {
           project.removePropertiesForProjectVersionID(versionId);
       }
       project.addProperties(projectPropertyList);
    }

    private List<ProjectProperty> extractProjectPropertiesForVersion(ProjectData project, String versionId)
    {
        List<ProjectProperty> projectPropertyList = new ArrayList<>();
        Model model = this.repositoryServices.getPOM(project.getGroupId(), project.getArtifactId(), versionId);
        if (model != null)
        {
            Enumeration<?> propertyNames = model.getProperties().keys();

            while (propertyNames.hasMoreElements())
            {
                String propertyName = propertyNames.nextElement().toString();
                if (projectProperties.contains(propertyName) || projectProperties.stream().anyMatch(propertyName::matches))
                {
                    projectPropertyList.add(new ProjectProperty(propertyName, model.getProperties().getProperty(propertyName), versionId));
                }
            }
        }
        return projectPropertyList;
    }

    private MetadataEventResponse refreshDependencies(List<ProjectVersionDependency> dependencies,boolean fullUpdate,boolean transitive,String parentEventId)
    {
        MetadataEventResponse response = new MetadataEventResponse();
        dependencies.stream().forEach(dependency ->
        {
            Optional<ProjectData> found = projects.find(dependency.getDependency().getGroupId(), dependency.getDependency().getArtifactId());
            if (found.isPresent())
            {
                ProjectData dependentProject = found.get();
                String projectCoordinates = String.format("[%s-%s-%s]", dependency.getGroupId(), dependency.getArtifactId(), dependency.getVersionId());
                String dependencyCoordinates = String.format("[%s-%s-%s]", dependency.getDependency().getGroupId(), dependency.getDependency().getArtifactId(), dependency.getDependency().getVersionId());
                response.addMessage(String.format("Processing dependency %s -> %s", projectCoordinates, dependencyCoordinates));
                response.addMessage(queueWorkToRefreshProjectVersion(dependentProject,dependency.getDependency().getVersionId(), fullUpdate,transitive,parentEventId));
                if (response.hasErrors())
                {
                    return;
                }
            }
            else
            {
                response.addError(String.format("Could not find dependent project: [%s-%s]", dependency.getDependency().getGroupId(), dependency.getDependency().getArtifactId()));
                return;
            }
        });
        return response;
    }

    private MetadataEventResponse executeVersionRefresh(ArtifactType artifactType, ProjectData projectData, String versionId, boolean fullUpdate)
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

    private MetadataEventResponse refreshAllVersionsForProject(ProjectData project, boolean fullUpdate, boolean transitive, String parentEvent)
    {
        String parentEventId = buildParentEventId(project.getGroupId(), project.getArtifactId(), ALL, parentEvent);
        MetadataEventResponse response = new MetadataEventResponse();
        decorateSpanWithVersionInfo(project.getGroupId(), project.getArtifactId(), ALL);
        String projectArtifacts = String.format("%s: [%s-%s]", project.getProjectId(), project.getGroupId(), project.getArtifactId());
        if (this.repositoryServices.areValidCoordinates(project.getGroupId(), project.getArtifactId()))
        {
            getLOGGER().info("Fetching {} versions from repository", projectArtifacts);
            List<VersionId> repoVersions;
            try
            {
                repoVersions = this.repositoryServices.findVersions(project.getGroupId(), project.getArtifactId());
            }
            catch (ArtifactRepositoryException e)
            {
                response.addError(e.getMessage());
                return response;
            }

            if (repoVersions != null && !repoVersions.isEmpty())
            {
                List<VersionId> candidateVersions;
                if (!fullUpdate && project.getLatestVersion().isPresent())
                {
                    candidateVersions = repoVersions.stream().filter(v -> v.compareTo(project.getLatestVersion().get()) > 1).collect(Collectors.toList());
                }
                else
                {
                    candidateVersions  = repoVersions;
                }
                String versionInfoMessage = String.format("%s found [%s] versions to update: %s", projectArtifacts, candidateVersions.size(), candidateVersions);
                getLOGGER().info(versionInfoMessage);
                response.addMessage(versionInfoMessage);
                candidateVersions.forEach(v -> response.addMessage(queueWorkToRefreshProjectVersion(project, v.toVersionIdString(), fullUpdate, transitive, parentEventId)));
                LOGGER.info("Finished processing all versions {}{}", project.getGroupId(), project.getArtifactId());
            }
        }
        else
        {
            String badCoordinatesMessage = String.format("invalid coordinates : [%s-%s] ", project.getGroupId(), project.getArtifactId());
            getLOGGER().error(badCoordinatesMessage);
            response.logError(badCoordinatesMessage);
        }
        return response;

    }

    private List<File> findArtifactFiles(ArtifactType type, ProjectData project, String versionId, boolean includeUnchangedFiles)
    {
        List<File> filesFromRepo = this.repositoryServices.findFiles(type, project.getGroupId(), project.getArtifactId(), versionId);
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
    public MetadataEventResponse refreshProjectsWithMissingVersions(boolean fullUpdate, boolean transitive,String parentEvent)
    {
        return executeWithTrace(REFRESH_PROJECTS_WITH_MISSING_VERSIONS,ALL, ALL, MISSING, () ->
        {
            MetadataEventResponse response = new MetadataEventResponse();
            String parentEventId = buildParentEventId(ALL, ALL,MISSING,parentEvent);
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
                                        response.addMessage(queueWorkToRefreshProjectVersion(getProject(vm.groupId, vm.artifactId), missingVersion, fullUpdate,transitive,parentEventId));
                                        String message = String.format("queued fixing missing version: %s-%s-%s ", vm.groupId, vm.artifactId, missingVersion);
                                        LOGGER.info(message);
                                        response.addMessage(message);
                                    }
                                    catch (Exception e)
                                    {
                                        String message = String.format("queue failed for missing version: %s-%s-%s ", vm.groupId, vm.artifactId, missingVersion);
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
}
