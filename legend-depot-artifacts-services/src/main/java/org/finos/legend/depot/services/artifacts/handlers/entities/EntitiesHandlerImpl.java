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
import org.finos.legend.depot.services.api.entities.ManageEntitiesService;
import org.finos.legend.depot.services.api.artifacts.handlers.entties.EntitiesArtifactsHandler;
import org.finos.legend.depot.services.api.artifacts.handlers.entties.EntityArtifactsProvider;

import javax.inject.Inject;
import java.io.File;
import java.util.List;

public class EntitiesHandlerImpl extends AbstractEntityRefreshHandlerImpl implements EntitiesArtifactsHandler
{
    @Inject
    public EntitiesHandlerImpl(ManageEntitiesService entitiesService, EntityArtifactsProvider artifactProvider)
    {
        super(entitiesService, artifactProvider);
    }

    @Override
    public MetadataNotificationResponse refreshProjectVersionArtifacts(String groupId, String artifactId, String versionId, List<File> files)
    {
        return super.refreshVersionArtifacts(groupId, artifactId, versionId, files);
    }

    @Override
    public void delete(String groupId, String artifactId, String versionId)
    {
        super.deleteByVersion(groupId,artifactId,versionId);
    }
}
