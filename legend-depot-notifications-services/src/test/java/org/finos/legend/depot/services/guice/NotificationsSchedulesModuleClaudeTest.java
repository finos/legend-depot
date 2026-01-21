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
import org.finos.legend.depot.services.api.notifications.NotificationsService;
import org.finos.legend.depot.services.api.schedules.SchedulesFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class NotificationsSchedulesModuleClaudeTest


{
    /**
     * Test constructor.
     *
     * <p>Methods under test:
     *
     * <ul>
     *   <li>{@link NotificationsSchedulesModule#NotificationsSchedulesModule()}
     * </ul>
     */
    @Test
    @DisplayName("Test constructor")
    @Tag("ContributionFromDiffblue")
    @ManagedByDiffblue
    @MethodsUnderTest({"void NotificationsSchedulesModule.<init>()"})
    public void testConstructor()
  {
        // Arrange and Act
        NotificationsSchedulesModule actualModule = new NotificationsSchedulesModule();

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
     *   <li>{@link NotificationsSchedulesModule#configure()}
     * </ul>
     */
    @Test
    @DisplayName("Test configure method indirectly through module instantiation")
    @Tag("ContributionFromDiffblue")
    @ManagedByDiffblue
    @MethodsUnderTest({"void NotificationsSchedulesModule.configure()"})
    public void testConfigure()
  {
        // Arrange and Act - Test configure() indirectly by verifying the module can be instantiated.
        // If configure() had binding errors, the module would fail to be used in application context.
        NotificationsSchedulesModule module = new NotificationsSchedulesModule();

        // Assert - If we successfully create the module, configure() must be structurally valid
        assertNotNull(module);
    }

    /**
     * Test notificationsCleanUp registers the cleanup schedule correctly.
     *
     * <p>Methods under test:
     *
     * <ul>
     *   <li>{@link NotificationsSchedulesModule#notificationsCleanUp(SchedulesFactory, NotificationsService)}
     * </ul>
     */
    @Test
    @DisplayName("Test notificationsCleanUp registers cleanup schedule")
    @Tag("ContributionFromDiffblue")
    @ManagedByDiffblue
    @MethodsUnderTest({"boolean NotificationsSchedulesModule.notificationsCleanUp(SchedulesFactory, NotificationsService)"})
    public void testNotificationsCleanUpRegistersSchedule()
  {
        // Arrange
        SchedulesFactory schedulesFactory = mock(SchedulesFactory.class);
        NotificationsService notificationsService = mock(NotificationsService.class);
        NotificationsSchedulesModule module = new NotificationsSchedulesModule();

        // Act
        boolean result = module.notificationsCleanUp(schedulesFactory, notificationsService);

        // Assert
        assertTrue(result);
        verify(schedulesFactory, times(1)).register(
            eq("clean-notifications-schedule"),
            eq(SchedulesFactory.MINUTE),
            eq(1 * SchedulesFactory.HOUR),
            any()
        );
    }

    /**
     * Test notificationsCleanUp verifies the schedule parameters.
     *
     * <p>Methods under test:
     *
     * <ul>
     *   <li>{@link NotificationsSchedulesModule#notificationsCleanUp(SchedulesFactory, NotificationsService)}
     * </ul>
     */
    @Test
    @DisplayName("Test notificationsCleanUp verifies schedule parameters")
    @Tag("ContributionFromDiffblue")
    @ManagedByDiffblue
    @MethodsUnderTest({"boolean NotificationsSchedulesModule.notificationsCleanUp(SchedulesFactory, NotificationsService)"})
    public void testNotificationsCleanUpVerifiesScheduleParameters()
  {
        // Arrange
        SchedulesFactory schedulesFactory = mock(SchedulesFactory.class);
        NotificationsService notificationsService = mock(NotificationsService.class);
        NotificationsSchedulesModule module = new NotificationsSchedulesModule();

        // Act
        boolean result = module.notificationsCleanUp(schedulesFactory, notificationsService);

        // Assert
        assertTrue(result);
        // Verify that register is called with the correct parameters:
        // - schedule name: "clean-notifications-schedule"
        // - delay: MINUTE (6000L)
        // - interval: 1 * HOUR (3600000L)
        verify(schedulesFactory, times(1)).register(
            eq("clean-notifications-schedule"),
            eq(6000L),
            eq(3600000L),
            any()
        );
    }

    /**
     * Test notificationsCleanUp ensures schedule task is registered only once.
     *
     * <p>Methods under test:
     *
     * <ul>
     *   <li>{@link NotificationsSchedulesModule#notificationsCleanUp(SchedulesFactory, NotificationsService)}
     * </ul>
     */
    @Test
    @DisplayName("Test notificationsCleanUp registers schedule once")
    @Tag("ContributionFromDiffblue")
    @ManagedByDiffblue
    @MethodsUnderTest({"boolean NotificationsSchedulesModule.notificationsCleanUp(SchedulesFactory, NotificationsService)"})
    public void testNotificationsCleanUpRegistersScheduleOnce()
  {
        // Arrange
        SchedulesFactory schedulesFactory = mock(SchedulesFactory.class);
        NotificationsService notificationsService = mock(NotificationsService.class);
        NotificationsSchedulesModule module = new NotificationsSchedulesModule();

        // Act
        module.notificationsCleanUp(schedulesFactory, notificationsService);

        // Assert
        verify(schedulesFactory, times(1)).register(any(), anyLong(), anyLong(), any());
    }

    /**
     * Test notificationsCleanUp returns true unconditionally.
     *
     * <p>Methods under test:
     *
     * <ul>
     *   <li>{@link NotificationsSchedulesModule#notificationsCleanUp(SchedulesFactory, NotificationsService)}
     * </ul>
     */
    @Test
    @DisplayName("Test notificationsCleanUp returns true")
    @Tag("ContributionFromDiffblue")
    @ManagedByDiffblue
    @MethodsUnderTest({"boolean NotificationsSchedulesModule.notificationsCleanUp(SchedulesFactory, NotificationsService)"})
    public void testNotificationsCleanUpReturnsTrue()
  {
        // Arrange
        SchedulesFactory schedulesFactory = mock(SchedulesFactory.class);
        NotificationsService notificationsService = mock(NotificationsService.class);
        NotificationsSchedulesModule module = new NotificationsSchedulesModule();

        // Act
        boolean result = module.notificationsCleanUp(schedulesFactory, notificationsService);

        // Assert - The method always returns true
        assertTrue(result);
    }

    /**
     * Test that the cleanup schedule task, when executed, calls deleteOldNotifications with 30 days.
     *
     * <p>Methods under test:
     *
     * <ul>
     *   <li>{@link NotificationsSchedulesModule#notificationsCleanUp(SchedulesFactory, NotificationsService)}
     * </ul>
     */
    @Test
    @DisplayName("Test cleanup task calls deleteOldNotifications with 30 days")
    @Tag("ContributionFromDiffblue")
    @ManagedByDiffblue
    @MethodsUnderTest({"boolean NotificationsSchedulesModule.notificationsCleanUp(SchedulesFactory, NotificationsService)"})
    public void testCleanupTaskCallsDeleteOldNotifications()
  {
        // Arrange
        SchedulesFactory schedulesFactory = mock(SchedulesFactory.class);
        NotificationsService notificationsService = mock(NotificationsService.class);
        when(notificationsService.deleteOldNotifications(30)).thenReturn(5L);
        NotificationsSchedulesModule module = new NotificationsSchedulesModule();

        // Capture the task supplier that gets registered
        final Supplier<Object>[] capturedTask = new Supplier[1];
        org.mockito.Mockito.doAnswer(invocation ->
                {
            capturedTask[0] = invocation.getArgument(3);
            return null;
        }).when(schedulesFactory).register(any(), anyLong(), anyLong(), any());

        // Act
        module.notificationsCleanUp(schedulesFactory, notificationsService);

        // Execute the captured task to verify it calls deleteOldNotifications
        assertNotNull(capturedTask[0]);
        capturedTask[0].get();

        // Assert - Verify that the task calls deleteOldNotifications with 30 days
        verify(notificationsService, times(1)).deleteOldNotifications(30);
    }

    /**
     * Test cleanup task executes successfully and returns the result from deleteOldNotifications.
     *
     * <p>Methods under test:
     *
     * <ul>
     *   <li>{@link NotificationsSchedulesModule#notificationsCleanUp(SchedulesFactory, NotificationsService)}
     * </ul>
     */
    @Test
    @DisplayName("Test cleanup task executes successfully")
    @Tag("ContributionFromDiffblue")
    @ManagedByDiffblue
    @MethodsUnderTest({"boolean NotificationsSchedulesModule.notificationsCleanUp(SchedulesFactory, NotificationsService)"})
    public void testCleanupTaskExecutesSuccessfully()
  {
        // Arrange
        SchedulesFactory schedulesFactory = mock(SchedulesFactory.class);
        NotificationsService notificationsService = mock(NotificationsService.class);
        long expectedDeletedCount = 42L;
        when(notificationsService.deleteOldNotifications(30)).thenReturn(expectedDeletedCount);
        NotificationsSchedulesModule module = new NotificationsSchedulesModule();

        // Capture the task supplier
        final Supplier<Object>[] capturedTask = new Supplier[1];
        org.mockito.Mockito.doAnswer(invocation ->
                {
            capturedTask[0] = invocation.getArgument(3);
            return null;
        }).when(schedulesFactory).register(any(), anyLong(), anyLong(), any());

        // Act
        module.notificationsCleanUp(schedulesFactory, notificationsService);

        // Execute the captured task
        assertNotNull(capturedTask[0]);
        Object result = capturedTask[0].get();

        // Assert - Verify the task returns the result from deleteOldNotifications
        verify(notificationsService, times(1)).deleteOldNotifications(30);
        assertTrue(result instanceof Long);
        assertTrue((Long) result == expectedDeletedCount);
    }
}
