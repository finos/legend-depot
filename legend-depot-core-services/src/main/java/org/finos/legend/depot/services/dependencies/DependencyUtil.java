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
import org.finos.legend.depot.domain.project.ProjectVersion;

import java.util.Set;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Collection;
import java.util.stream.Collectors;

public class DependencyUtil
{
    public List<ProjectVersion> overrideDependencies(List<ProjectVersion> directDependencies, List<ProjectVersion> projectDependencies, Function2<List<ProjectVersion>, Boolean, Set<ProjectVersion>> executableFunction)
    {
        Map<String, List<ProjectVersion>> dependenciesLocator = projectDependencies.stream().collect(Collectors.groupingBy(dep -> dep.getGroupId() + dep.getArtifactId()));
        Set<ProjectVersion> overriddenDependencies = directDependencies.stream().map(dep -> dependenciesLocator.getOrDefault(dep.getGroupId() + dep.getArtifactId(), Collections.emptyList())).flatMap(Collection::stream).collect(Collectors.toSet());
        overriddenDependencies.removeAll(directDependencies);
        Set<ProjectVersion> deleteDependencies = overriddenDependencies.parallelStream().map(dep -> executableFunction.apply(Collections.singletonList(dep), true)).flatMap(Collection::stream).collect(Collectors.toSet());
        projectDependencies.removeAll(deleteDependencies);
        projectDependencies.removeAll(overriddenDependencies);
        return projectDependencies;
    }
}
