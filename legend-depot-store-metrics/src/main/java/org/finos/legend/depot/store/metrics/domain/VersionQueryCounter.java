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

package org.finos.legend.depot.store.metrics.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.finos.legend.depot.domain.HasIdentifier;

import java.util.Date;

@JsonIgnoreProperties(ignoreUnknown = true)
public class VersionQueryCounter implements HasIdentifier
{
    private String id;
    private String groupId;
    private String artifactId;
    private String versionId;
    private Date lastQueryTime;

    public VersionQueryCounter()
    {
    }


    public VersionQueryCounter(String groupId, String artifactId, String versionId)
    {
        this.versionId = versionId;
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.lastQueryTime = new Date();
    }

    public VersionQueryCounter(String groupId, String artifactId, String versionId, Date lastQueryTime)
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
    public String getId()
    {
        return id;
    }
}
