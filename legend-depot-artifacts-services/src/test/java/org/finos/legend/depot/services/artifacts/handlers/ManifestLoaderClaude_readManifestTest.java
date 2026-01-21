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

import java.io.File;
import java.util.jar.Manifest;

public class ManifestLoaderClaude_readManifestTest
{
    @Test
    public void testReadManifestFromValidJar()
    {
        // Test reading manifest from a valid JAR file
        String jarFilePath = ManifestLoaderClaude_readManifestTest.class.getClassLoader()
                .getResource("repository/examples/metadata/test-entities/1.0.0/test-entities-1.0.0.jar").getFile();
        File jarFile = new File(jarFilePath);

        Manifest manifest = ManifestLoader.readManifest(jarFile);

        Assertions.assertNotNull(manifest);
    }

    @Test
    public void testReadManifestFromNullFile()
    {
        // Test that null input returns null
        Manifest manifest = ManifestLoader.readManifest(null);

        Assertions.assertNull(manifest);
    }

    @Test
    public void testReadManifestFromNonJarFile()
    {
        // Test reading manifest from a POM file (not a JAR)
        String pomFilePath = ManifestLoaderClaude_readManifestTest.class.getClassLoader()
                .getResource("repository/examples/metadata/test-entities/1.0.0/test-entities-1.0.0.pom").getFile();
        File pomFile = new File(pomFilePath);

        Manifest manifest = ManifestLoader.readManifest(pomFile);

        Assertions.assertNull(manifest);
    }

    @Test
    public void testReadManifestFromNonExistentFile()
    {
        // Test reading manifest from a file that doesn't exist
        File nonExistentFile = new File("/path/to/non/existent/file.jar");

        Manifest manifest = ManifestLoader.readManifest(nonExistentFile);

        Assertions.assertNull(manifest);
    }

    @Test
    public void testReadManifestFromDifferentJar()
    {
        // Test reading manifest from a different JAR file
        String jarFilePath = ManifestLoaderClaude_readManifestTest.class.getClassLoader()
                .getResource("repository/examples/metadata/test-entities/2.0.0/test-entities-2.0.0.jar").getFile();
        File jarFile = new File(jarFilePath);

        Manifest manifest = ManifestLoader.readManifest(jarFile);

        Assertions.assertNotNull(manifest);
    }

    @Test
    public void testReadManifestFromSnapshotJar()
    {
        // Test reading manifest from a snapshot JAR
        String jarFilePath = ManifestLoaderClaude_readManifestTest.class.getClassLoader()
                .getResource("repository/examples/metadata/test-entities/master-SNAPSHOT/test-entities-master-SNAPSHOT.jar").getFile();
        File jarFile = new File(jarFilePath);

        Manifest manifest = ManifestLoader.readManifest(jarFile);

        Assertions.assertNotNull(manifest);
    }

    @Test
    public void testReadManifestMultipleTimes()
    {
        // Test reading manifest multiple times from the same file
        String jarFilePath = ManifestLoaderClaude_readManifestTest.class.getClassLoader()
                .getResource("repository/examples/metadata/test-entities/1.0.0/test-entities-1.0.0.jar").getFile();
        File jarFile = new File(jarFilePath);

        Manifest manifest1 = ManifestLoader.readManifest(jarFile);
        Manifest manifest2 = ManifestLoader.readManifest(jarFile);

        Assertions.assertNotNull(manifest1);
        Assertions.assertNotNull(manifest2);
        // Each call should return a new Manifest object
        Assertions.assertNotSame(manifest1, manifest2);
    }

    @Test
    public void testReadManifestWithDifferentVersions()
    {
        // Test reading manifests from different versions of the same artifact
        String jar1Path = ManifestLoaderClaude_readManifestTest.class.getClassLoader()
                .getResource("repository/examples/metadata/test-versioned-entities/1.0.0/test-versioned-entities-1.0.0.jar").getFile();
        String jar2Path = ManifestLoaderClaude_readManifestTest.class.getClassLoader()
                .getResource("repository/examples/metadata/test-versioned-entities/2.0.0/test-versioned-entities-2.0.0.jar").getFile();

        File jar1 = new File(jar1Path);
        File jar2 = new File(jar2Path);

        Manifest manifest1 = ManifestLoader.readManifest(jar1);
        Manifest manifest2 = ManifestLoader.readManifest(jar2);

        Assertions.assertNotNull(manifest1);
        Assertions.assertNotNull(manifest2);
    }

    @Test
    public void testReadManifestFromFileGenerationJar()
    {
        // Test reading manifest from a file-generation JAR
        String jarFilePath = ManifestLoaderClaude_readManifestTest.class.getClassLoader()
                .getResource("repository/examples/metadata/test-dependencies-file-generation/1.0.0/test-dependencies-file-generation-1.0.0.jar").getFile();
        File jarFile = new File(jarFilePath);

        Manifest manifest = ManifestLoader.readManifest(jarFile);

        Assertions.assertNotNull(manifest);
    }
}
