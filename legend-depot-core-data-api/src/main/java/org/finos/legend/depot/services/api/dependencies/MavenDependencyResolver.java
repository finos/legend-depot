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

package org.finos.legend.depot.services.api.dependencies;

import org.finos.legend.depot.domain.artifacts.repository.ArtifactDependency;
import org.finos.legend.depot.domain.project.ProjectVersion;
import org.finos.legend.depot.domain.project.dependencies.ProjectDependencyReport;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Centralised Maven/Aether-based dependency resolution.
 * <p>
 * Implementations use the Maven dependency collection engine (Aether) backed
 * by an in-memory artifact descriptor reader that reads project data from the
 * depot store, providing nearest-version conflict resolution, scope selection,
 * and exclusion propagation.
 */
public interface MavenDependencyResolver
{
    /**
     * Collect the flattened set of resolved dependencies using Maven resolution.
     *
     * @param projectVersions the root project versions whose dependencies to resolve
     * @param exclusionsMap   per-dependency exclusions keyed by {@code ProjectVersionData.createDependencyKey}
     * @return the resolved set of dependencies (does NOT include the root versions themselves)
     */
    Set<ProjectVersion> collectDependencies(List<ProjectVersion> projectVersions, Map<String, List<ProjectVersion>> exclusionsMap);

    /**
     * Collect the flattened set of resolved dependencies using Maven resolution
     * from {@link ArtifactDependency} objects that carry their own exclusions.
     *
     * @param artifactDependencies the root dependencies with embedded exclusions
     * @return the resolved set of dependencies (does NOT include the root versions themselves)
     */
    Set<ProjectVersion> collectDependencies(List<ArtifactDependency> artifactDependencies);

    /**
     * Build a full dependency report (graph with nodes, edges, conflicts) using
     * Maven resolution from {@link ArtifactDependency} objects.
     *
     * @param artifactDependencies the root dependencies with embedded exclusions
     * @return the dependency report
     */
    ProjectDependencyReport collectDependencyReport(List<ArtifactDependency> artifactDependencies);
}

