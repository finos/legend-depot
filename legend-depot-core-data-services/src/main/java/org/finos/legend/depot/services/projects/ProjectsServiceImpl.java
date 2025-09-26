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
import org.finos.legend.depot.domain.notifications.MetadataNotification;
import org.finos.legend.depot.domain.notifications.Priority;
import org.finos.legend.depot.domain.project.ProjectVersion;
import org.finos.legend.depot.domain.project.ProjectVersionData;
import org.finos.legend.depot.domain.project.dependencies.ProjectDependencyGraph;
import org.finos.legend.depot.domain.project.dependencies.ProjectDependencyReport;
import org.finos.legend.depot.domain.project.dependencies.ProjectDependencyVersionNode;
import org.finos.legend.depot.domain.project.dependencies.ProjectDependencyWithPlatformVersions;
import org.finos.legend.depot.domain.version.VersionAlias;
import org.finos.legend.depot.domain.version.VersionValidator;
import org.finos.legend.depot.services.api.dependencies.DependencyOverride;
import org.finos.legend.depot.services.api.metrics.query.QueryMetricsRegistry;
import org.finos.legend.depot.services.api.notifications.queue.Queue;
import org.finos.legend.depot.services.api.projects.ProjectsService;
import org.finos.legend.depot.services.api.projects.configuration.ProjectsConfiguration;
import org.finos.legend.depot.services.dependencies.DependencySATConverter;
import org.finos.legend.depot.services.dependencies.DependencyUtil;
import org.finos.legend.depot.services.dependencies.LogicNGSATResult;
import org.finos.legend.depot.services.dependencies.ProjectDependencyGraphWalkerContext;
import org.finos.legend.depot.store.api.projects.Projects;
import org.finos.legend.depot.store.api.projects.ProjectsVersions;
import org.finos.legend.depot.store.api.projects.UpdateProjects;
import org.finos.legend.depot.store.api.projects.UpdateProjectsVersions;
import org.finos.legend.depot.store.model.projects.StoreProjectData;
import org.finos.legend.depot.store.model.projects.StoreProjectVersionData;
import org.logicng.datastructures.Assignment;
import org.logicng.formulas.FormulaFactory;
import org.logicng.solvers.MaxSATSolver;
import org.logicng.solvers.SATSolver;
import org.logicng.solvers.maxsat.algorithms.MaxSAT;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.Optional;
import java.util.List;
import java.util.HashSet;
import java.util.Collections;
import java.util.ArrayList;
import java.util.Comparator;
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

    private final DependencyOverride dependencyOverride;

    private static final String EXCLUSION_FOUND_IN_STORE = "project version not found for %s-%s-%s, exclusion reason: %s";
    private static final String NOT_FOUND_IN_STORE = "project version not found for %s-%s-%s";

    @Inject
    public ProjectsServiceImpl(ProjectsVersions projectsVersions, Projects projects, @Named("queryMetricsRegistry") QueryMetricsRegistry metricsRegistry, Queue queue, ProjectsConfiguration configuration, @Named("dependencyOverride") DependencyOverride dependencyOverride)
    {
        this.projectsVersions = projectsVersions;
        this.projects = projects;
        this.metricsRegistry = metricsRegistry;
        this.queue = queue;
        this.configuration = configuration;
        this.dependencyOverride = dependencyOverride;
    }

    public ProjectsServiceImpl(UpdateProjectsVersions projectsVersions, UpdateProjects projects, QueryMetricsRegistry metricsRegistry, Queue queue, ProjectsConfiguration configuration)
    {
        this.projectsVersions = projectsVersions;
        this.projects = projects;
        this.metricsRegistry = metricsRegistry;
        this.queue = queue;
        this.configuration = configuration;
        this.dependencyOverride = new DependencyUtil();
    }

    @Override
    public List<StoreProjectData> getAllProjectCoordinates()
    {
        return projects.getAll();
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
    public List<StoreProjectVersionData> findByUpdatedDate(long updatedFrom, long updatedTo)
    {
        return projectsVersions.findByUpdatedDate(updatedFrom, updatedTo);
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
            Optional<StoreProjectData> projectData = this.findCoordinates(groupId, artifactId);
            if (projectData.isPresent() && projectData.get().getLatestVersion() != null)
            {
                return projectsVersions.find(groupId, artifactId, projectData.get().getLatestVersion());
            }
            return Optional.empty();
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
        this.queue.push(new MetadataNotification(projectData.getProjectId(), groupId, artifactId, versionId,true, false, null, Priority.HIGH));
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
    public Set<ProjectVersion> getDependencies(List<ProjectVersion> projectVersions, boolean transitive)
    {
        Set<ProjectVersion> dependencies = new HashSet<>();
        projectVersions.forEach(pv ->
        {
            String version = this.resolveAliasesAndCheckVersionExists(pv.getGroupId(), pv.getArtifactId(), pv.getVersionId());
            StoreProjectVersionData projectData = this.getProject(pv.getGroupId(), pv.getArtifactId(), version);
            List<ProjectVersion> projectVersionDependencies = projectData.getVersionData().getDependencies();
            dependencies.addAll(this.dependencyOverride.overrideWith(projectVersionDependencies, projectVersions, this::getDependencies));
            if (transitive && !projectVersionDependencies.isEmpty())
            {
                if (projectData.getTransitiveDependenciesReport().isValid())
                {
                    // Transitive dependencies report contains both direct and transitive dependencies
                    dependencies.addAll(this.dependencyOverride.overrideWith(projectData.getTransitiveDependenciesReport().getTransitiveDependencies(), projectVersions, this::getDependencies));
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
        buildProjectVersionMap(projectDependencyVersions, graphWalkerContext);
        return buildReportFromGraph(graph, graphWalkerContext);
    }

    private void buildProjectVersionMap(List<ProjectVersion> projectDependencyVersions, ProjectDependencyGraphWalkerContext graphWalkerContext)
    {
        Set<ProjectVersion> dependencies = new HashSet<>();
        projectDependencyVersions.forEach(pv ->
        {
            StoreProjectVersionData versionData = graphWalkerContext.getProjectData(pv.getGroupId(), pv.getArtifactId(), pv.getVersionId());
            if (versionData.getTransitiveDependenciesReport().isValid())
            {
                dependencies.addAll(this.dependencyOverride.overrideWith(versionData.getTransitiveDependenciesReport().getTransitiveDependencies(), projectDependencyVersions, graphWalkerContext::getProjectDataDependencies));
            }
            else
            {
                throw new IllegalStateException(String.format("Error calculating transitive dependencies for project version - %s-%s-%s", versionData.getGroupId(), versionData.getArtifactId(), versionData.getVersionId()));
            }
        });
        dependencies.forEach(dep -> graphWalkerContext.addVersionToProject(dep.getGroupId(), dep.getArtifactId(), dep));
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

    @Override
    public List<ProjectVersion> resolveCompatibleVersions(List<ProjectVersion> projectDependencyVersions, int backtrackVersions)
    {
        Map<ProjectVersion, List<ProjectVersion>> alternativeVersions = new HashMap<>();
        if (projectDependencyVersions.isEmpty())
        {
            return Collections.emptyList();
        }

        projectDependencyVersions.forEach(pv ->
        {
            List<ProjectVersion> alternatives = getAlternativeVersions(pv, backtrackVersions);
            alternativeVersions.put(pv, alternatives);
        });

        MaxSATSolver maxSatSolver = MaxSATSolver.wbo(new FormulaFactory());

        // Convert to LogicNG formulas
        DependencySATConverter converter = new DependencySATConverter(maxSatSolver.factory());
        LogicNGSATResult satResult = converter.convertToLogicNGFormulas(projectDependencyVersions, alternativeVersions, this);

        satResult.getClauses().forEach(maxSatSolver::addHardFormula);

        satResult.getWeights().forEach(maxSatSolver::addSoftFormula);
        MaxSAT.MaxSATResult result = maxSatSolver.solve();
        // Solve and extract solution
        if (result == MaxSAT.MaxSATResult.OPTIMUM)
        {
            return extractSolutionFromModel(maxSatSolver.model(), satResult, projectDependencyVersions);
        }

        return Collections.emptyList();
    }

    private List<ProjectVersion> extractSolutionFromModel(Assignment model, LogicNGSATResult satResult, List<ProjectVersion> originalRequiredProjects)
    {
        List<ProjectVersion> solution = new ArrayList<>();
        Set<String> requiredProjectCoordinates = originalRequiredProjects.stream()
                .map(pv -> pv.getGroupId() + ":" + pv.getArtifactId())
                .collect(Collectors.toSet());

        satResult.getReverseVariableMap().forEach((variable, projectVersion) ->
        {
            if (model.evaluateLit(variable))
            {
                String projectCoordinate = projectVersion.getGroupId() + ":" + projectVersion.getArtifactId();
                if (requiredProjectCoordinates.contains(projectCoordinate))
                {
                    solution.add(projectVersion);
                }
            }
        });

        return solution;
    }

    private List<ProjectVersion> getAlternativeVersions(ProjectVersion pv, int backtrackVersions)
    {
        List<ProjectVersion> alternatives = new ArrayList<>();

        if (backtrackVersions > 0)
        {
            // Only add alternative versions if backtrack is enabled
            List<String> versionStrings = this.getVersions(pv.getGroupId(), pv.getArtifactId(), false);
            versionStrings.sort(ProjectsServiceImpl::compareVersions);

            versionStrings.stream()
                    .sorted(Comparator.reverseOrder())
                    .limit(backtrackVersions)
                    .forEach(v -> alternatives.add(new ProjectVersion(pv.getGroupId(), pv.getArtifactId(), v)));
        }
        else
        {
            alternatives.add(pv);
        }

        return alternatives;
    }

    public static int compareVersions(String v1, String v2)
    {
        String[] parts1 = v1.split("\\.");
        String[] parts2 = v2.split("\\.");

        int length = Math.max(parts1.length, parts2.length);
        for (int i = 0; i < length; i++)
        {
            int part1 = i < parts1.length ? Integer.parseInt(parts1[i]) : 0;
            int part2 = i < parts2.length ? Integer.parseInt(parts2[i]) : 0;

            if (part1 != part2)
            {
                return Integer.compare(part2, part1);
            }
        }
        return 0;
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
