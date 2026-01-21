//  Copyright 2023 Goldman Sachs
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

package org.finos.legend.depot.services.artifacts.handlers;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ManifestLoaderClaude_closeTest
{
    @Test
    public void testCloseDoesNotThrowException() throws Exception
    {
        // Test that close() can be called without throwing an exception
        ManifestLoader loader = new ManifestLoader();

        Assertions.assertDoesNotThrow(() -> loader.close());
    }

    @Test
    public void testCloseMultipleTimes() throws Exception
    {
        // Test that close() can be called multiple times (idempotency)
        ManifestLoader loader = new ManifestLoader();

        loader.close();
        loader.close();
        loader.close();

        // No exception should be thrown
        Assertions.assertDoesNotThrow(() -> loader.close());
    }

    @Test
    public void testCloseInTryWithResources() throws Exception
    {
        // Test that close() is automatically called in try-with-resources
        try (ManifestLoader loader = new ManifestLoader())
        {
            Assertions.assertNotNull(loader);
        }
        // If we reach here without exception, close() was called successfully
    }

    @Test
    public void testCloseAfterConstruction() throws Exception
    {
        // Test that close() can be called immediately after construction
        ManifestLoader loader = new ManifestLoader();
        loader.close();

        // Should not throw any exception
    }

    @Test
    public void testMultipleInstancesClose() throws Exception
    {
        // Test that multiple instances can be closed independently
        ManifestLoader loader1 = new ManifestLoader();
        ManifestLoader loader2 = new ManifestLoader();

        loader1.close();
        loader2.close();

        // Both should close without exception
        Assertions.assertDoesNotThrow(() -> loader1.close());
        Assertions.assertDoesNotThrow(() -> loader2.close());
    }

    @Test
    public void testNestedTryWithResources() throws Exception
    {
        // Test that close() works correctly in nested try-with-resources
        try (ManifestLoader loader1 = new ManifestLoader())
        {
            try (ManifestLoader loader2 = new ManifestLoader())
            {
                Assertions.assertNotNull(loader1);
                Assertions.assertNotNull(loader2);
            }
        }
        // Both instances should have close() called without exception
    }

    @Test
    public void testCloseAfterExplicitClose() throws Exception
    {
        // Test that close() can be called explicitly even when used in try-with-resources
        ManifestLoader loader = new ManifestLoader();
        loader.close();

        // Close again in try-with-resources
        try (ManifestLoader sameLoader = loader)
        {
            Assertions.assertNotNull(sameLoader);
        }
        // close() should be called again without issue
    }

    @Test
    public void testCloseWithMultipleCalls() throws Exception
    {
        // Test that close() can be called many times in succession
        ManifestLoader loader = new ManifestLoader();

        for (int i = 0; i < 10; i++)
        {
            loader.close();
        }

        // All calls should succeed without exception
        Assertions.assertDoesNotThrow(() -> loader.close());
    }
}
