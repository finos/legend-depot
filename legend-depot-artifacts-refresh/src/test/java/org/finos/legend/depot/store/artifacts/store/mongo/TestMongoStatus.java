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

import org.finos.legend.depot.domain.entity.VersionRevision;
import org.finos.legend.depot.store.mongo.TestStoreMongo;
import org.junit.Assert;
import org.junit.Test;

public class TestMongoStatus extends TestStoreMongo
{

    public static final String TEST_ARTIFACT_ID = "test";
    public static final String TEST_GROUP_ID = "group.example";
    private MongoRefreshStatus status = new MongoRefreshStatus(mongoProvider);

    @Test
    public void testStatus()
    {
        Assert.assertEquals(0, status.getCollection().countDocuments());
        status.createOrUpdate(status.get(VersionRevision.VERSIONS, TEST_GROUP_ID, TEST_ARTIFACT_ID, "1.0.0").withRunning(true));
        Assert.assertEquals(1, status.getCollection().countDocuments());
        status.createOrUpdate(status.get(VersionRevision.VERSIONS, TEST_GROUP_ID, TEST_ARTIFACT_ID, "1.0.0").withRunning(false));
        Assert.assertEquals(1, status.getCollection().countDocuments());

        Assert.assertNotNull(status.get(VersionRevision.VERSIONS, TEST_GROUP_ID, TEST_ARTIFACT_ID, "1.0.0"));
        Assert.assertFalse(status.get(VersionRevision.VERSIONS, TEST_GROUP_ID, TEST_ARTIFACT_ID, "1.0.0").isRunning());

        status.createOrUpdate(status.get(VersionRevision.REVISIONS, TEST_GROUP_ID, TEST_ARTIFACT_ID, "1.0.0").withRunning(true));
        Assert.assertEquals(2, status.getCollection().countDocuments());
        Assert.assertNotNull(status.get(VersionRevision.REVISIONS, TEST_GROUP_ID, TEST_ARTIFACT_ID, "1.0.0"));
        Assert.assertTrue(status.get(VersionRevision.REVISIONS, TEST_GROUP_ID, TEST_ARTIFACT_ID, "1.0.0").isRunning());

    }
}
