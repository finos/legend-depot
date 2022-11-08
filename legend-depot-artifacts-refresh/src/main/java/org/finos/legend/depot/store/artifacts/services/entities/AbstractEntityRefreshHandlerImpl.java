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

import org.finos.legend.depot.artifacts.repository.domain.ArtifactType;
import org.finos.legend.depot.domain.api.MetadataEventResponse;
import org.finos.legend.depot.domain.entity.StoredEntity;
import org.finos.legend.depot.domain.project.ProjectData;
import org.finos.legend.depot.domain.version.VersionValidator;
import org.finos.legend.depot.services.api.entities.ManageEntitiesService;
import org.finos.legend.depot.store.artifacts.api.entities.EntityArtifactsProvider;
import org.finos.legend.sdlc.domain.model.entity.Entity;
import org.slf4j.Logger;

import java.io.File;
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


    abstract List<StoredEntity> transformVersionedEntities(ProjectData project, String versionId, List<Entity> entityList);


    protected MetadataEventResponse delete(String groupId, String artifactId, String versionId,boolean versioned)
    {
        return getEntitiesApi().delete(groupId, artifactId, versionId,versioned);
    }


    private String getGAVCoordinates(ProjectData projectConfig, String versionId)
    {
        return String.format("%s-%s-%s", projectConfig.getGroupId(), projectConfig.getArtifactId(), versionId);
    }


    public MetadataEventResponse refreshVersionArtifacts(ProjectData project, String versionId, List<File> files)
    {

        MetadataEventResponse response = new MetadataEventResponse();
        try
        {
            String gavCoordinates = getGAVCoordinates(project, versionId);
            List<Entity> entityList = getEntities(files);
            if (entityList != null && !entityList.isEmpty())
            {
                String message = String.format("[%s]: found [%s] %s for [%s] ", project.getProjectId(), entityList.size(), this.entitiesProvider.getType(), gavCoordinates);
                getLOGGER().info(message);
                response.addMessage(message);
                List<StoredEntity> storedEntities = transformVersionedEntities(project, versionId, entityList);
                if (versionId.equals(VersionValidator.MASTER_SNAPSHOT))
                {
                    MetadataEventResponse deleteResponse = getEntitiesApi().delete(project.getGroupId(), project.getArtifactId(),versionId,this.entitiesProvider.getType().equals(ArtifactType.VERSIONED_ENTITIES));
                    message = String.format("removed [%s] %s [%s-%s]",storedEntities.size(),this.entitiesProvider.getType(),gavCoordinates,versionId);
                    getLOGGER().info(message);
                    response.addMessage(message);
                    response.combine(deleteResponse);
                }
                response.combine(getEntitiesApi().createOrUpdate(storedEntities));
            }
            else
            {
                String message = String.format("[%s]: found 0 %s for [%s] ", project.getProjectId(), this.entitiesProvider.getType(), gavCoordinates);
                getLOGGER().info(message);
                response.addMessage(message);
            }

        }
        catch (Exception e)
        {
            String errorMessage = String.format("Unexpected exception refreshing %s %s-%s-%s , %s",entitiesProvider.getType(),project.getGroupId(),project.getArtifactId(),versionId,e.getMessage());
            response.addError(errorMessage);
            LOGGER.error(errorMessage);
        }
        return response;
    }

    private List<Entity> getEntities(List<File> files)
    {
        return entitiesProvider.loadArtifacts(files);
    }


}
