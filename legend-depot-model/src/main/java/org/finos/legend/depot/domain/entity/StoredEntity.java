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
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.finos.legend.depot.domain.BaseDomain;
import org.finos.legend.depot.domain.HasIdentifier;

import javax.validation.constraints.NotNull;

@JsonIgnoreProperties(ignoreUnknown = true)
public class StoredEntity extends BaseDomain implements HasIdentifier
{
    @NotNull
    @JsonProperty
    private String versionId;
    @JsonProperty
    private boolean versionedEntity;
    @JsonProperty
    private EntityDefinition entity;


    @JsonCreator
    public StoredEntity(@JsonProperty(value = "groupId") String groupId,
                        @JsonProperty(value = "artifactId") String artifactId,
                        @JsonProperty(value = "versionId") String versionId,
                        @JsonProperty(value = "versionedEntity") boolean versionedEntity,
                        @JsonProperty(value = "entity") EntityDefinition entity)
    {
        super(groupId, artifactId);
        this.versionId = versionId;
        this.versionedEntity = versionedEntity;
        this.entity = entity;
    }

    StoredEntity(String groupId, String artifactId, String versionId,boolean versionedEntity)
    {
        super(groupId, artifactId);
        this.versionId = versionId;
        this.versionedEntity = versionedEntity;
    }

    @Override
    public String getId()
    {
        return "";
    }

    public String getVersionId()
    {
        return versionId;
    }

    public void setVersionId(String versionId)
    {
        this.versionId = versionId;
    }

    public boolean isVersionedEntity()
    {
        return versionedEntity;
    }

    public EntityDefinition getEntity()
    {
        return entity;
    }

    @Override
    public boolean equals(Object obj)
    {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode()
    {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}
