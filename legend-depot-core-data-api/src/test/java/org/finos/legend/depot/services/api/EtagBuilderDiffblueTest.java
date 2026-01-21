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

package org.finos.legend.depot.services.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import com.diffblue.cover.annotations.ManagedByDiffblue;
import com.diffblue.cover.annotations.MethodsUnderTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

class EtagBuilderDiffblueTest 


{
  /**
   * Test {@link EtagBuilder#create()}.
   *
   * <p>Method under test: {@link EtagBuilder#create()}
   */
  @Test
  @DisplayName("Test create()")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({"EtagBuilder EtagBuilder.create()"})
  void testCreate()
  {
    // Arrange, Act and Assert
    String actualString = EtagBuilder.create().build();
    assertEquals("", actualString);
  }

  /**
   * Test {@link EtagBuilder#withGAV(String, String, String)}.
   *
   * <ul>
   *   <li>Given create.
   *   <li>When {@code 42}.
   *   <li>Then create build is {@code 424242}.
   * </ul>
   *
   * <p>Method under test: {@link EtagBuilder#withGAV(String, String, String)}
   */
  @Test
  @DisplayName(
      "Test withGAV(String, String, String); given create; when '42'; then create build is '424242'")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({"EtagBuilder EtagBuilder.withGAV(String, String, String)"})
  void testWithGAV_givenCreate_when42_thenCreateBuildIs424242()
  {
    // Arrange
    EtagBuilder createResult = EtagBuilder.create();

    // Act
    EtagBuilder actualWithGAVResult = createResult.withGAV("42", "42", "42");

    // Assert
    String actualString = createResult.build();
    assertEquals("424242", actualString);
    assertSame(createResult, actualWithGAVResult);
  }

  /**
   * Test {@link EtagBuilder#withGAV(String, String, String)}.
   *
   * <ul>
   *   <li>Given create.
   *   <li>When {@code -SNAPSHOT}.
   *   <li>Then create build is {@code null}.
   * </ul>
   *
   * <p>Method under test: {@link EtagBuilder#withGAV(String, String, String)}
   */
  @Test
  @DisplayName(
      "Test withGAV(String, String, String); given create; when '-SNAPSHOT'; then create build is 'null'")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({"EtagBuilder EtagBuilder.withGAV(String, String, String)"})
  void testWithGAV_givenCreate_whenSnapshot_thenCreateBuildIsNull()
  {
    // Arrange
    EtagBuilder createResult = EtagBuilder.create();

    // Act
    EtagBuilder actualWithGAVResult = createResult.withGAV("42", "42", "-SNAPSHOT");

    // Assert
    String actualString = createResult.build();
    assertNull(actualString);
    assertSame(createResult, actualWithGAVResult);
  }

  /**
   * Test {@link EtagBuilder#withProtocolVersion(String)}.
   *
   * <ul>
   *   <li>When {@code 1.0.2}.
   *   <li>Then create build is {@code 1.0.2}.
   * </ul>
   *
   * <p>Method under test: {@link EtagBuilder#withProtocolVersion(String)}
   */
  @Test
  @DisplayName("Test withProtocolVersion(String); when '1.0.2'; then create build is '1.0.2'")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({"EtagBuilder EtagBuilder.withProtocolVersion(String)"})
  void testWithProtocolVersion_when102_thenCreateBuildIs102()
  {
    // Arrange
    EtagBuilder createResult = EtagBuilder.create();

    // Act
    EtagBuilder actualWithProtocolVersionResult = createResult.withProtocolVersion("1.0.2");

    // Assert
    String actualString = createResult.build();
    assertEquals("1.0.2", actualString);
    assertSame(createResult, actualWithProtocolVersionResult);
  }

  /**
   * Test {@link EtagBuilder#withProtocolVersion(String)}.
   *
   * <ul>
   *   <li>When {@code null}.
   *   <li>Then create build is {@code null}.
   * </ul>
   *
   * <p>Method under test: {@link EtagBuilder#withProtocolVersion(String)}
   */
  @Test
  @DisplayName("Test withProtocolVersion(String); when 'null'; then create build is 'null'")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({"EtagBuilder EtagBuilder.withProtocolVersion(String)"})
  void testWithProtocolVersion_whenNull_thenCreateBuildIsNull()
  {
    // Arrange
    EtagBuilder createResult = EtagBuilder.create();

    // Act
    EtagBuilder actualWithProtocolVersionResult = createResult.withProtocolVersion(null);

    // Assert
    String actualString = createResult.build();
    assertNull(actualString);
    assertSame(createResult, actualWithProtocolVersionResult);
  }

  /**
   * Test {@link EtagBuilder#withProtocolVersion(String)}.
   *
   * <ul>
   *   <li>When {@code vX_X_X}.
   *   <li>Then create build is {@code null}.
   * </ul>
   *
   * <p>Method under test: {@link EtagBuilder#withProtocolVersion(String)}
   */
  @Test
  @DisplayName("Test withProtocolVersion(String); when 'vX_X_X'; then create build is 'null'")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({"EtagBuilder EtagBuilder.withProtocolVersion(String)"})
  void testWithProtocolVersion_whenVXXX_thenCreateBuildIsNull()
  {
    // Arrange
    EtagBuilder createResult = EtagBuilder.create();

    // Act
    EtagBuilder actualWithProtocolVersionResult = createResult.withProtocolVersion("vX_X_X");

    // Assert
    String actualString = createResult.build();
    assertNull(actualString);
    assertSame(createResult, actualWithProtocolVersionResult);
  }

  /**
   * Test {@link EtagBuilder#build()}.
   *
   * <ul>
   *   <li>Given create withProtocolVersion {@code vX_X_X}.
   *   <li>Then return {@code null}.
   * </ul>
   *
   * <p>Method under test: {@link EtagBuilder#build()}
   */
  @Test
  @DisplayName("Test build(); given create withProtocolVersion 'vX_X_X'; then return 'null'")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({"String EtagBuilder.build()"})
  void testBuild_givenCreateWithProtocolVersionVXXX_thenReturnNull()
  {
    // Arrange
    EtagBuilder createResult = EtagBuilder.create();
    createResult.withProtocolVersion("vX_X_X");

    // Act
    String actualString = createResult.build();

    // Assert
    assertNull(actualString);
  }

  /**
   * Test {@link EtagBuilder#build()}.
   *
   * <ul>
   *   <li>Given create.
   *   <li>Then return empty string.
   * </ul>
   *
   * <p>Method under test: {@link EtagBuilder#build()}
   */
  @Test
  @DisplayName("Test build(); given create; then return empty string")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({"String EtagBuilder.build()"})
  void testBuild_givenCreate_thenReturnEmptyString()
  {
    // Arrange and Act
    String actualString = EtagBuilder.create().build();

    // Assert
    assertEquals("", actualString);
  }

  /**
   * Test {@link EtagBuilder#build()}.
   *
   * <ul>
   *   <li>Then return {@code 424242}.
   * </ul>
   *
   * <p>Method under test: {@link EtagBuilder#build()}
   */
  @Test
  @DisplayName("Test build(); then return '424242'")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({"String EtagBuilder.build()"})
  void testBuild_thenReturn424242()
  {
    // Arrange
    EtagBuilder createResult = EtagBuilder.create();
    createResult.withGAV("42", "42", "42");

    // Act
    String actualString = createResult.build();

    // Assert
    assertEquals("424242", actualString);
  }
}
