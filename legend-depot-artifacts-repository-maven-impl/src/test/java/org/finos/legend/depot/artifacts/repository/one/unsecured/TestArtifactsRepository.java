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

package org.finos.legend.depot.artifacts.repository.one.unsecured;

import org.finos.legend.depot.artifacts.repository.api.ArtifactRepository;
import org.finos.legend.sdlc.domain.model.version.VersionId;
import org.slf4j.Logger;

import java.io.File;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestArtifactsRepository extends ArtifactRepositoryOneUnsecured implements ArtifactRepository
{
    public static final String SEPARATOR = "/";
    public static final String DOT = ".";
    private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(TestArtifactsRepository.class);
    private static Map<String, List<VersionId>> TESTING_VERSIONS = new HashMap<>();

    static
    {
        TESTING_VERSIONS.put("examples.metadata.test", Arrays.asList(
                VersionId.parseVersionId("2.0.0"),
                VersionId.parseVersionId("1.0.0")));
        TESTING_VERSIONS.put("examples.metadata.test-dependencies", Arrays.asList(VersionId.parseVersionId("1.0.0")));
    }

    public TestArtifactsRepository()
    {
        super(null);
    }

    @Override
    public List<VersionId> findVersions(String group, String artifact)
    {
        return TESTING_VERSIONS.get(group + DOT + artifact);
    }

    @Override
    protected URL[] resolvePOMFromRepository(String group, String artifact, String version)
    {
        String pomFile = getFilePath(group, artifact, version, "pom");
        LOGGER.info("test pom file {}", pomFile);

        URL filePath = this.getClass().getClassLoader().getResource(pomFile);
        if (filePath == null)
        {
            throw new RuntimeException("could not find " + getFilePath(group, artifact, version, "pom"));
        }
        return new URL[]{filePath};
    }

    private String getFilePath(String group, String artifact, String version, String type)
    {
        return "repository" + SEPARATOR + group.replace(DOT, SEPARATOR) + SEPARATOR + artifact
                + SEPARATOR + version
                + SEPARATOR + artifact + "-" + version + DOT + type;
    }

    @Override
    protected File[] resolveArtifactFilesFromRepository(String group, String artifact, String version)
    {
        URL filePath = this.getClass().getClassLoader().getResource(getFilePath(group, artifact, version, "jar"));
        if (filePath == null)
        {
            throw new RuntimeException("could not find " + getFilePath(group, artifact, version, "jar"));
        }
        return new File[]{new File(filePath.getFile())};
    }
}
