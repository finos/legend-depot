//  Copyright 2022 Goldman Sachs
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

package org.finos.legend.depot.core.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;

import javax.ws.rs.ext.ContextResolver;
import java.text.SimpleDateFormat;

public class BaseServerJacksonJsonProvider extends JacksonJsonProvider implements ContextResolver<ObjectMapper>
{
    public static final String SIMPLE_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    protected final ObjectMapper objectMapper;

    public BaseServerJacksonJsonProvider()
    {
        objectMapper = new ObjectMapper().setDateFormat(new SimpleDateFormat(SIMPLE_DATE_FORMAT));
    }

    @Override
    public ObjectMapper getContext(Class<?> type)
    {
        return objectMapper;
    }
}
