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
import org.junit.Assert;
import org.junit.Test;

public class TestProjectUtilities
{

    @Test
    public void validGroupId()
    {
        Assert.assertTrue(CoordinateValidator.isValidGroupId("example.good.group"));
        Assert.assertTrue(CoordinateValidator.isValidGroupId("org.finos"));
        Assert.assertFalse(CoordinateValidator.isValidGroupId(""));
        Assert.assertFalse(CoordinateValidator.isValidGroupId("this is not a good one"));
        Assert.assertFalse(CoordinateValidator.isValidGroupId("this.is.starting.well but not ending well"));
        Assert.assertTrue(CoordinateValidator.isValidGroupId("singleWordNoDots"));
    }

    @Test
    public void validArtifactId()
    {
        Assert.assertTrue(CoordinateValidator.isValidArtifactId("test"));
        Assert.assertTrue(CoordinateValidator.isValidArtifactId("test-other"));
        Assert.assertFalse(CoordinateValidator.isValidArtifactId(""));
        Assert.assertFalse(CoordinateValidator.isValidArtifactId("singleWordNoDots"));
    }

    @Test
    public void testEvaluateLatestVersionAndUpdate()
    {
        StoreProjectData projectData = new StoreProjectData("PROD-1", "examples.test", "metadata", null, "2.1.0");

        Assert.assertTrue(projectData.evaluateLatestVersionAndUpdate("3.0.0"));
        Assert.assertEquals(projectData.getLatestVersion(), "3.0.0");

        projectData.setLatestVersion("2.1.0");
        Assert.assertFalse(projectData.evaluateLatestVersionAndUpdate("2.1.0"));
        Assert.assertFalse(projectData.evaluateLatestVersionAndUpdate("2.0.1"));

        projectData = new StoreProjectData("PROD-1", "examples.test", "metadata", null, null);
        Assert.assertTrue(projectData.evaluateLatestVersionAndUpdate("3.0.0"));
        Assert.assertEquals(projectData.getLatestVersion(), "3.0.0");

        Assert.assertFalse(projectData.evaluateLatestVersionAndUpdate("master-SNAPSHOT"));
    }
}
