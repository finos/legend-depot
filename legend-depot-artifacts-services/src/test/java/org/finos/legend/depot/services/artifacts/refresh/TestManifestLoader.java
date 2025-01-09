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

package org.finos.legend.depot.services.artifacts.refresh;

import org.finos.legend.depot.services.artifacts.handlers.ManifestLoader;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.jar.Manifest;

public class TestManifestLoader
{
    @Test
    public void canReadManifest()
    {
        String jarFilePath = TestManifestLoader.class.getClassLoader().getResource("repository/examples/metadata/test-entities/1.0.0/test-entities-1.0.0.jar").getFile();
        File jarFile = new File(jarFilePath);
        Manifest manifest = ManifestLoader.readManifest(jarFile);
        Assertions.assertNotNull(manifest);
    }

    @Test
    public void canHandleInvalidFile()
    {
        String pomFilePath = TestManifestLoader.class.getClassLoader().getResource("repository/examples/metadata/test-entities/1.0.0/test-entities-1.0.0.pom").getFile();
        File pomFile = new File(pomFilePath);
        Manifest manifest = ManifestLoader.readManifest(pomFile);
        Assertions.assertNull(manifest);
    }

    @Test
    public void canHandleNullFile()
    {
        Manifest manifest = ManifestLoader.readManifest(null);
        Assertions.assertNull(manifest);
    }
}
