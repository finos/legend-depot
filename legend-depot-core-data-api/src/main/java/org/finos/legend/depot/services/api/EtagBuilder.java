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

package org.finos.legend.depot.services.api;

import org.finos.legend.depot.domain.version.VersionValidator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class EtagBuilder
{
    private static final String HEAD_PROTOCOL_VERSION = "vX_X_X";
    //can only calculate an eTag if all parameters are non changeable ( ie no aliases, etc)
    private boolean constantParams = true;

    private EtagBuilder()
    {
    }

    public static EtagBuilder create()
    {
        return new EtagBuilder();
    }

    private List<String> params = new ArrayList<>();

    public EtagBuilder withGAV(String groupId, String artifactId, String versionId)
    {
        if (VersionValidator.isSnapshotVersion(versionId) || VersionValidator.isVersionAlias(versionId))
        {
            this.constantParams = false;
        }
        else
        {
            params.addAll(Arrays.asList(groupId, artifactId, versionId));
        }
        return this;
    }

    public EtagBuilder withProtocolVersion(String clientProtocolVersion)
    {
        if (clientProtocolVersion == null || clientProtocolVersion.equalsIgnoreCase(HEAD_PROTOCOL_VERSION))
        {
           this.constantParams = false;
        }
        else
        {
            params.add(clientProtocolVersion);
        }
        return this;
    }

    public EtagBuilder withClassifier(String classifier)
    {
        if (classifier == null)
        {
            this.constantParams = false;
        }
        else
        {
            params.add(classifier);
        }
        return this;
    }

    public String build()
    {
        if (this.constantParams)
        {
            StringBuilder etagBuilder = new StringBuilder();
            params.forEach(param -> etagBuilder.append(param));
            return etagBuilder.toString();
        }
        return null;
    }
}
