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

import java.util.regex.Pattern;

public class ProjectValidator
{

    public static final Pattern PROJECT_NAME_PATTERN = Pattern.compile("^PROD-\\d+$");
    public static final Pattern GROUP_ID_PATTERN = Pattern.compile("^\\S+\\.\\S+$");
    public static final Pattern ARTIFACT_ID_PATTERN = Pattern.compile("^\\S+$");
    private static final String STRING = "string";

    private ProjectValidator()
    {
    }

    public static boolean isValid(ProjectData projectData)
    {
        return isValidProjectId(projectData.getProjectId()) && isValidGroupId(projectData.getGroupId()) && isValidArtifactId(projectData.getArtifactId());
    }

    public static boolean isValidArtifactId(String artifactId)
    {
        return artifactId != null && !STRING.equals(artifactId) && ARTIFACT_ID_PATTERN.matcher(artifactId).matches();
    }

    public static boolean isValidGroupId(String groupId)
    {
        return groupId != null && GROUP_ID_PATTERN.matcher(groupId).matches();
    }

    public static boolean isValidProjectId(String projectId)
    {
        return projectId != null && PROJECT_NAME_PATTERN.matcher(projectId).matches();
    }


}
