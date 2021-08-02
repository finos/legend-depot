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
import org.finos.legend.depot.domain.project.ProjectData;
import org.finos.legend.depot.domain.version.VersionValidator;
import org.finos.legend.depot.services.api.entities.ManageEntitiesService;
import org.finos.legend.depot.store.artifacts.api.entities.EntityArtifactsProvider;
import org.finos.legend.sdlc.domain.model.entity.Entity;
import org.slf4j.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public abstract class AbstractEntityRefreshHandlerImpl
{
    private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(AbstractEntityRefreshHandlerImpl.class);

    private final EntityArtifactsProvider entitiesProvider;
    private final ManageEntitiesService entitiesApi;


    protected AbstractEntityRefreshHandlerImpl(ManageEntitiesService entitiesService, EntityArtifactsProvider artifactProvider)
    {
        this.entitiesApi = entitiesService;
        this.entitiesProvider = artifactProvider;
    }

    protected Logger getLOGGER()
    {
        return LOGGER;
    }


    protected ManageEntitiesService getEntitiesApi()
    {
        return entitiesApi;
    }


    List<StoredEntity> transformVersionedEntities(ProjectData project, String versionId, List<Entity> entityList)
    {
        List<StoredEntity> versionedEntities = new ArrayList<>();
        for (Entity entity : entityList)
        {
            EntityDefinition entityDefinition = new EntityDefinition(entity.getPath(), entity.getClassifierPath(), entity.getContent());
            StoredEntity storedEntity = new StoredEntity(project.getGroupId(), project.getArtifactId(), versionId, entityDefinition);
            versionedEntities.add(storedEntity);
        }
        return versionedEntities;
    }


    public void delete(String groupId, String artifactId, String versionId)
    {
        getEntitiesApi().delete(groupId, artifactId, versionId);
    }


    private String getGAVCoordinates(ProjectData projectConfig, String versionId)
    {
        return String.format("%s:%s:%s", projectConfig.getGroupId(), projectConfig.getArtifactId(), versionId);
    }


    public MetadataEventResponse refreshVersionArtifacts(ProjectData project, String versionId, List<File> files)
    {

        MetadataEventResponse response = new MetadataEventResponse();
        String gavCoordinates = getGAVCoordinates(project, versionId);
        List<Entity> entityList = getEntities(files);
        if (entityList != null && !entityList.isEmpty())
        {
            String message = String.format("[%s]: found [%s] entities for %s ", project.getProjectId(), entityList.size(), gavCoordinates);
            getLOGGER().info(message);
            response.addMessage(message);
            List<StoredEntity> versionedEntities = transformVersionedEntities(project, versionId, entityList);
            if (versionId.equals(VersionValidator.MASTER_SNAPSHOT))
            {
                getEntitiesApi().deleteLatest(project.getGroupId(), project.getArtifactId());
                getLOGGER().info("removed old entities for {} - {} ", project.getProjectId(), gavCoordinates);
            }
            response.combine(getEntitiesApi().createOrUpdate(versionedEntities));
        }

        return response;
    }

    private List<Entity> getEntities(List<File> files)
    {
        return entitiesProvider.loadArtifacts(files);
    }


}
