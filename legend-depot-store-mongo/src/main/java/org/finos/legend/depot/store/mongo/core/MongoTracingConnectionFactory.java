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

import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import io.opentracing.Tracer;

import javax.inject.Singleton;

@Singleton
public class MongoTracingConnectionFactory extends AbstractMongoConnectionFactory
{

    public MongoTracingConnectionFactory(String applicationName, MongoConfiguration mongoConfiguration, Tracer tracer)
    {
        super(applicationName, mongoConfiguration);
        this.client = initClient(tracer);
    }

    private MongoClient initClient(Tracer tracer)
    {
        MongoClientSettings mongoClientSettings = super.buildMongoClientSettings();
        if (getMongoURI().contains("+srv://"))
        {
            // SRV doesn't work with TracingMongoClient, and it seems that it can't be supported without
            // changes to the Mongo driver
            // For now, don't use Tracing if we're using SRV
            return MongoClients.create(mongoClientSettings);
        }
        MongoClientSettings tracerMongoSettings = MongoClientSettings.builder(mongoClientSettings).addCommandListener(new TracerCommandListener(tracer)).build();
        return MongoClients.create(tracerMongoSettings);
    }
}
