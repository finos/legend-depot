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

import org.eclipse.collections.api.block.function.Function2;
import org.eclipse.collections.api.map.ConcurrentMutableMap;
import org.eclipse.collections.impl.map.mutable.ConcurrentHashMap;
import org.finos.legend.depot.domain.project.ProjectData;
import org.finos.legend.depot.domain.project.ProjectVersion;
import org.finos.legend.depot.domain.project.ProjectVersionDependency;
import org.finos.legend.depot.store.api.projects.Projects;
import org.slf4j.Logger;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.finos.legend.depot.domain.version.VersionValidator.MASTER_SNAPSHOT;

public final class DependenciesCache
{
    private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(DependenciesCache.class);
    final ConcurrentMutableMap<ProjectVersion, Set<ProjectVersion>> transitiveDependencies = new ConcurrentHashMap<>();
    AtomicInteger absentKeys = new AtomicInteger(0);
    private final Projects projectsStore;

    public DependenciesCache(Projects projectsService)
    {
        this.projectsStore = projectsService;
        initCache();
    }

    private void initCache()
    {
        try
        {
            List<ProjectData> allProject = projectsStore.getAll();
            Stream<ProjectVersion> allProjectVersions = allProject.stream().filter(p -> !p.getDependencies().isEmpty()).flatMap(p -> p.getVersions().stream().map(v -> new ProjectVersion(p.getGroupId(), p.getArtifactId(), v)));
            LOGGER.info("Initialising dependencies cache");
            Map<String, ProjectData> projectDataMap = allProject.stream().collect(Collectors.toMap(p -> p.getGroupId() + p.getArtifactId(), Function.identity()));
            allProjectVersions.forEach(pv -> transitiveDependencies.put(pv, calculateTransitiveDependencies(pv, projectDataMap)));
            LOGGER.info("Total [{}] keys in cache",transitiveDependencies.keySet().size());
        }
        catch (Exception e)
        {
            LOGGER.error("Could not initialise dependencies cache {}",e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private Function2<String,String,ProjectData> getProjectDataFromStore()
    {
        return (group, artifact) -> this.projectsStore.find(group, artifact).get();
    }

    private Function2<String,String,ProjectData> getProjectDataFromProjectsMap(Map<String, ProjectData> projectDataMap)
    {
        return (group, artifact) -> projectDataMap.get(group + artifact);
    }

    private Set<ProjectVersion> calculateTransitiveDependencies(ProjectVersion projectVersion, Map<String, ProjectData> projectDataMap)
    {
        return getTransitiveDependencies(projectVersion, getProjectDataFromProjectsMap(projectDataMap));
    }

    private Set<ProjectVersion> getTransitiveDependencies(ProjectVersion pv, Function2<String,String,ProjectData> projectDataProvider)
    {
        Set<ProjectVersion> dependencies = new HashSet<>();
        try
        {
            ProjectData projectData = projectDataProvider.apply(pv.getGroupId(), pv.getArtifactId());
            if (projectData != null)
            {
                List<ProjectVersionDependency> projectVersionDependencies = projectData.getDependencies(pv.getVersionId());
                projectVersionDependencies.forEach(dep ->
                {
                    dependencies.add(dep.getDependency());
                    ProjectVersion dpv = new ProjectVersion(dep.getDependency().getGroupId(), dep.getDependency().getArtifactId(), dep.getDependency().getVersionId());
                    Set<ProjectVersion> deps = this.transitiveDependencies.getIfAbsentPut(dpv,() ->
                    {
                        absentKeys.getAndIncrement();
                        return getTransitiveDependencies(dpv, projectDataProvider);
                    });
                    dependencies.addAll(deps);
                });
            }
        }
        catch (Exception e)
        {
            LOGGER.error("error getting transitive dependencies {}",e.getMessage());
        }
        return dependencies;
    }

    public Set<ProjectVersion> getTransitiveDependencies(ProjectVersion pv)
    {
        if (pv.getVersionId().equals(MASTER_SNAPSHOT))
        {
            absentKeys.getAndIncrement();
            Set<ProjectVersion> masterSNAPSHOTDependencies = getTransitiveDependencies(pv, getProjectDataFromStore());
            this.transitiveDependencies.put(pv,masterSNAPSHOTDependencies);
            return masterSNAPSHOTDependencies;
        }
        else
        {
            //we might end up with dirty entries for wrong projects or non existent versions in the cache but we need speed
            //we must absolutely return empty dependencies
            return this.transitiveDependencies.getIfAbsentPut(pv, () ->
            {
                absentKeys.getAndIncrement();
                return getTransitiveDependencies(pv, getProjectDataFromStore());
            });
        }
    }

}
