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

import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.tuple.Pair;
import org.eclipse.collections.impl.list.mutable.FastList;
import org.eclipse.collections.impl.parallel.ParallelIterate;
import org.finos.legend.depot.domain.api.MetadataEventResponse;
import org.finos.legend.depot.domain.entity.EntityDefinition;
import org.finos.legend.depot.domain.entity.ProjectVersionEntities;
import org.finos.legend.depot.domain.entity.StoredEntity;
import org.finos.legend.depot.domain.project.ProjectVersion;
import org.finos.legend.depot.services.api.entities.EntitiesService;
import org.finos.legend.depot.services.api.entities.ManageEntitiesService;
import org.finos.legend.depot.services.api.projects.ProjectsService;
import org.finos.legend.depot.store.api.entities.UpdateEntities;
import org.finos.legend.depot.tracing.services.TracerFactory;
import org.finos.legend.sdlc.domain.model.entity.Entity;
import org.slf4j.Logger;

import javax.inject.Inject;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class EntitiesServiceImpl implements ManageEntitiesService, EntitiesService
{
    private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(EntitiesServiceImpl.class);
    public static final String CALCULATE_PROJECT_DEPENDENCIES = "calculateProjectDependencies";
    public static final String RETRIEVE_DEPENDENCY_ENTITIES = "retrieveDependencyEntities";
    private final UpdateEntities entities;
    private final ProjectsService projects;


    @Inject
    public EntitiesServiceImpl(UpdateEntities entities, ProjectsService projects)
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
        Set<ProjectVersion> dependencies = (Set<ProjectVersion>) executeWithTrace(CALCULATE_PROJECT_DEPENDENCIES, () ->
        {
            Set<ProjectVersion> deps = projects.getDependencies(projectDependencies, transitive);
            if (includeOrigin)
            {
                deps.addAll(projectDependencies);
            }
            return deps;
        });
        TracerFactory.get().log(String.format("dependencies: [%s] ",dependencies.size()));
        LOGGER.info("finished calculating [{}] dependencies",dependencies.size());
        return  (List<ProjectVersionEntities>) executeWithTrace(RETRIEVE_DEPENDENCY_ENTITIES, () ->
        {
            MutableList<ProjectVersionEntities> depEntities = FastList.newList();
            final AtomicInteger totalEntities = new AtomicInteger();
            ParallelIterate.forEach(dependencies, dep ->
            {
                List<EntityDefinition> deps = entities.getStoredEntities(dep.getGroupId(), dep.getArtifactId(), dep.getVersionId(), versioned).stream().map(StoredEntity::getEntity).collect(Collectors.toList());
                depEntities.add(new ProjectVersionEntities(dep.getGroupId(), dep.getArtifactId(), dep.getVersionId(), versioned, deps));
                totalEntities.addAndGet(deps.size());
                TracerFactory.get().log(String.format("Total [%s-%s-%s]: [%s] entities",dep.getGroupId(), dep.getArtifactId(), dep.getVersionId(),deps.size()));
            });
            TracerFactory.get().log(String.format("Total [%s]: [%s] entities",depEntities.size(),totalEntities));
            return depEntities;
        });
    }

    @Override
    public List<StoredEntity> findLatestEntitiesByClassifier(String classifier, String search, Integer limit, boolean summary, boolean versioned)
    {
        return entities.findLatestEntitiesByClassifier(classifier, search, limit, summary, versioned);
    }

    @Override
    public List<StoredEntity> findReleasedEntitiesByClassifier(String classifier, String search, List<ProjectVersion> projectVersions, Integer limit, boolean summary, boolean versioned)
    {
        return entities.findReleasedEntitiesByClassifier(classifier, search, projectVersions, limit, summary, versioned);
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
    public MetadataEventResponse delete(String groupId, String artifactId, String versionId, boolean versioned)
    {
        return new MetadataEventResponse().combine(entities.delete(groupId, artifactId, versionId, versioned));
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

    private Object executeWithTrace(String label, Supplier<Object> functionToExecute)
    {
        return TracerFactory.get().executeWithTrace(label, () -> functionToExecute.get());
    }
}
