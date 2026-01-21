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

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import com.diffblue.cover.annotations.ManagedByDiffblue;
import com.diffblue.cover.annotations.MethodsUnderTest;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.DefaultDeserializationContext;
import com.fasterxml.jackson.databind.introspect.VisibilityChecker;
import com.fasterxml.jackson.databind.introspect.VisibilityChecker.Std;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.fasterxml.jackson.databind.jsontype.impl.StdSubtypeResolver;
import com.fasterxml.jackson.databind.ser.BeanSerializerFactory;
import com.fasterxml.jackson.databind.ser.DefaultSerializerProvider;
import com.fasterxml.jackson.databind.ser.DefaultSerializerProvider.Impl;
import com.fasterxml.jackson.databind.util.StdDateFormat;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

class StorageConfigurationDiffblueTest



{
  /**
   * Test {@link StorageConfiguration#configureObjectMapper(ObjectMapper)}.
   *
   * <p>Method under test: {@link StorageConfiguration#configureObjectMapper(ObjectMapper)}
   */
  @Test
  @DisplayName("Test configureObjectMapper(ObjectMapper)")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({"ObjectMapper StorageConfiguration.configureObjectMapper(ObjectMapper)"})
  void testConfigureObjectMapper()
  {
    // Arrange
    JsonMapper objectMapper = JsonMapper.builder().findAndAddModules().build();

    // Act
    ObjectMapper actualConfigureObjectMapperResult =
        StorageConfiguration.configureObjectMapper(objectMapper);

    // Assert
    assertTrue(
        actualConfigureObjectMapperResult.getDeserializationContext()
            instanceof DefaultDeserializationContext.Impl);
    assertTrue(actualConfigureObjectMapperResult.getVisibilityChecker() instanceof Std);
    assertTrue(actualConfigureObjectMapperResult instanceof JsonMapper);
    assertTrue(
        actualConfigureObjectMapperResult.getPolymorphicTypeValidator()
            instanceof LaissezFaireSubTypeValidator);
    assertTrue(
        actualConfigureObjectMapperResult.getSubtypeResolver() instanceof StdSubtypeResolver);
    assertTrue(
        actualConfigureObjectMapperResult.getSerializerFactory() instanceof BeanSerializerFactory);
    assertTrue(actualConfigureObjectMapperResult.getSerializerProvider() instanceof Impl);
    assertTrue(actualConfigureObjectMapperResult.getSerializerProviderInstance() instanceof Impl);
    assertTrue(actualConfigureObjectMapperResult.getDateFormat() instanceof StdDateFormat);
    assertNull(actualConfigureObjectMapperResult.getInjectableValues());
    assertNull(actualConfigureObjectMapperResult.getPropertyNamingStrategy());
    assertTrue(actualConfigureObjectMapperResult.getRegisteredModuleIds().isEmpty());
  }
}
