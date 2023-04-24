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

package org.finos.legend.depot.services.projects;

import org.eclipse.collections.api.factory.Sets;
import org.finos.legend.depot.domain.project.ProjectVersionData;
import org.finos.legend.depot.domain.project.StoreProjectData;
import org.finos.legend.depot.domain.project.StoreProjectVersionData;
import org.finos.legend.depot.domain.project.ProjectVersion;
import org.finos.legend.depot.domain.project.dependencies.ProjectDependencyGraph;
import org.finos.legend.depot.domain.project.dependencies.ProjectDependencyGraphWalkerContext;
import org.finos.legend.depot.domain.project.dependencies.ProjectDependencyReport;
import org.finos.legend.depot.domain.project.dependencies.ProjectDependencyVersionNode;
import org.finos.legend.depot.domain.project.dependencies.ProjectDependencyWithPlatformVersions;
import org.finos.legend.depot.domain.version.VersionAlias;
import org.finos.legend.depot.domain.version.VersionValidator;
import org.finos.legend.depot.services.api.projects.ProjectsService;
import org.finos.legend.depot.store.api.projects.Projects;
import org.finos.legend.depot.store.api.projects.ProjectsVersions;
import org.finos.legend.depot.store.api.projects.UpdateProjectsVersions;
import org.finos.legend.depot.store.api.projects.UpdateProjects;
import org.finos.legend.sdlc.domain.model.version.VersionId;

import javax.inject.Inject;
import java.util.List;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.Collections;
import java.util.HashSet;
import java.util.stream.Collectors;

public class ProjectsServiceImpl implements ProjectsService
{

    private final ProjectsVersions projectsVersions;

    private final Projects projects;

    private static final String EXCLUSION_FOUND_IN_STORE = "project version not found for %s-%s-%s, exclusion reason: %s";
    private static final String NOT_FOUND_IN_STORE = "project version not found for %s-%s-%s";

    @Inject
    public ProjectsServiceImpl(ProjectsVersions projectsVersions, Projects projects)
    {
        this.projectsVersions = projectsVersions;
        this.projects = projects;
    }

    public ProjectsServiceImpl(UpdateProjectsVersions projectsVersions, UpdateProjects projects)
    {
        this.projectsVersions = projectsVersions;
        this.projects = projects;
    }

    @Override
    public List<StoreProjectData> getAllProjectCoordinates()
    {
        return projects.getAll();
    }

    @Override
    public List<StoreProjectData> getProjects(int page, int pageSize)
    {
        return projects.getProjects(page, pageSize);
    }

    @Override
    public List<String> getVersions(String groupId, String artifactId,boolean includeSnapshots)
    {
        List<StoreProjectVersionData> storeProjectsVersions = this.find(groupId, artifactId);
        return storeProjectsVersions.isEmpty() ? Collections.EMPTY_LIST : storeProjectsVersions.stream().filter(pv -> includeSnapshots || !VersionValidator.isSnapshotVersion(pv.getVersionId()) && !pv.getVersionData().isExcluded()).map(pv -> pv.getVersionId()).collect(Collectors.toList());
    }

    @Override
    public List<StoreProjectData> findByProjectId(String projectId)
    {
        return projects.findByProjectId(projectId);
    }

    @Override
    public List<StoreProjectVersionData> find(String groupId, String artifactId)
    {
        return projectsVersions.find(groupId, artifactId);
    }

    @Override
    public List<StoreProjectVersionData> findVersion(Boolean excluded)
    {
        return projectsVersions.findVersion(excluded);
    }

    @Override
    public Optional<StoreProjectData> findCoordinates(String groupId, String artifactId)
    {
        return projects.find(groupId, artifactId);
    }

    @Override
    public Optional<StoreProjectVersionData> find(String groupId, String artifactId, String versionId)
    {
        if (VersionAlias.LATEST.getName().equals(versionId))
        {
            Optional<VersionId> latest = this.getLatestVersion(groupId, artifactId);
            if (latest.isPresent())
            {
                return projectsVersions.find(groupId, artifactId,latest.get().toVersionIdString());
            }
            else
            {
                return Optional.empty();
            }
        }
        return projectsVersions.find(groupId, artifactId, versionId);
    }

    @Override
    public void checkExists(String groupId, String artifactId) throws IllegalArgumentException
    {
        if (!this.projects.find(groupId, artifactId).isPresent())
        {
            throw new IllegalArgumentException(String.format("No project found for %s-%s",groupId,artifactId));
        }
    }

    @Override
    public void checkExists(String groupId, String artifactId, String versionId) throws IllegalArgumentException
    {
        Optional<StoreProjectVersionData> projectVersion = this.projectsVersions.find(groupId, artifactId, versionId);
        if (!projectVersion.isPresent())
        {
            throw new IllegalArgumentException(String.format(NOT_FOUND_IN_STORE, groupId, artifactId, versionId));
        }
        ProjectVersionData versionData = projectVersion.get().getVersionData();
        if (versionData.isExcluded())
        {
            throw new IllegalArgumentException(String.format(EXCLUSION_FOUND_IN_STORE, groupId, artifactId, versionId, versionData.getExclusionReason()));
        }
    }

    @Override
    public Optional<VersionId> getLatestVersion(String groupId, String artifactId)
    {
        return this.getVersions(groupId, artifactId).stream().filter(v -> !VersionValidator.isSnapshotVersion(v)).map(v -> VersionId.parseVersionId(v)).max(VersionId::compareTo);
    }

    @Override
    public Set<ProjectVersion> getDependencies(List<ProjectVersion> projectVersions, boolean transitive)
    {
        Set<ProjectVersion> dependencies = new HashSet<>();
        projectVersions.forEach(pv ->
        {
            StoreProjectVersionData projectData = this.getProject(pv.getGroupId(), pv.getArtifactId(), pv.getVersionId());
            List<ProjectVersion> projectVersionDependencies = projectData.getVersionData().getDependencies();
            dependencies.addAll(projectVersionDependencies);
            if (transitive && !projectVersionDependencies.isEmpty())
            {
                if (projectData.getTransitiveDependenciesReport().isValid())
                {
                    dependencies.addAll(projectData.getTransitiveDependenciesReport().getTransitiveDependencies());
                }
                else
                {
                    throw new IllegalStateException(String.format("Error calculating transitive dependencies for project version - %s-%s-%s", projectData.getGroupId(), projectData.getArtifactId(), projectData.getVersionId()));
                }
            }
        });
        return dependencies;
    }

    public void buildDependencyGraph(ProjectDependencyGraph graph, ProjectVersion parent, List<ProjectVersion> children, ProjectDependencyGraphWalkerContext context)
    {
        children.forEach(projectVersion ->
        {
            if (!graph.hasNode(projectVersion))
            {
                graph.addNode(projectVersion, parent);
                context.addVersionToProject(projectVersion.getGroupId(), projectVersion.getArtifactId(), projectVersion);
                if (!context.getProjectVersionToDependencyMap().containsKey(projectVersion))
                {
                    StoreProjectVersionData projectData =  context.getProjectDataPutIfAbsent(projectVersion.getGroupId(), projectVersion.getArtifactId(), projectVersion.getVersionId(), () -> getProject(projectVersion.getGroupId(), projectVersion.getArtifactId(), projectVersion.getVersionId()));
                    context.getProjectVersionToDependencyMap().putIfAbsent(projectVersion, projectData.getVersionData().getDependencies());
                }
                List<ProjectVersion> dependencies = context.getProjectVersionToDependencyMap().get(projectVersion);
                dependencies.forEach(child -> graph.setEdges(projectVersion, child));
                buildDependencyGraph(graph, projectVersion, dependencies, context);
            }
        });
    }

    public ProjectDependencyReport getProjectDependencyReport(List<ProjectVersion> projectDependencyVersions)
    {
        ProjectDependencyGraph graph = new ProjectDependencyGraph();
        ProjectDependencyGraphWalkerContext  graphWalkerContext =  new ProjectDependencyGraphWalkerContext();
        buildDependencyGraph(graph, null, projectDependencyVersions, graphWalkerContext);
        return buildReportFromGraph(graph,graphWalkerContext);
    }

    public ProjectDependencyReport buildReportFromGraph(ProjectDependencyGraph dependencyGraph, ProjectDependencyGraphWalkerContext graphWalkerContext)
    {
        ProjectDependencyReport report = new ProjectDependencyReport();
        ProjectDependencyReport.SerializedGraph graph = report.getGraph();

        dependencyGraph.getNodes().forEach(projectVersion ->
        {
            // add node
            ProjectDependencyVersionNode versionNode = ProjectDependencyVersionNode.buildFromProjectVersion(projectVersion);
            graph.getNodes().putIfAbsent(versionNode.getId(), versionNode);
            StoreProjectVersionData projectData = graphWalkerContext.getProjectData(versionNode.getGroupId(), versionNode.getArtifactId(), versionNode.getVersionId());
            if (projectData != null)
            {
                StoreProjectData projectCoordinates = this.projects.find(projectData.getGroupId(), projectData.getArtifactId()).get();
                versionNode.setProjectId(projectCoordinates.getProjectId());
            }
            // forward edges
            dependencyGraph.getForwardEdges().getIfAbsentValue(projectVersion, Sets.mutable.empty()).forEach(forwardNode -> versionNode.getForwardEdges().add(forwardNode.getGav()));
            // back edges
            dependencyGraph.getBackEdges().getIfAbsentValue(projectVersion, Sets.mutable.empty()).forEach(backEdge -> versionNode.getBackEdges().add(backEdge.getGav()));
        });
        // add root nodes
        dependencyGraph.getRootNodes().forEach(rootNode -> graph.getRootNodes().add(rootNode.getGav()));

        // conflicts
        graphWalkerContext.getProjectToVersions().forEach((key, versions) ->
        {
            if (versions.size() > 1)
            {
                Set<String> conflictingVersions = Sets.mutable.empty();
                conflictingVersions.addAll(versions.stream().map(ProjectVersion::getGav).collect(Collectors.toList()));
                report.addConflict(key.getGroupId(), key.getArtifactId(), conflictingVersions);
            }
        });
        return report;
    }

    @Override
    public List<ProjectDependencyWithPlatformVersions> getDependentProjects(String groupId, String artifactId, String versionId)
    {
        if (versionId.equalsIgnoreCase("ALL"))
        {
            return projectsVersions.getAll().stream().map(projectData -> projectData.getVersionData().getDependencies().stream()
                    .filter(dep -> dep.getGroupId().equals(groupId) && dep.getArtifactId().equals(artifactId))
                    .map(dep -> new ProjectDependencyWithPlatformVersions(projectData.getGroupId(), projectData.getArtifactId(), projectData.getVersionId(), dep,projectData.getVersionData().getProperties()))
                    .collect(Collectors.toList())).flatMap(Collection::stream).collect(Collectors.toList());
        }
        return projectsVersions.getAll().stream().map(projectData -> projectData.getVersionData().getDependencies().stream()
                .filter(dep -> dep.getGroupId().equals(groupId) && dep.getArtifactId().equals(artifactId) && dep.getVersionId().equals(versionId))
                .map(dep -> new ProjectDependencyWithPlatformVersions(projectData.getGroupId(), projectData.getArtifactId(), projectData.getVersionId(), dep,projectData.getVersionData().getProperties()))
                .collect(Collectors.toList())).flatMap(Collection::stream).collect(Collectors.toList());
    }


    private StoreProjectVersionData getProject(String groupId, String artifactId, String versionId)
    {
        Optional<StoreProjectVersionData> projectData = projectsVersions.find(groupId, artifactId, versionId);
        if (!projectData.isPresent())
        {
            throw new IllegalArgumentException(String.format(NOT_FOUND_IN_STORE, groupId, artifactId, versionId));
        }
        ProjectVersionData versionData = projectData.get().getVersionData();
        if (versionData.isExcluded())
        {
            throw new IllegalArgumentException(String.format(EXCLUSION_FOUND_IN_STORE, groupId, artifactId, versionId, versionData.getExclusionReason()));
        }
        return projectData.get();
    }

}
