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

package org.finos.legend.depot.server.resources.versions;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.finos.legend.depot.domain.VersionedData;
import org.finos.legend.depot.store.model.projects.StoreProjectVersionData;
import org.finos.legend.depot.domain.project.ProjectVersionData;
import org.finos.legend.depot.domain.version.VersionValidator;
import org.finos.legend.depot.services.api.projects.ProjectsService;
import org.finos.legend.depot.core.services.tracing.resources.TracingResource;
import org.finos.legend.depot.core.services.tracing.ResourceLoggingAndTracing;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.finos.legend.depot.domain.DatesHandler.toTime;

@Path("")
@Tag(name = "Versions")
public class ProjectsVersionsResource extends TracingResource
{

    private final ProjectsService projectVersionApi;

    @Inject
    public ProjectsVersionsResource(ProjectsService projectVersionApi)
    {
        this.projectVersionApi = projectVersionApi;
    }


    @GET
    @Path("/projects/versions/{updatedFrom}")
    @Operation(summary = ResourceLoggingAndTracing.GET_VERSIONS_BY_LASTUPDATE_DATE)
    @Produces(MediaType.APPLICATION_JSON)
    public Response findByUpdatedDate(@PathParam("updatedFrom") @Parameter(description = "Updated From Date value in milliseconds (UTC) ") long updatedFrom,
                                      @QueryParam("updatedTo") @Parameter(description = "Updated To Date value in milliseconds (UTC) ")  Long updatedTo)
    {
        return handleResponse(ResourceLoggingAndTracing.GET_VERSIONS_BY_LASTUPDATE_DATE,
                () -> projectVersionApi.findByUpdatedDate(updatedFrom, updatedTo == null ? toTime(LocalDateTime.now()) : updatedTo));
    }

    @GET
    @Path("/versions/{groupId}/{artifactId}/{versionId}")
    @Operation(summary = ResourceLoggingAndTracing.GET_PROJECT_VERSION_BY_GAV)
    @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(schema = @Schema(implementation = ProjectVersionDTO.class)))
    @Produces(MediaType.APPLICATION_JSON)
    public Response getProjectVersion(@PathParam("groupId") String groupId,
                                      @PathParam("artifactId") String artifactId,
                                      @PathParam("versionId") @Parameter(description = VersionValidator.VALID_VERSION_ID_TXT) String versionId)
    {
        return handleResponse(ResourceLoggingAndTracing.GET_PROJECT_VERSION_BY_GAV, ResourceLoggingAndTracing.GET_PROJECT_VERSION_BY_GAV + groupId + artifactId + versionId, () ->
        {
            Optional<StoreProjectVersionData> projectVersion = projectVersionApi.find(groupId, artifactId, versionId);
            if (projectVersion.isPresent())
            {
                StoreProjectVersionData pv = projectVersion.get();
                return Optional.of(new ProjectVersionDTO(pv.getGroupId(), pv.getArtifactId(), pv.getVersionId(), pv.getVersionData()));
            }
            return Optional.empty();
        });
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public class ProjectVersionDTO extends VersionedData
    {
        @JsonProperty
        private ProjectVersionData versionData;

        public ProjectVersionDTO()
        {

        }

        public ProjectVersionDTO(String groupId, String artifactId, String versionId, ProjectVersionData versionData)
        {
            super(groupId, artifactId, versionId);
            this.versionData = versionData;
        }

        public ProjectVersionData getVersionData()
        {
            return versionData;
        }

        public void setVersionData(ProjectVersionData versionData)
        {
            this.versionData = versionData;
        }

        @Override
        public boolean equals(Object obj)
        {
            return EqualsBuilder.reflectionEquals(this, obj);
        }

        @Override
        public int hashCode()
        {
            return HashCodeBuilder.reflectionHashCode(this);
        }
    }

}
