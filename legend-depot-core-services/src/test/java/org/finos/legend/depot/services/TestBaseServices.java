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

package org.finos.legend.depot.services;

import com.squarespace.jersey2.guice.JerseyGuiceUtils;
import org.finos.legend.depot.store.api.projects.UpdateProjects;
import org.finos.legend.depot.store.api.projects.UpdateProjectsVersions;
import org.finos.legend.depot.store.mongo.TestStoreMongo;
import org.finos.legend.depot.store.mongo.projects.ProjectsMongo;
import org.finos.legend.depot.store.mongo.projects.ProjectsVersionsMongo;
import org.junit.Before;

public class TestBaseServices extends TestStoreMongo
{
    protected UpdateProjectsVersions projectsVersionsStore = new ProjectsVersionsMongo(mongoProvider);
    protected UpdateProjects projectsStore = new ProjectsMongo(mongoProvider);

    static
    {
        JerseyGuiceUtils.install((s, serviceLocator) -> null);
    }

    @Before
    public void setUpData()
    {
        setUpProjectsVersionsFromFile(TestStoreMongo.class.getClassLoader().getResource("data/projectsVersions.json"));
        setUpProjectsFromFile(TestStoreMongo.class.getClassLoader().getResource("data/projects.json"));
    }

}

