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

package org.finos.legend.depot.artifacts.repository.api;

import org.apache.maven.model.Model;
import org.finos.legend.depot.artifacts.repository.domain.ArtifactDependency;
import org.finos.legend.depot.artifacts.repository.domain.ArtifactType;
import org.finos.legend.sdlc.domain.model.version.VersionId;

import java.io.File;
import java.util.List;
import java.util.Set;

public interface ArtifactRepository
{

    boolean areValidCoordinates(String group, String artifact);

    Model getPOM(String group, String artifact, String version);

    List<String> getModulesFromPOM(ArtifactType type, String group, String artifact, String version);

    List<VersionId> findVersions(String group, String artifact) throws ArtifactRepositoryException;

    List<File> findFiles(ArtifactType type, String group, String artifact, String version);

    List<File> findDependenciesFiles(ArtifactType type, String group, String artifact, String version);

    Set<ArtifactDependency> findDependenciesByArtifactType(ArtifactType type, String groupId, String artifactId, String versionId);

    Set<ArtifactDependency> findDependencies(String groupId, String artifactId, String versionId);

}
