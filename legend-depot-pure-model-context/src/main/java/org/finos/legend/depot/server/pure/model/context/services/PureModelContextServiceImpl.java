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

package org.finos.legend.depot.server.pure.model.context.services;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.finos.legend.depot.domain.entity.ProjectVersionEntities;
import org.finos.legend.depot.server.pure.model.context.api.PureModelContextService;
import org.finos.legend.depot.server.pure.model.context.api.PureModelContextServiceException;
import org.finos.legend.depot.services.api.entities.EntitiesService;
import org.finos.legend.depot.services.api.projects.ProjectsService;
import org.finos.legend.engine.protocol.pure.PureClientVersions;
import org.finos.legend.engine.protocol.pure.v1.model.context.AlloySDLC;
import org.finos.legend.engine.protocol.pure.v1.model.context.PureModelContextData;
import org.finos.legend.engine.shared.core.ObjectMapperFactory;
import org.finos.legend.sdlc.domain.model.entity.Entity;
import org.finos.legend.sdlc.domain.model.version.VersionId;
import org.finos.legend.sdlc.protocol.pure.v1.PureModelContextDataBuilder;
import org.slf4j.Logger;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.finos.legend.engine.protocol.pure.v1.model.context.PureModelContextData.newBuilder;

public class PureModelContextServiceImpl implements PureModelContextService
{
    public static final String PURE = "pure";
    private static final String LATEST = "latest";
    private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(PureModelContextServiceImpl.class);
    private final EntitiesService entitiesService;
    private final ProjectsService projectsService;
    private final ObjectMapper objectMapper;

    @Inject
    public PureModelContextServiceImpl(EntitiesService entitiesService, ProjectsService projectsService)
    {
        this.entitiesService = entitiesService;
        this.projectsService = projectsService;
        objectMapper = ObjectMapperFactory.getNewStandardObjectMapperWithPureProtocolExtensionSupports();
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    public String getPureModelContextDataAsString(String groupId, String artifactId, String versionId, String clientVersion, boolean versioned, boolean getDependencies)
    {
        return toString(getPureModelContextData(groupId, artifactId, versionId, clientVersion, versioned, getDependencies));
    }

    @Override
    public PureModelContextData getPureModelContextData(String groupId, String artifactId, String versionId, String clientVersion, boolean versioned, boolean getDependencies)
    {
        this.projectsService.checkExists(groupId, artifactId);
        String version = versionId;
        if (version.equals(LATEST))
        {
            Optional<VersionId> project = this.projectsService.getLatestVersion(groupId, artifactId);
            if (project.isPresent())
            {
                version = project.get().toVersionIdString();
            }
        }

        List<Entity> entities = this.entitiesService.getEntities(groupId, artifactId, version, versioned);
        PureModelContextData pureModelContextData = getPureModelContextData(entities, groupId, artifactId, version, clientVersion);
        if (!getDependencies)
        {
            return pureModelContextData;
        }
        else
        {
            List<ProjectVersionEntities> dependencyProjectVersionEntities = this.entitiesService.getDependenciesEntities(groupId, artifactId, version, versioned, true, false);      // always get transitive dependencies and include entities of project itself

            List<PureModelContextData> dependenciesPMCD = new ArrayList<>();
            for (ProjectVersionEntities projectVersionEntities : dependencyProjectVersionEntities)
            {
                dependenciesPMCD.add(getPureModelContextData(projectVersionEntities.getEntities().stream().map(x -> (Entity) x).collect(Collectors.toList()),
                        projectVersionEntities.getGroupId(),
                        projectVersionEntities.getArtifactId(),
                        projectVersionEntities.getVersionId(),
                        clientVersion));
            }
            return combinePureModelContextData(pureModelContextData, dependenciesPMCD);
        }
    }

    private PureModelContextData getPureModelContextData(List<Entity> entities, String groupId, String artifactId, String versionId, String clientVersion)
    {
        return PureModelContextDataBuilder
                .newBuilder()
                .withProtocol(PURE, clientVersion == null ? PureClientVersions.production : clientVersion)
                .withSDLC(getAlloySDLC(groupId, artifactId, versionId))
                .withEntities(entities)
                .build();
    }

    private AlloySDLC getAlloySDLC(String groupId, String artifactId, String versionId)
    {
        AlloySDLC sdlc = new AlloySDLC();
        sdlc.project = groupId + ":" + artifactId;
        sdlc.baseVersion = versionId;
        return sdlc;
    }

    private String toString(PureModelContextData contextData)
    {
        if (contextData.getElements().isEmpty())
        {
            return null;
        }
        try
        {
            return objectMapper.writeValueAsString(contextData);
        }
        catch (JsonProcessingException e)
        {
            LOGGER.error(e.getMessage());
            throw new PureModelContextServiceException(e);
        }
    }

    private PureModelContextData combinePureModelContextData(PureModelContextData pureModelContextData, List<PureModelContextData> dataList)
    {
        PureModelContextData.Builder builder = newBuilder().withPureModelContextData(pureModelContextData);
        if (dataList != null)
        {
            dataList.forEach(data ->
            {
                Objects.requireNonNull(data);
                builder.addPureModelContextData(data);
            });
        }
        return builder.distinct().sorted().build();
    }
}
