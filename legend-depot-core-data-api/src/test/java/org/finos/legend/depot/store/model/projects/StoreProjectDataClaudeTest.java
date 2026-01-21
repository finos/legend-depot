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

package org.finos.legend.depot.store.model.projects;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class StoreProjectDataClaudeTest


{
    @Test
    public void testDefaultConstructor()
  {
        StoreProjectData data = new StoreProjectData();
        Assertions.assertNull(data.getProjectId());
        Assertions.assertNull(data.getGroupId());
        Assertions.assertNull(data.getArtifactId());
        Assertions.assertNull(data.getDefaultBranch());
        Assertions.assertNull(data.getLatestVersion());
    }

    @Test
    public void testThreeParameterConstructor()
  {
        StoreProjectData data = new StoreProjectData("project1", "com.example", "artifact1");
        Assertions.assertEquals("project1", data.getProjectId());
        Assertions.assertEquals("com.example", data.getGroupId());
        Assertions.assertEquals("artifact1", data.getArtifactId());
        Assertions.assertNull(data.getDefaultBranch());
        Assertions.assertNull(data.getLatestVersion());
    }

    @Test
    public void testThreeParameterConstructorWithNulls()
  {
        StoreProjectData data = new StoreProjectData(null, null, null);
        Assertions.assertNull(data.getProjectId());
        Assertions.assertNull(data.getGroupId());
        Assertions.assertNull(data.getArtifactId());
        Assertions.assertNull(data.getDefaultBranch());
        Assertions.assertNull(data.getLatestVersion());
    }

    @Test
    public void testFiveParameterConstructor()
  {
        StoreProjectData data = new StoreProjectData("project1", "com.example", "artifact1", "main", "1.0.0");
        Assertions.assertEquals("project1", data.getProjectId());
        Assertions.assertEquals("com.example", data.getGroupId());
        Assertions.assertEquals("artifact1", data.getArtifactId());
        Assertions.assertEquals("main", data.getDefaultBranch());
        Assertions.assertEquals("1.0.0", data.getLatestVersion());
    }

    @Test
    public void testFiveParameterConstructorWithNulls()
  {
        StoreProjectData data = new StoreProjectData(null, null, null, null, null);
        Assertions.assertNull(data.getProjectId());
        Assertions.assertNull(data.getGroupId());
        Assertions.assertNull(data.getArtifactId());
        Assertions.assertNull(data.getDefaultBranch());
        Assertions.assertNull(data.getLatestVersion());
    }

    @Test
    public void testGetProjectId()
  {
        StoreProjectData data = new StoreProjectData("project1", "com.example", "artifact1");
        Assertions.assertEquals("project1", data.getProjectId());
    }

    @Test
    public void testGetDefaultBranch()
  {
        StoreProjectData data = new StoreProjectData("project1", "com.example", "artifact1", "master", "1.0.0");
        Assertions.assertEquals("master", data.getDefaultBranch());
    }

    @Test
    public void testSetDefaultBranch()
  {
        StoreProjectData data = new StoreProjectData();
        Assertions.assertNull(data.getDefaultBranch());
        data.setDefaultBranch("develop");
        Assertions.assertEquals("develop", data.getDefaultBranch());
    }

    @Test
    public void testSetDefaultBranchToNull()
  {
        StoreProjectData data = new StoreProjectData("project1", "com.example", "artifact1", "main", "1.0.0");
        Assertions.assertEquals("main", data.getDefaultBranch());
        data.setDefaultBranch(null);
        Assertions.assertNull(data.getDefaultBranch());
    }

    @Test
    public void testGetLatestVersion()
  {
        StoreProjectData data = new StoreProjectData("project1", "com.example", "artifact1", "main", "2.0.0");
        Assertions.assertEquals("2.0.0", data.getLatestVersion());
    }

    @Test
    public void testSetLatestVersion()
  {
        StoreProjectData data = new StoreProjectData();
        Assertions.assertNull(data.getLatestVersion());
        data.setLatestVersion("3.0.0");
        Assertions.assertEquals("3.0.0", data.getLatestVersion());
    }

    @Test
    public void testSetLatestVersionToNull()
  {
        StoreProjectData data = new StoreProjectData("project1", "com.example", "artifact1", "main", "1.0.0");
        Assertions.assertEquals("1.0.0", data.getLatestVersion());
        data.setLatestVersion(null);
        Assertions.assertNull(data.getLatestVersion());
    }

    @Test
    public void testGetId()
  {
        StoreProjectData data = new StoreProjectData("project1", "com.example", "artifact1");
        Assertions.assertEquals("", data.getId());
    }

    @Test
    public void testGetIdAlwaysReturnsEmptyString()
  {
        StoreProjectData data1 = new StoreProjectData();
        StoreProjectData data2 = new StoreProjectData("project1", "com.example", "artifact1", "main", "1.0.0");
        Assertions.assertEquals("", data1.getId());
        Assertions.assertEquals("", data2.getId());
    }

    @Test
    public void testEvaluateLatestVersionAndUpdateWithNullLatestVersion()
  {
        StoreProjectData data = new StoreProjectData("project1", "com.example", "artifact1");
        Assertions.assertNull(data.getLatestVersion());

        boolean result = data.evaluateLatestVersionAndUpdate("1.0.0");

        Assertions.assertTrue(result);
        Assertions.assertEquals("1.0.0", data.getLatestVersion());
    }

    @Test
    public void testEvaluateLatestVersionAndUpdateWithHigherVersion()
  {
        StoreProjectData data = new StoreProjectData("project1", "com.example", "artifact1", "main", "1.0.0");

        boolean result = data.evaluateLatestVersionAndUpdate("2.0.0");

        Assertions.assertTrue(result);
        Assertions.assertEquals("2.0.0", data.getLatestVersion());
    }

    @Test
    public void testEvaluateLatestVersionAndUpdateWithLowerVersion()
  {
        StoreProjectData data = new StoreProjectData("project1", "com.example", "artifact1", "main", "2.0.0");

        boolean result = data.evaluateLatestVersionAndUpdate("1.0.0");

        Assertions.assertFalse(result);
        Assertions.assertEquals("2.0.0", data.getLatestVersion());
    }

    @Test
    public void testEvaluateLatestVersionAndUpdateWithEqualVersion()
  {
        StoreProjectData data = new StoreProjectData("project1", "com.example", "artifact1", "main", "1.0.0");

        boolean result = data.evaluateLatestVersionAndUpdate("1.0.0");

        Assertions.assertFalse(result);
        Assertions.assertEquals("1.0.0", data.getLatestVersion());
    }

    @Test
    public void testEvaluateLatestVersionAndUpdateWithSnapshotVersion()
  {
        StoreProjectData data = new StoreProjectData("project1", "com.example", "artifact1");

        boolean result = data.evaluateLatestVersionAndUpdate("1.0.0-SNAPSHOT");

        Assertions.assertFalse(result);
        Assertions.assertNull(data.getLatestVersion());
    }

    @Test
    public void testEvaluateLatestVersionAndUpdateWithSnapshotVersionWhenLatestExists()
  {
        StoreProjectData data = new StoreProjectData("project1", "com.example", "artifact1", "main", "1.0.0");

        boolean result = data.evaluateLatestVersionAndUpdate("2.0.0-SNAPSHOT");

        Assertions.assertFalse(result);
        Assertions.assertEquals("1.0.0", data.getLatestVersion());
    }

    @Test
    public void testEvaluateLatestVersionAndUpdateWithPatchVersionIncrement()
  {
        StoreProjectData data = new StoreProjectData("project1", "com.example", "artifact1", "main", "1.0.0");

        boolean result = data.evaluateLatestVersionAndUpdate("1.0.1");

        Assertions.assertTrue(result);
        Assertions.assertEquals("1.0.1", data.getLatestVersion());
    }

    @Test
    public void testEvaluateLatestVersionAndUpdateWithMinorVersionIncrement()
  {
        StoreProjectData data = new StoreProjectData("project1", "com.example", "artifact1", "main", "1.0.5");

        boolean result = data.evaluateLatestVersionAndUpdate("1.1.0");

        Assertions.assertTrue(result);
        Assertions.assertEquals("1.1.0", data.getLatestVersion());
    }

    @Test
    public void testEvaluateLatestVersionAndUpdateWithMajorVersionIncrement()
  {
        StoreProjectData data = new StoreProjectData("project1", "com.example", "artifact1", "main", "1.9.9");

        boolean result = data.evaluateLatestVersionAndUpdate("2.0.0");

        Assertions.assertTrue(result);
        Assertions.assertEquals("2.0.0", data.getLatestVersion());
    }

    @Test
    public void testEqualsWithSameObject()
  {
        StoreProjectData data = new StoreProjectData("project1", "com.example", "artifact1");
        Assertions.assertEquals(data, data);
    }

    @Test
    public void testEqualsWithEqualObjects()
  {
        StoreProjectData data1 = new StoreProjectData("project1", "com.example", "artifact1");
        StoreProjectData data2 = new StoreProjectData("project1", "com.example", "artifact1");
        Assertions.assertEquals(data1, data2);
    }

    @Test
    public void testEqualsWithEqualObjectsIncludingAllFields()
  {
        StoreProjectData data1 = new StoreProjectData("project1", "com.example", "artifact1", "main", "1.0.0");
        StoreProjectData data2 = new StoreProjectData("project1", "com.example", "artifact1", "main", "1.0.0");
        Assertions.assertEquals(data1, data2);
    }

    @Test
    public void testEqualsWithDifferentProjectId()
  {
        StoreProjectData data1 = new StoreProjectData("project1", "com.example", "artifact1");
        StoreProjectData data2 = new StoreProjectData("project2", "com.example", "artifact1");
        Assertions.assertNotEquals(data1, data2);
    }

    @Test
    public void testEqualsWithDifferentGroupId()
  {
        StoreProjectData data1 = new StoreProjectData("project1", "com.example", "artifact1");
        StoreProjectData data2 = new StoreProjectData("project1", "org.example", "artifact1");
        Assertions.assertNotEquals(data1, data2);
    }

    @Test
    public void testEqualsWithDifferentArtifactId()
  {
        StoreProjectData data1 = new StoreProjectData("project1", "com.example", "artifact1");
        StoreProjectData data2 = new StoreProjectData("project1", "com.example", "artifact2");
        Assertions.assertNotEquals(data1, data2);
    }

    @Test
    public void testEqualsWithDifferentDefaultBranch()
  {
        StoreProjectData data1 = new StoreProjectData("project1", "com.example", "artifact1", "main", "1.0.0");
        StoreProjectData data2 = new StoreProjectData("project1", "com.example", "artifact1", "develop", "1.0.0");
        Assertions.assertNotEquals(data1, data2);
    }

    @Test
    public void testEqualsWithDifferentLatestVersion()
  {
        StoreProjectData data1 = new StoreProjectData("project1", "com.example", "artifact1", "main", "1.0.0");
        StoreProjectData data2 = new StoreProjectData("project1", "com.example", "artifact1", "main", "2.0.0");
        Assertions.assertNotEquals(data1, data2);
    }

    @Test
    public void testEqualsWithNull()
  {
        StoreProjectData data = new StoreProjectData("project1", "com.example", "artifact1");
        Assertions.assertNotEquals(data, null);
    }

    @Test
    public void testEqualsWithDifferentType()
  {
        StoreProjectData data = new StoreProjectData("project1", "com.example", "artifact1");
        String differentType = "project1:com.example:artifact1";
        Assertions.assertNotEquals(data, differentType);
    }

    @Test
    public void testEqualsWithAllNullFields()
  {
        StoreProjectData data1 = new StoreProjectData(null, null, null);
        StoreProjectData data2 = new StoreProjectData(null, null, null);
        Assertions.assertEquals(data1, data2);
    }

    @Test
    public void testHashCodeConsistency()
  {
        StoreProjectData data = new StoreProjectData("project1", "com.example", "artifact1", "main", "1.0.0");
        int hashCode1 = data.hashCode();
        int hashCode2 = data.hashCode();
        Assertions.assertEquals(hashCode1, hashCode2);
    }

    @Test
    public void testHashCodeEqualityForEqualObjects()
  {
        StoreProjectData data1 = new StoreProjectData("project1", "com.example", "artifact1", "main", "1.0.0");
        StoreProjectData data2 = new StoreProjectData("project1", "com.example", "artifact1", "main", "1.0.0");
        Assertions.assertEquals(data1.hashCode(), data2.hashCode());
    }

    @Test
    public void testHashCodeWithNullFields()
  {
        StoreProjectData data1 = new StoreProjectData(null, null, null);
        StoreProjectData data2 = new StoreProjectData(null, null, null);
        Assertions.assertEquals(data1.hashCode(), data2.hashCode());
    }

    @Test
    public void testHashCodeDifferenceForDifferentProjectId()
  {
        StoreProjectData data1 = new StoreProjectData("project1", "com.example", "artifact1");
        StoreProjectData data2 = new StoreProjectData("project2", "com.example", "artifact1");
        Assertions.assertNotEquals(data1.hashCode(), data2.hashCode());
    }

    @Test
    public void testHashCodeDifferenceForDifferentObjects()
  {
        StoreProjectData data1 = new StoreProjectData("project1", "com.example", "artifact1", "main", "1.0.0");
        StoreProjectData data2 = new StoreProjectData("project2", "org.example", "artifact2", "develop", "2.0.0");
        Assertions.assertNotEquals(data1.hashCode(), data2.hashCode());
    }
}
