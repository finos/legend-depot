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

package org.finos.legend.depot.store.model.metrics.query;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.finos.legend.depot.store.model.HasIdentifier;

import java.util.Date;

@JsonIgnoreProperties(ignoreUnknown = true)
public class VersionQueryMetric implements HasIdentifier
{

    @JsonProperty
    private String groupId;
    @JsonProperty
    private String artifactId;
    @JsonProperty
    private String versionId;
    @JsonProperty
    private Date lastQueryTime;

    public VersionQueryMetric()
    {
    }


    public VersionQueryMetric(String groupId, String artifactId, String versionId)
    {
        this.versionId = versionId;
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.lastQueryTime = new Date();
    }

    public VersionQueryMetric(String groupId, String artifactId, String versionId, Date lastQueryTime)
    {
        this.versionId = versionId;
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.lastQueryTime = lastQueryTime;
    }

    public String getGroupId()
    {
        return groupId;
    }

    public String getArtifactId()
    {
        return artifactId;
    }

    public String getVersionId()
    {
        return versionId;
    }

    public Date getLastQueryTime()
    {
        return lastQueryTime;
    }

    protected void setLastQueryTime(Date time)
    {
        this.lastQueryTime = time;
    }

    @Override
    @JsonIgnore
    public String getId()
    {
        return "";
    }
}
