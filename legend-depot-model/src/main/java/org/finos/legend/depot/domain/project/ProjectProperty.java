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

package org.finos.legend.depot.domain.project;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ProjectProperty
{
    private String propertyName;
    private String value;
    private String projectVersionId;

    @JsonCreator
    public ProjectProperty(@JsonProperty("propertyName") String propertyName, @JsonProperty("value") String value, @JsonProperty("projectVersionId") String projectVersionId)
    {
        this.propertyName = propertyName;
        this.value = value;
        this.projectVersionId = projectVersionId;
    }

    public String getPropertyName()
    {
        return propertyName;
    }

    public String getValue()
    {
        return value;
    }

    public String getProjectVersionId()
    {
        return projectVersionId;
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
