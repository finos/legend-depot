//  Copyright 2022 Goldman Sachs
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

package org.finos.legend.depot.server.pure.model.context.api;

import org.finos.legend.depot.domain.version.VersionValidator;
import org.finos.legend.engine.protocol.pure.v1.model.context.PureModelContextData;

public interface PureModelContextService
{
    String getPureModelContextDataAsString(String groupId, String artifactId, String versionId, String clientVersion, boolean versioned, boolean getDependencies);

    default String getLatestPureModelContextDataAsString(String groupId, String artifactId, String clientVersion, boolean versioned, boolean getDependencies)
    {
        return getPureModelContextDataAsString(groupId, artifactId, VersionValidator.MASTER_SNAPSHOT, clientVersion, versioned, getDependencies);
    }

    PureModelContextData getPureModelContextData(String groupId, String artifactId, String versionId, String clientVersion, boolean versioned, boolean getDependencies);

    default PureModelContextData getLatestPureModelContextData(String groupId, String artifactId, String clientVersion, boolean versioned, boolean getDependencies)
    {
        return getPureModelContextData(groupId, artifactId, VersionValidator.MASTER_SNAPSHOT, clientVersion, versioned, getDependencies);
    }
}
