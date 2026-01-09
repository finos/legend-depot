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

package org.finos.legend.depot.services.api.dependencies;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.finos.legend.depot.domain.project.ProjectVersion;

import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DependencyResponseModel
{
    @JsonProperty
    private boolean success;

    @JsonProperty
    private List<ProjectVersion> resolvedVersions;

    @JsonProperty
    private List<DependencyConflict> conflicts;

    @JsonProperty
    private String failureReason;

    public DependencyResponseModel()
    {
        this.resolvedVersions = new ArrayList<>();
        this.conflicts = new ArrayList<>();
    }

    public static DependencyResponseModel success(List<ProjectVersion> resolvedVersions)
    {
        DependencyResponseModel model = new DependencyResponseModel();
        model.success = true;
        model.resolvedVersions = resolvedVersions;
        return model;
    }

    public static DependencyResponseModel failure(String failureReason, List<DependencyConflict> conflicts)
    {
        DependencyResponseModel model = new DependencyResponseModel();
        model.success = false;
        model.failureReason = failureReason;
        model.conflicts = conflicts;
        return model;
    }

    public boolean isSuccess()
    {
        return success;
    }

    public void setSuccess(boolean success)
    {
        this.success = success;
    }

    public List<ProjectVersion> getResolvedVersions()
    {
        return resolvedVersions;
    }

    public void setResolvedVersions(List<ProjectVersion> resolvedVersions)
    {
        this.resolvedVersions = resolvedVersions;
    }

    public List<DependencyConflict> getConflicts()
    {
        return conflicts;
    }

    public void setConflicts(List<DependencyConflict> conflicts)
    {
        this.conflicts = conflicts;
    }

    public String getFailureReason()
    {
        return failureReason;
    }

    public void setFailureReason(String failureReason)
    {
        this.failureReason = failureReason;
    }

    public void addConflict(DependencyConflict conflict)
    {
        this.conflicts.add(conflict);
    }
}
