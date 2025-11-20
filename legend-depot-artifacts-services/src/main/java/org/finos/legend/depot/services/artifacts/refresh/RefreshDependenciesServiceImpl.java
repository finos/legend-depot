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

import org.finos.legend.depot.domain.project.ProjectVersion;
import org.finos.legend.depot.domain.project.ProjectVersionData;
import org.finos.legend.depot.domain.project.dependencies.ProjectDependencyWithPlatformVersions;
import org.finos.legend.depot.domain.project.dependencies.VersionDependencyReport;
import org.finos.legend.depot.domain.version.VersionValidator;
import org.finos.legend.depot.services.api.artifacts.refresh.RefreshDependenciesService;
import org.finos.legend.depot.services.api.dependencies.DependencyOverride;
import org.finos.legend.depot.services.api.projects.ManageProjectsService;
import org.finos.legend.depot.services.api.artifacts.repository.ArtifactRepository;
import org.finos.legend.depot.domain.artifacts.repository.ArtifactDependency;
import org.finos.legend.depot.services.dependencies.DependencyExclusionsUtil;
import org.finos.legend.depot.store.model.projects.StoreProjectVersionData;
import org.slf4j.Logger;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.util.Optional;

import java.util.stream.Collectors;

public class RefreshDependenciesServiceImpl implements RefreshDependenciesService
{
    private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(RefreshDependenciesServiceImpl.class);

    private final ManageProjectsService projects;
    private final ArtifactRepository repositoryServices;
    private final DependencyOverride dependencyOverride;

    @Inject
    public RefreshDependenciesServiceImpl(ManageProjectsService projects, ArtifactRepository repositoryServices,@Named("dependencyOverride") DependencyOverride dependencyOverride)
    {
        this.projects = projects;
        this.repositoryServices = repositoryServices;
        this.dependencyOverride = dependencyOverride;
    }

    @Override
    public List<ProjectVersion> retrieveDependenciesFromRepository(String groupId, String artifactId, String versionId)
    {
        List<ProjectVersion> versionDependencies = new ArrayList<>();
        LOGGER.info("Finding dependencies for [{}-{}-{}]", groupId, artifactId, versionId);
        Set<ArtifactDependency> dependencies = this.repositoryServices.findDependencies(groupId, artifactId, versionId);
        LOGGER.info("Found [{}] dependencies for [{}-{}-{}]", dependencies.size(), groupId, artifactId, versionId);
        dependencies.forEach(dependency ->  versionDependencies.add(new ProjectVersion(dependency.getGroupId(), dependency.getArtifactId(), dependency.getVersionId())));
        return versionDependencies;
    }

    private VersionDependencyReport calculateTransitiveDependencies(List<ProjectVersion> projectVersions, Map<String, List<ProjectVersion>> exclusions)
    {
        Set<ProjectVersion> projectDependencies = new HashSet<>();
        try
        {
            projectVersions.forEach(deps ->
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
                    List<ProjectVersion> allDependencies = new ArrayList<>(projectData.get().getVersionData().getDependencies());
                    allDependencies.addAll(projectData.get().getTransitiveDependenciesReport().getTransitiveDependencies());
                    if (exclusions != null && !exclusions.isEmpty())
                    {
                        List<ProjectVersion> filteredDependencies = this.dependencyOverride.applyExclusions(allDependencies, deps, exclusions);
                        LOGGER.info("Applied exclusions to dependencies for {}-{}-{}", deps.getGroupId(), deps.getArtifactId(), deps.getVersionId());
                        projectDependencies.addAll(this.dependencyOverride.overrideWith(filteredDependencies, projectVersions, this::getCalculatedTransitiveDependencies));
                    }
                    else
                    {
                        projectDependencies.addAll(this.dependencyOverride.overrideWith(allDependencies, projectVersions, this::getCalculatedTransitiveDependencies));
                    }
                }
                else
                {
                    LOGGER.info(String.format("Finding dependencies for %s-%s-%s as no data is present in the store", deps.getGroupId(), deps.getArtifactId(), deps.getVersionId()));
                    List<ArtifactDependency> artifactDependencies = this.retrieveDependenciesFromRepositoryAsArtifactDependencies(deps.getGroupId(), deps.getArtifactId(), deps.getVersionId());
                    List<ProjectVersion> dependencyVersions = this.dependencyOverride.getArtifactDependenciesAsProjectVersions(artifactDependencies);
                    projectDependencies.addAll(dependencyVersions);
                    Map<String, List<ProjectVersion>> exclusionsFromRepository = this.createExclusionsMapFromArtifactDependencies(artifactDependencies);
                    VersionDependencyReport report;
                    if (exclusionsFromRepository != null && !exclusionsFromRepository.isEmpty())
                    {
                        LOGGER.info("Applying exclusions retrieved from repository for {}-{}-{}", deps.getGroupId(), deps.getArtifactId(), deps.getVersionId());
                        report = calculateTransitiveDependencies(dependencyVersions, exclusionsFromRepository);
                    }
                    else
                    {
                        report = calculateTransitiveDependencies(dependencyVersions);
                    }
                    if (!report.isValid())
                    {
                        throw new IllegalStateException(String.format("Cannot calculate dependencies for project version: %s", deps.getGav()));
                    }
                    else
                    {
                        List<ProjectVersion> allDependencies = new ArrayList<>(report.getTransitiveDependencies());
                        if (exclusions != null && !exclusions.isEmpty())
                        {
                            List<ProjectVersion> filteredDependencies = this.dependencyOverride.applyExclusions(allDependencies, deps, exclusions);
                            projectDependencies.addAll(this.dependencyOverride.overrideWith(filteredDependencies, projectVersions, this::getCalculatedTransitiveDependencies));
                            LOGGER.info("Applied exclusions to dependencies for {}-{}-{}", deps.getGroupId(), deps.getArtifactId(), deps.getVersionId());
                        }
                        else
                        {
                            projectDependencies.addAll(this.dependencyOverride.overrideWith(allDependencies, projectVersions, this::getCalculatedTransitiveDependencies));
                        }
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

    private Set<ProjectVersion> getCalculatedTransitiveDependencies(List<ProjectVersion> directDependencies, boolean transitive)
    {
        return this.calculateTransitiveDependencies(directDependencies).getTransitiveDependencies().stream().collect(Collectors.toSet());
    }

    private VersionDependencyReport calculateTransitiveDependencies(List<ProjectVersion> projectVersions)
    {
        return this.calculateTransitiveDependencies(projectVersions, Collections.emptyMap());
    }

    @Override
    public List<String> validateDependencies(List<ProjectVersion> dependencies, String versionId)
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
            List<ProjectDependencyWithPlatformVersions> dependantProjects = this.projects.getDependantProjects(projectData.getGroupId(), projectData.getArtifactId(), projectData.getVersionId());
            dependantProjects.forEach(d -> updateTransitiveDependencies(d.getGroupId(), d.getArtifactId(), d.getVersionId()));
        }
        return projectData;
    }

    public void setProjectDataTransitiveDependencies(StoreProjectVersionData projectData)
    {
        Map<String, List<ProjectVersion>> exclusions = projectData.getVersionData().getDependencyExclusions();
        if (!exclusions.isEmpty())
        {
            LOGGER.info("Project data has exclusions, calculating transitive exclusions");
            Map<String, List<ProjectVersion>> transitiveExclusions = DependencyExclusionsUtil.getTransitiveDependenciesOfExclusions(exclusions, this.projects);
            projectData.setTransitiveDependenciesReport(calculateTransitiveDependencies(projectData.getVersionData().getDependencies(), transitiveExclusions));
        }
        else
        {
            projectData.setTransitiveDependenciesReport(calculateTransitiveDependencies(projectData.getVersionData().getDependencies()));
        }
    }

    private List<ArtifactDependency> retrieveDependenciesFromRepositoryAsArtifactDependencies(String groupId, String artifactId, String versionId)
    {
        LOGGER.info("Finding dependencies for [{}-{}-{}]", groupId, artifactId, versionId);
        return new ArrayList<>(this.repositoryServices.findDependencies(groupId, artifactId, versionId));
    }

    private Map<String, List<ProjectVersion>> createExclusionsMapFromArtifactDependencies(List<ArtifactDependency> artifactDependencies)
    {
        Map<String, List<ProjectVersion>> exclusions = new HashMap<>();
        for (ArtifactDependency artifactDependency : artifactDependencies)
        {
            if (artifactDependency.getExclusions().isEmpty())
            {
                continue;
            }
            String key = ProjectVersionData.createDependencyKey(new ProjectVersion(artifactDependency.getGroupId(), artifactDependency.getArtifactId(), artifactDependency.getVersionId()));
            List<ProjectVersion> exclusionsList = artifactDependency.getExclusions().stream()
                    .map(exclusion -> new ProjectVersion(exclusion.getGroupId(), exclusion.getArtifactId(), null))
                    .collect(Collectors.toList());
            exclusions.put(key, exclusionsList);
        }
        if (exclusions.isEmpty())
        {
            return null;
        }
        DependencyExclusionsUtil.getTransitiveDependenciesOfExclusions(exclusions, this.projects);
        return exclusions;
    }
}
