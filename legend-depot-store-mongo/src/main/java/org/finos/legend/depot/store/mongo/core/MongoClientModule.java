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

import com.google.inject.PrivateModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Names;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import org.finos.legend.depot.store.StorageConfiguration;
import org.finos.legend.depot.core.services.api.tracing.configuration.OpenTracingConfiguration;
import org.finos.legend.depot.core.services.tracing.TracerFactory;

import jakarta.inject.Named;
import java.util.List;
import java.util.Optional;

public class MongoClientModule extends PrivateModule
{
    @Override
    protected void configure()
    {
        expose(ConnectionFactory.class);
        expose(MongoDatabase.class).annotatedWith(Names.named("mongoDatabase"));
        expose(MongoClient.class);
    }

    @Provides
    @Singleton
    MongoConfiguration getMongoConfiguration(List<StorageConfiguration> configurations)
    {
        Optional<StorageConfiguration> mongoConfiguration = configurations.stream().filter(configuration -> configuration != null && configuration instanceof MongoConfiguration).findFirst();
        if (mongoConfiguration.isPresent())
        {
            return (MongoConfiguration) mongoConfiguration.get();
        }
        else
        {
            throw new IllegalArgumentException("mongo configuration not provided");
        }
    }

    @Provides
    @Singleton
    ConnectionFactory getConnectionFactory(@Named("applicationName") String applicationName, MongoConfiguration mongoConfiguration, OpenTracingConfiguration openTracingConfiguration, TracerFactory tracerFactory)
    {
        if (openTracingConfiguration.isEnabled() && mongoConfiguration.isTracingEnabled())
        {
            return new MongoTracingConnectionFactory(applicationName, mongoConfiguration, tracerFactory.getTracer());
        }
        else
        {
            return new MongoNonTracingConnectionFactory(applicationName, mongoConfiguration);
        }
    }

    @Provides
    @Named("mongoDatabase")
    public MongoDatabase getMongoDatabase(ConnectionFactory connectionFactory)
    {
        return connectionFactory.getDatabase();
    }

    @Provides
    @Singleton
    public MongoClient getMongoClient(ConnectionFactory connectionFactory)
    {
        return connectionFactory.getClient();
    }

}
