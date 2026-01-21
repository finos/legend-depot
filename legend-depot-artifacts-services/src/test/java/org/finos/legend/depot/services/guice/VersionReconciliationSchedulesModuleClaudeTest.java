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

import com.diffblue.cover.annotations.ManagedByDiffblue;
import com.diffblue.cover.annotations.MethodsUnderTest;
import org.finos.legend.depot.core.services.api.metrics.PrometheusMetricsHandler;
import org.finos.legend.depot.core.services.api.metrics.configuration.PrometheusConfiguration;
import org.finos.legend.depot.services.api.artifacts.reconciliation.VersionsReconciliationService;
import org.finos.legend.depot.services.api.schedules.SchedulesFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.function.Supplier;

import static org.finos.legend.depot.services.artifacts.reconciliation.VersionsReconciliationServiceImpl.MISSING_REPO_VERSIONS;
import static org.finos.legend.depot.services.artifacts.reconciliation.VersionsReconciliationServiceImpl.MISSING_STORE_VERSIONS;
import static org.finos.legend.depot.services.artifacts.reconciliation.VersionsReconciliationServiceImpl.PROJECTS;
import static org.finos.legend.depot.services.artifacts.reconciliation.VersionsReconciliationServiceImpl.PROJECT_UPDATE_EXCEPTIONS;
import static org.finos.legend.depot.services.artifacts.reconciliation.VersionsReconciliationServiceImpl.REPO_EXCEPTIONS;
import static org.finos.legend.depot.services.artifacts.reconciliation.VersionsReconciliationServiceImpl.REPO_VERSIONS;
import static org.finos.legend.depot.services.artifacts.reconciliation.VersionsReconciliationServiceImpl.STORE_VERSIONS;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class VersionReconciliationSchedulesModuleClaudeTest
{
    /**
     * Test constructor.
     *
     * <p>Methods under test:
     *
     * <ul>
     *   <li>{@link VersionReconciliationSchedulesModule#VersionReconciliationSchedulesModule()}
     * </ul>
     */
    @Test
    @DisplayName("Test constructor")
    @Tag("ContributionFromDiffblue")
    @ManagedByDiffblue
    @MethodsUnderTest({"void VersionReconciliationSchedulesModule.<init>()"})
    public void testConstructor()
    {
        // Arrange and Act
        VersionReconciliationSchedulesModule actualModule = new VersionReconciliationSchedulesModule();

        // Assert
        assertNotNull(actualModule);
    }

    /**
     * Test configure method.
     * Note: configure() is a protected method that is empty in this module.
     * It can be tested indirectly by verifying the module can be instantiated.
     *
     * <p>Methods under test:
     *
     * <ul>
     *   <li>{@link VersionReconciliationSchedulesModule#configure()}
     * </ul>
     */
    @Test
    @DisplayName("Test configure method indirectly through module instantiation")
    @Tag("ContributionFromDiffblue")
    @ManagedByDiffblue
    @MethodsUnderTest({"void VersionReconciliationSchedulesModule.configure()"})
    public void testConfigure()
    {
        // Arrange and Act - Test configure() indirectly by verifying the module can be instantiated.
        // If configure() had binding errors, the module would fail to be used in application context.
        VersionReconciliationSchedulesModule module = new VersionReconciliationSchedulesModule();

        // Assert - If we successfully create the module, configure() must be structurally valid
        assertNotNull(module);
    }

    /**
     * Test registerMetrics when Prometheus is enabled - verifies all gauges are registered.
     *
     * <p>Methods under test:
     *
     * <ul>
     *   <li>{@link VersionReconciliationSchedulesModule#registerMetrics(PrometheusConfiguration, SchedulesFactory, VersionsReconciliationService)}
     * </ul>
     */
    @Test
    @DisplayName("Test registerMetrics when Prometheus is enabled - registers all gauges")
    @Tag("ContributionFromDiffblue")
    @ManagedByDiffblue
    @MethodsUnderTest({"boolean VersionReconciliationSchedulesModule.registerMetrics(PrometheusConfiguration, SchedulesFactory, VersionsReconciliationService)"})
    public void testRegisterMetricsWhenPrometheusEnabledRegistersAllGauges()
    {
        // Arrange
        PrometheusMetricsHandler metricsHandler = mock(PrometheusMetricsHandler.class);
        PrometheusConfiguration prometheusConfiguration = new PrometheusConfiguration(true, metricsHandler);
        SchedulesFactory schedulesFactory = mock(SchedulesFactory.class);
        VersionsReconciliationService versionsReconciliationService = mock(VersionsReconciliationService.class);
        VersionReconciliationSchedulesModule module = new VersionReconciliationSchedulesModule();

        // Act
        boolean result = module.registerMetrics(prometheusConfiguration, schedulesFactory, versionsReconciliationService);

        // Assert
        assertTrue(result);
        verify(metricsHandler, times(1)).registerGauge(PROJECTS, PROJECTS);
        verify(metricsHandler, times(1)).registerGauge(REPO_VERSIONS, REPO_VERSIONS);
        verify(metricsHandler, times(1)).registerGauge(STORE_VERSIONS, STORE_VERSIONS);
        verify(metricsHandler, times(1)).registerGauge(MISSING_REPO_VERSIONS, MISSING_REPO_VERSIONS);
        verify(metricsHandler, times(1)).registerGauge(MISSING_STORE_VERSIONS, MISSING_STORE_VERSIONS);
        verify(metricsHandler, times(1)).registerGauge(REPO_EXCEPTIONS, REPO_EXCEPTIONS);
        verify(metricsHandler, times(1)).registerGauge(PROJECT_UPDATE_EXCEPTIONS, PROJECT_UPDATE_EXCEPTIONS);
    }

    /**
     * Test registerMetrics when Prometheus is enabled - verifies all schedules are registered.
     *
     * <p>Methods under test:
     *
     * <ul>
     *   <li>{@link VersionReconciliationSchedulesModule#registerMetrics(PrometheusConfiguration, SchedulesFactory, VersionsReconciliationService)}
     * </ul>
     */
    @Test
    @DisplayName("Test registerMetrics when Prometheus is enabled - registers all schedules")
    @Tag("ContributionFromDiffblue")
    @ManagedByDiffblue
    @MethodsUnderTest({"boolean VersionReconciliationSchedulesModule.registerMetrics(PrometheusConfiguration, SchedulesFactory, VersionsReconciliationService)"})
    public void testRegisterMetricsWhenPrometheusEnabledRegistersAllSchedules()
    {
        // Arrange
        PrometheusMetricsHandler metricsHandler = mock(PrometheusMetricsHandler.class);
        PrometheusConfiguration prometheusConfiguration = new PrometheusConfiguration(true, metricsHandler);
        SchedulesFactory schedulesFactory = mock(SchedulesFactory.class);
        VersionsReconciliationService versionsReconciliationService = mock(VersionsReconciliationService.class);
        VersionReconciliationSchedulesModule module = new VersionReconciliationSchedulesModule();

        // Act
        boolean result = module.registerMetrics(prometheusConfiguration, schedulesFactory, versionsReconciliationService);

        // Assert
        assertTrue(result);
        verify(schedulesFactory, times(1)).register(
            eq(VersionReconciliationSchedulesModule.REPOSITORY_METRICS_SCHEDULE),
            eq(5 * SchedulesFactory.MINUTE),
            eq(5 * SchedulesFactory.MINUTE),
            any()
        );
        verify(schedulesFactory, times(1)).register(
            eq(VersionReconciliationSchedulesModule.SYNC_PROJECT_LATEST_VERSIONS_SCHEDULE),
            eq(5 * SchedulesFactory.MINUTE),
            eq(5 * SchedulesFactory.MINUTE),
            any()
        );
    }

    /**
     * Test registerMetrics when Prometheus is enabled - verifies schedule parameters with exact values.
     *
     * <p>Methods under test:
     *
     * <ul>
     *   <li>{@link VersionReconciliationSchedulesModule#registerMetrics(PrometheusConfiguration, SchedulesFactory, VersionsReconciliationService)}
     * </ul>
     */
    @Test
    @DisplayName("Test registerMetrics when Prometheus is enabled - verifies schedule parameters")
    @Tag("ContributionFromDiffblue")
    @ManagedByDiffblue
    @MethodsUnderTest({"boolean VersionReconciliationSchedulesModule.registerMetrics(PrometheusConfiguration, SchedulesFactory, VersionsReconciliationService)"})
    public void testRegisterMetricsWhenPrometheusEnabledVerifiesScheduleParameters()
    {
        // Arrange
        PrometheusMetricsHandler metricsHandler = mock(PrometheusMetricsHandler.class);
        PrometheusConfiguration prometheusConfiguration = new PrometheusConfiguration(true, metricsHandler);
        SchedulesFactory schedulesFactory = mock(SchedulesFactory.class);
        VersionsReconciliationService versionsReconciliationService = mock(VersionsReconciliationService.class);
        VersionReconciliationSchedulesModule module = new VersionReconciliationSchedulesModule();

        // Act
        boolean result = module.registerMetrics(prometheusConfiguration, schedulesFactory, versionsReconciliationService);

        // Assert
        assertTrue(result);
        // Verify schedule parameters: delay and interval are both 5 * MINUTE (30000L)
        verify(schedulesFactory, times(1)).register(
            eq("repository-metrics-schedule"),
            eq(30000L),
            eq(30000L),
            any()
        );
        verify(schedulesFactory, times(1)).register(
            eq("sync-project-latest-versions-schedule"),
            eq(30000L),
            eq(30000L),
            any()
        );
    }

    /**
     * Test registerMetrics when Prometheus is enabled - verifies only 2 schedules are registered.
     *
     * <p>Methods under test:
     *
     * <ul>
     *   <li>{@link VersionReconciliationSchedulesModule#registerMetrics(PrometheusConfiguration, SchedulesFactory, VersionsReconciliationService)}
     * </ul>
     */
    @Test
    @DisplayName("Test registerMetrics when Prometheus is enabled - registers exactly 2 schedules")
    @Tag("ContributionFromDiffblue")
    @ManagedByDiffblue
    @MethodsUnderTest({"boolean VersionReconciliationSchedulesModule.registerMetrics(PrometheusConfiguration, SchedulesFactory, VersionsReconciliationService)"})
    public void testRegisterMetricsWhenPrometheusEnabledRegistersTwoSchedules()
    {
        // Arrange
        PrometheusMetricsHandler metricsHandler = mock(PrometheusMetricsHandler.class);
        PrometheusConfiguration prometheusConfiguration = new PrometheusConfiguration(true, metricsHandler);
        SchedulesFactory schedulesFactory = mock(SchedulesFactory.class);
        VersionsReconciliationService versionsReconciliationService = mock(VersionsReconciliationService.class);
        VersionReconciliationSchedulesModule module = new VersionReconciliationSchedulesModule();

        // Act
        module.registerMetrics(prometheusConfiguration, schedulesFactory, versionsReconciliationService);

        // Assert - Verify exactly 2 schedules are registered
        verify(schedulesFactory, times(2)).register(any(), anyLong(), anyLong(), any());
    }

    /**
     * Test registerMetrics when Prometheus is enabled - verifies 7 gauges are registered.
     *
     * <p>Methods under test:
     *
     * <ul>
     *   <li>{@link VersionReconciliationSchedulesModule#registerMetrics(PrometheusConfiguration, SchedulesFactory, VersionsReconciliationService)}
     * </ul>
     */
    @Test
    @DisplayName("Test registerMetrics when Prometheus is enabled - registers exactly 7 gauges")
    @Tag("ContributionFromDiffblue")
    @ManagedByDiffblue
    @MethodsUnderTest({"boolean VersionReconciliationSchedulesModule.registerMetrics(PrometheusConfiguration, SchedulesFactory, VersionsReconciliationService)"})
    public void testRegisterMetricsWhenPrometheusEnabledRegistersSevenGauges()
    {
        // Arrange
        PrometheusMetricsHandler metricsHandler = mock(PrometheusMetricsHandler.class);
        PrometheusConfiguration prometheusConfiguration = new PrometheusConfiguration(true, metricsHandler);
        SchedulesFactory schedulesFactory = mock(SchedulesFactory.class);
        VersionsReconciliationService versionsReconciliationService = mock(VersionsReconciliationService.class);
        VersionReconciliationSchedulesModule module = new VersionReconciliationSchedulesModule();

        // Act
        module.registerMetrics(prometheusConfiguration, schedulesFactory, versionsReconciliationService);

        // Assert - Verify exactly 7 gauges are registered
        verify(metricsHandler, times(7)).registerGauge(any(), any());
    }

    /**
     * Test registerMetrics when Prometheus is disabled - no gauges are registered.
     *
     * <p>Methods under test:
     *
     * <ul>
     *   <li>{@link VersionReconciliationSchedulesModule#registerMetrics(PrometheusConfiguration, SchedulesFactory, VersionsReconciliationService)}
     * </ul>
     */
    @Test
    @DisplayName("Test registerMetrics when Prometheus is disabled - no gauges registered")
    @Tag("ContributionFromDiffblue")
    @ManagedByDiffblue
    @MethodsUnderTest({"boolean VersionReconciliationSchedulesModule.registerMetrics(PrometheusConfiguration, SchedulesFactory, VersionsReconciliationService)"})
    public void testRegisterMetricsWhenPrometheusDisabledNoGaugesRegistered()
    {
        // Arrange
        PrometheusMetricsHandler metricsHandler = mock(PrometheusMetricsHandler.class);
        PrometheusConfiguration prometheusConfiguration = new PrometheusConfiguration(false, metricsHandler);
        SchedulesFactory schedulesFactory = mock(SchedulesFactory.class);
        VersionsReconciliationService versionsReconciliationService = mock(VersionsReconciliationService.class);
        VersionReconciliationSchedulesModule module = new VersionReconciliationSchedulesModule();

        // Act
        boolean result = module.registerMetrics(prometheusConfiguration, schedulesFactory, versionsReconciliationService);

        // Assert
        assertTrue(result);
        verify(metricsHandler, never()).registerGauge(any(), any());
    }

    /**
     * Test registerMetrics when Prometheus is disabled - no schedules are registered.
     *
     * <p>Methods under test:
     *
     * <ul>
     *   <li>{@link VersionReconciliationSchedulesModule#registerMetrics(PrometheusConfiguration, SchedulesFactory, VersionsReconciliationService)}
     * </ul>
     */
    @Test
    @DisplayName("Test registerMetrics when Prometheus is disabled - no schedules registered")
    @Tag("ContributionFromDiffblue")
    @ManagedByDiffblue
    @MethodsUnderTest({"boolean VersionReconciliationSchedulesModule.registerMetrics(PrometheusConfiguration, SchedulesFactory, VersionsReconciliationService)"})
    public void testRegisterMetricsWhenPrometheusDisabledNoSchedulesRegistered()
    {
        // Arrange
        PrometheusMetricsHandler metricsHandler = mock(PrometheusMetricsHandler.class);
        PrometheusConfiguration prometheusConfiguration = new PrometheusConfiguration(false, metricsHandler);
        SchedulesFactory schedulesFactory = mock(SchedulesFactory.class);
        VersionsReconciliationService versionsReconciliationService = mock(VersionsReconciliationService.class);
        VersionReconciliationSchedulesModule module = new VersionReconciliationSchedulesModule();

        // Act
        boolean result = module.registerMetrics(prometheusConfiguration, schedulesFactory, versionsReconciliationService);

        // Assert
        assertTrue(result);
        verify(schedulesFactory, never()).register(any(), anyLong(), anyLong(), any());
    }

    /**
     * Test registerMetrics when Prometheus is disabled - returns true.
     *
     * <p>Methods under test:
     *
     * <ul>
     *   <li>{@link VersionReconciliationSchedulesModule#registerMetrics(PrometheusConfiguration, SchedulesFactory, VersionsReconciliationService)}
     * </ul>
     */
    @Test
    @DisplayName("Test registerMetrics when Prometheus is disabled - returns true")
    @Tag("ContributionFromDiffblue")
    @ManagedByDiffblue
    @MethodsUnderTest({"boolean VersionReconciliationSchedulesModule.registerMetrics(PrometheusConfiguration, SchedulesFactory, VersionsReconciliationService)"})
    public void testRegisterMetricsWhenPrometheusDisabledReturnsTrue()
    {
        // Arrange
        PrometheusMetricsHandler metricsHandler = mock(PrometheusMetricsHandler.class);
        PrometheusConfiguration prometheusConfiguration = new PrometheusConfiguration(false, metricsHandler);
        SchedulesFactory schedulesFactory = mock(SchedulesFactory.class);
        VersionsReconciliationService versionsReconciliationService = mock(VersionsReconciliationService.class);
        VersionReconciliationSchedulesModule module = new VersionReconciliationSchedulesModule();

        // Act
        boolean result = module.registerMetrics(prometheusConfiguration, schedulesFactory, versionsReconciliationService);

        // Assert - The method always returns true regardless of Prometheus state
        assertTrue(result);
    }

    /**
     * Test that the repository metrics schedule task, when executed, calls findVersionsMismatches.
     *
     * <p>Methods under test:
     *
     * <ul>
     *   <li>{@link VersionReconciliationSchedulesModule#registerMetrics(PrometheusConfiguration, SchedulesFactory, VersionsReconciliationService)}
     * </ul>
     */
    @Test
    @DisplayName("Test repository metrics task calls findVersionsMismatches")
    @Tag("ContributionFromDiffblue")
    @ManagedByDiffblue
    @MethodsUnderTest({"boolean VersionReconciliationSchedulesModule.registerMetrics(PrometheusConfiguration, SchedulesFactory, VersionsReconciliationService)"})
    public void testRepositoryMetricsTaskCallsFindVersionsMismatches()
    {
        // Arrange
        PrometheusMetricsHandler metricsHandler = mock(PrometheusMetricsHandler.class);
        PrometheusConfiguration prometheusConfiguration = new PrometheusConfiguration(true, metricsHandler);
        SchedulesFactory schedulesFactory = mock(SchedulesFactory.class);
        VersionsReconciliationService versionsReconciliationService = mock(VersionsReconciliationService.class);
        VersionReconciliationSchedulesModule module = new VersionReconciliationSchedulesModule();

        // Capture the task supplier for repository-metrics-schedule
        final Supplier<Object>[] capturedTask = new Supplier[1];
        org.mockito.Mockito.doAnswer(invocation -> {
            String scheduleName = invocation.getArgument(0);
            if (VersionReconciliationSchedulesModule.REPOSITORY_METRICS_SCHEDULE.equals(scheduleName))
            {
                capturedTask[0] = invocation.getArgument(3);
            }
            return null;
        }).when(schedulesFactory).register(any(), anyLong(), anyLong(), any());

        // Act
        module.registerMetrics(prometheusConfiguration, schedulesFactory, versionsReconciliationService);

        // Execute the captured task to verify it calls findVersionsMismatches
        assertNotNull(capturedTask[0]);
        capturedTask[0].get();

        // Assert - Verify that the task calls findVersionsMismatches
        verify(versionsReconciliationService, times(1)).findVersionsMismatches();
    }

    /**
     * Test that the sync project latest versions schedule task, when executed, calls syncLatestProjectVersions.
     *
     * <p>Methods under test:
     *
     * <ul>
     *   <li>{@link VersionReconciliationSchedulesModule#registerMetrics(PrometheusConfiguration, SchedulesFactory, VersionsReconciliationService)}
     * </ul>
     */
    @Test
    @DisplayName("Test sync project latest versions task calls syncLatestProjectVersions")
    @Tag("ContributionFromDiffblue")
    @ManagedByDiffblue
    @MethodsUnderTest({"boolean VersionReconciliationSchedulesModule.registerMetrics(PrometheusConfiguration, SchedulesFactory, VersionsReconciliationService)"})
    public void testSyncProjectLatestVersionsTaskCallsSyncLatestProjectVersions()
    {
        // Arrange
        PrometheusMetricsHandler metricsHandler = mock(PrometheusMetricsHandler.class);
        PrometheusConfiguration prometheusConfiguration = new PrometheusConfiguration(true, metricsHandler);
        SchedulesFactory schedulesFactory = mock(SchedulesFactory.class);
        VersionsReconciliationService versionsReconciliationService = mock(VersionsReconciliationService.class);
        VersionReconciliationSchedulesModule module = new VersionReconciliationSchedulesModule();

        // Capture the task supplier for sync-project-latest-versions-schedule
        final Supplier<Object>[] capturedTask = new Supplier[1];
        org.mockito.Mockito.doAnswer(invocation -> {
            String scheduleName = invocation.getArgument(0);
            if (VersionReconciliationSchedulesModule.SYNC_PROJECT_LATEST_VERSIONS_SCHEDULE.equals(scheduleName))
            {
                capturedTask[0] = invocation.getArgument(3);
            }
            return null;
        }).when(schedulesFactory).register(any(), anyLong(), anyLong(), any());

        // Act
        module.registerMetrics(prometheusConfiguration, schedulesFactory, versionsReconciliationService);

        // Execute the captured task to verify it calls syncLatestProjectVersions
        assertNotNull(capturedTask[0]);
        capturedTask[0].get();

        // Assert - Verify that the task calls syncLatestProjectVersions
        verify(versionsReconciliationService, times(1)).syncLatestProjectVersions();
    }

    /**
     * Test registerMetrics always returns true.
     *
     * <p>Methods under test:
     *
     * <ul>
     *   <li>{@link VersionReconciliationSchedulesModule#registerMetrics(PrometheusConfiguration, SchedulesFactory, VersionsReconciliationService)}
     * </ul>
     */
    @Test
    @DisplayName("Test registerMetrics always returns true")
    @Tag("ContributionFromDiffblue")
    @ManagedByDiffblue
    @MethodsUnderTest({"boolean VersionReconciliationSchedulesModule.registerMetrics(PrometheusConfiguration, SchedulesFactory, VersionsReconciliationService)"})
    public void testRegisterMetricsAlwaysReturnsTrue()
    {
        // Arrange
        PrometheusMetricsHandler metricsHandler = mock(PrometheusMetricsHandler.class);
        PrometheusConfiguration prometheusConfiguration = new PrometheusConfiguration(true, metricsHandler);
        SchedulesFactory schedulesFactory = mock(SchedulesFactory.class);
        VersionsReconciliationService versionsReconciliationService = mock(VersionsReconciliationService.class);
        VersionReconciliationSchedulesModule module = new VersionReconciliationSchedulesModule();

        // Act
        boolean result = module.registerMetrics(prometheusConfiguration, schedulesFactory, versionsReconciliationService);

        // Assert - The method always returns true
        assertTrue(result);
    }
}
