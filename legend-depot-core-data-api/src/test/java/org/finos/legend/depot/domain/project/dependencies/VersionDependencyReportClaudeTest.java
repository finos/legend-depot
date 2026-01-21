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

package org.finos.legend.depot.domain.project.dependencies;

import org.finos.legend.depot.domain.project.ProjectVersion;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class VersionDependencyReportClaudeTest


{
    @Test
    void testNoArgConstructor()
  {
        // Test the default no-arg constructor
        VersionDependencyReport report = new VersionDependencyReport();

        // Verify default values
        assertNotNull(report.getTransitiveDependencies());
        assertTrue(report.getTransitiveDependencies().isEmpty());
        assertTrue(report.isValid());
    }

    @Test
    void testParameterizedConstructorWithEmptyList()
  {
        // Test parameterized constructor with empty list and true
        List<ProjectVersion> dependencies = new ArrayList<>();
        VersionDependencyReport report = new VersionDependencyReport(dependencies, true);

        assertNotNull(report.getTransitiveDependencies());
        assertTrue(report.getTransitiveDependencies().isEmpty());
        assertTrue(report.isValid());
    }

    @Test
    void testParameterizedConstructorWithNonEmptyList()
  {
        // Test parameterized constructor with non-empty list
        ProjectVersion pv1 = new ProjectVersion("group1", "artifact1", "1.0.0");
        ProjectVersion pv2 = new ProjectVersion("group2", "artifact2", "2.0.0");
        List<ProjectVersion> dependencies = Arrays.asList(pv1, pv2);

        VersionDependencyReport report = new VersionDependencyReport(dependencies, true);

        assertNotNull(report.getTransitiveDependencies());
        assertEquals(2, report.getTransitiveDependencies().size());
        assertEquals(pv1, report.getTransitiveDependencies().get(0));
        assertEquals(pv2, report.getTransitiveDependencies().get(1));
        assertTrue(report.isValid());
    }

    @Test
    void testParameterizedConstructorWithInvalidFlag()
  {
        // Test parameterized constructor with valid=false
        List<ProjectVersion> dependencies = new ArrayList<>();
        VersionDependencyReport report = new VersionDependencyReport(dependencies, false);

        assertNotNull(report.getTransitiveDependencies());
        assertFalse(report.isValid());
    }

    @Test
    void testSetTransitiveDependencies()
  {
        // Test setting transitive dependencies
        VersionDependencyReport report = new VersionDependencyReport();

        ProjectVersion pv = new ProjectVersion("group", "artifact", "1.0.0");
        List<ProjectVersion> dependencies = Arrays.asList(pv);

        report.setTransitiveDependencies(dependencies);

        assertEquals(1, report.getTransitiveDependencies().size());
        assertEquals(pv, report.getTransitiveDependencies().get(0));
    }

    @Test
    void testSetTransitiveDependenciesToNull()
  {
        // Test setting transitive dependencies to null
        VersionDependencyReport report = new VersionDependencyReport();
        report.setTransitiveDependencies(null);

        assertNull(report.getTransitiveDependencies());
    }

    @Test
    void testSetValid()
  {
        // Test setting valid flag
        VersionDependencyReport report = new VersionDependencyReport();
        assertTrue(report.isValid());

        report.setValid(false);
        assertFalse(report.isValid());

        report.setValid(true);
        assertTrue(report.isValid());
    }

    @Test
    void testEqualsWithSameObject()
  {
        // Test equals with the same object reference
        VersionDependencyReport report = new VersionDependencyReport();
        assertEquals(report, report);
    }

    @Test
    void testEqualsWithEqualObjects()
  {
        // Test equals with two equal objects
        VersionDependencyReport report1 = new VersionDependencyReport();
        VersionDependencyReport report2 = new VersionDependencyReport();

        assertEquals(report1, report2);
        assertEquals(report2, report1);
    }

    @Test
    void testEqualsWithDifferentTransitiveDependencies()
  {
        // Test equals with different transitive dependencies
        ProjectVersion pv = new ProjectVersion("group", "artifact", "1.0.0");

        VersionDependencyReport report1 = new VersionDependencyReport();
        VersionDependencyReport report2 = new VersionDependencyReport(Arrays.asList(pv), true);

        assertNotEquals(report1, report2);
    }

    @Test
    void testEqualsWithDifferentValidFlag()
  {
        // Test equals with different valid flags
        VersionDependencyReport report1 = new VersionDependencyReport(new ArrayList<>(), true);
        VersionDependencyReport report2 = new VersionDependencyReport(new ArrayList<>(), false);

        assertNotEquals(report1, report2);
    }

    @Test
    void testEqualsWithNull()
  {
        // Test equals with null
        VersionDependencyReport report = new VersionDependencyReport();
        assertNotEquals(report, null);
    }

    @Test
    void testEqualsWithDifferentType()
  {
        // Test equals with different type
        VersionDependencyReport report = new VersionDependencyReport();
        assertNotEquals(report, "string");
        assertNotEquals(report, Integer.valueOf(42));
    }

    @Test
    void testHashCodeConsistency()
  {
        // Test that hashCode is consistent for the same object
        VersionDependencyReport report = new VersionDependencyReport();
        int hash1 = report.hashCode();
        int hash2 = report.hashCode();

        assertEquals(hash1, hash2);
    }

    @Test
    void testHashCodeForEqualObjects()
  {
        // Test that equal objects have the same hash code
        VersionDependencyReport report1 = new VersionDependencyReport();
        VersionDependencyReport report2 = new VersionDependencyReport();

        assertEquals(report1, report2);
        assertEquals(report1.hashCode(), report2.hashCode());
    }

    @Test
    void testHashCodeWithDifferentContent()
  {
        // Test hashCode with different content
        ProjectVersion pv = new ProjectVersion("group", "artifact", "1.0.0");

        VersionDependencyReport report1 = new VersionDependencyReport();
        VersionDependencyReport report2 = new VersionDependencyReport(Arrays.asList(pv), true);

        // Different objects should typically have different hash codes (not guaranteed, but likely)
        assertNotEquals(report1.hashCode(), report2.hashCode());
    }

    @Test
    void testEqualsAndHashCodeContractWithComplexData()
  {
        // Test equals and hashCode contract with complex data
        ProjectVersion pv1 = new ProjectVersion("group1", "artifact1", "1.0.0");
        ProjectVersion pv2 = new ProjectVersion("group2", "artifact2", "2.0.0");
        List<ProjectVersion> dependencies = Arrays.asList(pv1, pv2);

        VersionDependencyReport report1 = new VersionDependencyReport(dependencies, false);
        VersionDependencyReport report2 = new VersionDependencyReport(dependencies, false);

        // Test equals
        assertEquals(report1, report2);
        assertEquals(report2, report1);

        // Test hashCode
        assertEquals(report1.hashCode(), report2.hashCode());
    }

    @Test
    void testMutabilityOfTransitiveDependencies()
  {
        // Test that the list of transitive dependencies is mutable
        VersionDependencyReport report = new VersionDependencyReport();

        ProjectVersion pv1 = new ProjectVersion("group1", "artifact1", "1.0.0");
        report.getTransitiveDependencies().add(pv1);

        assertEquals(1, report.getTransitiveDependencies().size());
        assertEquals(pv1, report.getTransitiveDependencies().get(0));

        ProjectVersion pv2 = new ProjectVersion("group2", "artifact2", "2.0.0");
        report.getTransitiveDependencies().add(pv2);

        assertEquals(2, report.getTransitiveDependencies().size());
    }

    @Test
    void testReplacingTransitiveDependenciesList()
  {
        // Test replacing the entire list of transitive dependencies
        ProjectVersion pv1 = new ProjectVersion("group1", "artifact1", "1.0.0");
        List<ProjectVersion> dependencies1 = Arrays.asList(pv1);

        VersionDependencyReport report = new VersionDependencyReport(dependencies1, true);
        assertEquals(1, report.getTransitiveDependencies().size());

        ProjectVersion pv2 = new ProjectVersion("group2", "artifact2", "2.0.0");
        ProjectVersion pv3 = new ProjectVersion("group3", "artifact3", "3.0.0");
        List<ProjectVersion> dependencies2 = Arrays.asList(pv2, pv3);

        report.setTransitiveDependencies(dependencies2);
        assertEquals(2, report.getTransitiveDependencies().size());
        assertEquals(pv2, report.getTransitiveDependencies().get(0));
        assertEquals(pv3, report.getTransitiveDependencies().get(1));
    }
}
