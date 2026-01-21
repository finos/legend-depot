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

import org.finos.legend.depot.domain.entity.ProjectVersionEntities;
import org.finos.legend.depot.domain.project.ProjectVersion;
import org.finos.legend.depot.services.api.entities.EntitiesService;
import org.finos.legend.depot.services.api.projects.ProjectsService;
import org.finos.legend.engine.protocol.pure.v1.model.context.AlloySDLC;
import org.finos.legend.engine.protocol.pure.v1.model.context.PureModelContextData;
import org.finos.legend.sdlc.domain.model.entity.Entity;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class PureModelContextServiceImplClaudeTest


{
    @Mock
    private EntitiesService mockEntitiesService;

    @Mock
    private ProjectsService mockProjectsService;

    private PureModelContextServiceImpl service;

    @BeforeEach
    public void setUp()
  {
        MockitoAnnotations.openMocks(this);
        service = new PureModelContextServiceImpl(mockEntitiesService, mockProjectsService);
    }

    @Test
    public void testConstructor()
  {
        EntitiesService entitiesService = mock(EntitiesService.class);
        ProjectsService projectsService = mock(ProjectsService.class);
        PureModelContextServiceImpl newService = new PureModelContextServiceImpl(entitiesService, projectsService);
        Assertions.assertNotNull(newService);
    }

    @Test
    public void testResolveAndValidateClientVersion_WithNullClientVersion()
  {
        String result = service.resolveAndValidateClientVersion(null);
        Assertions.assertNotNull(result);
    }

    @Test
    public void testResolveAndValidateClientVersion_WithValidClientVersion()
  {
        String result = service.resolveAndValidateClientVersion("v1_0_0");
        Assertions.assertEquals("v1_0_0", result);
    }

    @Test
    public void testResolveAndValidateClientVersion_WithInvalidClientVersion()
  {
        IllegalArgumentException exception = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> service.resolveAndValidateClientVersion("invalid_version")
        );
        Assertions.assertTrue(exception.getMessage().contains("Client version provided is invalid"));
    }

    @Test
    public void testBuildAlloySDLC_WithValidParameters()
  {
        AlloySDLC sdlc = service.buildAlloySDLC("org.example", "test-artifact", "1.0.0");

        Assertions.assertNotNull(sdlc);
        Assertions.assertEquals("org.example:test-artifact", sdlc.project);
        Assertions.assertEquals("1.0.0", sdlc.baseVersion);
    }

    @Test
    public void testBuildAlloySDLC_WithDifferentParameters()
  {
        AlloySDLC sdlc = service.buildAlloySDLC("com.test", "another-artifact", "2.5.0");

        Assertions.assertNotNull(sdlc);
        Assertions.assertEquals("com.test:another-artifact", sdlc.project);
        Assertions.assertEquals("2.5.0", sdlc.baseVersion);
    }

    @Test
    public void testCombinePureModelContextData_WithValidPMCDs()
  {
        PureModelContextData rootPMCD = PureModelContextData.newBuilder().build();
        PureModelContextData childPMCD = PureModelContextData.newBuilder().build();

        PureModelContextData result = service.combinePureModelContextData(rootPMCD, childPMCD);

        Assertions.assertNotNull(result);
    }

    @Test
    public void testCombinePureModelContextData_WithNullChildPMCD()
  {
        PureModelContextData rootPMCD = PureModelContextData.newBuilder().build();

        Assertions.assertThrows(
                NullPointerException.class,
                () -> service.combinePureModelContextData(rootPMCD, null)
        );
    }

    @Test
    public void testBuildPureModelContextData_WithEmptyStreamAndAlloySDLC()
  {
        Stream<Entity> entities = Stream.empty();
        AlloySDLC sdlc = new AlloySDLC();
        sdlc.project = "test:project";
        sdlc.baseVersion = "1.0.0";

        PureModelContextData result = service.buildPureModelContextData(entities, sdlc, "v1_0_0", true);

        Assertions.assertNotNull(result);
    }

    @Test
    public void testBuildPureModelContextData_WithEmptyStreamAndGroupArtifactVersion()
  {
        Stream<Entity> entities = Stream.empty();

        PureModelContextData result = service.buildPureModelContextData(
                entities,
                "org.example",
                "test-artifact",
                "1.0.0",
                "v1_0_0",
                true
        );

        Assertions.assertNotNull(result);
    }

    @Test
    public void testBuildPureModelContextData_WithConvertToNewProtocolFalse()
  {
        Stream<Entity> entities = Stream.empty();
        AlloySDLC sdlc = new AlloySDLC();
        sdlc.project = "test:project";
        sdlc.baseVersion = "1.0.0";

        PureModelContextData result = service.buildPureModelContextData(entities, sdlc, "v1_0_0", false);

        Assertions.assertNotNull(result);
    }

    @Test
    public void testGetPureModelContextData_WithGAV_NoTransitive()
  {
        String groupId = "org.example";
        String artifactId = "test-artifact";
        String versionId = "1.0.0";
        String resolvedVersion = "1.0.0";

        when(mockProjectsService.resolveAliasesAndCheckVersionExists(groupId, artifactId, versionId))
                .thenReturn(resolvedVersion);
        when(mockEntitiesService.getEntities(groupId, artifactId, resolvedVersion))
                .thenReturn(Collections.emptyList());

        PureModelContextData result = service.getPureModelContextData(
                groupId,
                artifactId,
                versionId,
                null,
                false,
                true
        );

        Assertions.assertNotNull(result);
        verify(mockProjectsService, times(1)).resolveAliasesAndCheckVersionExists(groupId, artifactId, versionId);
        verify(mockEntitiesService, times(1)).getEntities(groupId, artifactId, resolvedVersion);
    }

    @Test
    public void testGetPureModelContextData_WithGAV_WithTransitive()
  {
        String groupId = "org.example";
        String artifactId = "test-artifact";
        String versionId = "1.0.0";
        String resolvedVersion = "1.0.0";

        when(mockProjectsService.resolveAliasesAndCheckVersionExists(groupId, artifactId, versionId))
                .thenReturn(resolvedVersion);
        when(mockEntitiesService.getEntities(groupId, artifactId, resolvedVersion))
                .thenReturn(Collections.emptyList());
        when(mockEntitiesService.getDependenciesEntities(groupId, artifactId, resolvedVersion, true, false))
                .thenReturn(Collections.emptyList());

        PureModelContextData result = service.getPureModelContextData(
                groupId,
                artifactId,
                versionId,
                null,
                true,
                true
        );

        Assertions.assertNotNull(result);
        verify(mockProjectsService, times(1)).resolveAliasesAndCheckVersionExists(groupId, artifactId, versionId);
        verify(mockEntitiesService, times(1)).getEntities(groupId, artifactId, resolvedVersion);
        verify(mockEntitiesService, times(1)).getDependenciesEntities(groupId, artifactId, resolvedVersion, true, false);
    }

    @Test
    public void testGetPureModelContextData_WithGAV_WithClientVersion()
  {
        String groupId = "org.example";
        String artifactId = "test-artifact";
        String versionId = "1.0.0";
        String resolvedVersion = "1.0.0";
        String clientVersion = "v1_0_0";

        when(mockProjectsService.resolveAliasesAndCheckVersionExists(groupId, artifactId, versionId))
                .thenReturn(resolvedVersion);
        when(mockEntitiesService.getEntities(groupId, artifactId, resolvedVersion))
                .thenReturn(Collections.emptyList());

        PureModelContextData result = service.getPureModelContextData(
                groupId,
                artifactId,
                versionId,
                clientVersion,
                false,
                true
        );

        Assertions.assertNotNull(result);
    }

    @Test
    public void testGetPureModelContextData_WithGAV_ConvertToNewProtocolFalse()
  {
        String groupId = "org.example";
        String artifactId = "test-artifact";
        String versionId = "1.0.0";
        String resolvedVersion = "1.0.0";

        when(mockProjectsService.resolveAliasesAndCheckVersionExists(groupId, artifactId, versionId))
                .thenReturn(resolvedVersion);
        when(mockEntitiesService.getEntities(groupId, artifactId, resolvedVersion))
                .thenReturn(Collections.emptyList());

        PureModelContextData result = service.getPureModelContextData(
                groupId,
                artifactId,
                versionId,
                null,
                false,
                false
        );

        Assertions.assertNotNull(result);
    }

    @Test
    public void testGetPureModelContextData_WithProjectDependencies_NoTransitive()
  {
        List<ProjectVersion> dependencies = Arrays.asList(
                new ProjectVersion("org.example", "project1", "1.0.0")
        );

        when(mockEntitiesService.getDependenciesEntities(dependencies, false, true))
                .thenReturn(Collections.emptyList());

        PureModelContextData result = service.getPureModelContextData(
                dependencies,
                null,
                false,
                true
        );

        Assertions.assertNotNull(result);
        verify(mockEntitiesService, times(1)).getDependenciesEntities(dependencies, false, true);
    }

    @Test
    public void testGetPureModelContextData_WithProjectDependencies_WithTransitive()
  {
        List<ProjectVersion> dependencies = Arrays.asList(
                new ProjectVersion("org.example", "project1", "1.0.0"),
                new ProjectVersion("org.example", "project2", "2.0.0")
        );

        when(mockEntitiesService.getDependenciesEntities(dependencies, true, true))
                .thenReturn(Collections.emptyList());

        PureModelContextData result = service.getPureModelContextData(
                dependencies,
                null,
                true,
                true
        );

        Assertions.assertNotNull(result);
        verify(mockEntitiesService, times(1)).getDependenciesEntities(dependencies, true, true);
    }

    @Test
    public void testGetPureModelContextData_WithProjectDependencies_WithClientVersion()
  {
        List<ProjectVersion> dependencies = Arrays.asList(
                new ProjectVersion("org.example", "project1", "1.0.0")
        );

        when(mockEntitiesService.getDependenciesEntities(dependencies, true, true))
                .thenReturn(Collections.emptyList());

        PureModelContextData result = service.getPureModelContextData(
                dependencies,
                "v1_0_0",
                true,
                true
        );

        Assertions.assertNotNull(result);
    }

    @Test
    public void testGetPureModelContextData_WithProjectDependencies_ConvertToNewProtocolFalse()
  {
        List<ProjectVersion> dependencies = Arrays.asList(
                new ProjectVersion("org.example", "project1", "1.0.0")
        );

        when(mockEntitiesService.getDependenciesEntities(dependencies, true, true))
                .thenReturn(Collections.emptyList());

        PureModelContextData result = service.getPureModelContextData(
                dependencies,
                null,
                true,
                false
        );

        Assertions.assertNotNull(result);
    }

    @Test
    public void testGetPureModelContextData_WithProjectDependencies_EmptyList()
  {
        List<ProjectVersion> dependencies = Collections.emptyList();

        when(mockEntitiesService.getDependenciesEntities(dependencies, true, true))
                .thenReturn(Collections.emptyList());

        PureModelContextData result = service.getPureModelContextData(
                dependencies,
                null,
                true,
                true
        );

        Assertions.assertNotNull(result);
    }

    @Test
    public void testGetPureModelContextData_WithGAV_WithTransitiveDependencies()
  {
        String groupId = "org.example";
        String artifactId = "test-artifact";
        String versionId = "1.0.0";
        String resolvedVersion = "1.0.0";

        Entity mainEntity = createMockEntity("test::Main");
        Entity depEntity = createMockEntity("test::Dependency");

        ProjectVersionEntities projectVersionEntities = new ProjectVersionEntities(
                "org.example",
                "dep-artifact",
                "1.0.0",
                Arrays.asList(depEntity)
        );

        when(mockProjectsService.resolveAliasesAndCheckVersionExists(groupId, artifactId, versionId))
                .thenReturn(resolvedVersion);
        when(mockEntitiesService.getEntities(groupId, artifactId, resolvedVersion))
                .thenReturn(Arrays.asList(mainEntity));
        when(mockEntitiesService.getDependenciesEntities(groupId, artifactId, resolvedVersion, true, false))
                .thenReturn(Arrays.asList(projectVersionEntities));

        PureModelContextData result = service.getPureModelContextData(
                groupId,
                artifactId,
                versionId,
                null,
                true,
                true
        );

        Assertions.assertNotNull(result);
        verify(mockEntitiesService, times(1)).getDependenciesEntities(groupId, artifactId, resolvedVersion, true, false);
    }

    @Test
    public void testGetPureModelContextData_WithProjectDependencies_WithEntities()
  {
        List<ProjectVersion> dependencies = Arrays.asList(
                new ProjectVersion("org.example", "project1", "1.0.0")
        );

        Entity entity = createMockEntity("test::Entity");
        ProjectVersionEntities projectVersionEntities = new ProjectVersionEntities(
                "org.example",
                "project1",
                "1.0.0",
                Arrays.asList(entity)
        );

        when(mockEntitiesService.getDependenciesEntities(dependencies, true, true))
                .thenReturn(Arrays.asList(projectVersionEntities));

        PureModelContextData result = service.getPureModelContextData(
                dependencies,
                null,
                true,
                true
        );

        Assertions.assertNotNull(result);
    }

    @SuppressWarnings("unchecked")
    private Entity createMockEntity(String path)
  {
        Entity entity = mock(Entity.class);
        when(entity.getPath()).thenReturn(path);
        when(entity.getContent()).thenReturn(Collections.emptyMap());
        return entity;
    }
}
