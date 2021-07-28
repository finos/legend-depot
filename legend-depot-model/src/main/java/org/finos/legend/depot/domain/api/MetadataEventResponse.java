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

package org.finos.legend.depot.domain.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.finos.legend.depot.domain.api.status.MetadataEventStatus;
import org.finos.legend.depot.domain.status.StoreOperationResult;

import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MetadataEventResponse
{

    @JsonProperty
    private List<String> messages = new ArrayList<>();
    @JsonProperty
    private List<String> errors = new ArrayList<>();

    @JsonProperty(value = "status")
    public MetadataEventStatus getStatus()
    {
        return !errors.isEmpty() ? MetadataEventStatus.FAILED : MetadataEventStatus.SUCCESS;
    }

    @Override
    public String toString()
    {
        return "MetadataEventResponse{" +
                "messages=" + messages +
                ", errors=" + errors +
                '}';
    }

    public List<String> getErrors()
    {
        return errors;
    }

    public List<String> getMessages()
    {
        return messages;
    }

    public MetadataEventResponse addMessage(String message)
    {
        this.messages.add(message);
        return this;
    }

    public MetadataEventResponse addMessages(List<String> messages)
    {
        this.messages.addAll(messages);
        return this;
    }

    public void logError(String error)
    {
        this.errors.add(error);
    }

    public boolean hasErrors()
    {
        return getErrors() != null && !getErrors().isEmpty();
    }

    public MetadataEventResponse combine(StoreOperationResult storeResult)
    {
        this.errors.addAll(storeResult.getErrors());
        addMessage(storeResult.toString());
        return this;
    }

    public MetadataEventResponse combine(MetadataEventResponse eventResponse)
    {
        this.errors.addAll(eventResponse.getErrors());
        this.addMessages(eventResponse.getMessages());
        return this;
    }
}
