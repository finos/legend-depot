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

package org.finos.legend.depot.server.resources.pure.model.context;

import org.finos.legend.depot.domain.project.ProjectVersion;
import org.finos.legend.depot.services.api.pure.model.context.PureModelContextService;
import org.finos.legend.engine.protocol.pure.v1.model.context.PureModelContextData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class PureModelContextResourceClaudeTest


{
    @Mock
    private PureModelContextService mockService;

    @Mock
    private PureModelContextData testPMCD;

    private PureModelContextResource resource;

    @BeforeEach
    public void setUp()
  {
        MockitoAnnotations.openMocks(this);
        resource = new PureModelContextResource(mockService);
    }

    @Test
    public void testConstructor()
  {
        PureModelContextService service = mock(PureModelContextService.class);
        PureModelContextResource newResource = new PureModelContextResource(service);
        Assertions.assertNotNull(newResource);
    }

    @Test
    public void testGetPureModelContextDataWithGAV_NoClientVersion_WithDependencies_ConvertToNewProtocol()
  {
        when(mockService.getPureModelContextData("test.group", "test-artifact", "1.0.0", null, true, true))
                .thenReturn(testPMCD);

        Response response = resource.getPureModelContextData(
                "test.group",
                "test-artifact",
                "1.0.0",
                null,
                true,
                true,
                null
        );

        Assertions.assertNotNull(response);
        Assertions.assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        Assertions.assertSame(testPMCD, response.getEntity());
        verify(mockService, times(1)).getPureModelContextData("test.group", "test-artifact", "1.0.0", null, true, true);
    }

    @Test
    public void testGetPureModelContextDataWithGAV_WithClientVersion()
  {
        when(mockService.getPureModelContextData("org.example", "my-project", "2.0.0", "v1_0_0", true, true))
                .thenReturn(testPMCD);

        Response response = resource.getPureModelContextData(
                "org.example",
                "my-project",
                "2.0.0",
                "v1_0_0",
                true,
                true,
                null
        );

        Assertions.assertNotNull(response);
        Assertions.assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        Assertions.assertSame(testPMCD, response.getEntity());
        verify(mockService, times(1)).getPureModelContextData("org.example", "my-project", "2.0.0", "v1_0_0", true, true);
    }

    @Test
    public void testGetPureModelContextDataWithGAV_NoDependencies()
  {
        when(mockService.getPureModelContextData("test.group", "artifact", "3.0.0", "v2_0_0", false, true))
                .thenReturn(testPMCD);

        Response response = resource.getPureModelContextData(
                "test.group",
                "artifact",
                "3.0.0",
                "v2_0_0",
                false,
                true,
                null
        );

        Assertions.assertNotNull(response);
        Assertions.assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        verify(mockService, times(1)).getPureModelContextData("test.group", "artifact", "3.0.0", "v2_0_0", false, true);
    }

    @Test
    public void testGetPureModelContextDataWithGAV_NoConversion()
  {
        when(mockService.getPureModelContextData("test.group", "artifact", "1.0.0", null, true, false))
                .thenReturn(testPMCD);

        Response response = resource.getPureModelContextData(
                "test.group",
                "artifact",
                "1.0.0",
                null,
                true,
                false,
                null
        );

        Assertions.assertNotNull(response);
        Assertions.assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        verify(mockService, times(1)).getPureModelContextData("test.group", "artifact", "1.0.0", null, true, false);
    }

    @Test
    public void testGetPureModelContextDataWithGAV_AllFalseFlags()
  {
        when(mockService.getPureModelContextData("test.group", "artifact", "1.0.0", null, false, false))
                .thenReturn(testPMCD);

        Response response = resource.getPureModelContextData(
                "test.group",
                "artifact",
                "1.0.0",
                null,
                false,
                false,
                null
        );

        Assertions.assertNotNull(response);
        Assertions.assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        verify(mockService, times(1)).getPureModelContextData("test.group", "artifact", "1.0.0", null, false, false);
    }

    @Test
    public void testGetPureModelContextDataWithGAV_WithRequestAndMatchingETag()
  {
        Request mockRequest = mock(Request.class);
        Response.ResponseBuilder mockBuilder = mock(Response.ResponseBuilder.class);
        Response mockResponse = mock(Response.class);

        when(mockBuilder.build()).thenReturn(mockResponse);
        when(mockRequest.evaluatePreconditions(any(EntityTag.class))).thenReturn(mockBuilder);
        when(mockService.getPureModelContextData(anyString(), anyString(), anyString(), isNull(), anyBoolean(), anyBoolean()))
                .thenReturn(testPMCD);

        Response response = resource.getPureModelContextData(
                "test.group",
                "test-artifact",
                "1.0.0",
                null,
                true,
                true,
                mockRequest
        );

        Assertions.assertNotNull(response);
    }

    @Test
    public void testGetPureModelContextDataWithDependencies_SingleDependency()
  {
        ProjectVersion dep = new ProjectVersion("org.example", "project1", "1.0.0");
        List<ProjectVersion> dependencies = Collections.singletonList(dep);

        when(mockService.getPureModelContextData(dependencies, null, true, true))
                .thenReturn(testPMCD);

        Response response = resource.getPureModelContextData(
                dependencies,
                null,
                true,
                true,
                null
        );

        Assertions.assertNotNull(response);
        Assertions.assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        Assertions.assertSame(testPMCD, response.getEntity());
        verify(mockService, times(1)).getPureModelContextData(dependencies, null, true, true);
    }

    @Test
    public void testGetPureModelContextDataWithDependencies_MultipleDependencies()
  {
        ProjectVersion dep1 = new ProjectVersion("org.example", "project1", "1.0.0");
        ProjectVersion dep2 = new ProjectVersion("org.example", "project2", "2.0.0");
        ProjectVersion dep3 = new ProjectVersion("com.test", "project3", "3.0.0");
        List<ProjectVersion> dependencies = Arrays.asList(dep1, dep2, dep3);

        when(mockService.getPureModelContextData(dependencies, "v1_0_0", true, true))
                .thenReturn(testPMCD);

        Response response = resource.getPureModelContextData(
                dependencies,
                "v1_0_0",
                true,
                true,
                null
        );

        Assertions.assertNotNull(response);
        Assertions.assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        Assertions.assertSame(testPMCD, response.getEntity());
        verify(mockService, times(1)).getPureModelContextData(dependencies, "v1_0_0", true, true);
    }

    @Test
    public void testGetPureModelContextDataWithDependencies_NoTransitive()
  {
        ProjectVersion dep = new ProjectVersion("org.example", "project1", "1.0.0");
        List<ProjectVersion> dependencies = Collections.singletonList(dep);

        when(mockService.getPureModelContextData(dependencies, null, false, true))
                .thenReturn(testPMCD);

        Response response = resource.getPureModelContextData(
                dependencies,
                null,
                false,
                true,
                null
        );

        Assertions.assertNotNull(response);
        Assertions.assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        verify(mockService, times(1)).getPureModelContextData(dependencies, null, false, true);
    }

    @Test
    public void testGetPureModelContextDataWithDependencies_NoConversion()
  {
        ProjectVersion dep = new ProjectVersion("org.example", "project1", "1.0.0");
        List<ProjectVersion> dependencies = Collections.singletonList(dep);

        when(mockService.getPureModelContextData(dependencies, "v1_0_0", true, false))
                .thenReturn(testPMCD);

        Response response = resource.getPureModelContextData(
                dependencies,
                "v1_0_0",
                true,
                false,
                null
        );

        Assertions.assertNotNull(response);
        Assertions.assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        verify(mockService, times(1)).getPureModelContextData(dependencies, "v1_0_0", true, false);
    }

    @Test
    public void testGetPureModelContextDataWithDependencies_AllFalseFlags()
  {
        ProjectVersion dep = new ProjectVersion("org.example", "project1", "1.0.0");
        List<ProjectVersion> dependencies = Collections.singletonList(dep);

        when(mockService.getPureModelContextData(dependencies, null, false, false))
                .thenReturn(testPMCD);

        Response response = resource.getPureModelContextData(
                dependencies,
                null,
                false,
                false,
                null
        );

        Assertions.assertNotNull(response);
        Assertions.assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        verify(mockService, times(1)).getPureModelContextData(dependencies, null, false, false);
    }

    @Test
    public void testGetPureModelContextDataWithDependencies_EmptyList()
  {
        List<ProjectVersion> dependencies = Collections.emptyList();

        when(mockService.getPureModelContextData(dependencies, null, true, true))
                .thenReturn(testPMCD);

        Response response = resource.getPureModelContextData(
                dependencies,
                null,
                true,
                true,
                null
        );

        Assertions.assertNotNull(response);
        Assertions.assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        verify(mockService, times(1)).getPureModelContextData(dependencies, null, true, true);
    }
}
