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

package org.finos.legend.depot.store.model.entities;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import com.diffblue.cover.annotations.ManagedByDiffblue;
import com.diffblue.cover.annotations.MethodsUnderTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

class StoredEntityDiffblueTest 


{
  /**
   * Test {@link StoredEntity#getEntityAttributes()}.
   *
   * <p>Method under test: {@link StoredEntity#getEntityAttributes()}
   */
  @Test
  @DisplayName("Test getEntityAttributes()")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({"java.util.Map StoredEntity.getEntityAttributes()"})
  void testGetEntityAttributes()
  {
    // Arrange
    StoredEntityData storedEntityData = new StoredEntityData("42", "42", "42");

    // Act and Assert
    assertNull(storedEntityData.getEntityAttributes());
  }

  /**
   * Test {@link StoredEntity#getId()}.
   *
   * <p>Method under test: {@link StoredEntity#getId()}
   */
  @Test
  @DisplayName("Test getId()")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({"java.lang.String StoredEntity.getId()"})
  void testGetId()
  {
    // Arrange
    StoredEntityReference storedEntityReference = new StoredEntityReference("42", "42", "42");

    // Act and Assert
    assertEquals("", storedEntityReference.getId());
  }
}
