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

package org.finos.legend.depot.services.pure.model.context;

import org.finos.legend.depot.core.services.tracing.TracerFactory;
import org.finos.legend.depot.domain.entity.ProjectVersionEntities;
import org.finos.legend.depot.services.api.entities.EntitiesService;
import org.finos.legend.depot.services.api.projects.ProjectsService;
import org.finos.legend.depot.services.api.pure.model.context.PureModelContextService;
import org.finos.legend.engine.protocol.pure.PureClientVersions;
import org.finos.legend.engine.protocol.pure.v1.model.context.AlloySDLC;
import org.finos.legend.engine.protocol.pure.v1.model.context.PureModelContextData;
import org.finos.legend.sdlc.domain.model.entity.Entity;
import org.finos.legend.sdlc.protocol.pure.v1.PureModelContextDataBuilder;

import javax.inject.Inject;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import static org.finos.legend.engine.protocol.pure.v1.model.context.PureModelContextData.newBuilder;

public class PureModelContextServiceImpl implements PureModelContextService
{
    private static final String PURE = "pure";
    private static final String CALCULATE_COMBINED_PMCD = "calculate combined PMCD";
    private static final String GA_SEPARATOR = ":";
    private static final TracerFactory tracer = TracerFactory.get();
    private final EntitiesService entitiesService;
    private final ProjectsService projectsService;

    @Inject
    public PureModelContextServiceImpl(EntitiesService entitiesService, ProjectsService projectsService)
    {
        this.entitiesService = entitiesService;
        this.projectsService = projectsService;
    }

    @Override
    public PureModelContextData getPureModelContextData(String groupId, String artifactId, String versionId, String clientVersion, boolean transitive)
    {
        String resolvedClientVersion = resolveAndValidateClientVersion(clientVersion);
        String version = this.projectsService.resolveAliasesAndCheckVersionExists(groupId, artifactId, versionId);

        List<Entity> entities = this.entitiesService.getEntities(groupId, artifactId, version);

        PureModelContextData pureModelContextData = getPureModelContextData(entities.stream(), groupId, artifactId, version, resolvedClientVersion);
        if (!transitive)
        {
            return pureModelContextData;
        }

        List<ProjectVersionEntities> dependenciesEntities = this.entitiesService.getDependenciesEntities(groupId, artifactId, version, true, false);
        return tracer.executeWithTrace(CALCULATE_COMBINED_PMCD, () ->
        {
            PureModelContextData dependenciesPMCD = getPureModelContextData(dependenciesEntities.stream().flatMap(dep -> dep.getEntities().stream()),groupId,artifactId,version,resolvedClientVersion);
            return combinePureModelContextData(pureModelContextData,dependenciesPMCD);
        });
    }

    protected String resolveAndValidateClientVersion(String clientVersion)
    {
        if (clientVersion != null && !PureClientVersions.versions.contains(clientVersion))
        {
            throw new IllegalArgumentException(String.format("Client version provided is invalid, following are the valid client versions: %s", String.join(", ", PureClientVersions.versions)));
        }
        return clientVersion == null ? PureClientVersions.production : clientVersion;
    }

    protected PureModelContextData getPureModelContextData(Stream<Entity> entities, String groupId, String artifactId, String versionId, String clientVersion)
    {
        return PureModelContextDataBuilder
                .newBuilder()
                .withProtocol(PURE, clientVersion)
                .withSDLC(buildAlloySDLC(groupId, artifactId, versionId))
                .withEntitiesIfPossible(entities)
                .build();
    }

    protected PureModelContextData combinePureModelContextData(PureModelContextData rootPMCD, PureModelContextData childPMCD)
    {
        PureModelContextData.Builder builder = newBuilder().withPureModelContextData(rootPMCD);
        Objects.requireNonNull(childPMCD);
        builder.addPureModelContextData(childPMCD);
        return builder.distinct().sorted().build();
    }

    protected AlloySDLC buildAlloySDLC(String groupId, String artifactId, String versionId)
    {
        AlloySDLC sdlc = new AlloySDLC();
        sdlc.project = String.join(GA_SEPARATOR,groupId,artifactId);
        sdlc.baseVersion = versionId;
        return sdlc;
    }
}
