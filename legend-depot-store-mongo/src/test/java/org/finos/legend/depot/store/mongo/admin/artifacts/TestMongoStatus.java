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

import org.finos.legend.depot.domain.api.MetadataEventResponse;
import org.finos.legend.depot.store.admin.domain.artifacts.RefreshStatus;
import org.finos.legend.depot.store.mongo.TestStoreMongo;
import org.junit.Assert;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.finos.legend.depot.domain.version.VersionValidator.MASTER_SNAPSHOT;

public class TestMongoStatus extends TestStoreMongo
{

    public static final String TEST_ARTIFACT_ID = "test";
    public static final String TEST_GROUP_ID = "group.example";
    private ArtifactsRefreshStatusMongo refreshStatus = new ArtifactsRefreshStatusMongo(mongoProvider);

    @Test
    public void testStatus()
    {
        Assert.assertEquals(0, refreshStatus.getCollection().countDocuments());
        refreshStatus.createOrUpdate(refreshStatus.get(TEST_GROUP_ID, TEST_ARTIFACT_ID, "1.0.0").startRunning());
        Assert.assertEquals(1, refreshStatus.getCollection().countDocuments());
        refreshStatus.createOrUpdate(refreshStatus.get(TEST_GROUP_ID, TEST_ARTIFACT_ID, "1.0.0").stopRunning(null));
        Assert.assertEquals(1, refreshStatus.getCollection().countDocuments());

        Assert.assertNotNull(refreshStatus.get(TEST_GROUP_ID, TEST_ARTIFACT_ID, "1.0.0"));
        Assert.assertFalse(refreshStatus.get(TEST_GROUP_ID, TEST_ARTIFACT_ID, "1.0.0").isRunning());

        refreshStatus.createOrUpdate(refreshStatus.get(TEST_GROUP_ID, TEST_ARTIFACT_ID, MASTER_SNAPSHOT).startRunning());
        Assert.assertEquals(2, refreshStatus.getCollection().countDocuments());
        Assert.assertNotNull(refreshStatus.get(TEST_GROUP_ID, TEST_ARTIFACT_ID, "1.0.0"));
        Assert.assertTrue(refreshStatus.get(TEST_GROUP_ID, TEST_ARTIFACT_ID, MASTER_SNAPSHOT).isRunning());

    }

    @Test
    public void canRetrieveStatusFromToDate()
    {
        RefreshStatus status1 = new RefreshStatus("test1.org", "TEST", "1.0.0");
        RefreshStatus status2 = new RefreshStatus("test2.org", "TEST", "1.0.0");
        RefreshStatus status3 = new RefreshStatus("test3.org", "TEST", "1.0.0");
        RefreshStatus status4 = new RefreshStatus("test4.org", "TEST", "1.0.0");

        LocalDateTime aPointInTime = LocalDateTime.of(2022, 9, 12, 12, 0).minusDays(12);
        refreshStatus.createOrUpdate(status1.withStartTime(toDate(aPointInTime)));
        refreshStatus.createOrUpdate(status2.withStartTime(toDate(aPointInTime.plusHours(1))));
        refreshStatus.createOrUpdate(status3.withStartTime(toDate(aPointInTime.plusHours(1).plusMinutes(22))));
        refreshStatus.createOrUpdate(status4.withStartTime(toDate(aPointInTime.plusHours(2).plusMinutes(35))));

        List<RefreshStatus> allstatuss = refreshStatus.find(null,null,null,null);
        Assert.assertNotNull(allstatuss);
        Assert.assertEquals(4, allstatuss.size());
        List<RefreshStatus> statusesBeforeLunch = refreshStatus.find(null,null,null,null,null,null,aPointInTime.toLocalDate().atStartOfDay(), aPointInTime);
        Assert.assertNotNull(statusesBeforeLunch);
        Assert.assertEquals(1, statusesBeforeLunch.size());
        Assert.assertEquals("test1.org", statusesBeforeLunch.get(0).getGroupId());

        List<RefreshStatus> afterLunch = refreshStatus.find(null,null,null,null,null,null,aPointInTime.withHour(12).withMinute(0).withSecond(1), null);
        Assert.assertNotNull(afterLunch);
        Assert.assertEquals(3, afterLunch.size());

        List<RefreshStatus> statusInBetween = refreshStatus.find(null,null,null,null,null,null,aPointInTime.plusHours(1).plusMinutes(1),aPointInTime.plusHours(2));
        Assert.assertNotNull(statusInBetween);
        Assert.assertEquals(1, statusInBetween.size());
        Assert.assertEquals("test3.org", statusInBetween.get(0).getGroupId());


    }

    @Test
    public void testFindByStatus()
    {
        Assert.assertEquals(0, refreshStatus.getCollection().countDocuments());
        refreshStatus.createOrUpdate(refreshStatus.get(TEST_GROUP_ID, TEST_ARTIFACT_ID, "1.0.0").stopRunning(new MetadataEventResponse()));
        refreshStatus.createOrUpdate(refreshStatus.get(TEST_GROUP_ID, TEST_ARTIFACT_ID, "1.0.1").stopRunning(new MetadataEventResponse().addError("it failed")));
        Assert.assertEquals(2, refreshStatus.getCollection().countDocuments());

        Assert.assertEquals(2,refreshStatus.find(TEST_GROUP_ID,TEST_ARTIFACT_ID,null).size());
        Assert.assertEquals("1.0.0",refreshStatus.find(TEST_GROUP_ID,TEST_ARTIFACT_ID,null,null,null,true,null,null).get(0).getVersionId());
        Assert.assertEquals("1.0.1",refreshStatus.find(TEST_GROUP_ID,TEST_ARTIFACT_ID,null,null,null,false,null,null).get(0).getVersionId());

    }


    @Test
    public void testFindByParentId()
    {
        Assert.assertEquals(0, refreshStatus.getCollection().countDocuments());
        refreshStatus.createOrUpdate(new RefreshStatus(TEST_GROUP_ID,TEST_ARTIFACT_ID,"0.0.0").setParentEventId("test"));
        refreshStatus.createOrUpdate(new RefreshStatus(TEST_GROUP_ID,TEST_ARTIFACT_ID,"0.0.1").setParentEventId("test"));
        refreshStatus.createOrUpdate(new RefreshStatus(TEST_GROUP_ID,TEST_ARTIFACT_ID,"0.0.2").setParentEventId("test"));
        refreshStatus.createOrUpdate(new RefreshStatus(TEST_GROUP_ID,TEST_ARTIFACT_ID,"0.0.3").setParentEventId("test1"));
        Assert.assertEquals(4, refreshStatus.getCollection().countDocuments());

        Assert.assertEquals(3,refreshStatus.find(null,null,null,"test",null,null,null,null).size());
        Assert.assertEquals(1,refreshStatus.find(null,null,null,"test1",null,null,null,null).size());


    }
}
