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

package org.finos.legend.depot.domain.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class StoredVersionedEntity extends StoredEntity
{
    @JsonCreator
    public StoredVersionedEntity(@JsonProperty(value = "groupId") String groupId,
                                 @JsonProperty(value = "artifactId") String artifactId,
                                 @JsonProperty(value = "versionId") String versionId,
                                 @JsonProperty(value = "entity") EntityDefinition entity)
    {
        super(groupId, artifactId, versionId, entity);
        this.setVersionedEntity(true);
    }

    StoredVersionedEntity(String groupId, String artifactId, String versionId)
    {
        super(groupId, artifactId, versionId);
    }
}
