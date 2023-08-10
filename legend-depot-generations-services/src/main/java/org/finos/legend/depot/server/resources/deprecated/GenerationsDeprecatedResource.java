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

package org.finos.legend.depot.server.resources.deprecated;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.finos.legend.depot.domain.generation.DepotGeneration;
import org.finos.legend.depot.services.api.generations.FileGenerationsService;
import org.finos.legend.depot.tracing.resources.BaseResource;
import org.finos.legend.sdlc.domain.model.entity.Entity;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.Optional;

import static org.finos.legend.depot.domain.version.VersionValidator.BRANCH_SNAPSHOT;

@Path("")
@Api("Deprecated")
public class GenerationsDeprecatedResource extends BaseResource
{
    private static final String GET_REVISION_FILE_GENERATION_ENTITIES = "get revision generation entities deprecated";
    private static final String GET_REVISION_FILE_GENERATION = "get revision file generations deprecated";
    private static final String GET_REVISION_FILE_GENERATION_BY_ELEMENT_PATH = "get revision file generations by element path deprecated";
    private static final String GET_REVISION_FILE_GENERATION_BY_FILEPATH = "get revision file generations by file deprecated";
    private final FileGenerationsService generationsService;

    @Inject
    public GenerationsDeprecatedResource(FileGenerationsService generationsService)
    {
        this.generationsService = generationsService;
    }

    @GET
    @Path("/projects/{groupId}/{artifactId}/latest/generations")
    @ApiOperation(value = GET_REVISION_FILE_GENERATION_ENTITIES,notes = "replaced by: /projects/{groupId}/{artifactId}/master-SNAPSHOT/generations",tags = "_Deprecated: remove by Q1 2024")
    @Produces(MediaType.APPLICATION_JSON)
    @Deprecated
    public List<Entity> getLatestGenerations(@PathParam("groupId") String groupId,
                                             @PathParam("artifactId") String artifactId)
    {
        return handle(GET_REVISION_FILE_GENERATION_ENTITIES, () -> this.generationsService.getGenerations(groupId, artifactId, BRANCH_SNAPSHOT("master")));
    }


    @GET
    @Path("/generations/{groupId}/{artifactId}/latest")
    @ApiOperation(value = GET_REVISION_FILE_GENERATION, notes = "replaced by: /generations/{groupId}/{artifactId}/versions/master-SNAPSHOT",tags = "_Deprecated: remove by Q1 2024")
    @Produces(MediaType.APPLICATION_JSON)
    @Deprecated
    public List<DepotGeneration> getLatestFileGenerations(@PathParam("groupId") String groupId,
                                                          @PathParam("artifactId") String artifactId)
    {
        return handle(GET_REVISION_FILE_GENERATION, () -> this.generationsService.getFileGenerations(groupId, artifactId,BRANCH_SNAPSHOT("master")));
    }

    @GET
    @Path("/generations/{groupId}/{artifactId}/latest/{elementPath}")
    @ApiOperation(value = GET_REVISION_FILE_GENERATION_BY_ELEMENT_PATH, notes = " deprecated use /generations/{groupId}/{artifactId}/versions/master-SNAPSHOT/{elementPath}",tags = "_Deprecated: remove by Q1 2024")
    @Produces(MediaType.APPLICATION_JSON)
    @Deprecated
    public List<DepotGeneration> getLatestFileGenerationsByElementPath(@PathParam("groupId") String groupId,
                                                                       @PathParam("artifactId") String artifactId,
                                                                       @PathParam("elementPath") String elementPath)
    {
        return handle(GET_REVISION_FILE_GENERATION_BY_ELEMENT_PATH, () -> this.generationsService.getFileGenerationsByElementPath(groupId, artifactId, BRANCH_SNAPSHOT("master"),elementPath));
    }

    @GET
    @Path("/generations/{groupId}/{artifactId}/latest/file/{filePath}")
    @ApiOperation(value = GET_REVISION_FILE_GENERATION_BY_FILEPATH, notes = "replaced by: /generations/{groupId}/{artifactId}/versions/master-SNAPSHOT/file/{filePath}",tags = "_Deprecated: remove by Q1 2024")
    @Produces(MediaType.APPLICATION_JSON)
    @Deprecated
    public Optional<DepotGeneration> getLatestFileGenerationsByFilePath(@PathParam("groupId") String groupId,
                                                                        @PathParam("artifactId") String artifactId,
                                                                        @PathParam("filePath") String filePath)
    {
        return handle(GET_REVISION_FILE_GENERATION_BY_FILEPATH, () -> this.generationsService.getFileGenerationsByFilePath(groupId, artifactId, BRANCH_SNAPSHOT("master"),filePath));
    }

}
