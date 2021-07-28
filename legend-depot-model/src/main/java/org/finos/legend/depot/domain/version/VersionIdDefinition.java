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

package org.finos.legend.depot.domain.version;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.finos.legend.sdlc.domain.model.version.VersionId;

public class VersionIdDefinition extends VersionId
{

    @JsonProperty
    public int patchVersion;
    @JsonProperty
    public int minorVersion;
    @JsonProperty
    public int majorVersion;


    @JsonCreator
    public VersionIdDefinition(@JsonProperty("majorVersion") int majorVersion, @JsonProperty("minorVersion") int minorVersion, @JsonProperty("patchVersion") int patchVersion)
    {
        this.patchVersion = patchVersion;
        this.minorVersion = minorVersion;
        this.majorVersion = majorVersion;
    }

    @Override
    public int getPatchVersion()
    {
        return patchVersion;
    }

    @Override
    public int getMinorVersion()
    {
        return minorVersion;
    }

    @Override
    public int getMajorVersion()
    {
        return majorVersion;
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
