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

package org.finos.legend.depot.domain.project;

import org.finos.legend.depot.domain.CoordinateValidator;
import org.finos.legend.depot.store.model.projects.StoreProjectData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TestProjectUtilities
{

    @Test
    public void validGroupId()
    {
        Assertions.assertTrue(CoordinateValidator.isValidGroupId("example.good.group"));
        Assertions.assertTrue(CoordinateValidator.isValidGroupId("org.finos"));
        Assertions.assertFalse(CoordinateValidator.isValidGroupId(""));
        Assertions.assertFalse(CoordinateValidator.isValidGroupId("this is not a good one"));
        Assertions.assertFalse(CoordinateValidator.isValidGroupId("this.is.starting.well but not ending well"));
        Assertions.assertTrue(CoordinateValidator.isValidGroupId("singleWordNoDots"));
    }

    @Test
    public void validArtifactId()
    {
        Assertions.assertTrue(CoordinateValidator.isValidArtifactId("test"));
        Assertions.assertTrue(CoordinateValidator.isValidArtifactId("test-other"));
        Assertions.assertFalse(CoordinateValidator.isValidArtifactId(""));
        Assertions.assertFalse(CoordinateValidator.isValidArtifactId("singleWordNoDots"));
    }

    @Test
    public void testEvaluateLatestVersionAndUpdate()
    {
        StoreProjectData projectData = new StoreProjectData("PROD-1", "examples.test", "metadata", null, "2.1.0");

        Assertions.assertTrue(projectData.evaluateLatestVersionAndUpdate("3.0.0"));
        Assertions.assertEquals(projectData.getLatestVersion(), "3.0.0");

        projectData.setLatestVersion("2.1.0");
        Assertions.assertFalse(projectData.evaluateLatestVersionAndUpdate("2.1.0"));
        Assertions.assertFalse(projectData.evaluateLatestVersionAndUpdate("2.0.1"));

        projectData = new StoreProjectData("PROD-1", "examples.test", "metadata", null, null);
        Assertions.assertTrue(projectData.evaluateLatestVersionAndUpdate("3.0.0"));
        Assertions.assertEquals(projectData.getLatestVersion(), "3.0.0");

        Assertions.assertFalse(projectData.evaluateLatestVersionAndUpdate("master-SNAPSHOT"));
    }
}
