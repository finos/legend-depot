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

package org.finos.legend.depot.core.services.api.tracing.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.finos.legend.opentracing.AuthenticationProvider;

public class OpenTracingConfiguration
{

    @JsonProperty
    private boolean enabled = false;

    @JsonProperty
    private String openTracingUri;

    @JsonProperty
    private String serviceName;

    @JsonProperty
    private AuthenticationProvider authenticationProvider;

    public String getOpenTracingUri()
    {
        return openTracingUri;
    }

    public void setOpenTracingUri(String openTracingUri)
    {
        this.openTracingUri = openTracingUri;
    }

    public String getServiceName()
    {
        return serviceName;
    }

    public void setServiceName(String serviceName)
    {
        this.serviceName = serviceName;
    }

    public boolean isEnabled()
    {
        return enabled;
    }

    public void setEnabled(boolean enabled)
    {
        this.enabled = enabled;
    }

    public AuthenticationProvider getAuthenticationProvider()
    {
        return authenticationProvider;
    }

    public void setAuthenticationProvider(AuthenticationProvider authenticationProvider)
    {
        this.authenticationProvider = authenticationProvider;
    }
}
