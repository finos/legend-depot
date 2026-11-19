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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.finos.legend.depot.core.services.api.authorisation.AuthorisationProvider;
import org.finos.legend.depot.core.services.authorisation.resources.AuthorisedResource;
import org.finos.legend.depot.store.mongo.admin.CoreDataMigrations;

import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.inject.Provider;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;
import java.security.Principal;

@Path("")
@Tag(name = "Mongo Store Administration")
public class CoreDataStoreMigrationsResource extends AuthorisedResource
{

    public static final String STORE_ADMINISTRATION_RESOURCE = "Store Administration";
    private final CoreDataMigrations mongoMigrations;


    @Inject
    protected CoreDataStoreMigrationsResource(CoreDataMigrations mongoMigrations,
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


    @PUT
    @Path("/migrations/migrateToVersionData")
    @Operation(summary = "migrate data from projects to store projects versions data")
    @Deprecated
    public Response migrationToProjectVersionData()
    {
        return handle("migrate data from projects to store projects versions data", () ->
        {
            validateUser();
            mongoMigrations.migrationToProjectVersions();
            return Response.status(Response.Status.NO_CONTENT).build();
        });
    }

    @PUT
    @Path("/migrations/cleanupProjectData")
    @Operation(summary = "cleanup projects data")
    @Deprecated
    public Response cleanupProjectData()
    {
        return handle("cleanup projects data", () ->
        {
            validateUser();
            mongoMigrations.cleanUpProjectData();
            return Response.status(Response.Status.NO_CONTENT).build();
        });
    }

    @PUT
    @Path("/migrations/calculateDependenciesForVersions/all")
    @Operation(summary = "calculating transitive dependencies of each version present in the store")
    @Deprecated
    public Response storeTransitiveDependenciesForVersions()
    {
        return handle("calculating transitive dependencies of each version present in the store", () ->
        {
            validateUser();
            mongoMigrations.calculateTransitiveDependenciesForAllProjectVersions();
            return Response.status(Response.Status.NO_CONTENT).build();
        });
    }

    @PUT
    @Path("/migrations/addTransitiveDependenciesToVersionData")
    @Operation(summary = "Update versions collection with transitive dependencies")
    @Deprecated
    public Response addTransitiveDependenciesToVersionData()
    {
        return handle("Update versions collection with transitive dependencies", () ->
        {
            validateUser();
            mongoMigrations.addTransitiveDependenciesToVersionData();
            return Response.status(Response.Status.NO_CONTENT).build();
        });
    }

    @PUT
    @Path("/migrations/addLatestVersionToProjectData")
    @Operation(summary = "Update project configurations with latest version")
    @Deprecated
    public Response addLatestVersionToProjectData()
    {
        return handle("Update project configurations with latest version", () ->
        {
            validateUser();
            mongoMigrations.addLatestVersionToProjectData();
            return Response.status(Response.Status.NO_CONTENT).build();
        });
    }

}
