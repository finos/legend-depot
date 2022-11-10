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

import org.finos.legend.depot.artifacts.repository.api.ArtifactRepository;
import org.finos.legend.depot.artifacts.repository.api.ArtifactRepositoryException;
import org.finos.legend.depot.artifacts.repository.domain.VersionMismatch;
import org.finos.legend.depot.services.api.projects.ProjectsService;
import org.slf4j.Logger;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class RepositoryServices
{
    private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(RepositoryServices.class);

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
        projects.getAll().forEach(p ->
        {
            try
            {
                    List<String> repositoryVersions = repository.findVersions(p.getGroupId(), p.getArtifactId()).stream().map(v -> v.toVersionIdString()).collect(Collectors.toList());
                    Collections.sort(repositoryVersions);
                    //check versions not in cache
                    List<String> versionsNotInCache = new ArrayList<>(repositoryVersions);
                    versionsNotInCache.removeAll(p.getVersions());
                    //check versions not in repo
                    List<String> versionsNotInRepo = new ArrayList<>(p.getVersions());
                    versionsNotInRepo.removeAll(repositoryVersions);

                    if (!versionsNotInCache.isEmpty() || !versionsNotInRepo.isEmpty())
                    {
                        versionMismatches.add(new VersionMismatch(p.getProjectId(), p.getGroupId(), p.getArtifactId(), versionsNotInCache, versionsNotInRepo));
                        LOGGER.info("version-mismatch found for {} {} {} : notInCache [{}], notInRepo [{}]", p.getProjectId(), p.getGroupId(), p.getArtifactId(), versionsNotInCache, versionsNotInRepo);
                    }

            }
            catch (ArtifactRepositoryException e)
            {
                String message = String.format("Could not get versions for %s:%s exception: %s ",p.getGroupId(),p.getArtifactId(),e.getMessage());
                LOGGER.error(message);
                versionMismatches.add(new VersionMismatch(p.getProjectId(), p.getGroupId(), p.getArtifactId(), Collections.emptyList(), Collections.emptyList(), Arrays.asList(message)));
            }
        });
        return versionMismatches;
    }
}
