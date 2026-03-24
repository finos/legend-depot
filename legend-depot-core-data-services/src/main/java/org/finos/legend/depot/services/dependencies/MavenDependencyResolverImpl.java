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

package org.finos.legend.depot.services.dependencies;

import org.apache.maven.repository.internal.MavenRepositorySystemUtils;
import org.eclipse.aether.DefaultRepositorySystemSession;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.collection.CollectRequest;
import org.eclipse.aether.collection.CollectResult;
import org.eclipse.aether.collection.DependencyCollectionException;
import org.eclipse.aether.connector.basic.BasicRepositoryConnectorFactory;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.graph.DependencyNode;
import org.eclipse.aether.graph.Exclusion;
import org.eclipse.aether.impl.ArtifactDescriptorReader;
import org.eclipse.aether.impl.DefaultServiceLocator;
import org.eclipse.aether.repository.LocalRepository;
import org.eclipse.aether.spi.connector.RepositoryConnectorFactory;
import org.eclipse.aether.util.graph.transformer.ChainedDependencyGraphTransformer;
import org.eclipse.aether.util.graph.transformer.ConflictResolver;
import org.eclipse.aether.util.graph.transformer.JavaScopeDeriver;
import org.eclipse.aether.util.graph.transformer.JavaScopeSelector;
import org.eclipse.aether.util.graph.transformer.NearestVersionSelector;
import org.eclipse.aether.util.graph.transformer.SimpleOptionalitySelector;
import org.eclipse.aether.util.repository.SimpleArtifactDescriptorPolicy;
import org.finos.legend.depot.domain.artifacts.repository.ArtifactDependency;
import org.finos.legend.depot.domain.artifacts.repository.DependencyExclusion;
import org.finos.legend.depot.domain.project.ProjectVersion;
import org.finos.legend.depot.domain.project.ProjectVersionData;
import org.finos.legend.depot.domain.project.dependencies.ProjectDependencyReport;
import org.finos.legend.depot.domain.project.dependencies.ProjectDependencyVersionNode;
import org.finos.legend.depot.services.api.dependencies.MavenDependencyResolver;
import org.finos.legend.depot.services.api.projects.ProjectsService;
import org.finos.legend.depot.services.projects.InMemoryArtifactDescriptorReader;
import org.slf4j.Logger;

import javax.inject.Inject;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class MavenDependencyResolverImpl implements MavenDependencyResolver
{
    private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(MavenDependencyResolverImpl.class);

    private final ProjectsService projectsService;

    @Inject
    public MavenDependencyResolverImpl(ProjectsService projectsService)
    {
        this.projectsService = projectsService;
    }

    @Override
    public Set<ProjectVersion> collectDependencies(List<ProjectVersion> projectVersions, Map<String, List<ProjectVersion>> exclusionsMap)
    {
        boolean hasExclusions = exclusionsMap != null && !exclusionsMap.isEmpty();
        InMemoryArtifactDescriptorReader reader = new InMemoryArtifactDescriptorReader(projectsService);

        List<Dependency> rootDependencies = projectVersions.stream()
                .map(pv ->
                {
                    String gav = pv.getGroupId() + ":" + pv.getArtifactId() + ":" + pv.getVersionId();

                    List<DependencyExclusion> depExclusions = Collections.emptyList();
                    Collection<Exclusion> aetherExclusions = Collections.emptyList();

                    if (hasExclusions)
                    {
                        String depKey = ProjectVersionData.createDependencyKey(pv);
                        List<ProjectVersion> excluded = exclusionsMap.getOrDefault(depKey, Collections.emptyList());
                        if (!excluded.isEmpty())
                        {
                            depExclusions = excluded.stream()
                                    .map(ex -> new DependencyExclusion(ex.getGroupId(), ex.getArtifactId()))
                                    .collect(Collectors.toList());

                            aetherExclusions = excluded.stream()
                                    .map(ex -> new Exclusion(ex.getGroupId(), ex.getArtifactId(), "*", "*"))
                                    .collect(Collectors.toList());
                        }
                    }

                    reader.setExclusions(gav, depExclusions);

                    return new Dependency(
                            new DefaultArtifact(pv.getGroupId(), pv.getArtifactId(), "jar", pv.getVersionId()),
                            "compile",
                            false,
                            aetherExclusions);
                })
                .collect(Collectors.toList());

        return executeCollectRequest(reader, rootDependencies);
    }

    @Override
    public Set<ProjectVersion> collectDependencies(List<ArtifactDependency> artifactDependencies)
    {
        InMemoryArtifactDescriptorReader reader = new InMemoryArtifactDescriptorReader(projectsService);

        List<Dependency> rootDependencies = artifactDependencies.stream()
                .map(ad ->
                {
                    String gav = ad.getGroupId() + ":" + ad.getArtifactId() + ":" + ad.getVersionId();

                    reader.setExclusions(gav, ad.getExclusions());

                    Collection<Exclusion> aetherExclusions = ad.getExclusions().stream()
                            .map(ex -> new Exclusion(ex.getGroupId(), ex.getArtifactId(), "*", "*"))
                            .collect(Collectors.toList());

                    return new Dependency(
                            new DefaultArtifact(ad.getGroupId(), ad.getArtifactId(), "jar", ad.getVersionId()),
                            "compile",
                            false,
                            aetherExclusions);
                })
                .collect(Collectors.toList());

        return executeCollectRequest(reader, rootDependencies);
    }

    @Override
    public ProjectDependencyReport collectDependencyReport(List<ArtifactDependency> artifactDependencies)
    {
        InMemoryArtifactDescriptorReader reader = new InMemoryArtifactDescriptorReader(projectsService);

        List<Dependency> rootDependencies = artifactDependencies.stream()
                .map(ad ->
                {
                    String gav = ad.getGroupId() + ":" + ad.getArtifactId() + ":" + ad.getVersionId();

                    reader.setExclusions(gav, ad.getExclusions());

                    Collection<Exclusion> aetherExclusions = ad.getExclusions().stream()
                            .map(ex -> new Exclusion(ex.getGroupId(), ex.getArtifactId(), "*", "*"))
                            .collect(Collectors.toList());

                    return new Dependency(
                            new DefaultArtifact(ad.getGroupId(), ad.getArtifactId(), "jar", ad.getVersionId()),
                            "compile",
                            false,
                            aetherExclusions);
                })
                .collect(Collectors.toList());

        DependencyNode root = executeCollectRequestForNode(reader, rootDependencies);
        return buildReportFromDependencyNode(root);
    }

    // ── Internal helpers ────────────────────────────────────────────────

    private Set<ProjectVersion> executeCollectRequest(InMemoryArtifactDescriptorReader reader, List<Dependency> rootDependencies)
    {
        DependencyNode root = executeCollectRequestForNode(reader, rootDependencies);
        Set<ProjectVersion> dependencies = new HashSet<>();
        collectDependenciesFromNode(root, dependencies);
        return dependencies;
    }

    private DependencyNode executeCollectRequestForNode(InMemoryArtifactDescriptorReader reader, List<Dependency> rootDependencies)
    {
        RepositorySystem system = newRepositorySystem(reader);
        DefaultRepositorySystemSession session = newSession(system);

        CollectRequest request = new CollectRequest();
        request.setDependencies(rootDependencies);

        try
        {
            CollectResult result = system.collectDependencies(session, request);
//            debugPrintTree(result.getRoot(), 0);
            return result.getRoot();
        }
        catch (DependencyCollectionException e)
        {
            LOGGER.error("Failed to collect dependencies via Maven resolution", e);
            throw new IllegalStateException("Error collecting dependencies: " + e.getMessage(), e);
        }
    }

    private void collectDependenciesFromNode(DependencyNode node, Set<ProjectVersion> dependencies)
    {
        Dependency dependency = node.getDependency();
        if (dependency != null)
        {
            Artifact artifact = dependency.getArtifact();
            dependencies.add(new ProjectVersion(
                    artifact.getGroupId(),
                    artifact.getArtifactId(),
                    artifact.getVersion()
            ));
        }

        for (DependencyNode child : node.getChildren())
        {
            collectDependenciesFromNode(child, dependencies);
        }
    }

    private void debugPrintTree(DependencyNode node, int depth)
    {
        String indent = "  ".repeat(depth);
        Dependency dep = node.getDependency();
        if (dep != null)
        {
            Artifact a = dep.getArtifact();
            System.out.println(indent + a.getGroupId() + ":" + a.getArtifactId() + ":" + a.getVersion() + " exclusions=" + dep.getExclusions());
        }
        else
        {
            System.out.println(indent + "(root)");
        }
        for (DependencyNode child : node.getChildren())
        {
            debugPrintTree(child, depth + 1);
        }
    }

    private ProjectDependencyReport buildReportFromDependencyNode(DependencyNode root)
    {
        ProjectDependencyReport report = new ProjectDependencyReport();
        ProjectDependencyReport.SerializedGraph graph = report.getGraph();
        walkDependencyNode(root, graph, null);
        return report;
    }

    private void walkDependencyNode(DependencyNode node, ProjectDependencyReport.SerializedGraph graph, ProjectVersion parent)
    {
        Dependency dependency = node.getDependency();
        if (dependency != null)
        {
            Artifact artifact = dependency.getArtifact();
            ProjectVersion projectVersion = new ProjectVersion(
                    artifact.getGroupId(),
                    artifact.getArtifactId(),
                    artifact.getVersion()
            );

            ProjectDependencyVersionNode versionNode = ProjectDependencyVersionNode.buildFromProjectVersion(projectVersion);
            graph.getNodes().putIfAbsent(versionNode.getId(), versionNode);

            if (parent == null)
            {
                graph.getRootNodes().add(projectVersion.getGav());
            }
            else
            {
                ProjectDependencyVersionNode parentNode = graph.getNodes().get(parent.getGav());
                if (parentNode != null)
                {
                    parentNode.getForwardEdges().add(projectVersion.getGav());
                    versionNode.getBackEdges().add(parent.getGav());
                }
            }

            for (DependencyNode child : node.getChildren())
            {
                walkDependencyNode(child, graph, projectVersion);
            }
        }
        else
        {
            for (DependencyNode child : node.getChildren())
            {
                walkDependencyNode(child, graph, null);
            }
        }
    }

    @SuppressWarnings("deprecation")
    private RepositorySystem newRepositorySystem(InMemoryArtifactDescriptorReader reader)
    {
        DefaultServiceLocator locator = MavenRepositorySystemUtils.newServiceLocator();

        locator.setServices(ArtifactDescriptorReader.class, reader);
        locator.addService(RepositoryConnectorFactory.class, BasicRepositoryConnectorFactory.class);

        locator.setErrorHandler(new DefaultServiceLocator.ErrorHandler()
        {
            @Override
            public void serviceCreationFailed(Class<?> type, Class<?> impl, Throwable exception)
            {
                LOGGER.error("Service creation failed for {} with implementation {}", type.getName(), impl.getName(), exception);
            }
        });

        return locator.getService(RepositorySystem.class);
    }

    private DefaultRepositorySystemSession newSession(RepositorySystem system)
    {
        DefaultRepositorySystemSession session = MavenRepositorySystemUtils.newSession();

        session.setLocalRepositoryManager(
                system.newLocalRepositoryManager(session, new LocalRepository("target/local-repo"))
        );

        session.setArtifactDescriptorPolicy(new SimpleArtifactDescriptorPolicy(true, true));

        session.setDependencyGraphTransformer(
                new ChainedDependencyGraphTransformer(
                        new ConflictResolver(
                                new NearestVersionSelector(),
                                new JavaScopeSelector(),
                                new SimpleOptionalitySelector(),
                                new JavaScopeDeriver()
                        )
                )
        );

        return session;
    }
}

