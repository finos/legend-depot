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

package org.finos.legend.depot.store.mongo.core;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoDatabase;
import com.mongodb.management.JMXConnectionPoolListener;

public abstract class AbstractMongoConnectionFactory implements ConnectionFactory
{
    private final String mongoURI;
    private final String applicationName;
    private final String databaseName;
    protected MongoClient client;

    public AbstractMongoConnectionFactory(String applicationName, MongoConfiguration mongoConfiguration)
    {
        if (mongoConfiguration == null || isNullOrEmpty(mongoConfiguration.database) || isNullOrEmpty(mongoConfiguration.url))
        {
            throw new IllegalArgumentException("Invalid mongo configuration provided");
        }

        this.applicationName = applicationName;
        this.databaseName = mongoConfiguration.database;
        this.mongoURI = mongoConfiguration.url;
    }

    private boolean isNullOrEmpty(String string)
    {
        return string == null || string.isEmpty();
    }

    protected MongoClientURI buildMongoURI()
    {
        MongoClientOptions.Builder optionsBuilder = MongoClientOptions.builder().applicationName(applicationName);
        optionsBuilder.addConnectionPoolListener(new JMXConnectionPoolListener());
        return new MongoClientURI(mongoURI, optionsBuilder);
    }

    public String getMongoURI()
    {
        return mongoURI;
    }

    public String getApplicationName()
    {
        return applicationName;
    }

    @Override
    public MongoDatabase getDatabase()
    {
        return this.client.getDatabase(this.databaseName);
    }

    @Override
    public MongoClient getClient()
    {
        return client;
    }

}
