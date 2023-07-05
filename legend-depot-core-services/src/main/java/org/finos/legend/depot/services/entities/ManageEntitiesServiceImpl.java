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
import org.finos.legend.depot.domain.entity.StoredEntity;
import org.finos.legend.depot.services.api.entities.ManageEntitiesService;
import org.finos.legend.depot.services.api.projects.ProjectsService;
import org.finos.legend.depot.store.api.entities.UpdateEntities;

import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;

public class ManageEntitiesServiceImpl<T extends StoredEntity> extends EntitiesServiceImpl<T> implements ManageEntitiesService<T>
{

    private final UpdateEntities entities;

    @Inject
    public ManageEntitiesServiceImpl(UpdateEntities entities, ProjectsService projects)
    {
        super(entities,projects);
        this.entities = entities;
    }


    @Override
    public List<T> getStoredEntities(String groupId, String artifactId)
    {
        return entities.getStoredEntities(groupId, artifactId);
    }

    @Override
    public List<T> getStoredEntities(String groupId, String artifactId, String versionId)
    {
        return entities.getStoredEntities(groupId, artifactId, versionId);
    }


    @Override
    public long delete(String groupId, String artifactId, String versionId)
    {
        this.projects.checkExists(groupId, artifactId);
        return entities.delete(groupId, artifactId, versionId);
    }

    @Override
    public long delete(String groupId, String artifactId)
    {
        this.projects.checkExists(groupId, artifactId);
        return entities.delete(groupId, artifactId);
    }

    @Override
    public void createOrUpdate(List<T> entityList)
    {
        entities.createOrUpdate(entityList);
    }

    @Override
    public List<Pair<String, String>> getOrphanedStoredEntities()
    {
        List<Pair<String, String>> allArtifacts = entities.getStoredEntitiesCoordinates();
        return allArtifacts.stream().filter(art -> !projects.findCoordinates(art.getOne(), art.getTwo()).isPresent()).collect(Collectors.toList());
    }
}
