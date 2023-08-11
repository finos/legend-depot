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

package org.finos.legend.depot.domain.project;

import org.finos.legend.depot.store.model.projects.StoreProjectData;

import java.util.regex.Pattern;

import static org.finos.legend.depot.domain.CoordinateValidator.isValidArtifactId;
import static org.finos.legend.depot.domain.CoordinateValidator.isValidGroupId;

public class ProjectValidator
{

    public static final Pattern PROJECT_NAME_PATTERN = Pattern.compile("^PROD-\\d+$");


    private ProjectValidator()
    {
    }

    public static boolean isValid(StoreProjectData projectData)
    {
        return isValidProjectId(projectData.getProjectId()) && isValidGroupId(projectData.getGroupId()) && isValidArtifactId(projectData.getArtifactId());
    }

    public static boolean isValidProjectId(String projectId)
    {
        return projectId != null && PROJECT_NAME_PATTERN.matcher(projectId).matches();
    }

}
