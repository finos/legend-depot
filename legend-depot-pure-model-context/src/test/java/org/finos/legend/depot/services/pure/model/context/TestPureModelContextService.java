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

package org.finos.legend.depot.services.pure.model.context;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URL;
import java.util.List;
import static org.finos.legend.depot.domain.version.VersionValidator.BRANCH_SNAPSHOT;
import org.finos.legend.depot.services.TestBaseServices;
import org.finos.legend.depot.services.api.entities.EntitiesService;
import org.finos.legend.depot.services.api.metrics.query.QueryMetricsRegistry;
import org.finos.legend.depot.services.api.metrics.query.QueryMetricsService;
import org.finos.legend.depot.services.api.notifications.queue.Queue;
import org.finos.legend.depot.services.api.projects.ProjectsService;
import org.finos.legend.depot.services.api.projects.configuration.ProjectsConfiguration;
import org.finos.legend.depot.services.api.pure.model.context.PureModelContextService;
import org.finos.legend.depot.services.entities.EntitiesServiceImpl;
import org.finos.legend.depot.services.metrics.query.InMemoryQueryMetricsRegistry;
import org.finos.legend.depot.services.metrics.query.QueryMetricsServiceImpl;
import org.finos.legend.depot.services.projects.ProjectsServiceImpl;
import org.finos.legend.depot.store.api.entities.UpdateEntities;
import org.finos.legend.depot.store.api.metrics.query.QueryMetrics;
import org.finos.legend.depot.store.model.projects.StoreProjectData;
import org.finos.legend.depot.store.model.projects.StoreProjectVersionData;
import org.finos.legend.depot.store.mongo.entities.EntitiesMongo;
import org.finos.legend.depot.store.mongo.entities.test.EntitiesMongoTestUtils;
import org.finos.legend.depot.store.mongo.metrics.query.QueryMetricsMongo;
import org.finos.legend.engine.protocol.pure.v1.model.context.AlloySDLC;
import org.finos.legend.engine.protocol.pure.v1.model.context.PureModelContextData;
import org.finos.legend.engine.shared.core.ObjectMapperFactory;
import org.finos.legend.sdlc.domain.model.entity.Entity;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import static org.mockito.Mockito.mock;

public class TestPureModelContextService extends TestBaseServices
{
    public static final URL projects = TestPureModelContextService.class.getClassLoader().getResource("allProjectVersions.json");
    protected static final URL revisionEntities = TestPureModelContextService.class.getClassLoader().getResource("data/revision-entities.json");
    private static final URL versionedEntities = TestPureModelContextService.class.getClassLoader().getResource("data/versioned-entities.json");
    public static final URL entities_16538 = TestPureModelContextService.class.getClassLoader().getResource("versioned-entities_PROD-16538.json");
    private static final URL entities_10357 = TestPureModelContextService.class.getClassLoader().getResource("versioned-entities_PROD-10357.json");
    private static final URL entities_10855 = TestPureModelContextService.class.getClassLoader().getResource("versioned-entities_PROD-10855.json");
    public static final String TEST_GROUP_ID = "examples.metadata";
    public static final String CLIENT_VERSION = "vX_X_X";
    private final QueryMetrics metrics = new QueryMetricsMongo(mongoProvider);
    private final QueryMetricsRegistry metricsRegistry = new InMemoryQueryMetricsRegistry();
    private final QueryMetricsService metricsHandler = new QueryMetricsServiceImpl(metrics);
    private final Queue queue = mock(Queue.class);
    ProjectsService projectsService = new ProjectsServiceImpl(projectsVersionsStore, projectsStore, metricsRegistry, queue, new ProjectsConfiguration("master"));
    protected UpdateEntities<?> entitiesStore = new EntitiesMongo<>(mongoProvider);
    private final EntitiesMongoTestUtils entityUtils = new EntitiesMongoTestUtils(mongoProvider);
    private final PureModelContextService service = new PureModelContextServiceImpl(new EntitiesServiceImpl<>(entitiesStore, projectsService), projectsService);

    private final ObjectMapper objectMapper = ObjectMapperFactory.getNewStandardObjectMapperWithPureProtocolExtensionSupports().setSerializationInclusion(JsonInclude.Include.NON_NULL);

    private String getPureModelContextDataAsString(String groupId, String artifactId, String versionId, String clientVersion, boolean getDependencies, boolean convertToNewProtocol)
    {
        return toString(service.getPureModelContextData(groupId, artifactId, versionId, clientVersion, getDependencies, convertToNewProtocol));
    }

    private String toString(PureModelContextData contextData)
    {
        if (contextData.getElements().isEmpty())
        {
            return null;
        }
        try
        {
            return objectMapper.writeValueAsString(contextData);
        }
        catch (JsonProcessingException e)
        {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

    }

    @BeforeEach
    public void setupMetadata()
    {
        projectsStore.createOrUpdate(new StoreProjectData("PROD-1", "test.legend", "blank-prod", null, "2.0.0"));
        setUpProjectsVersionsFromFile(projects);
        Assertions.assertEquals(4, projectsService.getAllProjectCoordinates().size());
        entityUtils.loadEntities(versionedEntities);
        entityUtils.loadEntities(revisionEntities);
        entityUtils.loadEntities(entities_16538);
        entityUtils.loadEntities(entities_10357);
        entityUtils.loadEntities(entities_10855);
    }

    @Test
    public void canGetEntitiesForProjectAsPureModelContextData() throws JsonProcessingException
    {
        String modelContextDataAsString = getPureModelContextDataAsString(TEST_GROUP_ID, "test", "2.2.0", CLIENT_VERSION, false, true);
        Assertions.assertEquals("{\"_type\":\"data\",\"elements\":[{\"_type\":\"profile\",\"name\":\"TestProfile\",\"package\":\"examples::metadata::test\",\"stereotypes\":[],\"tags\":[]},{\"_type\":\"class\",\"constraints\":[],\"name\":\"ClientBasic\",\"originalMilestonedProperties\":[],\"package\":\"examples::metadata::test\",\"properties\":[{\"genericType\":{\"multiplicityArguments\":[],\"rawType\":{\"_type\":\"packageableType\",\"fullPath\":\"String\"},\"typeArguments\":[],\"typeVariableValues\":[]},\"multiplicity\":{\"lowerBound\":1,\"upperBound\":1},\"name\":\"Name\",\"stereotypes\":[],\"taggedValues\":[]},{\"genericType\":{\"multiplicityArguments\":[],\"rawType\":{\"_type\":\"packageableType\",\"fullPath\":\"Integer\"},\"typeArguments\":[],\"typeVariableValues\":[]},\"multiplicity\":{\"lowerBound\":1,\"upperBound\":1},\"name\":\"EntityId\",\"stereotypes\":[],\"taggedValues\":[]},{\"genericType\":{\"multiplicityArguments\":[],\"rawType\":{\"_type\":\"packageableType\",\"fullPath\":\"Boolean\"},\"typeArguments\":[],\"typeVariableValues\":[]},\"multiplicity\":{\"lowerBound\":1,\"upperBound\":1},\"name\":\"IsActive\",\"stereotypes\":[],\"taggedValues\":[]},{\"genericType\":{\"multiplicityArguments\":[],\"rawType\":{\"_type\":\"packageableType\",\"fullPath\":\"Float\"},\"typeArguments\":[],\"typeVariableValues\":[]},\"multiplicity\":{\"lowerBound\":1,\"upperBound\":1},\"name\":\"RiskScore\",\"stereotypes\":[],\"taggedValues\":[]},{\"genericType\":{\"multiplicityArguments\":[],\"rawType\":{\"_type\":\"packageableType\",\"fullPath\":\"StrictDate\"},\"typeArguments\":[],\"typeVariableValues\":[]},\"multiplicity\":{\"lowerBound\":1,\"upperBound\":1},\"name\":\"IncorporationDate\",\"stereotypes\":[],\"taggedValues\":[]},{\"genericType\":{\"multiplicityArguments\":[],\"rawType\":{\"_type\":\"packageableType\",\"fullPath\":\"String\"},\"typeArguments\":[],\"typeVariableValues\":[]},\"multiplicity\":{\"lowerBound\":0,\"upperBound\":1},\"name\":\"OptionalAlternativeName\",\"stereotypes\":[],\"taggedValues\":[]},{\"genericType\":{\"multiplicityArguments\":[],\"rawType\":{\"_type\":\"packageableType\",\"fullPath\":\"String\"},\"typeArguments\":[],\"typeVariableValues\":[]},\"multiplicity\":{\"lowerBound\":0,\"upperBound\":1},\"name\":\"newProperty\",\"stereotypes\":[],\"taggedValues\":[]}],\"qualifiedProperties\":[],\"stereotypes\":[],\"superTypes\":[],\"taggedValues\":[]},{\"_type\":\"profile\",\"name\":\"TestProfileTwo\",\"package\":\"examples::metadata::test::subpackage\",\"stereotypes\":[],\"tags\":[]}],\"origin\":{\"_type\":\"pointer\",\"sdlcInfo\":{\"_type\":\"alloy\",\"baseVersion\":\"2.2.0\",\"packageableElementPointers\":[],\"project\":\"examples.metadata:test\",\"version\":\"none\"},\"serializer\":{\"name\":\"pure\",\"version\":\"vX_X_X\"}},\"serializer\":{\"name\":\"pure\",\"version\":\"vX_X_X\"}}", modelContextDataAsString);

        String modelContextDataAsStringNoConversion = getPureModelContextDataAsString(TEST_GROUP_ID, "test", "2.2.0", CLIENT_VERSION, false, false);
        Assertions.assertEquals("{\"_type\":\"data\",\"elements\":[{\"_type\":\"profile\",\"name\":\"TestProfile\",\"package\":\"examples::metadata::test\"},{\"_type\":\"class\",\"name\":\"ClientBasic\",\"package\":\"examples::metadata::test\",\"properties\":[{\"multiplicity\":{\"lowerBound\":1,\"upperBound\":1},\"name\":\"Name\",\"type\":\"String\"},{\"multiplicity\":{\"lowerBound\":1,\"upperBound\":1},\"name\":\"EntityId\",\"type\":\"Integer\"},{\"multiplicity\":{\"lowerBound\":1,\"upperBound\":1},\"name\":\"IsActive\",\"type\":\"Boolean\"},{\"multiplicity\":{\"lowerBound\":1,\"upperBound\":1},\"name\":\"RiskScore\",\"type\":\"Float\"},{\"multiplicity\":{\"lowerBound\":1,\"upperBound\":1},\"name\":\"IncorporationDate\",\"type\":\"StrictDate\"},{\"multiplicity\":{\"lowerBound\":0,\"upperBound\":1},\"name\":\"OptionalAlternativeName\",\"type\":\"String\"},{\"multiplicity\":{\"lowerBound\":0,\"upperBound\":1},\"name\":\"newProperty\",\"type\":\"String\"}]},{\"_type\":\"profile\",\"name\":\"TestProfileTwo\",\"package\":\"examples::metadata::test::subpackage\"}],\"origin\":{\"_type\":\"pointer\",\"sdlcInfo\":{\"_type\":\"alloy\",\"baseVersion\":\"2.2.0\",\"packageableElementPointers\":[],\"project\":\"examples.metadata:test\",\"version\":\"none\"},\"serializer\":{\"name\":\"pure\",\"version\":\"vX_X_X\"}},\"serializer\":{\"name\":\"pure\",\"version\":\"vX_X_X\"}}", modelContextDataAsStringNoConversion);

        ObjectMapper mapper = ObjectMapperFactory.getNewStandardObjectMapperWithPureProtocolExtensionSupports();
        String roundTripWithConversion = mapper.writeValueAsString(mapper.readValue(modelContextDataAsStringNoConversion, PureModelContextData.class));
        Assertions.assertEquals(modelContextDataAsString, roundTripWithConversion);
    }

    @Test
    public void testNonExistentProjectOrVersion()
    {
        Assertions.assertThrows(RuntimeException.class, () -> getPureModelContextDataAsString("non-existent-project", "tst", "2.0.0", CLIENT_VERSION, false, true));
    }

    @Test
    public void testNonExistentProjectOrVersion1()
    {
        Assertions.assertThrows(RuntimeException.class, () -> getPureModelContextDataAsString(TEST_GROUP_ID, "non-existent-version", "test", CLIENT_VERSION, false, true));
    }

    @Test
    public void canGetEntitiesAsPureModelContextData()
    {
        String modelContextDataAsString = getPureModelContextDataAsString(TEST_GROUP_ID, "test", BRANCH_SNAPSHOT("master"), CLIENT_VERSION, false, true);
        Assertions.assertEquals("{\"_type\":\"data\",\"elements\":[{\"_type\":\"class\",\"constraints\":[],\"name\":\"ClassWithDependency\",\"originalMilestonedProperties\":[],\"package\":\"examples::metadata::test\",\"properties\":[{\"genericType\":{\"multiplicityArguments\":[],\"rawType\":{\"_type\":\"packageableType\",\"fullPath\":\"String\"},\"typeArguments\":[],\"typeVariableValues\":[]},\"multiplicity\":{\"lowerBound\":1,\"upperBound\":1},\"name\":\"Name\",\"stereotypes\":[],\"taggedValues\":[]}],\"qualifiedProperties\":[],\"stereotypes\":[],\"superTypes\":[],\"taggedValues\":[]},{\"_type\":\"profile\",\"name\":\"TestProfile\",\"package\":\"examples::metadata::test\",\"stereotypes\":[],\"tags\":[]},{\"_type\":\"class\",\"constraints\":[],\"name\":\"ClientBasic\",\"originalMilestonedProperties\":[],\"package\":\"examples::metadata::test\",\"properties\":[{\"genericType\":{\"multiplicityArguments\":[],\"rawType\":{\"_type\":\"packageableType\",\"fullPath\":\"String\"},\"typeArguments\":[],\"typeVariableValues\":[]},\"multiplicity\":{\"lowerBound\":1,\"upperBound\":1},\"name\":\"Name\",\"stereotypes\":[],\"taggedValues\":[]},{\"genericType\":{\"multiplicityArguments\":[],\"rawType\":{\"_type\":\"packageableType\",\"fullPath\":\"Integer\"},\"typeArguments\":[],\"typeVariableValues\":[]},\"multiplicity\":{\"lowerBound\":1,\"upperBound\":1},\"name\":\"EntityId\",\"stereotypes\":[],\"taggedValues\":[]},{\"genericType\":{\"multiplicityArguments\":[],\"rawType\":{\"_type\":\"packageableType\",\"fullPath\":\"Boolean\"},\"typeArguments\":[],\"typeVariableValues\":[]},\"multiplicity\":{\"lowerBound\":1,\"upperBound\":1},\"name\":\"IsActive\",\"stereotypes\":[],\"taggedValues\":[]},{\"genericType\":{\"multiplicityArguments\":[],\"rawType\":{\"_type\":\"packageableType\",\"fullPath\":\"Float\"},\"typeArguments\":[],\"typeVariableValues\":[]},\"multiplicity\":{\"lowerBound\":1,\"upperBound\":1},\"name\":\"RiskScore\",\"stereotypes\":[],\"taggedValues\":[]},{\"genericType\":{\"multiplicityArguments\":[],\"rawType\":{\"_type\":\"packageableType\",\"fullPath\":\"StrictDate\"},\"typeArguments\":[],\"typeVariableValues\":[]},\"multiplicity\":{\"lowerBound\":1,\"upperBound\":1},\"name\":\"IncorporationDate\",\"stereotypes\":[],\"taggedValues\":[]},{\"genericType\":{\"multiplicityArguments\":[],\"rawType\":{\"_type\":\"packageableType\",\"fullPath\":\"String\"},\"typeArguments\":[],\"typeVariableValues\":[]},\"multiplicity\":{\"lowerBound\":0,\"upperBound\":1},\"name\":\"OptionalAlternativeName\",\"stereotypes\":[],\"taggedValues\":[]},{\"genericType\":{\"multiplicityArguments\":[],\"rawType\":{\"_type\":\"packageableType\",\"fullPath\":\"String\"},\"typeArguments\":[],\"typeVariableValues\":[]},\"multiplicity\":{\"lowerBound\":0,\"upperBound\":1},\"name\":\"newProperty\",\"stereotypes\":[],\"taggedValues\":[]}],\"qualifiedProperties\":[],\"stereotypes\":[],\"superTypes\":[],\"taggedValues\":[]},{\"_type\":\"profile\",\"name\":\"TestProfileTwo\",\"package\":\"examples::metadata::test::subpackage\",\"stereotypes\":[],\"tags\":[]}],\"origin\":{\"_type\":\"pointer\",\"sdlcInfo\":{\"_type\":\"alloy\",\"baseVersion\":\"master-SNAPSHOT\",\"packageableElementPointers\":[],\"project\":\"examples.metadata:test\",\"version\":\"none\"},\"serializer\":{\"name\":\"pure\",\"version\":\"vX_X_X\"}},\"serializer\":{\"name\":\"pure\",\"version\":\"vX_X_X\"}}", modelContextDataAsString);

        String modelContextDataAsStringOld = getPureModelContextDataAsString(TEST_GROUP_ID, "test", BRANCH_SNAPSHOT("master"), CLIENT_VERSION, false, false);
        Assertions.assertEquals("{\"_type\":\"data\",\"elements\":[{\"_type\":\"class\",\"name\":\"ClassWithDependency\",\"package\":\"examples::metadata::test\",\"properties\":[{\"multiplicity\":{\"lowerBound\":1,\"upperBound\":1},\"name\":\"Name\",\"type\":\"String\"}]},{\"_type\":\"profile\",\"name\":\"TestProfile\",\"package\":\"examples::metadata::test\"},{\"_type\":\"class\",\"name\":\"ClientBasic\",\"package\":\"examples::metadata::test\",\"properties\":[{\"multiplicity\":{\"lowerBound\":1,\"upperBound\":1},\"name\":\"Name\",\"type\":\"String\"},{\"multiplicity\":{\"lowerBound\":1,\"upperBound\":1},\"name\":\"EntityId\",\"type\":\"Integer\"},{\"multiplicity\":{\"lowerBound\":1,\"upperBound\":1},\"name\":\"IsActive\",\"type\":\"Boolean\"},{\"multiplicity\":{\"lowerBound\":1,\"upperBound\":1},\"name\":\"RiskScore\",\"type\":\"Float\"},{\"multiplicity\":{\"lowerBound\":1,\"upperBound\":1},\"name\":\"IncorporationDate\",\"type\":\"StrictDate\"},{\"multiplicity\":{\"lowerBound\":0,\"upperBound\":1},\"name\":\"OptionalAlternativeName\",\"type\":\"String\"},{\"multiplicity\":{\"lowerBound\":0,\"upperBound\":1},\"name\":\"newProperty\",\"type\":\"String\"}]},{\"_type\":\"profile\",\"name\":\"TestProfileTwo\",\"package\":\"examples::metadata::test::subpackage\"}],\"origin\":{\"_type\":\"pointer\",\"sdlcInfo\":{\"_type\":\"alloy\",\"baseVersion\":\"master-SNAPSHOT\",\"packageableElementPointers\":[],\"project\":\"examples.metadata:test\",\"version\":\"none\"},\"serializer\":{\"name\":\"pure\",\"version\":\"vX_X_X\"}},\"serializer\":{\"name\":\"pure\",\"version\":\"vX_X_X\"}}", modelContextDataAsStringOld);
    }

    @Test
    public void testNonExistentProject()
    {
        Assertions.assertThrows(RuntimeException.class, () -> getPureModelContextDataAsString("non.existent.project", "test", BRANCH_SNAPSHOT("master"), CLIENT_VERSION, false, true));
    }

    @Test
    public void testErrorThrownWhenNoProjectVersionFound()
    {

        EntitiesService mockVersions = Mockito.mock(EntitiesService.class);
        PureModelContextService newService = new PureModelContextServiceImpl(mockVersions, projectsService);
        Assertions.assertThrows(IllegalArgumentException.class, () -> getPureModelContextDataAsString("test.legend", "blank-prod", BRANCH_SNAPSHOT("master"), CLIENT_VERSION, false, true), "project version not found for test.legend-blank-prod-master-SNAPSHOT");
    }

    @Test
    public void canGetEntitiesForProjectAsPureModelContextData_WithDependencies()
    {
        projectsVersionsStore.createOrUpdate(new StoreProjectVersionData("examples.metadata", "test-dependencies", "2.0.0"));
        projectsVersionsStore.createOrUpdate(new StoreProjectVersionData("examples.metadata", "test", "3.0.0"));
        List<Entity> entityList1 = entitiesStore.getAllEntities("test.legend", "blank-prod", "2.0.0");
        List<Entity> entityList2 = entitiesStore.getAllEntities("examples.metadata", "test", "3.0.0");
        List<Entity> entityList3 = entitiesStore.getAllEntities("examples.metadata", "test-dependencies", "2.0.0");
        Assertions.assertNotNull(entityList1);
        Assertions.assertNotNull(entityList2);
        Assertions.assertNotNull(entityList3);
        Assertions.assertEquals(2, entityList1.size());
        Assertions.assertEquals(5, entityList2.size());
        Assertions.assertEquals(6, entityList3.size());

        String modelContextDataAsString = getPureModelContextDataAsString("test.legend", "blank-prod", "2.0.0", CLIENT_VERSION, true, true);
        Assertions.assertNotNull(modelContextDataAsString);
        Assertions.assertEquals("{\"_type\":\"data\",\"elements\":[{\"_type\":\"service\",\"autoActivateUpdates\":true,\"documentation\":\"\",\"execution\":{\"_type\":\"pureMultiExecution\",\"executionKey\":\"env\",\"executionParameters\":[{\"key\":\"PROD\",\"mapping\":\"mapping::SomeMapping\",\"runtime\":{\"_type\":\"runtimePointer\",\"runtime\":\"runtime::H2Runtime\"}},{\"key\":\"DEV\",\"mapping\":\"mapping::SomeMapping\",\"runtime\":{\"_type\":\"runtimePointer\",\"runtime\":\"runtime::H2Runtime\"}}],\"func\":{\"_type\":\"lambda\",\"body\":[{\"_type\":\"func\",\"function\":\"project\",\"parameters\":[{\"_type\":\"func\",\"function\":\"getAll\",\"parameters\":[{\"_type\":\"packageableElementPtr\",\"fullPath\":\"domain::COVIDData\"}]},{\"_type\":\"collection\",\"multiplicity\":{\"lowerBound\":1,\"upperBound\":1},\"values\":[{\"_type\":\"lambda\",\"body\":[{\"_type\":\"property\",\"parameters\":[{\"_type\":\"var\",\"name\":\"x\"}],\"property\":\"cases\"}],\"parameters\":[{\"_type\":\"var\",\"name\":\"x\"}]}]},{\"_type\":\"collection\",\"multiplicity\":{\"lowerBound\":1,\"upperBound\":1},\"values\":[{\"_type\":\"string\",\"value\":\"Cases\"}]}]}],\"parameters\":[]}},\"name\":\"SomeService\",\"owners\":[\"anonymous\",\"akphi\"],\"package\":\"service\",\"pattern\":\"/9566f101-2108-408f-863f-6d7e154dc17a\",\"postValidations\":[],\"stereotypes\":[],\"taggedValues\":[]},{\"_type\":\"class\",\"constraints\":[],\"name\":\"Person\",\"originalMilestonedProperties\":[],\"package\":\"test::legend::blank_prod::v2_0_0::blank\",\"properties\":[],\"qualifiedProperties\":[],\"sourceInformation\":{\"endColumn\":1,\"endLine\":3,\"sourceId\":\"\",\"startColumn\":1,\"startLine\":1},\"stereotypes\":[],\"superTypes\":[],\"taggedValues\":[]},{\"_type\":\"class\",\"constraints\":[],\"name\":\"test\",\"originalMilestonedProperties\":[],\"package\":\"test::legend::metadata::test::v3_0_0::com\",\"properties\":[],\"qualifiedProperties\":[],\"stereotypes\":[],\"superTypes\":[],\"taggedValues\":[]},{\"_type\":\"class\",\"constraints\":[],\"name\":\"ClassWithDependency\",\"originalMilestonedProperties\":[],\"package\":\"test::legend::metadata::test::v3_0_0::test::legend::metadata::test\",\"properties\":[{\"genericType\":{\"multiplicityArguments\":[],\"rawType\":{\"_type\":\"packageableType\",\"fullPath\":\"String\"},\"typeArguments\":[],\"typeVariableValues\":[]},\"multiplicity\":{\"lowerBound\":1,\"upperBound\":1},\"name\":\"Name\",\"stereotypes\":[],\"taggedValues\":[]}],\"qualifiedProperties\":[],\"stereotypes\":[],\"superTypes\":[],\"taggedValues\":[]},{\"_type\":\"class\",\"constraints\":[],\"name\":\"ClientBasic\",\"originalMilestonedProperties\":[],\"package\":\"test::legend::metadata::test::v3_0_0::test::legend::metadata::test\",\"properties\":[{\"genericType\":{\"multiplicityArguments\":[],\"rawType\":{\"_type\":\"packageableType\",\"fullPath\":\"String\"},\"typeArguments\":[],\"typeVariableValues\":[]},\"multiplicity\":{\"lowerBound\":1,\"upperBound\":1},\"name\":\"Name\",\"stereotypes\":[],\"taggedValues\":[]},{\"genericType\":{\"multiplicityArguments\":[],\"rawType\":{\"_type\":\"packageableType\",\"fullPath\":\"Integer\"},\"typeArguments\":[],\"typeVariableValues\":[]},\"multiplicity\":{\"lowerBound\":1,\"upperBound\":1},\"name\":\"EntityId\",\"stereotypes\":[],\"taggedValues\":[]},{\"genericType\":{\"multiplicityArguments\":[],\"rawType\":{\"_type\":\"packageableType\",\"fullPath\":\"Boolean\"},\"typeArguments\":[],\"typeVariableValues\":[]},\"multiplicity\":{\"lowerBound\":1,\"upperBound\":1},\"name\":\"IsActive\",\"stereotypes\":[],\"taggedValues\":[]},{\"genericType\":{\"multiplicityArguments\":[],\"rawType\":{\"_type\":\"packageableType\",\"fullPath\":\"Float\"},\"typeArguments\":[],\"typeVariableValues\":[]},\"multiplicity\":{\"lowerBound\":1,\"upperBound\":1},\"name\":\"RiskScore\",\"stereotypes\":[],\"taggedValues\":[]},{\"genericType\":{\"multiplicityArguments\":[],\"rawType\":{\"_type\":\"packageableType\",\"fullPath\":\"StrictDate\"},\"typeArguments\":[],\"typeVariableValues\":[]},\"multiplicity\":{\"lowerBound\":1,\"upperBound\":1},\"name\":\"IncorporationDate\",\"stereotypes\":[],\"taggedValues\":[]},{\"genericType\":{\"multiplicityArguments\":[],\"rawType\":{\"_type\":\"packageableType\",\"fullPath\":\"String\"},\"typeArguments\":[],\"typeVariableValues\":[]},\"multiplicity\":{\"lowerBound\":0,\"upperBound\":1},\"name\":\"OptionalAlternativeName\",\"stereotypes\":[],\"taggedValues\":[]},{\"genericType\":{\"multiplicityArguments\":[],\"rawType\":{\"_type\":\"packageableType\",\"fullPath\":\"String\"},\"typeArguments\":[],\"typeVariableValues\":[]},\"multiplicity\":{\"lowerBound\":0,\"upperBound\":1},\"name\":\"newProperty\",\"stereotypes\":[],\"taggedValues\":[]}],\"qualifiedProperties\":[],\"stereotypes\":[],\"superTypes\":[],\"taggedValues\":[]},{\"_type\":\"profile\",\"name\":\"TestProfile\",\"package\":\"test::legend::metadata::test::v3_0_0::test::legend::metadata::test\",\"stereotypes\":[],\"tags\":[]},{\"_type\":\"profile\",\"name\":\"TestProfileTwo\",\"package\":\"test::legend::metadata::test::v3_0_0::test::legend::metadata::test::subpackage\",\"stereotypes\":[],\"tags\":[]}],\"origin\":{\"_type\":\"pointer\",\"sdlcInfo\":{\"_type\":\"alloy\",\"baseVersion\":\"2.0.0\",\"packageableElementPointers\":[],\"project\":\"test.legend:blank-prod\",\"version\":\"none\"},\"serializer\":{\"name\":\"pure\",\"version\":\"vX_X_X\"}},\"serializer\":{\"name\":\"pure\",\"version\":\"vX_X_X\"}}", modelContextDataAsString);

        PureModelContextData pureModelContextData = service.getPureModelContextData("test.legend", "blank-prod", "2.0.0", CLIENT_VERSION, true, true);
        Assertions.assertNotNull(pureModelContextData);
        Assertions.assertEquals("2.0.0", pureModelContextData.origin.sdlcInfo.baseVersion);
        Assertions.assertEquals("test.legend:blank-prod", ((AlloySDLC) pureModelContextData.origin.sdlcInfo).project);
    }

    @Test
    public void canGetEntitiesForProjectAsPureModelContextDataWithLatestAsVersion()
    {
        List<Entity> entityList1 = entitiesStore.getAllEntities("test.legend", "blank-prod", "2.0.0");
        Assertions.assertNotNull(entityList1);
        Assertions.assertEquals(2, entityList1.size());

        String modelContextDataAsString = getPureModelContextDataAsString("test.legend", "blank-prod", "latest", CLIENT_VERSION, false, true);
        Assertions.assertNotNull(modelContextDataAsString);
        Assertions.assertEquals("{\"_type\":\"data\",\"elements\":[{\"_type\":\"service\",\"autoActivateUpdates\":true,\"documentation\":\"\",\"execution\":{\"_type\":\"pureMultiExecution\",\"executionKey\":\"env\",\"executionParameters\":[{\"key\":\"PROD\",\"mapping\":\"mapping::SomeMapping\",\"runtime\":{\"_type\":\"runtimePointer\",\"runtime\":\"runtime::H2Runtime\"}},{\"key\":\"DEV\",\"mapping\":\"mapping::SomeMapping\",\"runtime\":{\"_type\":\"runtimePointer\",\"runtime\":\"runtime::H2Runtime\"}}],\"func\":{\"_type\":\"lambda\",\"body\":[{\"_type\":\"func\",\"function\":\"project\",\"parameters\":[{\"_type\":\"func\",\"function\":\"getAll\",\"parameters\":[{\"_type\":\"packageableElementPtr\",\"fullPath\":\"domain::COVIDData\"}]},{\"_type\":\"collection\",\"multiplicity\":{\"lowerBound\":1,\"upperBound\":1},\"values\":[{\"_type\":\"lambda\",\"body\":[{\"_type\":\"property\",\"parameters\":[{\"_type\":\"var\",\"name\":\"x\"}],\"property\":\"cases\"}],\"parameters\":[{\"_type\":\"var\",\"name\":\"x\"}]}]},{\"_type\":\"collection\",\"multiplicity\":{\"lowerBound\":1,\"upperBound\":1},\"values\":[{\"_type\":\"string\",\"value\":\"Cases\"}]}]}],\"parameters\":[]}},\"name\":\"SomeService\",\"owners\":[\"anonymous\",\"akphi\"],\"package\":\"service\",\"pattern\":\"/9566f101-2108-408f-863f-6d7e154dc17a\",\"postValidations\":[],\"stereotypes\":[],\"taggedValues\":[]},{\"_type\":\"class\",\"constraints\":[],\"name\":\"Person\",\"originalMilestonedProperties\":[],\"package\":\"test::legend::blank_prod::v2_0_0::blank\",\"properties\":[],\"qualifiedProperties\":[],\"sourceInformation\":{\"endColumn\":1,\"endLine\":3,\"sourceId\":\"\",\"startColumn\":1,\"startLine\":1},\"stereotypes\":[],\"superTypes\":[],\"taggedValues\":[]}],\"origin\":{\"_type\":\"pointer\",\"sdlcInfo\":{\"_type\":\"alloy\",\"baseVersion\":\"2.0.0\",\"packageableElementPointers\":[],\"project\":\"test.legend:blank-prod\",\"version\":\"none\"},\"serializer\":{\"name\":\"pure\",\"version\":\"vX_X_X\"}},\"serializer\":{\"name\":\"pure\",\"version\":\"vX_X_X\"}}", modelContextDataAsString);
    }

    @Test
    public void canGetEntitiesForProjectAsPureModelContextDataWithMetricsStored()
    {
        getPureModelContextDataAsString("examples.metadata", "test", "2.3.1", CLIENT_VERSION, true, true);
        metricsHandler.persist(metricsRegistry);
        Assertions.assertEquals(metrics.getAll().size(), 4);
        Assertions.assertNotNull(metrics.get("examples.metadata", "test-dependencies", "1.0.0").get(0).getLastQueryTime());
        Assertions.assertNotNull(metrics.get("examples.metadata", "test", "2.3.1").get(0).getLastQueryTime());
    }

    @Test
    public void canGetEntitiesAsPureModelContextDataWithHeadVersionAlias()
    {
        String modelContextDataAsString = getPureModelContextDataAsString(TEST_GROUP_ID, "test", "head", CLIENT_VERSION, false, true);
        Assertions.assertNotNull(modelContextDataAsString);
        Assertions.assertEquals("{\"_type\":\"data\",\"elements\":[{\"_type\":\"class\",\"constraints\":[],\"name\":\"ClassWithDependency\",\"originalMilestonedProperties\":[],\"package\":\"examples::metadata::test\",\"properties\":[{\"genericType\":{\"multiplicityArguments\":[],\"rawType\":{\"_type\":\"packageableType\",\"fullPath\":\"String\"},\"typeArguments\":[],\"typeVariableValues\":[]},\"multiplicity\":{\"lowerBound\":1,\"upperBound\":1},\"name\":\"Name\",\"stereotypes\":[],\"taggedValues\":[]}],\"qualifiedProperties\":[],\"stereotypes\":[],\"superTypes\":[],\"taggedValues\":[]},{\"_type\":\"profile\",\"name\":\"TestProfile\",\"package\":\"examples::metadata::test\",\"stereotypes\":[],\"tags\":[]},{\"_type\":\"class\",\"constraints\":[],\"name\":\"ClientBasic\",\"originalMilestonedProperties\":[],\"package\":\"examples::metadata::test\",\"properties\":[{\"genericType\":{\"multiplicityArguments\":[],\"rawType\":{\"_type\":\"packageableType\",\"fullPath\":\"String\"},\"typeArguments\":[],\"typeVariableValues\":[]},\"multiplicity\":{\"lowerBound\":1,\"upperBound\":1},\"name\":\"Name\",\"stereotypes\":[],\"taggedValues\":[]},{\"genericType\":{\"multiplicityArguments\":[],\"rawType\":{\"_type\":\"packageableType\",\"fullPath\":\"Integer\"},\"typeArguments\":[],\"typeVariableValues\":[]},\"multiplicity\":{\"lowerBound\":1,\"upperBound\":1},\"name\":\"EntityId\",\"stereotypes\":[],\"taggedValues\":[]},{\"genericType\":{\"multiplicityArguments\":[],\"rawType\":{\"_type\":\"packageableType\",\"fullPath\":\"Boolean\"},\"typeArguments\":[],\"typeVariableValues\":[]},\"multiplicity\":{\"lowerBound\":1,\"upperBound\":1},\"name\":\"IsActive\",\"stereotypes\":[],\"taggedValues\":[]},{\"genericType\":{\"multiplicityArguments\":[],\"rawType\":{\"_type\":\"packageableType\",\"fullPath\":\"Float\"},\"typeArguments\":[],\"typeVariableValues\":[]},\"multiplicity\":{\"lowerBound\":1,\"upperBound\":1},\"name\":\"RiskScore\",\"stereotypes\":[],\"taggedValues\":[]},{\"genericType\":{\"multiplicityArguments\":[],\"rawType\":{\"_type\":\"packageableType\",\"fullPath\":\"StrictDate\"},\"typeArguments\":[],\"typeVariableValues\":[]},\"multiplicity\":{\"lowerBound\":1,\"upperBound\":1},\"name\":\"IncorporationDate\",\"stereotypes\":[],\"taggedValues\":[]},{\"genericType\":{\"multiplicityArguments\":[],\"rawType\":{\"_type\":\"packageableType\",\"fullPath\":\"String\"},\"typeArguments\":[],\"typeVariableValues\":[]},\"multiplicity\":{\"lowerBound\":0,\"upperBound\":1},\"name\":\"OptionalAlternativeName\",\"stereotypes\":[],\"taggedValues\":[]},{\"genericType\":{\"multiplicityArguments\":[],\"rawType\":{\"_type\":\"packageableType\",\"fullPath\":\"String\"},\"typeArguments\":[],\"typeVariableValues\":[]},\"multiplicity\":{\"lowerBound\":0,\"upperBound\":1},\"name\":\"newProperty\",\"stereotypes\":[],\"taggedValues\":[]}],\"qualifiedProperties\":[],\"stereotypes\":[],\"superTypes\":[],\"taggedValues\":[]},{\"_type\":\"profile\",\"name\":\"TestProfileTwo\",\"package\":\"examples::metadata::test::subpackage\",\"stereotypes\":[],\"tags\":[]}],\"origin\":{\"_type\":\"pointer\",\"sdlcInfo\":{\"_type\":\"alloy\",\"baseVersion\":\"master-SNAPSHOT\",\"packageableElementPointers\":[],\"project\":\"examples.metadata:test\",\"version\":\"none\"},\"serializer\":{\"name\":\"pure\",\"version\":\"vX_X_X\"}},\"serializer\":{\"name\":\"pure\",\"version\":\"vX_X_X\"}}", modelContextDataAsString);
    }

    @Test
    public void testErrorThrownWhenIncorrectClientVersionProvided()
    {
        Assertions.assertThrows(IllegalArgumentException.class, () -> getPureModelContextDataAsString("examples.metadata", "test", "lastest", "dummy_version", false, true));
    }
}
