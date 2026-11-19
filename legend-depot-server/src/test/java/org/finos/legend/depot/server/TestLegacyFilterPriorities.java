//  Copyright 2026 Goldman Sachs
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

package org.finos.legend.depot.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import de.bwaldvogel.mongo.MongoServer;
import de.bwaldvogel.mongo.backend.memory.MemoryBackend;
import io.dropwizard.testing.ResourceHelpers;
import io.dropwizard.testing.junit5.DropwizardAppExtension;
import io.dropwizard.testing.junit5.DropwizardExtensionsSupport;
import org.eclipse.jetty.ee10.servlet.FilterMapping;
import org.finos.legend.depot.server.configuration.DepotServerConfiguration;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import ru.vyarus.dropwizard.guice.module.jersey.GuiceWebModule;

import java.io.File;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@ExtendWith(DropwizardExtensionsSupport.class)
public class TestLegacyFilterPriorities
{
    private static final MongoServer MONGO = new MongoServer(new MemoryBackend());
    private static final DropwizardAppExtension<DepotServerConfiguration> APP = new DropwizardAppExtension<>(LegendDepotServer.class, bootConfig());

    private static String bootConfig()
    {
        try
        {
            ObjectMapper mapper = new ObjectMapper();
            ObjectNode config = (ObjectNode) mapper.readTree(new File(ResourceHelpers.resourceFilePath("sample-server-config.json")));
            ((ObjectNode) config.at("/server/connector")).put("port", 0);
            InetSocketAddress mongo = MONGO.bind();
            ((ObjectNode) config.at("/storages/0/org.finos.legend.depot.store.mongo.core.MongoConfiguration")).put("url", "mongodb://" + mongo.getHostString() + ":" + mongo.getPort());
            ObjectNode priorities = config.putObject("filterPriorities");
            priorities.put("Username", 1);
            priorities.put("OpenTracingFilter", 2);
            priorities.put("org.pac4j.j2e.filter.SecurityFilter", 3);
            priorities.put("org.pac4j.j2e.filter.CallbackFilter", 4);
            priorities.put("CORS", 5);
            Path file = Files.createTempFile("depot-server-legacy-config", ".json");
            file.toFile().deleteOnExit();
            mapper.writeValue(file.toFile(), config);
            return file.toString();
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    @AfterAll
    public static void stopMongo()
    {
        MONGO.shutdownNow();
    }

    @Test
    public void legacyPac4jNamesKeepTheSecurityFilterAheadOfUsernameAndGuice()
    {
        // ChainFixingFilterHandler reorders the chain on the first request
        Assertions.assertEquals(200, APP.client().target("http://localhost:" + APP.getLocalPort() + "/depot/api/info").request().get().getStatus());
        List<String> filters = Arrays.stream(APP.getEnvironment().getApplicationContext().getServletHandler().getFilterMappings())
                .map(FilterMapping::getFilterName)
                .collect(Collectors.toList());
        int callback = filters.indexOf("org.pac4j.jee.filter.CallbackFilter");
        int security = filters.indexOf("org.pac4j.jee.filter.SecurityFilter");
        int username = filters.indexOf("Username");
        int guice = filters.indexOf(GuiceWebModule.GUICE_FILTER);
        Assertions.assertTrue(callback >= 0 && callback < security && security < username && username < guice, "filter order: " + filters);
    }
}
