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
import org.finos.legend.depot.store.artifacts.domain.status.RefreshStatus;
import org.finos.legend.depot.store.mongo.TestStoreMongo;
import org.junit.Assert;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.List;

public class TestMongoStatus extends TestStoreMongo
{

    public static final String TEST_ARTIFACT_ID = "test";
    public static final String TEST_GROUP_ID = "group.example";
    private MongoRefreshStatus refreshStatus = new MongoRefreshStatus(mongoProvider);

    @Test
    public void testStatus()
    {
        Assert.assertEquals(0, refreshStatus.getCollection().countDocuments());
        refreshStatus.createOrUpdate(refreshStatus.get(VersionRevision.VERSIONS, TEST_GROUP_ID, TEST_ARTIFACT_ID, "1.0.0").withRunning(true));
        Assert.assertEquals(1, refreshStatus.getCollection().countDocuments());
        refreshStatus.createOrUpdate(refreshStatus.get(VersionRevision.VERSIONS, TEST_GROUP_ID, TEST_ARTIFACT_ID, "1.0.0").withRunning(false));
        Assert.assertEquals(1, refreshStatus.getCollection().countDocuments());

        Assert.assertNotNull(refreshStatus.get(VersionRevision.VERSIONS, TEST_GROUP_ID, TEST_ARTIFACT_ID, "1.0.0"));
        Assert.assertFalse(refreshStatus.get(VersionRevision.VERSIONS, TEST_GROUP_ID, TEST_ARTIFACT_ID, "1.0.0").isRunning());

        refreshStatus.createOrUpdate(refreshStatus.get(VersionRevision.REVISIONS, TEST_GROUP_ID, TEST_ARTIFACT_ID, "1.0.0").withRunning(true));
        Assert.assertEquals(2, refreshStatus.getCollection().countDocuments());
        Assert.assertNotNull(refreshStatus.get(VersionRevision.REVISIONS, TEST_GROUP_ID, TEST_ARTIFACT_ID, "1.0.0"));
        Assert.assertTrue(refreshStatus.get(VersionRevision.REVISIONS, TEST_GROUP_ID, TEST_ARTIFACT_ID, "1.0.0").isRunning());

    }

    @Test
    public void canRetrieveStatusFromToDate()
    {
        RefreshStatus status1 = new RefreshStatus(VersionRevision.VERSIONS.name(), "test1.org", "TEST", "1.0.0");
        RefreshStatus status2 = new RefreshStatus(VersionRevision.VERSIONS.name(), "test2.org", "TEST", "1.0.0");
        RefreshStatus status3 = new RefreshStatus(VersionRevision.VERSIONS.name(), "test3.org", "TEST", "1.0.0");
        RefreshStatus status4 = new RefreshStatus(VersionRevision.VERSIONS.name(), "test4.org", "TEST", "1.0.0");

        LocalDateTime aPointInTime = LocalDateTime.of(2022, 9, 12, 12, 0).minusDays(12);
        refreshStatus.createOrUpdate(status1.withStartTime(toDate(aPointInTime)));
        refreshStatus.createOrUpdate(status2.withStartTime(toDate(aPointInTime.plusHours(1))));
        refreshStatus.createOrUpdate(status3.withStartTime(toDate(aPointInTime.plusHours(1).plusMinutes(22))));
        refreshStatus.createOrUpdate(status4.withStartTime(toDate(aPointInTime.plusHours(2).plusMinutes(35))));

        List<RefreshStatus> allstatuss = refreshStatus.find(null,null,null,null,null);
        Assert.assertNotNull(allstatuss);
        Assert.assertEquals(4, allstatuss.size());
        List<RefreshStatus> statusesBeforeLunch = refreshStatus.find(null,null,null,null,null,aPointInTime.toLocalDate().atStartOfDay(), aPointInTime);
        Assert.assertNotNull(statusesBeforeLunch);
        Assert.assertEquals(1, statusesBeforeLunch.size());
        Assert.assertEquals("test1.org", statusesBeforeLunch.get(0).getGroupId());

        List<RefreshStatus> afterLunch = refreshStatus.find(null,null,null,null,null,aPointInTime.withHour(12).withMinute(0).withSecond(1), null);
        Assert.assertNotNull(afterLunch);
        Assert.assertEquals(3, afterLunch.size());

        List<RefreshStatus> statusInBetween = refreshStatus.find(null,null,null,null,null,aPointInTime.plusHours(1).plusMinutes(1),aPointInTime.plusHours(2));
        Assert.assertNotNull(statusInBetween);
        Assert.assertEquals(1, statusInBetween.size());
        Assert.assertEquals("test3.org", statusInBetween.get(0).getGroupId());


    }
}
