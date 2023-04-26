//  Copyright 2022 Goldman Sachs
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

package org.finos.legend.depot.server.pure.model.context;

import org.finos.legend.depot.domain.project.StoreProjectData;
import org.finos.legend.depot.domain.project.StoreProjectVersionData;
import org.finos.legend.depot.server.pure.model.context.api.PureModelContextService;
import org.finos.legend.depot.server.pure.model.context.services.PureModelContextServiceImpl;
import org.finos.legend.depot.services.TestBaseServices;
import org.finos.legend.depot.services.api.entities.EntitiesService;
import org.finos.legend.depot.services.api.projects.ProjectsService;
import org.finos.legend.depot.services.entities.EntitiesServiceImpl;
import org.finos.legend.depot.services.projects.ProjectsServiceImpl;
import org.finos.legend.depot.store.mongo.TestStoreMongo;
import org.finos.legend.engine.protocol.pure.v1.model.context.AlloySDLC;
import org.finos.legend.engine.protocol.pure.v1.model.context.PureModelContextData;
import org.finos.legend.sdlc.domain.model.entity.Entity;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.net.URL;
import java.util.List;

import static org.finos.legend.depot.domain.version.VersionValidator.MASTER_SNAPSHOT;

public class TestPureModelContextService extends TestBaseServices
{
    protected static final URL projects = TestStoreMongo.class.getClassLoader().getResource("allProjectVersions.json");
    protected static final URL revisionEntities = TestStoreMongo.class.getClassLoader().getResource("data/revision-entities.json");
    private static final URL versionedEntities = TestStoreMongo.class.getClassLoader().getResource("data/versioned-entities.json");
    private static final URL entities_16538 = TestPureModelContextService.class.getClassLoader().getResource("versioned-entities_PROD-16538.json");
    private static final URL entities_10357 = TestPureModelContextService.class.getClassLoader().getResource("versioned-entities_PROD-10357.json");
    private static final URL entities_10855 = TestPureModelContextService.class.getClassLoader().getResource("versioned-entities_PROD-10855.json");
    public static final String TEST_GROUP_ID = "examples.metadata";
    public static final String CLIENT_VERSION = "vX_X_X";

    ProjectsService projectsService = new ProjectsServiceImpl(projectsVersionsStore, projectsStore);
    private final PureModelContextService service = new PureModelContextServiceImpl(new EntitiesServiceImpl(entitiesStore, projectsService), projectsService);


    @Before
    public void setupMetadata()
    {
        projectsStore.createOrUpdate(new StoreProjectData("PROD-1","test.legend","blank-prod"));
        setUpProjectsVersionsFromFile(projects);
        Assert.assertEquals(4, projectsService.getAllProjectCoordinates().size());
        setUpEntitiesDataFromFile(versionedEntities);
        setUpEntitiesDataFromFile(revisionEntities);
        setUpEntitiesDataFromFile(entities_16538);
        setUpEntitiesDataFromFile(entities_10357);
        setUpEntitiesDataFromFile(entities_10855);
    }

    @Test
    public void canGetEntitiesForProjectAsPureModelContextData()
    {

        String modelContextDataAsString = service.getPureModelContextDataAsString(TEST_GROUP_ID, "test", "2.2.0", CLIENT_VERSION, false, false);
        Assert.assertNotNull(modelContextDataAsString);
        Assert.assertEquals("{\"_type\":\"data\",\"elements\":[{\"_type\":\"profile\",\"name\":\"TestProfile\",\"package\":\"examples::metadata::test\",\"stereotypes\":[],\"tags\":[]},{\"_type\":\"class\",\"constraints\":[],\"name\":\"ClientBasic\",\"originalMilestonedProperties\":[],\"package\":\"examples::metadata::test\",\"properties\":[{\"multiplicity\":{\"lowerBound\":1,\"upperBound\":1},\"name\":\"Name\",\"stereotypes\":[],\"taggedValues\":[],\"type\":\"String\"},{\"multiplicity\":{\"lowerBound\":1,\"upperBound\":1},\"name\":\"EntityId\",\"stereotypes\":[],\"taggedValues\":[],\"type\":\"Integer\"},{\"multiplicity\":{\"lowerBound\":1,\"upperBound\":1},\"name\":\"IsActive\",\"stereotypes\":[],\"taggedValues\":[],\"type\":\"Boolean\"},{\"multiplicity\":{\"lowerBound\":1,\"upperBound\":1},\"name\":\"RiskScore\",\"stereotypes\":[],\"taggedValues\":[],\"type\":\"Float\"},{\"multiplicity\":{\"lowerBound\":1,\"upperBound\":1},\"name\":\"IncorporationDate\",\"stereotypes\":[],\"taggedValues\":[],\"type\":\"StrictDate\"},{\"multiplicity\":{\"lowerBound\":0,\"upperBound\":1},\"name\":\"OptionalAlternativeName\",\"stereotypes\":[],\"taggedValues\":[],\"type\":\"String\"},{\"multiplicity\":{\"lowerBound\":0,\"upperBound\":1},\"name\":\"newProperty\",\"stereotypes\":[],\"taggedValues\":[],\"type\":\"String\"}],\"qualifiedProperties\":[],\"stereotypes\":[],\"superTypes\":[],\"taggedValues\":[]},{\"_type\":\"profile\",\"name\":\"TestProfileTwo\",\"package\":\"examples::metadata::test::subpackage\",\"stereotypes\":[],\"tags\":[]}],\"origin\":{\"_type\":\"pointer\",\"sdlcInfo\":{\"_type\":\"alloy\",\"baseVersion\":\"2.2.0\",\"packageableElementPointers\":[],\"project\":\"examples.metadata:test\",\"version\":\"none\"},\"serializer\":{\"name\":\"pure\",\"version\":\"vX_X_X\"}},\"serializer\":{\"name\":\"pure\",\"version\":\"vX_X_X\"}}", modelContextDataAsString);
    }

    @Test(expected = RuntimeException.class)
    public void testNonExistentProjectOrVersion()
    {
        service.getPureModelContextDataAsString("non-existent-project", "tst", "2.0.0", CLIENT_VERSION, false, false);
    }

    @Test(expected = RuntimeException.class)
    public void testNonExistentProjectOrVersion1()
    {
        service.getPureModelContextDataAsString(TEST_GROUP_ID, "non-existent-version", "test", CLIENT_VERSION, false, false);
    }

    @Test
    public void canGetEntitiesAsPureModelContextData()
    {
        String modelContextDataAsString = service.getPureModelContextDataAsString(TEST_GROUP_ID, "test", MASTER_SNAPSHOT, CLIENT_VERSION, false, false);
        Assert.assertNotNull(modelContextDataAsString);
        Assert.assertEquals("{\"_type\":\"data\",\"elements\":[{\"_type\":\"class\",\"constraints\":[],\"name\":\"ClassWithDependency\",\"originalMilestonedProperties\":[],\"package\":\"examples::metadata::test\",\"properties\":[{\"multiplicity\":{\"lowerBound\":1,\"upperBound\":1},\"name\":\"Name\",\"stereotypes\":[],\"taggedValues\":[],\"type\":\"String\"}],\"qualifiedProperties\":[],\"stereotypes\":[],\"superTypes\":[],\"taggedValues\":[]},{\"_type\":\"profile\",\"name\":\"TestProfile\",\"package\":\"examples::metadata::test\",\"stereotypes\":[],\"tags\":[]},{\"_type\":\"class\",\"constraints\":[],\"name\":\"ClientBasic\",\"originalMilestonedProperties\":[],\"package\":\"examples::metadata::test\",\"properties\":[{\"multiplicity\":{\"lowerBound\":1,\"upperBound\":1},\"name\":\"Name\",\"stereotypes\":[],\"taggedValues\":[],\"type\":\"String\"},{\"multiplicity\":{\"lowerBound\":1,\"upperBound\":1},\"name\":\"EntityId\",\"stereotypes\":[],\"taggedValues\":[],\"type\":\"Integer\"},{\"multiplicity\":{\"lowerBound\":1,\"upperBound\":1},\"name\":\"IsActive\",\"stereotypes\":[],\"taggedValues\":[],\"type\":\"Boolean\"},{\"multiplicity\":{\"lowerBound\":1,\"upperBound\":1},\"name\":\"RiskScore\",\"stereotypes\":[],\"taggedValues\":[],\"type\":\"Float\"},{\"multiplicity\":{\"lowerBound\":1,\"upperBound\":1},\"name\":\"IncorporationDate\",\"stereotypes\":[],\"taggedValues\":[],\"type\":\"StrictDate\"},{\"multiplicity\":{\"lowerBound\":0,\"upperBound\":1},\"name\":\"OptionalAlternativeName\",\"stereotypes\":[],\"taggedValues\":[],\"type\":\"String\"},{\"multiplicity\":{\"lowerBound\":0,\"upperBound\":1},\"name\":\"newProperty\",\"stereotypes\":[],\"taggedValues\":[],\"type\":\"String\"}],\"qualifiedProperties\":[],\"stereotypes\":[],\"superTypes\":[],\"taggedValues\":[]},{\"_type\":\"profile\",\"name\":\"TestProfileTwo\",\"package\":\"examples::metadata::test::subpackage\",\"stereotypes\":[],\"tags\":[]}],\"origin\":{\"_type\":\"pointer\",\"sdlcInfo\":{\"_type\":\"alloy\",\"baseVersion\":\"master-SNAPSHOT\",\"packageableElementPointers\":[],\"project\":\"examples.metadata:test\",\"version\":\"none\"},\"serializer\":{\"name\":\"pure\",\"version\":\"vX_X_X\"}},\"serializer\":{\"name\":\"pure\",\"version\":\"vX_X_X\"}}", modelContextDataAsString);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNonExistentProject()
    {
        service.getPureModelContextDataAsString("non.existent.project", "test",MASTER_SNAPSHOT, CLIENT_VERSION, false, false);
    }

    @Test
    public void testErrorThrownWhenNoProjectVersionFound()
    {

        EntitiesService mockVersions = Mockito.mock(EntitiesService.class);
        PureModelContextService newService = new PureModelContextServiceImpl(mockVersions, projectsService);
        Assert.assertThrows("project version not found for test.legend-blank-prod-master-SNAPSHOT", IllegalArgumentException.class, () -> newService.getPureModelContextDataAsString("test.legend", "blank-prod", MASTER_SNAPSHOT, CLIENT_VERSION, false, false));
    }

    @Test
    public void canGetEntitiesForProjectAsPureModelContextData_WithDependencies()
    {
        projectsVersionsStore.createOrUpdate(new StoreProjectVersionData("examples.metadata", "test-dependencies", "2.0.0"));
        projectsVersionsStore.createOrUpdate(new StoreProjectVersionData("examples.metadata", "test", "3.0.0"));
        List<Entity> entityList1 = entitiesStore.getEntities("test.legend", "blank-prod", "2.0.0", true);
        List<Entity> entityList2 = entitiesStore.getEntities("examples.metadata", "test", "3.0.0", true);
        List<Entity> entityList3 = entitiesStore.getEntities("examples.metadata", "test-dependencies", "2.0.0", true);
        Assert.assertNotNull(entityList1);
        Assert.assertNotNull(entityList2);
        Assert.assertNotNull(entityList3);
        Assert.assertEquals(2, entityList1.size());
        Assert.assertEquals(5, entityList2.size());
        Assert.assertEquals(6, entityList3.size());

        String modelContextDataAsString = service.getPureModelContextDataAsString("test.legend", "blank-prod", "2.0.0", CLIENT_VERSION, true, true);
        Assert.assertNotNull(modelContextDataAsString);
        Assert.assertEquals("{\"_type\":\"data\",\"elements\":[{\"_type\":\"service\",\"autoActivateUpdates\":true,\"documentation\":\"\",\"execution\":{\"_type\":\"pureMultiExecution\",\"executionKey\":\"env\",\"executionParameters\":[{\"key\":\"PROD\",\"mapping\":\"mapping::SomeMapping\",\"runtime\":{\"_type\":\"runtimePointer\",\"runtime\":\"runtime::H2Runtime\"}},{\"key\":\"DEV\",\"mapping\":\"mapping::SomeMapping\",\"runtime\":{\"_type\":\"runtimePointer\",\"runtime\":\"runtime::H2Runtime\"}}],\"func\":{\"_type\":\"lambda\",\"body\":[{\"_type\":\"func\",\"function\":\"project\",\"parameters\":[{\"_type\":\"func\",\"function\":\"getAll\",\"parameters\":[{\"_type\":\"packageableElementPtr\",\"fullPath\":\"domain::COVIDData\"}]},{\"_type\":\"collection\",\"multiplicity\":{\"lowerBound\":1,\"upperBound\":1},\"values\":[{\"_type\":\"lambda\",\"body\":[{\"_type\":\"property\",\"parameters\":[{\"_type\":\"var\",\"name\":\"x\"}],\"property\":\"cases\"}],\"parameters\":[{\"_type\":\"var\",\"name\":\"x\"}]}]},{\"_type\":\"collection\",\"multiplicity\":{\"lowerBound\":1,\"upperBound\":1},\"values\":[{\"_type\":\"string\",\"value\":\"Cases\"}]}]}],\"parameters\":[]}},\"name\":\"SomeService\",\"owners\":[\"anonymous\",\"akphi\"],\"package\":\"service\",\"pattern\":\"/9566f101-2108-408f-863f-6d7e154dc17a\",\"postValidations\":[],\"stereotypes\":[],\"taggedValues\":[]},{\"_type\":\"class\",\"constraints\":[],\"name\":\"Person\",\"originalMilestonedProperties\":[],\"package\":\"test::legend::blank_prod::v2_0_0::blank\",\"properties\":[],\"qualifiedProperties\":[],\"sourceInformation\":{\"endColumn\":1,\"endLine\":3,\"sourceId\":\"\",\"startColumn\":1,\"startLine\":1},\"stereotypes\":[],\"superTypes\":[],\"taggedValues\":[]},{\"_type\":\"class\",\"constraints\":[],\"name\":\"test\",\"originalMilestonedProperties\":[],\"package\":\"test::legend::metadata::test::v3_0_0::com\",\"properties\":[],\"qualifiedProperties\":[],\"stereotypes\":[],\"superTypes\":[],\"taggedValues\":[]},{\"_type\":\"class\",\"constraints\":[],\"name\":\"ClassWithDependency\",\"originalMilestonedProperties\":[],\"package\":\"test::legend::metadata::test::v3_0_0::test::legend::metadata::test\",\"properties\":[{\"multiplicity\":{\"lowerBound\":1,\"upperBound\":1},\"name\":\"Name\",\"stereotypes\":[],\"taggedValues\":[],\"type\":\"String\"}],\"qualifiedProperties\":[],\"stereotypes\":[],\"superTypes\":[],\"taggedValues\":[]},{\"_type\":\"class\",\"constraints\":[],\"name\":\"ClientBasic\",\"originalMilestonedProperties\":[],\"package\":\"test::legend::metadata::test::v3_0_0::test::legend::metadata::test\",\"properties\":[{\"multiplicity\":{\"lowerBound\":1,\"upperBound\":1},\"name\":\"Name\",\"stereotypes\":[],\"taggedValues\":[],\"type\":\"String\"},{\"multiplicity\":{\"lowerBound\":1,\"upperBound\":1},\"name\":\"EntityId\",\"stereotypes\":[],\"taggedValues\":[],\"type\":\"Integer\"},{\"multiplicity\":{\"lowerBound\":1,\"upperBound\":1},\"name\":\"IsActive\",\"stereotypes\":[],\"taggedValues\":[],\"type\":\"Boolean\"},{\"multiplicity\":{\"lowerBound\":1,\"upperBound\":1},\"name\":\"RiskScore\",\"stereotypes\":[],\"taggedValues\":[],\"type\":\"Float\"},{\"multiplicity\":{\"lowerBound\":1,\"upperBound\":1},\"name\":\"IncorporationDate\",\"stereotypes\":[],\"taggedValues\":[],\"type\":\"StrictDate\"},{\"multiplicity\":{\"lowerBound\":0,\"upperBound\":1},\"name\":\"OptionalAlternativeName\",\"stereotypes\":[],\"taggedValues\":[],\"type\":\"String\"},{\"multiplicity\":{\"lowerBound\":0,\"upperBound\":1},\"name\":\"newProperty\",\"stereotypes\":[],\"taggedValues\":[],\"type\":\"String\"}],\"qualifiedProperties\":[],\"stereotypes\":[],\"superTypes\":[],\"taggedValues\":[]},{\"_type\":\"profile\",\"name\":\"TestProfile\",\"package\":\"test::legend::metadata::test::v3_0_0::test::legend::metadata::test\",\"stereotypes\":[],\"tags\":[]},{\"_type\":\"profile\",\"name\":\"TestProfileTwo\",\"package\":\"test::legend::metadata::test::v3_0_0::test::legend::metadata::test::subpackage\",\"stereotypes\":[],\"tags\":[]}],\"origin\":{\"_type\":\"pointer\",\"sdlcInfo\":{\"_type\":\"alloy\",\"baseVersion\":\"2.0.0\",\"packageableElementPointers\":[],\"project\":\"test.legend:blank-prod\",\"version\":\"none\"},\"serializer\":{\"name\":\"pure\",\"version\":\"vX_X_X\"}},\"serializer\":{\"name\":\"pure\",\"version\":\"vX_X_X\"}}", modelContextDataAsString);

        PureModelContextData pureModelContextData = service.getPureModelContextData("test.legend", "blank-prod", "2.0.0", CLIENT_VERSION, true, true);
        Assert.assertNotNull(pureModelContextData);
        Assert.assertEquals("2.0.0", pureModelContextData.origin.sdlcInfo.baseVersion);
        Assert.assertEquals("test.legend:blank-prod", ((AlloySDLC) pureModelContextData.origin.sdlcInfo).project);
    }

    @Test
    public void canGetEntitiesForProjectAsPureModelContextDataWithLatestAsVersion()
    {
        List<Entity> entityList1 = entitiesStore.getEntities("test.legend", "blank-prod", "2.0.0", true);
        Assert.assertNotNull(entityList1);
        Assert.assertEquals(2, entityList1.size());

        String modelContextDataAsString = service.getPureModelContextDataAsString("test.legend", "blank-prod", "latest", CLIENT_VERSION, true, false);
        Assert.assertNotNull(modelContextDataAsString);
        Assert.assertEquals("{\"_type\":\"data\",\"elements\":[{\"_type\":\"service\",\"autoActivateUpdates\":true,\"documentation\":\"\",\"execution\":{\"_type\":\"pureMultiExecution\",\"executionKey\":\"env\",\"executionParameters\":[{\"key\":\"PROD\",\"mapping\":\"mapping::SomeMapping\",\"runtime\":{\"_type\":\"runtimePointer\",\"runtime\":\"runtime::H2Runtime\"}},{\"key\":\"DEV\",\"mapping\":\"mapping::SomeMapping\",\"runtime\":{\"_type\":\"runtimePointer\",\"runtime\":\"runtime::H2Runtime\"}}],\"func\":{\"_type\":\"lambda\",\"body\":[{\"_type\":\"func\",\"function\":\"project\",\"parameters\":[{\"_type\":\"func\",\"function\":\"getAll\",\"parameters\":[{\"_type\":\"packageableElementPtr\",\"fullPath\":\"domain::COVIDData\"}]},{\"_type\":\"collection\",\"multiplicity\":{\"lowerBound\":1,\"upperBound\":1},\"values\":[{\"_type\":\"lambda\",\"body\":[{\"_type\":\"property\",\"parameters\":[{\"_type\":\"var\",\"name\":\"x\"}],\"property\":\"cases\"}],\"parameters\":[{\"_type\":\"var\",\"name\":\"x\"}]}]},{\"_type\":\"collection\",\"multiplicity\":{\"lowerBound\":1,\"upperBound\":1},\"values\":[{\"_type\":\"string\",\"value\":\"Cases\"}]}]}],\"parameters\":[]}},\"name\":\"SomeService\",\"owners\":[\"anonymous\",\"akphi\"],\"package\":\"service\",\"pattern\":\"/9566f101-2108-408f-863f-6d7e154dc17a\",\"postValidations\":[],\"stereotypes\":[],\"taggedValues\":[]},{\"_type\":\"class\",\"constraints\":[],\"name\":\"Person\",\"originalMilestonedProperties\":[],\"package\":\"test::legend::blank_prod::v2_0_0::blank\",\"properties\":[],\"qualifiedProperties\":[],\"sourceInformation\":{\"endColumn\":1,\"endLine\":3,\"sourceId\":\"\",\"startColumn\":1,\"startLine\":1},\"stereotypes\":[],\"superTypes\":[],\"taggedValues\":[]}],\"origin\":{\"_type\":\"pointer\",\"sdlcInfo\":{\"_type\":\"alloy\",\"baseVersion\":\"2.0.0\",\"packageableElementPointers\":[],\"project\":\"test.legend:blank-prod\",\"version\":\"none\"},\"serializer\":{\"name\":\"pure\",\"version\":\"vX_X_X\"}},\"serializer\":{\"name\":\"pure\",\"version\":\"vX_X_X\"}}", modelContextDataAsString);
    }
}
