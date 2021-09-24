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

package org.finos.legend.depot.artifacts.repository.one.unsecured;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.finos.legend.depot.artifacts.repository.api.ArtifactRepository;
import org.finos.legend.depot.artifacts.repository.api.ArtifactRepositoryProviderConfiguration;

import javax.validation.constraints.NotNull;

public class OneUnsecuredArtifactRepositoryConfiguration extends ArtifactRepositoryProviderConfiguration
{

    @NotNull
    @JsonProperty
    private String settingsLocation;

    @JsonCreator
    public OneUnsecuredArtifactRepositoryConfiguration(@JsonProperty("settingsLocation") String settingsLocation)
    {
        super("OneUnsecuredArtifactRepository");
        this.settingsLocation = settingsLocation;
    }

    public String getSettingsLocation()
    {
        return settingsLocation;
    }

    @Override
    public ArtifactRepository initialiseArtifactRepositoryProvider()
    {
        return new ArtifactRepositoryOneUnsecured(this);
    }

    @Override
    public String toString()
    {
        return "OneUnsecuredArtifactRepositoryConfiguration{" +
                "name='" + super.getName() + '\'' +
                "settings='" + this.settingsLocation + '\'' +
                '}';
    }
}
