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
import org.finos.legend.depot.domain.notifications.EventPriority;
import org.finos.legend.depot.domain.notifications.MetadataNotification;
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
import org.finos.legend.depot.services.dependencies.DependencyUtil;
import org.finos.legend.depot.services.projects.configuration.ProjectsConfiguration;
import org.finos.legend.depot.store.api.projects.Projects;
import org.finos.legend.depot.store.api.projects.ProjectsVersions;
import org.finos.legend.depot.store.api.projects.UpdateProjectsVersions;
import org.finos.legend.depot.store.api.projects.UpdateProjects;
import org.finos.legend.depot.store.metrics.api.QueryMetricsRegistry;
import org.finos.legend.depot.store.notifications.queue.api.Queue;
import org.finos.legend.sdlc.domain.model.version.VersionId;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.finos.legend.depot.domain.version.VersionValidator.BRANCH_SNAPSHOT;

public class ProjectsServiceImpl implements ProjectsService
{

    private final ProjectsVersions projectsVersions;

    private final Projects projects;

    private final QueryMetricsRegistry metricsRegistry;

    private final Queue queue;

    private final ProjectsConfiguration configuration;

    private final DependencyUtil dependencyUtil;

    private static final String EXCLUSION_FOUND_IN_STORE = "project version not found for %s-%s-%s, exclusion reason: %s";
    private static final String NOT_FOUND_IN_STORE = "project version not found for %s-%s-%s";

    @Inject
    public ProjectsServiceImpl(ProjectsVersions projectsVersions, Projects projects, @Named("queryMetricsRegistry") QueryMetricsRegistry metricsRegistry, Queue queue, ProjectsConfiguration configuration, @Named("dependencyUtil") DependencyUtil dependencyUtil)
    {
        this.projectsVersions = projectsVersions;
        this.projects = projects;
        this.metricsRegistry = metricsRegistry;
        this.queue = queue;
        this.configuration = configuration;
        this.dependencyUtil = dependencyUtil;
    }

    public ProjectsServiceImpl(UpdateProjectsVersions projectsVersions, UpdateProjects projects, QueryMetricsRegistry metricsRegistry, Queue queue, ProjectsConfiguration configuration)
    {
        this.projectsVersions = projectsVersions;
        this.projects = projects;
        this.metricsRegistry = metricsRegistry;
        this.queue = queue;
        this.configuration = configuration;
        this.dependencyUtil = new DependencyUtil();
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
        return this.find(groupId, artifactId).stream().filter(pv -> (includeSnapshots || !VersionValidator.isSnapshotVersion(pv.getVersionId())) && !pv.getVersionData().isExcluded()).map(pv -> pv.getVersionId()).collect(Collectors.toList());
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
    public List<StoreProjectVersionData> findSnapshotVersions(String groupId, String artifactId)
    {
        return this.find(groupId, artifactId).stream().filter(v -> VersionValidator.isSnapshotVersion(v.getVersionId()) && !v.getVersionData().isExcluded()).collect(Collectors.toList());
    }

    private String defaultBranch(StoreProjectData project)
    {
        String defaultBranch = project.getDefaultBranch();
        return defaultBranch != null ? defaultBranch : configuration.getDefaultBranch();
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
            return projectsVersions.find(groupId, artifactId).stream().filter(v -> !VersionValidator.isSnapshotVersion(v.getVersionId()) && !v.getVersionData().isExcluded()).max(Comparator.comparing(o -> VersionId.parseVersionId(o.getVersionId())));
        }
        else if (VersionAlias.HEAD.getName().equals(versionId))
        {
            Optional<StoreProjectData> project = this.findCoordinates(groupId, artifactId);
            if (project.isPresent())
            {
                return projectsVersions.find(groupId, artifactId, BRANCH_SNAPSHOT(defaultBranch(project.get())));
            }
            else
            {
                return Optional.empty();
            }
        }
        return projectsVersions.find(groupId, artifactId, versionId);
    }

    private void restoreEvictedProjectVersion(String groupId, String artifactId, String versionId)
    {
        StoreProjectData projectData = this.findCoordinates(groupId, artifactId).get();
        this.queue.push(new MetadataNotification(projectData.getProjectId(), groupId, artifactId, versionId,true, false, null, EventPriority.HIGH));
    }

    @Override
    public String resolveAliasesAndCheckVersionExists(String groupId, String artifactId, String versionId)
    {
        String version;
        Optional<StoreProjectVersionData> projectVersion = this.find(groupId, artifactId, versionId);
        if (projectVersion.isPresent())
        {
            version = projectVersion.get().getVersionId();
        }
        else
        {
            throw new IllegalArgumentException(String.format(NOT_FOUND_IN_STORE, groupId, artifactId, versionId));
        }
        ProjectVersionData versionData = projectVersion.get().getVersionData();
        if (versionData.isExcluded())
        {
            throw new IllegalArgumentException(String.format(EXCLUSION_FOUND_IN_STORE, groupId, artifactId, version, versionData.getExclusionReason()));
        }
        else if (projectVersion.get().isEvicted())
        {
            restoreEvictedProjectVersion(groupId, artifactId, version);
            throw new IllegalStateException(String.format("Project version: %s-%s-%s is being restored, please retry in 5 minutes", groupId, artifactId, version));
        }
        metricsRegistry.record(groupId, artifactId, version);
        return version;
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
    public Optional<VersionId> getLatestVersion(String groupId, String artifactId)
    {
        return this.getVersions(groupId, artifactId,false).stream().map(v -> VersionId.parseVersionId(v)).max(VersionId::compareTo);
    }

    @Override
    public Set<ProjectVersion> getDependencies(List<ProjectVersion> projectVersions, boolean transitive)
    {
        Set<ProjectVersion> dependencies = new HashSet<>();
        projectVersions.forEach(pv ->
        {
            String version = this.resolveAliasesAndCheckVersionExists(pv.getGroupId(), pv.getArtifactId(), pv.getVersionId());
            StoreProjectVersionData projectData = this.getProject(pv.getGroupId(), pv.getArtifactId(), version);
            List<ProjectVersion> projectVersionDependencies = projectData.getVersionData().getDependencies();
            dependencies.addAll(projectVersionDependencies);
            if (transitive && !projectVersionDependencies.isEmpty())
            {
                if (projectData.getTransitiveDependenciesReport().isValid())
                {
                    dependencies.addAll(this.dependencyUtil.overrideDependencies(projectVersions, projectData.getTransitiveDependenciesReport().getTransitiveDependencies(), this::getDependencies));
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
        ProjectDependencyReport report = buildReportFromGraph(graph, graphWalkerContext);
        return overrideConflictDependencies(projectDependencyVersions, report);
    }

    public ProjectDependencyReport overrideConflictDependencies(List<ProjectVersion> projectDependencyVersions, ProjectDependencyReport report)
    {
        List<ProjectDependencyReport.ProjectDependencyConflict> conflicts = new ArrayList<>(report.getConflicts());
        projectDependencyVersions.stream().forEach(dep ->
        {
            Optional<ProjectDependencyReport.ProjectDependencyConflict> conflictPresent = conflicts.stream().filter(conflict -> conflict.getGroupId().equals(dep.getGroupId()) && conflict.getArtifactId().equals(dep.getArtifactId())).findFirst();
            if (conflictPresent.isPresent())
            {
                report.removeConflict(conflictPresent.get());
            }
        });
        return report;
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
    public List<ProjectDependencyWithPlatformVersions> getDependantProjects(String groupId, String artifactId, String versionId, boolean latestOnly)
    {
        ConcurrentLinkedQueue<ProjectDependencyWithPlatformVersions> result = new ConcurrentLinkedQueue<>();
        if (versionId.equalsIgnoreCase("ALL"))
        {
            this.getAllProjectCoordinates().parallelStream().forEach(project ->
            {
                projectsVersions.find(project.getGroupId(), project.getArtifactId()).parallelStream().forEach(projectData ->
                {
                    Stream<ProjectVersion> dependencies = projectData.getVersionData().getDependencies().stream().filter(dep -> dep.getGroupId().equals(groupId) && dep.getArtifactId().equals(artifactId));
                   dependencies.forEach(dep -> result.offer(new ProjectDependencyWithPlatformVersions(projectData.getGroupId(), projectData.getArtifactId(), projectData.getVersionId(), dep, projectData.getVersionData().getProperties())));
                });
            });
        }
        else
        {
            String version =  this.resolveAliasesAndCheckVersionExists(groupId, artifactId, versionId);
            this.getAllProjectCoordinates().parallelStream().forEach(project ->
            {
                projectsVersions.find(project.getGroupId(), project.getArtifactId()).parallelStream().forEach(projectData ->
                {
                    Stream<ProjectVersion> dependencies = projectData.getVersionData().getDependencies().stream().filter(dep -> dep.getGroupId().equals(groupId) && dep.getArtifactId().equals(artifactId) && dep.getVersionId().equals(version));
                    dependencies.forEach(dep -> result.offer(new ProjectDependencyWithPlatformVersions(projectData.getGroupId(), projectData.getArtifactId(), projectData.getVersionId(), dep, projectData.getVersionData().getProperties())));
                });
            });
        }
        return latestOnly ? filterProjectByLatest(result.stream().collect(Collectors.toList())) : result.stream().collect(Collectors.toList());
    }

    private List<ProjectDependencyWithPlatformVersions> filterProjectByLatest(List<ProjectDependencyWithPlatformVersions> projects)
    {
        Map<String, List<ProjectDependencyWithPlatformVersions>> groupedProjectDependencyWithPlatformVersions = projects.stream().filter(p -> !VersionValidator.isSnapshotVersion(p.getVersionId())).collect(Collectors.groupingBy(p -> p.getGroupId() + p.getArtifactId()));
        return groupedProjectDependencyWithPlatformVersions.entrySet().stream().map(set -> set.getValue().stream().max(Comparator.comparing(ProjectDependencyWithPlatformVersions::getVersionId)).get()).collect(Collectors.toList());
    }

    private StoreProjectVersionData getProject(String groupId, String artifactId, String versionId)
    {
        Optional<StoreProjectVersionData> projectData = this.find(groupId, artifactId, versionId);
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
