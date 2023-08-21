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
public class ArtifactsRetentionPolicyConfiguration
{
    private static final int DEFAULT_TTL_FOR_SNAPSHOTS = 30;
    private static final int DEFAULT_TTL_FOR_VERSIONS = 365;
    private static final int DEFAULT_MAX_SNAPSHOTS_ALLOWED = 5;

    @JsonProperty
    private int maximumSnapshotsAllowed = DEFAULT_MAX_SNAPSHOTS_ALLOWED;

    @JsonProperty
    private int ttlForVersionsInDays = DEFAULT_TTL_FOR_VERSIONS;

    @JsonProperty
    private int ttlForSnapshotsInDays = DEFAULT_TTL_FOR_SNAPSHOTS;

    @JsonCreator
    public ArtifactsRetentionPolicyConfiguration(@JsonProperty(value = "maximumSnapshotsAllowed") int maximumSnapshotsAllowed,
                                                 @JsonProperty(value = "ttlForVersionsInDays") int ttlForVersionsInDays,
                                                 @JsonProperty(value = "ttlForSnapshotsInDay") int ttlForSnapshotsInDays)
    {
        this.maximumSnapshotsAllowed = maximumSnapshotsAllowed;
        this.ttlForSnapshotsInDays = ttlForSnapshotsInDays;
        this.ttlForVersionsInDays = ttlForVersionsInDays;
    }

    public int getMaximumSnapshotsAllowed()
    {
        return maximumSnapshotsAllowed;
    }

    public int getTtlForVersions()
    {
        return ttlForVersionsInDays;
    }

    public int getTtlForSnapshots()
    {
        return ttlForSnapshotsInDays;
    }
}
