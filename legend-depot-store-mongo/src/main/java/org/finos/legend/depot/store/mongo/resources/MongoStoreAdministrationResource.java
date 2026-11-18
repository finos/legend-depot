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

package org.finos.legend.depot.store.mongo.resources;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.bson.Document;
import org.finos.legend.depot.core.services.api.authorisation.AuthorisationProvider;
import org.finos.legend.depot.core.services.authorisation.resources.AuthorisedResource;
import org.finos.legend.depot.store.mongo.admin.MongoAdminStore;
import org.finos.legend.depot.store.mongo.admin.metrics.StorageMetricsHandler;

import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.inject.Provider;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.security.Principal;
import java.util.List;
import java.util.Map;

@Path("")
@Tag(name = "Mongo Store Administration")
public class MongoStoreAdministrationResource extends AuthorisedResource
{

    public static final String STORE_ADMINISTRATION_RESOURCE = "Store Administration";
    private final MongoAdminStore manageStoreService;
    private final StorageMetricsHandler storageMetrics;

    @Inject
    protected MongoStoreAdministrationResource(MongoAdminStore manageStoreService,
                                               AuthorisationProvider authorisationProvider,
                                               @Named("requestPrincipal") Provider<Principal> principalProvider, StorageMetricsHandler storageMetrics)
    {
        super(authorisationProvider, principalProvider);
        this.manageStoreService = manageStoreService;
        this.storageMetrics = storageMetrics;
    }

    @Override
    protected String getResourceName()
    {
        return STORE_ADMINISTRATION_RESOURCE;
    }


    @GET
    @Path("/indexes")
    @Operation(summary = "get indexes")
    public Map<String,List<Document>> getIndexed()
    {
        return handle("Get indexes", this::getAllIndexes);
    }


    @PUT
    @Path("/indexes")
    @Operation(summary = "createIndexes if absent")
    public List<String> createIndexesIfAbsent()
    {
        return handle("Create indexes", this::createIndexes);
    }

    private List<String> createIndexes()
    {
        validateUser();
        return manageStoreService.createIndexes();
    }


    @DELETE
    @Path("/indexes/{index}/{collection}")
    @Operation(summary = "remove")
    public boolean remove(@PathParam("index") String index, @PathParam("collection") String collection)
    {
        return handle("delete index", () ->
        {
            validateUser();
            manageStoreService.deleteIndex(collection, index);
            return true;
        });
    }

    @GET
    @Path("/collections/stats")
    @Operation(summary = "get collections stats")
    public Object getCollectionStats()
    {
        return handle("Get collections stats", () -> this.storageMetrics.reportMetrics());
    }


    @GET
    @Path("/collections")
    @Operation(summary = "get collections")
    public List<String> getCollections()
    {
        validateUser();
        return handle("Get collections", this::getAllCollections);
    }

    @DELETE
    @Path("/collections/{id}")
    @Operation(summary = "delete collection")
    public Response deleteCollections(@PathParam("id") String collId)
    {
        return handle("delete collection", () ->
        {
            validateUser();
            manageStoreService.deleteCollection(collId);
            return Response.status(Response.Status.NO_CONTENT).build();
        });
    }

    private List<String> getAllCollections()
    {
        return manageStoreService.getAllCollections();
    }

    private Map<String, List<Document>> getAllIndexes()
    {
        return manageStoreService.getAllIndexes();
    }

    @GET
    @Path("/collections/pipeline/{collectionName}")
    @Operation(summary = "run collection pipeline")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response runPipeline(@PathParam("collectionName") String collectionMame, @QueryParam("pipeline") String jsonPipeline) throws JsonProcessingException
    {
        validateUser();
        return Response.ok().entity(this.manageStoreService.runPipeline(collectionMame,jsonPipeline)).build();
    }

}
