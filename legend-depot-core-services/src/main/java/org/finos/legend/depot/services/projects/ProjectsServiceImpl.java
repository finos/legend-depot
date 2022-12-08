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
import org.finos.legend.depot.domain.api.MetadataEventResponse;
import org.finos.legend.depot.domain.project.ProjectData;
import org.finos.legend.depot.domain.project.dependencies.ProjectDependencyGraph;
import org.finos.legend.depot.domain.project.dependencies.ProjectDependencyGraphWalkerContext;
import org.finos.legend.depot.domain.project.dependencies.ProjectDependencyReport;
import org.finos.legend.depot.domain.project.ProjectVersion;
import org.finos.legend.depot.domain.project.ProjectVersionDependency;
import org.finos.legend.depot.domain.project.ProjectVersionPlatformDependency;
import org.finos.legend.depot.domain.project.dependencies.ProjectDependencyVersionNode;
import org.finos.legend.depot.services.api.projects.ManageProjectsService;
import org.finos.legend.depot.store.api.projects.UpdateProjects;
import org.finos.legend.sdlc.domain.model.version.VersionId;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.finos.legend.depot.domain.version.VersionValidator.MASTER_SNAPSHOT;

public class ProjectsServiceImpl implements ManageProjectsService
{
    private static final String PATH_DELIMITER = ">";

    private final UpdateProjects projects;

    private final DependenciesCache dependenciesCache;

    @Inject
    public ProjectsServiceImpl(UpdateProjects projects, @Named("dependencyCache") DependenciesCache dependenciesCache)
    {
        this.projects = projects;
        this.dependenciesCache = dependenciesCache;
    }

    public ProjectsServiceImpl(UpdateProjects projects)
    {
        this.projects = projects;
        this.dependenciesCache = new DependenciesCache(projects);
    }

    @Override
    public ProjectData createOrUpdate(ProjectData projectData)
    {
        return projects.createOrUpdate(projectData);
    }

    @Override
    public MetadataEventResponse delete(String groupId, String artifactId)
    {
        return projects.delete(groupId, artifactId);
    }

    @Override
    public MetadataEventResponse delete(String projectId)
    {
        return projects.deleteByProjectId(projectId);
    }

    @Override
    public List<ProjectData> getAll()
    {
        return projects.getAll();
    }

    @Override
    public List<ProjectData> getProjects(int page, int pageSize)
    {
        return projects.getProjects(page, pageSize);
    }

    @Override
    public List<ProjectData> findByProjectId(String id)
    {
        return projects.findByProjectId(id);
    }

    @Override
    public List<String> getVersions(String groupId, String artifactId)
    {
        return projects.getVersions(groupId, artifactId);
    }

    @Override
    public Optional<ProjectData> find(String groupId, String artifactId)
    {
        return projects.find(groupId, artifactId);
    }

    @Override
    public boolean exists(String groupId, String artifactId, String versionId)
    {
        return this.find(groupId,artifactId).orElse(new ProjectData()).getVersions().contains(versionId);
    }

    @Override
    public void checkExists(String groupId, String artifactId) throws IllegalArgumentException
    {
        checkExists(groupId, artifactId,null);
    }

    @Override
    public void checkExists(String groupId, String artifactId, String versionId) throws IllegalArgumentException
    {
        Optional<ProjectData> projectData = this.projects.find(groupId,artifactId);
        if (projectData.isPresent())
        {
            if (versionId != null && !versionId.equals(MASTER_SNAPSHOT) && !projectData.get().getVersions().stream().anyMatch(v -> v.equals(versionId)))
            {
                throw new IllegalArgumentException(String.format("No version found for %s-%s-%s",groupId,artifactId,versionId));
            }
        }
        else
        {
            throw new IllegalArgumentException(String.format("No project found for %s-%s",groupId,artifactId));
        }
    }

    @Override
    public Optional<VersionId> getLatestVersion(String groupId, String artifactId)
    {
        return getProject(groupId,artifactId).getLatestVersion();
    }

    @Override
    public Set<ProjectVersion> getDependencies(List<ProjectVersion> projectVersions, boolean transitive)
    {
        Set<ProjectVersion> dependencies = new HashSet<>();
        projectVersions.forEach(pv ->
        {
            ProjectData projectData = getProject(pv.getGroupId(), pv.getArtifactId());
            List<ProjectVersionDependency> projectVersionDependencies = projectData.getDependencies(pv.getVersionId());
            projectVersionDependencies.forEach(dep -> dependencies.add(dep.getDependency()));
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
                context.addVersionToProject(projectVersion.getGroupId(), projectVersion.getArtifactId(), projectVersion);
                if (!context.getProjectVersionToDependencyMap().containsKey(projectVersion))
                {
                    ProjectData projectData =  context.getProjectDataPutIfAbsent(projectVersion.getGroupId(), projectVersion.getArtifactId(),() -> getProject(projectVersion.getGroupId(), projectVersion.getArtifactId()));
                    context.getProjectVersionToDependencyMap().putIfAbsent(projectVersion, projectData.getDependencies(projectVersion.getVersionId()).stream().map(ProjectVersionDependency::getDependency).collect(Collectors.toList()));
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
            ProjectData projectData = graphWalkerContext.getProjectData(versionNode.getGroupId(), versionNode.getArtifactId());
            if (projectData != null)
            {
                versionNode.setProjectId(projectData.getProjectId());
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
            return getAll().stream().map(projectData -> projectData.getDependencies().stream()
                    .filter(dep -> dep.getDependency().getGroupId().equals(groupId) && dep.getDependency().getArtifactId().equals(artifactId))
                    .map(dep -> new ProjectVersionPlatformDependency(dep.getGroupId(), dep.getArtifactId(), dep.getVersionId(), dep.getDependency(), projectData.getPropertiesForProjectVersionID(dep.getVersionId())))
                    .collect(Collectors.toList())).flatMap(Collection::stream).collect(Collectors.toList());
        }
        return getAll().stream().map(projectData -> projectData.getDependencies().stream()
                .filter(dep -> dep.getDependency().getGroupId().equals(groupId) && dep.getDependency().getArtifactId().equals(artifactId) && dep.getDependency().getVersionId().equals(versionId))
                .map(dep -> new ProjectVersionPlatformDependency(dep.getGroupId(), dep.getArtifactId(), dep.getVersionId(), dep.getDependency(), projectData.getPropertiesForProjectVersionID(dep.getVersionId())))
                .collect(Collectors.toList())).flatMap(Collection::stream).collect(Collectors.toList());
    }

    private ProjectData getProject(String groupId, String artifactId)
    {
        Optional<ProjectData> projectData = find(groupId, artifactId);
        if (!projectData.isPresent())
        {
            throw new IllegalArgumentException(String.format("project not found for %s-%s", groupId, artifactId));
        }
        return projectData.get();
    }

}
