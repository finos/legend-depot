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

package org.finos.legend.depot.services.api.artifacts.handlers;

import org.finos.legend.depot.domain.notifications.RestCuratedArtifacts;
import org.finos.legend.depot.domain.notifications.MetadataNotificationResponse;

import java.io.File;
import java.util.List;

public interface ProjectArtifactsHandler
{

    MetadataNotificationResponse refreshProjectVersionArtifacts(String groupId, String artifactId, String versionId, List<File> files);

    void delete(String groupId, String artifactId, String versionId);

    MetadataNotificationResponse refreshRestArtifacts(String groupId, String artifactId, String versionId, List<RestCuratedArtifacts> restCuratedArtifacts);
}
