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

package org.finos.legend.depot.services.generation.file;

import org.finos.legend.depot.domain.generation.file.FileGeneration;
import org.finos.legend.depot.domain.generation.file.StoredFileGeneration;
import org.finos.legend.depot.domain.version.VersionValidator;
import org.finos.legend.depot.services.api.generation.file.ManageFileGenerationsService;
import org.finos.legend.depot.store.api.entities.Entities;
import org.finos.legend.depot.store.api.generation.file.UpdateFileGenerations;
import org.finos.legend.depot.store.mongo.TestStoreMongo;
import org.finos.legend.depot.store.mongo.entities.EntitiesMongo;
import org.finos.legend.depot.store.mongo.generation.file.FileGenerationLoader;
import org.finos.legend.depot.store.mongo.generation.file.FileGenerationsMongo;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.stream.Stream;

public class TestFileGenerationsService extends TestStoreMongo
{

    public static final String AVRO = "avro";
    private static final URL filePath = TestFileGenerationsService.class.getClassLoader().getResource("file-generation/test-file-generation-master-SNAPSHOT.jar");
    protected Entities entities = new EntitiesMongo(mongoProvider);
    private UpdateFileGenerations generations = new FileGenerationsMongo(mongoProvider);
    private ManageFileGenerationsService service = new FileGenerationsServiceImpl(generations, entities);

    @Before
    public void loadData() throws Exception
    {

        try (FileGenerationLoader file = FileGenerationLoader.newFileGenerationsLoader(new File(filePath.toURI())))
        {
            Stream<FileGeneration> data = file.getAllFileGenerations();
            Assert.assertNotNull(data);
            data.forEach(gen ->
            {
                FileGeneration generation = new FileGeneration(gen.getPath().replace("examples_avrogen/", ""), gen.getContent());
                generations.createOrUpdate(new StoredFileGeneration("group.test", "test", VersionValidator.MASTER_SNAPSHOT, "examples::avrogen", AVRO, generation));
                generations.createOrUpdate(new StoredFileGeneration("group.test", "test", "1.0.1", "examples::avrogen", AVRO, generation));
                generations.createOrUpdate(new StoredFileGeneration("group.test", "test", "1.0.0", "examples::avrogen", AVRO, generation));
                generations.createOrUpdate(new StoredFileGeneration("group.test.otherproject", "test", "1.0.0", "examples::avrogen1", AVRO, generation));
            });

            Assert.assertEquals(48, generations.getAll().size());
        }
    }

    @Test
    public void canDelete()
    {

        service.deleteLatest("group.test", "test");
        Assert.assertEquals(36, generations.getAll().size());
        service.delete("group.test.otherproject", "test", "1.0.0");
        Assert.assertEquals(24, generations.getAll().size());
        service.delete("group.test", "test", "1.1.0");
        Assert.assertEquals(24, generations.getAll().size());
    }


    @Test
    public void canQueryFileGenerationEntities()
    {

        List<FileGeneration> gens = service.getLatestFileGenerations("group.test", "test");
        Assert.assertEquals(12, gens.size());

        List<FileGeneration> gens1 = service.getFileGenerations("group.test", "test", "1.0.0");
        Assert.assertEquals(12, gens1.size());

        List<FileGeneration> gens2 = service.getFileGenerations("group.test.other", "test", "1.0.0");
        Assert.assertTrue(gens2.isEmpty());
    }


    @Test
    public void canQueryFileGenerationEntitiesByPath()
    {

        Assert.assertEquals(12, service.getFileGenerationsByPath("group.test", "test", "1.0.0", "examples::avrogen").size());
        Assert.assertTrue(service.getFileGenerationsByPath("group.test", "test", "1.0.0", "examples::avrogen1").isEmpty());

    }


    @Test
    public void canQueryFileGenerationEntitiesByFile()
    {

        Assert.assertTrue(service.getFileGenerationsByFile("group.test", "test", "1.0.0", "/examples/metadata/test/ClientBasic.avro").isPresent());
        Assert.assertTrue(service.getFileGenerationsByFile("group.test.otherproject", "test", "1.0.0", "/examples/metadata/test/ClientBasic.avro").isPresent());
        Assert.assertFalse(service.getFileGenerationsByFile("group.test", "test", "1.0.0", "bad").isPresent());

    }
}
