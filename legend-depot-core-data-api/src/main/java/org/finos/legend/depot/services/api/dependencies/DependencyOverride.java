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

package org.finos.legend.depot.services.api.dependencies;

import org.eclipse.collections.api.block.function.Function2;
import org.finos.legend.depot.domain.project.ProjectVersion;

import java.util.List;
import java.util.Set;

public interface DependencyOverride
{
    List<ProjectVersion> overrideWith(List<ProjectVersion> dependencies, List<ProjectVersion> overridingDependencies, Function2<List<ProjectVersion>, Boolean, Set<ProjectVersion>> executableFunction);
}
