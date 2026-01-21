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

public class CoordinateDataClaudeTest


{
    // Concrete implementation of CoordinateData for testing purposes
    private static class TestCoordinateData extends CoordinateData
    {
        public TestCoordinateData()
  {
            super();
        }

        public TestCoordinateData(String groupId, String artifactId)
  {
            super(groupId, artifactId);
        }
    }

    @Test
    public void testGetGroupId()
  {
        TestCoordinateData data = new TestCoordinateData("com.example", "test-artifact");
        Assertions.assertEquals("com.example", data.getGroupId());
    }

    @Test
    public void testGetGroupIdNull()
  {
        TestCoordinateData data = new TestCoordinateData(null, "test-artifact");
        Assertions.assertNull(data.getGroupId());
    }

    @Test
    public void testGetArtifactId()
  {
        TestCoordinateData data = new TestCoordinateData("com.example", "test-artifact");
        Assertions.assertEquals("test-artifact", data.getArtifactId());
    }

    @Test
    public void testGetArtifactIdNull()
  {
        TestCoordinateData data = new TestCoordinateData("com.example", null);
        Assertions.assertNull(data.getArtifactId());
    }

    @Test
    public void testSetArtifactId()
  {
        TestCoordinateData data = new TestCoordinateData("com.example", "initial-artifact");
        Assertions.assertEquals("initial-artifact", data.getArtifactId());

        data.setArtifactId("updated-artifact");
        Assertions.assertEquals("updated-artifact", data.getArtifactId());
    }

    @Test
    public void testSetArtifactIdToNull()
  {
        TestCoordinateData data = new TestCoordinateData("com.example", "test-artifact");
        data.setArtifactId(null);
        Assertions.assertNull(data.getArtifactId());
    }

    @Test
    public void testEqualsWithSameObject()
  {
        TestCoordinateData data = new TestCoordinateData("com.example", "test-artifact");
        Assertions.assertEquals(data, data);
    }

    @Test
    public void testEqualsWithEqualObjects()
  {
        TestCoordinateData data1 = new TestCoordinateData("com.example", "test-artifact");
        TestCoordinateData data2 = new TestCoordinateData("com.example", "test-artifact");
        Assertions.assertEquals(data1, data2);
    }

    @Test
    public void testEqualsWithDifferentGroupId()
  {
        TestCoordinateData data1 = new TestCoordinateData("com.example", "test-artifact");
        TestCoordinateData data2 = new TestCoordinateData("com.other", "test-artifact");
        Assertions.assertNotEquals(data1, data2);
    }

    @Test
    public void testEqualsWithDifferentArtifactId()
  {
        TestCoordinateData data1 = new TestCoordinateData("com.example", "test-artifact");
        TestCoordinateData data2 = new TestCoordinateData("com.example", "other-artifact");
        Assertions.assertNotEquals(data1, data2);
    }

    @Test
    public void testEqualsWithNull()
  {
        TestCoordinateData data = new TestCoordinateData("com.example", "test-artifact");
        Assertions.assertNotEquals(data, null);
    }

    @Test
    public void testEqualsWithDifferentType()
  {
        TestCoordinateData data = new TestCoordinateData("com.example", "test-artifact");
        String differentType = "com.example:test-artifact";
        Assertions.assertNotEquals(data, differentType);
    }

    @Test
    public void testEqualsWithNullFields()
  {
        TestCoordinateData data1 = new TestCoordinateData(null, null);
        TestCoordinateData data2 = new TestCoordinateData(null, null);
        Assertions.assertEquals(data1, data2);
    }

    @Test
    public void testEqualsWithPartiallyNullFields()
  {
        TestCoordinateData data1 = new TestCoordinateData("com.example", null);
        TestCoordinateData data2 = new TestCoordinateData("com.example", null);
        Assertions.assertEquals(data1, data2);
    }

    @Test
    public void testEqualsWithMixedNullFields()
  {
        TestCoordinateData data1 = new TestCoordinateData("com.example", null);
        TestCoordinateData data2 = new TestCoordinateData("com.example", "test-artifact");
        Assertions.assertNotEquals(data1, data2);
    }

    @Test
    public void testHashCodeConsistency()
  {
        TestCoordinateData data = new TestCoordinateData("com.example", "test-artifact");
        int hashCode1 = data.hashCode();
        int hashCode2 = data.hashCode();
        Assertions.assertEquals(hashCode1, hashCode2);
    }

    @Test
    public void testHashCodeEqualityForEqualObjects()
  {
        TestCoordinateData data1 = new TestCoordinateData("com.example", "test-artifact");
        TestCoordinateData data2 = new TestCoordinateData("com.example", "test-artifact");
        Assertions.assertEquals(data1.hashCode(), data2.hashCode());
    }

    @Test
    public void testHashCodeDifferenceForDifferentObjects()
  {
        TestCoordinateData data1 = new TestCoordinateData("com.example", "test-artifact");
        TestCoordinateData data2 = new TestCoordinateData("com.other", "other-artifact");
        // Note: Different objects may have same hash code, but it's very unlikely with different values
        // This test documents expected behavior but may not always fail for unequal objects
        Assertions.assertNotEquals(data1.hashCode(), data2.hashCode());
    }

    @Test
    public void testHashCodeWithNullFields()
  {
        TestCoordinateData data1 = new TestCoordinateData(null, null);
        TestCoordinateData data2 = new TestCoordinateData(null, null);
        Assertions.assertEquals(data1.hashCode(), data2.hashCode());
    }

    @Test
    public void testDefaultConstructor()
  {
        TestCoordinateData data = new TestCoordinateData();
        Assertions.assertNull(data.getGroupId());
        Assertions.assertNull(data.getArtifactId());
    }

    @Test
    public void testParameterizedConstructor()
  {
        TestCoordinateData data = new TestCoordinateData("com.example", "test-artifact");
        Assertions.assertEquals("com.example", data.getGroupId());
        Assertions.assertEquals("test-artifact", data.getArtifactId());
    }
}
