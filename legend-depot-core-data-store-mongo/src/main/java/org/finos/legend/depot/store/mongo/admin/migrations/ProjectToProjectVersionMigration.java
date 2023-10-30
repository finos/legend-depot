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

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import org.bson.Document;
import org.finos.legend.depot.domain.project.ProjectVersion;
import org.finos.legend.depot.domain.project.ProjectVersionData;
import org.finos.legend.depot.domain.project.Property;
import org.finos.legend.depot.store.model.projects.StoreProjectVersionData;
import org.finos.legend.depot.store.mongo.core.BaseMongo;
import org.finos.legend.depot.store.mongo.projects.ProjectsMongo;
import org.finos.legend.depot.store.mongo.projects.ProjectsVersionsMongo;
import org.finos.legend.sdlc.domain.model.version.VersionId;
import org.slf4j.Logger;

import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.finos.legend.depot.domain.version.VersionValidator.BRANCH_SNAPSHOT;

@Deprecated
public final class ProjectToProjectVersionMigration
{
    private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(ProjectToProjectVersionMigration.class);
    private final MongoDatabase mongoDatabase;
    private static final String EXCLUDED = "versionData.excluded";

    public ProjectToProjectVersionMigration(MongoDatabase mongoDatabase)
    {
        this.mongoDatabase = mongoDatabase;
    }

    private StoreProjectVersionData createStoreProjectData(Document document, String version)
    {
        String groupId = document.getString(BaseMongo.GROUP_ID);
        String artifactId = document.getString(BaseMongo.ARTIFACT_ID);
        List<Document> dependenciesDocs = document.getList("dependencies",Document.class, Collections.emptyList());
        List<ProjectVersion> dependencies = dependenciesDocs.stream().filter(doc -> doc.getString(BaseMongo.VERSION_ID).equals(version)).map(dep ->
        {
            Document dp = (Document) dep.get("dependency");
            return new ProjectVersion(dp.getString(BaseMongo.GROUP_ID),dp.getString(BaseMongo.ARTIFACT_ID),dp.getString(BaseMongo.VERSION_ID));
        }).collect(Collectors.toList());
        List<Document> propertyDocs = document.getList("properties",Document.class,Collections.emptyList());
        List<Property> properties = propertyDocs.stream().filter(doc -> doc.getString("projectVersionId").equals(version)).map(property ->
                new Property(property.getString("propertyName"),property.getString("value"))).collect(Collectors.toList());
        return new StoreProjectVersionData(groupId, artifactId, version, false, new ProjectVersionData(dependencies, properties));
    }

    @Deprecated
    public void migrationToProjectVersions()
    {
        mongoDatabase.getCollection(ProjectsVersionsMongo.COLLECTION).drop();
        MongoCollection<Document> projectCollection = mongoDatabase.getCollection(ProjectsMongo.COLLECTION);
        MongoCollection<Document> versionCollection = mongoDatabase.getCollection(ProjectsVersionsMongo.COLLECTION);
        projectCollection.find().forEach((Consumer<Document>) document ->
        {
            AtomicInteger i = new AtomicInteger();
            String groupId = document.getString(BaseMongo.GROUP_ID);
            String artifactId = document.getString(BaseMongo.ARTIFACT_ID);
            try
            {
                List<String> versions = document.getList("versions",String.class);

                LOGGER.info(String.format("versions that should be inserted [%s]",versions.size()));
                versionCollection.insertOne(BaseMongo.buildDocument(createStoreProjectData(document, BRANCH_SNAPSHOT("master"))));
                LOGGER.info(String.format("%s-%s-%s insertion completed",groupId,artifactId, BRANCH_SNAPSHOT("master")));

                versions.forEach(version ->
                {
                    versionCollection.insertOne(BaseMongo.buildDocument(createStoreProjectData(document, version)));
                    LOGGER.info(String.format("%s-%s-%s insertion completed",groupId, artifactId, version));
                    i.incrementAndGet();
                });
                LOGGER.info(String.format("versions inserted [%s]",i.get()));
            }
            catch (Exception e)
            {
                LOGGER.info("Error while inserting data:" + e.getMessage());
                LOGGER.info(String.format("versions inserted [%s] before error",i.get()));
                LOGGER.info(String.format("%s-%s insertion could not be completed",groupId, artifactId));
            }
        });
    }

    @Deprecated
    public void cleanUpProjectData()
    {
        MongoCollection<Document> projectCollection = mongoDatabase.getCollection(ProjectsMongo.COLLECTION);
        AtomicInteger i = new AtomicInteger();
        projectCollection.find().forEach((Consumer<Document>) document ->
        {
            String groupId = document.getString(BaseMongo.GROUP_ID);
            String artifactId = document.getString(BaseMongo.ARTIFACT_ID);
            try
            {
                projectCollection
                        .updateOne(Filters.and(Filters.eq(BaseMongo.GROUP_ID, groupId),
                                Filters.eq(BaseMongo.ARTIFACT_ID, artifactId)),
                                Updates.combine(Updates.unset("versions"), Updates.unset("dependencies"), Updates.unset("properties"), Updates.unset("latestVersion")));
                i.incrementAndGet();
                LOGGER.info(String.format("%s-%s updation completed", groupId, artifactId));
            }
            catch (Exception e)
            {
                LOGGER.info("Error while updating data: " + e);

                LOGGER.info(String.format("versions updated [%s] before error", i.get()));
                LOGGER.info(String.format("%s-%s updated could not be completed", groupId, artifactId));
            }
        });
        LOGGER.info(String.format("versions updated [%s]", i.get()));
    }

    @Deprecated
    public void addLatestVersionToProjectData()
    {
        MongoCollection<Document> projectCollection = mongoDatabase.getCollection(ProjectsMongo.COLLECTION);
        MongoCollection<Document> versionCollection = mongoDatabase.getCollection(ProjectsVersionsMongo.COLLECTION);
        projectCollection.find().forEach((Consumer<Document>) document ->
        {
            AtomicInteger i = new AtomicInteger();
            String groupId = document.getString(BaseMongo.GROUP_ID);
            String artifactId = document.getString(BaseMongo.ARTIFACT_ID);
            try
            {
                FindIterable<Document> versionDocument = versionCollection.find(Filters.and(
                        Filters.eq(BaseMongo.GROUP_ID, groupId), Filters.eq(BaseMongo.ARTIFACT_ID, artifactId),
                        Filters.not(Filters.regex(BaseMongo.VERSION_ID, BRANCH_SNAPSHOT(""))), Filters.eq(EXCLUDED, false)));
                List<VersionId> parsedVersions  = new ArrayList<>();
                versionDocument.forEach((Consumer<Document>) doc ->
                {
                    parsedVersions.add(VersionId.parseVersionId(doc.getString(BaseMongo.VERSION_ID)));
                });
                Optional<VersionId> latestVersion = parsedVersions.stream().max(Comparator.comparing(Function.identity()));
                if (latestVersion.isPresent())
                {
                    LOGGER.info(String.format("%s-%s updated with latest version", groupId, artifactId));
                    projectCollection.updateOne(Filters.and(Filters.eq(BaseMongo.GROUP_ID, groupId),
                            Filters.eq(BaseMongo.ARTIFACT_ID, artifactId)), Updates.set("latestVersion", latestVersion.get().toVersionIdString()));
                    LOGGER.info(String.format("%s-%s update completed",groupId,artifactId));
                }
                LOGGER.info(String.format("projects updated [%s]",i.incrementAndGet()));
            }
            catch (Exception e)
            {
                LOGGER.info("Error while updating data:" + e.getMessage());
                LOGGER.info(String.format("projects updated [%s] before error",i.get()));
                LOGGER.info(String.format("%s-%s update could not be completed",groupId, artifactId));
            }
        });
    }
}
