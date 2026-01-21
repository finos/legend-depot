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

package org.finos.legend.depot.domain.project;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PropertyClaudeTest 

{

    @Test
    @DisplayName("Test default constructor creates property with null fields")
    void testDefaultConstructor()
  {
        // Arrange and Act
        Property property = new Property();

        // Assert
        assertNull(property.getPropertyName());
        assertNull(property.getValue());
    }

    @Test
    @DisplayName("Test parameterized constructor with valid values")
    void testParameterizedConstructor()
  {
        // Arrange and Act
        Property property = new Property("testProperty", "testValue");

        // Assert
        assertEquals("testProperty", property.getPropertyName());
        assertEquals("testValue", property.getValue());
    }

    @Test
    @DisplayName("Test parameterized constructor with null propertyName")
    void testParameterizedConstructorWithNullPropertyName()
  {
        // Arrange and Act
        Property property = new Property(null, "value");

        // Assert
        assertNull(property.getPropertyName());
        assertEquals("value", property.getValue());
    }

    @Test
    @DisplayName("Test parameterized constructor with null value")
    void testParameterizedConstructorWithNullValue()
  {
        // Arrange and Act
        Property property = new Property("name", null);

        // Assert
        assertEquals("name", property.getPropertyName());
        assertNull(property.getValue());
    }

    @Test
    @DisplayName("Test parameterized constructor with both null values")
    void testParameterizedConstructorWithBothNull()
  {
        // Arrange and Act
        Property property = new Property(null, null);

        // Assert
        assertNull(property.getPropertyName());
        assertNull(property.getValue());
    }

    @Test
    @DisplayName("Test parameterized constructor with empty strings")
    void testParameterizedConstructorWithEmptyStrings()
  {
        // Arrange and Act
        Property property = new Property("", "");

        // Assert
        assertEquals("", property.getPropertyName());
        assertEquals("", property.getValue());
    }

    @Test
    @DisplayName("Test getPropertyName returns correct value")
    void testGetPropertyName()
  {
        // Arrange
        Property property = new Property("myProperty", "myValue");

        // Act
        String propertyName = property.getPropertyName();

        // Assert
        assertEquals("myProperty", propertyName);
    }

    @Test
    @DisplayName("Test getValue returns correct value")
    void testGetValue()
  {
        // Arrange
        Property property = new Property("myProperty", "myValue");

        // Act
        String value = property.getValue();

        // Assert
        assertEquals("myValue", value);
    }

    @Test
    @DisplayName("Test equals returns true for same object")
    void testEqualsSameObject()
  {
        // Arrange
        Property property = new Property("name", "value");

        // Act and Assert
        assertEquals(property, property);
    }

    @Test
    @DisplayName("Test equals returns true for equal properties")
    void testEqualsEqualProperties()
  {
        // Arrange
        Property property1 = new Property("name", "value");
        Property property2 = new Property("name", "value");

        // Act and Assert
        assertEquals(property1, property2);
        assertEquals(property2, property1); // Test symmetry
    }

    @Test
    @DisplayName("Test equals returns false for different propertyName")
    void testEqualsDifferentPropertyName()
  {
        // Arrange
        Property property1 = new Property("name1", "value");
        Property property2 = new Property("name2", "value");

        // Act and Assert
        assertNotEquals(property1, property2);
    }

    @Test
    @DisplayName("Test equals returns false for different value")
    void testEqualsDifferentValue()
  {
        // Arrange
        Property property1 = new Property("name", "value1");
        Property property2 = new Property("name", "value2");

        // Act and Assert
        assertNotEquals(property1, property2);
    }

    @Test
    @DisplayName("Test equals returns false when compared to null")
    void testEqualsNull()
  {
        // Arrange
        Property property = new Property("name", "value");

        // Act and Assert
        assertNotEquals(property, null);
    }

    @Test
    @DisplayName("Test equals returns false for different type")
    void testEqualsDifferentType()
  {
        // Arrange
        Property property = new Property("name", "value");
        String notAProperty = "not a property";

        // Act and Assert
        assertNotEquals(property, notAProperty);
    }

    @Test
    @DisplayName("Test equals with both null propertyNames")
    void testEqualsWithBothNullPropertyNames()
  {
        // Arrange
        Property property1 = new Property(null, "value");
        Property property2 = new Property(null, "value");

        // Act and Assert
        assertEquals(property1, property2);
    }

    @Test
    @DisplayName("Test equals with both null values")
    void testEqualsWithBothNullValues()
  {
        // Arrange
        Property property1 = new Property("name", null);
        Property property2 = new Property("name", null);

        // Act and Assert
        assertEquals(property1, property2);
    }

    @Test
    @DisplayName("Test equals with all null fields")
    void testEqualsWithAllNullFields()
  {
        // Arrange
        Property property1 = new Property(null, null);
        Property property2 = new Property(null, null);

        // Act and Assert
        assertEquals(property1, property2);
    }

    @Test
    @DisplayName("Test equals with one null propertyName")
    void testEqualsWithOneNullPropertyName()
  {
        // Arrange
        Property property1 = new Property(null, "value");
        Property property2 = new Property("name", "value");

        // Act and Assert
        assertNotEquals(property1, property2);
    }

    @Test
    @DisplayName("Test equals with one null value")
    void testEqualsWithOneNullValue()
  {
        // Arrange
        Property property1 = new Property("name", null);
        Property property2 = new Property("name", "value");

        // Act and Assert
        assertNotEquals(property1, property2);
    }

    @Test
    @DisplayName("Test equals with empty strings")
    void testEqualsWithEmptyStrings()
  {
        // Arrange
        Property property1 = new Property("", "");
        Property property2 = new Property("", "");

        // Act and Assert
        assertEquals(property1, property2);
    }

    @Test
    @DisplayName("Test equals distinguishes empty string from null")
    void testEqualsDistinguishesEmptyFromNull()
  {
        // Arrange
        Property property1 = new Property("", "value");
        Property property2 = new Property(null, "value");

        // Act and Assert
        assertNotEquals(property1, property2);
    }

    @Test
    @DisplayName("Test hashCode returns same value for equal objects")
    void testHashCodeConsistency()
  {
        // Arrange
        Property property1 = new Property("name", "value");
        Property property2 = new Property("name", "value");

        // Act
        int hash1 = property1.hashCode();
        int hash2 = property2.hashCode();

        // Assert
        assertEquals(hash1, hash2);
    }

    @Test
    @DisplayName("Test hashCode is consistent across multiple calls")
    void testHashCodeConsistentAcrossCalls()
  {
        // Arrange
        Property property = new Property("name", "value");

        // Act
        int hash1 = property.hashCode();
        int hash2 = property.hashCode();
        int hash3 = property.hashCode();

        // Assert
        assertEquals(hash1, hash2);
        assertEquals(hash2, hash3);
    }

    @Test
    @DisplayName("Test hashCode with null fields")
    void testHashCodeWithNullFields()
  {
        // Arrange
        Property property = new Property(null, null);

        // Act
        int hash = property.hashCode();

        // Assert
        assertNotNull(hash); // hashCode should not throw exception
    }

    @Test
    @DisplayName("Test hashCode with empty strings")
    void testHashCodeWithEmptyStrings()
  {
        // Arrange
        Property property = new Property("", "");

        // Act
        int hash = property.hashCode();

        // Assert
        assertNotNull(hash);
    }

    @Test
    @DisplayName("Test hashCode differs for different objects")
    void testHashCodeDifferentObjects()
  {
        // Arrange
        Property property1 = new Property("name1", "value1");
        Property property2 = new Property("name2", "value2");

        // Act
        int hash1 = property1.hashCode();
        int hash2 = property2.hashCode();

        // Assert
        // Note: hashCode can collide, but for different values it's unlikely
        // This tests the general case, not a guarantee
        assertNotEquals(hash1, hash2);
    }

    @Test
    @DisplayName("Test equals and hashCode contract - equal objects have same hashCode")
    void testEqualsHashCodeContract()
  {
        // Arrange
        Property property1 = new Property("name", "value");
        Property property2 = new Property("name", "value");

        // Act and Assert
        assertEquals(property1, property2);
        assertEquals(property1.hashCode(), property2.hashCode());
    }

    @Test
    @DisplayName("Test equals transitivity")
    void testEqualsTransitivity()
  {
        // Arrange
        Property property1 = new Property("name", "value");
        Property property2 = new Property("name", "value");
        Property property3 = new Property("name", "value");

        // Act and Assert
        assertEquals(property1, property2);
        assertEquals(property2, property3);
        assertEquals(property1, property3); // Transitivity
    }

    @Test
    @DisplayName("Test getPropertyName with special characters")
    void testGetPropertyNameWithSpecialCharacters()
  {
        // Arrange
        Property property = new Property("special!@#$%^&*()", "value");

        // Act
        String propertyName = property.getPropertyName();

        // Assert
        assertEquals("special!@#$%^&*()", propertyName);
    }

    @Test
    @DisplayName("Test getValue with special characters")
    void testGetValueWithSpecialCharacters()
  {
        // Arrange
        Property property = new Property("name", "special!@#$%^&*()");

        // Act
        String value = property.getValue();

        // Assert
        assertEquals("special!@#$%^&*()", value);
    }

    @Test
    @DisplayName("Test getPropertyName with unicode characters")
    void testGetPropertyNameWithUnicode()
  {
        // Arrange
        Property property = new Property("名前", "value");

        // Act
        String propertyName = property.getPropertyName();

        // Assert
        assertEquals("名前", propertyName);
    }

    @Test
    @DisplayName("Test getValue with unicode characters")
    void testGetValueWithUnicode()
  {
        // Arrange
        Property property = new Property("name", "値");

        // Act
        String value = property.getValue();

        // Assert
        assertEquals("値", value);
    }

    @Test
    @DisplayName("Test with very long strings")
    void testWithVeryLongStrings()
  {
        // Arrange
        String longString = "a".repeat(10000);
        Property property = new Property(longString, longString);

        // Act
        String propertyName = property.getPropertyName();
        String value = property.getValue();

        // Assert
        assertEquals(longString, propertyName);
        assertEquals(longString, value);
    }
}
