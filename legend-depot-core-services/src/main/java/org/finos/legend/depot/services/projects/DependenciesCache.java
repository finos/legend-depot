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

import org.finos.legend.depot.domain.project.ProjectVersion;
import org.finos.legend.depot.services.api.projects.ProjectsService;

import javax.inject.Singleton;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

@Singleton
final class DependenciesCache
{
    private final Map<ProjectVersion, Set<ProjectVersion>> transitiveDependencies = new HashMap<>();
    private final ProjectsService projectsService;

    private DependenciesCache(ProjectsService projectsService)
    {
        this.projectsService = projectsService;
        initCache();
    }

    private void initCache()
    {
        Stream<ProjectVersion> allProjectVersions = projectsService.getAll().parallelStream().flatMap(p -> p.getVersions().stream().map(v -> new ProjectVersion(p.getGroupId(),p.getArtifactId(),v)));
        allProjectVersions.forEach(projectVersion ->
        {
                 transitiveDependencies.putIfAbsent(projectVersion, calculateTransitiveDependencies(projectVersion));
        });
    }

    private Set<ProjectVersion> calculateTransitiveDependencies(ProjectVersion projectVersion)
    {
        //Set<ProjectVersion> depen = projectsService.
        return projectsService.getDependencies(projectVersion.getGroupId(),projectVersion.getArtifactId(),projectVersion.getVersionId(),true);
    }
}
