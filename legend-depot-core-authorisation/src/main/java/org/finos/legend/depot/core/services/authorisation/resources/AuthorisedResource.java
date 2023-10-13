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

package org.finos.legend.depot.core.services.authorisation.resources;

import org.finos.legend.depot.core.services.api.authorisation.AuthorisationProvider;
import org.finos.legend.depot.core.services.tracing.resources.TracingResource;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import java.security.Principal;

public abstract class AuthorisedResource extends TracingResource
{
    private final AuthorisationProvider authorisationProvider;
    private final Provider<Principal> principalProvider;

    @Inject
    public AuthorisedResource(AuthorisationProvider authorisationProvider, @Named("requestPrincipal") Provider<Principal> principalProvider)
    {
        this.authorisationProvider = authorisationProvider;
        this.principalProvider = principalProvider;
    }

    protected abstract String getResourceName();

    protected void validateUser()
    {
        this.authorisationProvider.authorise(principalProvider, getResourceName());
    }
}
