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

package org.finos.legend.depot.store.artifacts.api.status;

import org.finos.legend.depot.domain.entity.VersionRevision;
import org.finos.legend.depot.store.artifacts.domain.status.RefreshStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface RefreshStatusService
{
    RefreshStatus get(VersionRevision entitiesType,String groupId, String artifactId, String version);

    List<RefreshStatus> find(VersionRevision entityType, String groupId, String artifactId, String version, Boolean running, LocalDateTime startTimeFrom,LocalDateTime startTimeTo);

    default List<RefreshStatus> find(VersionRevision entityType, String groupId, String artifactId, String version, Boolean running)
    {
        return  find(entityType,groupId,artifactId,version,running,null,null);
    }
}
