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

package org.finos.legend.depot.store.artifacts.services.entities;

import org.finos.legend.depot.domain.entity.EntityDefinition;
import org.finos.legend.depot.domain.entity.StoredEntity;
import org.finos.legend.depot.domain.entity.StoredVersionedEntity;
import org.finos.legend.depot.domain.project.StoreProjectData;
import org.finos.legend.depot.services.api.versionedEntities.ManageVersionedEntitiesService;
import org.finos.legend.depot.store.artifacts.api.entities.EntityArtifactsProvider;
import org.finos.legend.depot.store.artifacts.api.entities.VersionedEntitiesArtifactsHandler;
import org.finos.legend.depot.store.artifacts.api.entities.VersionedEntityArtifactsProvider;
import org.finos.legend.sdlc.domain.model.entity.Entity;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

public class VersionedEntitiesHandlerImpl extends EntitiesHandlerImpl implements VersionedEntitiesArtifactsHandler
{
    @Inject
    public VersionedEntitiesHandlerImpl(ManageVersionedEntitiesService versionedEntitiesService, VersionedEntityArtifactsProvider artifactProvider)
    {
        super(versionedEntitiesService, (EntityArtifactsProvider) artifactProvider);
    }

    @Override
    public void delete(String groupId, String artifactId, String versionId)
    {
        super.deleteByVersion(groupId, artifactId, versionId);
    }


    @Override
    List<StoredEntity> transformVersionedEntities(StoreProjectData projectData, String versionId, List<Entity> entityList)
    {
        List<StoredEntity> versionedEntities = new ArrayList<>();
        for (Entity entity : entityList)
        {
            EntityDefinition entityDefinition = new EntityDefinition(entity.getPath(), entity.getClassifierPath(), entity.getContent());
            StoredEntity storedEntity = new StoredVersionedEntity(projectData.getGroupId(), projectData.getArtifactId(), versionId, entityDefinition);
            versionedEntities.add(storedEntity);
        }
        return versionedEntities;
    }
}
