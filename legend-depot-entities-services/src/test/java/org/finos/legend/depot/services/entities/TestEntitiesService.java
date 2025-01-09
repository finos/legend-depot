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

import org.eclipse.collections.api.factory.Lists;
import org.finos.legend.depot.domain.entity.DepotEntity;
import org.finos.legend.depot.domain.entity.ProjectVersionEntities;
import org.finos.legend.depot.domain.project.ProjectVersion;
import org.finos.legend.depot.domain.project.dependencies.VersionDependencyReport;
import org.finos.legend.depot.domain.version.Scope;
import org.finos.legend.depot.services.TestBaseServices;
import org.finos.legend.depot.services.api.entities.EntityClassifierService;
import org.finos.legend.depot.services.api.entities.ManageEntitiesService;
import org.finos.legend.depot.services.api.metrics.query.QueryMetricsRegistry;
import org.finos.legend.depot.services.api.notifications.queue.Queue;
import org.finos.legend.depot.services.api.projects.ManageProjectsService;
import org.finos.legend.depot.services.api.projects.configuration.ProjectsConfiguration;
import org.finos.legend.depot.services.projects.ManageProjectsServiceImpl;
import org.finos.legend.depot.store.api.entities.UpdateEntities;
import org.finos.legend.depot.store.model.entities.EntityDefinition;
import org.finos.legend.depot.store.model.entities.StoredEntityData;
import org.finos.legend.depot.store.model.entities.StoredEntityStringData;
import org.finos.legend.depot.store.model.projects.StoreProjectData;
import org.finos.legend.depot.store.model.projects.StoreProjectVersionData;
import org.finos.legend.depot.store.mongo.entities.EntitiesMongo;
import org.finos.legend.depot.store.mongo.entities.test.EntitiesMongoTestUtils;
import org.finos.legend.sdlc.domain.model.entity.Entity;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import static org.finos.legend.depot.domain.version.VersionValidator.BRANCH_SNAPSHOT;
import static org.mockito.Mockito.mock;

public class TestEntitiesService extends TestBaseServices
{
    private final QueryMetricsRegistry metrics = mock(QueryMetricsRegistry.class);
    private final Queue queue = mock(Queue.class);
    private  EntitiesMongoTestUtils entityUtils = new EntitiesMongoTestUtils(mongoProvider);
    private ManageProjectsService projectsService = new ManageProjectsServiceImpl(projectsVersionsStore, projectsStore, metrics, queue, new ProjectsConfiguration("master"));
    protected UpdateEntities entitiesStore = new EntitiesMongo(mongoProvider);
    protected ManageEntitiesService entitiesService = new ManageEntitiesServiceImpl(entitiesStore, projectsService);
    protected EntityClassifierService classifierService = new EntityClassifierServiceImpl(projectsService, entitiesStore);

    @BeforeEach
    public void setUpData()
    {
        super.setUpData();

        StoreProjectVersionData project1 = projectsVersionsStore.find("examples.metadata", "test-dependencies", "1.0.0").get();
        //"PROD-A" -> "PROD-B" -> "PROD-C"
        ProjectVersion pv = new ProjectVersion("example.services.test", "test", "2.0.1");
        project1.getVersionData().addDependency(pv);
        projectsVersionsStore.createOrUpdate(project1);
        StoreProjectVersionData project2 = projectsVersionsStore.find("examples.metadata","test", "2.3.1").get();
        project2.setTransitiveDependenciesReport(new VersionDependencyReport(Collections.singletonList(pv), true));
        projectsVersionsStore.createOrUpdate(project2);
        entityUtils.loadEntities("PROD-A", "2.3.1");
        entityUtils.loadEntities("PROD-B", "1.0.0");
        entityUtils.loadEntities("PROD-C", "2.0.1");
    }

    @Test
    public void canGetDependencies()
    {

        List<Entity> entityList = entitiesService.getEntities("examples.metadata", "test", "2.3.1");
        Assertions.assertFalse(entityList.isEmpty());
        Assertions.assertEquals(7, entityList.size());


        List<Entity> entityList2 = entitiesService.getEntities("examples.metadata", "test-dependencies", "1.0.0");
        Assertions.assertFalse(entityList2.isEmpty());
        Assertions.assertEquals(1, entityList2.size());

        List<Entity> entityList3 = entitiesService.getEntities("example.services.test", "test", "2.0.1");
        Assertions.assertFalse(entityList3.isEmpty());
        Assertions.assertEquals(18, entityList3.size());

        List<ProjectVersionEntities> dependencyList = entitiesService.getDependenciesEntities("examples.metadata", "test", "2.3.1",false, false);
        Assertions.assertFalse(dependencyList.isEmpty());
        Assertions.assertEquals(1, dependencyList.size());
        Assertions.assertFalse(dependencyList.stream().filter(projectToArtifactFilter("examples.metadata", "test")).findFirst().isPresent());
        Assertions.assertEquals(1, dependencyList.stream().filter(projectToArtifactFilter("examples.metadata", "test-dependencies")).findFirst().get().getEntities().size());
        Assertions.assertFalse(dependencyList.stream().filter(projectToArtifactFilter("example.services.test", "test")).findFirst().isPresent());

        List<ProjectVersionEntities> dependencyList2 = entitiesService.getDependenciesEntities("examples.metadata", "test", "2.3.1", true, false);
        Assertions.assertFalse(dependencyList2.isEmpty());
        Assertions.assertEquals(2, dependencyList2.size());
        Assertions.assertFalse(dependencyList2.stream().filter(projectToArtifactFilter("examples.metadata", "test")).findFirst().isPresent());
        Assertions.assertEquals(1, dependencyList2.stream().filter(projectToArtifactFilter("examples.metadata", "test-dependencies")).findFirst().get().getEntities().size());
        Assertions.assertEquals(18, dependencyList2.stream().filter(projectToArtifactFilter("example.services.test", "test")).findFirst().get().getEntities().size());

        List<ProjectVersionEntities> dependencyList3 = entitiesService.getDependenciesEntities("examples.metadata", "test", "2.3.1", true, true);
        Assertions.assertFalse(dependencyList3.isEmpty());
        Assertions.assertEquals(3, dependencyList3.size());
        Assertions.assertEquals(7, dependencyList3.stream().filter(projectToArtifactFilter("examples.metadata", "test")).findFirst().get().getEntities().size());
        Assertions.assertEquals(1, dependencyList3.stream().filter(projectToArtifactFilter("examples.metadata", "test-dependencies")).findFirst().get().getEntities().size());
        Assertions.assertEquals(18, dependencyList3.stream().filter(projectToArtifactFilter("example.services.test", "test")).findFirst().get().getEntities().size());
    }

    private Predicate<ProjectVersionEntities> projectToArtifactFilter(String groupId,String artifactId)
    {
        List<StoreProjectVersionData> p = projectsVersionsStore.find(groupId, artifactId);
        Assertions.assertTrue(!p.isEmpty());
        return dep -> dep.getGroupId().equals(groupId) && dep.getArtifactId().equals(artifactId);
    }

    @Test
    public void canGetDependenciesMap()
    {
        List<ProjectVersion> projectVersions = Arrays.asList(new ProjectVersion("examples.metadata", "test", "2.3.1"), new ProjectVersion("examples.metadata", "test-dependencies", "1.0.0"));
        List<ProjectVersionEntities> dependencyList3 = entitiesService.getDependenciesEntities(projectVersions, true, true);
        Assertions.assertFalse(dependencyList3.isEmpty());
        Assertions.assertEquals(3, dependencyList3.size());
        Assertions.assertEquals(7, dependencyList3.stream().filter(projectToArtifactFilter("examples.metadata", "test")).findFirst().get().getEntities().size());
        Assertions.assertEquals(1, dependencyList3.stream().filter(projectToArtifactFilter("examples.metadata", "test-dependencies")).findFirst().get().getEntities().size());
        Assertions.assertEquals(18, dependencyList3.stream().filter(projectToArtifactFilter("example.services.test", "test")).findFirst().get().getEntities().size());

    }


    @Test
    public void canQueryEntitiesWithVersionInPackage()
    {
        projectsVersionsStore.createOrUpdate(new StoreProjectVersionData("examples.metadata","test1","1.0.0"));
        entityUtils.loadEntities("PROD-D", "1.0.0");

        String pkgName = "examples::metadata::test::dependency::v1_2_3";
        Assertions.assertEquals(2, entitiesService.getStoredEntities("examples.metadata","test1","1.0.0").size());
        entitiesService.getStoredEntities("examples.metadata","test1","1.0.0").stream().allMatch(e -> ((StoredEntityData)e).getEntity().getPath().startsWith(pkgName));
        entitiesService.getStoredEntities("examples.metadata","test1","1.0.0").stream().allMatch(e -> ((String)(((StoredEntityData)e).getEntity().getContent().get("package"))).startsWith(pkgName));;

        Assertions.assertEquals(2, entitiesService.getEntities("examples.metadata","test1","1.0.0").size());
        Assertions.assertEquals(2, entitiesService.getEntitiesByPackage("examples.metadata","test1","1.0.0",pkgName, Collections.EMPTY_SET,true).size());

    }

    @Test
    public void canQueryEntitiesWithLatestVersionAlias()
    {
        //validation on project id , can't bypass PROD-D
        projectsStore.createOrUpdate(new StoreProjectData("PROD-1","examples.metadata","test1",null, "1.0.0"));
        projectsVersionsStore.createOrUpdate(new StoreProjectVersionData("examples.metadata","test1","1.0.0"));
        // latest is derived from project data
        entityUtils.loadEntities("PROD-D", "1.0.0");

        String pkgName = "examples::metadata::test::dependency::v1_2_3";

        Assertions.assertEquals(2, entitiesService.getEntities("examples.metadata","test1","latest").size());
        Assertions.assertEquals(2, entitiesService.getEntitiesByPackage("examples.metadata","test1","latest", pkgName, Collections.EMPTY_SET,true).size());

    }

    @Test
    public void canGetDependenciesMapWithLatestAlias()
    {
        List<ProjectVersion> projectVersions = Arrays.asList(new ProjectVersion("examples.metadata", "test", "latest"), new ProjectVersion("examples.metadata", "test-dependencies", "latest"));
        List<ProjectVersionEntities> dependencyList3 = entitiesService.getDependenciesEntities(projectVersions,  true, true);
        Assertions.assertFalse(dependencyList3.isEmpty());
        Assertions.assertEquals(3, dependencyList3.size());
        Assertions.assertEquals(7, dependencyList3.stream().filter(projectToArtifactFilter("examples.metadata", "test")).findFirst().get().getEntities().size());
        Assertions.assertEquals(1, dependencyList3.stream().filter(projectToArtifactFilter("examples.metadata", "test-dependencies")).findFirst().get().getEntities().size());
        Assertions.assertEquals(18, dependencyList3.stream().filter(projectToArtifactFilter("example.services.test", "test")).findFirst().get().getEntities().size());
    }

    @Test
    public void canGetDependenciesMapWithHeadAlias()
    {
        List<ProjectVersion> projectVersions = Arrays.asList(new ProjectVersion("examples.metadata", "test", "head"), new ProjectVersion("examples.metadata", "test-dependencies", "latest"));
        List<ProjectVersionEntities> dependencyList3 = entitiesService.getDependenciesEntities(projectVersions, true, true);
        Assertions.assertFalse(dependencyList3.isEmpty());
        Assertions.assertEquals(3, dependencyList3.size());
        Assertions.assertEquals(1, dependencyList3.stream().filter(projectToArtifactFilter("examples.metadata", "test-dependencies")).findFirst().get().getEntities().size());
        Assertions.assertEquals(18, dependencyList3.stream().filter(projectToArtifactFilter("example.services.test", "test")).findFirst().get().getEntities().size());
    }

    @Test
    public void canQueryEntitiesWithHeadVersionAlias()
    {
        projectsVersionsStore.createOrUpdate(new StoreProjectVersionData("examples.metadata","test", BRANCH_SNAPSHOT("master")));
        entityUtils.loadEntities("PROD-A", BRANCH_SNAPSHOT("master"));

        String pkgName = "examples::metadata::test::v2_3_1::examples::metadata::test";

        Assertions.assertEquals(7, entitiesService.getEntities("examples.metadata","test","head").size());
        Assertions.assertEquals(0, entitiesService.getEntitiesByPackage("examples.metadata","test","head",pkgName, Collections.EMPTY_SET,true).size());

    }

    @Test
    public void canCreateAndUpdateEntities()
    {
        EntityDefinition entity = new EntityDefinition("examples::metadata::test::subpackage::TestProfileTwo", "meta::pure::metamodel::extension::Profile", null);
        entitiesService.createOrUpdate("examples.metadata", "test", "3.0.0", Arrays.asList(entity));

        List storedEntities = entitiesService.getStoredEntities("examples.metadata", "test", "3.0.0");
        Assertions.assertEquals(1, storedEntities.size());
        Assertions.assertTrue(storedEntities.get(0) instanceof StoredEntityStringData);

    }

    @Test
    public void canSerializeEntityDefinitionWithNulls()
    {
        Map<String, Object> content = new HashMap<>();
        content.put("package", "examples.metadata.test.TestProfile");
        content.put("stereoTypes", null);
        content.put("superTypes", Collections.emptyList());
        EntityDefinition entityDefinition = new EntityDefinition("examples.metadata.test.TestProfile", "meta::pure::metamodel::extension::Profile", content);
        projectsVersionsStore.createOrUpdate(new StoreProjectVersionData("examples.metadata", "test", "5.0.0"));
        entitiesService.createOrUpdate("examples.metadata", "test", "5.0.0", Arrays.asList(entityDefinition));

        // check entities serialization and deserialization
        Entity entity = (Entity) entitiesService.getEntities("examples.metadata", "test", "5.0.0").get(0);
        Assertions.assertEquals(content, entity.getContent());
    }

    @Test
    public void canGetClassifiers()
    {
        List<DepotEntity> entities = classifierService.getEntitiesByClassifierPath("meta::pure::metamodel::type::Class", null, null, Scope.RELEASES, true);
        Assertions.assertEquals(entities.size(), 3);
    }

    @Test
    public void canGetEntitiesForProjectAndVersionByClassifier()
    {
        projectsVersionsStore.createOrUpdate(new StoreProjectVersionData("example.services.test","test","2.0.2"));
        entityUtils.loadEntities("PROD-C", "2.0.2");
        List<Entity> entity = entitiesService.getEntitiesByClassifier("example.services.test", "test", "2.0.2", "meta::pure::metamodel::function::ConcreteFunctionDefinition");
        Assertions.assertNotNull(entity);
        Assertions.assertEquals(1, entity.size());
    }

    @Test
    public void canGetDependencyEntitiesForProjectAndVersionByClassifier()
    {
        StoreProjectVersionData project = new StoreProjectVersionData("examples.metadata", "test-dependencies", "1.0.1");
        ProjectVersion pv = new ProjectVersion("example.services.test", "test", "2.0.2");
        project.getVersionData().addDependency(pv);
        projectsVersionsStore.createOrUpdate(project);
        projectsVersionsStore.createOrUpdate(new StoreProjectVersionData("example.services.test","test","2.0.2"));
        entityUtils.loadEntities("PROD-B", "1.0.1");
        entityUtils.loadEntities("PROD-C", "2.0.2");
        List<Entity> entity = entitiesService.getDependenciesEntitiesByClassifier("examples.metadata", "test-dependencies", "1.0.1", "meta::pure::metamodel::function::ConcreteFunctionDefinition", true, true);
        Assertions.assertNotNull(entity);
        Assertions.assertEquals(2, entity.size());

        List<Entity> entity1 = entitiesService.getDependenciesEntitiesByClassifier("examples.metadata", "test-dependencies", "1.0.1", "meta::pure::metamodel::function::ConcreteFunctionDefinition", true, false);
        Assertions.assertNotNull(entity1);
        Assertions.assertEquals(1, entity1.size());
    }

    @Test
    public void canGetEntityFromDependencies()
    {
        List<Entity> entity = entitiesService.getEntityFromDependencies("examples.metadata", "test", "2.3.1", Lists.fixedSize.of("domain::covid::JHUCovid19","examples::metadata::test::dependency::Dependency"), false);
        Assertions.assertEquals(entity.size(), 2);

        entity = entitiesService.getEntityFromDependencies("examples.metadata", "test", "2.3.1", Lists.fixedSize.of("examples::metadata::test::dependency::Dependency","com::MyGenerationSpecification"), true);
        Assertions.assertEquals(entity.size(), 2);

        entity = entitiesService.getEntityFromDependencies("examples.metadata", "test", "2.3.1", Lists.fixedSize.of("examples::metadata::test::dependency::Dependency","covid::JHUCovid19"), false);
        Assertions.assertEquals(entity.size(), 1);

        entity = entitiesService.getEntityFromDependencies("examples.metadata", "test", "2.3.1", Lists.fixedSize.of("com::MyGenerationSpecification","covid::JHUCovid19"), false);
        Assertions.assertEquals(entity.size(), 0);

        Assertions.assertThrows(IllegalArgumentException.class, () -> entitiesService.getEntityFromDependencies("examples.metadata", "test", "3.0.1", Lists.fixedSize.of("covid::JHUCovid19"), false), "project version not found for examples.metadata-test-3.0.1");
    }
}
