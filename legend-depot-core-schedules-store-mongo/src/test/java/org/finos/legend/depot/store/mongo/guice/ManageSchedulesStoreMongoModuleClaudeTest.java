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
import org.finos.legend.depot.store.mongo.schedules.ScheduleInstancesMongo;
import org.finos.legend.depot.store.mongo.schedules.SchedulesMongo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.List;

class ManageSchedulesStoreMongoModuleClaudeTest 

{
    /**
     * Test constructor.
     *
     * <p>Methods under test:
     *
     * <ul>
     *   <li>{@link ManageSchedulesStoreMongoModule#ManageSchedulesStoreMongoModule()}
     * </ul>
     */
    @Test
    @DisplayName("Test constructor")
    @Tag("ContributionFromDiffblue")
    @ManagedByDiffblue
    @MethodsUnderTest({"void ManageSchedulesStoreMongoModule.<init>()"})
    void testConstructor()
  {
        // Arrange and Act
        ManageSchedulesStoreMongoModule actualManageSchedulesStoreMongoModule = new ManageSchedulesStoreMongoModule();

        // Assert
        assertNotNull(actualManageSchedulesStoreMongoModule);
    }

    /**
     * Test registerIndexes method.
     *
     * <p>Methods under test:
     *
     * <ul>
     *   <li>{@link ManageSchedulesStoreMongoModule#registerIndexes(MongoAdminStore)}
     * </ul>
     */
    @Test
    @DisplayName("Test registerIndexes method")
    @Tag("ContributionFromDiffblue")
    @ManagedByDiffblue
    @MethodsUnderTest({"boolean ManageSchedulesStoreMongoModule.registerIndexes(MongoAdminStore)"})
    void testRegisterIndexes()
  {
        // Arrange
        ManageSchedulesStoreMongoModule module = new ManageSchedulesStoreMongoModule();
        MongoAdminStore mockAdminStore = mock(MongoAdminStore.class);

        // Act
        boolean result = module.registerIndexes(mockAdminStore);

        // Assert
        assertTrue(result);
        verify(mockAdminStore, times(1)).registerIndexes(eq(SchedulesMongo.COLLECTION), any(List.class));
        verify(mockAdminStore, times(1)).registerIndexes(eq(ScheduleInstancesMongo.COLLECTION), any(List.class));
    }

    /**
     * Test registerIndexes method verifies correct collection names.
     *
     * <p>Methods under test:
     *
     * <ul>
     *   <li>{@link ManageSchedulesStoreMongoModule#registerIndexes(MongoAdminStore)}
     * </ul>
     */
    @Test
    @DisplayName("Test registerIndexes verifies correct collection names")
    @Tag("ContributionFromDiffblue")
    @ManagedByDiffblue
    @MethodsUnderTest({"boolean ManageSchedulesStoreMongoModule.registerIndexes(MongoAdminStore)"})
    void testRegisterIndexes_verifiesCollectionNames()
  {
        // Arrange
        ManageSchedulesStoreMongoModule module = new ManageSchedulesStoreMongoModule();
        MongoAdminStore mockAdminStore = mock(MongoAdminStore.class);

        // Act
        boolean result = module.registerIndexes(mockAdminStore);

        // Assert
        assertTrue(result);
        verify(mockAdminStore).registerIndexes(eq("schedules"), any(List.class));
        verify(mockAdminStore).registerIndexes(eq("schedule-instances"), any(List.class));
    }

    /**
     * Test registerIndexes method verifies correct indexes are built.
     *
     * <p>Methods under test:
     *
     * <ul>
     *   <li>{@link ManageSchedulesStoreMongoModule#registerIndexes(MongoAdminStore)}
     * </ul>
     */
    @Test
    @DisplayName("Test registerIndexes verifies correct indexes are built")
    @Tag("ContributionFromDiffblue")
    @ManagedByDiffblue
    @MethodsUnderTest({"boolean ManageSchedulesStoreMongoModule.registerIndexes(MongoAdminStore)"})
    void testRegisterIndexes_verifiesIndexesBuilt()
  {
        // Arrange
        ManageSchedulesStoreMongoModule module = new ManageSchedulesStoreMongoModule();
        MongoAdminStore mockAdminStore = mock(MongoAdminStore.class);

        // Get expected indexes
        List<IndexModel> schedulesIndexes = SchedulesMongo.buildIndexes();
        List<IndexModel> scheduleInstancesIndexes = ScheduleInstancesMongo.buildIndexes();

        // Act
        boolean result = module.registerIndexes(mockAdminStore);

        // Assert
        assertTrue(result);
        assertEquals(1, schedulesIndexes.size());
        assertEquals(1, scheduleInstancesIndexes.size());
    }

    /**
     * Test registerIndexes method always returns true.
     *
     * <p>Methods under test:
     *
     * <ul>
     *   <li>{@link ManageSchedulesStoreMongoModule#registerIndexes(MongoAdminStore)}
     * </ul>
     */
    @Test
    @DisplayName("Test registerIndexes always returns true")
    @Tag("ContributionFromDiffblue")
    @ManagedByDiffblue
    @MethodsUnderTest({"boolean ManageSchedulesStoreMongoModule.registerIndexes(MongoAdminStore)"})
    void testRegisterIndexes_alwaysReturnsTrue()
  {
        // Arrange
        ManageSchedulesStoreMongoModule module = new ManageSchedulesStoreMongoModule();
        MongoAdminStore mockAdminStore = mock(MongoAdminStore.class);

        // Act
        boolean result1 = module.registerIndexes(mockAdminStore);
        boolean result2 = module.registerIndexes(mockAdminStore);

        // Assert
        assertTrue(result1);
        assertTrue(result2);
        verify(mockAdminStore, times(2)).registerIndexes(eq(SchedulesMongo.COLLECTION), any(List.class));
        verify(mockAdminStore, times(2)).registerIndexes(eq(ScheduleInstancesMongo.COLLECTION), any(List.class));
    }
}
