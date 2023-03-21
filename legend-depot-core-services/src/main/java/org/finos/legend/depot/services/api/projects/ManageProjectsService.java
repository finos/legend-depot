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

package org.finos.legend.depot.services.api.projects;

import org.finos.legend.depot.domain.api.MetadataEventResponse;
import org.finos.legend.depot.domain.project.StoreProjectData;
import org.finos.legend.depot.domain.project.StoreProjectVersionData;

import java.util.List;

public interface ManageProjectsService extends ProjectsService
{

    List<StoreProjectVersionData> getAll();

//    /**
//     * NOTE: page starting from 1
//     */

    StoreProjectVersionData createOrUpdate(StoreProjectVersionData projectData);

    StoreProjectData createOrUpdate(StoreProjectData projectData);

    MetadataEventResponse delete(String groupId,String artifactId);

    MetadataEventResponse delete(String groupId,String artifactId, String versionId);

    StoreProjectVersionData excludeProjectVersion(String groupId, String artifactId, String versionId, String exclusionReason);

}
