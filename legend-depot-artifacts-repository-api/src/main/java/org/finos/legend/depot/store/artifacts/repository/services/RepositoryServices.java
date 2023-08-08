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

package org.finos.legend.depot.store.artifacts.repository.services;

import org.apache.maven.model.Model;
import org.finos.legend.depot.store.artifacts.repository.api.ArtifactRepository;
import org.finos.legend.depot.store.artifacts.repository.api.ArtifactRepositoryException;
import org.finos.legend.depot.store.artifacts.repository.domain.ArtifactDependency;
import org.finos.legend.depot.store.artifacts.repository.domain.ArtifactType;
import org.finos.legend.sdlc.domain.model.version.VersionId;

import javax.inject.Inject;
import java.io.File;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class RepositoryServices
{
    private final ArtifactRepository repository;

    @Inject
    public RepositoryServices(ArtifactRepository repository)
    {
        this.repository = repository;
    }

    public List<VersionId> findVersions(String groupId, String artifactId) throws ArtifactRepositoryException
    {
        return this.repository.findVersions(groupId,artifactId);
    }

    public Optional<String> findVersion(String groupId, String artifactId, String versionId) throws ArtifactRepositoryException
    {
        return this.repository.findVersion(groupId,artifactId,versionId);
    }

    public Set<ArtifactDependency> findDependencies(String groupId, String artifactId, String versionId)
    {
        return this.repository.findDependencies(groupId, artifactId, versionId);
    }

    public Model getPOM(String groupId, String artifactId, String versionId)
    {
        return repository.getPOM(groupId, artifactId, versionId);
    }

    public File getJarFile(String groupId, String artifactId, String versionId)
    {
        return repository.getJarFile(groupId, artifactId, versionId);
    }

    public boolean areValidCoordinates(String groupId, String artifactId)
    {
        return this.repository.areValidCoordinates(groupId, artifactId);
    }

    public List<File> findFiles(ArtifactType type, String groupId, String artifactId, String versionId)
    {
        return this.repository.findFiles(type, groupId, artifactId, versionId);
    }
}
