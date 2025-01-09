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

package org.finos.legend.depot.services.artifacts.reconciliation;

import org.finos.legend.depot.services.api.artifacts.reconciliation.VersionsReconciliationService;
import org.finos.legend.depot.services.api.artifacts.repository.ArtifactRepository;
import org.finos.legend.depot.services.api.artifacts.repository.ArtifactRepositoryException;
import org.finos.legend.depot.services.api.projects.ManageProjectsService;
import org.finos.legend.depot.store.model.projects.StoreProjectData;
import org.finos.legend.depot.store.model.projects.StoreProjectVersionData;
import org.finos.legend.depot.domain.version.VersionMismatch;
import org.finos.legend.sdlc.domain.model.version.VersionId;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TestVersionsMismatchService
{
    protected ArtifactRepository repository = mock(ArtifactRepository.class);
    protected ManageProjectsService projects = mock(ManageProjectsService.class);
    protected VersionsReconciliationService repositoryServices = new VersionsReconciliationServiceImpl(repository, projects);

    @BeforeEach
    public void setup() throws ArtifactRepositoryException
    {
        List<StoreProjectData> coordinates = new ArrayList<>();
        coordinates.add(new StoreProjectData("PROD-A","examples.metadata", "test1", null, "2.1.0"));
        coordinates.add(new StoreProjectData("PROD-B","examples.metadata", "test2", null, "1.0.0"));
        coordinates.add(new StoreProjectData("PROD-C","examples.metadata", "test3", null, "2.0.2"));
        coordinates.add(new StoreProjectData("PROD-D","examples.metadata", "test4"));
        when(projects.getAllProjectCoordinates()).thenReturn(coordinates);
        StoreProjectVersionData p1v1 = new StoreProjectVersionData("examples.metadata", "test1", "2.2.0");
        StoreProjectVersionData p1v2 = new StoreProjectVersionData("examples.metadata", "test1", "2.3.0");
        StoreProjectVersionData p2v1 = new StoreProjectVersionData("examples.metadata", "test2", "1.0.0");
        StoreProjectVersionData p3v1 = new StoreProjectVersionData("examples.metadata", "test3", "2.0.1");
        StoreProjectVersionData p4v1 = new StoreProjectVersionData("examples.metadata", "test4", "0.0.1");
        when(projects.find("examples.metadata", "test1")).thenReturn(Arrays.asList(p1v1, p1v2));
        when(projects.find("examples.metadata", "test2")).thenReturn(Arrays.asList(p2v1));
        when(projects.find("examples.metadata", "test3")).thenReturn(Arrays.asList(p3v1));
        when(projects.find("examples.metadata", "test4")).thenReturn(Arrays.asList(p4v1));
        when(repository.findVersions("examples.metadata", "test1")).thenReturn(Arrays.asList(VersionId.parseVersionId("2.2.0"),VersionId.parseVersionId("2.3.0"), VersionId.parseVersionId("2.3.1")));
        when(repository.findVersions("examples.metadata", "test2")).thenReturn(Arrays.asList(VersionId.parseVersionId("1.0.1")));
        when(repository.findVersions("examples.metadata", "test3")).thenReturn(Collections.emptyList());
        when(repository.findVersions("examples.metadata", "test4")).thenReturn(Arrays.asList(VersionId.parseVersionId("0.0.1")));
    }

    @Test
    public void getVersionsMismatch()
    {

        List<VersionMismatch> counts = repositoryServices.findVersionsMismatches();
        Assertions.assertNotNull(counts);
        Assertions.assertEquals(3, counts.size());
        Assertions.assertEquals(1, counts.stream().filter(p -> p.projectId.equals("PROD-A")).count());
        Assertions.assertEquals(1, counts.stream().filter(p -> p.projectId.equals("PROD-B")).count());
        Assertions.assertEquals(1, counts.stream().filter(p -> p.projectId.equals("PROD-C")).count());

        VersionMismatch prodA = counts.stream().filter(p -> p.projectId.equals("PROD-A")).findFirst().get();
        Assertions.assertEquals(1, prodA.versionsNotInStore.size());
        Assertions.assertEquals("2.3.1", prodA.versionsNotInStore.get(0));
        VersionMismatch prodB = counts.stream().filter(p -> p.projectId.equals("PROD-B")).findFirst().get();
        Assertions.assertEquals("1.0.1", prodB.versionsNotInStore.get(0));
        Assertions.assertEquals("1.0.0", prodB.versionsNotInRepository.get(0));
        VersionMismatch prodC = counts.stream().filter(p -> p.projectId.equals("PROD-C")).findFirst().get();
        Assertions.assertEquals("2.0.1", prodC.versionsNotInRepository.get(0));


    }

    @Test
    public void getVersionsMismatchWithExceptions() throws ArtifactRepositoryException
    {
        when(repository.findVersions("examples.metadata", "test4")).thenThrow(new ArtifactRepositoryException("not found"));

        List<VersionMismatch> counts = repositoryServices.findVersionsMismatches();
        Assertions.assertNotNull(counts);
        Assertions.assertEquals(4, counts.size());
        Assertions.assertEquals(1, counts.stream().filter(p -> p.projectId.equals("PROD-A")).count());
        Assertions.assertEquals(1, counts.stream().filter(p -> p.projectId.equals("PROD-B")).count());
        Assertions.assertEquals(1, counts.stream().filter(p -> p.projectId.equals("PROD-C")).count());
        Assertions.assertEquals(1, counts.stream().filter(p -> p.projectId.equals("PROD-D")).count());

        VersionMismatch prodD = counts.stream().filter(p -> p.projectId.equals("PROD-D")).findFirst().get();
        Assertions.assertFalse(prodD.errors.isEmpty());



    }

    @Test
    public void getVersionsMismatchIfExcludedVersionsPresent() throws ArtifactRepositoryException
    {
        StoreProjectVersionData p1v1 = new StoreProjectVersionData("examples.metadata", "test5", "1.0.0");
        p1v1.getVersionData().setExcluded(true);
        p1v1.getVersionData().setExclusionReason("unknown error");
        when(projects.find("examples.metadata", "test5")).thenReturn(Arrays.asList(p1v1));
        when(repository.findVersions("examples.metadata", "test5")).thenReturn(Arrays.asList(VersionId.parseVersionId("1.0.0")));
        List<VersionMismatch> counts = repositoryServices.findVersionsMismatches();
        Assertions.assertNotNull(counts);
        Assertions.assertEquals(3, counts.size());
        Assertions.assertEquals(0, counts.stream().filter(p -> p.artifactId.equals("test5")).count());

    }

    @Test
    public void getProjectsWithUpdatedLatestVersions()
    {
        List<StoreProjectData> projectsWithUpdatedLatestVersions = repositoryServices.syncLatestProjectVersions();
        Assertions.assertEquals(2, projectsWithUpdatedLatestVersions.size());
        Assertions.assertEquals(1, projectsWithUpdatedLatestVersions.stream().filter(p -> p.getProjectId().equals("PROD-A")).count());
        Assertions.assertEquals(1, projectsWithUpdatedLatestVersions.stream().filter(p -> p.getProjectId().equals("PROD-D")).count());
        StoreProjectData prodA = projectsWithUpdatedLatestVersions.stream().filter(p -> p.getProjectId().equals("PROD-A")).findFirst().get();
        StoreProjectData prodD = projectsWithUpdatedLatestVersions.stream().filter(p -> p.getProjectId().equals("PROD-D")).findFirst().get();
        Assertions.assertEquals("2.3.0", prodA.getLatestVersion());
        Assertions.assertEquals("0.0.1", prodD.getLatestVersion());
    }

    @Test
    public void getProjectsWithUpdatedLatestVersionsIfExcludedAndEvictedVersionsPresent()
    {
        List<StoreProjectData> coordinates = new ArrayList<>();
        coordinates.add(new StoreProjectData("PROD-A","examples.metadata", "test1", null, "2.1.0"));
        coordinates.add(new StoreProjectData("PROD-B","examples.metadata", "test2", null, "1.0.0"));
        coordinates.add(new StoreProjectData("PROD-C","examples.metadata", "test3", null, "2.0.2"));
        when(projects.getAllProjectCoordinates()).thenReturn(coordinates);
        StoreProjectVersionData p1v1 = new StoreProjectVersionData("examples.metadata", "test1", "2.2.0");
        StoreProjectVersionData p1v2 = new StoreProjectVersionData("examples.metadata", "test1", "2.3.0");
        p1v2.getVersionData().setExcluded(true);
        p1v2.getVersionData().setExclusionReason("unknown error");
        StoreProjectVersionData p2v1 = new StoreProjectVersionData("examples.metadata", "test2", "1.0.1");
        p2v1.setEvicted(true);
        StoreProjectVersionData p3v1 = new StoreProjectVersionData("examples.metadata", "test3", "2.0.3");
        when(projects.find("examples.metadata", "test1")).thenReturn(Arrays.asList(p1v1, p1v2));
        when(projects.find("examples.metadata", "test2")).thenReturn(Arrays.asList(p2v1));
        when(projects.find("examples.metadata", "test3")).thenReturn(Arrays.asList(p3v1));

        List<StoreProjectData> projectsWithUpdatedLatestVersions = repositoryServices.syncLatestProjectVersions();
        Assertions.assertEquals(1, projectsWithUpdatedLatestVersions.stream().filter(p -> p.getProjectId().equals("PROD-A")).count());
        Assertions.assertEquals(1, projectsWithUpdatedLatestVersions.stream().filter(p -> p.getProjectId().equals("PROD-C")).count());
        StoreProjectData prodA = projectsWithUpdatedLatestVersions.stream().filter(p -> p.getProjectId().equals("PROD-A")).findFirst().get();
        StoreProjectData prodC = projectsWithUpdatedLatestVersions.stream().filter(p -> p.getProjectId().equals("PROD-C")).findFirst().get();
        Assertions.assertEquals("2.2.0", prodA.getLatestVersion());
        Assertions.assertEquals("2.0.3", prodC.getLatestVersion());
    }
}
