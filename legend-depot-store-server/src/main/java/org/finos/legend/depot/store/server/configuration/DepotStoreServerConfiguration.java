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

package org.finos.legend.depot.store.server.configuration;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.finos.legend.depot.services.api.artifacts.configuration.ArtifactsRefreshPolicyConfiguration;
import org.finos.legend.depot.services.api.artifacts.repository.ArtifactRepositoryProviderConfiguration;
import org.finos.legend.depot.core.server.ServerConfiguration;
import org.finos.legend.depot.services.api.artifacts.configuration.ArtifactsRetentionPolicyConfiguration;
import org.finos.legend.depot.services.api.notifications.queue.QueueManagerConfiguration;


@JsonIgnoreProperties(ignoreUnknown = true)
public class DepotStoreServerConfiguration extends ServerConfiguration
{

    @JsonProperty("queueManager")
    private QueueManagerConfiguration queueManagerConfiguration;

    @JsonProperty("artifactRepositoryProviderConfiguration")
    private ArtifactRepositoryProviderConfiguration artifactRepositoryProviderConfiguration;

    @JsonProperty("artifactsRefreshPolicyConfiguration")
    private ArtifactsRefreshPolicyConfiguration artifactsRefreshPolicyConfiguration;

    @JsonProperty("artifactsRetentionPolicyConfiguration")
    private ArtifactsRetentionPolicyConfiguration artifactsRetentionPolicyConfiguration;

    public QueueManagerConfiguration getQueueManagerConfiguration()
    {
        return queueManagerConfiguration;
    }

    public void setQueueManagerConfiguration(QueueManagerConfiguration queueManagerConfiguration)
    {
        this.queueManagerConfiguration = queueManagerConfiguration;
    }

    public ArtifactRepositoryProviderConfiguration getArtifactRepositoryProviderConfiguration()
    {
        return artifactRepositoryProviderConfiguration;
    }

    public void setArtifactRepositoryProviderConfiguration(ArtifactRepositoryProviderConfiguration artifactRepositoryProviderConfiguration)
    {
        this.artifactRepositoryProviderConfiguration = artifactRepositoryProviderConfiguration;
    }

    public ArtifactsRefreshPolicyConfiguration getArtifactsRefreshPolicyConfiguration()
    {
        return artifactsRefreshPolicyConfiguration;
    }

    public void setArtifactsRefreshPolicyConfiguration(ArtifactsRefreshPolicyConfiguration artifactsRefreshPolicyConfiguration)
    {
        this.artifactsRefreshPolicyConfiguration = artifactsRefreshPolicyConfiguration;
    }

    public ArtifactsRetentionPolicyConfiguration getRetentionPolicyConfiguration()
    {
        return artifactsRetentionPolicyConfiguration;
    }

    public void setRetentionPolicyConfiguration(ArtifactsRetentionPolicyConfiguration artifactsRetentionPolicyConfiguration)
    {
        this.artifactsRetentionPolicyConfiguration = artifactsRetentionPolicyConfiguration;
    }
}
