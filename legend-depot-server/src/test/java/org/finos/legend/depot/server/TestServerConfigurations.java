// Copyright 2026 Goldman Sachs
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package org.finos.legend.depot.server;

import io.dropwizard.configuration.FileConfigurationSourceProvider;
import io.dropwizard.configuration.SubstitutingSourceProvider;
import io.dropwizard.core.setup.Bootstrap;
import io.dropwizard.testing.ResourceHelpers;
import org.apache.commons.text.StringSubstitutor;
import org.finos.legend.depot.server.configuration.DepotServerConfiguration;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.net.URISyntaxException;
import java.util.Map;

public class TestServerConfigurations
{
    private static final Map<String, String> DOCKER_ENVIRONMENT = Map.of(
            "DEPOT_PORT", "8080",
            "MONGODB_URI", "mongodb://localhost:27017",
            "MONGODB_NAME", "legend-depot",
            "MONGO_SESSION_ENABLED", "false",
            "GITLAB_APP_ID", "app-id",
            "GITLAB_APP_SECRET", "app-secret",
            "GITLAB_HOST", "gitlab.example.com");

    private static DepotServerConfiguration parse(String path) throws Exception
    {
        Bootstrap<DepotServerConfiguration> bootstrap = new Bootstrap<>(new LegendDepotServer());
        bootstrap.getApplication().initialize(bootstrap);
        StringSubstitutor substitutor = new StringSubstitutor(DOCKER_ENVIRONMENT);
        substitutor.setEnableUndefinedVariableException(true);
        return bootstrap.getConfigurationFactoryFactory()
                .create(DepotServerConfiguration.class, bootstrap.getValidatorFactory().getValidator(), bootstrap.getObjectMapper(), "dw")
                .build(new SubstitutingSourceProvider(new FileConfigurationSourceProvider(), substitutor), path);
    }

    private static String dockerConfig() throws URISyntaxException
    {
        return new File(LegendDepotServer.class.getResource("/docker/config/config.json").toURI()).getPath();
    }

    @Test
    public void sampleConfigParses() throws Exception
    {
        Assertions.assertNotNull(parse(ResourceHelpers.resourceFilePath("sample-server-config.json")).getPac4jConfiguration());
    }

    @Test
    public void dockerConfigParses() throws Exception
    {
        DepotServerConfiguration configuration = parse(dockerConfig());
        Assertions.assertEquals("mongodb://localhost:27017", configuration.getPac4jConfiguration().getMongoUri());
    }
}
