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

package org.finos.legend.depot.store.model.admin.artifacts;

import com.diffblue.cover.annotations.ManagedByDiffblue;
import com.diffblue.cover.annotations.MethodsUnderTest;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

class ArtifactFileDiffblueTest 


{
    /**
     * Test getters and setters.
     * <p>Methods under test:
     * <ul>
     *   <li>{@link ArtifactFile#ArtifactFile()}
     *   <li>{@link ArtifactFile#setCheckSum(String)}
     *   <li>{@link ArtifactFile#setPath(String)}
     *   <li>{@link ArtifactFile#getCheckSum()}
     *   <li>{@link ArtifactFile#getId()}
     *   <li>{@link ArtifactFile#getPath()}
     * </ul>
     */
    @Test
    @DisplayName("Test getters and setters")
    @Disabled("TODO: Complete this test")
    @Tag("ContributionFromDiffblue")
    @ManagedByDiffblue
    @MethodsUnderTest({
            "void ArtifactFile.<init>()",
            "void ArtifactFile.<init>(String, String)",
            "String ArtifactFile.getCheckSum()",
            "String ArtifactFile.getId()",
            "String ArtifactFile.getPath()",
            "ArtifactFile ArtifactFile.setCheckSum(String)",
            "ArtifactFile ArtifactFile.setPath(String)"
    })
    void testGettersAndSetters()
  {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Diffblue AI was unable to find a test

        // Arrange and Act
        // TODO: Populate arranged inputs
        ArtifactFile actualArtifactFile = new ArtifactFile();
        String checkSum = "";
        ArtifactFile actualSetCheckSumResult = actualArtifactFile.setCheckSum(checkSum);
        String newPath = "";
        ArtifactFile actualSetPathResult = actualArtifactFile.setPath(newPath);
        String actualCheckSum = actualArtifactFile.getCheckSum();
        String actualId = actualArtifactFile.getId();
        String actualPath = actualArtifactFile.getPath();

        // Assert
        // TODO: Add assertions on result
    }

    /**
     * Test getters and setters.
     * <ul>
     *   <li>When empty string.
     * </ul>
     * <p>Methods under test:
     * <ul>
     *   <li>{@link ArtifactFile#ArtifactFile(String, String)}
     *   <li>{@link ArtifactFile#setCheckSum(String)}
     *   <li>{@link ArtifactFile#setPath(String)}
     *   <li>{@link ArtifactFile#getCheckSum()}
     *   <li>{@link ArtifactFile#getId()}
     *   <li>{@link ArtifactFile#getPath()}
     * </ul>
     */
    @Test
    @DisplayName("Test getters and setters; when empty string")
    @Disabled("TODO: Complete this test")
    @Tag("ContributionFromDiffblue")
    @ManagedByDiffblue
    @MethodsUnderTest({
            "void ArtifactFile.<init>()",
            "void ArtifactFile.<init>(String, String)",
            "String ArtifactFile.getCheckSum()",
            "String ArtifactFile.getId()",
            "String ArtifactFile.getPath()",
            "ArtifactFile ArtifactFile.setCheckSum(String)",
            "ArtifactFile ArtifactFile.setPath(String)"
    })
    void testGettersAndSetters_whenEmptyString()
  {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Diffblue AI was unable to find a test

        // Arrange
        // TODO: Populate arranged inputs
        String path = "";
        String checkSum = "";

        // Act
        ArtifactFile actualArtifactFile = new ArtifactFile(path, checkSum);
        String checkSum2 = "";
        ArtifactFile actualSetCheckSumResult = actualArtifactFile.setCheckSum(checkSum2);
        String newPath = "";
        ArtifactFile actualSetPathResult = actualArtifactFile.setPath(newPath);
        String actualCheckSum = actualArtifactFile.getCheckSum();
        String actualId = actualArtifactFile.getId();
        String actualPath = actualArtifactFile.getPath();

        // Assert
        // TODO: Add assertions on result
    }
}
