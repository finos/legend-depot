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
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotNull;
import java.util.List;

public class IncludeProjectPropertiesConfiguration
{
    @JsonProperty
    private final List<String> properties;

    @JsonProperty
    private final List<String> manifestProperties;

    @JsonCreator
    public IncludeProjectPropertiesConfiguration(@JsonProperty("properties") List<String> properties, @JsonProperty("manifestProperties") List<String> manifestProperties)
    {
        this.properties = properties;
        this.manifestProperties = manifestProperties;
    }

    public List<String> getProperties()
    {
        return properties;
    }

    public List<String> getManifestProperties()
    {
        return manifestProperties;
    }
}
