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

package org.finos.legend.depot.domain.generation.artifact;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.constraints.NotNull;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.finos.legend.depot.domain.BaseDomain;
import org.finos.legend.depot.domain.HasIdentifier;

public class StoredArtifactGeneration extends BaseDomain implements HasIdentifier
{
    @JsonProperty
    private String id;

    @JsonProperty
    @NotNull
    private String versionId;

    @JsonProperty
    private String generator;

    @JsonProperty
    private ArtifactGeneration artifact;

    @JsonCreator
    public StoredArtifactGeneration(@JsonProperty(value = "groupId") String groupId,
                                    @JsonProperty(value = "artifactId") String artifactId,
                                    @JsonProperty(value = "versionId") String versionId,
                                    @JsonProperty(value = "generator") String generator,
                                    @JsonProperty(value = "artifact") ArtifactGeneration artifact)
    {
        super(groupId, artifactId);
        this.versionId = versionId;
        this.generator = generator;
        this.artifact = artifact;
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

    public String getGenerator()
    {
        return generator;
    }

    public ArtifactGeneration getArtifact()
    {
        return artifact;
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
