//  Copyright 2026 Goldman Sachs
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

package org.finos.legend.depot.server;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectWriter;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.ext.MessageBodyWriter;
import org.finos.legend.engine.protocol.pure.v1.model.context.PureModelContextData;
import org.finos.legend.engine.shared.core.ObjectMapperFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

/**
 * Writes PureModelContextData with legend-engine's Pure protocol mapper, so elements keep their protocol type names
 * ("profile", "class", ...) and null fields are left out.
 * <p>
 * For a PureModelContextData response JAX-RS has two candidate writers: this one, and Dropwizard's Jackson writer, which
 * is declared for Object and writes with Dropwizard's bootstrap mapper (no Pure protocol type names, nulls included).
 * JAX-RS prefers the writer whose declared type is closest to the entity's type, so it always picks this one.
 */
@Produces(MediaType.APPLICATION_JSON)
public class PureModelContextDataJsonWriter implements MessageBodyWriter<PureModelContextData>
{
    private final ObjectWriter writer = ObjectMapperFactory.getNewStandardObjectMapperWithPureProtocolExtensionSupports()
            .writer()
            .without(JsonGenerator.Feature.AUTO_CLOSE_TARGET);

    @Override
    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
    {
        return PureModelContextData.class.isAssignableFrom(type);
    }

    @Override
    public void writeTo(PureModelContextData pureModelContextData, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType,
                        MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException
    {
        writer.writeValue(entityStream, pureModelContextData);
    }
}
