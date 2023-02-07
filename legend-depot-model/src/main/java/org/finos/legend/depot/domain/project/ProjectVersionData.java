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

package org.finos.legend.depot.domain.project;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ProjectVersionData
{
    @JsonProperty
    private List<ProjectVersion> dependencies = new ArrayList<>();
    @JsonProperty
    private List<Property> properties = new ArrayList<>();

    public ProjectVersionData()
    {
    }

    public ProjectVersionData(List<ProjectVersion> dependencies, List<Property> properties)
    {
        this.dependencies = dependencies;
        this.properties = properties;
    }

    public List<ProjectVersion> getDependencies()
    {
        return dependencies;
    }

    public void addDependencies(List<ProjectVersion> dependencies)
    {
        this.dependencies.addAll(dependencies);
    }

    public void addDependency(ProjectVersion dependency)
    {
        if (!dependencies.contains(dependency))
        {
            this.dependencies.add(dependency);
        }
    }

    public void setDependencies(List<ProjectVersion> dependencies)
    {
        this.dependencies = dependencies;
    }


    public List<Property> getProperties()
    {
        return properties;
    }

    public void setProperties(List<Property> properties)
    {
        this.properties = properties;
    }

    public void addProperties(List<Property> propertyList)
    {
        propertyList.stream().filter(property -> !properties.contains(property)).forEach(property -> this.properties.add(property));
    }
}
