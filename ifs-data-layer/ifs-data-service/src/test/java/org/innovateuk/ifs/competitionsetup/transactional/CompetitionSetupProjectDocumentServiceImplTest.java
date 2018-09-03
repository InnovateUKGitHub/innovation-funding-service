package org.innovateuk.ifs.competitionsetup.transactional;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.builder.ProjectDocumentResourceBuilder;
import org.innovateuk.ifs.competition.resource.ProjectDocumentResource;
import org.innovateuk.ifs.competitionsetup.domain.ProjectDocument;
import org.innovateuk.ifs.competitionsetup.mapper.ProjectDocumentMapper;
import org.innovateuk.ifs.competitionsetup.repository.ProjectDocumentRepository;
import org.junit.Test;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.util.Collections.*;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.FILES_SELECT_AT_LEAST_ONE_FILE_TYPE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Tests the CompetitionSetupProjectDocumentServiceImpl with mocked repository.
 */
public class CompetitionSetupProjectDocumentServiceImplTest extends BaseServiceUnitTest<CompetitionSetupProjectDocumentServiceImpl> {

    @Mock
    private ProjectDocumentMapper projectDocumentMapperMock;

    @Mock
    private ProjectDocumentRepository projectDocumentRepositoryMock;

    @Override
    protected CompetitionSetupProjectDocumentServiceImpl supplyServiceUnderTest() {
        return new CompetitionSetupProjectDocumentServiceImpl();
    }

    @Test
    public void saveWhenNoFileTypeSelected() {

        ProjectDocumentResource projectDocumentResource = new ProjectDocumentResource();
        ServiceResult<ProjectDocumentResource> result = service.save(projectDocumentResource);

        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(FILES_SELECT_AT_LEAST_ONE_FILE_TYPE));

        verify(projectDocumentRepositoryMock, never()).save(any(ProjectDocument.class));
    }

    @Test
    public void save() {

        ProjectDocumentResource projectDocumentResource = ProjectDocumentResourceBuilder.newProjectDocumentResource()
                .withFileType(singletonList(1L))
                .build();
        ProjectDocument projectDocument = new ProjectDocument();

        when(projectDocumentMapperMock.mapToDomain(projectDocumentResource)).thenReturn(projectDocument);
        when(projectDocumentRepositoryMock.save(projectDocument)).thenReturn(projectDocument);
        when(projectDocumentMapperMock.mapToResource(projectDocument)).thenReturn(projectDocumentResource);

        ServiceResult<ProjectDocumentResource> result = service.save(projectDocumentResource);

        assertTrue(result.isSuccess());
        assertEquals(projectDocumentResource, result.getSuccess());

        verify(projectDocumentRepositoryMock).save(projectDocument);
    }

    @Test
    public void saveAllWhenNoFileTypeSelected() {
        List<ProjectDocumentResource> projectDocumentResources = ProjectDocumentResourceBuilder.newProjectDocumentResource()
                .withFileType(singletonList(1L), emptyList())
                .build(2);

        ServiceResult<List<ProjectDocumentResource>> result = service.saveAll(projectDocumentResources);

        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(FILES_SELECT_AT_LEAST_ONE_FILE_TYPE));

        verify(projectDocumentRepositoryMock, never()).save(any(List.class));
    }

    @Test
    public void saveAll() {

        List<ProjectDocumentResource> projectDocumentResources = ProjectDocumentResourceBuilder.newProjectDocumentResource()
                .withId(1L, 2L)
                .withFileType(singletonList(1L))
                .build(2);
        ProjectDocument projectDocument1 = new ProjectDocument();
        projectDocument1.setId(1L);
        ProjectDocument projectDocument2 = new ProjectDocument();
        projectDocument2.setId(2L);
        List<ProjectDocument> projectDocuments = new ArrayList<>();
        projectDocuments.add(projectDocument1);
        projectDocuments.add(projectDocument2);

        when(projectDocumentMapperMock.mapToDomain(projectDocumentResources.get(0))).thenReturn(projectDocument1);
        when(projectDocumentMapperMock.mapToDomain(projectDocumentResources.get(1))).thenReturn(projectDocument2);
        when(projectDocumentRepositoryMock.save(projectDocuments)).thenReturn(projectDocuments);
        when(projectDocumentMapperMock.mapToResource(projectDocument1)).thenReturn(projectDocumentResources.get(0));
        when(projectDocumentMapperMock.mapToResource(projectDocument2)).thenReturn(projectDocumentResources.get(1));

        ServiceResult<List<ProjectDocumentResource>> result = service.saveAll(projectDocumentResources);

        assertTrue(result.isSuccess());
        assertEquals(projectDocumentResources.get(0), result.getSuccess().get(0));
        assertEquals(projectDocumentResources.get(1), result.getSuccess().get(1));

        verify(projectDocumentRepositoryMock).save(projectDocuments);
    }

    @Test
    public void findOne() {

        Long projectDocumentId = 1L;

        ProjectDocument projectDocument = new ProjectDocument();
        ProjectDocumentResource projectDocumentResource = new ProjectDocumentResource();

        when(projectDocumentRepositoryMock.findOne(projectDocumentId)).thenReturn(projectDocument);
        when(projectDocumentMapperMock.mapToResource(projectDocument)).thenReturn(projectDocumentResource);

        ServiceResult<ProjectDocumentResource> result = service.findOne(projectDocumentId);

        assertTrue(result.isSuccess());
        assertEquals(projectDocumentResource, result.getSuccess());

        verify(projectDocumentRepositoryMock).findOne(projectDocumentId);
    }

    @Test
    public void findByCompetitionId() {

        Long competitionId = 1L;

        ProjectDocument projectDocument1 = new ProjectDocument();
        ProjectDocument projectDocument2 = new ProjectDocument();

        List<ProjectDocument> projectDocuments = Arrays.asList(projectDocument1, projectDocument2);

        ProjectDocumentResource projectDocumentResource1 = new ProjectDocumentResource();
        ProjectDocumentResource projectDocumentResource2 = new ProjectDocumentResource();

        when(projectDocumentRepositoryMock.findByCompetitionId(competitionId)).thenReturn(projectDocuments);
        when(projectDocumentMapperMock.mapToResource(projectDocument1)).thenReturn(projectDocumentResource1);
        when(projectDocumentMapperMock.mapToResource(projectDocument2)).thenReturn(projectDocumentResource2);

        ServiceResult<List<ProjectDocumentResource>> result = service.findByCompetitionId(competitionId);

        assertTrue(result.isSuccess());
        assertEquals(projectDocumentResource1, result.getSuccess().get(0));
        assertEquals(projectDocumentResource2, result.getSuccess().get(1));

        verify(projectDocumentRepositoryMock).findByCompetitionId(competitionId);
    }

    @Test
    public void delete() {

        Long projectDocumentId = 1L;

        ServiceResult<Void> result = service.delete(projectDocumentId);

        assertTrue(result.isSuccess());
        verify(projectDocumentRepositoryMock).delete(projectDocumentId);
    }
}
