package org.finos.legend.depot.domain.project;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.finos.legend.depot.domain.BaseVersionDomain;
import org.finos.legend.depot.domain.HasIdentifier;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ProjectVersionData extends BaseVersionDomain implements HasIdentifier
{
    @JsonProperty
    private String creator;
    @JsonProperty
    private String createdTime;
    @JsonProperty
    private String id;
    @JsonProperty
    private String projectId;
    @JsonProperty
    private List<ProjectVersionDependency> dependencies = new ArrayList<>();
    @JsonProperty
    private List<ProjectProperty> properties = new ArrayList<>();

    public ProjectVersionData()
    {
    }

    public ProjectVersionData(String projectId, String groupId, String artifactId, String versionId)
    {
        super(groupId, artifactId, versionId);
        this.projectId = projectId;
    }


    public String getId() {
        return id;
    }

    public String getCreator()
    {
        return creator;
    }

    public void setCreator(String creator)
    {
        this.creator = creator;
    }

    public String getCreatedTime()
    {
        return createdTime;
    }

    public void setCreatedTime(String createdTime)
    {
        this.createdTime = createdTime;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getProjectId()
    {
        return projectId;
    }

    public void setProjectId(String projectId)
    {
        this.projectId = projectId;
    }

    public List<ProjectVersionDependency> getDependencies()
    {
        return dependencies;
    }

    public void addDependencies(List<ProjectVersionDependency> dependencies)
    {
        this.dependencies.addAll(dependencies);
    }

    public void addDependency(ProjectVersionDependency dependency)
    {
        if (!dependencies.contains(dependency))
        {
            this.dependencies.add(dependency);
        }
    }

    public void setDependencies(List<ProjectVersionDependency> dependencies)
    {
        this.dependencies = dependencies;
    }


    public List<ProjectProperty> getProperties()
    {
        return properties;
    }

    public void setProperties(List<ProjectProperty> properties)
    {
        this.properties = properties;
    }

    public List<ProjectProperty> getPropertiesForProjectVersionID(String projectVersionId)
    {
        return properties.stream().filter(property -> property.getProjectVersionId().equals(projectVersionId)).collect(Collectors.toList());
    }

    public void addProperties(List<ProjectProperty> propertyList)
    {
        propertyList.stream().filter(property -> !properties.contains(property)).forEach(property -> this.properties.add(property));
    }
}
