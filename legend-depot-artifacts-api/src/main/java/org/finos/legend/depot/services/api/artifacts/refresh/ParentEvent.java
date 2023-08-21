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

package org.finos.legend.depot.services.api.artifacts.refresh;

public enum ParentEvent
{
    UPDATE_PROJECT_VERSION,
    UPDATE_PROJECT_ALL_VERSIONS,
    UPDATE_ALL_PROJECT_ALL_VERSIONS,
    UPDATE_ALL_PROJECT_ALL_SNAPSHOTS,
    REFRESH_ALL_VERSION_ARTIFACTS_SCHEDULE;

    private static final String SEPARATOR = "_";

    public static String build(String groupId, String artifactId, String versionId,String parentEventId)
    {
        return parentEventId != null ? parentEventId : groupId + SEPARATOR + artifactId + SEPARATOR + versionId;
    }
}
