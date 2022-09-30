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

package org.finos.legend.depot.store.artifacts.services.generation.file;

import org.finos.legend.depot.domain.generation.file.FileGeneration;
import org.finos.legend.depot.store.mongo.generation.file.FileGenerationLoader;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;

public class TestFileGenerationLoader
{

    private static final URL filePath = TestFileGenerationLoader.class.getClassLoader().getResource("file-generation/test-file-generation-master-SNAPSHOT.jar");

    @Test
    public void canReadFileGenerationArtifacts() throws URISyntaxException
    {
        List<FileGeneration> generations = FileGenerationLoader.newFileGenerationsLoader(new File(filePath.toURI())).getAllFileGenerations().collect(Collectors.toList());
        Assert.assertNotNull(generations);
        Assert.assertEquals(14, generations.size());
        FileGeneration generation = generations.get(0);
        Assert.assertFalse(generation.getContent().isEmpty());
    }

}
