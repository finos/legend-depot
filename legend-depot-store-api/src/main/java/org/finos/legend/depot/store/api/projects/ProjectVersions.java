package org.finos.legend.depot.store.api.projects;

import org.finos.legend.depot.domain.project.ProjectVersionData;

import java.util.List;
import java.util.Optional;

public interface ProjectVersions {
    List<ProjectVersionData> getAll();

    /**
     * NOTE: page starting from 1
     */
    List<ProjectVersionData> getProjects(int page, int pageSize);

    List<ProjectVersionData> findByProjectId(String projectId);

    Optional<ProjectVersionData> find(String groupId, String artifactId, String versionId);


}
