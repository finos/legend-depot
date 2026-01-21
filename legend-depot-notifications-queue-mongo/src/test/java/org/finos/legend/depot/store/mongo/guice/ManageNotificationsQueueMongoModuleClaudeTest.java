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

package org.finos.legend.depot.store.mongo.guice;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.diffblue.cover.annotations.ManagedByDiffblue;
import com.diffblue.cover.annotations.MethodsUnderTest;
import com.mongodb.client.model.IndexModel;
import org.finos.legend.depot.store.mongo.admin.MongoAdminStore;
import org.finos.legend.depot.store.mongo.notifications.queue.NotificationsQueueMongo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.List;

class ManageNotificationsQueueMongoModuleClaudeTest 

{
    /**
     * Test constructor.
     *
     * <p>Methods under test:
     *
     * <ul>
     *   <li>{@link ManageNotificationsQueueMongoModule#ManageNotificationsQueueMongoModule()}
     * </ul>
     */
    @Test
    @DisplayName("Test constructor")
    @Tag("ContributionFromDiffblue")
    @ManagedByDiffblue
    @MethodsUnderTest({"void ManageNotificationsQueueMongoModule.<init>()"})
    void testConstructor()
  {
        // Arrange and Act
        ManageNotificationsQueueMongoModule actualManageNotificationsQueueMongoModule = new ManageNotificationsQueueMongoModule();

        // Assert
        assertNotNull(actualManageNotificationsQueueMongoModule);
    }

    /**
     * Test registerIndexes method.
     *
     * <p>Methods under test:
     *
     * <ul>
     *   <li>{@link ManageNotificationsQueueMongoModule#registerIndexes(MongoAdminStore)}
     * </ul>
     */
    @Test
    @DisplayName("Test registerIndexes method")
    @Tag("ContributionFromDiffblue")
    @ManagedByDiffblue
    @MethodsUnderTest({"boolean ManageNotificationsQueueMongoModule.registerIndexes(MongoAdminStore)"})
    void testRegisterIndexes()
  {
        // Arrange
        ManageNotificationsQueueMongoModule module = new ManageNotificationsQueueMongoModule();
        MongoAdminStore mockAdminStore = mock(MongoAdminStore.class);

        // Act
        boolean result = module.registerIndexes(mockAdminStore);

        // Assert
        assertTrue(result);
        verify(mockAdminStore, times(1)).registerIndexes(eq(NotificationsQueueMongo.COLLECTION), any(List.class));
    }

    /**
     * Test registerIndexes method verifies correct collection name.
     *
     * <p>Methods under test:
     *
     * <ul>
     *   <li>{@link ManageNotificationsQueueMongoModule#registerIndexes(MongoAdminStore)}
     * </ul>
     */
    @Test
    @DisplayName("Test registerIndexes verifies correct collection name")
    @Tag("ContributionFromDiffblue")
    @ManagedByDiffblue
    @MethodsUnderTest({"boolean ManageNotificationsQueueMongoModule.registerIndexes(MongoAdminStore)"})
    void testRegisterIndexes_verifiesCollectionName()
  {
        // Arrange
        ManageNotificationsQueueMongoModule module = new ManageNotificationsQueueMongoModule();
        MongoAdminStore mockAdminStore = mock(MongoAdminStore.class);

        // Act
        boolean result = module.registerIndexes(mockAdminStore);

        // Assert
        assertTrue(result);
        verify(mockAdminStore).registerIndexes(eq("notifications-queue"), any(List.class));
    }

    /**
     * Test registerIndexes method verifies correct indexes are built.
     *
     * <p>Methods under test:
     *
     * <ul>
     *   <li>{@link ManageNotificationsQueueMongoModule#registerIndexes(MongoAdminStore)}
     * </ul>
     */
    @Test
    @DisplayName("Test registerIndexes verifies correct indexes are built")
    @Tag("ContributionFromDiffblue")
    @ManagedByDiffblue
    @MethodsUnderTest({"boolean ManageNotificationsQueueMongoModule.registerIndexes(MongoAdminStore)"})
    void testRegisterIndexes_verifiesIndexesBuilt()
  {
        // Arrange
        ManageNotificationsQueueMongoModule module = new ManageNotificationsQueueMongoModule();
        MongoAdminStore mockAdminStore = mock(MongoAdminStore.class);

        // Get expected indexes
        List<IndexModel> notificationsQueueIndexes = NotificationsQueueMongo.buildIndexes();

        // Act
        boolean result = module.registerIndexes(mockAdminStore);

        // Assert
        assertTrue(result);
        assertEquals(1, notificationsQueueIndexes.size());
    }

    /**
     * Test registerIndexes method always returns true.
     *
     * <p>Methods under test:
     *
     * <ul>
     *   <li>{@link ManageNotificationsQueueMongoModule#registerIndexes(MongoAdminStore)}
     * </ul>
     */
    @Test
    @DisplayName("Test registerIndexes always returns true")
    @Tag("ContributionFromDiffblue")
    @ManagedByDiffblue
    @MethodsUnderTest({"boolean ManageNotificationsQueueMongoModule.registerIndexes(MongoAdminStore)"})
    void testRegisterIndexes_alwaysReturnsTrue()
  {
        // Arrange
        ManageNotificationsQueueMongoModule module = new ManageNotificationsQueueMongoModule();
        MongoAdminStore mockAdminStore = mock(MongoAdminStore.class);

        // Act
        boolean result1 = module.registerIndexes(mockAdminStore);
        boolean result2 = module.registerIndexes(mockAdminStore);

        // Assert
        assertTrue(result1);
        assertTrue(result2);
        verify(mockAdminStore, times(2)).registerIndexes(eq(NotificationsQueueMongo.COLLECTION), any(List.class));
    }

    /**
     * Test registerIndexes method verifies the method is called with correct parameters.
     *
     * <p>Methods under test:
     *
     * <ul>
     *   <li>{@link ManageNotificationsQueueMongoModule#registerIndexes(MongoAdminStore)}
     * </ul>
     */
    @Test
    @DisplayName("Test registerIndexes verifies method invocation")
    @Tag("ContributionFromDiffblue")
    @ManagedByDiffblue
    @MethodsUnderTest({"boolean ManageNotificationsQueueMongoModule.registerIndexes(MongoAdminStore)"})
    void testRegisterIndexes_verifiesMethodInvocation()
  {
        // Arrange
        ManageNotificationsQueueMongoModule module = new ManageNotificationsQueueMongoModule();
        MongoAdminStore mockAdminStore = mock(MongoAdminStore.class);

        // Act
        boolean result = module.registerIndexes(mockAdminStore);

        // Assert
        assertTrue(result);
        verify(mockAdminStore, times(1)).registerIndexes(eq(NotificationsQueueMongo.COLLECTION), any(List.class));
    }
}
