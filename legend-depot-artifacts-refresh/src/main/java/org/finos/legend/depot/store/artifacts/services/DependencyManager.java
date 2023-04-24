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

import org.finos.legend.depot.artifacts.repository.domain.ArtifactDependency;
import org.finos.legend.depot.artifacts.repository.services.RepositoryServices;
import org.finos.legend.depot.domain.project.ProjectVersion;
import org.finos.legend.depot.domain.project.StoreProjectVersionData;
import org.finos.legend.depot.domain.project.dependencies.ProjectDependencyWithPlatformVersions;
import org.finos.legend.depot.domain.project.dependencies.VersionDependencyReport;
import org.finos.legend.depot.domain.version.VersionValidator;
import org.finos.legend.depot.services.api.projects.ManageProjectsService;
import org.slf4j.Logger;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.Optional;
import java.util.stream.Collectors;

public class DependencyManager
{
    private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(DependencyManager.class);

    private final ManageProjectsService projects;
    private final RepositoryServices repositoryServices;


    @Inject
    public DependencyManager(ManageProjectsService projects, RepositoryServices repositoryServices)
    {
        this.projects = projects;
        this.repositoryServices = repositoryServices;
    }

    protected List<ProjectVersion> calculateDependencies(String groupId, String artifactId, String versionId)
    {
        List<ProjectVersion> versionDependencies = new ArrayList<>();
        LOGGER.info("Finding dependencies for [{}-{}-{}]", groupId, artifactId, versionId);
        Set<ArtifactDependency> dependencies = this.repositoryServices.findDependencies(groupId, artifactId, versionId);
        LOGGER.info("Found [{}] dependencies for [{}-{}-{}]", dependencies.size(), groupId, artifactId, versionId);
        dependencies.forEach(dependency ->  versionDependencies.add(new ProjectVersion(dependency.getGroupId(), dependency.getArtifactId(), dependency.getVersion())));
        return versionDependencies;
    }

    protected VersionDependencyReport calculateTransitiveDependencies(List<ProjectVersion> directDependencies)
    {
        Set<ProjectVersion> projectDependencies = new HashSet<>();
        try
        {
            directDependencies.forEach(deps ->
            {
                LOGGER.info(String.format("Finding dependencies for %s-%s-%s", deps.getGroupId(), deps.getArtifactId(), deps.getVersionId()));
                Optional<StoreProjectVersionData> projectData = this.projects.find(deps.getGroupId(), deps.getArtifactId(), deps.getVersionId());
                if (projectData.isPresent())
                {
                    if (projectData.get().getVersionData().isExcluded())
                    {
                        throw new IllegalStateException(String.format("Project Version depending on an excluded version: %s", deps.getGav()));
                    }
                    else if (!projectData.get().getTransitiveDependenciesReport().isValid())
                    {
                        throw new IllegalStateException(String.format("Cannot calculate dependencies for project version: %s", deps.getGav()));
                    }
                    projectDependencies.addAll(projectData.get().getVersionData().getDependencies());
                    projectDependencies.addAll(projectData.get().getTransitiveDependenciesReport().getTransitiveDependencies());
                }
                else
                {
                    LOGGER.info(String.format("Finding dependencies for %s-%s-%s as no data is present in the store", deps.getGroupId(), deps.getArtifactId(), deps.getVersionId()));
                    List<ProjectVersion> dependencies = this.calculateDependencies(deps.getGroupId(), deps.getArtifactId(), deps.getVersionId());
                    projectDependencies.addAll(dependencies);
                    VersionDependencyReport report = calculateTransitiveDependencies(dependencies);
                    if (!report.isValid())
                    {
                        throw new IllegalStateException(String.format("Cannot calculate dependencies for project version: %s", deps.getGav()));
                    }
                    else
                    {
                        projectDependencies.addAll(report.getTransitiveDependencies());
                    }
                }
                projectDependencies.add(deps);
            });
        }
        catch (IllegalStateException e)
        {
            LOGGER.error(e.getMessage());
            return new VersionDependencyReport(new ArrayList<>(), false);
        }
        LOGGER.info("Completed finding dependencies");
        return new VersionDependencyReport(projectDependencies.stream().collect(Collectors.toList()), true);
    }

    public StoreProjectVersionData updateTransitiveDependencies(String groupId, String artifactId, String versionId)
    {
        Optional<StoreProjectVersionData> projectVersionData = this.projects.find(groupId, artifactId, versionId);
        if (!projectVersionData.isPresent() || projectVersionData.get().getVersionData().isExcluded())
        {
            throw new IllegalArgumentException(String.format("project version not found for %s-%s-%s", groupId, artifactId, versionId));
        }
        StoreProjectVersionData projectData = projectVersionData.get();
        LOGGER.info(String.format("Finding dependencies for %s-%s-%s", groupId, artifactId, versionId));
        this.setProjectDataTransitiveDependencies(projectData);
        LOGGER.info(String.format("Completed finding dependencies for %s-%s-%s", groupId, artifactId, versionId));
        projectData = this.projects.createOrUpdate(projectData);
        if (VersionValidator.isSnapshotVersion(projectData.getVersionId()))
        {
            List<ProjectDependencyWithPlatformVersions> dependantProjects = this.projects.getDependentProjects(projectData.getGroupId(), projectData.getArtifactId(), projectData.getVersionId());
            dependantProjects.forEach(d -> updateTransitiveDependencies(d.getGroupId(), d.getArtifactId(), d.getVersionId()));
        }
        return projectData;
    }

    public void setProjectDataTransitiveDependencies(StoreProjectVersionData projectData)
    {
        projectData.setTransitiveDependenciesReport(calculateTransitiveDependencies(projectData.getVersionData().getDependencies()));
    }

    protected List<String> validateDependencies(List<ProjectVersion> dependencies, String versionId)
    {
        List<String> errors = new ArrayList<>();
        dependencies.stream().forEach(dep ->
        {
            if (VersionValidator.isValidReleaseVersion(versionId) && VersionValidator.isSnapshotVersion(dep.getVersionId()))
            {
                String illegalDepError = String.format("Snapshot dependency %s-%s-%s not allowed in versions", dep.getGroupId(), dep.getArtifactId(), dep.getVersionId());
                errors.add(illegalDepError);
                LOGGER.error(illegalDepError);
            }
        });
        return errors;
    }
}
