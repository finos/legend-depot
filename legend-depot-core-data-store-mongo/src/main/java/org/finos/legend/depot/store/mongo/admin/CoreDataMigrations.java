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

package org.finos.legend.depot.store.mongo.admin;

import com.mongodb.client.MongoDatabase;
import org.finos.legend.depot.store.mongo.admin.migrations.DependenciesMigration;
import org.finos.legend.depot.store.mongo.admin.migrations.MongoMigrations;
import org.finos.legend.depot.store.mongo.admin.migrations.ProjectToProjectVersionMigration;

import jakarta.inject.Inject;
import jakarta.inject.Named;

@Deprecated
public final class CoreDataMigrations extends MongoMigrations
{
    @Inject
    public CoreDataMigrations(@Named("mongoDatabase") MongoDatabase mongoDatabase)
    {
        super(mongoDatabase);
    }

    @Deprecated
    public void migrationToProjectVersions()
    {
        new ProjectToProjectVersionMigration(mongoDatabase).migrationToProjectVersions();
    }

    @Deprecated
    public void cleanUpProjectData()
    {
        new ProjectToProjectVersionMigration(mongoDatabase).cleanUpProjectData();
    }

    @Deprecated
    public void calculateTransitiveDependenciesForAllProjectVersions()
    {
        new DependenciesMigration(mongoDatabase).calculateTransitiveDependenciesForAllProjectVersions();
    }

    @Deprecated
    public void addTransitiveDependenciesToVersionData()
    {
        new DependenciesMigration(mongoDatabase).addTransitiveDependenciesToVersionData();
    }

    @Deprecated
    public void addLatestVersionToProjectData()
    {
        new ProjectToProjectVersionMigration(mongoDatabase).addLatestVersionToProjectData();
    }
}

