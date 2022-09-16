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

package org.finos.legend.depot.domain.project;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.HashSet;
import java.util.Set;
import org.finos.legend.depot.domain.BaseDomain;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ProjectVersionDependencies extends BaseDomain
{

    private String path;
    private String versionId;
    private Set<ProjectVersionDependencies> dependencies;

    public ProjectVersionDependencies()
    {

    }

    public ProjectVersionDependencies(String groupId, String artifactId, String versionId)
    {
        super(groupId, artifactId);
        this.versionId = versionId;
        this.dependencies = new HashSet<>();
    }

    public Set<ProjectVersionDependencies> getDependencies()
    {
        return dependencies;
    }

    public String getVersionId()
    {
        return versionId;
    }


    public String getPath()
    {
        return path;
    }

    public void setPath(String path)
    {
        this.path = path;
    }

    @JsonIgnore
    public String getGav()
    {
        return String.format("%s:%s:%s", this.getGroupId(), this.getArtifactId(), this.getVersionId());
    }
}
