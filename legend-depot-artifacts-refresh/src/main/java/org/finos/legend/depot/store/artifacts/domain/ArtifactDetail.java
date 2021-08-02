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

package org.finos.legend.depot.store.artifacts.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.finos.legend.depot.domain.HasIdentifier;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ArtifactDetail implements HasIdentifier
{

    public static final String SEPARATOR = "::";
    private String id;
    private String artifactType;
    private String groupId;
    private String artifactId;
    private String versionId;
    private String checkSum;
    private String path;

    public ArtifactDetail()
    {
    }

    public ArtifactDetail(String path, String checkSum)
    {
        this.path = path;
        this.checkSum = checkSum;
    }

    public ArtifactDetail(String type, String group, String artifact, String version, String path)
    {
        this.artifactType = type;
        this.groupId = group;
        this.artifactId = artifact;
        this.versionId = version;
        this.path = path;
    }

    public static String getGavCoordinates(String group, String artifact, String version)
    {
        return group + SEPARATOR + artifact + SEPARATOR + version;
    }

    @Override
    public String getId()
    {
        return this.id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getArtifactType()
    {
        return artifactType;
    }

    public void setArtifactType(String artifactType)
    {
        this.artifactType = artifactType;
    }

    public String getGroupId()
    {
        return groupId;
    }

    public void setGroupId(String groupId)
    {
        this.groupId = groupId;
    }

    public String getArtifactId()
    {
        return artifactId;
    }

    public void setArtifactId(String artifactId)
    {
        this.artifactId = artifactId;
    }

    public String getVersionId()
    {
        return versionId;
    }

    public void setVersionId(String versionId)
    {
        this.versionId = versionId;
    }

    public String getCheckSum()
    {
        return checkSum;
    }

    public ArtifactDetail setCheckSum(String checkSum)
    {
        this.checkSum = checkSum;
        return this;
    }

    public String getGavCoordinates()
    {
        return this.groupId + SEPARATOR + this.getArtifactId() + SEPARATOR + this.getVersionId();
    }

    public String getPath()
    {
        return this.path;
    }

    public ArtifactDetail setPath(String newPath)
    {
        this.path = newPath;
        return this;
    }
}
