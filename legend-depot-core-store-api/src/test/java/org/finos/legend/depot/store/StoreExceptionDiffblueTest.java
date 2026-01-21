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

package org.finos.legend.depot.store;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import com.diffblue.cover.annotations.ManagedByDiffblue;
import com.diffblue.cover.annotations.MethodsUnderTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

class StoreExceptionDiffblueTest



{
  /**
   * Test {@link StoreException#StoreException(String)}.
   *
   * <p>Method under test: {@link StoreException#StoreException(String)}
   */
  @Test
  @DisplayName("Test new StoreException(String)")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({"void StoreException.<init>(String)"})
  void testNewStoreException()
  {
    // Arrange and Act
    StoreException actualStoreException = new StoreException("An error occurred");

    // Assert
    assertEquals("An error occurred", actualStoreException.getMessage());
    assertNull(actualStoreException.getCause());
    assertEquals(0, actualStoreException.getSuppressed().length);
  }
}
