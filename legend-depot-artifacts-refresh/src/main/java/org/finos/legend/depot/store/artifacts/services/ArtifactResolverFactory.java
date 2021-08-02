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

package org.finos.legend.depot.store.artifacts.services;

import org.finos.legend.depot.artifacts.repository.domain.ArtifactType;
import org.finos.legend.depot.store.artifacts.api.ProjectVersionArtifactsHandler;

import javax.inject.Singleton;
import java.util.EnumMap;

@Singleton
public class ArtifactResolverFactory
{
    private static final ArtifactResolverFactory instance = new ArtifactResolverFactory();
    private final EnumMap<ArtifactType, ProjectVersionArtifactsHandler> versionsProviders = new EnumMap<>(ArtifactType.class);

    private ArtifactResolverFactory()
    {
    }

    public static void registerVersionUpdater(ArtifactType artifactType, ProjectVersionArtifactsHandler provider)
    {
        instance.versionsProviders.put(artifactType, provider);
    }

    public static ProjectVersionArtifactsHandler getVersionRefresher(ArtifactType artifactType)
    {
        return instance.versionsProviders.get(artifactType);
    }
}
