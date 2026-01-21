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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import com.diffblue.cover.annotations.ManagedByDiffblue;
import com.diffblue.cover.annotations.MethodsUnderTest;
import org.finos.legend.depot.domain.notifications.MetadataNotification;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

class VoidQueueDiffblueTest 


{
  /**
   * Test getters and setters.
   *
   * <p>Methods under test:
   *
   * <ul>
   *   <li>default or parameterless constructor of {@link VoidQueue}
   *   <li>{@link VoidQueue#size()}
   * </ul>
   */
  @Test
  @DisplayName("Test getters and setters")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({"void VoidQueue.<init>()", "long VoidQueue.size()"})
  void testGettersAndSetters()
  {
    // Arrange, Act and Assert
    assertEquals(0L, new VoidQueue().size());
  }

  /**
   * Test {@link VoidQueue#getAll()}.
   *
   * <p>Method under test: {@link VoidQueue#getAll()}
   */
  @Test
  @DisplayName("Test getAll()")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({"java.util.List VoidQueue.getAll()"})
  void testGetAll()
  {
    // Arrange, Act and Assert
    assertTrue(new VoidQueue().getAll().isEmpty());
  }

  /**
   * Test {@link VoidQueue#pullAll()}.
   *
   * <p>Method under test: {@link VoidQueue#pullAll()}
   */
  @Test
  @DisplayName("Test pullAll()")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({"java.util.List VoidQueue.pullAll()"})
  void testPullAll()
  {
    // Arrange, Act and Assert
    assertTrue(new VoidQueue().pullAll().isEmpty());
  }

  /**
   * Test {@link VoidQueue#getFirstInQueue()}.
   *
   * <p>Method under test: {@link VoidQueue#getFirstInQueue()}
   */
  @Test
  @DisplayName("Test getFirstInQueue()")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({"java.util.Optional VoidQueue.getFirstInQueue()"})
  void testGetFirstInQueue()
  {
    // Arrange, Act and Assert
    assertFalse(new VoidQueue().getFirstInQueue().isPresent());
  }

  /**
   * Test {@link VoidQueue#get(String)}.
   *
   * <p>Method under test: {@link VoidQueue#get(String)}
   */
  @Test
  @DisplayName("Test get(String)")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({"java.util.Optional VoidQueue.get(String)"})
  void testGet()
  {
    // Arrange, Act and Assert
    assertFalse(new VoidQueue().get("42").isPresent());
  }

  /**
   * Test {@link VoidQueue#push(MetadataNotification)}.
   *
   * <p>Method under test: {@link VoidQueue#push(MetadataNotification)}
   */
  @Test
  @DisplayName("Test push(MetadataNotification)")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({"String VoidQueue.push(MetadataNotification)"})
  void testPush()
  {
    // Arrange
    VoidQueue voidQueue = new VoidQueue();
    MetadataNotification metadataEvent = new MetadataNotification("myproject", "42", "42", "42");

    // Act
    String actualPushResult = voidQueue.push(metadataEvent);

    // Assert
    assertNull(actualPushResult);
  }

  /**
   * Test {@link VoidQueue#deleteAll()}.
   *
   * <p>Method under test: {@link VoidQueue#deleteAll()}
   */
  @Test
  @DisplayName("Test deleteAll()")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({"long VoidQueue.deleteAll()"})
  void testDeleteAll()
  {
    // Arrange, Act and Assert
    assertEquals(0L, new VoidQueue().deleteAll());
  }
}
