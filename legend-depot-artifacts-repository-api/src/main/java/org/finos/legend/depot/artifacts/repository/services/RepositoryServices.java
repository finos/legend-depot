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
import org.finos.legend.depot.domain.project.StoreProjectData;
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
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

public class RepositoryServices
{
    private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(RepositoryServices.class);

    public static final String REPO_VERSIONS = "repo_versions";
    public static final String STORE_VERSIONS = "store_versions";
    public static final String MISSING_REPO_VERSIONS = "missing_repo_versions";
    public static final String MISSING_STORE_VERSIONS = "missing_store_versions";
    public static final String REPO_EXCEPTIONS = "repo_exceptions";
    public static final String PROJECTS = "projects";

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
        AtomicLong repoVersions = new AtomicLong(0);
        AtomicLong storeVersionsCount = new AtomicLong(0);
        AtomicLong missingRepoVersions = new AtomicLong(0);
        AtomicLong missingStoreVersions = new AtomicLong(0);
        AtomicLong repoExceptions = new AtomicLong(0);
        List<StoreProjectData> allProjects = projects.getAllProjectCoordinates();
        LOGGER.info("Starting findVersionsMismatches {}",allProjects.size());
        allProjects.forEach(p ->
        {
            try
            {
                final List<String> storeVersions = projects.getVersions(p.getGroupId(), p.getArtifactId());
                storeVersionsCount.addAndGet(storeVersions.size());
                final List<String> repositoryVersions = repository.findVersions(p.getGroupId(), p.getArtifactId()).stream().map(v -> v.toVersionIdString()).collect(Collectors.toList());
                repoVersions.addAndGet(repositoryVersions.size());

                //check versions not in store
                List<String> versionsNotInStore = repositoryVersions.stream().filter(repoVersion -> !storeVersions.contains(repoVersion)).collect(Collectors.toList());
                missingRepoVersions.addAndGet(versionsNotInStore.size());
                if (!versionsNotInStore.isEmpty())
                {
                    LOGGER.info("version-mismatch found for {} {}-{} : notInStore[{}]", p.getProjectId(), p.getGroupId(), p.getArtifactId(), versionsNotInStore);
                }
                //check versions not in repo
                List<String> versionsNotInRepo = storeVersions.stream().filter(storeVersion -> !repositoryVersions.contains(storeVersion)).collect(Collectors.toList());
                missingStoreVersions.addAndGet(versionsNotInRepo.size());
                if (!versionsNotInRepo.isEmpty())
                {
                    LOGGER.info("version-mismatch found for {} {}-{} : notInRepository [{}]", p.getProjectId(), p.getGroupId(), p.getArtifactId(), versionsNotInRepo);
                }

                if (!versionsNotInStore.isEmpty() || !versionsNotInRepo.isEmpty())
                {
                    versionMismatches.add(new VersionMismatch(p.getProjectId(), p.getGroupId(), p.getArtifactId(), versionsNotInStore, versionsNotInRepo));
                }
            }
            catch (Exception e)
            {
                String message = String.format("Could not get versions for %s:%s exception: %s ", p.getGroupId(), p.getArtifactId(), e.getMessage());
                LOGGER.error(message);
                versionMismatches.add(new VersionMismatch(p.getProjectId(), p.getGroupId(), p.getArtifactId(), Collections.emptyList(), Collections.emptyList(), Arrays.asList(message)));
                repoExceptions.addAndGet(1);
            }
        });

        PrometheusMetricsFactory.getInstance().setGauge(PROJECTS,allProjects.size());
        PrometheusMetricsFactory.getInstance().setGauge(REPO_VERSIONS,repoVersions.get());
        PrometheusMetricsFactory.getInstance().setGauge(STORE_VERSIONS,storeVersionsCount.get());
        PrometheusMetricsFactory.getInstance().setGauge(MISSING_REPO_VERSIONS,missingRepoVersions.get());
        PrometheusMetricsFactory.getInstance().setGauge(MISSING_STORE_VERSIONS,missingStoreVersions.get());
        PrometheusMetricsFactory.getInstance().setGauge(REPO_EXCEPTIONS,repoExceptions.get());
        LOGGER.info("Finished findVersionsMismatches {}",versionMismatches.size());
        return versionMismatches;
    }

    public List<VersionId> findVersions(String groupId, String artifactId) throws ArtifactRepositoryException
    {
        return this.repository.findVersions(groupId,artifactId);
    }

    public Optional<String> findVersion(String groupId, String artifactId, String versionId) throws ArtifactRepositoryException
    {
        return this.repository.findVersion(groupId,artifactId,versionId);
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
