//  Copyright 2022 Goldman Sachs
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

package org.finos.legend.depot.server.resources.artifact;

import static org.finos.legend.depot.tracing.resources.ResourceLoggingAndTracing.GET_ARTIFACT_GENERATIONS;
import static org.finos.legend.depot.tracing.resources.ResourceLoggingAndTracing.GET_ARTIFACT_GENERATIONS_BY_GENERATOR;
import static org.finos.legend.depot.tracing.resources.ResourceLoggingAndTracing.GET_ARTIFACT_GENERATION_BY_PATH;
import static org.finos.legend.depot.tracing.resources.ResourceLoggingAndTracing.GET_ARTIFACT_GENERATION_CONTENT_BY_PATH;
import static org.finos.legend.depot.tracing.resources.ResourceLoggingAndTracing.GET_REVISION_FILE_GENERATION_ENTITIES;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.util.List;
import java.util.Optional;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.finos.legend.depot.domain.generation.artifact.ArtifactGeneration;
import org.finos.legend.depot.services.api.generation.artifact.ArtifactGenerationsService;
import org.finos.legend.depot.store.metrics.QueryMetricsContainer;
import org.finos.legend.depot.tracing.resources.BaseResource;

@Path("artifactGenerations")
@Api("Artifact Generations")
public class ArtifactGenerationsResource extends BaseResource
{
    private final ArtifactGenerationsService artifactGenerationService;

    @Inject
    public ArtifactGenerationsResource(ArtifactGenerationsService artifactGenerationService)
    {
        this.artifactGenerationService = artifactGenerationService;
    }

    @GET
    @Path("/{groupId}/{artifactId}/{versionId}")
    @ApiOperation(GET_ARTIFACT_GENERATIONS)
    @Produces(MediaType.APPLICATION_JSON)
    public List<ArtifactGeneration> getArtifactGenerations(@PathParam("groupId") String groupId, @PathParam("artifactId") String artifactId, @PathParam("versionId") String versionId)
    {
        QueryMetricsContainer.record(groupId, artifactId, versionId);
        return handle(GET_REVISION_FILE_GENERATION_ENTITIES, () -> this.artifactGenerationService.getArtifactGenerations(groupId, artifactId, versionId));
    }

    @GET
    @Path("/{groupId}/{artifactId}/{versionId}/artifact{path}")
    @ApiOperation(GET_ARTIFACT_GENERATION_BY_PATH)
    @Produces(MediaType.APPLICATION_JSON)
    public Optional<ArtifactGeneration> getArtifactGenerationByPath(@PathParam("groupId") String groupId, @PathParam("artifactId") String artifactId, @PathParam("versionId") String versionId,
        @PathParam("path") String path)
    {
        QueryMetricsContainer.record(groupId, artifactId, versionId);
        return handle(GET_ARTIFACT_GENERATION_BY_PATH, () -> this.artifactGenerationService.getArtifactGenerationByPath(groupId, artifactId, versionId, path));
    }

    @GET
    @Path("/{groupId}/{artifactId}/{versionId}/artifact/{path}/content")
    @ApiOperation(GET_ARTIFACT_GENERATION_CONTENT_BY_PATH)
    @Produces(MediaType.TEXT_PLAIN)
    public Optional<String> getArtifactGenerationContentByPath(@PathParam("groupId") String groupId, @PathParam("artifactId") String artifactId, @PathParam("versionId") String versionId,
        @PathParam("path") String path)
    {
        QueryMetricsContainer.record(groupId, artifactId, versionId);
        return handle(GET_ARTIFACT_GENERATION_CONTENT_BY_PATH, () -> this.artifactGenerationService.getArtifactGenerationContentByPath(groupId, artifactId, versionId, path));
    }

    @GET
    @Path("/{groupId}/{artifactId}/{versionId}/generator/{generatorPath}")
    @ApiOperation(GET_ARTIFACT_GENERATIONS_BY_GENERATOR)
    @Produces(MediaType.APPLICATION_JSON)
    public List<ArtifactGeneration> getArtifactGenerationsByGenerator(@PathParam("groupId") String groupId, @PathParam("artifactId") String artifactId, @PathParam("versionId") String versionId,
        @PathParam("generatorPath") String generatorPath)
    {

        QueryMetricsContainer.record(groupId, artifactId, versionId);
        return handle(GET_ARTIFACT_GENERATIONS_BY_GENERATOR, () -> this.artifactGenerationService.getArtifactsGenerationsByGenerator(groupId, artifactId, versionId, generatorPath));
    }


}
