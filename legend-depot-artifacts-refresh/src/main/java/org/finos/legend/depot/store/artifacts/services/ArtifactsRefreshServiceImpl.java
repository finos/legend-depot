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
import org.finos.legend.depot.artifacts.repository.api.ArtifactRepository;
import org.finos.legend.depot.artifacts.repository.api.ArtifactRepositoryException;
import org.finos.legend.depot.artifacts.repository.domain.ArtifactDependency;
import org.finos.legend.depot.artifacts.repository.domain.ArtifactType;
import org.finos.legend.depot.artifacts.repository.services.RepositoryServices;
import org.finos.legend.depot.domain.api.MetadataEventResponse;
import org.finos.legend.depot.domain.project.IncludeProjectPropertiesConfiguration;
import org.finos.legend.depot.domain.project.ProjectData;
import org.finos.legend.depot.domain.project.ProjectProperty;
import org.finos.legend.depot.domain.project.ProjectVersion;
import org.finos.legend.depot.domain.project.ProjectVersionDependency;
import org.finos.legend.depot.services.api.projects.ManageProjectsService;
import org.finos.legend.depot.store.artifacts.api.ArtifactsRefreshService;
import org.finos.legend.depot.store.artifacts.api.ProjectArtifactsHandler;
import org.finos.legend.depot.store.artifacts.api.status.ManageRefreshStatusService;
import org.finos.legend.depot.store.artifacts.domain.ArtifactDetail;
import org.finos.legend.depot.store.artifacts.domain.status.RefreshStatus;
import org.finos.legend.depot.artifacts.repository.domain.VersionMismatch;
import org.finos.legend.depot.store.artifacts.store.mongo.api.UpdateArtifacts;
import org.finos.legend.depot.store.metrics.QueryMetricsContainer;
import org.finos.legend.depot.tracing.services.TracerFactory;
import org.finos.legend.sdlc.domain.model.version.VersionId;
import org.slf4j.Logger;

import javax.inject.Inject;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
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

    private final ManageProjectsService projects;
    private final ManageRefreshStatusService store;
    private final ArtifactRepository repository;
    private final RepositoryServices repositoryServices;
    private final UpdateArtifacts artifacts;
    private final List<String> projectProperties;


    @Inject
    public ArtifactsRefreshServiceImpl(ManageProjectsService projects, ManageRefreshStatusService store, ArtifactRepository repository, RepositoryServices repositoryServices, UpdateArtifacts artifacts, IncludeProjectPropertiesConfiguration includePropertyConfig)
    {
        this.projects = projects;
        this.store = store;
        this.repository = repository;
        this.repositoryServices = repositoryServices;
        this.artifacts = artifacts;
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

    private List<ArtifactType> getSupportedArtifactTypes()
    {
        return Arrays.asList(ArtifactType.ENTITIES, ArtifactType.VERSIONED_ENTITIES,ArtifactType.FILE_GENERATIONS);
    }

    private MetadataEventResponse handleRefresh(String groupId, String artifactId, String version, Supplier<MetadataEventResponse> functionToExecute)
    {
        MetadataEventResponse response = new MetadataEventResponse();
        getLOGGER().info("Starting [{}{}{}] refresh", groupId, artifactId, version);
        RefreshStatus storeStatus = store.get(groupId, artifactId, version);
        if (storeStatus.isRunning())
        {
            getLOGGER().info("Other instance is running, skipping [{}-{}-{}] refresh",  groupId, artifactId, version);
            return response;
        }
        try
        {
            storeStatus = store.createOrUpdate(storeStatus.startRunning());
            response = functionToExecute.get();
            storeStatus = store.createOrUpdate(storeStatus.stopRunning(response));
            getLOGGER().info("Finished [{}{}{}] refresh", groupId, artifactId, version);
        }
        catch (Exception e)
        {
            String message = String.format("Error refreshing [%s-%s-%s] : %s",groupId,artifactId,version,e.getMessage());
            storeStatus.getResponse().addError(message);
            LOGGER.error(message);
        }
        finally
        {
            store.createOrUpdate(storeStatus.stopRunning(response));
        }
        return response;
    }

    private void decorateSpanWithVersionInfo(String groupId,String artifactId, String versionId)
    {
        Map<String, String> tags = new HashMap<>();  
        tags.put("groupId", groupId);
        tags.put("artifactId", artifactId);
        tags.put("versionId", versionId);
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
            throw new IllegalArgumentException("can't find project for " + groupId + "-" + artifactId);
        }
        return found.get();
    }


    @Override
    public MetadataEventResponse  refreshAllVersionsForAllProjects(boolean fullUpdate)
    {
        return executeWithTrace("refreshAllVersionsForAllProjects",ALL, ALL, ALL, () ->
                {
                    MetadataEventResponse result = new MetadataEventResponse();
                    ParallelIterate.forEach(getProjects(), p -> result.combine(refreshAllVersionsForProject(p, fullUpdate)));
                    return result;
                }
        );
    }

    @Override
    public MetadataEventResponse refreshMasterSnapshotForAllProjects(boolean fullUpdate)
    {
        return executeWithTrace("refreshMasterSnapshotForAllProjects", ALL, ALL, MASTER_SNAPSHOT, () ->
                {
                    MetadataEventResponse result = new MetadataEventResponse();
                    ParallelIterate.forEach(getProjects(), project -> result.combine(refreshVersionForProject(project,MASTER_SNAPSHOT,fullUpdate)));
                    return result;
                }
        );
    }

    @Override
    public MetadataEventResponse refreshVersionForProject(String groupId, String artifactId, String versionId, boolean fullUpdate)
    {
        MetadataEventResponse response = new MetadataEventResponse();
        response.combine(validateInput(groupId, artifactId, versionId, response));
        if (response.hasErrors())
        {
            return response;
        }
        return response.combine(refreshVersionForProject(getProject(groupId, artifactId), versionId, fullUpdate));
    }

    private MetadataEventResponse validateInput(String groupId, String artifactId, String versionId, MetadataEventResponse response)
    {
        if (!this.projects.find(groupId, artifactId).isPresent())
        {
            response.addError(String.format("Project does not exists for %s-%s", groupId, artifactId));
        }
        else
        {
            if (!MASTER_SNAPSHOT.equals(versionId))
            {
                try
                {
                    List<VersionId> versionsInRepo = this.repository.findVersions(groupId, artifactId);
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

    @Override
    public MetadataEventResponse refreshAllVersionsForProject(String groupId, String artifactId, boolean fullUpdate)
    {
            ProjectData projectData = getProject(groupId,artifactId);
            MetadataEventResponse response = refreshAllVersionsForProject(projectData,fullUpdate);
            return response.combine(refreshMasterSnapshotForProject(groupId,artifactId, fullUpdate));
    }

    private MetadataEventResponse refreshVersionForProject(ProjectData project, String versionId, boolean fullUpdate)
    {
        return executeWithTrace("refreshProjectVersionArtifacts", project.getGroupId(), project.getArtifactId(), versionId, () ->
        {
            MetadataEventResponse response = new MetadataEventResponse();
            response.addMessage(String.format("Processing [%s-%s-%s] fullUpdate? [%s]", project.getGroupId(), project.getArtifactId(), versionId, fullUpdate));
            getSupportedArtifactTypes().forEach(artifactType -> response.combine(executeVersionRefresh(artifactType, project, versionId, fullUpdate)));
            if (!response.hasErrors())
            {
                QueryMetricsContainer.record(project.getGroupId(), project.getArtifactId(), versionId);
                Optional<ProjectData> latestProjectData = projects.find(project.getGroupId(), project.getArtifactId());
                latestProjectData.ifPresent(p ->
                {
                    List<ProjectVersionDependency> newDependencies = calculateDependencies(project.getGroupId(), project.getArtifactId(), versionId);
                    response.combine(refreshDependencies(newDependencies,fullUpdate));
                    LOGGER.info("Finished updating {} dependencies for [{}{}{}]", project.getDependencies(versionId).size(), project.getGroupId(), project.getArtifactId(), versionId);
                    if (!response.hasErrors())
                    {
                        if (!versionId.equals(MASTER_SNAPSHOT))
                        {
                            p.addVersion(versionId);
                        }
                        p.addProperties(refreshProjectProperties(p, versionId));
                        p.getDependencies(versionId).forEach(p::removeDependency);
                        p.addDependencies(newDependencies);
                        projects.createOrUpdate(p);
                    }
                });
            }
            return response;
        });
    }

    private List<ProjectVersionDependency> calculateDependencies(String groupId, String artifactId, String versionId)
    {
        List<ProjectVersionDependency> versionDependencies = new ArrayList<>();
        Set<ArtifactDependency> dependencies = repository.findDependencies(groupId, artifactId, versionId);
        LOGGER.info("Found [{}] dependencies for [{}{}{}]", dependencies.size(), groupId, artifactId, versionId);
        dependencies.forEach(dependency ->  versionDependencies.add(new ProjectVersionDependency(groupId, artifactId, versionId,
                new ProjectVersion(dependency.getGroupId(), dependency.getArtifactId(), dependency.getVersion()))));
        return versionDependencies;
    }

    private MetadataEventResponse executeWithTrace(String label, String groupId, String artifactId, String version, Supplier<MetadataEventResponse> functionToExecute)
    {
        return TracerFactory.get().executeWithTrace(label, () ->
        {
            decorateSpanWithVersionInfo(groupId, artifactId, version);
            return handleRefresh(groupId, artifactId, version, functionToExecute);
        });
    }

    private List<ProjectProperty> refreshProjectProperties(ProjectData project, String versionId)
    {
        Model model = repository.getPOM(project.getGroupId(), project.getArtifactId(), versionId);
        Enumeration<?> propertyNames = model.getProperties().keys();
        List<ProjectProperty> projectPropertyList = new ArrayList<>();
        while (propertyNames.hasMoreElements())
        {
            String propertyName = propertyNames.nextElement().toString();
            if (projectProperties.contains(propertyName) || projectProperties.stream().anyMatch(propertyName::matches))
            {
                projectPropertyList.add(new ProjectProperty(propertyName, model.getProperties().getProperty(propertyName), versionId));
            }
        }
        return projectPropertyList;
    }


    private MetadataEventResponse refreshDependencies(List<ProjectVersionDependency> dependencies,boolean fullUpdate)
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
                response.combine(refreshVersionForProject(dependentProject,dependency.getDependency().getVersionId(), fullUpdate));
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
        ProjectArtifactsHandler refreshHandler = ArtifactResolverFactory.getArtifactHandler(artifactType);
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

    private MetadataEventResponse refreshAllVersionsForProject(ProjectData project, boolean fullUpdate)
    {
        return handleRefresh(project.getGroupId(), project.getArtifactId(), ALL,
                () ->
                {
                    MetadataEventResponse response = new MetadataEventResponse();
                    decorateSpanWithVersionInfo(project.getGroupId(),project.getArtifactId(),ALL);
                    String projectArtifacts = String.format("%s: [%s-%s]", project.getProjectId(), project.getGroupId(), project.getArtifactId());
                    if (repository.areValidCoordinates(project.getGroupId(), project.getArtifactId()))
                    {
                        getLOGGER().info("Fetching {} versions ", projectArtifacts);
                        List<VersionId> repoVersions;
                        try
                        {
                            repoVersions = repository.findVersions(project.getGroupId(), project.getArtifactId());
                        }
                        catch (ArtifactRepositoryException e)
                        {
                            response.addError(e.getMessage());
                            return response;
                        }
                        List<VersionId> versionsToUpdate = new ArrayList<>();
                        if (repoVersions != null)
                        {
                            versionsToUpdate.addAll(repoVersions);
                        }
                        if (!versionsToUpdate.isEmpty())
                        {
                            if (!fullUpdate && project.getVersions() != null && !project.getVersions().isEmpty())
                            {
                                versionsToUpdate.removeAll(project.getVersionIds());
                            }
                            getLOGGER().info(String.format("%s found [%s] new versions: [%s]", projectArtifacts, versionsToUpdate.size(), versionsToUpdate));
                            versionsToUpdate.forEach(v -> response.combine(refreshVersionForProject(project, v.toVersionIdString(), fullUpdate)));
                            response.addMessage(String.format("%s: [%s-%s] found [%s] new versions", project.getProjectId(),
                                    project.getGroupId(),
                                    project.getArtifactId(),
                                    versionsToUpdate.size()));
                        }
                    }
                    else
                    {
                        getLOGGER().warn("bad project settings : {} [{}:{}] ", project.getProjectId(), project.getGroupId(), project.getArtifactId());
                        response.logError(project.getProjectId() + " invalid project settings " + projectArtifacts + " versions not refreshed");
                    }
                    return response;
                });
    }

    private List<File> findArtifactFiles(ArtifactType type, ProjectData project, String versionId, boolean includeUnchangedFiles)
    {
        List<File> filesFromRepo = repository.findFiles(type, project.getGroupId(), project.getArtifactId(), versionId);
        return filesFromRepo.stream().filter(file -> includeUnchangedFiles || artifactFileHasChangedOrNotBeenProcessed(file)).collect(Collectors.toList());
    }

    private boolean artifactFileHasChangedOrNotBeenProcessed(File file)
    {
        String filePath = file.getPath();
        Optional<ArtifactDetail> artifactDetails = this.artifacts.find(filePath);
        try
        {
            String fileCheckSum = DigestUtils.sha256Hex(new FileInputStream(filePath));
            if (!artifactDetails.isPresent() || !MessageDigest.isEqual(fileCheckSum.getBytes(), artifactDetails.get().getCheckSum().getBytes()))
            {
                LOGGER.info("loading artifacts from updated file: {}", filePath);
                LOGGER.info("file check sum: {}", fileCheckSum);
                this.artifacts.createOrUpdate(new ArtifactDetail(filePath, fileCheckSum));
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
    public void delete(String groupId, String artifactId, String versionId)
    {
        getSupportedArtifactTypes().forEach(artifactType ->
        {
            ProjectArtifactsHandler versionsRefresh = ArtifactResolverFactory.getArtifactHandler(artifactType);
            if (versionsRefresh != null)
            {
                versionsRefresh.delete(groupId, artifactId, versionId);
            }
        });
        ProjectData project = getProject(groupId, artifactId);
        project.removeVersion(versionId);
        projects.createOrUpdate(project);
    }

    @Override
    public MetadataEventResponse retireOldProjectVersions(int versionsToKeep)
    {
        getLOGGER().info("Start purging versions for all projects");
        MetadataEventResponse response = new MetadataEventResponse();
        List<ProjectData> allProjects = getProjects();
        ParallelIterate.forEach(allProjects, project ->
        {
            MetadataEventResponse res = purgeProjectVersions(project, versionsToKeep);
            response.combine(res);
        });
        response.addMessage(String.format("Total %s projects ", allProjects.size()));
        getLOGGER().info("Finished purging versions for all projects");
        if (!response.getErrors().isEmpty())
        {
            String errors = String.join(",\n", response.getErrors());
            getLOGGER().error(errors);
        }
        return response;
    }

    private MetadataEventResponse purgeProjectVersions(ProjectData project, int versionsToKeep)
    {
        MetadataEventResponse response = new MetadataEventResponse();
        List<VersionId> versionIds = project.getVersionsOrdered();
        int numberOfVersions = versionIds.size();
        while (versionIds.size() > versionsToKeep)
        {
            VersionId versionId = versionIds.get(0);
            delete(project.getGroupId(), project.getArtifactId(), versionId.toVersionIdString());
            versionIds.remove(versionId);
            project.removeVersion(versionId.toVersionIdString());
        }
        projects.createOrUpdate(project);
        response.addMessage(String.format("%s purged %s version", project.getProjectId(), numberOfVersions - versionIds.size()));
        return response;
    }

    @Override
    public MetadataEventResponse retireLeastRecentlyUsedVersions(int numberOfDays)
    {
        return null;
    }

    @Override
    public boolean createIndexesIfAbsent()
    {
        return artifacts.createIndexesIfAbsent();
    }


    @Override
    public MetadataEventResponse refreshProjectsWithMissingVersions()
    {
        return executeWithTrace("refreshProjectsWithMissingVersions",ALL, ALL, MISSING, () ->
        {
            MetadataEventResponse response = new MetadataEventResponse();
            List<VersionMismatch> projectsWithMissingVersions = this.repositoryServices.findVersionsMismatches().stream().filter(r -> !r.versionsNotInCache.isEmpty()).collect(Collectors.toList());
            String countInfo = String.format("Starting fixing [%s] projects with missing versions",projectsWithMissingVersions.size());
            LOGGER.info(countInfo);
            response.addMessage(countInfo);
            AtomicInteger totalMissingVersions = new AtomicInteger();
            AtomicInteger totalMissingVersionsFixed = new AtomicInteger();
            projectsWithMissingVersions.forEach(vm ->
                    {
                        vm.versionsNotInCache.forEach(missingVersion ->
                                {
                                    try
                                    {
                                        response.combine(refreshVersionForProject(vm.groupId, vm.artifactId, missingVersion, false));
                                        totalMissingVersionsFixed.getAndIncrement();
                                        String message = String.format("fixed missing version: %s-%s-%s ", vm.groupId, vm.artifactId, missingVersion);
                                        LOGGER.info(message);
                                        response.addMessage(message);
                                    }
                                    catch (Exception e)
                                    {
                                        String message = String.format("fix failed for missing version: %s-%s-%s ", vm.groupId, vm.artifactId, missingVersion);
                                        LOGGER.error(message);
                                        response.addError(message);
                                    }
                                    totalMissingVersions.getAndIncrement();
                                }
                        );
                    }
            );
            LOGGER.info("Fixed [{}]/[{}] missing versions", totalMissingVersionsFixed, totalMissingVersions);
            return response;
        });
    }
}
