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

package org.finos.legend.depot.store.mongo.admin;

import org.bson.Document;
import org.finos.legend.depot.store.mongo.TestStoreMongo;
import org.finos.legend.depot.store.mongo.core.BaseMongo;
import org.finos.legend.depot.store.mongo.projects.ProjectsMongo;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class TestMongoAdminStore extends TestStoreMongo
{
    MongoAdminStore mongoAdminStore = new MongoAdminStore(mongoProvider);


    @Test
    public void canCreateProjectsIndexesIfAbsent()
    {
        List<Document> indexes = new ArrayList<>();
        this.mongoProvider.getCollection(ProjectsMongo.COLLECTION).listIndexes().forEach((Consumer<Document>) indexes::add);
        Assert.assertTrue(indexes.isEmpty());

        List<String> result = BaseMongo.createIndexesIfAbsent(mongoProvider, ProjectsMongo.COLLECTION, ProjectsMongo.buildIndexes());
        Assert.assertFalse(result.isEmpty());

        List indexes1 = new ArrayList();
        this.mongoProvider.getCollection(ProjectsMongo.COLLECTION).listIndexes().forEach((Consumer<Document>) indexes1::add);
        Assert.assertFalse(indexes1.isEmpty());
        Assert.assertEquals(2, indexes1.size());
    }

}
