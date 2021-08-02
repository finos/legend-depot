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

package org.finos.legend.depot.domain.version;

import org.finos.legend.depot.domain.project.ProjectData;
import org.junit.Assert;
import org.junit.Test;

public class TestVersionValidator
{
    @Test
    public void testVersionsAreCorrect()
    {
        Assert.assertTrue(VersionValidator.isValid("1.1.1"));
        Assert.assertTrue(VersionValidator.isValid("master-SNAPSHOT"));
        Assert.assertFalse(VersionValidator.isValid("jkwhfkjasf-jhdfjks"));
    }


    @Test
    public void canGetLatestVersion()
    {
        ProjectData projectData = new ProjectData("one", "sample", "project");
        projectData.addVersion("1.0.0");
        projectData.addVersion("1.0.1");
        projectData.addVersion("2.0.0");
        projectData.addVersion("2.0.2");
        Assert.assertEquals("2.0.2", projectData.getLatestVersionAsString());
    }
}
