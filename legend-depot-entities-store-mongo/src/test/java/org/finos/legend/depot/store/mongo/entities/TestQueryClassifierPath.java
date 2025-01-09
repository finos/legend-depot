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
import org.finos.legend.depot.store.mongo.entities.test.EntitiesMongoTestUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URL;

public class TestQueryClassifierPath extends TestStoreMongo
{
    private static final URL ENTITIES_FILE = TestUpdateVersions.class.getClassLoader().getResource("data/classifiers.json");
    private EntitiesMongo mongo = new EntitiesMongo(mongoProvider);

    @BeforeEach
    public void setUp()
    {
        new EntitiesMongoTestUtils(mongoProvider).loadEntities(ENTITIES_FILE);
    }

    @Test
    public void canQuerySnapshotEntitiesByClassifier()
    {
        String CPATH = "meta::pure::metamodel::extension::Profile";
        Assertions.assertEquals(3, mongo.findLatestClassifierEntities(CPATH).size());
        Assertions.assertEquals(3, mongo.findLatestClassifierSummaries(CPATH).size());
        Assertions.assertEquals(2, mongo.findClassifierEntitiesByVersions(CPATH, Lists.fixedSize.of(new ProjectVersion("examples.metadata", "test", "master-SNAPSHOT"))).size());
        Assertions.assertEquals(2, mongo.findClassifierSummariesByVersions(CPATH, Lists.fixedSize.of(new ProjectVersion("examples.metadata", "test", "master-SNAPSHOT"))).size());
        Assertions.assertEquals(1, mongo.findClassifierEntitiesByVersions(CPATH, Lists.fixedSize.of(new ProjectVersion("examples.metadata", "test", "master-SNAPSHOT")), "TestProfileTwo", 2).size());
        Assertions.assertEquals(2, mongo.findLatestClassifierEntities(CPATH, null, 2).size());
        Assertions.assertEquals(1, mongo.findLatestClassifierEntities(CPATH, "TestProfileTwo", 2).size());
    }

    @Test
    public void canQueryVersionEntitiesByClassifier()
    {
        String CPATH = "meta::pure::metamodel::extension::Profile";
        Assertions.assertEquals(6, mongo.findReleasedClassifierEntities(CPATH).size());
        Assertions.assertEquals(6, mongo.findReleasedClassifierSummaries(CPATH).size());
        Assertions.assertEquals(2, mongo.findReleasedClassifierEntities(CPATH, null, 2).size());
        Assertions.assertEquals(3, mongo.findReleasedClassifierEntities(CPATH, "TestProfileTwo", null).size());
        Assertions.assertEquals(6, mongo.findReleasedClassifierEntities(CPATH, "TestProfile", null).size());
        Assertions.assertEquals(0, mongo.findClassifierEntitiesByVersions(CPATH, Lists.fixedSize.of(new ProjectVersion("examples.metadata", "test", "1.0.0"))).size());
        Assertions.assertEquals(3, mongo.findClassifierEntitiesByVersions(CPATH, Lists.fixedSize.of(new ProjectVersion("examples.metadata", "test", "2.2.0"))).size());
        Assertions.assertEquals(3, mongo.findClassifierSummariesByVersions(CPATH, Lists.fixedSize.of(new ProjectVersion("examples.metadata", "test", "2.2.0"))).size());
        Assertions.assertEquals(1, mongo.findClassifierEntitiesByVersions(CPATH, Lists.fixedSize.of(new ProjectVersion("examples.metadata", "test", "2.3.0")), null, null).size());
        Assertions.assertEquals(2, mongo.findClassifierEntitiesByVersions(CPATH, Lists.fixedSize.of(new ProjectVersion("examples.metadata", "test2", "2.3.0")), null, null).size());
        Assertions.assertEquals(3, mongo.findClassifierEntitiesByVersions(CPATH, Lists.fixedSize.of(new ProjectVersion("examples.metadata", "test", "2.3.0"), new ProjectVersion("examples.metadata", "test2", "2.3.0")), null, null).size());
        Assertions.assertEquals(1, mongo.findClassifierEntitiesByVersions(CPATH, Lists.fixedSize.of(new ProjectVersion("examples.metadata", "test", "2.3.0")), "TestProfileTwo", null).size());
        Assertions.assertEquals(1, mongo.findClassifierEntitiesByVersions(CPATH, Lists.fixedSize.of(new ProjectVersion("examples.metadata", "test2", "2.3.0")), "TestProfileTwo", null).size());
        Assertions.assertEquals(2, mongo.findClassifierEntitiesByVersions(CPATH, Lists.fixedSize.of(new ProjectVersion("examples.metadata", "test", "2.3.0"), new ProjectVersion("examples.metadata", "test2", "2.3.0")), "TestProfileTwo", null).size());
    }
}
