// Copyright 2026 Goldman Sachs
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package org.finos.legend.depot.core.server.error;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Instant;

public class TestExtendedErrorMessage
{
    @Test
    public void timestampKeepsItsBeanFormWithoutTheJavaTimeModule() throws Exception
    {
        ExtendedErrorMessage message = ExtendedErrorMessage.newExtendedErrorMessage(403, "denied", null, null, Instant.ofEpochSecond(1760000000L, 123000000L));
        JsonNode json = new ObjectMapper().valueToTree(message);
        Assertions.assertEquals(403, json.get("code").asInt());
        Assertions.assertEquals("denied", json.get("message").asText());
        Assertions.assertEquals(1760000000L, json.at("/timestamp/epochSecond").asLong());
        Assertions.assertEquals(123000000, json.at("/timestamp/nano").asInt());
    }
}
