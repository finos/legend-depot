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

package org.finos.legend.depot.core.services.authorisation;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.finos.legend.depot.core.services.api.authorisation.AuthorisationProvider;

import javax.inject.Provider;
import java.io.IOException;
import java.net.URL;
import java.security.Principal;
import java.util.List;
import java.util.Map;

public final class BasicAuthorisationProvider implements AuthorisationProvider
{
    private final Map<String, List<String>> authorisedIdentities;

    public BasicAuthorisationProvider()
    {
        URL configFile = this.getClass().getClassLoader().getResource("authorisedIdentities.json");
        if (configFile == null)
        {
            throw new IllegalArgumentException("authorisedIdentities.json not found in classpath");
        }
        try
        {
            authorisedIdentities = new ObjectMapper().readValue(configFile, new TypeReference<Map<String, List<String>>>()
            {});
        }
        catch (IOException e)
        {
            throw new IllegalArgumentException(e);
        }
    }

    BasicAuthorisationProvider(Map<String, List<String>> authorisedIdentities)
    {
        this.authorisedIdentities = authorisedIdentities;
    }

    @Override
    public void authorise(Provider<Principal> principalProvider, String role)
    {
        if (authorisedIdentities.get(role) == null)
        {
            throw new SecurityException(String.format("Unknown role [%s]", role));
        }
        if (authorisedIdentities.get(role).stream().noneMatch(p -> principalProvider.get().getName().equals(p)))
        {
            throw new SecurityException(String.format("User [%s] not authorised for role [%s]", principalProvider.get().getName(), role));
        }
    }
}
