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

package org.finos.legend.depot.services.artifacts.handlers.generations;

import org.finos.legend.depot.services.api.artifacts.repository.ArtifactRepository;
import org.finos.legend.depot.domain.artifacts.repository.ArtifactType;
import org.finos.legend.depot.services.artifacts.repository.maven.TestMavenArtifactsRepository;
import org.finos.legend.depot.domain.notifications.MetadataNotificationResponse;
import org.finos.legend.depot.domain.generation.DepotGeneration;
import org.finos.legend.depot.store.model.entities.EntityDefinition;
import org.finos.legend.depot.store.model.generations.StoredFileGeneration;
import org.finos.legend.depot.store.model.projects.StoreProjectData;
import org.finos.legend.depot.store.model.projects.StoreProjectVersionData;
import org.finos.legend.depot.services.api.generations.ManageFileGenerationsService;
import org.finos.legend.depot.services.generations.impl.ManageFileGenerationsServiceImpl;
import org.finos.legend.depot.services.projects.ProjectsServiceImpl;
import org.finos.legend.depot.services.api.projects.configuration.ProjectsConfiguration;
import org.finos.legend.depot.store.api.entities.UpdateEntities;
import org.finos.legend.depot.store.api.projects.UpdateProjects;
import org.finos.legend.depot.store.api.projects.UpdateProjectsVersions;
import org.finos.legend.depot.services.api.artifacts.handlers.generations.FileGenerationsArtifactsProvider;
import org.finos.legend.depot.services.api.metrics.query.QueryMetricsRegistry;
import org.finos.legend.depot.store.mongo.TestStoreMongo;
import org.finos.legend.depot.store.mongo.generations.FileGenerationsMongo;
import org.finos.legend.depot.services.api.notifications.queue.Queue;
import org.finos.legend.sdlc.domain.model.entity.Entity;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.finos.legend.depot.domain.version.VersionValidator.BRANCH_SNAPSHOT;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;


public class TestGenerationsProvider extends TestStoreMongo
{

    public static final String PRODUCT_A = "PROD-23992";
    protected static final String TEST_GROUP_ID = "examples.metadata";
    public static final String TEST_ARTIFACT_ID = "test";
    private final ArtifactRepository repository = new TestMavenArtifactsRepository();
    private FileGenerationsArtifactsProvider fileGenerationsProvider = new FileGenerationsProvider();
    private final UpdateProjects projects = mock(UpdateProjects.class);
    private final UpdateProjectsVersions projectsVersions = mock(UpdateProjectsVersions.class);
    private final UpdateEntities entities = mock(UpdateEntities.class);
    private final QueryMetricsRegistry metrics = mock(QueryMetricsRegistry.class);
    private final Queue queue = mock(Queue.class);
    private final FileGenerationsArtifactsProvider provider = mock(FileGenerationsArtifactsProvider.class);
    private final ManageFileGenerationsService generations = new ManageFileGenerationsServiceImpl(new FileGenerationsMongo(mongoProvider), new ProjectsServiceImpl(projectsVersions,projects,metrics,queue,new ProjectsConfiguration("master")));
    private final FileGenerationHandlerImpl handler = spy(new FileGenerationHandlerImpl(repository, provider, generations));

    @BeforeEach
    public void setup()
    {
       when(projects.find(TEST_GROUP_ID, TEST_ARTIFACT_ID)).thenReturn(Optional.of(new StoreProjectData(PRODUCT_A, TEST_GROUP_ID, TEST_ARTIFACT_ID)));
       when(projectsVersions.find(TEST_GROUP_ID, TEST_ARTIFACT_ID, BRANCH_SNAPSHOT("master"))).thenReturn(Optional.of(new StoreProjectVersionData(TEST_GROUP_ID, TEST_ARTIFACT_ID, BRANCH_SNAPSHOT("master"))));
       when(projectsVersions.find(TEST_GROUP_ID, TEST_ARTIFACT_ID, "2.0.0")).thenReturn(Optional.of(new StoreProjectVersionData(TEST_GROUP_ID, TEST_ARTIFACT_ID, "2.0.0")));
    }

    private List<File> getFiles(String versionId)
    {
        return repository.findFiles(ArtifactType.FILE_GENERATIONS, TEST_GROUP_ID, TEST_ARTIFACT_ID, versionId);
    }

    private List<File> getDependenciesFiles(String versionId)
    {
        return repository.findDependenciesFiles(ArtifactType.FILE_GENERATIONS, TEST_GROUP_ID, TEST_ARTIFACT_ID, versionId);
    }

    @Test
    public void canResolveGenerationsInJar()
    {
        List<File> files = getFiles("2.0.0");
        Assertions.assertNotNull(files);

        Assertions.assertEquals(1, files.size());

        Assertions.assertEquals(0, getDependenciesFiles("2.0.0").size());

        List<DepotGeneration> gens = fileGenerationsProvider.extractArtifacts(files);
        Assertions.assertFalse(gens.isEmpty());
    }

    @Test
    public void canRefreshRevisions()
    {

        Assertions.assertTrue(generations.getAll().isEmpty());
        FileGenerationHandlerImpl handler = new FileGenerationHandlerImpl(repository, fileGenerationsProvider, generations);
        StoreProjectData projectData = projects.find(TEST_GROUP_ID, TEST_ARTIFACT_ID).get();
        MetadataNotificationResponse response = handler.refreshProjectVersionArtifacts(projectData.getGroupId(),projectData.getArtifactId(), BRANCH_SNAPSHOT("master"), getFiles(BRANCH_SNAPSHOT("master")));
        Assertions.assertNotNull(response);
        Assertions.assertFalse(response.hasErrors());
        List<StoredFileGeneration> fileGenerations = generations.getAll();
        Assertions.assertNotNull(fileGenerations);
        Assertions.assertEquals(14, fileGenerations.size());

    }

    @Test
    public void canRefreshRevisionWithChangeInGenerations()
    {
        URL filePath = this.getClass().getClassLoader().getResource("repository/examples/metadata/test-file-generation/master-SNAPSHOT/test-file-generation-new-master-SNAPSHOT.jar");
        Assertions.assertTrue(generations.getAll().isEmpty());
        FileGenerationHandlerImpl handler = new FileGenerationHandlerImpl(repository, fileGenerationsProvider, generations);
        StoreProjectData projectData = projects.find(TEST_GROUP_ID, TEST_ARTIFACT_ID).get();
        //deleted one generation as part of new master snapshot version
        MetadataNotificationResponse response = handler.refreshProjectVersionArtifacts(projectData.getGroupId(),projectData.getArtifactId(), BRANCH_SNAPSHOT("master"),Arrays.asList(new File(filePath.getFile())));
        Assertions.assertNotNull(response);
        Assertions.assertFalse(response.hasErrors());
        List<StoredFileGeneration> fileGenerations = generations.getAll();
        Assertions.assertNotNull(fileGenerations);
        Assertions.assertEquals(13, fileGenerations.size());
    }

    @Test
    public void canGetTheRightElementPathFromGeneratedFile()
    {

        Assertions.assertTrue(generations.getAll().isEmpty());
        List<Entity> projectEntities = new ArrayList<>(Arrays.asList(new EntityDefinition("examples::metadata::snowFlakeApp", "", Collections.emptyMap()), new EntityDefinition("examples::metadata::snowFlakeApp2", "", Collections.emptyMap())));
        List<DepotGeneration> generatedFiles = new ArrayList<>(Arrays.asList(new DepotGeneration("/examples/metadata/snowFlakeApp/searchDocuments/SearchDocumentResult.json", ""), new DepotGeneration("/examples/metadata/snowFlakeApp2/searchDocuments/SearchDocumentResult.json", "")));
        StoreProjectData projectData = projects.find(TEST_GROUP_ID, TEST_ARTIFACT_ID).get();
        List<File> files = getFiles("2.0.0");
        when(this.provider.extractArtifacts(files)).thenReturn(generatedFiles);
        doReturn(projectEntities).when(this.handler).getAllNonVersionedEntities(projectData.getGroupId(), projectData.getArtifactId(), "2.0.0");
        MetadataNotificationResponse response = this.handler.refreshProjectVersionArtifacts(projectData.getGroupId(),projectData.getArtifactId(), "2.0.0", files);

        Assertions.assertNotNull(response);
        Assertions.assertFalse(response.hasErrors());
        List<StoredFileGeneration> fileGenerations = generations.getAll();
        Assertions.assertNotNull(fileGenerations);
        Assertions.assertEquals(2, fileGenerations.size());
        Assertions.assertEquals(1, fileGenerations.stream().filter(f -> f.getPath().equals("examples::metadata::snowFlakeApp")).count());
        Assertions.assertEquals(1, fileGenerations.stream().filter(f -> f.getPath().equals("examples::metadata::snowFlakeApp2")).count());
        StoredFileGeneration storedFileGeneration = fileGenerations.stream().filter(f -> f.getPath().equals("examples::metadata::snowFlakeApp")).findFirst().get();
        StoredFileGeneration storedFileGeneration2 = fileGenerations.stream().filter(f -> f.getPath().equals("examples::metadata::snowFlakeApp2")).findFirst().get();
        Assertions.assertEquals(generatedFiles.get(0).getPath(), storedFileGeneration.getFile().getPath());
        Assertions.assertEquals(generatedFiles.get(1).getPath(), storedFileGeneration2.getFile().getPath());

        List<StoredFileGeneration> searchDocuments = generations.findByType(TEST_GROUP_ID,TEST_ARTIFACT_ID, "2.0.0", "searchDocuments");
        Assertions.assertEquals(2, searchDocuments.size());
        List<StoredFileGeneration> snowFlakeAppSearchDocuments = generations.findByTypeAndElementPath(TEST_GROUP_ID,TEST_ARTIFACT_ID, "2.0.0", "searchDocuments", "examples::metadata::snowFlakeApp");
        Assertions.assertEquals(1, snowFlakeAppSearchDocuments.size());
        StoredFileGeneration storedFileGeneration1 = snowFlakeAppSearchDocuments.get(0);
        Assertions.assertEquals(storedFileGeneration1.getPath(),"examples::metadata::snowFlakeApp");
        Assertions.assertEquals(storedFileGeneration1.getType(),"searchDocuments");
    }

    @Test
    public void canRefreshVersions()
    {

        Assertions.assertTrue(generations.getAll().isEmpty());
        FileGenerationHandlerImpl handler = new FileGenerationHandlerImpl(repository, fileGenerationsProvider, generations);
        StoreProjectData projectData = projects.find(TEST_GROUP_ID, TEST_ARTIFACT_ID).get();
        MetadataNotificationResponse response = handler.refreshProjectVersionArtifacts(projectData.getGroupId(),projectData.getArtifactId(), "2.0.0", getFiles("2.0.0"));
        Assertions.assertNotNull(response);
        Assertions.assertFalse(response.hasErrors());
        List<StoredFileGeneration> fileGenerations = generations.getAll();
        Assertions.assertNotNull(fileGenerations);
        Assertions.assertEquals(12, fileGenerations.size());

    }


    @Test
    public void canReadFileGenerationArtifactsWithMultipleGenerations()
    {
        Assertions.assertTrue(generations.getAll().isEmpty());
        FileGenerationHandlerImpl handler = new FileGenerationHandlerImpl(repository, fileGenerationsProvider, generations);
        StoreProjectData projectData = projects.find(TEST_GROUP_ID, TEST_ARTIFACT_ID).get();
        MetadataNotificationResponse response = handler.refreshProjectVersionArtifacts(projectData.getGroupId(),projectData.getArtifactId(), "2.0.0", getFiles("2.0.0"));
        Assertions.assertNotNull(response);

        Assertions.assertFalse(response.hasErrors());
        List<StoredFileGeneration> fileGenerations = generations.getAll();
        Assertions.assertNotNull(fileGenerations);
        Assertions.assertEquals(12, fileGenerations.size());

        Assertions.assertEquals(4, generations.findByType(TEST_GROUP_ID, TEST_ARTIFACT_ID, "2.0.0", "java").size());
        Assertions.assertEquals(2, generations.findByType(TEST_GROUP_ID, TEST_ARTIFACT_ID, "2.0.0", "my-ext").size());
    }

    @Test
    public void canGetGenerationsForNonExistentVersion()
    {
        Assertions.assertThrows(IllegalArgumentException.class, () -> generations.getFileGenerations(TEST_GROUP_ID,TEST_ARTIFACT_ID,"10.0.0"));
    }

    @Test
    public void cannotGetGenerationsForExcludedVersions()
    {
        String versionId = "3.0.0";
        StoreProjectVersionData storeProjectVersion = new StoreProjectVersionData(TEST_GROUP_ID, TEST_ARTIFACT_ID, versionId);
        storeProjectVersion.getVersionData().setExcluded(true);
        when(projectsVersions.find(TEST_GROUP_ID, TEST_ARTIFACT_ID, versionId)).thenReturn(Optional.of(storeProjectVersion));
        Assertions.assertThrows(IllegalArgumentException.class, () -> generations.getFileGenerations(TEST_GROUP_ID,TEST_ARTIFACT_ID, versionId));
    }
}
