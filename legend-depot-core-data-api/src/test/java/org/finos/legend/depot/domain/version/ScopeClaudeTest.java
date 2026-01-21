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

package org.finos.legend.depot.domain.version;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ScopeClaudeTest 

{
  /**
   * Test {@link Scope#values()}.
   *
   * <ul>
   *   <li>Then return array containing all enum values.
   * </ul>
   *
   * <p>Method under test: {@link Scope#values()}
   */
  @Test
  @DisplayName("Test values(); then return array containing all enum values")
  void testValues_thenReturnArrayContainingAllEnumValues()
  {
    // Act
    Scope[] actualValues = Scope.values();

    // Assert
    assertNotNull(actualValues);
    assertEquals(2, actualValues.length);
    assertArrayEquals(new Scope[] {Scope.RELEASES, Scope.SNAPSHOT}, actualValues);
  }

  /**
   * Test {@link Scope#values()}.
   *
   * <ul>
   *   <li>Then verify each element is correct.
   * </ul>
   *
   * <p>Method under test: {@link Scope#values()}
   */
  @Test
  @DisplayName("Test values(); then verify each element is correct")
  void testValues_thenVerifyEachElementIsCorrect()
  {
    // Act
    Scope[] actualValues = Scope.values();

    // Assert
    assertEquals(Scope.RELEASES, actualValues[0]);
    assertEquals(Scope.SNAPSHOT, actualValues[1]);
  }

  /**
   * Test {@link Scope#valueOf(String)}.
   *
   * <ul>
   *   <li>Given {@code "RELEASES"}.
   *   <li>Then return {@link Scope#RELEASES}.
   * </ul>
   *
   * <p>Method under test: {@link Scope#valueOf(String)}
   */
  @Test
  @DisplayName("Test valueOf(String); given 'RELEASES'; then return RELEASES")
  void testValueOf_givenRELEASES_thenReturnRELEASES()
  {
    // Act
    Scope actualScope = Scope.valueOf("RELEASES");

    // Assert
    assertEquals(Scope.RELEASES, actualScope);
  }

  /**
   * Test {@link Scope#valueOf(String)}.
   *
   * <ul>
   *   <li>Given {@code "SNAPSHOT"}.
   *   <li>Then return {@link Scope#SNAPSHOT}.
   * </ul>
   *
   * <p>Method under test: {@link Scope#valueOf(String)}
   */
  @Test
  @DisplayName("Test valueOf(String); given 'SNAPSHOT'; then return SNAPSHOT")
  void testValueOf_givenSNAPSHOT_thenReturnSNAPSHOT()
  {
    // Act
    Scope actualScope = Scope.valueOf("SNAPSHOT");

    // Assert
    assertEquals(Scope.SNAPSHOT, actualScope);
  }

  /**
   * Test {@link Scope#valueOf(String)}.
   *
   * <ul>
   *   <li>Given invalid name.
   *   <li>Then throw {@link IllegalArgumentException}.
   * </ul>
   *
   * <p>Method under test: {@link Scope#valueOf(String)}
   */
  @Test
  @DisplayName("Test valueOf(String); given invalid name; then throw IllegalArgumentException")
  void testValueOf_givenInvalidName_thenThrowIllegalArgumentException()
  {
    // Act & Assert
    assertThrows(IllegalArgumentException.class, () -> Scope.valueOf("INVALID"));
  }

  /**
   * Test {@link Scope#valueOf(String)}.
   *
   * <ul>
   *   <li>Given null.
   *   <li>Then throw {@link NullPointerException}.
   * </ul>
   *
   * <p>Method under test: {@link Scope#valueOf(String)}
   */
  @Test
  @DisplayName("Test valueOf(String); given null; then throw NullPointerException")
  void testValueOf_givenNull_thenThrowNullPointerException()
  {
    // Act & Assert
    assertThrows(NullPointerException.class, () -> Scope.valueOf(null));
  }

  /**
   * Test {@link Scope#valueOf(String)}.
   *
   * <ul>
   *   <li>Given lowercase name.
   *   <li>Then throw {@link IllegalArgumentException}.
   * </ul>
   *
   * <p>Method under test: {@link Scope#valueOf(String)}
   */
  @Test
  @DisplayName("Test valueOf(String); given lowercase name; then throw IllegalArgumentException")
  void testValueOf_givenLowercaseName_thenThrowIllegalArgumentException()
  {
    // Act & Assert
    assertThrows(IllegalArgumentException.class, () -> Scope.valueOf("releases"));
  }

  /**
   * Test {@link Scope#valueOf(String)}.
   *
   * <ul>
   *   <li>Given empty string.
   *   <li>Then throw {@link IllegalArgumentException}.
   * </ul>
   *
   * <p>Method under test: {@link Scope#valueOf(String)}
   */
  @Test
  @DisplayName("Test valueOf(String); given empty string; then throw IllegalArgumentException")
  void testValueOf_givenEmptyString_thenThrowIllegalArgumentException()
  {
    // Act & Assert
    assertThrows(IllegalArgumentException.class, () -> Scope.valueOf(""));
  }
}
