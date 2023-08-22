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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.jar.JarInputStream;
import java.util.jar.Manifest;

public class ManifestLoader implements AutoCloseable
{
    private static final Logger LOGGER = LoggerFactory.getLogger(ManifestLoader.class);

    public static Manifest readManifest(File jarFile)
    {
        if (jarFile == null)
        {
            return null;
        }

        try (InputStream reader = new FileInputStream(jarFile))
        {
            JarInputStream jarInputStream = new JarInputStream(reader);
            return jarInputStream.getManifest();
        }
        catch (Exception e)
        {
            LOGGER.error(e.getMessage());
            return null;
        }
    }

    @Override
    public void close() throws Exception
    {

    }
}
