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

import org.eclipse.collections.api.block.function.Function0;
import org.eclipse.collections.api.factory.Maps;
import org.eclipse.collections.api.factory.Sets;
import org.eclipse.collections.api.map.ConcurrentMutableMap;
import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.impl.map.mutable.ConcurrentHashMap;
import org.finos.legend.depot.domain.CoordinateData;
import org.finos.legend.depot.domain.project.ProjectVersion;
import org.finos.legend.depot.store.model.projects.StoreProjectVersionData;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class ProjectDependencyGraphWalkerContext
{
    private final ConcurrentMutableMap<ProjectVersion, List<ProjectVersion>> projectVersionToDependencyMap = new ConcurrentHashMap<>();
    private final MutableMap<ProjectVersion, StoreProjectVersionData> projectDataMap = Maps.mutable.empty();
    private final MutableMap<DependencyProject, Set<ProjectVersion>> projectToVersions = Maps.mutable.empty();

    public static class DependencyProject extends CoordinateData
    {
        DependencyProject(String groupId, String artifactId)
        {
            super(groupId, artifactId);
        }
    }

    public ProjectDependencyGraphWalkerContext()
    {

    }

    public Map<DependencyProject, Set<ProjectVersion>> getProjectToVersions()
    {
        return projectToVersions;
    }

    public void addVersionToProject(String groupId, String artifactId, ProjectVersion version)
    {
        this.projectToVersions.getIfAbsentPut(new DependencyProject(groupId, artifactId), Sets.mutable.empty()).add(version);
    }

    public ConcurrentMutableMap<ProjectVersion, List<ProjectVersion>> getProjectVersionToDependencyMap()
    {
        return projectVersionToDependencyMap;
    }

    public StoreProjectVersionData getProjectDataPutIfAbsent(String groupId, String artifactId, String versionId, Function0<? extends StoreProjectVersionData> projectDataGetter)
    {
        return this.projectDataMap.getIfAbsentPut(new ProjectVersion(groupId, artifactId, versionId), projectDataGetter);
    }

    public StoreProjectVersionData getProjectData(String groupId, String artifactId, String versionId)
    {
        return this.projectDataMap.get(new ProjectVersion(groupId, artifactId, versionId));
    }

    public Set<ProjectVersion> getProjectDataDependencies(List<ProjectVersion> pv, boolean transitive)
    {
        return transitive == true ? pv.stream().map(p -> this.projectDataMap.get(p).getTransitiveDependenciesReport().getTransitiveDependencies()).flatMap(Collection::stream).collect(Collectors.toSet())
                : pv.stream().map(p -> this.projectDataMap.get(p).getVersionData().getDependencies()).flatMap(Collection::stream).collect(Collectors.toSet());
    }
}
