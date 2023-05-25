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

package org.finos.legend.depot.server.guice;

import com.google.inject.PrivateModule;
import org.finos.legend.depot.server.resources.deprecated.DeprecatedDependenciesAPIsResource;
import org.finos.legend.depot.server.resources.deprecated.DeprecatedEntitiesAPIsResource;
import org.finos.legend.depot.server.resources.deprecated.DeprecatedFileGenerationAPIsResource;
import org.finos.legend.depot.server.resources.deprecated.DeprecatedProjectAPIsResource;

public class DepotServerDeprecatedResourcesModule extends PrivateModule
{
    @Override
    protected void configure()
    {

        bind(DeprecatedProjectAPIsResource.class);
        bind(DeprecatedEntitiesAPIsResource.class);
        bind(DeprecatedFileGenerationAPIsResource.class);
        bind(DeprecatedDependenciesAPIsResource.class);

        expose(DeprecatedProjectAPIsResource.class);
        expose(DeprecatedEntitiesAPIsResource.class);
        expose(DeprecatedFileGenerationAPIsResource.class);
        expose(DeprecatedDependenciesAPIsResource.class);
    }
}