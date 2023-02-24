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
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.finos.legend.depot.domain.version.VersionValidator.MASTER_SNAPSHOT;

public final class DependenciesCache
{
    private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(DependenciesCache.class);
    final ConcurrentMutableMap<ProjectVersion, DependencyResult> transitiveDependencies = new ConcurrentHashMap<>();
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
            Stream<ProjectVersion> versionWithDependencies = allProjectsVersions.stream().filter(p -> !p.getVersionId().equals(MASTER_SNAPSHOT) && !p.getVersionData().getDependencies().isEmpty()).map(pv -> new ProjectVersion(pv.getGroupId(), pv.getArtifactId(), pv.getVersionId()));
            LOGGER.info("Initialising dependencies cache");
            Map<String, StoreProjectVersionData> projectDataMap = allProjectsVersions.stream().collect(Collectors.toMap(p -> p.getGroupId() + p.getArtifactId() + p.getVersionId(), Function.identity()));
            versionWithDependencies.forEach(pv -> transitiveDependencies.put(pv, calculateTransitiveDependencies(pv, projectDataMap, DependencyStatus.SUCCESS)));
            LOGGER.info("Total [{}] keys in cache",transitiveDependencies.keySet().size());
        }
        catch (Exception e)
        {
            LOGGER.error("Could not initialise dependencies cache {}",e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private Function3<String,String,String,StoreProjectVersionData> getProjectVersionDataFromStore()
    {
        return (group, artifact, versionId) ->
        {
            Optional<StoreProjectVersionData> projectVersion = this.projectsVersionsStore.find(group, artifact, versionId);
            if (!projectVersion.isPresent())
            {
                throw new IllegalStateException(String.format("%s-%s-%s not found in store", group, artifact, versionId));
            }
            return this.projectsVersionsStore.find(group, artifact,versionId).get();
        };
    }

    private Function3<String,String,String,StoreProjectVersionData> getProjectVersionDataFromProjectsMap(Map<String, StoreProjectVersionData> projectDataMap)
    {
        return (group, artifact, versionId) -> projectDataMap.get(group + artifact + versionId);
    }

    private DependencyResult calculateTransitiveDependencies(ProjectVersion projectVersion, Map<String, StoreProjectVersionData> projectDataMap, DependencyStatus status)
    {
        return calculateTransitiveDependencies(projectVersion, getProjectVersionDataFromProjectsMap(projectDataMap), status);
    }

    private DependencyResult calculateTransitiveDependencies(ProjectVersion pv, Function3<String,String,String,StoreProjectVersionData> projectDataProvider, DependencyStatus status)
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
                    DependencyResult deps = this.transitiveDependencies.getIfAbsentPut(dep,() ->
                    {
                        absentKeys.getAndIncrement();
                        return calculateTransitiveDependencies(dep, projectDataProvider, status);
                    });
                    if (deps.getStatus() == DependencyStatus.FAIL)
                    {
                        throw new IllegalStateException(String.format("Dependency not present in store %s", dep.getGav()));
                    }
                    dependencies.add(dep);
                    dependencies.addAll(deps.getProjectVersion());
                });
            }
            else
            {
                return new DependencyResult(DependencyStatus.FAIL, dependencies);
            }
        }
        catch (Exception e)
        {
            LOGGER.error("error getting transitive dependencies {}",e.getMessage());
            return new DependencyResult(DependencyStatus.FAIL, dependencies);
        }
        return new DependencyResult(status, dependencies);
    }

    public Set<ProjectVersion> getTransitiveDependencies(ProjectVersion pv)
    {
        if (pv.getVersionId().equals(MASTER_SNAPSHOT))
        {
            absentKeys.getAndIncrement();
            DependencyResult depResult = calculateTransitiveDependencies(pv, getProjectVersionDataFromStore(), DependencyStatus.SUCCESS);
            if (depResult.status == DependencyStatus.FAIL)
            {
                throw new RuntimeException(String.format("Error fetching dependencies for %s", pv.getGav()));
            }
            this.transitiveDependencies.put(pv,depResult);
            return depResult.getProjectVersion();
        }
        else
        {
            DependencyResult depResult = this.transitiveDependencies.getIfAbsentPut(pv, () ->
            {
                absentKeys.getAndIncrement();
                return calculateTransitiveDependencies(pv, getProjectVersionDataFromStore(), DependencyStatus.SUCCESS);
            });
            if (depResult.status == DependencyStatus.FAIL)
            {
                throw new RuntimeException(String.format("Error fetching dependencies for %s", pv.getGav()));
            }
            return depResult.getProjectVersion();
        }
    }

    public class DependencyResult
    {
        private DependencyStatus status;
        private Set<ProjectVersion> projectVersion;

        DependencyResult(DependencyStatus status, Set<ProjectVersion> projectVersion)
        {
            this.status = status;
            this.projectVersion = projectVersion;
        }

        public DependencyStatus getStatus()
        {
            return status;
        }

        public Set<ProjectVersion> getProjectVersion()
        {
            return projectVersion;
        }
    }

    public enum DependencyStatus
    {
        SUCCESS, FAIL
    }

}
