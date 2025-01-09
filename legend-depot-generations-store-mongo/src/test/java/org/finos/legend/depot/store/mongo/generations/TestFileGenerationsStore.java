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

package org.finos.legend.depot.store.mongo.generations;

import org.finos.legend.depot.store.model.generations.StoredFileGeneration;
import org.finos.legend.depot.store.api.generations.UpdateFileGenerations;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.finos.legend.depot.domain.version.VersionValidator.BRANCH_SNAPSHOT;

public class TestFileGenerationsStore extends TestGenerationsStoreMongo
{

    private UpdateFileGenerations generations = new FileGenerationsMongo(mongoProvider);
    private static String TEST_GROUP_ID = "examples.metadata";
    private static String TEST_ARTIFACT_ID = "test";


    @BeforeEach
    public void loadData()
    {
        setUpFileGenerationFromFile(this.getClass().getClassLoader().getResource("data/file-generations.json"),mongoProvider);
        Assertions.assertEquals(11, generations.getAll().size());
    }


    @Test
    public void canQueryByVersion()
    {
        List<StoredFileGeneration> result = generations.find(TEST_GROUP_ID, TEST_ARTIFACT_ID, "2.3.3");
        Assertions.assertEquals(11, result.size());

        Assertions.assertEquals(0, generations.find(TEST_GROUP_ID, TEST_ARTIFACT_ID, "12.3.2").size());

    }

    @Test
    public void canQueryByElementPath()
    {
        List<StoredFileGeneration> result = generations.findByElementPath(TEST_GROUP_ID, TEST_ARTIFACT_ID, "2.3.3", "com::avrogen");
        Assertions.assertEquals(3, result.size());

        Assertions.assertEquals(0, generations.findByElementPath(TEST_GROUP_ID, TEST_ARTIFACT_ID, "12.3.2", "com::avrogen").size());
        Assertions.assertEquals(0, generations.findByElementPath(TEST_GROUP_ID, TEST_ARTIFACT_ID, BRANCH_SNAPSHOT("master"), "com::jsonGen").size());

        List<StoredFileGeneration> result2 = generations.findByElementPath(TEST_GROUP_ID, TEST_ARTIFACT_ID, "2.3.3", "com::avrogen");
        Assertions.assertEquals(3, result2.size());

        List<StoredFileGeneration> result3 = generations.findByElementPath(TEST_GROUP_ID, TEST_ARTIFACT_ID, "2.3.3", "com::MyElementPath");
        Assertions.assertEquals(1, result3.size());
    }

    @Test
    public void canQueryByGenerationType()
    {
        List<StoredFileGeneration> result = generations.findByType(TEST_GROUP_ID, TEST_ARTIFACT_ID, "2.3.3", "avro");
        Assertions.assertEquals(3, result.size());

        Assertions.assertEquals(0, generations.findByType(TEST_GROUP_ID, TEST_ARTIFACT_ID, "12.3.2", "java").size());
        Assertions.assertEquals(0, generations.findByType(TEST_GROUP_ID, TEST_ARTIFACT_ID, BRANCH_SNAPSHOT("master"), "java").size());

    }

    @Test
    public void canQueryByGenerationFilePath()
    {
        Assertions.assertTrue(generations.findByFilePath(TEST_GROUP_ID, TEST_ARTIFACT_ID, "2.3.3", "/examples/metadata/test/ClientBasic.avro").isPresent());
        Assertions.assertTrue(generations.findByFilePath(TEST_GROUP_ID, TEST_ARTIFACT_ID, "2.3.3", "/examples/generated/test/other/MyOutput.json").isPresent());
        Assertions.assertFalse(generations.findByFilePath(TEST_GROUP_ID, TEST_ARTIFACT_ID, "0.01.1", "/examples/metadata/test/ClientBasic.avro").isPresent());
        Assertions.assertFalse(generations.findByFilePath(TEST_GROUP_ID, TEST_ARTIFACT_ID, BRANCH_SNAPSHOT("master"), "com/finos/sdgashdf").isPresent());
    }

}
