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

package org.finos.legend.depot.store.mongo.admin.migrations;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.MongoNamespace;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.RenameCollectionOptions;
import org.bson.Document;
import org.eclipse.collections.api.map.ConcurrentMutableMap;
import org.eclipse.collections.impl.map.mutable.ConcurrentHashMap;
import org.finos.legend.depot.domain.project.ProjectVersion;
import org.finos.legend.depot.domain.project.StoreProjectVersionData;
import org.finos.legend.depot.domain.project.dependencies.VersionDependencyReport;
import org.finos.legend.depot.store.mongo.projects.ProjectsVersionsMongo;
import org.slf4j.Logger;

import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static org.finos.legend.depot.store.mongo.core.BaseMongo.buildDocument;
import static org.finos.legend.depot.store.mongo.core.BaseMongo.convert;

@Deprecated
public final class DependenciesMigration
{
    private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(ProjectToProjectVersionMigration.class);
    private final MongoDatabase mongoDatabase;
    private static final String NOT_FOUND_IN_STORE = "%s-%s-%s not found in store";
    private static final String INVALID_DEPENDENCIES = "%s-%s-%s has invalid transitive dependencies";
    private static final String VERSIONS_COLLECTION = "versionsTemp";

    public DependenciesMigration(MongoDatabase mongoDatabase)
    {
        this.mongoDatabase = mongoDatabase;
    }

    @Deprecated
    public void storeTransitiveDependenciesForAllProjectVersions()
    {
        MongoCollection<Document> versionsCollection = mongoDatabase.getCollection(ProjectsVersionsMongo.COLLECTION);
        mongoDatabase.getCollection(VERSIONS_COLLECTION).drop();
        MongoCollection<Document> tempVersionCollection = mongoDatabase.getCollection(VERSIONS_COLLECTION);
        List<StoreProjectVersionData> versionData = new ArrayList<>();
        versionsCollection.find().forEach((Consumer<Document>) document -> versionData.add(convert(new ObjectMapper(), document, StoreProjectVersionData.class)));
        List<StoreProjectVersionData> versionsToUpdate = calculateTransitiveDependenciesForAllVersions(versionData);
        try
        {
            versionsToUpdate.forEach(pv ->
            {
                tempVersionCollection.insertOne(buildDocument(pv));
                LOGGER.info(String.format("%s-%s-%s insertion completed",pv.getGroupId(), pv.getArtifactId(), pv.getVersionId()));
            });
        }
        catch (Exception e)
        {
            LOGGER.info(String.format("Error while inserting data:%s", e));
        }
    }

    @Deprecated
    public void renameVersionsCollection()
    {
        mongoDatabase.getCollection(VERSIONS_COLLECTION).renameCollection(new MongoNamespace(mongoDatabase.getName(), ProjectsVersionsMongo.COLLECTION), new RenameCollectionOptions().dropTarget(true));
    }

    private List<StoreProjectVersionData> calculateTransitiveDependenciesForAllVersions(List<StoreProjectVersionData> allProjectsVersions)
    {
        ConcurrentMutableMap<ProjectVersion, VersionDependencyReport> transitiveDependenciesMap = new ConcurrentHashMap<>();
        ConcurrentMutableMap<ProjectVersion, List<ProjectVersion>> directDependenciesMap = new ConcurrentHashMap<>();
        AtomicInteger i = new AtomicInteger();
        List<StoreProjectVersionData> versionWithDependencies = allProjectsVersions.stream().filter(p -> !p.getVersionData().getDependencies().isEmpty() && !p.getVersionData().isExcluded()).collect(Collectors.toList());
        List<StoreProjectVersionData> versionWithoutDependencies = allProjectsVersions.stream().filter(p -> p.getVersionData().getDependencies().isEmpty() || p.getVersionData().isExcluded()).collect(Collectors.toList());
        allProjectsVersions.stream().filter(pv -> !pv.getVersionData().isExcluded()).forEach(pv -> directDependenciesMap.put(new ProjectVersion(pv.getGroupId(), pv.getArtifactId(), pv.getVersionId()), pv.getVersionData().getDependencies()));
        try
        {
            LOGGER.info(String.format("Dependencies count for calculation: [%s]", versionWithDependencies.size()));
            versionWithDependencies.forEach(pv ->
            {
                ProjectVersion projectVersion = new ProjectVersion(pv.getGroupId(), pv.getArtifactId(), pv.getVersionId());
                List<ProjectVersion> artifactDependencies = directDependenciesMap.get(projectVersion);
                LOGGER.info(String.format("Finding transitive dependencies for [%s-%s-%s]", pv.getGroupId(), pv.getArtifactId(), pv.getVersionId()));
                VersionDependencyReport report = transitiveDependenciesMap.getIfAbsentPut(projectVersion, calculateTransitiveDependencies(artifactDependencies, transitiveDependenciesMap, directDependenciesMap));
                pv.setTransitiveDependenciesReport(report);
                LOGGER.info(String.format("Completed finding transitive dependencies for [%s-%s-%s]", pv.getGroupId(), pv.getArtifactId(), pv.getVersionId()));
                LOGGER.info(String.format("Dependencies calculation count completed: [%s]", i.incrementAndGet()));

            });
        }
        catch (Exception e)
        {
            LOGGER.info(String.format("Error finding dependencies: %s", e.getMessage()));
            throw new IllegalStateException(String.format("Error finding transitive dependencies due to: %s", e.getMessage()));
        }
        List<StoreProjectVersionData> finalList = new ArrayList<>();
        finalList.addAll(versionWithDependencies);
        finalList.addAll(versionWithoutDependencies);
        return finalList;
    }

    private VersionDependencyReport calculateTransitiveDependencies(List<ProjectVersion> artifactDependencies, ConcurrentMutableMap<ProjectVersion, VersionDependencyReport> transitiveDependenciesMap, ConcurrentMutableMap<ProjectVersion, List<ProjectVersion>> directDependenciesMap)
    {
        Set<ProjectVersion> dependencies = new HashSet<>();
        try
        {
            if (!artifactDependencies.isEmpty())
            {
                artifactDependencies.forEach(dep ->
                {
                    ProjectVersion pv = new ProjectVersion(dep.getGroupId(), dep.getArtifactId(), dep.getVersionId());
                    if (directDependenciesMap.get(dep) == null)
                    {
                        LOGGER.error(String.format(NOT_FOUND_IN_STORE, dep.getGroupId(), dep.getArtifactId(), dep.getVersionId()));
                        throw new IllegalStateException(String.format(NOT_FOUND_IN_STORE, dep.getGroupId(), dep.getArtifactId(), dep.getVersionId()));
                    }
                    VersionDependencyReport transitiveDependencies = transitiveDependenciesMap.getIfAbsentPut(pv, calculateTransitiveDependencies(directDependenciesMap.get(dep), transitiveDependenciesMap, directDependenciesMap));
                    if (transitiveDependencies.isValid())
                    {
                        dependencies.add(pv);
                        dependencies.addAll(transitiveDependencies.getTransitiveDependencies());
                    }
                    else
                    {
                        LOGGER.error(String.format(INVALID_DEPENDENCIES, dep.getGroupId(), dep.getArtifactId(), dep.getVersionId()));
                        throw new IllegalStateException(String.format(INVALID_DEPENDENCIES, dep.getGroupId(), dep.getArtifactId(), dep.getVersionId()));
                    }
                });
            }
        }
        catch (IllegalStateException e)
        {
            return new VersionDependencyReport(new ArrayList<>(), false);
        }
        catch (Exception e)
        {
            LOGGER.info(String.format("Error finding dependencies: %s", e.getMessage()));
            throw new IllegalStateException(String.format("Error finding transitive dependencies with message: %s", e.getMessage()));
        }
        return new VersionDependencyReport(dependencies.stream().collect(Collectors.toList()), true);
    }
}
