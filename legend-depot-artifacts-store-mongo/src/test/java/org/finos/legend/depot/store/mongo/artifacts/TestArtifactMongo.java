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

package org.finos.legend.depot.store.mongo.artifacts;

import org.finos.legend.depot.store.api.admin.artifacts.ArtifactsFilesStore;
import org.finos.legend.depot.store.model.admin.artifacts.ArtifactFile;
import org.finos.legend.depot.store.mongo.TestStoreMongo;
import org.finos.legend.depot.store.mongo.artifacts.ArtifactsFilesMongo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Optional;

public class TestArtifactMongo extends TestStoreMongo
{

    private static final String FILE_PATH = "path/to/entity/jar/entity.jar";


    @Test
    public void canStoreArtifactsInformation()
    {
        ArtifactFile detail = new ArtifactFile(FILE_PATH, "lala");

        ArtifactsFilesStore artifacts = new ArtifactsFilesMongo(this.mongoProvider);
        ArtifactFile result = artifacts.createOrUpdate(detail);
        Assertions.assertNotNull(result);

        Optional<ArtifactFile> artifact = artifacts.find(FILE_PATH);
        Assertions.assertTrue(artifact.isPresent());
        Assertions.assertEquals(FILE_PATH, artifact.get().getPath());

    }

    @Test
    public void canUpdateArtifactsInformation()
    {
        ArtifactFile detail = new ArtifactFile(FILE_PATH,null);

        ArtifactsFilesStore artifacts = new ArtifactsFilesMongo(this.mongoProvider);
        ArtifactFile result = artifacts.createOrUpdate(detail);
        Assertions.assertNotNull(result);
        Optional<ArtifactFile> artifact = artifacts.find(FILE_PATH);
        Assertions.assertNotNull(artifact);
        Assertions.assertEquals(FILE_PATH, artifact.get().getPath());
        Assertions.assertNull(artifact.get().getCheckSum());

        ArtifactFile result1 = artifacts.createOrUpdate(detail.setCheckSum("laalalala"));
        Assertions.assertNotNull(result1);
        Optional<ArtifactFile> artifact1 = artifacts.find(FILE_PATH);
        Assertions.assertNotNull(artifact);
        Assertions.assertEquals(FILE_PATH, artifact1.get().getPath());
        Assertions.assertEquals("laalalala", artifact1.get().getCheckSum());

    }

}
