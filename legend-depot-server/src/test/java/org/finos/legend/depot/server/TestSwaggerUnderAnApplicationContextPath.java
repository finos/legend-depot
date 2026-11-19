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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import de.bwaldvogel.mongo.MongoServer;
import de.bwaldvogel.mongo.backend.memory.MemoryBackend;
import io.dropwizard.testing.ResourceHelpers;
import io.dropwizard.testing.junit5.DropwizardAppExtension;
import io.dropwizard.testing.junit5.DropwizardExtensionsSupport;
import jakarta.ws.rs.core.Response;
import org.finos.legend.depot.server.configuration.DepotServerConfiguration;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.io.File;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;

@ExtendWith(DropwizardExtensionsSupport.class)
public class TestSwaggerUnderAnApplicationContextPath
{
    private static final MongoServer MONGO = new MongoServer(new MemoryBackend());
    private static final DropwizardAppExtension<DepotServerConfiguration> APP = new DropwizardAppExtension<>(LegendDepotServer.class, bootConfig());

    private static String bootConfig()
    {
        try
        {
            ObjectMapper mapper = new ObjectMapper();
            ObjectNode config = (ObjectNode) mapper.readTree(new File(ResourceHelpers.resourceFilePath("sample-server-config.json")));
            ((ObjectNode) config.at("/server")).put("applicationContextPath", "/legend");
            ((ObjectNode) config.at("/server/connector")).put("port", 0);
            ((ObjectNode) config.at("/swagger")).remove("uriPrefix");
            InetSocketAddress mongo = MONGO.bind();
            ((ObjectNode) config.at("/storages/0/org.finos.legend.depot.store.mongo.core.MongoConfiguration")).put("url", "mongodb://" + mongo.getHostString() + ":" + mongo.getPort());
            Path file = Files.createTempFile("depot-server-context-path-config", ".json");
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

    private Response get(String path)
    {
        return APP.client().target("http://localhost:" + APP.getLocalPort() + path).request().get();
    }

    @Test
    public void openApiSpecNamesTheContextPathAndUrlPatternAsItsServer()
    {
        Response response = get("/legend/depot/api/openapi.json");
        Assertions.assertEquals(200, response.getStatus());
        Assertions.assertEquals("/legend/depot/api", response.readEntity(JsonNode.class).at("/servers/0/url").asText());
    }

    @Test
    public void swaggerUiAndItsAssetsAreServedWhereThePageLinksThem()
    {
        Response page = get("/legend/depot/api/swagger");
        Assertions.assertEquals(200, page.getStatus());
        String html = page.readEntity(String.class);
        Assertions.assertTrue(html.contains("\"/legend/depot/api/swagger-static/swagger-ui-bundle.js\""), html);
        Assertions.assertTrue(html.contains("\"/legend/depot/api/openapi.json\""), html);
        Assertions.assertEquals(200, get("/legend/depot/api/swagger-static/swagger-ui-bundle.js").getStatus());
        Assertions.assertEquals(200, get("/legend/depot/api/oauth2-redirect.html").getStatus());
    }
}
