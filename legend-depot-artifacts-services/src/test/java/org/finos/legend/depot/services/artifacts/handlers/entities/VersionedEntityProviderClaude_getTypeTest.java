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

package org.finos.legend.depot.services.artifacts.handlers.entities;

import org.finos.legend.depot.domain.artifacts.repository.ArtifactType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class VersionedEntityProviderClaude_getTypeTest
{
    private VersionedEntityProvider provider;

    @BeforeEach
    public void setUp()
    {
        provider = new VersionedEntityProvider();
    }

    @Test
    public void testGetType_returnsVersionedEntities()
    {
        // Test that getType returns VERSIONED_ENTITIES artifact type
        ArtifactType type = provider.getType();

        Assertions.assertNotNull(type, "getType() should not return null");
        Assertions.assertEquals(ArtifactType.VERSIONED_ENTITIES, type,
            "getType() should return VERSIONED_ENTITIES artifact type");
    }

    @Test
    public void testGetType_doesNotReturnEntitiesType()
    {
        // Test that getType returns VERSIONED_ENTITIES and not the parent's ENTITIES type
        ArtifactType type = provider.getType();

        Assertions.assertNotEquals(ArtifactType.ENTITIES, type,
            "getType() should not return ENTITIES type (should return VERSIONED_ENTITIES)");
    }

    @Test
    public void testGetType_doesNotReturnFileGenerationsType()
    {
        // Test that getType returns VERSIONED_ENTITIES and not FILE_GENERATIONS
        ArtifactType type = provider.getType();

        Assertions.assertNotEquals(ArtifactType.FILE_GENERATIONS, type,
            "getType() should not return FILE_GENERATIONS type");
    }

    @Test
    public void testGetType_hasCorrectModuleName()
    {
        // Test that the returned artifact type has the correct module name
        ArtifactType type = provider.getType();
        String moduleName = type.getModuleName();

        Assertions.assertEquals("versioned-entities", moduleName,
            "Module name should be 'versioned-entities'");
    }

    @Test
    public void testGetType_isConsistent()
    {
        // Test that multiple calls to getType return the same artifact type
        ArtifactType type1 = provider.getType();
        ArtifactType type2 = provider.getType();

        Assertions.assertSame(type1, type2,
            "Multiple calls to getType() should return the same enum constant");
    }

    @Test
    public void testGetType_acrossMultipleInstances()
    {
        // Test that different instances return the same artifact type
        VersionedEntityProvider provider1 = new VersionedEntityProvider();
        VersionedEntityProvider provider2 = new VersionedEntityProvider();

        ArtifactType type1 = provider1.getType();
        ArtifactType type2 = provider2.getType();

        Assertions.assertSame(type1, type2,
            "Different instances should return the same VERSIONED_ENTITIES enum constant");
    }

    @Test
    public void testGetType_overridesParentImplementation()
    {
        // Test that VersionedEntityProvider overrides EntityProvider's getType
        VersionedEntityProvider versionedProvider = new VersionedEntityProvider();
        EntityProvider entityProvider = new EntityProvider();

        ArtifactType versionedType = versionedProvider.getType();
        ArtifactType entityType = entityProvider.getType();

        Assertions.assertNotEquals(entityType, versionedType,
            "VersionedEntityProvider.getType() should override parent's implementation");
        Assertions.assertEquals(ArtifactType.VERSIONED_ENTITIES, versionedType);
        Assertions.assertEquals(ArtifactType.ENTITIES, entityType);
    }

    @Test
    public void testGetType_returnedTypeIsValidEnum()
    {
        // Test that the returned type is a valid ArtifactType enum value
        ArtifactType type = provider.getType();

        // Should be one of the valid enum constants
        boolean isValidEnum = type == ArtifactType.ENTITIES ||
                            type == ArtifactType.VERSIONED_ENTITIES ||
                            type == ArtifactType.FILE_GENERATIONS;

        Assertions.assertTrue(isValidEnum, "Returned type should be a valid ArtifactType enum");
        Assertions.assertEquals(ArtifactType.VERSIONED_ENTITIES, type);
    }

    @Test
    public void testGetType_usedByMatchesArtifactType()
    {
        // Test that getType is used internally by matchesArtifactType
        // This verifies the integration between the two methods
        ArtifactType type = provider.getType();
        String moduleName = type.getModuleName();

        // The matchesArtifactType method should use this module name
        Assertions.assertEquals("versioned-entities", moduleName);

        // Verify that a file with this module name in the correct format matches
        java.io.File file = new java.io.File("test-" + moduleName + "-1.0.0.jar");
        boolean matches = provider.matchesArtifactType(file);

        Assertions.assertTrue(matches,
            "matchesArtifactType should use the module name from getType()");
    }
}
