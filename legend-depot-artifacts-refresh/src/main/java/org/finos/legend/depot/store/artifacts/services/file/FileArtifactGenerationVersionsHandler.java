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

package org.finos.legend.depot.store.artifacts.services.file;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.finos.legend.depot.domain.api.MetadataEventResponse;
import org.finos.legend.depot.domain.generation.artifact.ArtifactGeneration;
import org.finos.legend.depot.domain.generation.file.FileGeneration;
import org.finos.legend.depot.domain.project.ProjectData;
import org.finos.legend.depot.store.artifacts.api.generation.FileArtifactGenerationsVersionArtifactsHandler;
import org.finos.legend.depot.store.artifacts.api.generation.artifact.ArtifactGenerationsVersionArtifactsHandler;
import org.finos.legend.depot.store.artifacts.api.generation.file.FileGenerationsProvider;
import org.finos.legend.depot.store.artifacts.api.generation.file.FileGenerationsVersionArtifactsHandler;


public class FileArtifactGenerationVersionsHandler implements FileArtifactGenerationsVersionArtifactsHandler
{

    private final FileGenerationsProvider fileGenerationsProvider;
    private final ArtifactGenerationsVersionArtifactsHandler artifactGenerationHandler;
    private final FileGenerationsVersionArtifactsHandler fileGenerationHandler;

    @Inject
    public FileArtifactGenerationVersionsHandler(FileGenerationsProvider fileGenerationsProvider, ArtifactGenerationsVersionArtifactsHandler artifactGenerationHandler, FileGenerationsVersionArtifactsHandler fileGenerationHandler)
    {
        this.fileGenerationsProvider = fileGenerationsProvider;
        this.artifactGenerationHandler = artifactGenerationHandler;
        this.fileGenerationHandler = fileGenerationHandler;
    }

    @Override
    public MetadataEventResponse refreshProjectVersionArtifacts(ProjectData project, String versionId, List<File> files)
    {
        List<FileGeneration> fileGenerations = this.fileGenerationsProvider.loadArtifacts(files);
        MetadataEventResponse response = new MetadataEventResponse();
        response.combine(this.fileGenerationHandler.refreshProjectVersionArtifacts(project, versionId, fileGenerations));
        response.combine(this.artifactGenerationHandler.refreshProjectVersionArtifacts(project, versionId,
            fileGenerations.stream().map(e -> new ArtifactGeneration(e.getPath(), e.getContent())).collect(Collectors.toList())));
        return response;
    }

    @Override
    public void delete(String groupId, String artifactId, String versionId)
    {
        this.fileGenerationHandler.delete(groupId, artifactId, versionId);
        this.artifactGenerationHandler.delete(groupId, artifactId, versionId);
    }
}
