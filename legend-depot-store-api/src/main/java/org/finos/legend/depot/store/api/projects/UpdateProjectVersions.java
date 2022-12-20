package org.finos.legend.depot.store.api.projects;

import org.finos.legend.depot.domain.api.MetadataEventResponse;
import org.finos.legend.depot.domain.project.ProjectVersionData;

public interface UpdateProjectVersions extends ProjectVersions
{
    ProjectVersionData createOrUpdate(ProjectVersionData projectData);

    boolean createIndexesIfAbsent();

    MetadataEventResponse delete(String groupId, String artifactId, String versionId);

    MetadataEventResponse deleteByProjectId(String projectId);
}
