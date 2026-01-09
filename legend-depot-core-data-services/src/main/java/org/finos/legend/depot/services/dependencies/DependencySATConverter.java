//  Copyright 2025 Goldman Sachs
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
import org.finos.legend.depot.domain.version.VersionValidator;
import org.finos.legend.depot.services.api.projects.ProjectsService;
import org.finos.legend.sdlc.domain.model.version.VersionId;
import org.logicng.formulas.Formula;
import org.logicng.formulas.FormulaFactory;
import org.logicng.formulas.Variable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;


public class DependencySATConverter
{
    private final FormulaFactory formulaFactory;
    private final Map<ProjectVersion, Set<ProjectVersion>> transitiveDependenciesMap = new HashMap<>();
    private final Map<String, Variable> variableMap = new HashMap<>();
    private final Map<Variable, ProjectVersion> reverseVariableMap = new HashMap<>();
    private final Map<String, Set<ProjectVersion>> conflictingVersionsByGA = new HashMap<>(); // tracks conflicting versions for each GA
    private final Map<ProjectVersion, Set<ProjectVersion>> dependencyOrigins = new HashMap<>(); // tracks which direct deps require each transitive dep

    public DependencySATConverter(FormulaFactory formulaFactory)
    {
        this.formulaFactory = formulaFactory;
    }

    public LogicNGSATResult convertToLogicNGFormulas(Map<String, Set<ProjectVersion>> alternativeVersions, ProjectsService projectsService)
    {
        List<Formula> clauses = new ArrayList<>();
        Map<Variable, Integer> weights = new HashMap<>();

        createVariables(alternativeVersions, projectsService);
        assignVersionWeights(weights);

        // Add dependency constraints with override handling
        addDependencyConstraintsWithOverrides(clauses, alternativeVersions, projectsService);

        // Add mutual exclusion constraints
        addMutualExclusionConstraints(clauses);

        // Add at-least-one constraints
        addAtLeastOneConstraints(clauses, alternativeVersions);

        return new LogicNGSATResult(clauses, variableMap, reverseVariableMap, formulaFactory, weights, conflictingVersionsByGA, dependencyOrigins);
    }

    private void assignVersionWeights(Map<Variable, Integer> weights)
    {
        // Group variables by project (groupId:artifactId)
        Map<String, List<Variable>> projectGroups = new HashMap<>();

        reverseVariableMap.forEach((variable, projectVersion) ->
        {
            String projectKey = projectVersion.getGa();
            projectGroups.computeIfAbsent(projectKey, k -> new ArrayList<>()).add(variable);
        });

        // Assign weights within each project group
        projectGroups.forEach((projectKey, variables) ->
        {
            if (variables.size() > 1)
            {
                List<Variable> sortedReleaseVersionsVariables = variables.stream().filter(v -> !VersionValidator.isSnapshotVersion(reverseVariableMap.get(v).getVersionId())).sorted(Comparator.comparing(v -> VersionId.parseVersionId(reverseVariableMap.get(v).getVersionId()))).collect(Collectors.toList());
                List<Variable> snapshotVersionsVariables = variables.stream().filter(v -> VersionValidator.isSnapshotVersion(reverseVariableMap.get(v).getVersionId())).collect(Collectors.toList());
                int size = sortedReleaseVersionsVariables.size();

                // Assign increasing weights (higher for newer versions)
                for (int i = 0; i < size; i++)
                {
                    weights.put(sortedReleaseVersionsVariables.get(i), i + 1);
                }
                // preference to snapshot versions - snapshot versions can only be dependencies to other snapshot versions
                snapshotVersionsVariables.forEach(v ->
                {
                    weights.put(v, size + 1);
                });
            }
        });
    }

    private void createVariables(Map<String, Set<ProjectVersion>> alternativeVersions, ProjectsService projectsService)
    {
        Set<ProjectVersion> allVersions = new HashSet<>();

        alternativeVersions.values().stream().flatMap(Set::stream).forEach(alt ->
        {
            allVersions.add(alt);
            Set<ProjectVersion> altDependencies = projectsService.getDependencies(Collections.singletonList(alt), true);
            this.transitiveDependenciesMap.put(alt, altDependencies);

            // Track which direct dependency requires each transitive dependency
            altDependencies.forEach(dep ->
            {
                dependencyOrigins.computeIfAbsent(dep, k -> new HashSet<>()).add(alt);
            });

            allVersions.addAll(altDependencies.stream().filter(dep -> !alternativeVersions.containsKey(dep.getGa()) || alternativeVersions.get(dep.getGa()).contains(dep)).collect(Collectors.toSet()));
        });

        // Create LogicNG variables for all versions
        allVersions.forEach(pv ->
        {
            String gavKey = pv.getGav();
            if (!variableMap.containsKey(gavKey))
            {
                Variable var = formulaFactory.variable(gavKey);
                variableMap.put(gavKey, var);
                reverseVariableMap.put(var, pv);
            }
        });
    }

    private void addDependencyConstraintsWithOverrides(List<Formula> clauses, Map<String, Set<ProjectVersion>> alternativeVersions, ProjectsService projectsService)
    {
        alternativeVersions.values().stream().flatMap(Set::stream).forEach(pv ->
        {
            Variable parentVar = variableMap.get(pv.getGav());
            Set<ProjectVersion> dependencies = this.transitiveDependenciesMap.get(pv);
            Set<ProjectVersion> potentiallyOverriddenDependencies = dependencies.stream().filter(dep -> !variableMap.containsKey(dep.getGav()) || alternativeVersions.containsKey(dep.getGa())).collect(Collectors.toSet());
            Set<ProjectVersion> dependenciesNotGuaranteed = potentiallyOverriddenDependencies.stream()
                    .flatMap(dep -> this.transitiveDependenciesMap.computeIfAbsent(dep, key -> projectsService.getDependencies(Collections.singletonList(key), true)).stream())
                    .collect(Collectors.toSet());
            dependenciesNotGuaranteed.addAll(potentiallyOverriddenDependencies);
            dependencies.forEach(dep ->
            {
                if (!dependenciesNotGuaranteed.contains(dep))
                {
                    // we don't allow dep to be an alternativeVersion since apriori we don't know what version is to be picked up
                    clauses.add(formulaFactory.or(parentVar.negate(), variableMap.get(dep.getGav())));
                }
            });
        });
    }

    private void addMutualExclusionConstraints(List<Formula> clauses)
    {
        Map<String, List<Variable>> projectGroups = new HashMap<>();

        // Include all variables from the variable map, grouped by project coordinates
        reverseVariableMap.forEach((variable, projectVersion) ->
        {
            String projectKey = projectVersion.getGa();
            projectGroups.computeIfAbsent(projectKey, k -> new ArrayList<>()).add(variable);
        });

        // Add mutual exclusion constraints for each project group
        projectGroups.values().forEach(versions ->
        {
            if (versions.size() > 1)
            {
                for (int i = 0; i < versions.size() - 1; i++)
                {
                    for (int j = i + 1; j < versions.size(); j++)
                    {
                        // ¬version1 ? ¬version2 (at most one version per project)
                        clauses.add(formulaFactory.or(versions.get(i).negate(), versions.get(j).negate()));

                        ProjectVersion pv1 = reverseVariableMap.get(versions.get(i));
                        ProjectVersion pv2 = reverseVariableMap.get(versions.get(j));

                        // Track conflicting versions for conflict analysis
                        String projectKey = pv1.getGa();
                        conflictingVersionsByGA.computeIfAbsent(projectKey, k -> new HashSet<>()).add(pv1);
                        conflictingVersionsByGA.computeIfAbsent(projectKey, k -> new HashSet<>()).add(pv2);
                    }
                }
            }
        });
    }

    private void addAtLeastOneConstraints(List<Formula> clauses, Map<String, Set<ProjectVersion>> alternativeVersions)
    {
        alternativeVersions.values().forEach(alternatives ->
        {
            List<Variable> alternativeVariables = alternatives
                    .stream()
                    .map(v -> variableMap.get(v.getGav()))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
            if (!alternativeVariables.isEmpty())
            {
                Formula clause;
                if (alternativeVariables.size() == 1)
                {
                    clause = alternativeVariables.get(0); // Unit clause
                }
                else
                {
                    clause = formulaFactory.or(alternativeVariables); // At least one
                }
                clauses.add(clause);
            }
        });
    }

    public FormulaFactory getFormulaFactory()
    {
        return formulaFactory;
    }

}
