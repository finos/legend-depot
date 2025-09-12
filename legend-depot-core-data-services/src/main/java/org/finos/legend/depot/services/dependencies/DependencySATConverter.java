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
import org.finos.legend.depot.services.api.projects.ProjectsService;
import org.logicng.formulas.Formula;
import org.logicng.formulas.FormulaFactory;
import org.logicng.formulas.Variable;

import java.util.ArrayList;
import java.util.Arrays;
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
    private final Map<String, Variable> variableMap = new HashMap<>();
    private final Map<Variable, ProjectVersion> reverseVariableMap = new HashMap<>();

    public DependencySATConverter(FormulaFactory formulaFactory)
    {
        this.formulaFactory = formulaFactory;
    }

    public LogicNGSATResult convertToLogicNGFormulas(List<ProjectVersion> requiredProjects, Map<ProjectVersion, List<ProjectVersion>> alternativeVersions, ProjectsService projectsService)
    {
        List<Formula> clauses = new ArrayList<>();
        Map<Variable, Integer> weights = new HashMap<>();

        // Extract overrides from requiredProjects and separate actual requirements
        Map<ProjectVersion, ProjectVersion> detectedOverrides = extractOverrides(requiredProjects, projectsService);
        List<ProjectVersion> actualRequiredProjects = getActualRequiredProjects(requiredProjects, detectedOverrides);

        Map<ProjectVersion, List<ProjectVersion>> resolvedAlternatives = applyOverridesToAlternatives(alternativeVersions, detectedOverrides);
        createVariables(actualRequiredProjects, resolvedAlternatives, projectsService);
        assignVersionWeights(weights);

        // Add dependency constraints with override handling
        addDependencyConstraintsWithOverrides(clauses, actualRequiredProjects, resolvedAlternatives, detectedOverrides, projectsService, new HashSet<>());

        // Add mutual exclusion constraints
        addMutualExclusionConstraints(clauses, resolvedAlternatives);

        // Add at-least-one constraints
        addAtLeastOneConstraints(clauses, actualRequiredProjects, resolvedAlternatives);

        // Add override constraints (force specific versions)
        addOverrideConstraints(clauses, detectedOverrides);

        return new LogicNGSATResult(clauses, variableMap, reverseVariableMap, formulaFactory, weights);
    }

    private int calculateVersionWeight(ProjectVersion projectVersion)
    {
        String version = projectVersion.getVersionId();
        // Parse semantic version (e.g., "1.2.3" -> 10203)
        String[] parts = version.split("\\.");
        int weight = 0;
        for (int i = 0; i < Math.min(parts.length, 3); i++)
        {
            try
            {
                weight += Integer.parseInt(parts[i]) * (int) Math.pow(1000, 2 - i);
            }
            catch (NumberFormatException e)
            {
                // Handle non-numeric versions
                weight += parts[i].hashCode() % 1000;
            }
        }
        return weight;
    }

    private void assignVersionWeights(Map<Variable, Integer> weights)
    {
        // Group variables by project (groupId:artifactId)
        Map<String, List<Variable>> projectGroups = new HashMap<>();

        reverseVariableMap.forEach((variable, projectVersion) ->
        {
            String projectKey = projectVersion.getGroupId() + ":" + projectVersion.getArtifactId();
            projectGroups.computeIfAbsent(projectKey, k -> new ArrayList<>()).add(variable);
        });

        // Assign weights within each project group
        projectGroups.forEach((projectKey, variables) ->
        {
            if (variables.size() > 1)
            {
                // Sort variables by version to assign relative weights
                variables.sort((v1, v2) ->
                {
                    ProjectVersion pv1 = reverseVariableMap.get(v1);
                    ProjectVersion pv2 = reverseVariableMap.get(v2);
                    return Integer.compare(calculateVersionWeight(pv1), calculateVersionWeight(pv2));
                });

                // Assign increasing weights (higher for newer versions)
                for (int i = 0; i < variables.size(); i++)
                {
                    weights.put(variables.get(i), i + 1);
                }
            }
            else
            {
                // Single version gets weight 1
                weights.put(variables.get(0), 1);
            }
        });
    }

    private Map<ProjectVersion, List<ProjectVersion>> applyOverridesToAlternatives(Map<ProjectVersion, List<ProjectVersion>> alternatives, Map<ProjectVersion, ProjectVersion> overrides)
    {
        return alternatives.entrySet().stream()
                .collect(Collectors.toMap(
                        entry -> overrides.getOrDefault(entry.getKey(), entry.getKey()), // Override key
                        entry -> entry.getValue().stream()
                                .map(alt -> overrides.getOrDefault(alt, alt)) // Override alternatives
                                .collect(Collectors.toList())
                ));
    }

    private Map<ProjectVersion, ProjectVersion> extractOverrides(List<ProjectVersion> requiredProjects, ProjectsService projectsService)
    {
        Map<ProjectVersion, ProjectVersion> overrides = new HashMap<>();
        Map<String, ProjectVersion> winningVersions = new HashMap<>();

        // 1. Determine the "winning" version for each project in the initial required list.
        // The last one in the list for a given G:A wins.
        requiredProjects.forEach(pv ->
        {
            String projectKey = pv.getGroupId() + ":" + pv.getArtifactId();
            winningVersions.put(projectKey, pv);
        });

        // 2. Collect all possible versions that could appear in the dependency graph.
        Set<ProjectVersion> allPossibleVersions = new HashSet<>();
        requiredProjects.forEach(pv ->
        {
            // Include the project itself and all its transitive dependencies
            allPossibleVersions.add(pv);
            allPossibleVersions.addAll(projectsService.getDependencies(Arrays.asList(pv), true));
        });

        // 3. For each "winning" version, find all other versions of the same project
        // in the total set of dependencies and create an override rule.
        winningVersions.forEach((projectKey, winner) ->
        {
            allPossibleVersions.forEach(p ->
            {
                String currentProjectKey = p.getGroupId() + ":" + p.getArtifactId();
                if (projectKey.equals(currentProjectKey) && !p.equals(winner))
                {
                    overrides.put(p, winner);
                }
            });
        });

        return overrides;
    }

    private List<ProjectVersion> getActualRequiredProjects(List<ProjectVersion> requiredProjects, Map<ProjectVersion, ProjectVersion> overrides)
    {
        Set<String> seenProjects = new HashSet<>();
        List<ProjectVersion> actualRequired = new ArrayList<>();

        // Process in reverse order to prioritize later specifications
        for (int i = requiredProjects.size() - 1; i >= 0; i--)
        {
            ProjectVersion pv = requiredProjects.get(i);
            String projectKey = pv.getGroupId() + ":" + pv.getArtifactId();

            if (!seenProjects.contains(projectKey))
            {
                seenProjects.add(projectKey);
                actualRequired.add(0, pv); // Add to front to maintain original order
            }
        }

        return actualRequired;
    }

    private void createVariables(List<ProjectVersion> requiredProjects, Map<ProjectVersion, List<ProjectVersion>> alternativeVersions, ProjectsService projectsService)
    {
        Set<ProjectVersion> allVersions = new HashSet<>();

        // Add all alternative versions for required projects
        requiredProjects.forEach(pv ->
        {
            allVersions.add(pv);
            Set<ProjectVersion> dependencies = projectsService.getDependencies(Arrays.asList(pv), true);
            allVersions.addAll(dependencies);
        });

        // Add all transitive dependencies for each alternative
        requiredProjects.forEach(pv ->
        {
            List<ProjectVersion> alternatives = alternativeVersions.getOrDefault(pv, Arrays.asList(pv));
            alternatives.forEach(alt ->
            {
                allVersions.add(alt);
                Set<ProjectVersion> dependencies = projectsService.getDependencies(Arrays.asList(alt), true);
                allVersions.addAll(dependencies);
            });
        });

        // Create LogicNG variables for all versions
        allVersions.forEach(pv ->
        {
            String gavKey = pv.getGav();
            if (!variableMap.containsKey(gavKey))
            {
                String varName = gavKey.replaceAll("[^a-zA-Z0-9_]", "_");
                Variable var = formulaFactory.variable(varName);
                variableMap.put(gavKey, var);
                reverseVariableMap.put(var, pv);
            }
        });
    }

    private void addDependencyConstraintsWithOverrides(List<Formula> clauses, List<ProjectVersion> requiredProjects, Map<ProjectVersion, List<ProjectVersion>> alternativeVersions, Map<ProjectVersion, ProjectVersion> overrides, ProjectsService projectsService, Set<ProjectVersion> processed)
    {
        Set<ProjectVersion> requiredProjectsAndAlternatives = new HashSet<>();
        requiredProjects.forEach(pv ->
        {
            requiredProjectsAndAlternatives.add(pv);
            List<ProjectVersion> alternatives = alternativeVersions.getOrDefault(pv, Arrays.asList(pv));
            requiredProjectsAndAlternatives.addAll(alternatives);
        });

        requiredProjectsAndAlternatives.forEach(pv -> processDependencyConstraintsWithOverrides(clauses, pv, overrides, projectsService, processed));
    }

    private void processDependencyConstraintsWithOverrides(List<Formula> clauses, ProjectVersion projectVersion, Map<ProjectVersion, ProjectVersion> overrides, ProjectsService projectsService, Set<ProjectVersion> processed)
    {
        if (processed.contains(projectVersion))
        {
            return;
        }
        processed.add(projectVersion);

        Variable parentVar = variableMap.get(projectVersion.getGav());
        if (parentVar == null)
        {
            return;
        }

        Set<ProjectVersion> dependencies = projectsService.getDependencies(Arrays.asList(projectVersion), false);

        dependencies.forEach(dep ->
        {
            // Apply override if one exists for this dependency
            ProjectVersion effectiveDep = overrides.getOrDefault(dep, dep);
            Variable depVar = variableMap.get(effectiveDep.getGav());

            if (depVar != null)
            {
                // ¬parent ? dependency (using effective dependency)
                clauses.add(formulaFactory.or(parentVar.negate(), depVar));
                processDependencyConstraintsWithOverrides(clauses, effectiveDep, overrides, projectsService, processed);
            }
        });
    }

    private void addMutualExclusionConstraints(List<Formula> clauses, Map<ProjectVersion, List<ProjectVersion>> alternativeVersions)
    {
        Map<String, List<Variable>> projectGroups = new HashMap<>();

        // Include all variables from the variable map, grouped by project coordinates
        reverseVariableMap.forEach((variable, projectVersion) ->
        {
            String projectKey = projectVersion.getGroupId() + ":" + projectVersion.getArtifactId();
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
                    }
                }
            }
        });
    }

    private void addAtLeastOneConstraints(List<Formula> clauses, List<ProjectVersion> requiredProjects, Map<ProjectVersion, List<ProjectVersion>> alternativeVersions)
    {
        Map<String, List<Variable>> projectRequirements = new HashMap<>();

        requiredProjects.forEach(pv ->
        {
            String projectKey = pv.getGroupId() + ":" + pv.getArtifactId();
            List<Variable> alternatives = alternativeVersions.getOrDefault(pv, Arrays.asList(pv))
                    .stream()
                    .map(v -> variableMap.get(v.getGav()))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
            projectRequirements.put(projectKey, alternatives);
        });

        projectRequirements.values().forEach(alternatives ->
        {
            if (!alternatives.isEmpty())
            {
                if (alternatives.size() == 1)
                {
                    clauses.add(alternatives.get(0)); // Unit clause
                }
                else
                {
                    clauses.add(formulaFactory.or(alternatives)); // At least one
                }
            }
        });
    }

    private void addOverrideConstraints(List<Formula> clauses, Map<ProjectVersion, ProjectVersion> overrides)
    {
        overrides.forEach((loser, winner) ->
        {
            Variable loserVar = variableMap.get(loser.getGav());
            Variable winnerVar = variableMap.get(winner.getGav());

            // Ensure the overridden version is not selected
            if (loserVar != null)
            {
                clauses.add(loserVar.negate());
            }

            // Add a constraint that if the loser was considered, the winner must be chosen.
            // This helps reinforce the override logic.
            if (loserVar != null && winnerVar != null)
            {
                // ¬loserVar V winnerVar
                clauses.add(formulaFactory.or(loserVar.negate(), winnerVar));
            }
        });

        // Also, explicitly force the selection of winning versions that override another
        // version from the initial required projects list.
        overrides.values().stream().distinct().forEach(winner ->
        {
            Variable winnerVar = variableMap.get(winner.getGav());
            if (winnerVar != null)
            {
                clauses.add(winnerVar);
            }
        });
    }

    public FormulaFactory getFormulaFactory()
    {
        return formulaFactory;
    }

}
