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

package org.finos.legend.depot.store.resources.artifacts.repository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.finos.legend.depot.services.api.artifacts.repository.ArtifactRepository;
import org.finos.legend.depot.services.api.artifacts.repository.ArtifactRepositoryException;
import org.finos.legend.depot.core.services.tracing.resources.TracingResource;
import org.finos.legend.depot.core.services.tracing.ResourceLoggingAndTracing;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Path("")
@Tag(name = "Repository")
public class RepositoryResource extends TracingResource
{

    private final ArtifactRepository artifactRepository;

    @Inject
    public RepositoryResource(ArtifactRepository artifactRepository)
    {
        this.artifactRepository = artifactRepository;
    }

    @GET
    @Path("/repository/versions/{groupId}/{artifactId}")
    @Operation(summary = ResourceLoggingAndTracing.REPOSITORY_PROJECT_VERSIONS)
    @Produces(MediaType.APPLICATION_JSON)
    public List<String> getRepositoryVersions(@PathParam("groupId") String groupId,
                                                 @PathParam("artifactId") String artifactId)
    {
        return handle(ResourceLoggingAndTracing.REPOSITORY_PROJECT_VERSIONS, ResourceLoggingAndTracing.REPOSITORY_PROJECT_VERSIONS + groupId + artifactId,
                () ->
                {
                    try
                    {
                        return artifactRepository.findVersions(groupId, artifactId).stream().map(v -> v.toVersionIdString()).collect(Collectors.toList());
                    }
                    catch (Exception e)
                    {
                        throw new RuntimeException(e.getMessage());
                    }
                });
    }

    @GET
    @Path("/repository/versions/{groupId}/{artifactId}/{versionId}")
    @Operation(summary = ResourceLoggingAndTracing.REPOSITORY_PROJECT_VERSIONS)
    @Produces(MediaType.TEXT_PLAIN)
    public Optional<String> getRepositoryVersion(@PathParam("groupId") String groupId,
                                                 @PathParam("artifactId") String artifactId,
                                                 @PathParam("versionId") @Parameter(description = "a valid version string: x.y.z, master-SNAPSHOT") String versionId)
    {
        return handle(ResourceLoggingAndTracing.REPOSITORY_PROJECT_VERSIONS, ResourceLoggingAndTracing.REPOSITORY_PROJECT_VERSIONS + groupId + artifactId + versionId, () ->
        {
            try
            {
                return artifactRepository.findVersion(groupId, artifactId,versionId);
            }
            catch (ArtifactRepositoryException e)
            {
                return Optional.of(e.getMessage());
            }
        });
    }

}
