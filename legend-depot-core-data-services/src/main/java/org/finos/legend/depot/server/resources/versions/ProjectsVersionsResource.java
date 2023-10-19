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
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.finos.legend.depot.domain.VersionedData;
import org.finos.legend.depot.store.model.projects.StoreProjectVersionData;
import org.finos.legend.depot.domain.project.ProjectVersionData;
import org.finos.legend.depot.domain.version.VersionValidator;
import org.finos.legend.depot.services.api.projects.ProjectsService;
import org.finos.legend.depot.core.services.tracing.resources.TracingResource;
import org.finos.legend.depot.core.services.tracing.ResourceLoggingAndTracing;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.finos.legend.depot.domain.DatesHandler.toTime;

@Path("")
@Api("Versions")
public class ProjectsVersionsResource extends TracingResource
{

    private final ProjectsService projectVersionApi;

    @Inject
    public ProjectsVersionsResource(ProjectsService projectVersionApi)
    {
        this.projectVersionApi = projectVersionApi;
    }


    @GET
    @Path("/projects/versions/{createdFrom}")
    @ApiOperation(ResourceLoggingAndTracing.GET_VERSIONS_BY_CREATION_DATE)
    @Produces(MediaType.APPLICATION_JSON)
    public List<StoreProjectVersionData> findByCreationDate(@PathParam("createdFrom") @ApiParam(value = "Created From Date value in milliseconds (UTC) ") long createdFrom,
                                                            @QueryParam("createdTo") @ApiParam(value = "Created To Date value in milliseconds (UTC) ")  Long createdTo)
    {
        return handle(ResourceLoggingAndTracing.GET_VERSIONS_BY_CREATION_DATE,
                () -> projectVersionApi.findByCreationDate(createdFrom, createdTo == null ? toTime(LocalDateTime.now()) : createdTo));
    }

    @GET
    @Path("/versions/{groupId}/{artifactId}/{versionId}")
    @ApiOperation(value = ResourceLoggingAndTracing.GET_PROJECT_VERSION_BY_GAV, response = ProjectVersionDTO.class)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getProjectVersion(@PathParam("groupId") String groupId,
                                      @PathParam("artifactId") String artifactId,
                                      @PathParam("versionId") @ApiParam(value = VersionValidator.VALID_VERSION_ID_TXT) String versionId,
                                      @Context Request request)
    {
        return handle(ResourceLoggingAndTracing.GET_PROJECT_VERSION_BY_GAV, ResourceLoggingAndTracing.GET_PROJECT_VERSION_BY_GAV + groupId + artifactId + versionId, () ->
        {
            Optional<StoreProjectVersionData> projectVersion = projectVersionApi.find(groupId, artifactId, versionId);
            if (projectVersion.isPresent())
            {
                StoreProjectVersionData pv = projectVersion.get();
                return Optional.of(new ProjectVersionDTO(pv.getGroupId(), pv.getArtifactId(), pv.getVersionId(), pv.getVersionData()));
            }
            return Optional.empty();
        }, request, () -> null);
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
