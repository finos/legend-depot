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

package org.finos.legend.depot.services.artifacts.repository.maven;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TestStripModuleSuffix
{
    @Test
    public void stripsEntitiesModuleSuffix()
    {
        Assertions.assertEquals("test-dependencies", MavenArtifactRepository.stripModuleSuffix("test-dependencies-entities"));
    }

    @Test
    public void stripsLongestMatchingModuleSuffix()
    {
        Assertions.assertEquals("test-dependencies", MavenArtifactRepository.stripModuleSuffix("test-dependencies-versioned-entities"));
    }

    @Test
    public void stripsFileGenerationModuleSuffix()
    {
        Assertions.assertEquals("test-dependencies", MavenArtifactRepository.stripModuleSuffix("test-dependencies-file-generation"));
    }

    @Test
    public void leavesArtifactWithoutModuleSuffixUnchanged()
    {
        Assertions.assertEquals("test-dependencies", MavenArtifactRepository.stripModuleSuffix("test-dependencies"));
    }

    @Test
    public void handlesNullArtifactId()
    {
        Assertions.assertNull(MavenArtifactRepository.stripModuleSuffix(null));
    }
}

