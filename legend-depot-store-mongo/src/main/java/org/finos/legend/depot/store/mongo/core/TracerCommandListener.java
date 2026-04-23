//  Copyright 2025 Goldman Sachs
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

import com.mongodb.event.CommandListener;
import com.mongodb.event.CommandStartedEvent;
import com.mongodb.event.CommandSucceededEvent;
import com.mongodb.event.CommandFailedEvent;
import io.opentracing.Span;
import io.opentracing.Tracer;
import io.opentracing.tag.Tags;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TracerCommandListener implements CommandListener
{
    private final Tracer tracer;
    private final Map<Integer, Span> spanByRequestId = new ConcurrentHashMap<>();

    public TracerCommandListener(Tracer tracer)
    {
        this.tracer = tracer;
    }

    @Override
    public void commandStarted(CommandStartedEvent event)
    {
        // Start a child span under the current active span
        Span parent = tracer.activeSpan();
        Span span = tracer.buildSpan("mongo." + event.getCommandName())
                .asChildOf(parent)
                .withTag("db.system", "mongodb")
                .withTag("db.name", event.getDatabaseName())
                .withTag("db.statement", event.getCommand().toJson())
                .start();

        spanByRequestId.put(event.getRequestId(), span);
    }

    @Override
    public void commandSucceeded(CommandSucceededEvent event)
    {
        finishSpan(event.getRequestId(), null);
    }

    @Override
    public void commandFailed(CommandFailedEvent event)
    {
        finishSpan(event.getRequestId(), event.getThrowable());
    }

    private void finishSpan(int requestId, Throwable error)
    {
        Span span = spanByRequestId.remove(requestId);
        if (span != null)
        {
            if (error != null)
            {
                Tags.ERROR.set(span, true);
                span.log(Map.of(
                        "event", "error",
                        "error.object", error,
                        "message", error.getMessage()
                ));
            }
            span.finish();
        }
    }
}
