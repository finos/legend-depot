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

package org.finos.legend.depot.domain.status;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class StoreOperationResult
{
    private long modifiedCount = 0;
    private long insertedCount = 0;
    private long deletionCount = 0;
    private List<String> errors = new ArrayList<>();

    public StoreOperationResult()
    {
    }

    @JsonCreator
    public StoreOperationResult(@JsonProperty("modifiedCount") long modifiedCount, @JsonProperty("insertedCount") long insertedCount, @JsonProperty("deletionCount") long deletionCount, @JsonProperty("errors") List<String> errors)
    {
        this.modifiedCount = modifiedCount;
        this.insertedCount = insertedCount;
        this.deletionCount = deletionCount;
        this.errors = errors;
    }

    public void addModifiedCount()
    {
        this.modifiedCount++;
    }

    public void addInsertedCount()
    {
        this.insertedCount++;
    }

    public void addDeletionCount()
    {
        this.deletionCount++;
    }

    public long getModifiedCount()
    {
        return modifiedCount;
    }

    public long getInsertedCount()
    {
        return insertedCount;
    }

    public long getDeletionCount()
    {
        return deletionCount;
    }

    public boolean hasErrors()
    {
        return !errors.isEmpty();
    }

    public void logError(String error)
    {
        this.errors.add(error);
    }

    public List<String> getErrors()
    {
        return errors;
    }

    @Override
    public String toString()
    {
        return String.format("inserted: [%s], modified:[%s], deleted:[%s] ", insertedCount, modifiedCount, deletionCount);
    }
}
