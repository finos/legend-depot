package org.finos.legend.depot.services.api.artifacts.configuration;

import com.diffblue.cover.annotations.ManagedByDiffblue;
import com.diffblue.cover.annotations.MethodsUnderTest;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

class ArtifactsRefreshPolicyConfiguration0101TickTest {
    /**
     * Test
     * {@link ArtifactsRefreshPolicyConfiguration#ArtifactsRefreshPolicyConfiguration(Long,
     * IncludeProjectPropertiesConfiguration)}.
     * <p>Method under test: {@link
     * ArtifactsRefreshPolicyConfiguration#ArtifactsRefreshPolicyConfiguration(Long,
     * IncludeProjectPropertiesConfiguration)}
     */
    @Test
    @DisplayName(
            "Test new ArtifactsRefreshPolicyConfiguration(Long, IncludeProjectPropertiesConfiguration)")
    @Disabled("TODO: Complete this test")
    @Tag("ContributionFromDiffblue")
    @ManagedByDiffblue
    @MethodsUnderTest({
            "void ArtifactsRefreshPolicyConfiguration.<init>(Long, IncludeProjectPropertiesConfiguration)"
    })
    void testNewArtifactsRefreshPolicyConfiguration() {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Reason: Static initializer failed.
        //   The static initializer of
        //   com.diffblue.cover.k.b.a.b
        //   threw java.lang.NoClassDefFoundError while trying to load it.
        //   Make sure the static initializer of b
        //   can be executed without throwing exceptions.
        //   Exception: java.lang.NoClassDefFoundError: Could not initialize class
        // com.diffblue.cover.k.b.a.b
        //       at java.base/java.util.Optional.map(Optional.java:260)

        // Arrange
        // TODO: Populate arranged inputs
        Long versionsUpdateIntervalInMillis = null;
        IncludeProjectPropertiesConfiguration includeProjectPropertiesConfiguration = null;

        // Act
        ArtifactsRefreshPolicyConfiguration actualArtifactsRefreshPolicyConfiguration =
                new ArtifactsRefreshPolicyConfiguration(
                        versionsUpdateIntervalInMillis, includeProjectPropertiesConfiguration);

        // Assert
        // TODO: Add assertions on result
    }

    /**
     * Test getters and setters.
     * <p>Methods under test:
     * <ul>
     *   <li>{@link ArtifactsRefreshPolicyConfiguration#getIncludeProjectPropertiesConfiguration()}
     *   <li>{@link ArtifactsRefreshPolicyConfiguration#getVersionsUpdateIntervalInMillis()}
     * </ul>
     */
    @Test
    @DisplayName("Test getters and setters")
    @Disabled("TODO: Complete this test")
    @Tag("ContributionFromDiffblue")
    @ManagedByDiffblue
    @MethodsUnderTest({
            "IncludeProjectPropertiesConfiguration ArtifactsRefreshPolicyConfiguration.getIncludeProjectPropertiesConfiguration()",
            "long ArtifactsRefreshPolicyConfiguration.getVersionsUpdateIntervalInMillis()"
    })
    void testGettersAndSetters() {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Diffblue AI was unable to find a test

        // Arrange
        // TODO: Populate arranged inputs
        ArtifactsRefreshPolicyConfiguration artifactsRefreshPolicyConfiguration = null;

        // Act
        IncludeProjectPropertiesConfiguration actualIncludeProjectPropertiesConfiguration =
                artifactsRefreshPolicyConfiguration.getIncludeProjectPropertiesConfiguration();
        long actualVersionsUpdateIntervalInMillis =
                artifactsRefreshPolicyConfiguration.getVersionsUpdateIntervalInMillis();

        // Assert
        // TODO: Add assertions on result
    }
}
