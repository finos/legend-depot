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

package org.finos.legend.depot.services.api.artifacts.repository;

import org.apache.maven.model.Model;
import org.finos.legend.depot.domain.artifacts.repository.ArtifactDependency;
import org.finos.legend.depot.domain.artifacts.repository.ArtifactType;
import org.finos.legend.sdlc.domain.model.version.VersionId;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class VoidArtifactRepositoryProvider implements ArtifactRepository
{
    public VoidArtifactRepositoryProvider(ArtifactRepositoryProviderConfiguration configuration)
    {
    }

    @Override
    public boolean areValidCoordinates(String group, String artifact)
    {
        return false;
    }

    @Override
    public Model getPOM(String group, String artifact, String version)
    {
        return new Model();
    }

    @Override
    public File getJarFile(String group, String artifact, String version)
    {
        return null;
    }

    @Override
    public List<String> getModulesFromPOM(ArtifactType type, String group, String artifact, String version)
    {
        return Collections.emptyList();
    }

    @Override
    public List<VersionId> findVersions(String group, String artifact)
    {
        return Collections.emptyList();
    }

    @Override
    public Optional<String> findVersion(String group, String artifact, String versionId) throws ArtifactRepositoryException
    {
        return Optional.empty();
    }

    @Override
    public List<File> findFiles(ArtifactType type, String group, String artifact, String version)
    {
        return Collections.emptyList();
    }

    @Override
    public List<File> findDependenciesFiles(ArtifactType type, String group, String artifact, String version)
    {
        return Collections.emptyList();
    }

    @Override
    public Set<ArtifactDependency> findDependenciesByArtifactType(ArtifactType type, String groupId, String artifactId, String versionId)
    {
        return Collections.emptySet();
    }

    @Override
    public Set<ArtifactDependency> findDependencies(String groupId, String artifactId, String versionId)
    {
        return Collections.emptySet();
    }
}
