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
import org.finos.legend.depot.store.admin.domain.metrics.VersionQueryCounter;

import java.util.Date;

@JsonIgnoreProperties(ignoreUnknown = true)
public class VersionQuerySummary extends VersionQueryCounter
{
    private int queryCount;

    public VersionQuerySummary(String groupId,String artifactId, String versionId, Date lastQueryTime, int queryCount)
    {
        super(groupId,artifactId,versionId,lastQueryTime);
        this.queryCount = queryCount;
    }

    public int getQueryCount()
    {
        return queryCount;
    }

    public void addToSummary(VersionQueryCounter other)
    {
        queryCount++;
        if (getLastQueryTime().before(other.getLastQueryTime()))
        {
            super.setLastQueryTime(other.getLastQueryTime());
        }
    }

}
