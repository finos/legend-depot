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

package org.finos.legend.depot.domain.generation.file;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.finos.legend.depot.domain.BaseDomain;
import org.finos.legend.depot.domain.HasIdentifier;

import javax.validation.constraints.NotNull;

@JsonIgnoreProperties(ignoreUnknown = true)
public class StoredFileGeneration extends BaseDomain implements HasIdentifier
{
    @JsonProperty
    private String id;
    @JsonProperty
    @NotNull
    private String versionId;
    @JsonProperty
    private String path;
    @JsonProperty
    private String type;
    @JsonProperty
    private FileGeneration file;

    @JsonCreator
    public StoredFileGeneration(@JsonProperty(value = "groupId") String groupId,
                                @JsonProperty(value = "artifactId") String artifactId,
                                @JsonProperty(value = "versionId") String versionId,
                                @JsonProperty(value = "path") String path,
                                @JsonProperty(value = "type") String type,
                                @JsonProperty(value = "fileGeneration") FileGeneration fileGeneration)
    {
        super(groupId, artifactId);
        this.versionId = versionId;
        this.file = fileGeneration;
        this.path = path;
        this.type = type;
    }

    @Override
    public String getId()
    {
        return id;
    }

    public String getVersionId()
    {
        return versionId;
    }

    public String getPath()
    {
        return path;
    }

    public String getType()
    {
        return type;
    }

    public FileGeneration getFile()
    {
        return file;
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
