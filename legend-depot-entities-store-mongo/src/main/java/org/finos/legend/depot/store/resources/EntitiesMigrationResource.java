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

package org.finos.legend.depot.store.resources;

import com.mongodb.client.result.DeleteResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.finos.legend.depot.core.authorisation.api.AuthorisationProvider;
import org.finos.legend.depot.core.authorisation.resources.BaseAuthorisedResource;
import org.finos.legend.depot.store.mongo.admin.migrations.MongoEntitiesMigrations;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.ws.rs.DELETE;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import java.security.Principal;

@Deprecated
@Path("")
@Api("Mongo Store Administration")
public class EntitiesMigrationResource extends BaseAuthorisedResource
{

    public static final String STORE_ADMINISTRATION_RESOURCE = "Store Administration";
    private final MongoEntitiesMigrations mongoMigrations;


    @Inject
    protected EntitiesMigrationResource(MongoEntitiesMigrations mongoMigrations,
                                           AuthorisationProvider authorisationProvider,
                                           @Named("requestPrincipal") Provider<Principal> principalProvider)
    {
        super(authorisationProvider, principalProvider);
        this.mongoMigrations = mongoMigrations;

    }

    @Override
    protected String getResourceName()
    {
        return STORE_ADMINISTRATION_RESOURCE;
    }

    @DELETE
    @Path("/migrations/deleteVersionedEntities")
    @ApiOperation("Delete versioned entities from entities collection")
    @Deprecated
    public Response deleteVersionedEntities()
    {
        return handle("Delete versioned entities from entities collection", () ->
        {
            validateUser();
            DeleteResult deleteResult = mongoMigrations.deleteVersionedEntities();
            return Response.ok().entity(deleteResult).build();
        });
    }

    @PUT
    @Path("/migrations/migrateToStoredEntityData")
    @ApiOperation("Migrate entities to stored entity data")
    @Deprecated
    public Response migrateEntitiesToStoredEntityData()
    {
        return handle("Migrate entities to stored entity data", () ->
        {
            validateUser();
            mongoMigrations.migrateEntitiesToStoredEntityData();
            return Response.ok().build();
        });
    }
}
