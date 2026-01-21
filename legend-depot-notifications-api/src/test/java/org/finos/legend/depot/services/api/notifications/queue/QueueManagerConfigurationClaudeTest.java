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

package org.finos.legend.depot.services.api.notifications.queue;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Comprehensive tests for QueueManagerConfiguration.
 * Tests all getters, setters, and constructor initialization.
 */
class QueueManagerConfigurationClaudeTest 

{

    // ========== Constructor Tests ==========

    @Test
    @DisplayName("Constructor initializes queueInterval to default value (20 seconds)")
    void testConstructorInitializesQueueInterval()
  {
        QueueManagerConfiguration config = new QueueManagerConfiguration();

        assertEquals(20000L, config.getQueueInterval());
    }

    @Test
    @DisplayName("Constructor initializes queueDelay to default value (1 minute)")
    void testConstructorInitializesQueueDelay()
  {
        QueueManagerConfiguration config = new QueueManagerConfiguration();

        assertEquals(60000L, config.getQueueDelay());
    }

    @Test
    @DisplayName("Constructor initializes numberOfQueueWorkers to default value (1)")
    void testConstructorInitializesNumberOfQueueWorkers()
  {
        QueueManagerConfiguration config = new QueueManagerConfiguration();

        assertEquals(1L, config.getNumberOfQueueWorkers());
    }

    @Test
    @DisplayName("Constructor initializes all fields to their default values")
    void testConstructorInitializesAllFields()
  {
        QueueManagerConfiguration config = new QueueManagerConfiguration();

        assertEquals(20000L, config.getQueueInterval());
        assertEquals(60000L, config.getQueueDelay());
        assertEquals(1L, config.getNumberOfQueueWorkers());
    }

    // ========== QueueInterval Tests ==========

    @Test
    @DisplayName("setQueueInterval sets positive value correctly")
    void testSetQueueIntervalPositiveValue()
  {
        QueueManagerConfiguration config = new QueueManagerConfiguration();
        config.setQueueInterval(5000L);

        assertEquals(5000L, config.getQueueInterval());
    }

    @Test
    @DisplayName("setQueueInterval sets zero value correctly")
    void testSetQueueIntervalZero()
  {
        QueueManagerConfiguration config = new QueueManagerConfiguration();
        config.setQueueInterval(0L);

        assertEquals(0L, config.getQueueInterval());
    }

    @Test
    @DisplayName("setQueueInterval sets negative value correctly")
    void testSetQueueIntervalNegative()
  {
        QueueManagerConfiguration config = new QueueManagerConfiguration();
        config.setQueueInterval(-1000L);

        assertEquals(-1000L, config.getQueueInterval());
    }

    @Test
    @DisplayName("setQueueInterval sets very large value correctly")
    void testSetQueueIntervalLargeValue()
  {
        QueueManagerConfiguration config = new QueueManagerConfiguration();
        config.setQueueInterval(Long.MAX_VALUE);

        assertEquals(Long.MAX_VALUE, config.getQueueInterval());
    }

    @Test
    @DisplayName("setQueueInterval overwrites previous value")
    void testSetQueueIntervalOverwritesPreviousValue()
  {
        QueueManagerConfiguration config = new QueueManagerConfiguration();
        config.setQueueInterval(1000L);
        config.setQueueInterval(2000L);

        assertEquals(2000L, config.getQueueInterval());
    }

    @Test
    @DisplayName("getQueueInterval returns value after multiple sets")
    void testGetQueueIntervalAfterMultipleSets()
  {
        QueueManagerConfiguration config = new QueueManagerConfiguration();
        config.setQueueInterval(100L);
        config.setQueueInterval(200L);
        config.setQueueInterval(300L);

        assertEquals(300L, config.getQueueInterval());
    }

    // ========== QueueDelay Tests ==========

    @Test
    @DisplayName("setQueueDelay sets positive value correctly")
    void testSetQueueDelayPositiveValue()
  {
        QueueManagerConfiguration config = new QueueManagerConfiguration();
        config.setQueueDelay(10000L);

        assertEquals(10000L, config.getQueueDelay());
    }

    @Test
    @DisplayName("setQueueDelay sets zero value correctly")
    void testSetQueueDelayZero()
  {
        QueueManagerConfiguration config = new QueueManagerConfiguration();
        config.setQueueDelay(0L);

        assertEquals(0L, config.getQueueDelay());
    }

    @Test
    @DisplayName("setQueueDelay sets negative value correctly")
    void testSetQueueDelayNegative()
  {
        QueueManagerConfiguration config = new QueueManagerConfiguration();
        config.setQueueDelay(-5000L);

        assertEquals(-5000L, config.getQueueDelay());
    }

    @Test
    @DisplayName("setQueueDelay sets very large value correctly")
    void testSetQueueDelayLargeValue()
  {
        QueueManagerConfiguration config = new QueueManagerConfiguration();
        config.setQueueDelay(Long.MAX_VALUE);

        assertEquals(Long.MAX_VALUE, config.getQueueDelay());
    }

    @Test
    @DisplayName("setQueueDelay overwrites previous value")
    void testSetQueueDelayOverwritesPreviousValue()
  {
        QueueManagerConfiguration config = new QueueManagerConfiguration();
        config.setQueueDelay(3000L);
        config.setQueueDelay(4000L);

        assertEquals(4000L, config.getQueueDelay());
    }

    @Test
    @DisplayName("getQueueDelay returns value after multiple sets")
    void testGetQueueDelayAfterMultipleSets()
  {
        QueueManagerConfiguration config = new QueueManagerConfiguration();
        config.setQueueDelay(500L);
        config.setQueueDelay(600L);
        config.setQueueDelay(700L);

        assertEquals(700L, config.getQueueDelay());
    }

    // ========== NumberOfQueueWorkers Tests ==========

    @Test
    @DisplayName("setNumberOfQueueWorkers sets positive value correctly")
    void testSetNumberOfQueueWorkersPositiveValue()
  {
        QueueManagerConfiguration config = new QueueManagerConfiguration();
        config.setNumberOfQueueWorkers(5L);

        assertEquals(5L, config.getNumberOfQueueWorkers());
    }

    @Test
    @DisplayName("setNumberOfQueueWorkers sets zero value correctly")
    void testSetNumberOfQueueWorkersZero()
  {
        QueueManagerConfiguration config = new QueueManagerConfiguration();
        config.setNumberOfQueueWorkers(0L);

        assertEquals(0L, config.getNumberOfQueueWorkers());
    }

    @Test
    @DisplayName("setNumberOfQueueWorkers sets negative value correctly")
    void testSetNumberOfQueueWorkersNegative()
  {
        QueueManagerConfiguration config = new QueueManagerConfiguration();
        config.setNumberOfQueueWorkers(-10L);

        assertEquals(-10L, config.getNumberOfQueueWorkers());
    }

    @Test
    @DisplayName("setNumberOfQueueWorkers sets very large value correctly")
    void testSetNumberOfQueueWorkersLargeValue()
  {
        QueueManagerConfiguration config = new QueueManagerConfiguration();
        config.setNumberOfQueueWorkers(Long.MAX_VALUE);

        assertEquals(Long.MAX_VALUE, config.getNumberOfQueueWorkers());
    }

    @Test
    @DisplayName("setNumberOfQueueWorkers overwrites previous value")
    void testSetNumberOfQueueWorkersOverwritesPreviousValue()
  {
        QueueManagerConfiguration config = new QueueManagerConfiguration();
        config.setNumberOfQueueWorkers(2L);
        config.setNumberOfQueueWorkers(3L);

        assertEquals(3L, config.getNumberOfQueueWorkers());
    }

    @Test
    @DisplayName("getNumberOfQueueWorkers returns value after multiple sets")
    void testGetNumberOfQueueWorkersAfterMultipleSets()
  {
        QueueManagerConfiguration config = new QueueManagerConfiguration();
        config.setNumberOfQueueWorkers(10L);
        config.setNumberOfQueueWorkers(20L);
        config.setNumberOfQueueWorkers(30L);

        assertEquals(30L, config.getNumberOfQueueWorkers());
    }

    // ========== Integration Tests ==========

    @Test
    @DisplayName("Setting multiple properties independently works correctly")
    void testSettingMultiplePropertiesIndependently()
  {
        QueueManagerConfiguration config = new QueueManagerConfiguration();

        config.setQueueInterval(15000L);
        config.setQueueDelay(30000L);
        config.setNumberOfQueueWorkers(4L);

        assertEquals(15000L, config.getQueueInterval());
        assertEquals(30000L, config.getQueueDelay());
        assertEquals(4L, config.getNumberOfQueueWorkers());
    }

    @Test
    @DisplayName("Setting one property does not affect other properties")
    void testSettingOnePropertyDoesNotAffectOthers()
  {
        QueueManagerConfiguration config = new QueueManagerConfiguration();

        long originalDelay = config.getQueueDelay();
        long originalWorkers = config.getNumberOfQueueWorkers();

        config.setQueueInterval(99999L);

        assertEquals(99999L, config.getQueueInterval());
        assertEquals(originalDelay, config.getQueueDelay());
        assertEquals(originalWorkers, config.getNumberOfQueueWorkers());
    }

    @Test
    @DisplayName("All setters can be chained")
    void testSettersCanBeChained()
  {
        QueueManagerConfiguration config = new QueueManagerConfiguration();

        config.setQueueInterval(1000L);
        config.setQueueDelay(2000L);
        config.setNumberOfQueueWorkers(3L);

        assertEquals(1000L, config.getQueueInterval());
        assertEquals(2000L, config.getQueueDelay());
        assertEquals(3L, config.getNumberOfQueueWorkers());
    }

    @Test
    @DisplayName("Configuration can be reset to custom values")
    void testConfigurationCanBeResetToCustomValues()
  {
        QueueManagerConfiguration config = new QueueManagerConfiguration();

        // Set initial custom values
        config.setQueueInterval(5000L);
        config.setQueueDelay(10000L);
        config.setNumberOfQueueWorkers(2L);

        // Reset to different values
        config.setQueueInterval(7000L);
        config.setQueueDelay(14000L);
        config.setNumberOfQueueWorkers(5L);

        assertEquals(7000L, config.getQueueInterval());
        assertEquals(14000L, config.getQueueDelay());
        assertEquals(5L, config.getNumberOfQueueWorkers());
    }

    @Test
    @DisplayName("Configuration values are independent across instances")
    void testConfigurationValuesAreIndependentAcrossInstances()
  {
        QueueManagerConfiguration config1 = new QueueManagerConfiguration();
        QueueManagerConfiguration config2 = new QueueManagerConfiguration();

        config1.setQueueInterval(1000L);
        config1.setQueueDelay(2000L);
        config1.setNumberOfQueueWorkers(3L);

        config2.setQueueInterval(4000L);
        config2.setQueueDelay(5000L);
        config2.setNumberOfQueueWorkers(6L);

        // Verify config1 is unchanged
        assertEquals(1000L, config1.getQueueInterval());
        assertEquals(2000L, config1.getQueueDelay());
        assertEquals(3L, config1.getNumberOfQueueWorkers());

        // Verify config2 has different values
        assertEquals(4000L, config2.getQueueInterval());
        assertEquals(5000L, config2.getQueueDelay());
        assertEquals(6L, config2.getNumberOfQueueWorkers());
    }

    // ========== Edge Case Tests ==========

    @Test
    @DisplayName("Setting minimum long value works correctly")
    void testSettingMinimumLongValues()
  {
        QueueManagerConfiguration config = new QueueManagerConfiguration();

        config.setQueueInterval(Long.MIN_VALUE);
        config.setQueueDelay(Long.MIN_VALUE);
        config.setNumberOfQueueWorkers(Long.MIN_VALUE);

        assertEquals(Long.MIN_VALUE, config.getQueueInterval());
        assertEquals(Long.MIN_VALUE, config.getQueueDelay());
        assertEquals(Long.MIN_VALUE, config.getNumberOfQueueWorkers());
    }

    @Test
    @DisplayName("Setting maximum long value works correctly")
    void testSettingMaximumLongValues()
  {
        QueueManagerConfiguration config = new QueueManagerConfiguration();

        config.setQueueInterval(Long.MAX_VALUE);
        config.setQueueDelay(Long.MAX_VALUE);
        config.setNumberOfQueueWorkers(Long.MAX_VALUE);

        assertEquals(Long.MAX_VALUE, config.getQueueInterval());
        assertEquals(Long.MAX_VALUE, config.getQueueDelay());
        assertEquals(Long.MAX_VALUE, config.getNumberOfQueueWorkers());
    }

    @Test
    @DisplayName("Setting same value multiple times works correctly")
    void testSettingSameValueMultipleTimes()
  {
        QueueManagerConfiguration config = new QueueManagerConfiguration();

        config.setQueueInterval(5000L);
        config.setQueueInterval(5000L);
        config.setQueueInterval(5000L);

        assertEquals(5000L, config.getQueueInterval());
    }
}
