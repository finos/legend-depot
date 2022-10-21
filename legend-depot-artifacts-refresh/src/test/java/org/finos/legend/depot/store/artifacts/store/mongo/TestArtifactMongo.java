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

package org.finos.legend.depot.store.artifacts.store.mongo;

import org.finos.legend.depot.domain.version.VersionValidator;
import org.finos.legend.depot.store.artifacts.domain.ArtifactDetail;
import org.finos.legend.depot.store.artifacts.store.mongo.api.UpdateArtifacts;
import org.finos.legend.depot.store.mongo.TestStoreMongo;
import org.junit.Assert;
import org.junit.Test;

import java.util.Optional;

public class TestArtifactMongo extends TestStoreMongo
{

    private static final String FILE_PATH = "path/to/entity/jar/entity.jar";


    @Test
    public void canStoreArtifactsInformation()
    {
        ArtifactDetail detail = new ArtifactDetail(FILE_PATH, "lala");

        UpdateArtifacts artifacts = new ArtifactsMongo(this.mongoProvider);
        ArtifactDetail result = artifacts.createOrUpdate(detail);
        Assert.assertNotNull(result);

        Optional<ArtifactDetail> artifact = artifacts.find(FILE_PATH);
        Assert.assertTrue(artifact.isPresent());
        Assert.assertEquals(FILE_PATH, artifact.get().getPath());

    }

    @Test
    public void canUpdateArtifactsInformation()
    {
        ArtifactDetail detail = new ArtifactDetail(FILE_PATH,null);

        UpdateArtifacts artifacts = new ArtifactsMongo(this.mongoProvider);
        ArtifactDetail result = artifacts.createOrUpdate(detail);
        Assert.assertNotNull(result);
        Optional<ArtifactDetail> artifact = artifacts.find(FILE_PATH);
        Assert.assertNotNull(artifact);
        Assert.assertEquals(FILE_PATH, artifact.get().getPath());
        Assert.assertNull(artifact.get().getCheckSum());

        ArtifactDetail result1 = artifacts.createOrUpdate(detail.setCheckSum("laalalala"));
        Assert.assertNotNull(result1);
        Optional<ArtifactDetail> artifact1 = artifacts.find(FILE_PATH);
        Assert.assertNotNull(artifact);
        Assert.assertEquals(FILE_PATH, artifact1.get().getPath());
        Assert.assertEquals("laalalala", artifact1.get().getCheckSum());

    }
}
