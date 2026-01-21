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

package org.finos.legend.depot.domain.notifications;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import com.diffblue.cover.annotations.ManagedByDiffblue;
import com.diffblue.cover.annotations.MethodsUnderTest;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

class MetadataNotificationResponseDiffblueTest 


{
  /**
   * Test {@link MetadataNotificationResponse#getStatus()}.
   *
   * <ul>
   *   <li>Given {@link MetadataNotificationResponse} (default constructor).
   *   <li>Then return {@code SUCCESS}.
   * </ul>
   *
   * <p>Method under test: {@link MetadataNotificationResponse#getStatus()}
   */
  @Test
  @DisplayName(
      "Test getStatus(); given MetadataNotificationResponse (default constructor); then return 'SUCCESS'")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({"MetadataNotificationStatus MetadataNotificationResponse.getStatus()"})
  void testGetStatus_givenMetadataNotificationResponse_thenReturnSuccess()
  {
    // Arrange, Act and Assert
    assertEquals(
        MetadataNotificationStatus.SUCCESS, new MetadataNotificationResponse().getStatus());
  }

  /**
   * Test {@link MetadataNotificationResponse#getStatus()}.
   *
   * <ul>
   *   <li>Then return {@code FAILED}.
   * </ul>
   *
   * <p>Method under test: {@link MetadataNotificationResponse#getStatus()}
   */
  @Test
  @DisplayName("Test getStatus(); then return 'FAILED'")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({"MetadataNotificationStatus MetadataNotificationResponse.getStatus()"})
  void testGetStatus_thenReturnFailed()
  {
    // Arrange
    MetadataNotificationResponse metadataNotificationResponse = new MetadataNotificationResponse();
    metadataNotificationResponse.addError("Not all who wander are lost");

    // Act and Assert
    assertEquals(MetadataNotificationStatus.FAILED, metadataNotificationResponse.getStatus());
  }

  /**
   * Test {@link MetadataNotificationResponse#addError(String)}.
   *
   * <p>Method under test: {@link MetadataNotificationResponse#addError(String)}
   */
  @Test
  @DisplayName("Test addError(String)")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({"MetadataNotificationResponse MetadataNotificationResponse.addError(String)"})
  void testAddError()
  {
    // Arrange
    MetadataNotificationResponse metadataNotificationResponse = new MetadataNotificationResponse();

    // Act
    MetadataNotificationResponse actualAddErrorResult =
        metadataNotificationResponse.addError("Not all who wander are lost");

    // Assert
    assertEquals(MetadataNotificationStatus.FAILED, metadataNotificationResponse.getStatus());
    assertTrue(metadataNotificationResponse.hasErrors());
    assertSame(metadataNotificationResponse, actualAddErrorResult);
  }

  /**
   * Test {@link MetadataNotificationResponse#addMessage(String)}.
   *
   * <p>Method under test: {@link MetadataNotificationResponse#addMessage(String)}
   */
  @Test
  @DisplayName("Test addMessage(String)")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({
    "MetadataNotificationResponse MetadataNotificationResponse.addMessage(String)"
  })
  void testAddMessage()
  {
    // Arrange
    MetadataNotificationResponse metadataNotificationResponse = new MetadataNotificationResponse();

    // Act
    MetadataNotificationResponse actualAddMessageResult =
        metadataNotificationResponse.addMessage("Not all who wander are lost");

    // Assert
    assertSame(metadataNotificationResponse, actualAddMessageResult);
  }

  /**
   * Test {@link MetadataNotificationResponse#addMessages(List)}.
   *
   * <ul>
   *   <li>Given {@code 42}.
   *   <li>When {@link ArrayList#ArrayList()} add {@code 42}.
   * </ul>
   *
   * <p>Method under test: {@link MetadataNotificationResponse#addMessages(List)}
   */
  @Test
  @DisplayName("Test addMessages(List); given '42'; when ArrayList() add '42'")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({"MetadataNotificationResponse MetadataNotificationResponse.addMessages(List)"})
  void testAddMessages_given42_whenArrayListAdd42()
  {
    // Arrange
    MetadataNotificationResponse metadataNotificationResponse = new MetadataNotificationResponse();

    ArrayList<String> messages = new ArrayList<>();
    messages.add("42");
    messages.add("foo");

    // Act
    MetadataNotificationResponse actualAddMessagesResult =
        metadataNotificationResponse.addMessages(messages);

    // Assert
    assertSame(metadataNotificationResponse, actualAddMessagesResult);
  }

  /**
   * Test {@link MetadataNotificationResponse#addMessages(List)}.
   *
   * <ul>
   *   <li>Given {@code foo}.
   *   <li>When {@link ArrayList#ArrayList()} add {@code foo}.
   * </ul>
   *
   * <p>Method under test: {@link MetadataNotificationResponse#addMessages(List)}
   */
  @Test
  @DisplayName("Test addMessages(List); given 'foo'; when ArrayList() add 'foo'")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({"MetadataNotificationResponse MetadataNotificationResponse.addMessages(List)"})
  void testAddMessages_givenFoo_whenArrayListAddFoo()
  {
    // Arrange
    MetadataNotificationResponse metadataNotificationResponse = new MetadataNotificationResponse();

    ArrayList<String> messages = new ArrayList<>();
    messages.add("foo");

    // Act
    MetadataNotificationResponse actualAddMessagesResult =
        metadataNotificationResponse.addMessages(messages);

    // Assert
    assertSame(metadataNotificationResponse, actualAddMessagesResult);
  }

  /**
   * Test {@link MetadataNotificationResponse#addMessages(List)}.
   *
   * <ul>
   *   <li>When {@link ArrayList#ArrayList()}.
   * </ul>
   *
   * <p>Method under test: {@link MetadataNotificationResponse#addMessages(List)}
   */
  @Test
  @DisplayName("Test addMessages(List); when ArrayList()")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({"MetadataNotificationResponse MetadataNotificationResponse.addMessages(List)"})
  void testAddMessages_whenArrayList()
  {
    // Arrange
    MetadataNotificationResponse metadataNotificationResponse = new MetadataNotificationResponse();

    // Act
    MetadataNotificationResponse actualAddMessagesResult =
        metadataNotificationResponse.addMessages(new ArrayList<>());

    // Assert
    assertSame(metadataNotificationResponse, actualAddMessagesResult);
  }

  /**
   * Test {@link MetadataNotificationResponse#logError(String)}.
   *
   * <p>Method under test: {@link MetadataNotificationResponse#logError(String)}
   */
  @Test
  @DisplayName("Test logError(String)")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({"void MetadataNotificationResponse.logError(String)"})
  void testLogError()
  {
    // Arrange
    MetadataNotificationResponse metadataNotificationResponse = new MetadataNotificationResponse();

    // Act
    metadataNotificationResponse.logError("An error occurred");

    // Assert
    List<String> errors = metadataNotificationResponse.getErrors();
    assertEquals(1, errors.size());
    assertEquals("An error occurred", errors.get(0));
    assertEquals(MetadataNotificationStatus.FAILED, metadataNotificationResponse.getStatus());
    assertTrue(metadataNotificationResponse.hasErrors());
  }

  /**
   * Test {@link MetadataNotificationResponse#hasErrors()}.
   *
   * <ul>
   *   <li>Given {@link MetadataNotificationResponse} (default constructor).
   *   <li>Then return {@code false}.
   * </ul>
   *
   * <p>Method under test: {@link MetadataNotificationResponse#hasErrors()}
   */
  @Test
  @DisplayName(
      "Test hasErrors(); given MetadataNotificationResponse (default constructor); then return 'false'")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({"boolean MetadataNotificationResponse.hasErrors()"})
  void testHasErrors_givenMetadataNotificationResponse_thenReturnFalse()
  {
    // Arrange, Act and Assert
    assertFalse(new MetadataNotificationResponse().hasErrors());
  }

  /**
   * Test {@link MetadataNotificationResponse#hasErrors()}.
   *
   * <ul>
   *   <li>Then return {@code true}.
   * </ul>
   *
   * <p>Method under test: {@link MetadataNotificationResponse#hasErrors()}
   */
  @Test
  @DisplayName("Test hasErrors(); then return 'true'")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({"boolean MetadataNotificationResponse.hasErrors()"})
  void testHasErrors_thenReturnTrue()
  {
    // Arrange
    MetadataNotificationResponse metadataNotificationResponse = new MetadataNotificationResponse();
    metadataNotificationResponse.addError("Not all who wander are lost");

    // Act and Assert
    assertTrue(metadataNotificationResponse.hasErrors());
  }

  /**
   * Test {@link MetadataNotificationResponse#combine(MetadataNotificationResponse)}.
   *
   * <ul>
   *   <li>When {@link MetadataNotificationResponse} (default constructor).
   * </ul>
   *
   * <p>Method under test: {@link
   * MetadataNotificationResponse#combine(MetadataNotificationResponse)}
   */
  @Test
  @DisplayName(
      "Test combine(MetadataNotificationResponse); when MetadataNotificationResponse (default constructor)")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({
    "MetadataNotificationResponse MetadataNotificationResponse.combine(MetadataNotificationResponse)"
  })
  void testCombine_whenMetadataNotificationResponse()
  {
    // Arrange
    MetadataNotificationResponse metadataNotificationResponse = new MetadataNotificationResponse();

    // Act
    MetadataNotificationResponse actualCombineResult =
        metadataNotificationResponse.combine(new MetadataNotificationResponse());

    // Assert
    assertSame(metadataNotificationResponse, actualCombineResult);
  }

  /**
   * Test {@link MetadataNotificationResponse#combine(MetadataNotificationResponse)}.
   *
   * <ul>
   *   <li>When {@code null}.
   * </ul>
   *
   * <p>Method under test: {@link
   * MetadataNotificationResponse#combine(MetadataNotificationResponse)}
   */
  @Test
  @DisplayName("Test combine(MetadataNotificationResponse); when 'null'")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({
    "MetadataNotificationResponse MetadataNotificationResponse.combine(MetadataNotificationResponse)"
  })
  void testCombine_whenNull()
  {
    // Arrange
    MetadataNotificationResponse metadataNotificationResponse = new MetadataNotificationResponse();

    // Act
    MetadataNotificationResponse actualCombineResult = metadataNotificationResponse.combine(null);

    // Assert
    assertSame(metadataNotificationResponse, actualCombineResult);
  }

  /**
   * Test getters and setters.
   *
   * <p>Methods under test:
   *
   * <ul>
   *   <li>default or parameterless constructor of {@link MetadataNotificationResponse}
   *   <li>{@link MetadataNotificationResponse#toString()}
   *   <li>{@link MetadataNotificationResponse#getErrors()}
   *   <li>{@link MetadataNotificationResponse#getMessages()}
   * </ul>
   */
  @Test
  @DisplayName("Test getters and setters")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({
    "void MetadataNotificationResponse.<init>()",
    "List MetadataNotificationResponse.getErrors()",
    "List MetadataNotificationResponse.getMessages()",
    "String MetadataNotificationResponse.toString()"
  })
  void testGettersAndSetters()
  {
    // Arrange and Act
    MetadataNotificationResponse actualMetadataNotificationResponse =
        new MetadataNotificationResponse();
    String actualToStringResult = actualMetadataNotificationResponse.toString();
    List<String> actualErrors = actualMetadataNotificationResponse.getErrors();
    List<String> actualMessages = actualMetadataNotificationResponse.getMessages();

    // Assert
    assertEquals("MetadataEventResponse{messages=[], errors=[]}", actualToStringResult);
    assertTrue(actualErrors.isEmpty());
    assertTrue(actualMessages.isEmpty());
  }
}
