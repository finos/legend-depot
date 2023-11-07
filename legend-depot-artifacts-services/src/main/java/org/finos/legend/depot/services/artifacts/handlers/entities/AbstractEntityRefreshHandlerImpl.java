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

package org.finos.legend.depot.services.artifacts.handlers.entities;

import org.finos.legend.depot.domain.notifications.MetadataNotificationResponse;
import org.finos.legend.depot.domain.version.VersionValidator;
import org.finos.legend.depot.services.api.entities.ManageEntitiesService;
import org.finos.legend.depot.services.api.artifacts.handlers.entties.EntityArtifactsProvider;
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


    protected long deleteByVersion(String groupId, String artifactId, String versionId)
    {
        return getEntitiesApi().delete(groupId, artifactId, versionId);
    }


    private String getGAVCoordinates(String groupId,String artifactId, String versionId)
    {
        return String.format("%s-%s-%s", groupId, artifactId, versionId);
    }


    public MetadataNotificationResponse refreshVersionArtifacts(String groupId, String artifactId, String versionId, List<File> files)
    {

        MetadataNotificationResponse response = new MetadataNotificationResponse();
        try
        {
            String gavCoordinates = getGAVCoordinates(groupId, artifactId, versionId);
            List<Entity> entityList = getEntities(files);
            if (entityList != null && !entityList.isEmpty())
            {
                String message = String.format("found [%s] %s for [%s] ", entityList.size(), this.entitiesProvider.getType(), gavCoordinates);
                getLOGGER().info(message);
                response.addMessage(message);
                if (VersionValidator.isSnapshotVersion(versionId))
                {
                    message = String.format("removing prior %s artifacts for [%s-%s]",this.entitiesProvider.getType(),gavCoordinates,versionId);
                    response.addMessage(message);
                    response.addMessage("deleted " + getEntitiesApi().delete(groupId, artifactId,versionId));
                    LOGGER.info(message);
                }
                getEntitiesApi().createOrUpdate(groupId, artifactId, versionId, entityList);
            }
            else
            {
                String message = String.format("found 0 %s for [%s] ",this.entitiesProvider.getType(), gavCoordinates);
                getLOGGER().info(message);
                response.addMessage(message);
            }

        }
        catch (Exception e)
        {
            String errorMessage = String.format("Unexpected exception refreshing %s %s-%s-%s , %s",entitiesProvider.getType(),groupId,artifactId,versionId,e.getMessage());
            response.addError(errorMessage);
            LOGGER.error(errorMessage);
        }
        return response;
    }

    private List<Entity> getEntities(List<File> files)
    {
        return entitiesProvider.extractArtifacts(files);
    }
}
