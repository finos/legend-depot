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

package org.finos.legend.depot.store.model.admin.artifacts;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.finos.legend.depot.store.model.HasIdentifier;

import jakarta.validation.constraints.NotNull;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ArtifactFile implements HasIdentifier
{
    @JsonProperty
    @NotNull
    private String checkSum;
    @JsonProperty
    @NotNull
    private String path;

    public ArtifactFile()
    {
    }

    public ArtifactFile(String path, String checkSum)
    {
        this.path = path;
        this.checkSum = checkSum;
    }

    public String getCheckSum()
    {
        return checkSum;
    }

    public ArtifactFile setCheckSum(String checkSum)
    {
        this.checkSum = checkSum;
        return this;
    }

    public String getPath()
    {
        return this.path;
    }

    public ArtifactFile setPath(String newPath)
    {
        this.path = newPath;
        return this;
    }

    @Override
    @JsonIgnore
    public String getId()
    {
        return null;
    }
}
