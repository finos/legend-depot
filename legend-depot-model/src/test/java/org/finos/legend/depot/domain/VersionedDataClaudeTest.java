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

package org.finos.legend.depot.domain;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class VersionedDataClaudeTest


{
    // Concrete implementation of VersionedData for testing purposes
    private static class TestVersionedData extends VersionedData
    {
        public TestVersionedData()
  {
            super();
        }

        public TestVersionedData(String groupId, String artifactId, String versionId)
  {
            super(groupId, artifactId, versionId);
        }
    }

    @Test
    public void testGetVersionId()
  {
        TestVersionedData data = new TestVersionedData("com.example", "test-artifact", "1.0.0");
        Assertions.assertEquals("1.0.0", data.getVersionId());
    }

    @Test
    public void testGetVersionIdNull()
  {
        TestVersionedData data = new TestVersionedData("com.example", "test-artifact", null);
        Assertions.assertNull(data.getVersionId());
    }

    @Test
    public void testSetVersionId()
  {
        TestVersionedData data = new TestVersionedData("com.example", "test-artifact", "1.0.0");
        Assertions.assertEquals("1.0.0", data.getVersionId());

        data.setVersionId("2.0.0");
        Assertions.assertEquals("2.0.0", data.getVersionId());
    }

    @Test
    public void testSetVersionIdToNull()
  {
        TestVersionedData data = new TestVersionedData("com.example", "test-artifact", "1.0.0");
        data.setVersionId(null);
        Assertions.assertNull(data.getVersionId());
    }

    @Test
    public void testSetVersionIdWithDifferentFormats()
  {
        TestVersionedData data = new TestVersionedData();

        data.setVersionId("1.0.0");
        Assertions.assertEquals("1.0.0", data.getVersionId());

        data.setVersionId("2.0.0-SNAPSHOT");
        Assertions.assertEquals("2.0.0-SNAPSHOT", data.getVersionId());

        data.setVersionId("master-SNAPSHOT");
        Assertions.assertEquals("master-SNAPSHOT", data.getVersionId());
    }

    @Test
    public void testEqualsWithSameObject()
  {
        TestVersionedData data = new TestVersionedData("com.example", "test-artifact", "1.0.0");
        Assertions.assertEquals(data, data);
    }

    @Test
    public void testEqualsWithEqualObjects()
  {
        TestVersionedData data1 = new TestVersionedData("com.example", "test-artifact", "1.0.0");
        TestVersionedData data2 = new TestVersionedData("com.example", "test-artifact", "1.0.0");
        Assertions.assertEquals(data1, data2);
    }

    @Test
    public void testEqualsWithDifferentVersionId()
  {
        TestVersionedData data1 = new TestVersionedData("com.example", "test-artifact", "1.0.0");
        TestVersionedData data2 = new TestVersionedData("com.example", "test-artifact", "2.0.0");
        Assertions.assertNotEquals(data1, data2);
    }

    @Test
    public void testEqualsWithDifferentGroupId()
  {
        TestVersionedData data1 = new TestVersionedData("com.example", "test-artifact", "1.0.0");
        TestVersionedData data2 = new TestVersionedData("com.other", "test-artifact", "1.0.0");
        Assertions.assertNotEquals(data1, data2);
    }

    @Test
    public void testEqualsWithDifferentArtifactId()
  {
        TestVersionedData data1 = new TestVersionedData("com.example", "test-artifact", "1.0.0");
        TestVersionedData data2 = new TestVersionedData("com.example", "other-artifact", "1.0.0");
        Assertions.assertNotEquals(data1, data2);
    }

    @Test
    public void testEqualsWithNull()
  {
        TestVersionedData data = new TestVersionedData("com.example", "test-artifact", "1.0.0");
        Assertions.assertNotEquals(data, null);
    }

    @Test
    public void testEqualsWithDifferentType()
  {
        TestVersionedData data = new TestVersionedData("com.example", "test-artifact", "1.0.0");
        String differentType = "com.example:test-artifact:1.0.0";
        Assertions.assertNotEquals(data, differentType);
    }

    @Test
    public void testEqualsWithNullVersionId()
  {
        TestVersionedData data1 = new TestVersionedData("com.example", "test-artifact", null);
        TestVersionedData data2 = new TestVersionedData("com.example", "test-artifact", null);
        Assertions.assertEquals(data1, data2);
    }

    @Test
    public void testEqualsWithAllNullFields()
  {
        TestVersionedData data1 = new TestVersionedData(null, null, null);
        TestVersionedData data2 = new TestVersionedData(null, null, null);
        Assertions.assertEquals(data1, data2);
    }

    @Test
    public void testEqualsWithPartiallyNullFields()
  {
        TestVersionedData data1 = new TestVersionedData("com.example", null, "1.0.0");
        TestVersionedData data2 = new TestVersionedData("com.example", null, "1.0.0");
        Assertions.assertEquals(data1, data2);
    }

    @Test
    public void testEqualsWithMixedNullVersionId()
  {
        TestVersionedData data1 = new TestVersionedData("com.example", "test-artifact", null);
        TestVersionedData data2 = new TestVersionedData("com.example", "test-artifact", "1.0.0");
        Assertions.assertNotEquals(data1, data2);
    }

    @Test
    public void testHashCodeConsistency()
  {
        TestVersionedData data = new TestVersionedData("com.example", "test-artifact", "1.0.0");
        int hashCode1 = data.hashCode();
        int hashCode2 = data.hashCode();
        Assertions.assertEquals(hashCode1, hashCode2);
    }

    @Test
    public void testHashCodeEqualityForEqualObjects()
  {
        TestVersionedData data1 = new TestVersionedData("com.example", "test-artifact", "1.0.0");
        TestVersionedData data2 = new TestVersionedData("com.example", "test-artifact", "1.0.0");
        Assertions.assertEquals(data1.hashCode(), data2.hashCode());
    }

    @Test
    public void testHashCodeDifferenceForDifferentVersionId()
  {
        TestVersionedData data1 = new TestVersionedData("com.example", "test-artifact", "1.0.0");
        TestVersionedData data2 = new TestVersionedData("com.example", "test-artifact", "2.0.0");
        // Note: Different objects may have same hash code, but it's very unlikely with different values
        // This test documents expected behavior but may not always fail for unequal objects
        Assertions.assertNotEquals(data1.hashCode(), data2.hashCode());
    }

    @Test
    public void testHashCodeDifferenceForDifferentObjects()
  {
        TestVersionedData data1 = new TestVersionedData("com.example", "test-artifact", "1.0.0");
        TestVersionedData data2 = new TestVersionedData("com.other", "other-artifact", "2.0.0");
        // Note: Different objects may have same hash code, but it's very unlikely with different values
        // This test documents expected behavior but may not always fail for unequal objects
        Assertions.assertNotEquals(data1.hashCode(), data2.hashCode());
    }

    @Test
    public void testHashCodeWithNullVersionId()
  {
        TestVersionedData data1 = new TestVersionedData("com.example", "test-artifact", null);
        TestVersionedData data2 = new TestVersionedData("com.example", "test-artifact", null);
        Assertions.assertEquals(data1.hashCode(), data2.hashCode());
    }

    @Test
    public void testHashCodeWithAllNullFields()
  {
        TestVersionedData data1 = new TestVersionedData(null, null, null);
        TestVersionedData data2 = new TestVersionedData(null, null, null);
        Assertions.assertEquals(data1.hashCode(), data2.hashCode());
    }

    @Test
    public void testDefaultConstructor()
  {
        TestVersionedData data = new TestVersionedData();
        Assertions.assertNull(data.getGroupId());
        Assertions.assertNull(data.getArtifactId());
        Assertions.assertNull(data.getVersionId());
    }

    @Test
    public void testParameterizedConstructor()
  {
        TestVersionedData data = new TestVersionedData("com.example", "test-artifact", "1.0.0");
        Assertions.assertEquals("com.example", data.getGroupId());
        Assertions.assertEquals("test-artifact", data.getArtifactId());
        Assertions.assertEquals("1.0.0", data.getVersionId());
    }

    @Test
    public void testParameterizedConstructorWithNullValues()
  {
        TestVersionedData data = new TestVersionedData(null, null, null);
        Assertions.assertNull(data.getGroupId());
        Assertions.assertNull(data.getArtifactId());
        Assertions.assertNull(data.getVersionId());
    }
}
