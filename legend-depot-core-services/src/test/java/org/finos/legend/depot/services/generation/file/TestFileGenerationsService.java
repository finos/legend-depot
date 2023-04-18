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
import org.finos.legend.depot.domain.project.StoreProjectData;
import org.finos.legend.depot.domain.project.StoreProjectVersionData;
import org.finos.legend.depot.domain.version.VersionValidator;
import org.finos.legend.depot.services.api.generation.file.ManageFileGenerationsService;
import org.finos.legend.depot.services.api.projects.ProjectsService;
import org.finos.legend.depot.services.projects.ProjectsServiceImpl;
import org.finos.legend.depot.store.api.entities.Entities;
import org.finos.legend.depot.store.api.generation.file.UpdateFileGenerations;
import org.finos.legend.depot.store.api.projects.UpdateProjects;
import org.finos.legend.depot.store.api.projects.UpdateProjectsVersions;
import org.finos.legend.depot.store.mongo.TestStoreMongo;
import org.finos.legend.depot.store.mongo.entities.EntitiesMongo;
import org.finos.legend.depot.store.mongo.generation.file.FileGenerationsMongo;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.finos.legend.depot.domain.version.VersionValidator.MASTER_SNAPSHOT;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TestFileGenerationsService extends TestStoreMongo
{

    public static final String AVRO = "avro";
    private static final URL filePath = TestFileGenerationsService.class.getClassLoader().getResource("file-generation/test-file-generation-master-SNAPSHOT.jar");
    protected Entities entities = new EntitiesMongo(mongoProvider);
    private UpdateFileGenerations generations = new FileGenerationsMongo(mongoProvider);
    UpdateProjectsVersions projectsVersionsStore = mock(UpdateProjectsVersions.class);
    UpdateProjects projectsStore = mock(UpdateProjects.class);
    private ProjectsService projectsService = new ProjectsServiceImpl(projectsVersionsStore, projectsStore);
    private ManageFileGenerationsService service = new ManageFileGenerationsServiceImpl(generations, entities, projectsService);

    @Before
    public void loadData() throws Exception
    {

        try (FileGenerationLoader file = FileGenerationLoader.newFileGenerationsLoader(new File(filePath.toURI())))
        {
            Stream<FileGeneration> data = file.getAllFileGenerations();
            Assert.assertNotNull(data);
            data.forEach(gen ->
            {
                if (gen.getPath().startsWith("/examples/metadata"))
                {
                    FileGeneration generation = new FileGeneration(gen.getPath(), gen.getContent());
                    generations.createOrUpdate(new StoredFileGeneration("group.test", "test",  MASTER_SNAPSHOT, null, null, generation));
                    generations.createOrUpdate(new StoredFileGeneration("group.test", "test", "1.0.0", "examples::metadata::test::ClientBasic", null, generation));
                    generations.createOrUpdate(new StoredFileGeneration("group.test.otherproject", "test", "1.0.0", "examples::metadata::test::ClientBasic", null, generation));
                }
                else
                {
                    FileGeneration generation = new FileGeneration(gen.getPath().replace("examples_avrogen/", ""), gen.getContent());
                    generations.createOrUpdate(new StoredFileGeneration("group.test", "test", MASTER_SNAPSHOT, "examples::avrogen", AVRO, generation));
                    generations.createOrUpdate(new StoredFileGeneration("group.test", "test", "1.0.1", "examples::avrogen", AVRO, generation));
                    generations.createOrUpdate(new StoredFileGeneration("group.test", "test", "1.0.0", "examples::avrogen", AVRO, generation));
                    generations.createOrUpdate(new StoredFileGeneration("group.test.otherproject", "test", "1.0.0", "examples::avrogen1", AVRO, generation));
                }
            });

            Assert.assertEquals(54, generations.getAll().size());
        }

        when(projectsStore.find("group.test","test")).thenReturn(Optional.of(new StoreProjectData("prod-1","group.test","test")));
        when(projectsStore.find("group.test.otherproject", "test")).thenReturn(Optional.of(new StoreProjectData("prod-2","group.test.otherproject", "test")));
        when(projectsVersionsStore.find("group.test","test",MASTER_SNAPSHOT)).thenReturn(Optional.of(new StoreProjectVersionData("group-test","test",MASTER_SNAPSHOT)));
        when(projectsVersionsStore.find("group.test","test","1.0.0")).thenReturn(Optional.of(new StoreProjectVersionData("group-test","test","1.0.0")));
        when(projectsVersionsStore.find("i.dont","exist","version")).thenReturn(Optional.empty());
        when(projectsVersionsStore.find("group.test.otherproject", "test","1.0.0")).thenReturn(Optional.of(new StoreProjectVersionData("group-test","test","1.0.0")));
    }

    @Test
    public void canDelete()
    {

        service.delete("group.test", "test",MASTER_SNAPSHOT);
        Assert.assertEquals(40, generations.getAll().size());
        service.delete("group.test.otherproject", "test", "1.0.0");
        Assert.assertEquals(26, generations.getAll().size());
        try
        {
            service.delete("group.test", "test111", "1.1.0");
            Assert.fail("exception expected");
        }
        catch (IllegalArgumentException e)
        {
            Assert.assertTrue(true);
        }
        Assert.assertEquals(26, generations.getAll().size());

    }

    @Test
    public void canQueryFileGenerationEntities()
    {

        List<FileGeneration> generations = service.getFileGenerations("group.test", "test",MASTER_SNAPSHOT);
        Assert.assertEquals(14, generations.size());

        List<FileGeneration> gens1 = service.getFileGenerations("group.test", "test", "1.0.0");
        Assert.assertEquals(14, gens1.size());

        try
        {
            List<FileGeneration> gens2 = service.getFileGenerations("group.test.other", "test", "1.0.0");
            Assert.fail("Exception expected");
        }
        catch (IllegalArgumentException e)
        {
            Assert.assertTrue(true);
        }
    }

    @Test
    public void canQueryFileGenerationEntitiesByElementPath()
    {
        Assert.assertEquals(12, service.getFileGenerationsByElementPath("group.test", "test", "1.0.0", "examples::avrogen").size());
        Assert.assertTrue(service.getFileGenerationsByElementPath("group.test", "test", "1.0.0", "examples::avrogen1").isEmpty());
        Assert.assertEquals(2, service.getFileGenerationsByElementPath("group.test", "test", "1.0.0", "examples::metadata::test::ClientBasic").size());
    }


    @Test
    public void canQueryFileGenerationEntitiesByFilePath()
    {
        Assert.assertTrue(service.getFileGenerationsByFilePath("group.test", "test", "1.0.0", "/examples/metadata/test/ClientBasic.avro").isPresent());
        Assert.assertTrue(service.getFileGenerationsByFilePath("group.test", "test", "1.0.0", "/examples/metadata/test/ClientBasic/my-ext/Output1.txt").isPresent());
        Assert.assertTrue(service.getFileGenerationsByFilePath("group.test", "test", "1.0.0", "/examples/metadata/test/ClientBasic/my-ext/Output2.txt").isPresent());
        Assert.assertTrue(service.getFileGenerationsByFilePath("group.test.otherproject", "test", "1.0.0", "/examples/metadata/test/ClientBasic.avro").isPresent());

        Assert.assertFalse(service.getFileGenerationsByFilePath("group.test", "test", "1.0.0", "bad").isPresent());
        Assert.assertFalse(service.getFileGenerationsByFilePath("group.test", "test", "1.0.0", "/examples/metadata/test/ClientBasic/my-ext/DND.txt").isPresent());
    }

    @Test
    public void canQueryFileGenerationEntitiesByFileContent()
    {
        Assert.assertTrue(service.getFileGenerationContentByFilePath("group.test", "test", "1.0.0", "/examples/metadata/test/ClientBasic.avro").isPresent());
        Assert.assertTrue(service.getFileGenerationContentByFilePath("group.test", "test", "1.0.0", "/examples/metadata/test/ClientBasic/my-ext/Output1.txt").isPresent());
        Assert.assertTrue(service.getFileGenerationContentByFilePath("group.test", "test", "1.0.0", "/examples/metadata/test/ClientBasic/my-ext/Output2.txt").isPresent());
        Assert.assertTrue(service.getFileGenerationsByFilePath("group.test", "test",MASTER_SNAPSHOT,  "/examples/metadata/test/ClientBasic.avro").isPresent());
    }

    @Test(expected = IllegalArgumentException.class)
    public void cantGetGenerationsForNonExistentProject()
    {
        service.getFileGenerations("i.dont","exist","version");
    }

    @Test(expected = IllegalArgumentException.class)
    public void cantGetGenerationsForNonExistentVersion()
    {
        service.getFileGenerations("group.test","test","10.0.0");
    }
}