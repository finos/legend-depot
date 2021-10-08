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
import org.eclipse.collections.impl.tuple.Tuples;
import org.eclipse.collections.impl.utility.ListIterate;
import org.finos.legend.depot.domain.entity.StoredEntity;
import org.finos.legend.depot.domain.project.ProjectData;
import org.finos.legend.depot.services.api.entities.EntitiesService;
import org.finos.legend.depot.services.api.entities.EntityClassifierService;
import org.finos.legend.depot.services.api.projects.ProjectsService;
import org.finos.legend.sdlc.domain.model.version.VersionId;
import org.slf4j.Logger;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class EntityClassifierServiceImpl implements EntityClassifierService
{
    private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(EntityClassifierServiceImpl.class);
    private final EntitiesService entities;
    private final ProjectsService projects;

    @Inject
    public EntityClassifierServiceImpl(ProjectsService projects, EntitiesService versions)
    {
        this.projects = projects;
        this.entities = versions;
    }

    private Set<StoredEntity> findReleasedEntitiesByClassifier(String classifierPath)
    {
        Stream<StoredEntity> allEntities = this.entities.findReleasedEntitiesByClassifier(classifierPath, false, false).stream();
        LOGGER.info("finished getting entities by classifier path {} ", classifierPath);
        Map<String, Optional<VersionId>> latestVersions = projects.getAll().stream().collect(Collectors.toMap(k -> k.getGroupId() + ":" + k.getArtifactId(), ProjectData::getLatestVersion));
        LOGGER.info("getting entities by latest version classifier path {} ", classifierPath);
        allEntities = allEntities.filter(ent ->
        {
            Optional<VersionId> version = latestVersions.get(ent.getGroupId() + ":" + ent.getArtifactId());
            return version.isPresent() && version.get().toVersionIdString().equals(ent.getVersionId());
        });
        Set<StoredEntity> uniqueEntities = allEntities.collect(Collectors.toSet());
        LOGGER.info("found {} ", uniqueEntities.size());
        Map<Pair<String, String>, Integer> counts = new HashMap<>();

        uniqueEntities.forEach(ent ->
        {
            int count = counts.getOrDefault(Tuples.pair(ent.getGroupId() + ":" + ent.getArtifactId(), ent.getVersionId()), 0) + 1;
            counts.put(Tuples.pair(ent.getGroupId() + ":" + ent.getArtifactId(), ent.getVersionId()), count);
        });

        return uniqueEntities;
    }

    @Override
    public List<StoredEntity> getEntitiesByClassifierPath(String classifierPath, String search, Integer limit)
    {
        List<StoredEntity> entities = new ArrayList<>(this.findReleasedEntitiesByClassifier(classifierPath));
        if (search != null)
        {
            entities = ListIterate.select(entities, entity -> entity.getEntity().getPath().contains(search));
        }
        if (limit != null)
        {
            entities = ListIterate.take(entities, limit);
        }
        return entities;
    }
}
