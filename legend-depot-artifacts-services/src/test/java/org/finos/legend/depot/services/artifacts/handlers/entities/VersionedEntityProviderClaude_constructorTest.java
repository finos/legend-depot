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
import org.junit.jupiter.api.Test;

import java.io.File;

public class VersionedEntityProviderClaude_constructorTest
{
    @Test
    public void testConstructor_createsValidInstance()
    {
        // Test that constructor creates a non-null instance
        VersionedEntityProvider provider = new VersionedEntityProvider();
        Assertions.assertNotNull(provider, "Constructor should create a non-null instance");
    }

    @Test
    public void testConstructor_instanceCanCallGetType()
    {
        // Test that the constructed instance has proper inheritance chain
        // and can call the overridden getType method
        VersionedEntityProvider provider = new VersionedEntityProvider();
        ArtifactType type = provider.getType();

        Assertions.assertNotNull(type, "getType() should return a non-null value");
        Assertions.assertEquals(ArtifactType.VERSIONED_ENTITIES, type,
            "getType() should return VERSIONED_ENTITIES artifact type");
    }

    @Test
    public void testConstructor_instanceImplementsCorrectInterface()
    {
        // Test that the constructed instance implements the expected interface
        VersionedEntityProvider provider = new VersionedEntityProvider();

        // Should be assignable to parent class
        Assertions.assertTrue(provider instanceof EntityProvider,
            "VersionedEntityProvider should extend EntityProvider");
    }

    @Test
    public void testConstructor_multipleInstancesAreIndependent()
    {
        // Test that multiple instances can be created independently
        VersionedEntityProvider provider1 = new VersionedEntityProvider();
        VersionedEntityProvider provider2 = new VersionedEntityProvider();

        Assertions.assertNotNull(provider1);
        Assertions.assertNotNull(provider2);
        Assertions.assertNotSame(provider1, provider2,
            "Each constructor call should create a distinct instance");

        // Both should have the same behavior
        Assertions.assertEquals(provider1.getType(), provider2.getType());
    }

    @Test
    public void testConstructor_instanceCanCallMatchesArtifactType()
    {
        // Test that the constructed instance can call inherited/overridden methods
        VersionedEntityProvider provider = new VersionedEntityProvider();

        // Test with a file that should match versioned-entities pattern
        File versionedFile = new File("test-versioned-entities-1.0.0.jar");
        boolean matches = provider.matchesArtifactType(versionedFile);

        Assertions.assertTrue(matches,
            "Instance should correctly identify versioned-entities files");
    }

    @Test
    public void testConstructor_instanceHasCorrectTypeModuleName()
    {
        // Test that the instance returns correct module name through getType()
        VersionedEntityProvider provider = new VersionedEntityProvider();

        String moduleName = provider.getType().getModuleName();
        Assertions.assertEquals("versioned-entities", moduleName,
            "Module name should be 'versioned-entities'");
    }

    @Test
    public void testConstructor_instanceExcludesNonVersionedEntities()
    {
        // Test that the constructed instance properly distinguishes from regular entities
        VersionedEntityProvider provider = new VersionedEntityProvider();

        // Regular entities file should not match
        File entitiesFile = new File("test-entities-1.0.0.jar");
        boolean matches = provider.matchesArtifactType(entitiesFile);

        Assertions.assertFalse(matches,
            "Instance should not match regular entities files (without 'versioned-' prefix)");
    }

    @Test
    public void testConstructor_instanceMatchesVersionedEntitiesWithSeparators()
    {
        // Test that instance matches files with proper separator pattern
        VersionedEntityProvider provider = new VersionedEntityProvider();

        // File with separator pattern: "-versioned-entities-"
        File file = new File("my-project-versioned-entities-1.0.0.jar");
        boolean matches = provider.matchesArtifactType(file);

        Assertions.assertTrue(matches,
            "Instance should match files containing '-versioned-entities-'");
    }
}
