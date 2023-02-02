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

import org.eclipse.collections.api.block.function.Function3;
import org.eclipse.collections.api.map.ConcurrentMutableMap;
import org.eclipse.collections.impl.map.mutable.ConcurrentHashMap;
import org.finos.legend.depot.domain.project.ProjectVersion;
import org.finos.legend.depot.domain.project.StoreProjectVersionData;
import org.finos.legend.depot.store.api.projects.ProjectsVersions;
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
    private final ProjectsVersions projectsVersionsStore;

    public DependenciesCache(ProjectsVersions projectsVersionsService)
    {
        this.projectsVersionsStore = projectsVersionsService;
        initCache();
    }

    private void initCache()
    {
        try
        {
            List<StoreProjectVersionData> allProjectsVersions = projectsVersionsStore.getAll();
            Stream<ProjectVersion> allProjectVersions = allProjectsVersions.stream().filter(p -> !p.getVersionId().equals(MASTER_SNAPSHOT) && !p.getVersionData().getDependencies().isEmpty()).map(pv -> new ProjectVersion(pv.getGroupId(), pv.getArtifactId(), pv.getVersionId()));
            LOGGER.info("Initialising dependencies cache");
            Map<String, StoreProjectVersionData> projectDataMap = allProjectsVersions.stream().collect(Collectors.toMap(p -> p.getGroupId() + p.getArtifactId() + p.getVersionId(), Function.identity()));
            allProjectVersions.forEach(pv -> transitiveDependencies.put(pv, calculateTransitiveDependencies(pv, projectDataMap)));
            LOGGER.info("Total [{}] keys in cache",transitiveDependencies.keySet().size());
        }
        catch (Exception e)
        {
            LOGGER.error("Could not initialise dependencies cache {}",e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private Function3<String,String,String,StoreProjectVersionData> getProjectDataFromStore()
    {
        return (group, artifact, versionId) -> this.projectsVersionsStore.find(group, artifact,versionId).get();
    }

    private Function3<String,String,String,StoreProjectVersionData> getProjectDataFromProjectsMap(Map<String, StoreProjectVersionData> projectDataMap)
    {
        return (group, artifact, versionId) -> projectDataMap.get(group + artifact + versionId);
    }

    private Set<ProjectVersion> calculateTransitiveDependencies(ProjectVersion projectVersion, Map<String, StoreProjectVersionData> projectDataMap)
    {
        return getTransitiveDependencies(projectVersion, getProjectDataFromProjectsMap(projectDataMap));
    }

    private Set<ProjectVersion> getTransitiveDependencies(ProjectVersion pv, Function3<String,String,String,StoreProjectVersionData> projectDataProvider)
    {
        Set<ProjectVersion> dependencies = new HashSet<>();
        try
        {
            StoreProjectVersionData projectData = projectDataProvider.value(pv.getGroupId(), pv.getArtifactId(), pv.getVersionId());
            if (projectData != null)
            {
                List<ProjectVersion> projectVersionDependencies = projectData.getVersionData().getDependencies();
                projectVersionDependencies.forEach(dep ->
                {
                    dependencies.add(dep);
                    ProjectVersion dpv = new ProjectVersion(dep.getGroupId(), dep.getArtifactId(), dep.getVersionId());
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
