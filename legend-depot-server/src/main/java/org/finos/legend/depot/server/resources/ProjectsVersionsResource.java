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

package org.finos.legend.depot.server.resources;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.finos.legend.depot.domain.VersionedData;
import org.finos.legend.depot.domain.project.StoreProjectVersionData;
import org.finos.legend.depot.domain.project.ProjectVersionData;
import org.finos.legend.depot.services.api.projects.ProjectsService;
import org.finos.legend.depot.tracing.resources.BaseResource;
import org.finos.legend.depot.tracing.resources.ResourceLoggingAndTracing;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.Optional;

@Path("")
@Api("Versions")
public class ProjectsVersionsResource extends BaseResource
{

    private final ProjectsService projectVersionApi;

    @Inject
    public ProjectsVersionsResource(ProjectsService projectVersionApi)
    {
        this.projectVersionApi = projectVersionApi;
    }

    @GET
    @Path("/versions/{groupId}/{artifactId}/{versionId}")
    @ApiOperation(value = ResourceLoggingAndTracing.GET_PROJECT_VERSION_BY_GAV, response = ProjectVersionDTO.class)
    @Produces(MediaType.APPLICATION_JSON)
    public Optional<ProjectVersionDTO> getProjectVersion(@PathParam("groupId") String groupId,
                                                         @PathParam("artifactId") String artifactId,
                                                         @PathParam("versionId") @ApiParam(value = "a valid versionId, released version X.Y.Z,master-SNAPSHOT or alias", example = "latest = last released version") String versionId)
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
