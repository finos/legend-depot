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

package org.finos.legend.depot.services.api.entities;

import org.finos.legend.depot.domain.entity.DepotEntity;
import org.finos.legend.depot.domain.entity.DepotEntityOverview;
import org.finos.legend.depot.domain.project.ProjectVersion;
import org.finos.legend.depot.domain.version.Scope;

import java.util.List;

public interface EntityClassifierService
{
    List<DepotEntity> getEntitiesByClassifierPath(String classifierPath, String search, Integer limit, Scope scope, boolean latestVersion);

    List<DepotEntity> findClassifierEntities(String classifier, Scope scope);

    List<DepotEntity> findClassifierEntitiesByVersions(String classifier, List<ProjectVersion> projectVersions);

    List<DepotEntityOverview> findClassifierSummaries(String classifier, Scope scope);

    List<DepotEntityOverview> findClassifierSummariesByVersions(String classifier, List<ProjectVersion> projectVersions);

    List<DepotEntity> findClassifierEntities(String classifier, Scope scope, String search, Integer limit);

    List<DepotEntity> findClassifierEntitiesByVersions(String classifier, List<ProjectVersion> projectVersions, String search, Integer limit);
}
