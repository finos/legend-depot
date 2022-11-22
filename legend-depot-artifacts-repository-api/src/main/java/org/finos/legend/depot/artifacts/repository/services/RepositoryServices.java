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

package org.finos.legend.depot.artifacts.repository.services;

import org.apache.maven.model.Model;
import org.finos.legend.depot.artifacts.repository.api.ArtifactRepository;
import org.finos.legend.depot.artifacts.repository.api.ArtifactRepositoryException;
import org.finos.legend.depot.artifacts.repository.domain.ArtifactDependency;
import org.finos.legend.depot.artifacts.repository.domain.ArtifactType;
import org.finos.legend.depot.artifacts.repository.domain.VersionMismatch;
import org.finos.legend.depot.services.api.projects.ProjectsService;
import org.finos.legend.depot.tracing.services.prometheus.PrometheusMetricsFactory;
import org.finos.legend.sdlc.domain.model.version.VersionId;
import org.slf4j.Logger;

import javax.inject.Inject;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class RepositoryServices
{
    private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(RepositoryServices.class);

    public static final String REPO_VERSIONS = "repo_versions";
    public static final String STORE_VERSIONS = "store_versions";
    public static final String MISSING_REPO_VERSIONS = "missing_repo_versions";
    public static final String MISSING_STORE_VERSIONS = "missing_store_versions";
    public static final String REPO_EXCEPTIONS = "repo_exceptions";

    private final ArtifactRepository repository;
    private final ProjectsService projects;

    @Inject
    public RepositoryServices(ArtifactRepository repository, ProjectsService projectsService)
    {
        this.repository = repository;
        this.projects = projectsService;
    }

    public List<VersionMismatch> findVersionsMismatches()
    {
        List<VersionMismatch> versionMismatches = new ArrayList<>();
        PrometheusMetricsFactory.getInstance().setGauge(REPO_VERSIONS,0);
        PrometheusMetricsFactory.getInstance().setGauge(STORE_VERSIONS,0);
        PrometheusMetricsFactory.getInstance().setGauge(MISSING_REPO_VERSIONS,0);
        PrometheusMetricsFactory.getInstance().setGauge(MISSING_STORE_VERSIONS,0);

        projects.getAll().forEach(p ->
        {
            try
            {
                    List<String> repositoryVersions = repository.findVersions(p.getGroupId(), p.getArtifactId()).stream().map(v -> v.toVersionIdString()).collect(Collectors.toList());
                    PrometheusMetricsFactory.getInstance().increaseGauge(REPO_VERSIONS,repositoryVersions.size());
                    Collections.sort(repositoryVersions);
                    //check versions not in cache
                    List<String> versionsNotInCache = new ArrayList<>(repositoryVersions);
                    PrometheusMetricsFactory.getInstance().increaseGauge(STORE_VERSIONS,p.getVersions().size());
                    versionsNotInCache.removeAll(p.getVersions());
                    //check versions not in repo
                    List<String> versionsNotInRepo = new ArrayList<>(p.getVersions());
                    versionsNotInRepo.removeAll(repositoryVersions);

                    if (!versionsNotInCache.isEmpty() || !versionsNotInRepo.isEmpty())
                    {
                        PrometheusMetricsFactory.getInstance().increaseGauge(MISSING_REPO_VERSIONS,versionsNotInCache.size());
                        PrometheusMetricsFactory.getInstance().increaseGauge(MISSING_STORE_VERSIONS,versionsNotInRepo.size());
                        versionMismatches.add(new VersionMismatch(p.getProjectId(), p.getGroupId(), p.getArtifactId(), versionsNotInCache, versionsNotInRepo));
                        LOGGER.info("version-mismatch found for {} {} {} : notInCache [{}], notInRepo [{}]", p.getProjectId(), p.getGroupId(), p.getArtifactId(), versionsNotInCache, versionsNotInRepo);
                    }

            }
            catch (ArtifactRepositoryException e)
            {
                String message = String.format("Could not get versions for %s:%s exception: %s ",p.getGroupId(),p.getArtifactId(),e.getMessage());
                LOGGER.error(message);
                versionMismatches.add(new VersionMismatch(p.getProjectId(), p.getGroupId(), p.getArtifactId(), Collections.emptyList(), Collections.emptyList(), Arrays.asList(message)));
                PrometheusMetricsFactory.getInstance().incrementErrorCount(REPO_EXCEPTIONS);
            }
        });
        return versionMismatches;
    }

    public List<VersionId> findVersions(String groupId, String artifactId) throws ArtifactRepositoryException
    {
        return this.repository.findVersions(groupId,artifactId);
    }

    public Set<ArtifactDependency> findDependencies(String groupId, String artifactId, String versionId)
    {
        return this.repository.findDependencies(groupId, artifactId, versionId);
    }

    public Model getPOM(String groupId, String artifactId, String versionId)
    {
        return repository.getPOM(groupId, artifactId, versionId);
    }

    public boolean areValidCoordinates(String groupId, String artifactId)
    {
        return this.repository.areValidCoordinates(groupId, artifactId);
    }

    public List<File> findFiles(ArtifactType type, String groupId, String artifactId, String versionId)
    {
        return this.repository.findFiles(type, groupId, artifactId, versionId);
    }
}
