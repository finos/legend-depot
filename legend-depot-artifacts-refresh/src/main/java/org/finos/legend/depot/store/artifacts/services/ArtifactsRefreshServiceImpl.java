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
import org.finos.legend.depot.artifacts.repository.domain.ArtifactDependency;
import org.finos.legend.depot.artifacts.repository.domain.ArtifactType;
import org.finos.legend.depot.domain.api.MetadataEventResponse;
import org.finos.legend.depot.domain.entity.VersionRevision;
import org.finos.legend.depot.domain.project.IncludeProjectPropertiesConfiguration;
import org.finos.legend.depot.domain.project.ProjectData;
import org.finos.legend.depot.domain.project.ProjectProperty;
import org.finos.legend.depot.domain.project.ProjectVersion;
import org.finos.legend.depot.domain.project.ProjectVersionDependency;
import org.finos.legend.depot.services.api.projects.ManageProjectsService;
import org.finos.legend.depot.store.artifacts.api.ArtifactsRefreshService;
import org.finos.legend.depot.store.artifacts.api.ProjectVersionArtifactsHandler;
import org.finos.legend.depot.store.artifacts.api.status.ManageRefreshStatusService;
import org.finos.legend.depot.store.artifacts.domain.ArtifactDetail;
import org.finos.legend.depot.store.artifacts.domain.status.RefreshStatus;
import org.finos.legend.depot.store.artifacts.domain.status.VersionMismatch;
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
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.finos.legend.depot.domain.version.VersionValidator.MASTER_SNAPSHOT;

public class ArtifactsRefreshServiceImpl implements ArtifactsRefreshService
{

    private static final VersionRevision VERSIONS = VersionRevision.VERSIONS;
    private static final VersionRevision REVISIONS = VersionRevision.REVISIONS;
    private static final String ALL = "all";
    private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(ArtifactsRefreshServiceImpl.class);

    private final ManageProjectsService projects;
    private final ManageRefreshStatusService store;
    private final ArtifactRepository repository;
    private final UpdateArtifacts artifacts;
    private final List<String> projectProperties;


    @Inject
    public ArtifactsRefreshServiceImpl(ManageProjectsService projects, ManageRefreshStatusService store, ArtifactRepository repository, UpdateArtifacts artifacts, IncludeProjectPropertiesConfiguration includePropertyConfig)
    {
        this.projects = projects;
        this.store = store;
        this.repository = repository;
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
        return Arrays.asList(ArtifactType.ENTITIES, ArtifactType.FILE_GENERATIONS);
    }

    private MetadataEventResponse handleRefresh(VersionRevision type, String groupId, String artifactId, String version, Supplier<MetadataEventResponse> functionToExecute)
    {

        MetadataEventResponse response = new MetadataEventResponse();
        getLOGGER().info("Starting {} [{}:{}] {} refresh", type, groupId, artifactId, version);
        RefreshStatus storeStatus = store.get(type, groupId, artifactId, version);
        if (storeStatus.isRunning())
        {
            getLOGGER().info("Other instance is running, skipping {} {}:{} {} refresh", type, groupId, artifactId, version);
            return response;
        }
        try
        {
            store.createOrUpdate(storeStatus.withRunning(true).withResponse(null));
            response = functionToExecute.get();
            store.createOrUpdate(storeStatus.withRunning(false).withResponse(response));
            getLOGGER().info("Finished {} [{}:{}] {} refresh", type, groupId, artifactId, version);
            return response;
        }
        finally
        {
            store.createOrUpdate(storeStatus.withRunning(false).withResponse(response));
        }

    }


    private void decorateSpanWithProjectInfo(ProjectData projectConfiguration)
    {
        decorateSpanWithProjectInfo(projectConfiguration.getProjectId(), projectConfiguration.getGroupId(),
                projectConfiguration.getArtifactId());
    }

    private void decorateSpanWithProjectInfo(String projectId, String groupId, String artifact)
    {
        Map<String, String> tags = new HashMap<>();
        tags.put("projectId", projectId);
        tags.put("groupId", groupId);
        tags.put("artifactId", artifact);
        TracerFactory.get().decorateSpan(tags);
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
            throw new IllegalArgumentException("can't find project for  " + groupId + " " + artifactId);
        }
        return found.get();
    }


    @Override
    public MetadataEventResponse refreshAllProjectsVersionsArtifacts(boolean fullUpdate)
    {
        return handleRefresh(VERSIONS, ALL, ALL, ALL, () ->
                {
                    MetadataEventResponse result = new MetadataEventResponse();
                    ParallelIterate.forEach(getProjects(), p -> result.combine(refreshAllProjectVersions(p, fullUpdate)));
                    return result;
                }
        );
    }

    @Override
    public MetadataEventResponse refreshAllProjectRevisionsArtifacts()
    {
        return handleRefresh(REVISIONS, ALL, ALL, MASTER_SNAPSHOT, () ->
                {
                    MetadataEventResponse result = new MetadataEventResponse();
                    ParallelIterate.forEach(getProjects(), p -> result.combine(refreshProjectRevisionArtifacts(p)));
                    return result;
                }
        );
    }


    @Override
    public MetadataEventResponse refreshProjectVersionsArtifacts(String groupId, String artifactId, boolean fullUpdate)
    {
        return refreshAllProjectVersions(getProject(groupId, artifactId), fullUpdate);
    }


    @Override
    public MetadataEventResponse refreshProjectRevisionArtifacts(String groupId, String artifactId)
    {
        return refreshProjectRevisionArtifacts(getProject(groupId, artifactId));
    }

    @Override
    public MetadataEventResponse refreshProjectVersionArtifacts(String groupId, String artifactId, String versionId, boolean fullUpdate)
    {
        checkVersionExists(groupId,artifactId,versionId);
        return refreshProjectVersionArtifacts(getProject(groupId, artifactId), versionId, fullUpdate);
    }

    private void checkVersionExists(String groupId, String artifactId, String versionId)
    {
       if (!MASTER_SNAPSHOT.equals(versionId) && !this.repository.findVersions(groupId,artifactId).contains(VersionId.parseVersionId(versionId)))
       {
           throw new IllegalArgumentException(String.format("Version %s does not exists for %s-%s",versionId,groupId,artifactId));
       }
    }

    @Override
    public MetadataEventResponse refreshAllProjectArtifacts(String groupId, String artifactId)
    {
        return executeWithTrace("refreshAllProjectArtifacts", VERSIONS, groupId, artifactId, ALL, () ->
        {
            MetadataEventResponse response = refreshProjectVersionsArtifacts(groupId,artifactId,true);
            response.combine(refreshProjectRevisionArtifacts(groupId,artifactId));
            return  response;
        });
    }

    private MetadataEventResponse refreshProjectVersionArtifacts(ProjectData project, String versionId, boolean fullUpdate)
    {
        return executeWithTrace("refreshProjectVersionArtifacts", VERSIONS, project.getGroupId(), project.getArtifactId(), versionId, () ->
        {
            MetadataEventResponse response = new MetadataEventResponse();
            getSupportedArtifactTypes().forEach(artifactType -> response.combine(executeVersionRefresh(artifactType, project, versionId, fullUpdate)));
            if (!response.hasErrors())
            {
                QueryMetricsContainer.record(project.getGroupId(), project.getArtifactId(), versionId);
                Optional<ProjectData> latest = projects.find(project.getGroupId(), project.getArtifactId());
                latest.ifPresent(p ->
                {
                    p.addVersion(versionId);
                    p.addProperties(refreshProjectProperties(project, versionId));
                    projects.createOrUpdate(p);
                    response.combine(refreshDependencies(p.getGroupId(), p.getArtifactId(), versionId, fullUpdate));
                });
            }
            return response;
        });
    }

    private MetadataEventResponse executeWithTrace(String label, VersionRevision type, String groupId, String artifactId, String version, Supplier<MetadataEventResponse> functionToExecute)
    {
        return TracerFactory.get().executeWithTrace(label, () ->
        {
            decorateSpanWithProjectInfo(groupId, artifactId, version);
            return handleRefresh(type, groupId, artifactId, version, functionToExecute);
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

    private MetadataEventResponse refreshProjectRevisionArtifacts(ProjectData project)
    {
        return executeWithTrace("refreshProjectRevisionArtifacts", REVISIONS, project.getGroupId(), project.getArtifactId(), MASTER_SNAPSHOT, () ->
        {
            MetadataEventResponse response = new MetadataEventResponse();
            getSupportedArtifactTypes().forEach(artifactType -> response.combine(executeVersionRefresh(artifactType, project, MASTER_SNAPSHOT, false)));
            response.combine(refreshDependencies(project.getGroupId(), project.getArtifactId(), MASTER_SNAPSHOT, false));
            return response;
        });
    }

    private MetadataEventResponse refreshDependencies(String groupId, String artifactId, String versionId, boolean fullUpdate)
    {
        MetadataEventResponse response = new MetadataEventResponse();
        Set<ArtifactDependency> dependencies = repository.findDependencies(groupId, artifactId, versionId);
        Optional<ProjectData> projectData = projects.find(groupId, artifactId);
        LOGGER.info(" Found {} dependencies {}-{}-{}",dependencies.size(),groupId,artifactId,versionId);
        if (projectData.isPresent())
        {
            ProjectData project = projectData.get();
            List<ProjectVersionDependency> newDependencies = new ArrayList<>();

            dependencies.forEach(dependency ->
            {
                Optional<ProjectData> found = projects.find(dependency.getGroupId(), dependency.getArtifactId());
                if (found.isPresent())
                {
                    ProjectData dependentProject = found.get();
                    response.combine(refreshProjectVersionArtifacts(dependentProject.getGroupId(), dependentProject.getArtifactId(), dependency.getVersion(), fullUpdate));
                    newDependencies.add(new ProjectVersionDependency(project.getGroupId(), project.getArtifactId(), versionId,
                            new ProjectVersion(dependentProject.getGroupId(), dependentProject.getArtifactId(), dependency.getVersion())));
                }
                else
                {
                    response.addMessage(String.format("No dependent project found with coordinates: %s-%s", dependency.getGroupId(), dependency.getArtifactId()));
                }
            });
            project.getDependencies(versionId).forEach(project::removeDependency);
            project.addDependencies(newDependencies);
            projects.createOrUpdate(project);
            LOGGER.info("Finished updating {} dependencies {}-{}-{}",  project.getDependencies(versionId).size(),groupId,artifactId,versionId);
        }
        else
        {
            response.addMessage(String.format("No project found with coordinates: %s-%s", groupId, artifactId));
        }
        return response;
    }

    private MetadataEventResponse executeVersionRefresh(ArtifactType artifactType, ProjectData projectData, String versionId, boolean fullUpdate)
    {
        MetadataEventResponse response = new MetadataEventResponse();
        ProjectVersionArtifactsHandler versionsRefresh = ArtifactResolverFactory.getVersionRefresher(artifactType);
        if (versionsRefresh != null)
        {
            List<File> files = findFiles(artifactType, projectData, versionId, fullUpdate);
            response.combine(versionsRefresh.refreshProjectVersionArtifacts(projectData, versionId, files));
        }
        return response;
    }

    private MetadataEventResponse refreshAllProjectVersions(ProjectData project, boolean fullUpdate)
    {
        return handleRefresh(VERSIONS, project.getGroupId(), project.getArtifactId(), ALL,
                () ->
                {
                    MetadataEventResponse response = new MetadataEventResponse();
                    decorateSpanWithProjectInfo(project);
                    String projectArtifacts = String.format("%s,%s,%s", project.getProjectId(), project.getGroupId(), project.getArtifactId());
                    if (repository.areValidCoordinates(project.getGroupId(), project.getArtifactId()))
                    {
                        getLOGGER().info("Fetching project {} versions ", projectArtifacts);
                        List<VersionId> repoVersions = repository.findVersions(project.getGroupId(), project.getArtifactId());
                        List<VersionId> versionsToUpdate = new ArrayList<>();
                        if (repoVersions != null)
                        {
                            versionsToUpdate.addAll(repoVersions);
                        }
                        if (!versionsToUpdate.isEmpty())
                        {
                            if (!fullUpdate && project.getVersions() != null)
                            {
                                versionsToUpdate.removeAll(project.getVersionIds());
                            }
                            getLOGGER().info(String.format("[%s] %s new versions found: [%s]", projectArtifacts, versionsToUpdate.size(), versionsToUpdate));
                            versionsToUpdate.forEach(v -> refreshProjectVersionArtifacts(project, v.toVersionIdString(), fullUpdate));
                            response.addMessage(String.format("[%s:%s:%s] found [%s] new versions", project.getProjectId(),
                                    project.getGroupId(),
                                    project.getArtifactId(),
                                    versionsToUpdate.size()));
                        }
                    }
                    else
                    {
                        getLOGGER().warn("{} bad project settings : {}:{} ", project.getProjectId(), project.getGroupId(), project.getArtifactId());
                        response.logError(project.getProjectId() + " invalid project settings " + projectArtifacts + " versions not refreshed");
                    }
                    return response;
                });
    }

    private List<File> findFiles(ArtifactType type, ProjectData project, String versionId, boolean ignoreFileChanges)
    {
        List<File> filesFromRepo = repository.findFiles(type, project.getGroupId(), project.getArtifactId(), versionId);
        return filesFromRepo.stream().filter(file -> ignoreFileChanges || fileHasChanged(file)).collect(Collectors.toList());
    }

    private boolean fileHasChanged(File file)
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
            return false;
        }
        return false;
    }

    @Override
    public void delete(String groupId, String artifactId, String versionId)
    {
        getSupportedArtifactTypes().forEach(artifactType ->
        {
            ProjectVersionArtifactsHandler versionsRefresh = ArtifactResolverFactory.getVersionRefresher(artifactType);
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
    public List<String> getRepositoryVersions(String groupId, String artifactId)
    {
        return repository.findVersions(groupId,artifactId).stream().map(VersionId::toVersionIdString).collect(Collectors.toList());
    }

    @Override
    public MetadataEventResponse refreshProjectsVersionMismatches()
    {
        MetadataEventResponse response = new MetadataEventResponse();
        Stream<VersionMismatch> projectsWithVersionMismatches = findVersionsMismatches().stream().filter(r -> !r.versionsNotInCache.isEmpty());
        projectsWithVersionMismatches.forEach(vm ->
                {
                        vm.versionsNotInCache.forEach(missingVersion ->
                        {
                            String message = String.format("fixing version-mismatch: %s %s %s ",vm.groupId, vm.artifactId, missingVersion);
                            LOGGER.info(message);
                            response.addMessage(message);
                            response.combine(refreshProjectVersionArtifacts(vm.groupId,vm.artifactId,missingVersion,true));
                        }
                        );
                    }
                );
        LOGGER.info("Finished fixing mismatched versions");
        return response;
    }

    @Override
    public List<VersionMismatch> findVersionsMismatches()
    {
        List<VersionMismatch> versionMismatches = new ArrayList<>();
        projects.getAll().forEach(p ->
        {
            List<String> repositoryVersions = new ArrayList<>(getRepositoryVersions(p.getGroupId(), p.getArtifactId()));
            Collections.sort(repositoryVersions);
            //check versions not in cache
            List<String> versionsNotInCache = new ArrayList<>(repositoryVersions);
            versionsNotInCache.removeAll(p.getVersions());
            //chek versions not in repo
            List<String> versionsNotInRepo = new ArrayList<>(p.getVersions());
            versionsNotInRepo.removeAll(repositoryVersions);

            if (!versionsNotInCache.isEmpty() || !versionsNotInRepo.isEmpty())
            {
                versionMismatches.add(new VersionMismatch(p.getProjectId(), p.getGroupId(), p.getArtifactId(), versionsNotInCache, versionsNotInRepo));
                LOGGER.info("version-mismatch found for {} {} {} : notInCache [{}], notInRepo [{}]", p.getProjectId(), p.getGroupId(), p.getArtifactId(), versionsNotInCache,versionsNotInRepo);
            }

        });
        return versionMismatches;
    }




}
