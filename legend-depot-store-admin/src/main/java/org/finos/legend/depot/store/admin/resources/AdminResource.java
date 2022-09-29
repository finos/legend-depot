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

package org.finos.legend.depot.store.admin.resources;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.bson.Document;
import org.finos.legend.depot.core.authorisation.api.AuthorisationProvider;
import org.finos.legend.depot.core.authorisation.resources.BaseAuthorisedResource;
import org.finos.legend.depot.store.admin.api.ManageStoreService;
import org.finos.legend.depot.store.api.entities.UpdateEntities;
import org.finos.legend.depot.store.api.generation.file.UpdateFileGenerations;
import org.finos.legend.depot.store.api.projects.UpdateProjects;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import java.security.Principal;
import java.util.List;

@Path("")
@Api("Store Administration")
public class AdminResource extends BaseAuthorisedResource
{

    public static final String STORE_ADMINISTRATION_RESOURCE = "Store Administration";
    private final UpdateProjects projects;
    private final UpdateEntities entitiesService;
    private final UpdateFileGenerations fileGenerations;
    private final ManageStoreService manageStoreService;

    @Inject
    protected AdminResource(UpdateProjects projects,
                            UpdateEntities entities,
                            UpdateFileGenerations fileGenerations,
                            ManageStoreService manageStoreService,
                            AuthorisationProvider authorisationProvider,
                            @Named("requestPrincipal") Provider<Principal> principalProvider)
    {
        super(authorisationProvider, principalProvider);
        this.projects = projects;
        this.entitiesService = entities;
        this.fileGenerations = fileGenerations;
        this.manageStoreService = manageStoreService;
    }

    @Override
    protected String getResourceName()
    {
        return STORE_ADMINISTRATION_RESOURCE;
    }


    @GET
    @Path("/indexes")
    @ApiOperation("get indexes")
    public List<Document> getIndexed()
    {
        return handle("Get indexes", this::getAllIndexes);
    }


    @PUT
    @Path("/indexes")
    @ApiOperation("createIndexes if absent")
    public boolean createIndexesIfAbsent()
    {
        return handle("Create indexes", this::createIndexes);
    }

    private boolean createIndexes()
    {
        validateUser();
        return projects.createIndexesIfAbsent()
                && entitiesService.createIndexesIfAbsent()
                && fileGenerations.createIndexesIfAbsent();
    }


    @DELETE
    @Path("/indexes/{index}/{collection}")
    @ApiOperation("remove")
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
    @Path("/collections")
    @ApiOperation("get collections")
    public List<String> getCollections()
    {
        return handle("Get collections", this::getAllCollections);
    }

    @DELETE
    @Path("/collections/{id}")
    @ApiOperation("delete collection")
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

    private List<Document> getAllIndexes()
    {
        return manageStoreService.getAllIndexes();
    }



}
