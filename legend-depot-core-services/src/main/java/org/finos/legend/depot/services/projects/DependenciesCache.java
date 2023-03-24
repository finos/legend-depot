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
import org.finos.legend.depot.domain.project.ProjectVersionData;
import org.finos.legend.depot.domain.project.StoreProjectVersionData;
import org.finos.legend.depot.store.api.projects.ProjectsVersions;
import org.slf4j.Logger;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.finos.legend.depot.domain.version.VersionValidator.MASTER_SNAPSHOT;

public final class DependenciesCache
{
    private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(DependenciesCache.class);
    private static final String NOT_FOUND_IN_STORE = "%s-%s-%s not found in store";
    private static final String EXCLUSION_FOUND_IN_STORE = "%s-%s-%s not found in store, exclusion reason: %s";
    private static final String TRANSITIVE_DEPENDENCIES_FAILED_MGS = "getTransitiveDependencies failed for %s: %s";
    final ConcurrentMutableMap<ProjectVersion, DependencyResult> transitiveDependencies = new ConcurrentHashMap<>();
    AtomicInteger absentKeys = new AtomicInteger(0);
    AtomicInteger resolutionErrors = new AtomicInteger(0);
    private AtomicBoolean initialised = new AtomicBoolean(false);
    private final ProjectsVersions projectsVersionsStore;

    public DependenciesCache(ProjectsVersions projectsVersionsService,boolean preLoadFromStore)
    {
        this.projectsVersionsStore = projectsVersionsService;
        if (preLoadFromStore)
        {
            initCache();
        }
    }

    public DependenciesCache(ProjectsVersions projectsVersionsService)
    {
        this(projectsVersionsService,true);
    }

    private void initCache()
    {
        try
        {
            List<StoreProjectVersionData> allProjectsVersions = projectsVersionsStore.getAll();
            Stream<ProjectVersion> versionWithDependencies = allProjectsVersions.stream().filter(p -> !p.getVersionId().equals(MASTER_SNAPSHOT) && !p.getVersionData().getDependencies().isEmpty() && !p.getVersionData().isExcluded()).map(pv -> new ProjectVersion(pv.getGroupId(), pv.getArtifactId(), pv.getVersionId()));
            LOGGER.info("Initialising DependenciesCache");
            Map<String, StoreProjectVersionData> projectDataMap = allProjectsVersions.stream().filter(p -> !p.getVersionData().isExcluded()).collect(Collectors.toMap(p -> p.getGroupId() + p.getArtifactId() + p.getVersionId(), Function.identity()));
            versionWithDependencies.forEach(pv -> transitiveDependencies.put(pv, calculateTransitiveDependencies(pv, projectDataMap)));
            LOGGER.info("Initialising DependenciesCache done: Total [{}] keys in cache, resolutionErrors [{}]",transitiveDependencies.keySet().size(),resolutionErrors.get());
        }
        catch (Exception e)
        {
            LOGGER.error("Could not initialise DependenciesCache {}",e.getMessage());
            throw new RuntimeException(e);
        }
        initialised.getAndSet(true);
    }

    private Function3<String,String,String,StoreProjectVersionData> getProjectVersionDataFromStore()
    {
        return (group, artifact, versionId) ->
        {
            Optional<StoreProjectVersionData> projectVersion = this.projectsVersionsStore.find(group, artifact, versionId);
            if (!projectVersion.isPresent())
            {
                throw new IllegalStateException(String.format(NOT_FOUND_IN_STORE, group, artifact, versionId));
            }
            ProjectVersionData versionData = projectVersion.get().getVersionData();
            if (versionData.isExcluded())
            {
                throw new IllegalStateException(String.format(EXCLUSION_FOUND_IN_STORE, group, artifact, versionId, versionData.getExclusionReason()));
            }
            return projectVersion.get();
        };
    }

    private Function3<String,String,String,StoreProjectVersionData> getProjectVersionDataFromProjectsMap(Map<String, StoreProjectVersionData> projectDataMap)
    {
        return (group, artifact, versionId) -> projectDataMap.get(group + artifact + versionId);
    }

    private DependencyResult calculateTransitiveDependencies(ProjectVersion projectVersion, Map<String, StoreProjectVersionData> projectDataMap)
    {
        return calculateTransitiveDependencies(projectVersion, getProjectVersionDataFromProjectsMap(projectDataMap));
    }

    private DependencyResult calculateTransitiveDependencies(ProjectVersion pv, Function3<String,String,String,StoreProjectVersionData> projectDataProvider)
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
                        return calculateTransitiveDependencies(dep, projectDataProvider);
                    });
                    if (DependencyStatus.FAIL.equals(deps.getStatus()))
                    {
                        throw new IllegalStateException(String.format(NOT_FOUND_IN_STORE, dep.getGroupId(), dep.getArtifactId(), dep.getVersionId()));
                    }
                    dependencies.add(dep);
                    dependencies.addAll(deps.getProjectVersion());
                });
            }
            else
            {
                throw  new IllegalStateException(String.format(NOT_FOUND_IN_STORE, pv.getGroupId(),pv.getArtifactId(),pv.getVersionId()));
            }
        }
        catch (Exception e)
        {
            resolutionErrors.getAndIncrement();
            LOGGER.error(String.format(TRANSITIVE_DEPENDENCIES_FAILED_MGS,pv.getGav(),e.getMessage()));
            return new DependencyResult(e.getMessage());
        }
        return new DependencyResult(dependencies);
    }

    public Set<ProjectVersion> getTransitiveDependencies(ProjectVersion pv)
    {
        if (MASTER_SNAPSHOT.equals(pv.getVersionId()))
        {
            absentKeys.getAndIncrement();
            DependencyResult depResult = calculateTransitiveDependencies(pv, getProjectVersionDataFromStore());
            if (DependencyStatus.FAIL.equals(depResult.getStatus()))
            {
                throw new IllegalStateException(String.format(TRANSITIVE_DEPENDENCIES_FAILED_MGS, pv.getGav(),depResult.errors));
            }
            this.transitiveDependencies.put(pv, depResult);
            return depResult.getProjectVersion();
        }
        else
        {
            DependencyResult depResult = this.transitiveDependencies.getIfAbsentPut(pv, () ->
            {
                absentKeys.getAndIncrement();
                return calculateTransitiveDependencies(pv, getProjectVersionDataFromStore());
            });
            if (DependencyStatus.FAIL.equals(depResult.getStatus()))
            {
                depResult = calculateTransitiveDependencies(pv, getProjectVersionDataFromStore());
                if (DependencyStatus.FAIL.equals(depResult.getStatus()))
                {
                    throw new IllegalStateException(String.format(TRANSITIVE_DEPENDENCIES_FAILED_MGS, pv.getGav(),depResult.errors));
                }
                this.transitiveDependencies.put(pv, depResult);
            }
            return depResult.getProjectVersion();
        }
    }

    public static class DependencyResult
    {
        private Set<ProjectVersion> projectVersion;
        private String errors;

        DependencyResult(Set<ProjectVersion> projectVersion)
        {
            this.projectVersion = projectVersion;
        }

        public DependencyResult(String errorMessage)
        {
            this.errors = errorMessage;
        }

        public DependencyStatus getStatus()
        {
            return errors == null ? DependencyStatus.SUCCESS : DependencyStatus.FAIL;
        }

        public Set<ProjectVersion> getProjectVersion()
        {
            return projectVersion;
        }

        public String getErrors()
        {
            return errors;
        }
    }

    public enum DependencyStatus
    {
        SUCCESS, FAIL
    }

}
