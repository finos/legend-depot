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

package org.finos.legend.depot.domain.project.dependencies;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.eclipse.collections.api.factory.Sets;
import org.finos.legend.depot.domain.VersionedData;
import org.finos.legend.depot.store.model.HasIdentifier;
import org.finos.legend.depot.domain.project.ProjectVersion;

import java.util.Set;

public class ProjectDependencyVersionNode extends VersionedData implements HasIdentifier
{
    private String projectId;

    private Set<String> forwardEdges;

    private Set<String> backEdges;

    public ProjectDependencyVersionNode(String groupId, String artifactId, String versionId)
    {
        super(groupId, artifactId, versionId);
        this.forwardEdges = Sets.mutable.empty();
        this.backEdges = Sets.mutable.empty();
    }

    public static ProjectDependencyVersionNode buildFromProjectVersion(ProjectVersion version)
    {
        return new ProjectDependencyVersionNode(version.getGroupId(), version.getArtifactId(), version.getVersionId());
    }

    public Set<String> getBackEdges()
    {
        return backEdges;
    }

    public String getProjectId()
    {
        return projectId;
    }

    public Set<String> getForwardEdges()
    {
        return forwardEdges;
    }

    public void setProjectId(String projectId)
    {
        this.projectId = projectId;
    }

    @JsonIgnore
    public String getGav()
    {
        return String.format("%s:%s:%s", this.getGroupId(), getArtifactId(), this.getVersionId());
    }

    @JsonIgnore
    public String getCoordinates()
    {
        return String.format("%s:%s", this.getGroupId(), getArtifactId());
    }

    public String getId()
    {
        return this.getGav();
    }

}
