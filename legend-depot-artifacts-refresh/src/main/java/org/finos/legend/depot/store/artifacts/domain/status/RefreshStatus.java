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

package org.finos.legend.depot.store.artifacts.domain.status;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.finos.legend.depot.domain.HasIdentifier;
import org.finos.legend.depot.domain.api.MetadataEventResponse;

import java.util.Date;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RefreshStatus implements HasIdentifier
{

    private String id;
    private String type;
    private String groupId;
    private String artifactId;
    private String versionId;
    private boolean running = false;
    private MetadataEventResponse response;
    private Date lastRun;
    private Date startTime;
    private long duration;

    public RefreshStatus()
    {
    }

    public RefreshStatus(String type, String groupId, String artifactId, String version)
    {
        this.type = type;
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.versionId = version;
    }

    public String getId()
    {
        return id;
    }

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    public String getGroupId()
    {
        return groupId;
    }

    public String getArtifactId()
    {
        return artifactId;
    }

    public boolean isRunning()
    {
        return running;
    }

    public void setRunning(boolean running)
    {
        this.running = running;
    }

    public MetadataEventResponse getResponse()
    {
        return response;
    }

    public void setResponse(MetadataEventResponse response)
    {
        this.response = response;
    }

    public Date getLastRun()
    {
        return lastRun;
    }

    public void setLastRun(Date lastRun)
    {
        this.lastRun = lastRun;
    }

    public String getVersionId()
    {
        return versionId;
    }

    public Date getStartTime()
    {
        return startTime;
    }

    public void setStartTime(Date startTime)
    {
        this.startTime = startTime;
    }

    public long getDuration()
    {
        return duration;
    }

    public void setDuration(long duration)
    {
        this.duration = duration;
    }


    public RefreshStatus withRunning(boolean running)
    {
        this.running = running;
        if (running)
        {
            this.startTime = new Date();
            this.duration = 0;
        }
        else
        {
            this.lastRun = new Date();
            this.duration = lastRun.getTime() - startTime.getTime();
        }
        return this;
    }

    public RefreshStatus withResponse(MetadataEventResponse response)
    {
        this.response = response;
        return this;
    }

    public RefreshStatus withStartTime(Date startTime)
    {
        this.startTime = startTime;
        return this;
    }
}
