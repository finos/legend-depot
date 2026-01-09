//  Copyright 2026 Goldman Sachs
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

package org.finos.legend.depot.services.api.dependencies;

import org.finos.legend.depot.domain.project.ProjectVersion;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

import java.util.Arrays;
import java.util.Collections;

public class TestDependencyResponseModel
{
    @Test
    public void testSuccessResponse()
    {
        ProjectVersion pv1 = new ProjectVersion("org.finos.legend", "project_a", "1.0.0");
        ProjectVersion pv2 = new ProjectVersion("org.finos.legend", "project_b", "1.0.0");

        DependencyResponseModel response = DependencyResponseModel.success(Arrays.asList(pv1, pv2));

        Assertions.assertTrue(response.isSuccess());
        Assertions.assertEquals(2, response.getResolvedVersions().size());
        Assertions.assertEquals(0, response.getConflicts().size());
        Assertions.assertNull(response.getFailureReason());
    }

    @Test
    public void testFailureResponse()
    {
        DependencyConflict conflict = new DependencyConflict(
            "org.apache.commons",
            "commons-util"
        );

        ProjectVersion requiredBy = new ProjectVersion("org.finos.legend", "project_a", "1.0.0");
        conflict.addConflictingVersion("1.0.0", Collections.singletonList(requiredBy));

        DependencyResponseModel response = DependencyResponseModel.failure(
            "Cannot resolve",
            Collections.singletonList(conflict)
        );

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals(0, response.getResolvedVersions().size());
        Assertions.assertEquals(1, response.getConflicts().size());
        Assertions.assertEquals("Cannot resolve", response.getFailureReason());

        DependencyConflict retrievedConflict = response.getConflicts().get(0);
        Assertions.assertEquals("org.apache.commons", retrievedConflict.getGroupId());
        Assertions.assertEquals("commons-util", retrievedConflict.getArtifactId());
        Assertions.assertEquals(1, retrievedConflict.getConflictingVersions().size());
    }

    @Test
    public void testAddConflict()
    {
        DependencyResponseModel response = new DependencyResponseModel();
        DependencyConflict conflict = new DependencyConflict(
            "org.test",
            "artifact"
        );

        response.addConflict(conflict);

        Assertions.assertEquals(1, response.getConflicts().size());
        Assertions.assertEquals(conflict, response.getConflicts().get(0));
    }
}

