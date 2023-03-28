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

package org.finos.legend.depot.store.mongo.admin.artifacts;

import org.finos.legend.depot.store.admin.domain.artifacts.RefreshStatus;
import org.finos.legend.depot.store.mongo.TestStoreMongo;
import org.junit.Assert;
import org.junit.Test;

public class TestMongoStatus extends TestStoreMongo
{

    public static final String TEST_ARTIFACT_ID = "test";
    public static final String TEST_GROUP_ID = "group.example";
    private ArtifactsRefreshStatusMongo refreshStatus = new ArtifactsRefreshStatusMongo(mongoProvider);

    @Test
    public void testStatus()
    {
        Assert.assertEquals(0, refreshStatus.getCollection().countDocuments());
        refreshStatus.insert(refreshStatus.get(TEST_GROUP_ID, TEST_ARTIFACT_ID, "1.0.0").orElse(new RefreshStatus(TEST_GROUP_ID, TEST_ARTIFACT_ID, "1.0.0")));
        Assert.assertEquals(1, refreshStatus.getCollection().countDocuments());

        Assert.assertFalse(refreshStatus.get(TEST_GROUP_ID, TEST_ARTIFACT_ID, "1.0.0").get().isExpired());

    }

    @Test
    public void testFindByParentId()
    {
        Assert.assertEquals(0, refreshStatus.getCollection().countDocuments());
        refreshStatus.insert(new RefreshStatus(TEST_GROUP_ID,TEST_ARTIFACT_ID,"0.0.0").withParentEventId("test"));
        refreshStatus.insert(new RefreshStatus(TEST_GROUP_ID,TEST_ARTIFACT_ID,"0.0.1").withParentEventId("test"));
        refreshStatus.insert(new RefreshStatus(TEST_GROUP_ID,TEST_ARTIFACT_ID,"0.0.2").withParentEventId("test"));
        refreshStatus.insert(new RefreshStatus(TEST_GROUP_ID,TEST_ARTIFACT_ID,"0.0.3").withParentEventId("test1"));
        Assert.assertEquals(4, refreshStatus.getCollection().countDocuments());

        Assert.assertEquals(3,refreshStatus.find(null,null,null,null,"test").size());
        Assert.assertEquals(1,refreshStatus.find(null,null,null,null,"test1").size());


    }
}
