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

package org.finos.legend.depot.store.resources.entities;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.eclipse.collections.api.tuple.Pair;
import org.finos.legend.depot.core.authorisation.api.AuthorisationProvider;
import org.finos.legend.depot.core.authorisation.resources.BaseAuthorisedResource;
import org.finos.legend.depot.domain.api.MetadataEventResponse;
import org.finos.legend.depot.domain.entity.StoredEntity;
import org.finos.legend.depot.services.api.entities.ManageEntitiesService;
import org.finos.legend.depot.tracing.resources.ResourceLoggingAndTracing;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.security.Principal;
import java.util.List;

@Path("")
@Api("Entities")
public class ManageEntitiesResource extends BaseAuthorisedResource
{

    private final ManageEntitiesService entitiesService;


    @Inject
    public ManageEntitiesResource(ManageEntitiesService manageEntitiesService, AuthorisationProvider authorisationProvider, @Named("requestPrincipal") Provider<Principal> principalProvider)
    {
        super(authorisationProvider, principalProvider);
        this.entitiesService = manageEntitiesService;
    }

    @Override
    protected String getResourceName()
    {
        return "Entities";
    }

    @GET
    @Path("/projects/{groupId}/{artifactId}/versions/entities")
    @ApiOperation(ResourceLoggingAndTracing.GET_VERSION_STORE_ENTITIES)
    @Produces(MediaType.APPLICATION_JSON)
    public List<StoredEntity> getEntities(@PathParam("groupId") String groupId, @PathParam("artifactId") String artifactId)
    {

        return handle(ResourceLoggingAndTracing.GET_VERSION_STORE_ENTITIES, () -> entitiesService.getStoredEntities(groupId, artifactId));
    }

    @GET
    @Path("/projects/{groupId}/{artifactId}/versions/{versionId}/entities")
    @ApiOperation(ResourceLoggingAndTracing.GET_VERSION_STORE_ENTITIES)
    @Produces(MediaType.APPLICATION_JSON)
    public List<StoredEntity> getEntities(@PathParam("groupId") String groupId, @PathParam("artifactId") String artifactId, @PathParam("versionId") String versionId)
    {

        return handle(ResourceLoggingAndTracing.GET_VERSION_STORE_ENTITIES, () -> entitiesService.getStoredEntities(groupId, artifactId, versionId));
    }


    @DELETE
    @Path("/projects/{groupId}/{artifactId}/entities")
    @ApiOperation(ResourceLoggingAndTracing.DELETE_STORE_ENTITIES)
    @Produces(MediaType.APPLICATION_JSON)
    public MetadataEventResponse deleteEntities(@PathParam("groupId") String groupId, @PathParam("artifactId") String artifactId)
    {

        return handle(
                ResourceLoggingAndTracing.DELETE_STORE_ENTITIES,
                ResourceLoggingAndTracing.DELETE_STORE_ENTITIES + groupId + artifactId,
                () ->
                {
                    validateUser();
                    return entitiesService.deleteAll(groupId, artifactId);
                });
    }

    @GET
    @Path("/projects/orphan/entities")
    @ApiOperation(ResourceLoggingAndTracing.ORPHAN_STORE_ENTITIES)
    @Produces(MediaType.APPLICATION_JSON)
    public List<Pair<String, String>> getOrphanEntities()
    {

        return handle(ResourceLoggingAndTracing.ORPHAN_STORE_ENTITIES, () -> entitiesService.getOrphanedStoredEntities());
    }

}