//  Copyright 2026 Goldman Sachs
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

package org.finos.legend.depot.services.dependencies;

import org.finos.legend.depot.domain.project.ProjectVersion;
import org.finos.legend.depot.services.api.dependencies.DependencyConflict;
import org.finos.legend.depot.services.api.dependencies.DependencyResponseModel;
import org.logicng.solvers.maxsat.algorithms.MaxSAT;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class DependencyResolutionFailureAnalyzer
{

    public static DependencyResponseModel analyzeAndReportFailure(MaxSAT.MaxSATResult satResult,
                                                                  LogicNGSATResult logicNGSATResult,
                                                                  List<ProjectVersion> originalRequirements)
    {
        DependencyResponseModel response = new DependencyResponseModel();

        // Set the failure reason
        response.setFailureReason(buildFailureReason(satResult));

        // Extract and add conflicting transitive dependencies (excluding root projects)
        if (satResult == MaxSAT.MaxSATResult.UNSATISFIABLE && logicNGSATResult != null)
        {
            addConflictingDependencies(response, logicNGSATResult, originalRequirements);
        }

        return response;
    }

    private static String buildFailureReason(MaxSAT.MaxSATResult satResult)
    {
        switch (satResult)
        {
            case UNSATISFIABLE:
                return "Dependency resolution failed: Version requirements are unsatisfiable.";
            case UNDEF:
                return "Dependency resolution failed: Unable to determine a solution within the given constraints.";
            default:
                return "Dependency resolution failed: Unexpected error during resolution.";
        }
    }

    private static void addConflictingDependencies(DependencyResponseModel response,
                                                   LogicNGSATResult logicNGSATResult,
                                                   List<ProjectVersion> originalRequirements)
    {
        Map<String, Set<ProjectVersion>> conflictingVersionsByGA = logicNGSATResult.getConflictingVersionsByGA();
        Map<ProjectVersion, Set<ProjectVersion>> dependencyOrigins = logicNGSATResult.getDependencyOrigins();

        // Build set of root project keys to filter them out
        Set<String> rootProjectKeys = originalRequirements.stream()
                .map(pv -> pv.getGa())
                .collect(Collectors.toSet());

        conflictingVersionsByGA.forEach((projectKey, conflictingVersions) ->
        {
            String[] parts = projectKey.split(":");
            if (parts.length < 2)
            {
                return;
            }

            // Skip if this is a root project - we only want transitive dependency conflicts
            if (rootProjectKeys.contains(projectKey))
            {
                return;
            }

            String groupId = parts[0];
            String artifactId = parts[1];

            // Collect version origins and all unique origin GAs
            Map<ProjectVersion, Set<ProjectVersion>> versionOrigins = new HashMap<>();
            Set<String> allOriginGAs = new HashSet<>();

            conflictingVersions.forEach(version ->
            {
                Set<ProjectVersion> origins = dependencyOrigins.getOrDefault(version, Collections.emptySet());
                versionOrigins.put(version, origins);
                origins.forEach(origin -> allOriginGAs.add(origin.getGa()));
            });

            if (versionOrigins.size() > 1 && allOriginGAs.size() > 1) // Only report if there are multiple conflicting versions from different origins
            {
                DependencyConflict conflict = new DependencyConflict(groupId, artifactId);
                versionOrigins.forEach((version, origins) ->
                        conflict.addConflictingVersion(version.getVersionId(), new ArrayList<>(origins)));
                response.addConflict(conflict);
            }
        });
    }

}
