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

package org.finos.legend.depot.store.admin.api.artifacts;

import org.finos.legend.depot.store.admin.domain.artifacts.RefreshStatus;

import java.util.List;
import java.util.Optional;

public interface RefreshStatusStore
{
    Optional<RefreshStatus> get(String groupId, String artifactId, String version);

    List<RefreshStatus> find(String groupId, String artifactId, String version,String eventId, String parentEventId);

    List<RefreshStatus> getAll();

    default List<RefreshStatus> find(String groupId, String artifactId, String version)
    {
        return  find(groupId,artifactId,version,null,null);
    }

    void insert(RefreshStatus status);

    RefreshStatus createOrUpdate(RefreshStatus storeStatus);

    void delete(String groupId, String artifactId, String versionId);

}
