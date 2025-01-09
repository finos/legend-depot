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

package org.finos.legend.depot.services.artifacts.purge;

import org.finos.legend.depot.services.TestBaseServices;
import org.finos.legend.depot.services.api.metrics.query.QueryMetricsService;
import org.finos.legend.depot.services.artifacts.reconciliation.VersionsReconciliationServiceImpl;
import org.finos.legend.depot.store.model.metrics.query.VersionQueryMetric;
import org.finos.legend.depot.services.api.artifacts.repository.ArtifactRepository;
import org.finos.legend.depot.domain.artifacts.repository.ArtifactType;
import org.finos.legend.depot.domain.version.VersionMismatch;
import org.finos.legend.depot.domain.DatesHandler;
import org.finos.legend.depot.domain.notifications.MetadataNotificationResponse;
import org.finos.legend.depot.store.model.projects.StoreProjectData;
import org.finos.legend.depot.store.model.projects.StoreProjectVersionData;
import org.finos.legend.depot.services.api.entities.ManageEntitiesService;
import org.finos.legend.depot.services.api.projects.ManageProjectsService;
import org.finos.legend.depot.services.entities.ManageEntitiesServiceImpl;
import org.finos.legend.depot.services.generations.impl.ManageFileGenerationsServiceImpl;
import org.finos.legend.depot.services.projects.ManageProjectsServiceImpl;
import org.finos.legend.depot.services.api.projects.configuration.ProjectsConfiguration;
import org.finos.legend.depot.store.api.entities.UpdateEntities;
import org.finos.legend.depot.store.api.generations.UpdateFileGenerations;
import org.finos.legend.depot.store.api.projects.UpdateProjects;
import org.finos.legend.depot.store.api.projects.UpdateProjectsVersions;
import org.finos.legend.depot.services.api.artifacts.purge.ArtifactsPurgeService;
import org.finos.legend.depot.services.api.artifacts.handlers.ProjectArtifactHandlerFactory;
import org.finos.legend.depot.services.artifacts.handlers.entities.EntitiesHandlerImpl;
import org.finos.legend.depot.services.artifacts.handlers.entities.EntityProvider;
import org.finos.legend.depot.services.artifacts.handlers.generations.FileGenerationHandlerImpl;
import org.finos.legend.depot.services.artifacts.handlers.generations.FileGenerationsProvider;
import org.finos.legend.depot.services.api.metrics.query.QueryMetricsRegistry;
import org.finos.legend.depot.services.metrics.query.InMemoryQueryMetricsRegistry;
import org.finos.legend.depot.services.metrics.query.QueryMetricsServiceImpl;
import org.finos.legend.depot.store.mongo.metrics.query.QueryMetricsMongo;
import org.finos.legend.depot.store.mongo.entities.EntitiesMongo;
import org.finos.legend.depot.store.mongo.entities.test.EntitiesMongoTestUtils;
import org.finos.legend.depot.store.mongo.generations.FileGenerationsMongo;
import org.finos.legend.depot.store.mongo.projects.ProjectsMongo;
import org.finos.legend.depot.store.mongo.projects.ProjectsVersionsMongo;
import org.finos.legend.depot.services.api.notifications.queue.Queue;
import org.finos.legend.depot.store.mongo.generations.TestGenerationsStoreMongo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Date;
import java.util.Collections;

import static org.finos.legend.depot.domain.version.VersionValidator.BRANCH_SNAPSHOT;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class TestArtifactsPurgeService extends TestBaseServices
{

    public static final String TEST_GROUP_ID = "examples.metadata";
    public static final String TEST_ARTIFACT_ID = "test";

    protected UpdateProjects projectsStore = new ProjectsMongo(mongoProvider);
    protected UpdateProjectsVersions projectsVersionsStore = new ProjectsVersionsMongo(mongoProvider);
    private final QueryMetricsMongo metrics = new QueryMetricsMongo(mongoProvider);
    private final QueryMetricsRegistry metricsRegistry = new InMemoryQueryMetricsRegistry();
    private final QueryMetricsService metricHandler = new QueryMetricsServiceImpl(metrics);
    private final Queue queue = mock(Queue.class);
    protected ManageProjectsService projectsService = new ManageProjectsServiceImpl(projectsVersionsStore, projectsStore, metricsRegistry, queue, new ProjectsConfiguration("master"));
    protected UpdateEntities entitiesStore = new EntitiesMongo(mongoProvider);
    protected UpdateFileGenerations fileGenerationsStore = new FileGenerationsMongo(mongoProvider);
    protected ArtifactRepository repository = mock(ArtifactRepository.class);
    protected VersionsReconciliationServiceImpl versionsMismatchService = new VersionsReconciliationServiceImpl(repository, projectsService);
    protected ManageEntitiesService entitiesService = new ManageEntitiesServiceImpl(entitiesStore, projectsService);
    protected ArtifactsPurgeService purgeService = new ArtifactsPurgeServiceImpl(projectsService, versionsMismatchService, metricHandler);



    @BeforeEach
    public void setUpData()
    {
        ProjectArtifactHandlerFactory.registerArtifactHandler(ArtifactType.ENTITIES, new EntitiesHandlerImpl(entitiesService, mock(EntityProvider.class)));
        ProjectArtifactHandlerFactory.registerArtifactHandler(ArtifactType.FILE_GENERATIONS, new FileGenerationHandlerImpl(mock(ArtifactRepository.class), mock(FileGenerationsProvider.class), new ManageFileGenerationsServiceImpl(fileGenerationsStore, projectsService)));

        setUpProjectsFromFile(TestArtifactsPurgeService.class.getClassLoader().getResource("data/projects.json"));
        setUpProjectsVersionsFromFile(TestArtifactsPurgeService.class.getClassLoader().getResource("data/projectsVersions.json"));
        Assertions.assertEquals(3, projectsStore.getAll().size());

        new EntitiesMongoTestUtils(mongoProvider).loadEntities(TestArtifactsPurgeService.class.getClassLoader().getResource("data/entities.json"));
        TestGenerationsStoreMongo.setUpFileGenerationFromFile(TestArtifactsPurgeService.class.getClassLoader().getResource("data/generations.json"),mongoProvider);
    }


    @Test
    public void canEvictOldVersions()
    {

        StoreProjectData project = projectsStore.find(TEST_GROUP_ID,TEST_ARTIFACT_ID).get();
        Assertions.assertNotNull(project);
        List<String> projectVersions = projectsService.getVersions(TEST_GROUP_ID, TEST_ARTIFACT_ID);
        Assertions.assertEquals(3, projectVersions.size());
        Assertions.assertEquals("2.0.0", projectVersions.get(0));
        Assertions.assertEquals("2.3.0", project.getLatestVersion());

        Assertions.assertEquals(2, entitiesStore.getAllEntities(TEST_GROUP_ID, TEST_ARTIFACT_ID, "2.0.0").size());
        purgeService.evictOldestProjectVersions(TEST_GROUP_ID,TEST_ARTIFACT_ID,1);

        List<String> afterVersionsOrdered = projectsService.getVersions(TEST_GROUP_ID, TEST_ARTIFACT_ID);
        Assertions.assertEquals(3, afterVersionsOrdered.size());
        Assertions.assertTrue(projectsService.find(TEST_GROUP_ID, TEST_ARTIFACT_ID, "2.0.0").get().isEvicted());
        Assertions.assertTrue(projectsService.find(TEST_GROUP_ID, TEST_ARTIFACT_ID, "2.2.0").get().isEvicted());
        Assertions.assertTrue(afterVersionsOrdered.contains("2.3.0"));
        Assertions.assertEquals(0, entitiesStore.getAllEntities(TEST_GROUP_ID, TEST_ARTIFACT_ID, "2.0.0").size());
        Assertions.assertEquals(0, entitiesStore.getAllEntities(TEST_GROUP_ID, TEST_ARTIFACT_ID, "2.2.0").size());
        Assertions.assertEquals(2, entitiesStore.getAllEntities(TEST_GROUP_ID, TEST_ARTIFACT_ID, "2.3.0").size());
    }

    @Test
    public void canEvictOldVersionsKeepMoreThanExists()
    {

        StoreProjectData project = projectsStore.find(TEST_GROUP_ID,TEST_ARTIFACT_ID).get();
        Assertions.assertNotNull(project);
        List<String> projectVersions = projectsService.getVersions(TEST_GROUP_ID, TEST_ARTIFACT_ID);
        Assertions.assertEquals(3, projectVersions.size());
        MetadataNotificationResponse response = purgeService.evictOldestProjectVersions(TEST_GROUP_ID,TEST_ARTIFACT_ID,5);
        Assertions.assertNotNull(response);

        List<String> afterVersionsOrdered = projectsService.getVersions(TEST_GROUP_ID, TEST_ARTIFACT_ID);
        Assertions.assertEquals(3, afterVersionsOrdered.size());
        Assertions.assertTrue(!projectsService.find(TEST_GROUP_ID, TEST_ARTIFACT_ID, "2.0.0").get().isEvicted());
        Assertions.assertTrue(!projectsService.find(TEST_GROUP_ID, TEST_ARTIFACT_ID, "2.2.0").get().isEvicted());
        Assertions.assertTrue(!projectsService.find(TEST_GROUP_ID, TEST_ARTIFACT_ID, "2.3.0").get().isEvicted());
        Assertions.assertEquals(2, entitiesStore.getAllEntities(TEST_GROUP_ID, TEST_ARTIFACT_ID, "2.0.0").size());
        Assertions.assertEquals(2, entitiesStore.getAllEntities(TEST_GROUP_ID, TEST_ARTIFACT_ID, "2.2.0").size());
        Assertions.assertEquals(2, entitiesStore.getAllEntities(TEST_GROUP_ID, TEST_ARTIFACT_ID, "2.3.0").size());
    }

    @Test
    public void canEvictOldVersionsNotEnoughVersionsToKeep()
    {

        StoreProjectData project = projectsStore.find(TEST_GROUP_ID,"test1").get();
        Assertions.assertNotNull(project);
        List<String> projectVersions = projectsService.getVersions(TEST_GROUP_ID, "test1");
        Assertions.assertEquals(0, projectVersions.size());
        Assertions.assertEquals(1, entitiesStore.getAllEntities(TEST_GROUP_ID, "test1", BRANCH_SNAPSHOT("master")).size());

        MetadataNotificationResponse response = purgeService.evictOldestProjectVersions(TEST_GROUP_ID,"test1",1);
        Assertions.assertNotNull(response);

        List<String> afterVersionsOrdered = projectsService.getVersions(TEST_GROUP_ID, "test1");
        Assertions.assertEquals(0, afterVersionsOrdered.size());

        Assertions.assertEquals(1, entitiesStore.getAllEntities(TEST_GROUP_ID, "test1", BRANCH_SNAPSHOT("master")).size());
    }

    @Test
    public void canEvictVersion()
    {
        String versionId = "2.0.0";

        Assertions.assertEquals(2, entitiesStore.getAllEntities(TEST_GROUP_ID, TEST_ARTIFACT_ID, versionId).size());
        Assertions.assertEquals(2, entitiesStore.getAllEntities(TEST_GROUP_ID, TEST_ARTIFACT_ID, versionId).size());
        Assertions.assertEquals(3, fileGenerationsStore.getAll().size());

        purgeService.evict(TEST_GROUP_ID, TEST_ARTIFACT_ID, versionId);
        Assertions.assertEquals(0, entitiesStore.getAllEntities(TEST_GROUP_ID, TEST_ARTIFACT_ID, versionId).size());
        Assertions.assertEquals(0, fileGenerationsStore.find(TEST_GROUP_ID, TEST_ARTIFACT_ID, versionId).size());
        Assertions.assertTrue(projectsService.getVersions(TEST_GROUP_ID,TEST_ARTIFACT_ID).contains(versionId));
        Assertions.assertTrue(projectsService.find(TEST_GROUP_ID, TEST_ARTIFACT_ID, versionId).get().isEvicted());
    }

    @Test
    public void canDeleteVersion()
    {
        String versionId = "2.0.0";

        Assertions.assertEquals(2, entitiesStore.getAllEntities(TEST_GROUP_ID, TEST_ARTIFACT_ID, versionId).size());
        Assertions.assertEquals(2, entitiesStore.getAllEntities(TEST_GROUP_ID, TEST_ARTIFACT_ID, versionId).size());
        Assertions.assertEquals(3, fileGenerationsStore.getAll().size());

        purgeService.delete(TEST_GROUP_ID, TEST_ARTIFACT_ID, versionId);
        Assertions.assertEquals(0, entitiesStore.getAllEntities(TEST_GROUP_ID, TEST_ARTIFACT_ID, versionId).size());
        Assertions.assertEquals(0, fileGenerationsStore.find(TEST_GROUP_ID, TEST_ARTIFACT_ID, versionId).size());
        Assertions.assertFalse(projectsService.getVersions(TEST_GROUP_ID,TEST_ARTIFACT_ID).contains(versionId));
        Assertions.assertFalse(projectsService.find(TEST_GROUP_ID, TEST_ARTIFACT_ID, versionId).isPresent());
    }

    @Test
    public void canDeprecateVersion()
    {
        String versionId = "2.0.0";
        List<String> versions = projectsService.getVersions(TEST_GROUP_ID,TEST_ARTIFACT_ID);
        Assertions.assertEquals(versions.size(), 3);
        Assertions.assertEquals(1, fileGenerationsStore.find(TEST_GROUP_ID, TEST_ARTIFACT_ID, versionId).size());
        //deprecating version
        purgeService.deprecate(TEST_GROUP_ID, TEST_ARTIFACT_ID, versionId);
        versions = projectsService.getVersions(TEST_GROUP_ID,TEST_ARTIFACT_ID);
        //no versions deleted
        Assertions.assertEquals(versions, Arrays.asList("2.0.0", "2.2.0", "2.3.0"));
        //no artifacts deleted
        Assertions.assertEquals(1, fileGenerationsStore.find(TEST_GROUP_ID, TEST_ARTIFACT_ID, versionId).size());
        Optional<StoreProjectVersionData> storeProjectData = projectsService.find(TEST_GROUP_ID, TEST_ARTIFACT_ID, versionId);
        Assertions.assertTrue(storeProjectData.get().getVersionData().isDeprecated());
    }

    @Test
    public void canDeprecateVersionIfNotInRepository()
    {
        String versionId = "2.0.0";
        VersionMismatch versionMismatch = new VersionMismatch("PROD-A", TEST_GROUP_ID, TEST_ARTIFACT_ID, Collections.EMPTY_LIST, Collections.singletonList(versionId), Collections.EMPTY_LIST);
        List<String> versions = projectsService.getVersions(TEST_GROUP_ID,TEST_ARTIFACT_ID);
        Assertions.assertEquals(versions.size(), 3);
        Assertions.assertEquals(3, fileGenerationsStore.getAll().size());
        when(versionsMismatchService.findVersionsMismatches()).thenReturn(Collections.singletonList(versionMismatch));
        //deleting the version not present in the repository
        purgeService.deprecateVersionsNotInRepository();
        versions = projectsService.getVersions(TEST_GROUP_ID,TEST_ARTIFACT_ID);
        Assertions.assertEquals(versions.size(), 3);
        //checking if version data shows deprecation
        StoreProjectVersionData projectVersionData = projectsService.find(TEST_GROUP_ID, TEST_ARTIFACT_ID, versionId).get();
        Assertions.assertTrue(projectVersionData.getVersionData().isDeprecated());
    }

    @Test
    public void canEvictLeastRecentlyUsed()
    {
        projectsService.createOrUpdate(new StoreProjectVersionData(TEST_GROUP_ID, TEST_ARTIFACT_ID,"branch1-SNAPSHOT"));
        metrics.insert(new VersionQueryMetric(TEST_GROUP_ID, TEST_ARTIFACT_ID,"2.0.0", DatesHandler.toDate(LocalDateTime.now().minusDays(366))));
        metrics.insert(new VersionQueryMetric(TEST_GROUP_ID, TEST_ARTIFACT_ID, "branch1-SNAPSHOT", DatesHandler.toDate(LocalDateTime.now().minusDays(30))));
        metrics.insert(new VersionQueryMetric(TEST_GROUP_ID, TEST_ARTIFACT_ID, "master-SNAPSHOT", new Date()));
        purgeService.evictLeastRecentlyUsed(365, 30);

        StoreProjectVersionData version1 = projectsService.find(TEST_GROUP_ID, TEST_ARTIFACT_ID, "2.0.0").get();
        Assertions.assertTrue(version1.isEvicted());
        StoreProjectVersionData version2 = projectsService.find(TEST_GROUP_ID, TEST_ARTIFACT_ID, "branch1-SNAPSHOT").get();
        Assertions.assertTrue(version2.isEvicted());
        StoreProjectVersionData version3 = projectsService.find(TEST_GROUP_ID, TEST_ARTIFACT_ID, "master-SNAPSHOT").get();
        Assertions.assertFalse(version3.isEvicted());
    }

    @Test
    public void canEvictVersionsNotUsed()
    {
        projectsService.createOrUpdate(new StoreProjectVersionData(TEST_GROUP_ID, TEST_ARTIFACT_ID,"branch1-SNAPSHOT"));
        metrics.insert(new VersionQueryMetric(TEST_GROUP_ID, TEST_ARTIFACT_ID, "master-SNAPSHOT", new Date()));
        purgeService.evictVersionsNotUsed();

        StoreProjectVersionData version1 = projectsService.find(TEST_GROUP_ID, TEST_ARTIFACT_ID, "2.0.0").get();
        Assertions.assertTrue(version1.isEvicted());
        StoreProjectVersionData version2 = projectsService.find(TEST_GROUP_ID, TEST_ARTIFACT_ID, "branch1-SNAPSHOT").get();
        Assertions.assertTrue(version2.isEvicted());
        StoreProjectVersionData version3 = projectsService.find(TEST_GROUP_ID, TEST_ARTIFACT_ID, "master-SNAPSHOT").get();
        Assertions.assertFalse(version3.isEvicted());
    }

}
