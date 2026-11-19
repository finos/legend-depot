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

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.finos.legend.depot.store.StorageConfiguration;

import jakarta.validation.constraints.NotNull;

public class MongoConfiguration extends StorageConfiguration
{
    @NotNull
    @JsonProperty
    public String database;

    @NotNull
    @JsonProperty
    public String url;

    @NotNull
    @JsonProperty
    public boolean tracing;

    @JsonCreator
    public MongoConfiguration(@JsonProperty("database") String database, @JsonProperty("url") String url,@JsonProperty("tracing") boolean tracing)
    {
        super();
        this.database = database;
        this.url = url;
        this.tracing = tracing;
    }

    public String getDatabase()
    {
        return database;
    }

    public String getUrl()
    {
        return url;
    }

    public boolean isTracingEnabled()
    {
        return tracing;
    }
}
