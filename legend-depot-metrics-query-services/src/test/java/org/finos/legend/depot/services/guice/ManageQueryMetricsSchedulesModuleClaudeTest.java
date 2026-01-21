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

package org.finos.legend.depot.services.guice;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

import com.diffblue.cover.annotations.ManagedByDiffblue;
import com.diffblue.cover.annotations.MethodsUnderTest;
import org.finos.legend.depot.services.api.metrics.query.QueryMetricsService;
import org.finos.legend.depot.services.api.schedules.SchedulesFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.function.Supplier;

class ManageQueryMetricsSchedulesModuleClaudeTest 

{

    /**
     * Test constructor.
     *
     * <p>Methods under test:
     *
     * <ul>
     *   <li>{@link ManageQueryMetricsSchedulesModule#ManageQueryMetricsSchedulesModule()}
     * </ul>
     */
    @Test
    @DisplayName("Test constructor")
    @Tag("ContributionFromDiffblue")
    @ManagedByDiffblue
    @MethodsUnderTest({"void ManageQueryMetricsSchedulesModule.<init>()"})
    void testConstructor()
  {
        // Arrange and Act
        ManageQueryMetricsSchedulesModule actualModule = new ManageQueryMetricsSchedulesModule();

        // Assert
        assertNotNull(actualModule);
    }

    /**
     * Test configure method.
     *
     * <p>Methods under test:
     *
     * <ul>
     *   <li>{@link ManageQueryMetricsSchedulesModule#configure()}
     * </ul>
     */
    @Test
    @DisplayName("Test configure method")
    @Tag("ContributionFromDiffblue")
    @ManagedByDiffblue
    @MethodsUnderTest({"void ManageQueryMetricsSchedulesModule.configure()"})
    void testConfigure()
  {
        // Arrange
        ManageQueryMetricsSchedulesModule module = new ManageQueryMetricsSchedulesModule();

        // Act - configure is protected, but we can test that construction doesn't fail
        // and the module can be instantiated properly
        module.configure();

        // Assert - configure() does nothing, so we just verify it completes without exception
        assertNotNull(module);
    }

    /**
     * Test scheduleMetricsConsolidation method.
     *
     * <p>Methods under test:
     *
     * <ul>
     *   <li>{@link ManageQueryMetricsSchedulesModule#scheduleMetricsConsolidation(SchedulesFactory, QueryMetricsService)}
     * </ul>
     */
    @Test
    @DisplayName("Test scheduleMetricsConsolidation registers schedule correctly")
    @Tag("ContributionFromDiffblue")
    @ManagedByDiffblue
    @MethodsUnderTest({"boolean ManageQueryMetricsSchedulesModule.scheduleMetricsConsolidation(SchedulesFactory, QueryMetricsService)"})
    void testScheduleMetricsConsolidation()
  {
        // Arrange
        ManageQueryMetricsSchedulesModule module = new ManageQueryMetricsSchedulesModule();
        SchedulesFactory schedulesFactory = mock(SchedulesFactory.class);
        QueryMetricsService queryMetricsService = mock(QueryMetricsService.class);

        // Act
        boolean result = module.scheduleMetricsConsolidation(schedulesFactory, queryMetricsService);

        // Assert
        assertTrue(result);

        // Verify that registerSingleInstance was called with the correct parameters
        ArgumentCaptor<String> nameCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Long> delayCaptor = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<Long> intervalCaptor = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<Supplier> taskCaptor = ArgumentCaptor.forClass(Supplier.class);

        verify(schedulesFactory, times(1)).registerSingleInstance(
            nameCaptor.capture(),
            delayCaptor.capture(),
            intervalCaptor.capture(),
            taskCaptor.capture()
        );

        assertEquals("consolidate-query-metrics", nameCaptor.getValue());
        assertEquals(SchedulesFactory.MINUTE, delayCaptor.getValue().longValue());
        assertEquals(6 * SchedulesFactory.HOUR, intervalCaptor.getValue().longValue());
        assertNotNull(taskCaptor.getValue());
    }

    /**
     * Test that the scheduled task calls consolidateMetrics.
     *
     * <p>Methods under test:
     *
     * <ul>
     *   <li>{@link ManageQueryMetricsSchedulesModule#scheduleMetricsConsolidation(SchedulesFactory, QueryMetricsService)}
     * </ul>
     */
    @Test
    @DisplayName("Test scheduled task executes consolidateMetrics")
    @Tag("ContributionFromDiffblue")
    @ManagedByDiffblue
    @MethodsUnderTest({"boolean ManageQueryMetricsSchedulesModule.scheduleMetricsConsolidation(SchedulesFactory, QueryMetricsService)"})
    void testScheduledTaskExecutesConsolidateMetrics()
  {
        // Arrange
        ManageQueryMetricsSchedulesModule module = new ManageQueryMetricsSchedulesModule();
        SchedulesFactory schedulesFactory = mock(SchedulesFactory.class);
        QueryMetricsService queryMetricsService = mock(QueryMetricsService.class);

        ArgumentCaptor<Supplier> taskCaptor = ArgumentCaptor.forClass(Supplier.class);

        // Act
        module.scheduleMetricsConsolidation(schedulesFactory, queryMetricsService);

        verify(schedulesFactory).registerSingleInstance(
            anyString(),
            anyLong(),
            anyLong(),
            taskCaptor.capture()
        );

        // Execute the captured task
        Supplier<Object> task = taskCaptor.getValue();
        Object taskResult = task.get();

        // Assert
        assertTrue((Boolean) taskResult);
        verify(queryMetricsService, times(1)).consolidateMetrics();
    }

    /**
     * Test scheduleMetricsConsolidation with expected timing parameters.
     *
     * <p>Methods under test:
     *
     * <ul>
     *   <li>{@link ManageQueryMetricsSchedulesModule#scheduleMetricsConsolidation(SchedulesFactory, QueryMetricsService)}
     * </ul>
     */
    @Test
    @DisplayName("Test scheduleMetricsConsolidation timing parameters")
    @Tag("ContributionFromDiffblue")
    @ManagedByDiffblue
    @MethodsUnderTest({"boolean ManageQueryMetricsSchedulesModule.scheduleMetricsConsolidation(SchedulesFactory, QueryMetricsService)"})
    void testScheduleMetricsConsolidationTimingParameters()
  {
        // Arrange
        ManageQueryMetricsSchedulesModule module = new ManageQueryMetricsSchedulesModule();
        SchedulesFactory schedulesFactory = mock(SchedulesFactory.class);
        QueryMetricsService queryMetricsService = mock(QueryMetricsService.class);

        // Act
        module.scheduleMetricsConsolidation(schedulesFactory, queryMetricsService);

        // Assert - verify the schedule is registered with 1 minute delay and 6 hour interval
        verify(schedulesFactory).registerSingleInstance(
            eq("consolidate-query-metrics"),
            eq(SchedulesFactory.MINUTE),
            eq(6 * SchedulesFactory.HOUR),
            any(Supplier.class)
        );
    }
}
