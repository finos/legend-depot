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

import org.finos.legend.depot.domain.api.MetadataEventResponse;
import org.finos.legend.depot.domain.entity.EntityDefinition;
import org.finos.legend.depot.domain.entity.StoredEntity;
import org.finos.legend.depot.domain.project.StoreProjectData;
import org.finos.legend.depot.domain.version.VersionValidator;
import org.finos.legend.depot.services.api.entities.ManageEntitiesService;
import org.finos.legend.depot.store.artifacts.api.entities.EntitiesArtifactsHandler;
import org.finos.legend.depot.store.artifacts.api.entities.EntityArtifactsProvider;
import org.finos.legend.sdlc.domain.model.entity.Entity;

import javax.inject.Inject;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class EntitiesHandlerImpl extends AbstractEntityRefreshHandlerImpl implements EntitiesArtifactsHandler
{
    @Inject
    public EntitiesHandlerImpl(ManageEntitiesService entitiesService, EntityArtifactsProvider artifactProvider)
    {
        super(entitiesService, artifactProvider);
    }

    @Override
    public MetadataEventResponse refreshProjectVersionArtifacts(StoreProjectData projectData, String versionId, List<File> files)
    {
        return super.refreshVersionArtifacts(projectData, versionId, files);
    }

    @Override
    public void delete(String groupId, String artifactId, String versionId)
    {
       super.delete(groupId,artifactId,versionId,false);
    }

    @Override
    List<StoredEntity> transformVersionedEntities(StoreProjectData projectData, String versionId, List<Entity> entityList)
    {
        List<StoredEntity> versionedEntities = new ArrayList<>();
        for (Entity entity : entityList)
        {
            EntityDefinition entityDefinition = new EntityDefinition(entity.getPath(), entity.getClassifierPath(), entity.getContent());
            StoredEntity storedEntity = new StoredEntity(projectData.getGroupId(), projectData.getArtifactId(), versionId, false,entityDefinition);
            versionedEntities.add(storedEntity);
        }
        return versionedEntities;
    }
}
