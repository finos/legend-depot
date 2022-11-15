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

package org.finos.legend.depot.artifacts.repository.maven.impl;

import org.apache.maven.model.Build;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.model.Parent;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.settings.Settings;
import org.apache.maven.settings.io.DefaultSettingsReader;
import org.apache.maven.settings.io.SettingsReader;
import org.finos.legend.depot.artifacts.repository.api.ArtifactRepository;
import org.finos.legend.depot.artifacts.repository.api.ArtifactRepositoryException;
import org.finos.legend.depot.artifacts.repository.api.ArtifactRepositoryProviderConfiguration;
import org.finos.legend.depot.artifacts.repository.domain.ArtifactDependency;
import org.finos.legend.depot.artifacts.repository.domain.ArtifactType;
import org.finos.legend.depot.domain.version.VersionValidator;
import org.finos.legend.depot.tracing.services.TracerFactory;
import org.finos.legend.sdlc.domain.model.version.VersionId;
import org.jboss.shrinkwrap.resolver.api.NoResolvedResultException;
import org.jboss.shrinkwrap.resolver.api.ResolutionException;
import org.jboss.shrinkwrap.resolver.api.VersionResolutionException;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.jboss.shrinkwrap.resolver.api.maven.MavenResolverSystem;
import org.jboss.shrinkwrap.resolver.api.maven.MavenVersionRangeResult;
import org.jboss.shrinkwrap.resolver.api.maven.PackagingType;
import org.jboss.shrinkwrap.resolver.api.maven.coordinate.MavenCoordinate;
import org.slf4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class MavenArtifactRepository implements ArtifactRepository
{
    private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(MavenArtifactRepository.class);
    private static final String EMPTY_STRING = "";
    private static final String GAV_SEP = ":";
    private static final String GROUP_ID = "groupId";
    private static final String ARTIFACT_ID = "artifactId";
    public static final String VERSION_ID = "versionId";
    private static final String ALL_VERSIONS_SCOPE = ":[0.0,)";
    public static final String SEPARATOR = "-";
    private final MavenXpp3Reader mavenReader = new MavenXpp3Reader();
    private final String settingsLocation;
    private String localRepository;


    public MavenArtifactRepository(ArtifactRepositoryProviderConfiguration configuration)
    {
        if (configuration == null)
        {
            this.settingsLocation = null;
            return;
        }
        if (configuration instanceof MavenArtifactRepositoryConfiguration)
        {
            this.settingsLocation = ((MavenArtifactRepositoryConfiguration)configuration).getSettingsLocation();
            loadSettings(this.settingsLocation);
        }
        else
        {
            throw new IllegalArgumentException("cannot initialise repository, please provide a settings file");
        }
    }


    private MavenResolverSystem getResolver()
    {
        return Maven.configureResolver()
                .withMavenCentralRepo(false)
                .withClassPathResolution(false)
                .fromFile(settingsLocation);
    }

    private void loadSettings(String settingsFile)
    {
        SettingsReader reader = new DefaultSettingsReader();
        LOGGER.info("reading settings xml:[{}] ", settingsFile);
        try (FileInputStream stream = new FileInputStream(settingsFile))
        {
            Settings settings = reader.read(stream, new HashMap<>());
            if (settings.getLocalRepository() == null)
            {
                throw new ArtifactRepositoryException("Please provide a valid local repository in settings.xml");
            }
            this.localRepository = settings.getLocalRepository();

        }
        catch (Exception e)
        {
            LOGGER.error("could not initialise settings.xml {}", e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    protected String gavCoordinates(String group, String artifact, PackagingType type, String version)
    {
        return group + GAV_SEP + artifact + (type == null ? EMPTY_STRING : GAV_SEP + type.getId()) + GAV_SEP + version;
    }

    protected String gavCoordinates(String group, String artifact, String version)
    {
        return gavCoordinates(group, artifact, null, version);
    }

    @Override
    public boolean areValidCoordinates(String group, String artifact)
    {
        return group != null && artifact != null
                && !group.contains(GAV_SEP)
                && !artifact.contains(GAV_SEP);
    }

    @Override
    public List<File> findFiles(ArtifactType type, String group, String artifactId, String version)
    {
        LOGGER.info("resolving files for [{}] artifacts [{}:{}:{}]", type, group, artifactId, version);
        List<String> modulesWithArtifacts = getModulesFromPOM(type, group, artifactId, version);
        List<File> foundFiles = new ArrayList<>();
        try
        {
            modulesWithArtifacts.forEach(artifactModule ->
            {
                File[] artifactFiles = resolveArtifactFilesFromRepository(group, artifactModule, version);
                foundFiles.addAll(Arrays.asList(artifactFiles));
            });
        }
        catch (NoResolvedResultException ex)
        {
            LOGGER.error("could not resolve file for [{}] artifacts [{}{}{}] : [{}]", type, group, artifactId, version, ex.getMessage());
        }
        LOGGER.info("found [{}] files for [{}] artifacts [{}{}{}]",foundFiles.size(), type, group, artifactId, version);
        return foundFiles;
    }


    @Override
    public List<File> findDependenciesFiles(ArtifactType type, String group, String artifact, String version)
    {
        List<File> files = new ArrayList<>();
        findDependenciesByArtifactType(type, group, artifact, version).forEach(dep ->
                files.addAll(Arrays.asList(resolveArtifactFilesFromRepository(group, dep.getArtifactId(), dep.getVersion()))));
        return files;
    }

    @Override
    public Set<ArtifactDependency> findDependenciesByArtifactType(ArtifactType type, String groupId, String artifactId, String versionId)
    {
        List<Dependency> dependencies = new ArrayList<>();
        String moduleName = artifactId + SEPARATOR + type.getModuleName();
        getModulesFromPOM(type, groupId, artifactId, versionId)
                .stream()
                .filter(mod -> mod.equals(moduleName))
                .forEach(mod -> dependencies.addAll(getPOM(groupId, mod, versionId).getDependencies()));
        return dependencies.stream().filter(dep -> dep.getArtifactId().endsWith(type.getModuleName())).map(dep -> new ArtifactDependency(dep.getGroupId(), dep.getArtifactId(), dep.getVersion())).collect(Collectors.toSet());
    }

    @Override
    public Set<ArtifactDependency> findDependencies(String groupId, String artifactId, String versionId)
    {
        Set<ArtifactDependency> dependencies = new HashSet<>();
        List<String> modulesWithEntities = getModulesFromPOM(ArtifactType.ENTITIES, groupId, artifactId, versionId);
        if (!modulesWithEntities.isEmpty())
        {
            modulesWithEntities.forEach(module ->
            {
                Model modulePom = getPOM(groupId, module, versionId);
                List<Dependency> moduleDependencies = modulePom.getDependencies().stream().filter(dep -> dep.getVersion() != null).collect(Collectors.toList());
                if (moduleDependencies.isEmpty())
                {
                    List<Dependency> pluginsDependencies = new ArrayList<>();
                    Build build = modulePom.getBuild();
                    if (build != null && build.getPlugins() != null && !build.getPlugins().isEmpty())
                    {
                        build.getPlugins().forEach(plugin -> pluginsDependencies.addAll(plugin.getDependencies()));
                        moduleDependencies = pluginsDependencies;
                    }
                }
                moduleDependencies.stream().filter(dep -> dep.getArtifactId().endsWith(ArtifactType.ENTITIES.getModuleName()))
                        .forEach(dependency ->
                                {
                                    Parent parent = getPOM(dependency.getGroupId(), dependency.getArtifactId(), dependency.getVersion()).getParent();
                                    if (parent != null)
                                    {
                                        dependencies.add(new ArtifactDependency(parent.getGroupId(), parent.getArtifactId(), parent.getVersion()));
                                    }
                                }
                        );

            });
        }

        return dependencies;
    }

    @Override
    public List<String> getModulesFromPOM(ArtifactType type, String groupId, String artifactId, String versionId)
    {
        LOGGER.info("resolving module [{}] artifacts [{}{}{}] from pom",type,groupId, artifactId, versionId);
        Model model = getPOM(groupId, artifactId, versionId);
        List<String> modules = new ArrayList<>();
        if (model.getModules().isEmpty())
        {
            modules.add(artifactId);
        }
        else
        {
            modules.addAll(model.getModules().stream().filter(moduleName -> moduleName.equals(artifactId + SEPARATOR + type.getModuleName())).collect(Collectors.toList()));
        }
        LOGGER.info("found [{}] modules [{}] artifacts [{}:{}:{}]  from pom",modules,type,groupId, artifactId, versionId);
        return modules;
    }


    public Model getPOM(String group, String artifact, String version)
    {
        URL[] pom = null;
        try
        {
            pom = resolvePOMFromRepository(group, artifact, version);
        }
        catch (ResolutionException re)
        { // this will download the pom but wont be able to resolveArtifacts it(workaround)

        }
        String pomFileLocation;
        if (pom == null || pom.length == 0)
        {
            pomFileLocation = localRepository + File.separator + group.replace(".", File.separator) +
                    File.separator + artifact + File.separator + version + File.separator + artifact + "-" + version + "." + PackagingType.POM.getId();
        }
        else
        {
            pomFileLocation = pom[0].getFile();
        }
        LOGGER.info("pom file name has been successfully resolved {}", pomFileLocation);
        try (InputStream reader = new FileInputStream(pomFileLocation))
        {
            return mavenReader.read(reader);
        }
        catch (Exception e)
        {
            LOGGER.error("could not read {}", pomFileLocation);
            LOGGER.error(e.getMessage());
            return new Model();
        }
    }

    protected File[] resolveArtifactFilesFromRepository(String group, String artifact, String version)
    {
        return (File[]) executeWithTrace("resolveArtifactFilesFromRepository",group,artifact,version,() -> getResolver().resolve(gavCoordinates(group, artifact, version)).withoutTransitivity().asFile());
    }

    protected URL[] resolvePOMFromRepository(String group, String artifact, String version)
    {
        return (URL[]) executeWithTrace("resolvePOMFromRepository",group,artifact,version, () -> getResolver().resolve(gavCoordinates(group, artifact, PackagingType.POM, version)).withoutTransitivity().as(URL.class));
    }

    @Override
    public List<VersionId> findVersions(String group, String artifact) throws ArtifactRepositoryException
    {
            List<VersionId> result = new ArrayList<>();
            try
            {
                String groupArtifactVersionRange = gavCoordinates(group, artifact, ALL_VERSIONS_SCOPE);
                final MavenVersionRangeResult versionRangeResult = (MavenVersionRangeResult) executeWithTrace("resolveVersionsFromRepository",group,artifact,"ALL",() -> getResolver().resolveVersionRange(groupArtifactVersionRange));
                LOGGER.info("resolveVersionsFromRepository {}{}{} , Version data: [{}]", group, artifact, ALL_VERSIONS_SCOPE, versionRangeResult);
                for (MavenCoordinate coordinate : versionRangeResult.getVersions())
                {
                    if (VersionValidator.isValidReleaseVersion(coordinate.getVersion()))
                    {
                        result.add(VersionId.parseVersionId(coordinate.getVersion()));
                    }
                }
            }
            catch (VersionResolutionException ex)
            {
                LOGGER.error(String.format("Error resolveVersionsFromRepository %s-%s version resolution issue", group, artifact), ex.getMessage());
            }
            catch (Exception e)
            {
                LOGGER.error("unknown error executing resolveVersionsFromRepository", e);
                throw new ArtifactRepositoryException(e.getMessage());
            }
            Collections.sort(result);
            return result;
    }

    private Object executeWithTrace(String label, String groupId, String artifactId, String version, Supplier<Object> functionToExecute)
    {
        return TracerFactory.get().executeWithTrace(label, () ->
        {
            Map<String, String> tags = new HashMap<>();
            tags.put(GROUP_ID, groupId);
            tags.put(ARTIFACT_ID, artifactId);
            tags.put(VERSION_ID, version);
            TracerFactory.get().addTags(tags);
            return functionToExecute.get();
        });
    }
}
