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

package org.finos.legend.depot.store.admin.services;

import org.bson.Document;
import org.finos.legend.depot.store.admin.api.ManageStoreService;
import org.finos.legend.depot.store.admin.store.mongo.MongoAdminStore;

import javax.inject.Inject;
import java.util.List;

public class ManageStoreServiceImpl implements ManageStoreService
{

    private final MongoAdminStore admin;

    @Inject
    public ManageStoreServiceImpl(MongoAdminStore admin)
    {
        this.admin = admin;
    }

    @Override
    public List<String> getAllCollections()
    {
        return admin.getAllCollections();
    }

    @Override
    public List<Document> getAllIndexes()
    {
        return admin.getAllIndexes();
    }

    @Override
    public void deleteCollection(String collectionId)
    {
        admin.deleteCollection(collectionId);
    }

    @Override
    public void deleteIndex(String collectionId, String indexName)
    {
        admin.deleteIndex(collectionId, indexName);
    }
}
