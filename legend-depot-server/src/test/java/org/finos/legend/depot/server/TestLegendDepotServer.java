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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import de.bwaldvogel.mongo.MongoServer;
import de.bwaldvogel.mongo.backend.memory.MemoryBackend;
import io.dropwizard.testing.ResourceHelpers;
import io.dropwizard.testing.junit5.DropwizardAppExtension;
import io.dropwizard.testing.junit5.DropwizardExtensionsSupport;
import jakarta.ws.rs.core.Response;
import org.eclipse.jetty.ee10.servlet.FilterMapping;
import org.finos.legend.depot.domain.generation.DepotGeneration;
import org.finos.legend.depot.server.configuration.DepotServerConfiguration;
import org.finos.legend.depot.store.model.generations.StoredFileGeneration;
import org.finos.legend.depot.store.model.projects.StoreProjectVersionData;
import org.finos.legend.depot.store.mongo.generations.FileGenerationsMongo;
import org.finos.legend.depot.store.mongo.projects.ProjectsVersionsMongo;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import ru.vyarus.dropwizard.guice.module.jersey.GuiceWebModule;

import java.io.File;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@ExtendWith(DropwizardExtensionsSupport.class)
public class TestLegendDepotServer
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
            Path file = Files.createTempFile("depot-server-config", ".json");
            file.toFile().deleteOnExit();
            mapper.writeValue(file.toFile(), config);
            return file.toString();
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    @BeforeAll
    public static void storeAFileGeneration()
    {
        InetSocketAddress mongo = MONGO.getLocalAddress();
        try (MongoClient client = MongoClients.create("mongodb://" + mongo.getHostString() + ":" + mongo.getPort()))
        {
            MongoDatabase database = client.getDatabase("depot-dev");
            new ProjectsVersionsMongo(database).createOrUpdate(new StoreProjectVersionData("org.finos.test", "generated", "1.0.0"));
            new FileGenerationsMongo(database).createOrUpdate(new StoredFileGeneration("org.finos.test", "generated", "1.0.0", "model::Thing", "json",
                    new DepotGeneration("/model/Thing.json", "{\"generated\":true}")));
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

    private HttpResponse<String> getRaw(String path) throws Exception
    {
        return HttpClient.newHttpClient().send(HttpRequest.newBuilder(URI.create("http://localhost:" + APP.getLocalPort() + path)).build(), HttpResponse.BodyHandlers.ofString());
    }

    @Test
    public void infoFromAPrivateModuleResolves()
    {
        Assertions.assertEquals(200, get("/depot/api/info").getStatus());
    }

    @Test
    public void guiceBoundResourceBackedByMongoResolves()
    {
        Response response = get("/depot/api/project-configurations");
        Assertions.assertEquals(200, response.getStatus());
        Assertions.assertTrue(response.readEntity(JsonNode.class).isArray());
    }

    @Test
    public void configIsServed() throws Exception
    {
        Response response = get("/depot/api/config");
        Assertions.assertEquals(200, response.getStatus());
        JsonNode config = new ObjectMapper().readTree(response.readEntity(String.class));
        Assertions.assertEquals("/depot/api/*", config.get("urlPattern").asText());
        Assertions.assertEquals("AnonymousClient", config.at("/pac4j/clients/0/name").asText());
    }

    @Test
    public void swaggerUiIsServed()
    {
        Assertions.assertEquals(200, get("/depot/api/swagger").getStatus());
    }

    @Test
    public void openApiSpecNamesTheUrlPatternAsItsServer()
    {
        Response response = get("/depot/api/openapi.json");
        Assertions.assertEquals(200, response.getStatus());
        JsonNode spec = response.readEntity(JsonNode.class);
        Assertions.assertEquals("/depot/api", spec.at("/servers/0/url").asText());
        Assertions.assertTrue(spec.at("/paths").has("/project-configurations"));
    }

    @Test
    public void prometheusIsServedOnTheAdminContext()
    {
        Assertions.assertEquals(200, APP.client().target("http://localhost:" + APP.getAdminPort() + "/admin/prometheus").request().get().getStatus());
    }

    @Test
    public void prometheusOmitsCreatedSamples()
    {
        String metrics = APP.client().target("http://localhost:" + APP.getAdminPort() + "/admin/prometheus").request().get(String.class);
        List<String> samples = metrics.lines().filter(line -> !line.startsWith("#")).collect(Collectors.toList());
        Assertions.assertTrue(samples.stream().anyMatch(line -> line.matches("\\w+_total(\\{.*})? .*")), metrics);
        Assertions.assertTrue(samples.stream().noneMatch(line -> line.matches("\\w+_created(\\{.*})? .*")), metrics);
    }

    @Test
    public void guiceFilterRunsAfterTheSecurityFilter()
    {
        List<String> filters = Arrays.stream(APP.getEnvironment().getApplicationContext().getServletHandler().getFilterMappings())
                .map(FilterMapping::getFilterName)
                .collect(Collectors.toList());
        int security = filters.indexOf("org.pac4j.jee.filter.SecurityFilter");
        int guice = filters.indexOf(GuiceWebModule.GUICE_FILTER);
        Assertions.assertTrue(security >= 0 && guice > security, "filter order: " + filters);
    }

    @Test
    public void everyFilterPriorityNamesARegisteredFilter()
    {
        // the tracing bundle only registers its OpenTracing filter when tracing is enabled
        List<String> filters = Arrays.stream(APP.getEnvironment().getApplicationContext().getServletHandler().getFilterMappings())
                .map(FilterMapping::getFilterName)
                .collect(Collectors.toList());
        boolean tracing = APP.getConfiguration().getOpenTracingConfiguration().isEnabled();
        APP.getConfiguration().getFilterPriorities().keySet().stream()
                .filter(name -> tracing || !"OpenTracing".equals(name))
                .forEach(name -> Assertions.assertTrue(filters.contains(name), name + " is not among " + filters));
    }

    @Test
    public void fileGenerationIsFoundByAnEncodedFilePath() throws Exception
    {
        HttpResponse<String> response = getRaw("/depot/api/generations/org.finos.test/generated/versions/1.0.0/file/%2Fmodel%2FThing.json");
        Assertions.assertEquals(200, response.statusCode(), response.body());
        Assertions.assertEquals("/model/Thing.json", new ObjectMapper().readTree(response.body()).get("path").asText());
    }

    @Test
    public void fileGenerationContentIsFoundByAnEncodedFilePath() throws Exception
    {
        HttpResponse<String> response = getRaw("/depot/api/generationFileContent/org.finos.test/generated/versions/1.0.0/file/%2Fmodel%2FThing.json");
        Assertions.assertEquals(200, response.statusCode(), response.body());
        Assertions.assertEquals("{\"generated\":true}", response.body());
    }

    @Test
    public void anEncodedFilePathMayNotClimbOutOfTheRoute() throws Exception
    {
        Assertions.assertEquals(400, getRaw("/depot/api/generations/org.finos.test/generated/versions/1.0.0/file/%2F..%2F..%2F..%2F..%2F..%2Finfo").statusCode());
        Assertions.assertEquals(400, getRaw("/depot/api/generationFileContent/org.finos.test/generated/versions/1.0.0/file/%2Fmodel%2F%2e%2e%2FThing.json").statusCode());
    }

    @Test
    public void otherAmbiguousEncodingsAreStillRejected() throws Exception
    {
        Assertions.assertEquals(400, getRaw("/depot/api/generations/org.finos.test/generated/versions/1.0.0/file/%25model%25Thing.json").statusCode());
        Assertions.assertEquals(400, getRaw("/depot/api/generations/org.finos.test/generated/versions/1.0.0/file//model/Thing.json").statusCode());
    }
}
