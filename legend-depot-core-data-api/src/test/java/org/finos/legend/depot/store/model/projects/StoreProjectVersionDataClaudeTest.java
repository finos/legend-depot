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

import org.finos.legend.depot.domain.project.ProjectVersionData;
import org.finos.legend.depot.domain.project.dependencies.VersionDependencyReport;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Date;

public class StoreProjectVersionDataClaudeTest


{
    @Test
    public void testDefaultConstructor()
  {
        StoreProjectVersionData data = new StoreProjectVersionData();
        Assertions.assertNull(data.getGroupId());
        Assertions.assertNull(data.getArtifactId());
        Assertions.assertNull(data.getVersionId());
        Assertions.assertNull(data.getCreated());
        Assertions.assertNull(data.getUpdated());
        Assertions.assertFalse(data.isEvicted());
        Assertions.assertNotNull(data.getVersionData());
        Assertions.assertNotNull(data.getTransitiveDependenciesReport());
        Assertions.assertEquals("", data.getId());
    }

    @Test
    public void testThreeParameterConstructor()
  {
        StoreProjectVersionData data = new StoreProjectVersionData("com.example", "artifact1", "1.0.0");
        Assertions.assertEquals("com.example", data.getGroupId());
        Assertions.assertEquals("artifact1", data.getArtifactId());
        Assertions.assertEquals("1.0.0", data.getVersionId());
        Assertions.assertNotNull(data.getCreated());
        Assertions.assertNull(data.getUpdated());
        Assertions.assertFalse(data.isEvicted());
        Assertions.assertNotNull(data.getVersionData());
        Assertions.assertNotNull(data.getTransitiveDependenciesReport());
        Assertions.assertEquals("", data.getId());
    }

    @Test
    public void testThreeParameterConstructorWithNulls()
  {
        StoreProjectVersionData data = new StoreProjectVersionData(null, null, null);
        Assertions.assertNull(data.getGroupId());
        Assertions.assertNull(data.getArtifactId());
        Assertions.assertNull(data.getVersionId());
        Assertions.assertNotNull(data.getCreated());
        Assertions.assertNull(data.getUpdated());
        Assertions.assertFalse(data.isEvicted());
        Assertions.assertEquals("", data.getId());
    }

    @Test
    public void testThreeParameterConstructorSetsCreatedDate()
  {
        Date beforeCreation = new Date();
        StoreProjectVersionData data = new StoreProjectVersionData("com.example", "artifact1", "1.0.0");
        Date afterCreation = new Date();

        Assertions.assertNotNull(data.getCreated());
        Assertions.assertTrue(data.getCreated().getTime() >= beforeCreation.getTime());
        Assertions.assertTrue(data.getCreated().getTime() <= afterCreation.getTime());
    }

    @Test
    public void testFiveParameterConstructor()
  {
        ProjectVersionData versionData = new ProjectVersionData();
        StoreProjectVersionData data = new StoreProjectVersionData("com.example", "artifact1", "1.0.0", true, versionData);

        Assertions.assertEquals("com.example", data.getGroupId());
        Assertions.assertEquals("artifact1", data.getArtifactId());
        Assertions.assertEquals("1.0.0", data.getVersionId());
        Assertions.assertNotNull(data.getCreated());
        Assertions.assertNull(data.getUpdated());
        Assertions.assertTrue(data.isEvicted());
        Assertions.assertSame(versionData, data.getVersionData());
        Assertions.assertEquals("", data.getId());
    }

    @Test
    public void testFiveParameterConstructorWithEvictedFalse()
  {
        ProjectVersionData versionData = new ProjectVersionData();
        StoreProjectVersionData data = new StoreProjectVersionData("com.example", "artifact1", "1.0.0", false, versionData);

        Assertions.assertEquals("com.example", data.getGroupId());
        Assertions.assertEquals("artifact1", data.getArtifactId());
        Assertions.assertEquals("1.0.0", data.getVersionId());
        Assertions.assertNotNull(data.getCreated());
        Assertions.assertFalse(data.isEvicted());
        Assertions.assertSame(versionData, data.getVersionData());
    }

    @Test
    public void testFiveParameterConstructorWithNullVersionData()
  {
        StoreProjectVersionData data = new StoreProjectVersionData("com.example", "artifact1", "1.0.0", true, null);

        Assertions.assertEquals("com.example", data.getGroupId());
        Assertions.assertEquals("artifact1", data.getArtifactId());
        Assertions.assertEquals("1.0.0", data.getVersionId());
        Assertions.assertTrue(data.isEvicted());
        Assertions.assertNull(data.getVersionData());
    }

    @Test
    public void testFiveParameterConstructorSetsCreatedDate()
  {
        ProjectVersionData versionData = new ProjectVersionData();
        Date beforeCreation = new Date();
        StoreProjectVersionData data = new StoreProjectVersionData("com.example", "artifact1", "1.0.0", true, versionData);
        Date afterCreation = new Date();

        Assertions.assertNotNull(data.getCreated());
        Assertions.assertTrue(data.getCreated().getTime() >= beforeCreation.getTime());
        Assertions.assertTrue(data.getCreated().getTime() <= afterCreation.getTime());
    }

    @Test
    public void testGetCreated()
  {
        StoreProjectVersionData data = new StoreProjectVersionData("com.example", "artifact1", "1.0.0");
        Assertions.assertNotNull(data.getCreated());
    }

    @Test
    public void testSetCreated()
  {
        StoreProjectVersionData data = new StoreProjectVersionData();
        Date created = new Date();
        data.setCreated(created);
        Assertions.assertSame(created, data.getCreated());
    }

    @Test
    public void testSetCreatedToNull()
  {
        StoreProjectVersionData data = new StoreProjectVersionData("com.example", "artifact1", "1.0.0");
        Assertions.assertNotNull(data.getCreated());
        data.setCreated(null);
        Assertions.assertNull(data.getCreated());
    }

    @Test
    public void testSetCreatedOverwritesPreviousValue()
  {
        StoreProjectVersionData data = new StoreProjectVersionData("com.example", "artifact1", "1.0.0");
        Date firstDate = data.getCreated();
        Date secondDate = new Date(System.currentTimeMillis() + 10000);
        data.setCreated(secondDate);
        Assertions.assertNotSame(firstDate, data.getCreated());
        Assertions.assertSame(secondDate, data.getCreated());
    }

    @Test
    public void testIsEvictedDefaultValue()
  {
        StoreProjectVersionData data = new StoreProjectVersionData();
        Assertions.assertFalse(data.isEvicted());
    }

    @Test
    public void testSetEvictedToTrue()
  {
        StoreProjectVersionData data = new StoreProjectVersionData();
        Assertions.assertFalse(data.isEvicted());
        data.setEvicted(true);
        Assertions.assertTrue(data.isEvicted());
    }

    @Test
    public void testSetEvictedToFalse()
  {
        StoreProjectVersionData data = new StoreProjectVersionData("com.example", "artifact1", "1.0.0", true, new ProjectVersionData());
        Assertions.assertTrue(data.isEvicted());
        data.setEvicted(false);
        Assertions.assertFalse(data.isEvicted());
    }

    @Test
    public void testSetEvictedMultipleTimes()
  {
        StoreProjectVersionData data = new StoreProjectVersionData();
        data.setEvicted(true);
        Assertions.assertTrue(data.isEvicted());
        data.setEvicted(false);
        Assertions.assertFalse(data.isEvicted());
        data.setEvicted(true);
        Assertions.assertTrue(data.isEvicted());
    }

    @Test
    public void testGetVersionData()
  {
        StoreProjectVersionData data = new StoreProjectVersionData();
        Assertions.assertNotNull(data.getVersionData());
    }

    @Test
    public void testSetVersionData()
  {
        StoreProjectVersionData data = new StoreProjectVersionData();
        ProjectVersionData versionData = new ProjectVersionData();
        data.setVersionData(versionData);
        Assertions.assertSame(versionData, data.getVersionData());
    }

    @Test
    public void testSetVersionDataToNull()
  {
        StoreProjectVersionData data = new StoreProjectVersionData();
        Assertions.assertNotNull(data.getVersionData());
        data.setVersionData(null);
        Assertions.assertNull(data.getVersionData());
    }

    @Test
    public void testSetVersionDataOverwritesPreviousValue()
  {
        StoreProjectVersionData data = new StoreProjectVersionData();
        ProjectVersionData firstVersionData = new ProjectVersionData();
        ProjectVersionData secondVersionData = new ProjectVersionData();
        data.setVersionData(firstVersionData);
        Assertions.assertSame(firstVersionData, data.getVersionData());
        data.setVersionData(secondVersionData);
        Assertions.assertSame(secondVersionData, data.getVersionData());
        Assertions.assertNotSame(firstVersionData, data.getVersionData());
    }

    @Test
    public void testGetTransitiveDependenciesReport()
  {
        StoreProjectVersionData data = new StoreProjectVersionData();
        Assertions.assertNotNull(data.getTransitiveDependenciesReport());
    }

    @Test
    public void testSetTransitiveDependenciesReport()
  {
        StoreProjectVersionData data = new StoreProjectVersionData();
        VersionDependencyReport report = new VersionDependencyReport();
        data.setTransitiveDependenciesReport(report);
        Assertions.assertSame(report, data.getTransitiveDependenciesReport());
    }

    @Test
    public void testSetTransitiveDependenciesReportToNull()
  {
        StoreProjectVersionData data = new StoreProjectVersionData();
        Assertions.assertNotNull(data.getTransitiveDependenciesReport());
        data.setTransitiveDependenciesReport(null);
        Assertions.assertNull(data.getTransitiveDependenciesReport());
    }

    @Test
    public void testSetTransitiveDependenciesReportOverwritesPreviousValue()
  {
        StoreProjectVersionData data = new StoreProjectVersionData();
        VersionDependencyReport firstReport = new VersionDependencyReport();
        VersionDependencyReport secondReport = new VersionDependencyReport();
        data.setTransitiveDependenciesReport(firstReport);
        Assertions.assertSame(firstReport, data.getTransitiveDependenciesReport());
        data.setTransitiveDependenciesReport(secondReport);
        Assertions.assertSame(secondReport, data.getTransitiveDependenciesReport());
        Assertions.assertNotSame(firstReport, data.getTransitiveDependenciesReport());
    }

    @Test
    public void testGetUpdated()
  {
        StoreProjectVersionData data = new StoreProjectVersionData();
        Assertions.assertNull(data.getUpdated());
    }

    @Test
    public void testSetUpdated()
  {
        StoreProjectVersionData data = new StoreProjectVersionData();
        Date updated = new Date();
        data.setUpdated(updated);
        Assertions.assertSame(updated, data.getUpdated());
    }

    @Test
    public void testSetUpdatedToNull()
  {
        StoreProjectVersionData data = new StoreProjectVersionData();
        Date updated = new Date();
        data.setUpdated(updated);
        Assertions.assertNotNull(data.getUpdated());
        data.setUpdated(null);
        Assertions.assertNull(data.getUpdated());
    }

    @Test
    public void testSetUpdatedOverwritesPreviousValue()
  {
        StoreProjectVersionData data = new StoreProjectVersionData();
        Date firstDate = new Date();
        Date secondDate = new Date(System.currentTimeMillis() + 10000);
        data.setUpdated(firstDate);
        Assertions.assertSame(firstDate, data.getUpdated());
        data.setUpdated(secondDate);
        Assertions.assertSame(secondDate, data.getUpdated());
        Assertions.assertNotSame(firstDate, data.getUpdated());
    }

    @Test
    public void testGetIdAlwaysReturnsEmptyString()
  {
        StoreProjectVersionData data1 = new StoreProjectVersionData();
        StoreProjectVersionData data2 = new StoreProjectVersionData("com.example", "artifact1", "1.0.0");
        StoreProjectVersionData data3 = new StoreProjectVersionData("com.example", "artifact1", "1.0.0", true, new ProjectVersionData());

        Assertions.assertEquals("", data1.getId());
        Assertions.assertEquals("", data2.getId());
        Assertions.assertEquals("", data3.getId());
    }

    @Test
    public void testEqualsWithSameObject()
  {
        StoreProjectVersionData data = new StoreProjectVersionData("com.example", "artifact1", "1.0.0");
        Assertions.assertEquals(data, data);
    }

    @Test
    public void testEqualsWithNull()
  {
        StoreProjectVersionData data = new StoreProjectVersionData("com.example", "artifact1", "1.0.0");
        Assertions.assertNotEquals(data, null);
    }

    @Test
    public void testEqualsWithDifferentType()
  {
        StoreProjectVersionData data = new StoreProjectVersionData("com.example", "artifact1", "1.0.0");
        String differentType = "com.example:artifact1:1.0.0";
        Assertions.assertNotEquals(data, differentType);
    }

    @Test
    public void testEqualsWithEqualObjectsDefaultConstructor()
  {
        StoreProjectVersionData data1 = new StoreProjectVersionData();
        StoreProjectVersionData data2 = new StoreProjectVersionData();
        // Note: Due to reflection equals, these will not be equal because created dates differ
        // and the default constructor initializes versionData and transitiveDependenciesReport
        // as different instances
        Assertions.assertNotEquals(data1, data2);
    }

    @Test
    public void testEqualsWithIdenticalThreeParameterObjects()
  {
        // Note: These will not be equal because the created date is set in the constructor
        // and will be slightly different for each instance
        StoreProjectVersionData data1 = new StoreProjectVersionData("com.example", "artifact1", "1.0.0");
        StoreProjectVersionData data2 = new StoreProjectVersionData("com.example", "artifact1", "1.0.0");
        Assertions.assertNotEquals(data1, data2);
    }

    @Test
    public void testEqualsWithSameValuesAndSameCreatedDate()
  {
        ProjectVersionData versionData = new ProjectVersionData();
        VersionDependencyReport report = new VersionDependencyReport();
        Date sameDate = new Date();

        StoreProjectVersionData data1 = new StoreProjectVersionData("com.example", "artifact1", "1.0.0");
        data1.setCreated(sameDate);
        data1.setVersionData(versionData);
        data1.setTransitiveDependenciesReport(report);

        StoreProjectVersionData data2 = new StoreProjectVersionData("com.example", "artifact1", "1.0.0");
        data2.setCreated(sameDate);
        data2.setVersionData(versionData);
        data2.setTransitiveDependenciesReport(report);

        // They should be equal now with same created date and same object references
        Assertions.assertEquals(data1, data2);
    }

    @Test
    public void testEqualsWithDifferentGroupId()
  {
        StoreProjectVersionData data1 = new StoreProjectVersionData("com.example", "artifact1", "1.0.0");
        StoreProjectVersionData data2 = new StoreProjectVersionData("org.example", "artifact1", "1.0.0");

        Date sameDate = new Date();
        data1.setCreated(sameDate);
        data2.setCreated(sameDate);

        Assertions.assertNotEquals(data1, data2);
    }

    @Test
    public void testEqualsWithDifferentArtifactId()
  {
        StoreProjectVersionData data1 = new StoreProjectVersionData("com.example", "artifact1", "1.0.0");
        StoreProjectVersionData data2 = new StoreProjectVersionData("com.example", "artifact2", "1.0.0");

        Date sameDate = new Date();
        data1.setCreated(sameDate);
        data2.setCreated(sameDate);

        Assertions.assertNotEquals(data1, data2);
    }

    @Test
    public void testEqualsWithDifferentVersionId()
  {
        StoreProjectVersionData data1 = new StoreProjectVersionData("com.example", "artifact1", "1.0.0");
        StoreProjectVersionData data2 = new StoreProjectVersionData("com.example", "artifact1", "2.0.0");

        Date sameDate = new Date();
        data1.setCreated(sameDate);
        data2.setCreated(sameDate);

        Assertions.assertNotEquals(data1, data2);
    }

    @Test
    public void testEqualsWithDifferentEvictedFlag()
  {
        StoreProjectVersionData data1 = new StoreProjectVersionData("com.example", "artifact1", "1.0.0");
        StoreProjectVersionData data2 = new StoreProjectVersionData("com.example", "artifact1", "1.0.0");

        Date sameDate = new Date();
        data1.setCreated(sameDate);
        data2.setCreated(sameDate);

        data1.setEvicted(true);
        data2.setEvicted(false);

        Assertions.assertNotEquals(data1, data2);
    }

    @Test
    public void testEqualsWithDifferentUpdatedDate()
  {
        StoreProjectVersionData data1 = new StoreProjectVersionData("com.example", "artifact1", "1.0.0");
        StoreProjectVersionData data2 = new StoreProjectVersionData("com.example", "artifact1", "1.0.0");

        Date sameDate = new Date();
        data1.setCreated(sameDate);
        data2.setCreated(sameDate);

        data1.setUpdated(new Date());
        data2.setUpdated(new Date(System.currentTimeMillis() + 10000));

        Assertions.assertNotEquals(data1, data2);
    }

    @Test
    public void testEqualsWithAllFieldsEqual()
  {
        ProjectVersionData versionData = new ProjectVersionData();
        VersionDependencyReport report = new VersionDependencyReport();
        Date created = new Date();
        Date updated = new Date(System.currentTimeMillis() + 1000);

        StoreProjectVersionData data1 = new StoreProjectVersionData("com.example", "artifact1", "1.0.0");
        data1.setCreated(created);
        data1.setUpdated(updated);
        data1.setEvicted(true);
        data1.setVersionData(versionData);
        data1.setTransitiveDependenciesReport(report);

        StoreProjectVersionData data2 = new StoreProjectVersionData("com.example", "artifact1", "1.0.0");
        data2.setCreated(created);
        data2.setUpdated(updated);
        data2.setEvicted(true);
        data2.setVersionData(versionData);
        data2.setTransitiveDependenciesReport(report);

        Assertions.assertEquals(data1, data2);
    }

    @Test
    public void testHashCodeConsistency()
  {
        StoreProjectVersionData data = new StoreProjectVersionData("com.example", "artifact1", "1.0.0");
        int hashCode1 = data.hashCode();
        int hashCode2 = data.hashCode();
        Assertions.assertEquals(hashCode1, hashCode2);
    }

    @Test
    public void testHashCodeEqualityForEqualObjects()
  {
        ProjectVersionData versionData = new ProjectVersionData();
        VersionDependencyReport report = new VersionDependencyReport();
        Date created = new Date();
        Date updated = new Date(System.currentTimeMillis() + 1000);

        StoreProjectVersionData data1 = new StoreProjectVersionData("com.example", "artifact1", "1.0.0");
        data1.setCreated(created);
        data1.setUpdated(updated);
        data1.setEvicted(true);
        data1.setVersionData(versionData);
        data1.setTransitiveDependenciesReport(report);

        StoreProjectVersionData data2 = new StoreProjectVersionData("com.example", "artifact1", "1.0.0");
        data2.setCreated(created);
        data2.setUpdated(updated);
        data2.setEvicted(true);
        data2.setVersionData(versionData);
        data2.setTransitiveDependenciesReport(report);

        Assertions.assertEquals(data1.hashCode(), data2.hashCode());
    }

    @Test
    public void testHashCodeWithDefaultConstructor()
  {
        StoreProjectVersionData data1 = new StoreProjectVersionData();
        StoreProjectVersionData data2 = new StoreProjectVersionData();
        // HashCodes will differ because of different created dates and different object instances
        Assertions.assertNotEquals(data1.hashCode(), data2.hashCode());
    }

    @Test
    public void testHashCodeDifferenceForDifferentGroupId()
  {
        StoreProjectVersionData data1 = new StoreProjectVersionData("com.example", "artifact1", "1.0.0");
        StoreProjectVersionData data2 = new StoreProjectVersionData("org.example", "artifact1", "1.0.0");

        Date sameDate = new Date();
        data1.setCreated(sameDate);
        data2.setCreated(sameDate);

        Assertions.assertNotEquals(data1.hashCode(), data2.hashCode());
    }

    @Test
    public void testHashCodeDifferenceForDifferentArtifactId()
  {
        StoreProjectVersionData data1 = new StoreProjectVersionData("com.example", "artifact1", "1.0.0");
        StoreProjectVersionData data2 = new StoreProjectVersionData("com.example", "artifact2", "1.0.0");

        Date sameDate = new Date();
        data1.setCreated(sameDate);
        data2.setCreated(sameDate);

        Assertions.assertNotEquals(data1.hashCode(), data2.hashCode());
    }

    @Test
    public void testHashCodeDifferenceForDifferentVersionId()
  {
        StoreProjectVersionData data1 = new StoreProjectVersionData("com.example", "artifact1", "1.0.0");
        StoreProjectVersionData data2 = new StoreProjectVersionData("com.example", "artifact1", "2.0.0");

        Date sameDate = new Date();
        data1.setCreated(sameDate);
        data2.setCreated(sameDate);

        Assertions.assertNotEquals(data1.hashCode(), data2.hashCode());
    }
}
