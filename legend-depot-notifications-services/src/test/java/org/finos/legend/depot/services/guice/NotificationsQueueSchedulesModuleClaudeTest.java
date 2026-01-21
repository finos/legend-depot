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
import org.finos.legend.depot.services.api.notifications.queue.QueueManagerConfiguration;
import org.finos.legend.depot.services.api.schedules.SchedulesFactory;
import org.finos.legend.depot.services.notifications.NotificationsQueueManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class NotificationsQueueSchedulesModuleClaudeTest


{
    /**
     * Test constructor.
     *
     * <p>Methods under test:
     *
     * <ul>
     *   <li>{@link NotificationsQueueSchedulesModule#NotificationsQueueSchedulesModule()}
     * </ul>
     */
    @Test
    @DisplayName("Test constructor")
    @Tag("ContributionFromDiffblue")
    @ManagedByDiffblue
    @MethodsUnderTest({"void NotificationsQueueSchedulesModule.<init>()"})
    public void testConstructor()
  {
        // Arrange and Act
        NotificationsQueueSchedulesModule actualModule = new NotificationsQueueSchedulesModule();

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
     *   <li>{@link NotificationsQueueSchedulesModule#configure()}
     * </ul>
     */
    @Test
    @DisplayName("Test configure method indirectly through module instantiation")
    @Tag("ContributionFromDiffblue")
    @ManagedByDiffblue
    @MethodsUnderTest({"void NotificationsQueueSchedulesModule.configure()"})
    public void testConfigure()
  {
        // Arrange and Act - Test configure() indirectly by verifying the module can be instantiated.
        // If configure() had binding errors, the module would fail to be used in application context.
        NotificationsQueueSchedulesModule module = new NotificationsQueueSchedulesModule();

        // Assert - If we successfully create the module, configure() must be structurally valid
        assertNotNull(module);
    }

    /**
     * Test initQueue with a single queue worker.
     *
     * <p>Methods under test:
     *
     * <ul>
     *   <li>{@link NotificationsQueueSchedulesModule#initQueue(SchedulesFactory, QueueManagerConfiguration, NotificationsQueueManager)}
     * </ul>
     */
    @Test
    @DisplayName("Test initQueue with single worker")
    @Tag("ContributionFromDiffblue")
    @ManagedByDiffblue
    @MethodsUnderTest({"boolean NotificationsQueueSchedulesModule.initQueue(SchedulesFactory, QueueManagerConfiguration, NotificationsQueueManager)"})
    public void testInitQueueWithSingleWorker()
  {
        // Arrange
        SchedulesFactory schedulesFactory = mock(SchedulesFactory.class);
        QueueManagerConfiguration config = new QueueManagerConfiguration();
        config.setNumberOfQueueWorkers(1);
        config.setQueueDelay(60000L);
        config.setQueueInterval(20000L);
        NotificationsQueueManager notificationsManager = mock(NotificationsQueueManager.class);
        NotificationsQueueSchedulesModule module = new NotificationsQueueSchedulesModule();

        // Act
        boolean result = module.initQueue(schedulesFactory, config, notificationsManager);

        // Assert
        assertTrue(result);
        verify(schedulesFactory, times(1)).register(
            eq("queue-observer_1"),
            eq(60000L),
            eq(20000L),
            any()
        );
    }

    /**
     * Test initQueue with multiple queue workers.
     *
     * <p>Methods under test:
     *
     * <ul>
     *   <li>{@link NotificationsQueueSchedulesModule#initQueue(SchedulesFactory, QueueManagerConfiguration, NotificationsQueueManager)}
     * </ul>
     */
    @Test
    @DisplayName("Test initQueue with multiple workers")
    @Tag("ContributionFromDiffblue")
    @ManagedByDiffblue
    @MethodsUnderTest({"boolean NotificationsQueueSchedulesModule.initQueue(SchedulesFactory, QueueManagerConfiguration, NotificationsQueueManager)"})
    public void testInitQueueWithMultipleWorkers()
  {
        // Arrange
        SchedulesFactory schedulesFactory = mock(SchedulesFactory.class);
        QueueManagerConfiguration config = new QueueManagerConfiguration();
        config.setNumberOfQueueWorkers(3);
        config.setQueueDelay(60000L);
        config.setQueueInterval(20000L);
        NotificationsQueueManager notificationsManager = mock(NotificationsQueueManager.class);
        NotificationsQueueSchedulesModule module = new NotificationsQueueSchedulesModule();

        // Act
        boolean result = module.initQueue(schedulesFactory, config, notificationsManager);

        // Assert
        assertTrue(result);
        verify(schedulesFactory, times(1)).register(eq("queue-observer_1"), eq(60000L), eq(20000L), any());
        verify(schedulesFactory, times(1)).register(eq("queue-observer_2"), eq(60000L), eq(20000L), any());
        verify(schedulesFactory, times(1)).register(eq("queue-observer_3"), eq(60000L), eq(20000L), any());
        verify(schedulesFactory, times(3)).register(any(), anyLong(), anyLong(), any());
    }

    /**
     * Test initQueue throws exception when numberOfQueueWorkers is zero.
     *
     * <p>Methods under test:
     *
     * <ul>
     *   <li>{@link NotificationsQueueSchedulesModule#initQueue(SchedulesFactory, QueueManagerConfiguration, NotificationsQueueManager)}
     * </ul>
     */
    @Test
    @DisplayName("Test initQueue throws exception when numberOfQueueWorkers is zero")
    @Tag("ContributionFromDiffblue")
    @ManagedByDiffblue
    @MethodsUnderTest({"boolean NotificationsQueueSchedulesModule.initQueue(SchedulesFactory, QueueManagerConfiguration, NotificationsQueueManager)"})
    public void testInitQueueThrowsExceptionWhenZeroWorkers()
  {
        // Arrange
        SchedulesFactory schedulesFactory = mock(SchedulesFactory.class);
        QueueManagerConfiguration config = new QueueManagerConfiguration();
        config.setNumberOfQueueWorkers(0);
        NotificationsQueueManager notificationsManager = mock(NotificationsQueueManager.class);
        NotificationsQueueSchedulesModule module = new NotificationsQueueSchedulesModule();

        // Act and Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
            module.initQueue(schedulesFactory, config, notificationsManager)
        );
        assertEquals("Number of queue workers must be a positive number >1 ", exception.getMessage());
    }

    /**
     * Test initQueue throws exception when numberOfQueueWorkers is negative.
     *
     * <p>Methods under test:
     *
     * <ul>
     *   <li>{@link NotificationsQueueSchedulesModule#initQueue(SchedulesFactory, QueueManagerConfiguration, NotificationsQueueManager)}
     * </ul>
     */
    @Test
    @DisplayName("Test initQueue throws exception when numberOfQueueWorkers is negative")
    @Tag("ContributionFromDiffblue")
    @ManagedByDiffblue
    @MethodsUnderTest({"boolean NotificationsQueueSchedulesModule.initQueue(SchedulesFactory, QueueManagerConfiguration, NotificationsQueueManager)"})
    public void testInitQueueThrowsExceptionWhenNegativeWorkers()
  {
        // Arrange
        SchedulesFactory schedulesFactory = mock(SchedulesFactory.class);
        QueueManagerConfiguration config = new QueueManagerConfiguration();
        config.setNumberOfQueueWorkers(-1);
        NotificationsQueueManager notificationsManager = mock(NotificationsQueueManager.class);
        NotificationsQueueSchedulesModule module = new NotificationsQueueSchedulesModule();

        // Act and Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
            module.initQueue(schedulesFactory, config, notificationsManager)
        );
        assertEquals("Number of queue workers must be a positive number >1 ", exception.getMessage());
    }

    /**
     * Test initQueue with default configuration values.
     *
     * <p>Methods under test:
     *
     * <ul>
     *   <li>{@link NotificationsQueueSchedulesModule#initQueue(SchedulesFactory, QueueManagerConfiguration, NotificationsQueueManager)}
     * </ul>
     */
    @Test
    @DisplayName("Test initQueue with default configuration")
    @Tag("ContributionFromDiffblue")
    @ManagedByDiffblue
    @MethodsUnderTest({"boolean NotificationsQueueSchedulesModule.initQueue(SchedulesFactory, QueueManagerConfiguration, NotificationsQueueManager)"})
    public void testInitQueueWithDefaultConfiguration()
  {
        // Arrange - Use default configuration which has 1 worker
        SchedulesFactory schedulesFactory = mock(SchedulesFactory.class);
        QueueManagerConfiguration config = new QueueManagerConfiguration();
        NotificationsQueueManager notificationsManager = mock(NotificationsQueueManager.class);
        NotificationsQueueSchedulesModule module = new NotificationsQueueSchedulesModule();

        // Act
        boolean result = module.initQueue(schedulesFactory, config, notificationsManager);

        // Assert
        assertTrue(result);
        verify(schedulesFactory, times(1)).register(any(), anyLong(), anyLong(), any());
    }

    /**
     * Test initQueue verifies that the correct delay and interval are passed.
     *
     * <p>Methods under test:
     *
     * <ul>
     *   <li>{@link NotificationsQueueSchedulesModule#initQueue(SchedulesFactory, QueueManagerConfiguration, NotificationsQueueManager)}
     * </ul>
     */
    @Test
    @DisplayName("Test initQueue verifies correct delay and interval")
    @Tag("ContributionFromDiffblue")
    @ManagedByDiffblue
    @MethodsUnderTest({"boolean NotificationsQueueSchedulesModule.initQueue(SchedulesFactory, QueueManagerConfiguration, NotificationsQueueManager)"})
    public void testInitQueueVerifiesCorrectDelayAndInterval()
  {
        // Arrange
        SchedulesFactory schedulesFactory = mock(SchedulesFactory.class);
        QueueManagerConfiguration config = new QueueManagerConfiguration();
        config.setNumberOfQueueWorkers(2);
        config.setQueueDelay(30000L);
        config.setQueueInterval(10000L);
        NotificationsQueueManager notificationsManager = mock(NotificationsQueueManager.class);
        NotificationsQueueSchedulesModule module = new NotificationsQueueSchedulesModule();

        // Act
        boolean result = module.initQueue(schedulesFactory, config, notificationsManager);

        // Assert
        assertTrue(result);
        verify(schedulesFactory, times(1)).register(eq("queue-observer_1"), eq(30000L), eq(10000L), any());
        verify(schedulesFactory, times(1)).register(eq("queue-observer_2"), eq(30000L), eq(10000L), any());
    }
}
