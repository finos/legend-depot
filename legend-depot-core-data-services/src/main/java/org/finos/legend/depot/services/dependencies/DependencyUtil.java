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

import org.eclipse.collections.api.block.function.Function2;
import org.finos.legend.depot.domain.artifacts.repository.ArtifactDependency;
import org.finos.legend.depot.domain.project.ProjectVersion;
import org.finos.legend.depot.domain.project.ProjectVersionData;
import org.finos.legend.depot.services.api.dependencies.DependencyOverride;
import org.finos.legend.depot.store.model.projects.StoreProjectData;

import java.util.ArrayList;
import java.util.Set;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Collection;
import java.util.stream.Collectors;

public class DependencyUtil implements DependencyOverride
{
    @Override
    public List<ProjectVersion> overrideWith(List<ProjectVersion> dependencies, List<ProjectVersion> overridingDependencies, Function2<List<ProjectVersion>, Boolean, Set<ProjectVersion>> executableFunction)
    {
        Map<String, List<ProjectVersion>> dependenciesLocator = dependencies.stream().collect(Collectors.groupingBy(dep -> dep.getGroupId() + dep.getArtifactId()));
        Set<ProjectVersion> overriddenDependencies = overridingDependencies.stream().map(dep -> dependenciesLocator.getOrDefault(dep.getGroupId() + dep.getArtifactId(), Collections.emptyList())).flatMap(Collection::stream).collect(Collectors.toSet());
        overriddenDependencies.removeAll(overridingDependencies);
        Set<ProjectVersion> deleteDependencies = overriddenDependencies.parallelStream().map(dep -> executableFunction.apply(Collections.singletonList(dep), true)).flatMap(Collection::stream).collect(Collectors.toSet());
        dependencies.removeAll(deleteDependencies);
        dependencies.removeAll(overriddenDependencies);
        return dependencies;
    }

    @Override
    public List<ProjectVersion> applyExclusions(List<ProjectVersion> allDependencies, ProjectVersion directDep, Map<String, List<ProjectVersion>> exclusions)
    {
        if (exclusions == null || exclusions.isEmpty())
        {
            return allDependencies;
        }

        List<ProjectVersion> filtered = new ArrayList<>(allDependencies);
        String dependencyKey = ProjectVersionData.createDependencyKey(directDep);
        if (exclusions.containsKey(dependencyKey))
        {
            List<ProjectVersion> exclusionsForDependency = exclusions.get(dependencyKey);
            filtered.removeAll(exclusionsForDependency);
        }

        return filtered;
    }

    @Override
    public List<ProjectVersion> getArtifactDependenciesAsProjectVersions(List<ArtifactDependency> dependencies)
    {
        return dependencies.stream().map(dep -> new ProjectVersion(dep.getGroupId(), dep.getArtifactId(), dep.getVersionId())).collect(Collectors.toList());
    }
}
