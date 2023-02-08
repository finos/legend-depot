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

package org.finos.legend.depot.services.entities;

import org.eclipse.collections.api.tuple.Pair;
import org.finos.legend.depot.domain.api.MetadataEventResponse;
import org.finos.legend.depot.domain.entity.StoredEntity;
import org.finos.legend.depot.services.api.entities.ManageEntitiesService;
import org.finos.legend.depot.services.api.projects.ProjectsService;
import org.finos.legend.depot.store.api.entities.UpdateEntities;
import org.finos.legend.depot.tracing.services.TracerFactory;

import javax.inject.Inject;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class ManageEntitiesServiceImpl extends EntitiesServiceImpl implements ManageEntitiesService
{

    private final UpdateEntities entities;

    @Inject
    public ManageEntitiesServiceImpl(UpdateEntities entities, ProjectsService projects)
    {
        super(entities,projects);
        this.entities = entities;
    }


    @Override
    public List<StoredEntity> getStoredEntities(String groupId, String artifactId)
    {
        return entities.getStoredEntities(groupId, artifactId);
    }

    @Override
    public List<StoredEntity> getStoredEntities(String groupId, String artifactId, String versionId)
    {
        return entities.getStoredEntities(groupId, artifactId, versionId);
    }


    @Override
    public MetadataEventResponse delete(String groupId, String artifactId, String versionId, boolean versioned)
    {
        this.projects.checkExists(groupId, artifactId);
        return new MetadataEventResponse().combine(entities.delete(groupId, artifactId, versionId, versioned));
    }

    @Override
    public MetadataEventResponse deleteAll(String groupId, String artifactId)
    {
        this.projects.checkExists(groupId, artifactId);
        return new MetadataEventResponse().combine(entities.deleteAll(groupId, artifactId));
    }

    @Override
    public MetadataEventResponse createOrUpdate(List<StoredEntity> versionedEntities)
    {
        return new MetadataEventResponse().combine(entities.createOrUpdate(versionedEntities));
    }

    @Override
    public List<Pair<String, String>> getOrphanedStoredEntities()
    {
        List<Pair<String, String>> allArtifacts = entities.getStoredEntitiesCoordinates();
        return allArtifacts.stream().filter(art -> !projects.findCoordinates(art.getOne(), art.getTwo()).isPresent()).collect(Collectors.toList());
    }

    private Object executeWithTrace(String label, Supplier<Object> functionToExecute)
    {
        return TracerFactory.get().executeWithTrace(label, () -> functionToExecute.get());
    }
}
