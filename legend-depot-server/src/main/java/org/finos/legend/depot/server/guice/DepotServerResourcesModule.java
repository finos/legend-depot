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
import org.finos.legend.depot.server.resources.ProjectsResource;
import org.finos.legend.depot.server.resources.ProjectsVersionsResource;
import org.finos.legend.depot.server.resources.dependencies.DependenciesResource;
import org.finos.legend.depot.server.resources.entities.EntitiesResource;
import org.finos.legend.depot.server.resources.entities.EntityClassifierResource;


public class DepotServerResourcesModule extends PrivateModule
{
    @Override
    protected void configure()
    {
        bind(ProjectsResource.class);
        bind(ProjectsVersionsResource.class);
        bind(EntitiesResource.class);
        bind(EntityClassifierResource.class);
        bind(DependenciesResource.class);


        expose(ProjectsResource.class);
        expose(ProjectsVersionsResource.class);
        expose(EntityClassifierResource.class);
        expose(EntitiesResource.class);
        expose(DependenciesResource.class);

    }
}