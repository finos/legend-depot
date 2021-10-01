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

package org.finos.legend.depot.services.entities;

import org.eclipse.collections.api.tuple.Pair;
import org.finos.legend.depot.domain.api.MetadataEventResponse;
import org.finos.legend.depot.domain.entity.EntityDefinition;
import org.finos.legend.depot.domain.entity.ProjectVersionEntities;
import org.finos.legend.depot.domain.entity.StoredEntity;
import org.finos.legend.depot.domain.project.ProjectVersion;
import org.finos.legend.depot.services.api.entities.EntitiesService;
import org.finos.legend.depot.services.api.entities.ManageEntitiesService;
import org.finos.legend.depot.store.api.entities.UpdateEntities;
import org.finos.legend.depot.store.api.projects.UpdateProjects;
import org.finos.legend.sdlc.domain.model.entity.Entity;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class EntitiesServiceImpl implements ManageEntitiesService, EntitiesService
{

    private final UpdateEntities entities;
    private final UpdateProjects projects;


    @Inject
    public EntitiesServiceImpl(UpdateEntities entities, UpdateProjects projects)
    {
        this.entities = entities;
        this.projects = projects;
    }

    @Override
    public List<Entity> getEntities(String groupId, String artifactId, String versionId, boolean versioned)
    {
        return entities.getEntities(groupId, artifactId, versionId, versioned);
    }

    @Override
    public Optional<Entity> getEntity(String groupId, String artifactId, String versionId, String entityPath)
    {
        return entities.getEntity(groupId, artifactId, versionId, entityPath);
    }

    @Override
    public List<Entity> getEntitiesByPackage(String groupId, String artifactId, String versionId, String packageName, boolean versioned, Set<String> classifierPaths, boolean includeSubPackages)
    {
        return entities.getEntitiesByPackage(groupId, artifactId, versionId, packageName, versioned, classifierPaths, includeSubPackages);
    }

    @Override
    public List<StoredEntity> getStoredEntities(String groupId, String artifactId)
    {
        return entities.getStoredEntities(groupId, artifactId);
    }

    @Override
    public List<StoredEntity> getStoredEntities(String groupId, String artifactId, String versionId)
    {
        return entities.getStoredEntities(groupId, artifactId, versionId);
    }

    @Override
    public List<ProjectVersionEntities> getDependenciesEntities(List<ProjectVersion> projectDependencies, boolean versioned, boolean transitive, boolean includeOrigin)
    {
        Set<ProjectVersion> dependencies = projects.getDependencies(projectDependencies, transitive);
        if (includeOrigin)
        {
            dependencies.addAll(projectDependencies);
        }
        List<ProjectVersionEntities> depEntities = new ArrayList<>();
        dependencies.parallelStream().forEach(dep ->
        {
            List<EntityDefinition> deps = entities.getStoredEntities(dep.getGroupId(), dep.getArtifactId(), dep.getVersionId(), versioned).stream().map(StoredEntity::getEntity).collect(Collectors.toList());
            depEntities.add(new ProjectVersionEntities(dep.getGroupId(), dep.getArtifactId(), dep.getVersionId(), versioned, deps));
        });
        return depEntities;
    }

    @Override
    public List<StoredEntity> findLatestEntitiesByClassifier(String classifier, boolean summary, boolean versioned)
    {
        return entities.findLatestEntitiesByClassifier(classifier, summary, versioned);
    }

    @Override
    public List<StoredEntity> findReleasedEntitiesByClassifier(String classifier, boolean summary, boolean versioned)
    {
        return entities.findReleasedEntitiesByClassifier(classifier, summary, versioned);
    }

    @Override
    public MetadataEventResponse delete(String groupId, String artifactId, String versionId)
    {
        return new MetadataEventResponse().combine(entities.delete(groupId, artifactId, versionId));
    }

    @Override
    public MetadataEventResponse deleteAll(String groupId, String artifactId)
    {
        return new MetadataEventResponse().combine(entities.deleteAll(groupId, artifactId));
    }

    @Override
    public MetadataEventResponse createOrUpdate(List<StoredEntity> versionedEntities)
    {
        return new MetadataEventResponse().combine(entities.createOrUpdate(versionedEntities));
    }

    @Override
    public List<Pair<String, String>> getOrphanedStoredEntities()
    {
        List<Pair<String, String>> allArtifacts = entities.getStoredEntitiesCoordinates();
        return allArtifacts.stream().filter(art -> !projects.find(art.getOne(), art.getTwo()).isPresent()).collect(Collectors.toList());
    }
}
