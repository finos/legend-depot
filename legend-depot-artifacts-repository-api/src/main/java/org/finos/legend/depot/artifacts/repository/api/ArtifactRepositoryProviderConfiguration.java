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

package org.finos.legend.depot.artifacts.repository.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;

public abstract class ArtifactRepositoryProviderConfiguration
{
    private static final long ONE_HOUR =  60 * 60 * 1000L;
    private final String name;

    @JsonProperty
    long versionsUpdateIntervalInMillis = 12 * ONE_HOUR;

    @JsonProperty
    long latestUpdateIntervalInMillis = 3 * ONE_HOUR;

    @JsonProperty
    long fixVersionsMismatchIntervalInMillis = 1 * ONE_HOUR;

    protected ArtifactRepositoryProviderConfiguration(String name)
    {
        this.name = name;
    }

    public static ObjectMapper configureObjectMapper(ObjectMapper objectMapper)
    {
        @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.WRAPPER_OBJECT)
        abstract class WrapperMixin
        {
        }

        return objectMapper
                .addMixIn(ArtifactRepositoryProviderConfiguration.class, WrapperMixin.class);
    }

    public static ArtifactRepositoryProviderConfiguration voidConfiguration()
    {
        return new VoidArtifactRepositoryConfiguration();
    }

    public String getName()
    {
        return name;
    }

    public long getVersionsUpdateIntervalInMillis()
    {
        return versionsUpdateIntervalInMillis;
    }

    public long getLatestUpdateIntervalInMillis()
    {
        return latestUpdateIntervalInMillis;
    }

    public long getFixVersionsMismatchIntervalInMillis()
    {
        return fixVersionsMismatchIntervalInMillis;
    }

    public abstract ArtifactRepository initialiseArtifactRepositoryProvider();


}
