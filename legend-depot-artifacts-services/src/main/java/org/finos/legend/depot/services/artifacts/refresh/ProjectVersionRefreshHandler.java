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

package org.finos.legend.depot.services.artifacts.refresh;

import com.google.inject.name.Named;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.maven.model.Model;
import org.eclipse.collections.impl.parallel.ParallelIterate;
import org.finos.legend.depot.domain.CoordinateValidator;
import org.finos.legend.depot.domain.api.MetadataEventResponse;
import org.finos.legend.depot.domain.notifications.MetadataNotification;
import org.finos.legend.depot.domain.project.ProjectVersion;
import org.finos.legend.depot.domain.project.ProjectVersionData;
import org.finos.legend.depot.domain.project.Property;
import org.finos.legend.depot.domain.version.VersionValidator;
import org.finos.legend.depot.services.api.dependencies.ManageDependenciesService;
import org.finos.legend.depot.services.api.projects.ManageProjectsService;
import org.finos.legend.depot.services.artifacts.handlers.ManifestLoader;
import org.finos.legend.depot.store.api.admin.artifacts.ArtifactsFilesStore;
import org.finos.legend.depot.store.model.admin.artifacts.ArtifactFile;
import org.finos.legend.depot.services.api.artifacts.configuration.IncludeProjectPropertiesConfiguration;
import org.finos.legend.depot.services.api.artifacts.repository.ArtifactRepository;
import org.finos.legend.depot.services.api.artifacts.repository.ArtifactRepositoryException;
import org.finos.legend.depot.domain.artifacts.repository.ArtifactType;
import org.finos.legend.depot.store.model.projects.StoreProjectData;
import org.finos.legend.depot.store.model.projects.StoreProjectVersionData;
import org.finos.legend.depot.store.notifications.api.NotificationEventHandler;
import org.finos.legend.depot.store.notifications.queue.api.Queue;
import org.finos.legend.depot.services.api.artifacts.handlers.ProjectArtifactHandlerFactory;
import org.finos.legend.depot.services.api.artifacts.handlers.ProjectArtifactsHandler;
import org.finos.legend.depot.tracing.services.TracerFactory;
import org.finos.legend.depot.tracing.services.prometheus.PrometheusMetricsFactory;
import org.slf4j.Logger;

import javax.inject.Inject;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.jar.Manifest;
import java.util.stream.Collectors;


public final class ProjectVersionRefreshHandler implements NotificationEventHandler
{
    private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(ProjectVersionRefreshHandler.class);
    public static final String VERSION_REFRESH_COUNTER = "versionRefresh";
    public static final String VERSION_REFRESH_DURATION = "versionRefresh_duration";
    public static final String VERSION_REFRESH_DURATION_HELP = "version refresh duration";
    public static final String TOTAL_NUMBER_OF_VERSIONS_REFRESH = "total number of versions refresh";


    private static final String PROCESS_EVENT = "processNotification";
    private static final String SHA_256 = "SHA-256";
    private static final String GROUP_ID = "groupId";
    private static final String ARTIFACT_ID = "artifactId";
    private static final String VERSION_ID = "versionId";

    private final ManageProjectsService projects;
    private final ArtifactsFilesStore artifacts;
    private final ArtifactRepository repositoryServices;
    private final List<String> projectPropertiesInScope;
    private final List<String> manifestPropertiesInScope;
    private final Queue workQueue;
    private final ManageDependenciesService manageDependenciesService;
    private final int maximumSnapshotsAllowed;


    @Inject
    public ProjectVersionRefreshHandler(ManageProjectsService projects, ArtifactRepository repositoryServices, Queue workQueue, ArtifactsFilesStore artifacts, IncludeProjectPropertiesConfiguration includePropertyConfig, ManageDependenciesService manageDependenciesService, @Named("maximumSnapshotsAllowed") int maximumSnapshotsAllowed)
    {
        this.projects = projects;
        this.workQueue = workQueue;
        this.artifacts = artifacts;
        this.repositoryServices = repositoryServices;
        this.projectPropertiesInScope = includePropertyConfig != null ? includePropertyConfig.getProperties() : Collections.EMPTY_LIST;
        this.manifestPropertiesInScope = includePropertyConfig != null ? includePropertyConfig.getManifestProperties() : Collections.EMPTY_LIST;
        this.manageDependenciesService = manageDependenciesService;
        this.maximumSnapshotsAllowed = maximumSnapshotsAllowed;

        try
        {
            MessageDigest.getInstance(SHA_256);
        }
        catch (NoSuchAlgorithmException e)
        {
            LOGGER.info(e.getLocalizedMessage());
        }
    }

    @Override
    public MetadataEventResponse handleEvent(MetadataNotification versionEvent)
    {
        MetadataEventResponse response = new MetadataEventResponse();
        Optional<StoreProjectData> existingProject = projects.findCoordinates(versionEvent.getGroupId(), versionEvent.getArtifactId());
        if (!existingProject.isPresent())
        {
            StoreProjectData newProject = new StoreProjectData(versionEvent.getProjectId(), versionEvent.getGroupId(), versionEvent.getArtifactId());
            projects.createOrUpdate(newProject);
            String newProjectMessage = String.format("New project %s created with coordinates %s-%s", newProject.getProjectId(), newProject.getGroupId(), newProject.getArtifactId());
            response.addMessage(newProjectMessage);
            LOGGER.info(newProjectMessage);
        }
        return response.combine(executeWithTrace(PROCESS_EVENT, versionEvent, () -> doRefresh(versionEvent)));
    }

    @Override
    public List<String> validateEvent(MetadataNotification event)
    {
        List<String> errors = new ArrayList<>();

        if (!CoordinateValidator.isValidGroupId(event.getGroupId()))
        {
            errors.add(String.format("invalid groupId [%s]",event.getGroupId()));
        }
        if (!CoordinateValidator.isValidArtifactId(event.getArtifactId()))
        {
            errors.add(String.format("invalid artifactId [%s]",event.getArtifactId()));
        }
        if (!VersionValidator.isValid(event.getVersionId()))
        {
            errors.add(String.format("invalid versionId [%s]",event.getVersionId()));
        }

        Optional<StoreProjectData> projectData = projects.findCoordinates(event.getGroupId(),event.getArtifactId());
        if (projectData.isPresent() && projectData.get().getProjectId() != null && !projectData.get().getProjectId().equals(event.getProjectId()))
        {
            errors.add(String.format("Invalid projectId [%s]. Existing project [%s] has same [%s-%s] coordinates",event.getProjectId(),projectData.get().getProjectId(),event.getGroupId(),event.getArtifactId()));
        }
        if (VersionValidator.isSnapshotVersion(event.getVersionId()))
        {
            List<String> snapshotVersions = projects.findSnapshotVersions(event.getGroupId(), event.getArtifactId()).stream().filter(v -> !v.isEvicted()).map(versionData -> versionData.getVersionId()).collect(Collectors.toList());
            if (!snapshotVersions.contains(event.getVersionId()) && snapshotVersions.size() >= maximumSnapshotsAllowed)
            {
                errors.add(String.format("Number of snapshot versions stored for project %s-%s, has reached the limit [%s]", event.getGroupId(), event.getArtifactId(), maximumSnapshotsAllowed));
            }
        }
        return errors;
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

    private MetadataEventResponse validateGAV(String groupId, String artifactId, String versionId)
    {
        MetadataEventResponse response = new MetadataEventResponse();
        if (!projects.findCoordinates(groupId, artifactId).isPresent())
        {
            String missingProject = String.format("No Project with coordinates %s-%s found", groupId, artifactId);
            response.addError(missingProject);
            LOGGER.error(missingProject);
        }
        else
        {
            try
            {
                if (!this.repositoryServices.findVersion(groupId, artifactId, versionId).isPresent())
                {
                    String missingVersion = String.format("Version %s does not exist for %s-%s in repository", versionId, groupId, artifactId);
                    response.addError(missingVersion);
                    LOGGER.error(missingVersion);
                    return response;
                }
            }
            catch (ArtifactRepositoryException e)
            {
                response.addError(e.getMessage());
                LOGGER.error("error validating gav",e);
            }
        }
        return response;
    }

    public MetadataEventResponse executeWithTrace(String label, MetadataNotification notification, Supplier<MetadataEventResponse> functionToExecute)
    {
        return TracerFactory.get().executeWithTrace(label, () ->
                functionToExecute.get(),decorateSpanWithVersionInfo(notification.getGroupId(), notification.getArtifactId(), notification.getVersionId()));
    }

    private  Map<String, String> decorateSpanWithVersionInfo(String groupId,String artifactId, String versionId)
    {
        Map<String, String> tags = new HashMap<>();
        tags.put(GROUP_ID, groupId);
        tags.put(ARTIFACT_ID, artifactId);
        tags.put(VERSION_ID, versionId);
        return tags;
    }

    MetadataEventResponse doRefresh(MetadataNotification event)
    {
            long refreshStartTime = System.currentTimeMillis();
            MetadataEventResponse response = new MetadataEventResponse();
            String message = String.format("Executing: [%s-%s-%s], eventId: [%s], parentEventId: [%s], full/transitive: [%s/%s], attempts: [%s]", event.getGroupId(), event.getArtifactId(),
                    event.getVersionId(), event.getEventId(), event.getParentEventId(), event.isFullUpdate(), event.isTransitive(), event.getAttempt());
            response.addMessage(message);
            LOGGER.info(message);
            if (response.combine(validateGAV(event.getGroupId(), event.getArtifactId(), event.getVersionId())).hasErrors())
            {
                return response;
            }
            StoreProjectData project = getProject(event.getGroupId(), event.getArtifactId());
            try
            {
                List<ProjectVersion> newDependencies = this.manageDependenciesService.calculateDependencies(event.getGroupId(), event.getArtifactId(), event.getVersionId());
                this.manageDependenciesService.validateDependencies(newDependencies, event.getVersionId()).forEach(error -> response.addError(error));
                if (!response.hasErrors())
                {
                    LOGGER.info("Processing artifacts for [{}-{}-{}]", event.getGroupId(), event.getArtifactId(), event.getVersionId());

                    ParallelIterate.forEach(ProjectArtifactHandlerFactory.getSupportedTypes(), artifactType -> response.combine(handleArtifacts(artifactType, project, event.getVersionId(), event.isFullUpdate())));
                    LOGGER.info("Finished processing artifacts for [{}-{}-{}]", event.getGroupId(), event.getArtifactId(), event.getVersionId());
                    if (!response.hasErrors())
                    {

                        updateProjectVersionData(project, event.getVersionId(), newDependencies);
                        //we let the version load but will check dependencies exists and report missing dependencies as errors
                        if (!event.isTransitive())
                        {
                            newDependencies.stream().forEach(dep ->
                            {
                                if (!projects.find(dep.getGroupId(), dep.getArtifactId(), dep.getVersionId()).isPresent())
                                {
                                    String missingDepError = String.format("Dependency %s-%s-%s not found in store", dep.getGroupId(), dep.getArtifactId(), dep.getVersionId());
                                    response.addError(missingDepError);
                                    LOGGER.error(missingDepError);
                                }
                            });
                        }
                        else
                        {
                            LOGGER.info("Started updating {} dependencies for [{}{}{}]", newDependencies.size(), event.getGroupId(), event.getArtifactId(), event.getVersionId());
                            response.combine(handleDependencies(project, event.getVersionId(), newDependencies, event.isFullUpdate(), event.isTransitive(), event.getParentEventId()));
                            LOGGER.info("Finished updating {} dependencies for [{}{}{}]", newDependencies.size(), event.getGroupId(), event.getArtifactId(), event.getVersionId());
                        }
                    }
                }
            }
            catch (Exception e)
            {
                String errorMessage = String.format("Exception executing: [%s-%s-%s], eventId: [%s], parentEventId: [%s], full/transitive: [%s/%s], attempts: [%s], exception[%s]", event.getGroupId(), event.getArtifactId(),
                        event.getVersionId(), event.getEventId(), event.getParentEventId(), event.isFullUpdate(), event.isTransitive(), event.getAttempt(), e.getMessage());
                response.addError(errorMessage);
                LOGGER.error(errorMessage);
            }
            long refreshEndTime = System.currentTimeMillis();
            PrometheusMetricsFactory.getInstance().observeHistogram(VERSION_REFRESH_DURATION, refreshStartTime, refreshEndTime);
            return response;
    }

    private void updateProjectVersionData(StoreProjectData project, String versionId, List<ProjectVersion> newDependencies)
    {
        Optional<StoreProjectVersionData> projectVersionData = projects.find(project.getGroupId(), project.getArtifactId(), versionId);
        StoreProjectVersionData storeProjectVersionData = projectVersionData.isPresent() ? projectVersionData.get() : new StoreProjectVersionData(project.getGroupId(), project.getArtifactId(), versionId);
        ProjectVersionData versionData = storeProjectVersionData.getVersionData();
        versionData.setDependencies(newDependencies);
        this.manageDependenciesService.setProjectDataTransitiveDependencies(storeProjectVersionData);

        if (this.projectPropertiesInScope != null && !this.projectPropertiesInScope.isEmpty())
        {
            List<Property> properties = calculateProjectProperties(project.getGroupId(), project.getArtifactId(), versionId);
            versionData.setProperties(properties);
        }

        if (this.manifestPropertiesInScope != null && !this.manifestPropertiesInScope.isEmpty())
        {
            Map<String, String> manifestProperties = getPropertiesFromManifest(project.getGroupId(), project.getArtifactId(),versionId);
            versionData.setManifestProperties(manifestProperties);
        }

        storeProjectVersionData.setVersionData(versionData);
        storeProjectVersionData.setEvicted(false);
        storeProjectVersionData.getVersionData().setExcluded(false);
        storeProjectVersionData.getVersionData().setExclusionReason(null);
        projects.createOrUpdate(storeProjectVersionData);
        LOGGER.info("Finished updating project data [{}-{}-{}]", project.getGroupId(), project.getArtifactId(), versionId);
    }

    private String queueWorkToRefreshProjectVersion(StoreProjectData projectData, String versionId, boolean fullUpdate, boolean transitive, String parentEvent)
    {
        return String.format("queued: [%s-%s-%s], parentEventId :[%s], full/transitive :[%s/%s],event id :[%s] ",
                projectData.getGroupId(),projectData.getArtifactId(),versionId,parentEvent,fullUpdate,transitive,this.workQueue.push(new MetadataNotification(projectData.getProjectId(),projectData.getGroupId(),projectData.getArtifactId(),versionId,fullUpdate,transitive,parentEvent)));
    }

    private List<Property> calculateProjectProperties(String groupId, String artifactId, String versionId)
    {
        List<Property> projectPropertyList = new ArrayList<>();
        Model model = this.repositoryServices.getPOM(groupId, artifactId, versionId);
        if (model != null)
        {
            Enumeration<?> propertyNames = model.getProperties().keys();

            while (propertyNames.hasMoreElements())
            {
                String propertyName = propertyNames.nextElement().toString();
                if (projectPropertiesInScope.contains(propertyName) || projectPropertiesInScope.stream().anyMatch(propertyName::matches))
                {
                    projectPropertyList.add(new Property(propertyName, model.getProperties().getProperty(propertyName)));
                }
            }
        }
        return projectPropertyList;
    }

    private Map<String, String> getPropertiesFromManifest(String groupId, String artifactId, String versionId)
    {
        Map<String, String> manifestProperties = new HashMap<>();
        File jarFile = this.repositoryServices.getJarFile(groupId, artifactId + "-" + ArtifactType.ENTITIES.getModuleName(), versionId);
        Manifest manifest = ManifestLoader.readManifest(jarFile);

        if (manifest != null)
        {
            manifest.getMainAttributes().entrySet().forEach(entry ->
            {
                String key = entry.getKey().toString();
                String value = entry.getValue().toString();
                if (manifestPropertiesInScope != null && (manifestPropertiesInScope.contains(key) || manifestPropertiesInScope.stream().anyMatch(key::matches)))
                {
                    manifestProperties.put(key, value);
                }
            });
        }

        return manifestProperties;
    }

    private MetadataEventResponse handleDependencies(StoreProjectData projectData, String versionId, List<ProjectVersion> dependencies, boolean fullUpdate, boolean transitive, String parentEventId)
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
                Optional<StoreProjectVersionData> projectVersion = projects.find(dependency.getGroupId(), dependency.getArtifactId(), dependency.getVersionId());
                if (!VersionValidator.isSnapshotVersion(dependency.getVersionId()) && projectVersion.isPresent() && !projectVersion.get().getVersionData().isExcluded())
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

    private MetadataEventResponse handleArtifacts(ArtifactType artifactType, StoreProjectData project, String versionId, boolean fullUpdate)
    {
        MetadataEventResponse response = new MetadataEventResponse();
        ProjectArtifactsHandler refreshHandler = ProjectArtifactHandlerFactory.getArtifactHandler(artifactType);
        if (refreshHandler != null)
        {
            boolean processUnchangedFiles = !VersionValidator.isSnapshotVersion(versionId) ? true : fullUpdate;
            List<File> files = findArtifactFiles(artifactType, project, versionId, processUnchangedFiles);
            if (files != null && !files.isEmpty())
            {
                response.addMessage(String.format("[%s] files found [%s] artifacts to process [%s-%s-%s], processUnChangedFiles: %s",files.size(),artifactType,project.getGroupId(),project.getArtifactId(),versionId,processUnchangedFiles));
                response.combine(refreshHandler.refreshProjectVersionArtifacts(project.getGroupId(),project.getArtifactId(), versionId, files));
            }
            else
            {
                response.addMessage(String.format("No %s artifacts to process [%s-%s-%s], processUnChangedFiles: %s",artifactType,project.getGroupId(),project.getArtifactId(),versionId,processUnchangedFiles));
            }
        }
        else
        {
            response.addError(String.format("handler not found for artifact type %s, please check your configuration",artifactType));
        }
        return response;
    }

    private List<File> findArtifactFiles(ArtifactType type, StoreProjectData projectData, String versionId, boolean includeUnchangedFiles)
    {
        List<File> filesFromRepo = this.repositoryServices.findFiles(type, projectData.getGroupId(), projectData.getArtifactId(), versionId);
        return filesFromRepo.stream().filter(file -> includeUnchangedFiles || artifactFileChangedOrNotProcessed(file)).collect(Collectors.toList());
    }

    private boolean artifactFileChangedOrNotProcessed(File file)
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
}
