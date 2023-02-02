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
import org.finos.legend.depot.domain.project.StoreProjectData;
import org.finos.legend.depot.domain.project.StoreProjectVersionData;
import org.finos.legend.depot.domain.project.ProjectVersion;
import org.finos.legend.depot.domain.project.ProjectVersionPlatformDependency;
import org.finos.legend.depot.domain.project.ProjectProperty;
import org.finos.legend.depot.domain.project.ProjectVersionProperty;
import org.finos.legend.depot.domain.project.dependencies.ProjectDependencyGraph;
import org.finos.legend.depot.domain.project.dependencies.ProjectDependencyGraphWalkerContext;
import org.finos.legend.depot.domain.project.dependencies.ProjectDependencyReport;
import org.finos.legend.depot.domain.project.dependencies.ProjectDependencyVersionNode;
import org.finos.legend.depot.services.api.projects.ProjectsService;
import org.finos.legend.depot.store.api.projects.Projects;
import org.finos.legend.depot.store.api.projects.ProjectsVersions;
import org.finos.legend.depot.store.api.projects.UpdateProjectsVersions;
import org.finos.legend.depot.store.api.projects.UpdateProjects;
import org.finos.legend.sdlc.domain.model.version.VersionId;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;
import java.util.Collections;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.HashSet;
import java.util.stream.Collectors;

import static org.finos.legend.depot.domain.version.VersionValidator.MASTER_SNAPSHOT;

public class ProjectsServiceImpl implements ProjectsService
{

    private final ProjectsVersions projectsVersions;

    private final Projects projects;

    private final DependenciesCache dependenciesCache;

    @Inject
    public ProjectsServiceImpl(ProjectsVersions projectsVersions, Projects projects, @Named("dependencyCache") DependenciesCache dependenciesCache)
    {
        this.projectsVersions = projectsVersions;
        this.projects = projects;
        this.dependenciesCache = dependenciesCache;
    }

    public ProjectsServiceImpl(UpdateProjectsVersions projectsVersions, UpdateProjects projects)
    {
        this.projectsVersions = projectsVersions;
        this.projects = projects;
        this.dependenciesCache = new DependenciesCache(projectsVersions);
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
    public List<String> getVersions(String groupId, String artifactId)
    {
        return projectsVersions.getVersions(groupId, artifactId).stream().map(VersionId::parseVersionId).sorted().map(VersionId::toVersionIdString).collect(Collectors.toList());
    }

    @Override
    public List<StoreProjectVersionData> find(String groupId, String artifactId)
    {
        return projectsVersions.find(groupId, artifactId);
    }

    @Override
    public Optional<StoreProjectData> findCoordinates(String groupId, String artifactId)
    {
        return projects.find(groupId, artifactId);
    }

    @Override
    public Optional<StoreProjectVersionData> find(String groupId, String artifactId, String versionId)
    {
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
        if (versionId != null && !versionId.equals(MASTER_SNAPSHOT) && !this.projectsVersions.find(groupId,artifactId,versionId).isPresent())
        {
            throw new IllegalArgumentException(String.format("No version found for %s-%s-%s",groupId,artifactId,versionId));
        }
    }

    @Override
    public Optional<VersionId> getLatestVersion(String groupId, String artifactId)
    {
        List<String> versions = this.getVersions(groupId, artifactId);
        if (versions != null && !versions.isEmpty())
        {
            List<VersionId> versionIds = versions.stream().map(VersionId::parseVersionId).collect(Collectors.toList());
            return versionIds.stream().max(VersionId::compareTo);
        }
        return Optional.empty();
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
                dependencies.addAll(dependenciesCache.getTransitiveDependencies(pv));
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
                context.addVersionToProject(projectVersion.getGroupId(), projectVersion.getArtifactId(), projectVersion.getVersionId(), projectVersion);
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
    public List<ProjectVersionPlatformDependency> getDependentProjects(String groupId, String artifactId, String versionId)
    {
        if (versionId.equalsIgnoreCase("ALL"))
        {
            return projectsVersions.getAll().stream().map(projectData -> projectData.getVersionData().getDependencies().stream()
                    .filter(dep -> dep.getGroupId().equals(groupId) && dep.getArtifactId().equals(artifactId))
                    .map(dep -> new ProjectVersionPlatformDependency(projectData.getGroupId(), projectData.getArtifactId(), projectData.getVersionId(), dep, transformPropertyToProjectProperty(projectData.getVersionData().getProperties(), projectData.getVersionId())))
                    .collect(Collectors.toList())).flatMap(Collection::stream).collect(Collectors.toList());
        }
        return projectsVersions.getAll().stream().map(projectData -> projectData.getVersionData().getDependencies().stream()
                .filter(dep -> dep.getGroupId().equals(groupId) && dep.getArtifactId().equals(artifactId) && dep.getVersionId().equals(versionId))
                .map(dep -> new ProjectVersionPlatformDependency(projectData.getGroupId(), projectData.getArtifactId(), projectData.getVersionId(), dep, transformPropertyToProjectProperty(projectData.getVersionData().getProperties(), projectData.getVersionId())))
                .collect(Collectors.toList())).flatMap(Collection::stream).collect(Collectors.toList());
    }

    private List<ProjectProperty> transformPropertyToProjectProperty(List<ProjectVersionProperty> properties, String versionId)
    {
        return properties.isEmpty() ? Collections.emptyList() : properties.stream().map(p -> new ProjectProperty(p.getPropertyName(), p.getValue(), versionId)).collect(Collectors.toList());
    }

    private StoreProjectVersionData getProject(String groupId, String artifactId, String versionId)
    {
        Optional<StoreProjectVersionData> projectData = projectsVersions.find(groupId, artifactId, versionId);
        if (!projectData.isPresent())
        {
            throw new IllegalArgumentException(String.format("project version not found for %s-%s", groupId, artifactId, versionId));
        }
        return projectData.get();
    }

}
