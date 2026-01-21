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

public class ManifestLoaderClaude_constructorTest
{
    @Test
    public void testConstructor()
    {
        // Test that the default constructor can instantiate the object
        ManifestLoader loader = new ManifestLoader();
        Assertions.assertNotNull(loader);
    }

    @Test
    public void testConstructorWithTryWithResources() throws Exception
    {
        // Test that the object can be used in try-with-resources since it implements AutoCloseable
        try (ManifestLoader loader = new ManifestLoader())
        {
            Assertions.assertNotNull(loader);
        }
        // If we reach here without exception, the close method worked correctly
    }

    @Test
    public void testCloseDoesNotThrowException() throws Exception
    {
        // Test that close() can be called explicitly without throwing an exception
        ManifestLoader loader = new ManifestLoader();
        Assertions.assertDoesNotThrow(() -> loader.close());
    }

    @Test
    public void testMultipleInstances()
    {
        // Test that multiple instances can be created independently
        ManifestLoader loader1 = new ManifestLoader();
        ManifestLoader loader2 = new ManifestLoader();

        Assertions.assertNotNull(loader1);
        Assertions.assertNotNull(loader2);
        Assertions.assertNotSame(loader1, loader2);
    }
}
