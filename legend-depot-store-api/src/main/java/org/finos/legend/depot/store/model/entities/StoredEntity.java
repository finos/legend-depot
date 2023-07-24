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

package org.finos.legend.depot.store.model.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.finos.legend.depot.domain.HasIdentifier;
import org.finos.legend.depot.domain.VersionedData;

import java.util.Map;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "_type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = StoredEntityData.class, name = "entityData"),
        @JsonSubTypes.Type(value = StoredEntityStringData.class, name = "entityStringData"),
        @JsonSubTypes.Type(value = StoredEntityReference.class, name = "entityReference")
})
public abstract class StoredEntity extends VersionedData implements HasIdentifier
{

    @JsonProperty
    private Map<String,?> entityAttributes;

    public StoredEntity(String groupId, String artifactId, String versionId, Map<String,?> entityAttributes)
    {
        super(groupId, artifactId, versionId);
        this.entityAttributes = entityAttributes;
    }

    public StoredEntity(String groupId, String artifactId, String versionId)
    {
        super(groupId, artifactId, versionId);
    }

    public Map<String, ?> getEntityAttributes()
    {
        return entityAttributes;
    }

    @Override
    public String getId()
    {
        return "";
    }
}
