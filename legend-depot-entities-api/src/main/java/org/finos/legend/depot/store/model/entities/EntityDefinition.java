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

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.EqualsExclude;
import org.finos.legend.sdlc.domain.model.entity.Entity;

import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public final class EntityDefinition implements Entity
{
    @JsonProperty
    private String path;
    @JsonProperty
    private String classifierPath;
    @EqualsExclude
    @JsonProperty
    private Map<String, ?> content;

    @JsonCreator
    public EntityDefinition(@JsonProperty(value = "path") String path,
                            @JsonProperty(value = "classifierPath") String classifierPath,
                            @JsonProperty(value = "content") Map<String, ?> content)
    {
        this.path = path;
        this.classifierPath = classifierPath;
        this.content = content;
    }

    @Override
    public String getPath()
    {
        return this.path;
    }

    @Override
    public String getClassifierPath()
    {
        return this.classifierPath;
    }

    public void setClassifierPath(String path)
    {
        this.classifierPath = path;
    }

    @Override
    public Map<String, ?> getContent()
    {
        return this.content;
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
