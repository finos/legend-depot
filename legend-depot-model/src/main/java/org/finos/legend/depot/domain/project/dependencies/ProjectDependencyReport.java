//  Copyright 2022 Goldman Sachs
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

package org.finos.legend.depot.domain.project.dependencies;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.factory.Maps;
import org.eclipse.collections.api.factory.Sets;
import org.eclipse.collections.api.map.MutableMap;
import org.finos.legend.depot.domain.CoordinateData;

import java.util.List;
import java.util.Set;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ProjectDependencyReport
{

    private final List<ProjectDependencyConflict> conflicts = Lists.mutable.empty();
    private final SerializedGraph graph;

    public static class SerializedGraph
    {
        private final MutableMap<String, ProjectDependencyVersionNode> nodes = Maps.mutable.empty();
        private final Set<String> rootNodes = Sets.mutable.empty();

        public MutableMap<String, ProjectDependencyVersionNode> getNodes()
        {
            return nodes;
        }

        public Set<String> getRootNodes()
        {
            return rootNodes;
        }
    }

    public class ProjectDependencyConflict extends CoordinateData
    {
        private Set<String> versions;

        ProjectDependencyConflict(String groupId, String artifactId, Set<String> versions)
        {
            super(groupId, artifactId);
            this.versions = versions;
        }

        public Set<String> getVersions()
        {
            return versions;
        }
    }

    public ProjectDependencyReport()
    {
        this.graph = new SerializedGraph();
    }

    public void addConflict(String groupId, String artifactId, Set<String> versions)
    {
        if (versions.size() < 1)
        {
            throw new UnsupportedOperationException("Conflicts must have more than one version");
        }
        this.getConflicts().add(new ProjectDependencyConflict(groupId, artifactId, versions));
    }

    public List<ProjectDependencyConflict> getConflicts()
    {
        return conflicts;
    }

    public SerializedGraph getGraph()
    {
        return graph;
    }
}
