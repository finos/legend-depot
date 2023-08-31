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

package org.finos.legend.depot.services.api.artifacts.configuration;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ArtifactsRefreshPolicyConfiguration
{
    private static final long ONE_HOUR =  60 * 60 * 1000L;

    @JsonProperty
    long versionsUpdateIntervalInMillis = 2 * ONE_HOUR;

    @JsonProperty
    IncludeProjectPropertiesConfiguration includeProjectPropertiesConfiguration;

    @JsonCreator
    public ArtifactsRefreshPolicyConfiguration(@JsonProperty(value = "versionsUpdateIntervalInMillis") Long versionsUpdateIntervalInMillis,
                                               @JsonProperty(value = "includeProjectPropertiesConfiguration") IncludeProjectPropertiesConfiguration includeProjectPropertiesConfiguration)
    {
        this.includeProjectPropertiesConfiguration = includeProjectPropertiesConfiguration;
        if (versionsUpdateIntervalInMillis != null)
        {
            this.versionsUpdateIntervalInMillis = versionsUpdateIntervalInMillis;
        }
    }


    public long getVersionsUpdateIntervalInMillis()
    {
        return versionsUpdateIntervalInMillis;
    }


    public IncludeProjectPropertiesConfiguration getIncludeProjectPropertiesConfiguration()
    {
        return includeProjectPropertiesConfiguration;
    }
}
