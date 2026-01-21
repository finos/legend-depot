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

package org.finos.legend.depot.domain.artifacts.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ArtifactTypeClaudeTest
{
    @Test
    public void testValuesReturnsAllEnumConstants()
    {
        ArtifactType[] values = ArtifactType.values();

        Assertions.assertNotNull(values);
        Assertions.assertEquals(3, values.length);
        Assertions.assertEquals(ArtifactType.ENTITIES, values[0]);
        Assertions.assertEquals(ArtifactType.VERSIONED_ENTITIES, values[1]);
        Assertions.assertEquals(ArtifactType.FILE_GENERATIONS, values[2]);
    }

    @Test
    public void testValuesReturnsNewArrayEachCall()
    {
        ArtifactType[] values1 = ArtifactType.values();
        ArtifactType[] values2 = ArtifactType.values();

        Assertions.assertNotSame(values1, values2);
        Assertions.assertArrayEquals(values1, values2);
    }

    @Test
    public void testValuesArrayCanBeModifiedWithoutAffectingOriginal()
    {
        ArtifactType[] values = ArtifactType.values();
        int originalLength = values.length;
        values[0] = ArtifactType.FILE_GENERATIONS;

        ArtifactType[] newValues = ArtifactType.values();
        Assertions.assertEquals(originalLength, newValues.length);
        Assertions.assertEquals(ArtifactType.ENTITIES, newValues[0]);
    }

    @Test
    public void testValueOfEntities()
    {
        ArtifactType type = ArtifactType.valueOf("ENTITIES");

        Assertions.assertNotNull(type);
        Assertions.assertEquals(ArtifactType.ENTITIES, type);
        Assertions.assertSame(ArtifactType.ENTITIES, type);
    }

    @Test
    public void testValueOfVersionedEntities()
    {
        ArtifactType type = ArtifactType.valueOf("VERSIONED_ENTITIES");

        Assertions.assertNotNull(type);
        Assertions.assertEquals(ArtifactType.VERSIONED_ENTITIES, type);
        Assertions.assertSame(ArtifactType.VERSIONED_ENTITIES, type);
    }

    @Test
    public void testValueOfFileGenerations()
    {
        ArtifactType type = ArtifactType.valueOf("FILE_GENERATIONS");

        Assertions.assertNotNull(type);
        Assertions.assertEquals(ArtifactType.FILE_GENERATIONS, type);
        Assertions.assertSame(ArtifactType.FILE_GENERATIONS, type);
    }

    @Test
    public void testValueOfWithInvalidNameThrowsException()
    {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            ArtifactType.valueOf("INVALID_TYPE");
        });
    }

    @Test
    public void testValueOfWithNullThrowsException()
    {
        Assertions.assertThrows(NullPointerException.class, () -> {
            ArtifactType.valueOf(null);
        });
    }

    @Test
    public void testValueOfWithEmptyStringThrowsException()
    {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            ArtifactType.valueOf("");
        });
    }

    @Test
    public void testValueOfIsCaseSensitive()
    {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            ArtifactType.valueOf("entities");
        });

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            ArtifactType.valueOf("Entities");
        });
    }

    @Test
    public void testValueOfWithWhitespaceThrowsException()
    {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            ArtifactType.valueOf(" ENTITIES ");
        });
    }

    @Test
    public void testValueOfReturnsSameInstanceMultipleTimes()
    {
        ArtifactType type1 = ArtifactType.valueOf("ENTITIES");
        ArtifactType type2 = ArtifactType.valueOf("ENTITIES");

        Assertions.assertSame(type1, type2);
    }

    @Test
    public void testGetModuleNameForEntities()
    {
        String moduleName = ArtifactType.ENTITIES.getModuleName();

        Assertions.assertNotNull(moduleName);
        Assertions.assertEquals("entities", moduleName);
    }

    @Test
    public void testGetModuleNameForVersionedEntities()
    {
        String moduleName = ArtifactType.VERSIONED_ENTITIES.getModuleName();

        Assertions.assertNotNull(moduleName);
        Assertions.assertEquals("versioned-entities", moduleName);
    }

    @Test
    public void testGetModuleNameForFileGenerations()
    {
        String moduleName = ArtifactType.FILE_GENERATIONS.getModuleName();

        Assertions.assertNotNull(moduleName);
        Assertions.assertEquals("file-generation", moduleName);
    }

    @Test
    public void testGetModuleNameReturnsConsistentValue()
    {
        String moduleName1 = ArtifactType.ENTITIES.getModuleName();
        String moduleName2 = ArtifactType.ENTITIES.getModuleName();

        Assertions.assertEquals(moduleName1, moduleName2);
    }

    @Test
    public void testGetModuleNameReturnsDifferentValuesForDifferentTypes()
    {
        String entitiesModule = ArtifactType.ENTITIES.getModuleName();
        String versionedEntitiesModule = ArtifactType.VERSIONED_ENTITIES.getModuleName();
        String fileGenerationsModule = ArtifactType.FILE_GENERATIONS.getModuleName();

        Assertions.assertNotEquals(entitiesModule, versionedEntitiesModule);
        Assertions.assertNotEquals(entitiesModule, fileGenerationsModule);
        Assertions.assertNotEquals(versionedEntitiesModule, fileGenerationsModule);
    }

    @Test
    public void testGetModuleNameForAllEnumValues()
    {
        for (ArtifactType type : ArtifactType.values())
        {
            String moduleName = type.getModuleName();
            Assertions.assertNotNull(moduleName);
            Assertions.assertFalse(moduleName.isEmpty());
        }
    }

    @Test
    public void testEnumConstantsAreUnique()
    {
        ArtifactType[] values = ArtifactType.values();

        for (int i = 0; i < values.length; i++)
        {
            for (int j = i + 1; j < values.length; j++)
            {
                Assertions.assertNotEquals(values[i], values[j]);
            }
        }
    }

    @Test
    public void testValueOfAndValuesAreConsistent()
    {
        ArtifactType[] values = ArtifactType.values();

        for (ArtifactType type : values)
        {
            ArtifactType fromValueOf = ArtifactType.valueOf(type.name());
            Assertions.assertSame(type, fromValueOf);
        }
    }

    @Test
    public void testEnumOrdinalValues()
    {
        Assertions.assertEquals(0, ArtifactType.ENTITIES.ordinal());
        Assertions.assertEquals(1, ArtifactType.VERSIONED_ENTITIES.ordinal());
        Assertions.assertEquals(2, ArtifactType.FILE_GENERATIONS.ordinal());
    }

    @Test
    public void testEnumNameValues()
    {
        Assertions.assertEquals("ENTITIES", ArtifactType.ENTITIES.name());
        Assertions.assertEquals("VERSIONED_ENTITIES", ArtifactType.VERSIONED_ENTITIES.name());
        Assertions.assertEquals("FILE_GENERATIONS", ArtifactType.FILE_GENERATIONS.name());
    }

    @Test
    public void testEnumToString()
    {
        Assertions.assertEquals("ENTITIES", ArtifactType.ENTITIES.toString());
        Assertions.assertEquals("VERSIONED_ENTITIES", ArtifactType.VERSIONED_ENTITIES.toString());
        Assertions.assertEquals("FILE_GENERATIONS", ArtifactType.FILE_GENERATIONS.toString());
    }
}
