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

package org.finos.legend.depot.services.guice;

import com.google.inject.PrivateModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Names;
import org.finos.legend.depot.services.api.dependencies.DependencyOverride;
import org.finos.legend.depot.services.api.projects.ProjectsService;
import org.finos.legend.depot.services.dependencies.DependencyUtil;
import org.finos.legend.depot.services.projects.ProjectsServiceImpl;

import javax.inject.Named;

public class CoreDataServicesModule extends PrivateModule
{
    @Override
    protected void configure()
    {
        bind(ProjectsService.class).to(ProjectsServiceImpl.class);

        expose(ProjectsService.class);
        expose(DependencyOverride.class).annotatedWith(Names.named("dependencyOverride"));
    }

    @Provides
    @Named("dependencyOverride")
    @Singleton
    public DependencyOverride initialiseDependencyCache()
    {
        return new DependencyUtil();
    }

}
