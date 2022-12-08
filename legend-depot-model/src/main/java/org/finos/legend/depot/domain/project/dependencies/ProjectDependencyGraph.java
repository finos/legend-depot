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

import org.eclipse.collections.api.factory.Maps;
import org.eclipse.collections.api.factory.Sets;
import org.eclipse.collections.api.map.MutableMap;
import org.finos.legend.depot.domain.project.ProjectVersion;

import java.util.Set;

public class ProjectDependencyGraph
{
    private final Set<ProjectVersion> nodes = Sets.mutable.empty();
    private final Set<ProjectVersion> rootNodes = Sets.mutable.empty();
    private final MutableMap<ProjectVersion, Set<ProjectVersion>> forwardEdges = Maps.mutable.empty();
    private final MutableMap<ProjectVersion, Set<ProjectVersion>> backEdges =  Maps.mutable.empty();

    public ProjectDependencyGraph()
    {

    }

    public MutableMap<ProjectVersion, Set<ProjectVersion>> getBackEdges()
    {
        return backEdges;
    }

    public MutableMap<ProjectVersion, Set<ProjectVersion>> getForwardEdges()
    {
        return forwardEdges;
    }

    public Set<ProjectVersion> getNodes()
    {
        return nodes;
    }

    public Set<ProjectVersion> getRootNodes()
    {
        return rootNodes;
    }

    public boolean hasNode(ProjectVersion node)
    {
        return this.nodes.contains(node);
    }

    public void addNode(ProjectVersion node, ProjectVersion parent)
    {
        this.nodes.add(node);
        if (parent == null)
        {
            this.rootNodes.add(node);
        }
    }

    public void setEdges(ProjectVersion from, ProjectVersion to)
    {
        this.forwardEdges.getIfAbsentPut(from, Sets.mutable.empty()).add(to);
        this.backEdges.getIfAbsentPut(to, Sets.mutable.empty()).add(from);
    }
}
