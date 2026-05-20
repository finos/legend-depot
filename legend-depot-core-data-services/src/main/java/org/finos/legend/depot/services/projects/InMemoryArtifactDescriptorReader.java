//  Copyright 2026 Goldman Sachs
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

import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.graph.Exclusion;
import org.eclipse.aether.impl.ArtifactDescriptorReader;
import org.eclipse.aether.resolution.ArtifactDescriptorRequest;
import org.eclipse.aether.resolution.ArtifactDescriptorResult;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.finos.legend.depot.domain.artifacts.repository.DependencyExclusion;
import org.finos.legend.depot.domain.project.ProjectVersion;
import org.finos.legend.depot.domain.project.ProjectVersionData;
import org.finos.legend.depot.services.api.projects.ProjectsService;
import org.finos.legend.depot.store.model.projects.StoreProjectVersionData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.Comparator;


public class InMemoryArtifactDescriptorReader implements ArtifactDescriptorReader
{
    private final ProjectsService projectsService;
    private final Map<String, List<DependencyExclusion>> exclusionsMap;

    public InMemoryArtifactDescriptorReader(ProjectsService projectsService)
    {
        this.projectsService = projectsService;
        this.exclusionsMap = new ConcurrentHashMap<>();
    }

    public void setExclusions(String gav, List<DependencyExclusion> exclusions)
    {
        if (exclusions != null && !exclusions.isEmpty())
        {
            exclusionsMap.put(gav, exclusions);
        }
    }

    @Override
    public ArtifactDescriptorResult readArtifactDescriptor(
            RepositorySystemSession session,
            ArtifactDescriptorRequest request)
    {
        Artifact artifact = request.getArtifact();
        String groupId = artifact.getGroupId();
        String artifactId = artifact.getArtifactId();
        String versionId = artifact.getVersion();
        String gav = groupId + ":" + artifactId + ":" + versionId;

        Optional<StoreProjectVersionData> projectData = projectsService.find(groupId, artifactId, versionId);

        List<Dependency> dependencies = new ArrayList<>();
        if (projectData.isPresent())
        {
            List<ProjectVersion> directDependencies = projectData.get().getVersionData().getDependencies();

            directDependencies = directDependencies.stream()
                    .sorted(Comparator.comparing(ProjectVersion::getGroupId)
                            .thenComparing(ProjectVersion::getArtifactId))
                    .collect(Collectors.toList());

            // Root-level exclusions (set by MavenDependencyResolverImpl for API-provided exclusions)
            List<DependencyExclusion> rootExclusions = exclusionsMap.getOrDefault(gav, Collections.emptyList());

            // Per-dependency exclusions from the store (POM-level exclusions owned by this artifact)
            Map<String, List<ProjectVersion>> storedExclusions = projectData.get().getVersionData().getDependencyExclusions();

            dependencies = directDependencies.stream()
                    .map(pv ->
                    {
                        List<Exclusion> aetherExclusions = new ArrayList<>();

                        // 1. Add root-level exclusions (propagate down subtree via Aether)
                        rootExclusions.forEach(ex ->
                            aetherExclusions.add(new Exclusion(ex.getGroupId(), ex.getArtifactId(), "*", "*"))
                        );

                        // 2. Add per-dependency exclusions from the store for this specific child
                        String childKey = ProjectVersionData.createDependencyKey(pv);
                        List<ProjectVersion> childExclusions = storedExclusions.getOrDefault(childKey, Collections.emptyList());
                        childExclusions.forEach(ex ->
                            aetherExclusions.add(new Exclusion(ex.getGroupId(), ex.getArtifactId(), "*", "*"))
                        );

                        return new Dependency(
                                new DefaultArtifact(pv.getGroupId(), pv.getArtifactId(), "jar", pv.getVersionId()),
                                "compile",
                                false,
                                aetherExclusions);
                    })
                    .collect(Collectors.toList());
        }

        ArtifactDescriptorResult result = new ArtifactDescriptorResult(request);
        result.setArtifact(artifact);
        result.setDependencies(dependencies);
        return result;
    }
}
