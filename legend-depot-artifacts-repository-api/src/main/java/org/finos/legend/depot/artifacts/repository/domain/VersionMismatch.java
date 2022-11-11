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

package org.finos.legend.depot.artifacts.repository.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.EqualsExclude;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class VersionMismatch
{
    @JsonProperty
    public String projectId;
    @JsonProperty
    public String groupId;
    @JsonProperty
    public String artifactId;
    @JsonProperty
    public List<String> versionsNotInCache = new ArrayList<>();
    @JsonProperty
    public List<String> versionsNotInRepo = new ArrayList<>();
    @JsonProperty
    @EqualsExclude
    public List<String> errors = new ArrayList<>();


    public VersionMismatch(String projectId, String groupId, String artifactId, List<String> versionsNotInCache, List<String> versionsNotInRepo,List<String> errors)
    {
        this.projectId = projectId;
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.versionsNotInCache.addAll(versionsNotInCache);
        this.versionsNotInRepo.addAll(versionsNotInRepo);
        this.errors.addAll(errors);
    }

    public VersionMismatch(String projectId, String groupId, String artifactId, List<String> versionsNotInCache, List<String> versionsNotInRepo)
    {
        this(projectId,groupId,artifactId,versionsNotInCache,versionsNotInRepo, Collections.emptyList());
    }

    @Override
    public boolean equals(Object obj)
    {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode()
    {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}