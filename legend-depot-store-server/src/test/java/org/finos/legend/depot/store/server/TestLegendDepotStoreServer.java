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

package org.finos.legend.depot.store.server;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.model.Filters;
import de.bwaldvogel.mongo.MongoServer;
import de.bwaldvogel.mongo.backend.memory.MemoryBackend;
import io.dropwizard.testing.ResourceHelpers;
import io.dropwizard.testing.junit5.DropwizardAppExtension;
import io.dropwizard.testing.junit5.DropwizardExtensionsSupport;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.Response;
import org.eclipse.jetty.ee10.servlet.FilterMapping;
import org.finos.legend.depot.domain.notifications.MetadataNotification;
import org.finos.legend.depot.store.mongo.notifications.NotificationsMongo;
import org.finos.legend.depot.store.mongo.projects.ProjectsMongo;
import org.finos.legend.depot.store.server.configuration.DepotStoreServerConfiguration;
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
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@ExtendWith(DropwizardExtensionsSupport.class)
public class TestLegendDepotStoreServer
{
    private static final MongoServer MONGO = new MongoServer(new MemoryBackend());
    private static final DropwizardAppExtension<DepotStoreServerConfiguration> APP = new DropwizardAppExtension<>(LegendDepotStoreServer.class, bootConfig());

    private static String bootConfig()
    {
        try
        {
            ObjectMapper mapper = new ObjectMapper();
            ObjectNode config = (ObjectNode) mapper.readTree(new File(ResourceHelpers.resourceFilePath("sample-server-config.json")));
            ((ObjectNode) config.at("/server/connector")).put("port", 0);
            InetSocketAddress mongo = MONGO.bind();
            ((ObjectNode) config.at("/storages/0/org.finos.legend.depot.store.mongo.core.MongoConfiguration")).put("url", "mongodb://" + mongo.getHostString() + ":" + mongo.getPort());
            ((ObjectNode) config.at("/artifactRepositoryProviderConfiguration/org.finos.legend.depot.services.artifacts.repository.maven.MavenArtifactRepositoryConfiguration"))
                    .put("settingsLocation", ResourceHelpers.resourceFilePath("sample-repository-settings.xml"));
            ((ObjectNode) config.at("/pac4j")).putArray("clients").addObject().putObject("org.pac4j.core.client.direct.AnonymousClient");
            Path file = Files.createTempFile("depot-store-server-config", ".json");
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
    public static void storeANotification()
    {
        InetSocketAddress mongo = MONGO.getLocalAddress();
        try (MongoClient client = MongoClients.create("mongodb://" + mongo.getHostString() + ":" + mongo.getPort()))
        {
            new NotificationsMongo(client.getDatabase("depot-dev")).insert(new MetadataNotification("PROD-1", "org.finos.test", "dated", "1.0.0").complete().setUpdated(new Date()));
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
    public void infoFromAPrivateModuleResolves()
    {
        Assertions.assertEquals(200, get("/depot-store/api/info").getStatus());
    }

    @Test
    public void guiceBoundResourceSeesThePac4jPrincipal()
    {
        // the request-scoped principal reaches the resource, which then refuses it: the anonymous user holds no roles
        JsonNode error = get("/depot-store/api/schedules").readEntity(JsonNode.class);
        Assertions.assertTrue(error.get("message").asText().endsWith("User [anonymous] not authorised for role [Schedules]"), error.toString());
    }

    @Test
    public void principalWithTheRoleCanWrite()
    {
        // the test authorisedIdentities.json grants the anonymous user the Projects role, and no other
        Response response = APP.client().target("http://localhost:" + APP.getLocalPort() + "/depot-store/api/projects/PROD-1/org.finos.test/write-check")
                .queryParam("defaultBranch", "master")
                .request()
                .put(Entity.json(""));
        String body = response.readEntity(String.class);
        Assertions.assertEquals(200, response.getStatus(), body);
        Assertions.assertTrue(body.contains("\"projectId\":\"PROD-1\""), body);
        // the store has no read endpoint for projects, so look in the database
        InetSocketAddress mongo = MONGO.getLocalAddress();
        try (MongoClient client = MongoClients.create("mongodb://" + mongo.getHostString() + ":" + mongo.getPort()))
        {
            Assertions.assertEquals(1, client.getDatabase("depot-dev").getCollection(ProjectsMongo.COLLECTION).countDocuments(Filters.eq("artifactId", "write-check")));
        }
    }

    @Test
    public void configIsServed() throws Exception
    {
        Response response = get("/depot-store/api/config");
        Assertions.assertEquals(200, response.getStatus());
        JsonNode config = new ObjectMapper().readTree(response.readEntity(String.class));
        Assertions.assertEquals("/depot-store/api/*", config.get("urlPattern").asText());
        Assertions.assertEquals("AnonymousClient", config.at("/pac4j/clients/0/name").asText());
    }

    @Test
    public void swaggerUiIsServed()
    {
        Assertions.assertEquals(200, get("/depot-store/api/swagger").getStatus());
    }

    @Test
    public void openApiSpecNamesTheUrlPatternAsItsServer()
    {
        Response response = get("/depot-store/api/openapi.json");
        Assertions.assertEquals(200, response.getStatus());
        JsonNode spec = response.readEntity(JsonNode.class);
        Assertions.assertEquals("/depot-store/api", spec.at("/servers/0/url").asText());
        Assertions.assertTrue(spec.at("/paths").has("/schedules"));
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
    public void anEncodedPathSeparatorReachesJersey() throws Exception
    {
        URI uri = URI.create("http://localhost:" + APP.getLocalPort() + "/depot-store/api/no-such-resource/%2Fmodel%2FThing.json");
        HttpResponse<String> response = HttpClient.newHttpClient().send(HttpRequest.newBuilder(uri).build(), HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(404, response.statusCode(), response.body());
        Assertions.assertTrue(APP.getEnvironment().getApplicationContext().getServletHandler().isDecodeAmbiguousURIs());
    }

    @Test
    public void datesAreWrittenInTheStoreDateFormat()
    {
        JsonNode notifications = get("/depot-store/api/notifications?groupId=org.finos.test&artifactId=dated").readEntity(JsonNode.class);
        Assertions.assertEquals(1, notifications.size(), notifications.toString());
        Assertions.assertTrue(notifications.get(0).get("updated").asText().matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}"), notifications.toString());
    }
}
