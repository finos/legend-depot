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

package org.finos.legend.depot.core.server;

import org.finos.legend.server.pac4j.internal.AcceptHeaderAjaxRequestResolver;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.pac4j.core.context.CallContext;
import org.pac4j.core.context.WebContext;

import java.lang.reflect.Proxy;
import java.util.Optional;

public class TestAcceptHeaderAjaxRequestResolver
{
    private static boolean isAjax(String accept)
    {
        WebContext context = (WebContext) Proxy.newProxyInstance(WebContext.class.getClassLoader(), new Class<?>[]{WebContext.class},
                (proxy, method, args) -> "getRequestHeader".equals(method.getName()) ? Optional.of(accept) : null);
        return new AcceptHeaderAjaxRequestResolver().isAjax(new CallContext(context, null));
    }

    @Test
    public void browserRequestIsNotAjax()
    {
        Assertions.assertFalse(isAjax("text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8"));
    }

    @Test
    public void apiRequestIsAjax()
    {
        Assertions.assertTrue(isAjax("application/json"));
    }
}
