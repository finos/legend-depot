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

package org.finos.legend.depot.store.mongo.admin;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.FindOneAndReplaceOptions;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.finos.legend.depot.domain.HasIdentifier;
import org.finos.legend.depot.domain.project.ProjectData;
import org.finos.legend.depot.domain.project.StoreProjectData;
import org.finos.legend.depot.domain.project.StoreProjectVersionData;
import org.finos.legend.depot.domain.project.ProjectVersion;
import org.finos.legend.depot.domain.project.ProjectVersionData;
import org.finos.legend.depot.domain.project.ProjectVersionProperty;
import org.finos.legend.depot.domain.project.ProjectProperty;
import org.finos.legend.depot.store.StoreException;
import org.finos.legend.depot.store.mongo.projects.ProjectsMongo;
import org.finos.legend.depot.store.mongo.projects.ProjectsVersionsMongo;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;

public class MigrationHelper
{
    private final MongoDatabase mongoDatabase;
    private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(MigrationHelper.class);

    public MigrationHelper(MongoDatabase mongoDatabase)
    {
        this.mongoDatabase = mongoDatabase;
    }

    @Deprecated
    public void migrationToProjectVersions()
    {
        mongoDatabase.getCollection(ProjectsVersionsMongo.COLLECTION).drop();
        List<ProjectData> result = new ArrayList<>();
        MongoCollection<Document> projectCollection = mongoDatabase.getCollection(ProjectsMongo.COLLECTION);
        MongoCollection<Document> versionCollection = mongoDatabase.getCollection(ProjectsVersionsMongo.COLLECTION);
        projectCollection.find().forEach((Consumer<Document>) document -> result.add(convert(document, ProjectData.class)));
        result.stream().forEach(pd ->
                    {
                        AtomicInteger i = new AtomicInteger();
                        try
                        {
                            LOGGER.info(String.format("versions that should be inserted [%s]",pd.getVersions().size()));
                            versionCollection.insertOne(buildDocument(createStoreProjectData(pd, "master-SNAPSHOT")));
                            LOGGER.info(String.format("%s-%s-%s insertion completed",pd.getGroupId(), pd.getArtifactId(), "master-SNAPSHOT"));
                            pd.getVersions().forEach(version ->
                            {
                                versionCollection.insertOne(buildDocument(createStoreProjectData(pd, version)));
                                LOGGER.info(String.format("%s-%s-%s insertion completed",pd.getGroupId(), pd.getArtifactId(), version));
                                i.incrementAndGet();
                            });
                            LOGGER.info(String.format("versions inserted [%s]",i.get()));
                        }
                        catch (Exception e)
                        {
                            LOGGER.info("Error while inserting data:" + e.getMessage());
                            LOGGER.info(String.format("versions inserted [%s] before error",i.get()));
                            LOGGER.info(String.format("%s-%s insertion could not be completed",pd.getGroupId(), pd.getArtifactId()));
                        }
                    });
    }

    @Deprecated
    public void cleanUpProjectData()
    {
        List<ProjectData> result = new ArrayList<>();
        MongoCollection<Document> projectCollection = mongoDatabase.getCollection(ProjectsMongo.COLLECTION);
        projectCollection.find().forEach((Consumer<Document>) document -> result.add(convert(document, ProjectData.class)));
        AtomicInteger i = new AtomicInteger();
        LOGGER.info(String.format("versions that should be inserted [%s]",result.size()));
        result.stream().forEach(pd ->
                {
                    try
                    {
                        projectCollection
                                .findOneAndReplace(and(eq("groupId", pd.getGroupId()),
                                        eq("artifactId", pd.getArtifactId())),
                                        buildDocument(new StoreProjectData(pd.getProjectId(), pd.getGroupId(), pd.getArtifactId())),
                                        new FindOneAndReplaceOptions().upsert(true));
                        i.incrementAndGet();
                        LOGGER.info(String.format("%s-%s insertion completed",pd.getGroupId(), pd.getArtifactId()));
                    }
                    catch (Exception e)
                    {
                        LOGGER.info("Error while inserting data: " + e);

                        LOGGER.info(String.format("versions inserted [%s] before error",i.get()));
                        LOGGER.info(String.format("%s-%s insertion could not be completed",pd.getGroupId(), pd.getArtifactId()));
                    }
                });
        LOGGER.info(String.format("versions inserted [%s]",i.get()));
    }

    private StoreProjectVersionData createStoreProjectData(ProjectData pd, String version)
    {
        List<ProjectData.ProjectVersionDependency> dependencies = pd.getDependencies(version);
        List<ProjectVersion> dep = dependencies.isEmpty() ? Collections.emptyList() : dependencies.stream().map(x -> x.getDependency()).collect(Collectors.toList());
        List<ProjectProperty> properties = pd.getPropertiesForProjectVersionID(version);
        List<ProjectVersionProperty> prop = properties.isEmpty() ? Collections.emptyList() : properties.stream().map(x -> new ProjectVersionProperty(x.getPropertyName(), x.getValue())).collect(Collectors.toList());
        return new StoreProjectVersionData(pd.getGroupId(), pd.getArtifactId(), version, false, new ProjectVersionData(dep, prop));
    }

    private <T extends HasIdentifier> Document buildDocument(T object)
    {
        try
        {
            Document doc = Document.parse(new ObjectMapper().writeValueAsString(object));
            if (object.getId() != null && !object.getId().isEmpty())
            {
                doc.put("_id", new ObjectId(object.getId()));
                doc.remove("id");
            }
            return doc;
        }
        catch (JsonProcessingException e)
        {
            throw new StoreException("Error serializing dataset to json");
        }
    }

    public <T> T convert(Document document, Class<T> clazz)
    {
        ObjectMapper objectMapper = new ObjectMapper().setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
        if (document == null)
        {
            return null;
        }
        ObjectId id = document.getObjectId("_id");
        if (id != null)
        {
            document.remove("id");
            document.put("id", id.toHexString());
        }
        try
        {
            return objectMapper.convertValue(document, clazz);
        }
        catch (Exception e)
        {
            LOGGER.error(String.format("error converting document (%s) to class %s. reason: %s", Objects.requireNonNull(id).toString(), clazz.getSimpleName(), e.getMessage()));
            return null;
        }
    }
}
