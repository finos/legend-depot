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

package org.finos.legend.depot.store.notifications.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class QueueManagerConfiguration
{
    private static final long TWENTY_SECONDS = 20 * 1000L;
    private static final long ONE_MINUTE = 60 * 1000L;
    private static final long DEFAULT_NUMBER_OF_QUEUE_WORKERS = 1;

    @JsonProperty
    long queueInterval = TWENTY_SECONDS;

    @JsonProperty
    long queueDelay = ONE_MINUTE;

    @JsonProperty
    long numberOfQueueWorkers = DEFAULT_NUMBER_OF_QUEUE_WORKERS;

    public long getQueueInterval()
    {
        return queueInterval;
    }

    public long getQueueDelay()
    {
        return queueDelay;
    }

    public void setQueueInterval(long queueInterval)
    {
        this.queueInterval = queueInterval;
    }

    public void setQueueDelay(long queueDelay)
    {
        this.queueDelay = queueDelay;
    }

    public long getNumberOfQueueWorkers()
    {
        return numberOfQueueWorkers;
    }

    public void setNumberOfQueueWorkers(long numberOfQueueWorkers)
    {
        this.numberOfQueueWorkers = numberOfQueueWorkers;
    }
}
