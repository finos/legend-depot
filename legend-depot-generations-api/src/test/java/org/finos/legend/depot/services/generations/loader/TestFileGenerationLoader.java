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

package org.finos.legend.depot.services.generations.loader;

import org.finos.legend.depot.domain.generation.DepotGeneration;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;

public class TestFileGenerationLoader
{

    private static final URL filePath = TestFileGenerationLoader.class.getClassLoader().getResource("generations/test-file-generation-master-SNAPSHOT.jar");

    @Test
    public void canReadFileGenerationArtifacts() throws URISyntaxException
    {
        List<DepotGeneration> generations = FileGenerationLoader.newFileGenerationsLoader(new File(filePath.toURI())).getAllFileGenerations().collect(Collectors.toList());
        Assertions.assertNotNull(generations);
        Assertions.assertEquals(14, generations.size());
        DepotGeneration generation = generations.get(0);
        Assertions.assertFalse(generation.getContent().isEmpty());
    }

}
