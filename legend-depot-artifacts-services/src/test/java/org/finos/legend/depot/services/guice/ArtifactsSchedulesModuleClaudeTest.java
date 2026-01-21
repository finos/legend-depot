//  Copyright 2023 Goldman Sachs
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

package org.finos.legend.depot.services.guice;

import org.finos.legend.depot.services.api.artifacts.configuration.ArtifactsRefreshPolicyConfiguration;
import org.finos.legend.depot.services.api.artifacts.configuration.ArtifactsRetentionPolicyConfiguration;
import org.finos.legend.depot.services.api.artifacts.purge.ArtifactsPurgeService;
import org.finos.legend.depot.services.api.artifacts.refresh.ArtifactsRefreshService;
import org.finos.legend.depot.services.api.artifacts.refresh.ParentEvent;
import org.finos.legend.depot.services.api.schedules.SchedulesFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.function.Supplier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ArtifactsSchedulesModuleClaudeTest
{
    private ArtifactsSchedulesModule module;
    private SchedulesFactory mockSchedulesFactory;
    private ArtifactsRefreshService mockRefreshService;
    private ArtifactsPurgeService mockPurgeService;
    private ArtifactsRefreshPolicyConfiguration mockRefreshConfig;
    private ArtifactsRetentionPolicyConfiguration mockRetentionConfig;

    @BeforeEach
    public void setup()
    {
        module = new ArtifactsSchedulesModule();
        mockSchedulesFactory = mock(SchedulesFactory.class);
        mockRefreshService = mock(ArtifactsRefreshService.class);
        mockPurgeService = mock(ArtifactsPurgeService.class);
        mockRefreshConfig = mock(ArtifactsRefreshPolicyConfiguration.class);
        mockRetentionConfig = mock(ArtifactsRetentionPolicyConfiguration.class);
    }

    @Test
    public void testConstructor()
    {
        // Test that the constructor can be called successfully
        ArtifactsSchedulesModule newModule = new ArtifactsSchedulesModule();
        Assertions.assertNotNull(newModule);
    }

    @Test
    public void testConfigure()
    {
        // The configure method is empty, but we should verify it can be called
        // This test just ensures the method exists and doesn't throw an exception
        Assertions.assertDoesNotThrow(() -> module.configure());
    }

    @Test
    public void testInitVersions()
    {
        // Setup
        long expectedInterval = 7200000L; // 2 hours
        when(mockRefreshConfig.getVersionsUpdateIntervalInMillis()).thenReturn(expectedInterval);
        doNothing().when(mockSchedulesFactory).registerExternalTriggerSchedule(anyString(), anyLong(), any(Supplier.class));

        // Execute
        boolean result = module.initVersions(mockSchedulesFactory, mockRefreshService, mockRefreshConfig);

        // Verify
        Assertions.assertTrue(result);

        // Capture the arguments passed to registerExternalTriggerSchedule
        ArgumentCaptor<String> nameCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Long> intervalCaptor = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<Supplier> supplierCaptor = ArgumentCaptor.forClass(Supplier.class);

        verify(mockSchedulesFactory).registerExternalTriggerSchedule(
            nameCaptor.capture(),
            intervalCaptor.capture(),
            supplierCaptor.capture()
        );

        // Verify the schedule name
        Assertions.assertEquals(ParentEvent.REFRESH_ALL_VERSION_ARTIFACTS_SCHEDULE.name(), nameCaptor.getValue());

        // Verify the interval
        Assertions.assertEquals(expectedInterval, intervalCaptor.getValue());

        // Verify the supplier works correctly by invoking it
        Supplier<Object> capturedSupplier = supplierCaptor.getValue();
        Assertions.assertNotNull(capturedSupplier);

        // Execute the supplier and verify it calls the refresh service
        capturedSupplier.get();
        verify(mockRefreshService).refreshAllVersionsForAllProjects(false, false, false, ParentEvent.REFRESH_ALL_VERSION_ARTIFACTS_SCHEDULE.name());
    }

    @Test
    public void testInitVersionsWithDifferentInterval()
    {
        // Setup with a different interval
        long customInterval = 3600000L; // 1 hour
        when(mockRefreshConfig.getVersionsUpdateIntervalInMillis()).thenReturn(customInterval);
        doNothing().when(mockSchedulesFactory).registerExternalTriggerSchedule(anyString(), anyLong(), any(Supplier.class));

        // Execute
        boolean result = module.initVersions(mockSchedulesFactory, mockRefreshService, mockRefreshConfig);

        // Verify
        Assertions.assertTrue(result);

        ArgumentCaptor<Long> intervalCaptor = ArgumentCaptor.forClass(Long.class);
        verify(mockSchedulesFactory).registerExternalTriggerSchedule(
            anyString(),
            intervalCaptor.capture(),
            any(Supplier.class)
        );

        Assertions.assertEquals(customInterval, intervalCaptor.getValue());
    }

    @Test
    public void testScheduleEvictionOfProjectVersions()
    {
        // Setup
        int ttlForVersions = 365;
        int ttlForSnapshots = 30;
        when(mockRetentionConfig.getTtlForVersions()).thenReturn(ttlForVersions);
        when(mockRetentionConfig.getTtlForSnapshots()).thenReturn(ttlForSnapshots);
        doNothing().when(mockSchedulesFactory).registerSingleInstance(anyString(), anyLong(), anyLong(), any(Supplier.class));

        // Execute
        boolean result = module.scheduleEvictionOfProjectVersions(mockSchedulesFactory, mockPurgeService, mockRetentionConfig);

        // Verify
        Assertions.assertTrue(result);

        // Capture the arguments
        ArgumentCaptor<String> nameCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Long> delayCaptor = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<Long> intervalCaptor = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<Supplier> supplierCaptor = ArgumentCaptor.forClass(Supplier.class);

        verify(mockSchedulesFactory).registerSingleInstance(
            nameCaptor.capture(),
            delayCaptor.capture(),
            intervalCaptor.capture(),
            supplierCaptor.capture()
        );

        // Verify the schedule name
        Assertions.assertEquals("evict-LRU-project-versions", nameCaptor.getValue());

        // Verify the delay (MINUTE)
        Assertions.assertEquals(SchedulesFactory.MINUTE, delayCaptor.getValue());

        // Verify the interval (24 hours)
        Assertions.assertEquals(24 * SchedulesFactory.HOUR, intervalCaptor.getValue());

        // Verify the supplier works correctly
        Supplier<Object> capturedSupplier = supplierCaptor.getValue();
        Assertions.assertNotNull(capturedSupplier);

        // Execute the supplier and verify it calls the purge service
        Object supplierResult = capturedSupplier.get();
        Assertions.assertTrue((Boolean) supplierResult);
        verify(mockPurgeService).evictLeastRecentlyUsed(ttlForVersions, ttlForSnapshots);
    }

    @Test
    public void testScheduleEvictionWithDifferentTTLs()
    {
        // Setup with different TTL values
        int customTtlVersions = 180;
        int customTtlSnapshots = 15;
        when(mockRetentionConfig.getTtlForVersions()).thenReturn(customTtlVersions);
        when(mockRetentionConfig.getTtlForSnapshots()).thenReturn(customTtlSnapshots);
        doNothing().when(mockSchedulesFactory).registerSingleInstance(anyString(), anyLong(), anyLong(), any(Supplier.class));

        // Execute
        boolean result = module.scheduleEvictionOfProjectVersions(mockSchedulesFactory, mockPurgeService, mockRetentionConfig);

        // Verify
        Assertions.assertTrue(result);

        // Capture and execute the supplier to verify TTL values are used
        ArgumentCaptor<Supplier> supplierCaptor = ArgumentCaptor.forClass(Supplier.class);
        verify(mockSchedulesFactory).registerSingleInstance(
            anyString(),
            anyLong(),
            anyLong(),
            supplierCaptor.capture()
        );

        Supplier<Object> capturedSupplier = supplierCaptor.getValue();
        capturedSupplier.get();
        verify(mockPurgeService).evictLeastRecentlyUsed(customTtlVersions, customTtlSnapshots);
    }

    @Test
    public void testScheduleDeprecationOfProjectVersions()
    {
        // Setup
        doNothing().when(mockSchedulesFactory).registerSingleInstance(anyString(), anyLong(), anyLong(), any(Supplier.class));

        // Execute
        boolean result = module.scheduleDeprecationOfProjectVersions(mockSchedulesFactory, mockPurgeService);

        // Verify
        Assertions.assertTrue(result);

        // Capture the arguments
        ArgumentCaptor<String> nameCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Long> delayCaptor = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<Long> intervalCaptor = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<Supplier> supplierCaptor = ArgumentCaptor.forClass(Supplier.class);

        verify(mockSchedulesFactory).registerSingleInstance(
            nameCaptor.capture(),
            delayCaptor.capture(),
            intervalCaptor.capture(),
            supplierCaptor.capture()
        );

        // Verify the schedule name
        Assertions.assertEquals("deprecate-versions-notInRepository", nameCaptor.getValue());

        // Verify the delay (MINUTE)
        Assertions.assertEquals(SchedulesFactory.MINUTE, delayCaptor.getValue());

        // Verify the interval (48 hours)
        Assertions.assertEquals(48 * SchedulesFactory.HOUR, intervalCaptor.getValue());

        // Verify the supplier works correctly
        Supplier<Object> capturedSupplier = supplierCaptor.getValue();
        Assertions.assertNotNull(capturedSupplier);

        // Execute the supplier and verify it calls the purge service
        Object supplierResult = capturedSupplier.get();
        Assertions.assertTrue((Boolean) supplierResult);
        verify(mockPurgeService).deprecateVersionsNotInRepository();
    }

    @Test
    public void testScheduleDeprecationSupplierReturnValue()
    {
        // Setup
        doNothing().when(mockSchedulesFactory).registerSingleInstance(anyString(), anyLong(), anyLong(), any(Supplier.class));

        // Execute
        module.scheduleDeprecationOfProjectVersions(mockSchedulesFactory, mockPurgeService);

        // Capture the supplier
        ArgumentCaptor<Supplier> supplierCaptor = ArgumentCaptor.forClass(Supplier.class);
        verify(mockSchedulesFactory).registerSingleInstance(
            anyString(),
            anyLong(),
            anyLong(),
            supplierCaptor.capture()
        );

        // Execute the supplier multiple times to ensure it always returns true
        Supplier<Object> capturedSupplier = supplierCaptor.getValue();
        Object result1 = capturedSupplier.get();
        Object result2 = capturedSupplier.get();

        Assertions.assertTrue((Boolean) result1);
        Assertions.assertTrue((Boolean) result2);
    }
}
