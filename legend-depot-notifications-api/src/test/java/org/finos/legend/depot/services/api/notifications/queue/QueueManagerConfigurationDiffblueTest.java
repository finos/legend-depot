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
import com.diffblue.cover.annotations.ManagedByDiffblue;
import com.diffblue.cover.annotations.MethodsUnderTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

class QueueManagerConfigurationDiffblueTest 


{
  /**
   * Test getters and setters.
   *
   * <p>Methods under test:
   *
   * <ul>
   *   <li>default or parameterless constructor of {@link QueueManagerConfiguration}
   *   <li>{@link QueueManagerConfiguration#setNumberOfQueueWorkers(long)}
   *   <li>{@link QueueManagerConfiguration#setQueueDelay(long)}
   *   <li>{@link QueueManagerConfiguration#setQueueInterval(long)}
   *   <li>{@link QueueManagerConfiguration#getNumberOfQueueWorkers()}
   *   <li>{@link QueueManagerConfiguration#getQueueDelay()}
   *   <li>{@link QueueManagerConfiguration#getQueueInterval()}
   * </ul>
   */
  @Test
  @DisplayName("Test getters and setters")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({
    "void QueueManagerConfiguration.<init>()",
    "long QueueManagerConfiguration.getNumberOfQueueWorkers()",
    "long QueueManagerConfiguration.getQueueDelay()",
    "long QueueManagerConfiguration.getQueueInterval()",
    "void QueueManagerConfiguration.setNumberOfQueueWorkers(long)",
    "void QueueManagerConfiguration.setQueueDelay(long)",
    "void QueueManagerConfiguration.setQueueInterval(long)"
  })
  void testGettersAndSetters()
  {
    // Arrange and Act
    QueueManagerConfiguration actualQueueManagerConfiguration = new QueueManagerConfiguration();
    actualQueueManagerConfiguration.setNumberOfQueueWorkers(1L);
    actualQueueManagerConfiguration.setQueueDelay(1L);
    actualQueueManagerConfiguration.setQueueInterval(42L);
    long actualNumberOfQueueWorkers = actualQueueManagerConfiguration.getNumberOfQueueWorkers();
    long actualQueueDelay = actualQueueManagerConfiguration.getQueueDelay();

    // Assert
    assertEquals(1L, actualNumberOfQueueWorkers);
    assertEquals(1L, actualQueueDelay);
    assertEquals(42L, actualQueueManagerConfiguration.getQueueInterval());
  }
}
