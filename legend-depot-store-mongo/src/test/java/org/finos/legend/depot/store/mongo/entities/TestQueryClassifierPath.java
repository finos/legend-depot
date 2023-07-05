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

package org.finos.legend.depot.store.mongo.entities;

import org.eclipse.collections.api.factory.Lists;
import org.finos.legend.depot.domain.project.ProjectVersion;
import org.finos.legend.depot.store.mongo.TestStoreMongo;
import org.junit.Assert;
import org.junit.Test;

import java.net.URL;

import static org.finos.legend.depot.domain.version.VersionValidator.BRANCH_SNAPSHOT;

public class TestQueryClassifierPath extends TestStoreMongo
{
    private static final URL ENTITIES_FILE = TestUpdateVersions.class.getClassLoader().getResource("data/classifiers.json");
    private EntitiesMongo mongo = new EntitiesMongo(mongoProvider);


    @Test
    public void canQuerySnapshotEntitiesByClassifier()
    {
        String CPATH = "meta::pure::metamodel::extension::Profile";
        setUpEntitiesDataFromFile(ENTITIES_FILE);
        Assert.assertEquals(3, mongo.findLatestEntitiesByClassifier(CPATH, null, null, false).size());
        Assert.assertEquals(2, mongo.findLatestEntitiesByClassifier(CPATH, null, 2, false).size());
        Assert.assertEquals(1, mongo.findLatestEntitiesByClassifier(CPATH, "TestProfileTwo", 2, false).size());
    }

    @Test
    public void canQueryVersionEntitiesByClassifier()
    {
        String CPATH = "meta::pure::metamodel::extension::Profile";
        setUpEntitiesDataFromFile(ENTITIES_FILE);
        Assert.assertEquals(8, mongo.findReleasedEntitiesByClassifier(CPATH, null, null, null, false).size());
        Assert.assertEquals(2, mongo.findReleasedEntitiesByClassifier(CPATH, null, null, 2, false).size());
        Assert.assertEquals(4, mongo.findReleasedEntitiesByClassifier(CPATH, "TestProfileTwo", null, null, false).size());
        Assert.assertEquals(8, mongo.findReleasedEntitiesByClassifier(CPATH, "TestProfile", null, null, false).size());
        Assert.assertEquals(0, mongo.findReleasedEntitiesByClassifier(CPATH, null, Lists.fixedSize.of(new ProjectVersion("examples.metadata", "test", "1.0.0")), null, false).size());
        Assert.assertEquals(2, mongo.findReleasedEntitiesByClassifier(CPATH, null, Lists.fixedSize.of(new ProjectVersion("examples.metadata", "test", "2.2.0")), null, false).size());
        Assert.assertEquals(1, mongo.findReleasedEntitiesByClassifier(CPATH, null, Lists.fixedSize.of(new ProjectVersion("examples.metadata", "test", "2.3.0")), null, false).size());
        Assert.assertEquals(2, mongo.findReleasedEntitiesByClassifier(CPATH, null, Lists.fixedSize.of(new ProjectVersion("examples.metadata", "test2", "2.3.0")), null, false).size());
        Assert.assertEquals(3, mongo.findReleasedEntitiesByClassifier(CPATH, null, Lists.fixedSize.of(new ProjectVersion("examples.metadata", "test", "2.3.0"), new ProjectVersion("examples.metadata", "test2", "2.3.0")), null, false).size());
        Assert.assertEquals(1, mongo.findReleasedEntitiesByClassifier(CPATH, "TestProfileTwo", Lists.fixedSize.of(new ProjectVersion("examples.metadata", "test", "2.3.0")), null, false).size());
        Assert.assertEquals(1, mongo.findReleasedEntitiesByClassifier(CPATH, "TestProfileTwo", Lists.fixedSize.of(new ProjectVersion("examples.metadata", "test2", "2.3.0")), null, false).size());
        Assert.assertEquals(2, mongo.findReleasedEntitiesByClassifier(CPATH, "TestProfileTwo", Lists.fixedSize.of(new ProjectVersion("examples.metadata", "test", "2.3.0"), new ProjectVersion("examples.metadata", "test2", "2.3.0")), null, false).size());
    }
}
