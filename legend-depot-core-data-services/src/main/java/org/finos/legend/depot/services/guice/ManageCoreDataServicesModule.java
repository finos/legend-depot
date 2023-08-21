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

import org.finos.legend.depot.services.VersionsMismatchService;
import org.finos.legend.depot.services.api.dependencies.ManageDependenciesService;
import org.finos.legend.depot.services.api.projects.ManageProjectsService;
import org.finos.legend.depot.services.api.projects.ProjectsVersionsReconciliationService;
import org.finos.legend.depot.services.dependencies.ManageDependenciesServiceImpl;
import org.finos.legend.depot.services.projects.ManageProjectsServiceImpl;

public class ManageCoreDataServicesModule extends CoreDataServicesModule
{

    @Override
    protected void configure()
    {
        super.configure();

        bind(ManageProjectsService.class).to(ManageProjectsServiceImpl.class);
        bind(ManageDependenciesService.class).to(ManageDependenciesServiceImpl.class);
        bind(ProjectsVersionsReconciliationService.class).to(VersionsMismatchService.class);

        expose(ManageProjectsService.class);
        expose(ManageDependenciesService.class);
        expose(ProjectsVersionsReconciliationService.class);
    }

}
