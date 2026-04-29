//  Copyright 2025 Goldman Sachs
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

import org.finos.legend.depot.domain.artifacts.repository.ArtifactDependency;
import org.finos.legend.depot.domain.project.ProjectVersion;
import org.finos.legend.depot.domain.project.ProjectVersionData;
import org.finos.legend.depot.services.api.projects.ProjectsService;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Collections;
import java.util.stream.Collectors;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DependencyExclusionsUtil
{
    private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(DependencyExclusionsUtil.class);

    public static Map<String, List<ProjectVersion>> getTransitiveDependenciesOfExclusions(Map<String, List<ProjectVersion>> dependencyExclusions, ProjectsService projects)
    {
        populateVersionsOfDependencyExclusions(dependencyExclusions, projects);

        for (String key : dependencyExclusions.keySet())
        {
            List<ProjectVersion> exclusions = dependencyExclusions.get(key);
            if (exclusions != null)
            {
                LOGGER.info("Processing {} exclusions for direct dependency {} ", exclusions.size(), key);
                Set<ProjectVersion> transitiveExclusions = new HashSet<>();
                for (ProjectVersion exclusion : exclusions)
                {
                    transitiveExclusions.addAll(projects.getDependencies(Collections.singletonList(exclusion), true));
                    LOGGER.info("Found {} transitive dependencies for exclusion {} ", transitiveExclusions.size(), exclusion.getGa());
                }
                List<ProjectVersion> updatedExclusions = new ArrayList<>(exclusions);
                updatedExclusions.addAll(transitiveExclusions);
                dependencyExclusions.put(key, updatedExclusions);
            }
        }
        return dependencyExclusions;
    }

    public static Map<String, List<ProjectVersion>> createDependencyExclusionsMap(List<ArtifactDependency> dependencies)
    {
        HashMap<String, List<ProjectVersion>> dependencyExclusions = new HashMap<>();
        if (dependencies != null)
        {
            for (ArtifactDependency dep : dependencies)
            {
                if (!dep.getExclusions().isEmpty() && dep.getExclusions() != null)
                {
                    String key = ProjectVersionData.createDependencyKey(new ProjectVersion(dep.getGroupId(), dep.getArtifactId(), dep.getVersionId()));
                    dependencyExclusions.put(key, dep.getExclusions().stream().map(e -> new ProjectVersion(e.getGroupId(), e.getArtifactId(), null)).collect(Collectors.toList()));
                }
            }
        }
        return dependencyExclusions;
    }

    private static void populateVersionsOfDependencyExclusions(Map<String, List<ProjectVersion>> dependencyExclusions, ProjectsService projects)
    {
        for (String key : dependencyExclusions.keySet())
        {
            ProjectVersion directDep = ProjectVersionData.reverseDependencyKey(key);
            Set<ProjectVersion> allDepsOfDirectDep = projects.getDependencies(Collections.singletonList(directDep), true);
            List<ProjectVersion> exclusions = dependencyExclusions.get(key);
            for (ProjectVersion exclusion : exclusions)
            {
                ProjectVersion found = allDepsOfDirectDep.stream().filter(d -> d.getGroupId().equals(exclusion.getGroupId()) && d.getArtifactId().equals(exclusion.getArtifactId())).findFirst().orElse(null);
                if (found != null)
                {
                    LOGGER.info("Found version {} for exclusion {} ", found.getVersionId(), key);
                    exclusion.setVersionId(found.getVersionId());
                }
                else
                {
                    LOGGER.info("No version found for exclusion {} under base dependency {}", exclusion.getGa(), key);
                }
            }
        }
    }
}