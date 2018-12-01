package org.innovateuk.ifs.competitionsetup.transactional;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.builder.CompetitionDocumentResourceBuilder;
import org.innovateuk.ifs.competition.resource.CompetitionDocumentResource;
import org.innovateuk.ifs.competitionsetup.domain.CompetitionDocument;
import org.innovateuk.ifs.competitionsetup.mapper.CompetitionDocumentMapper;
import org.innovateuk.ifs.competitionsetup.repository.CompetitionDocumentConfigRepository;
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
 * Tests the CompetitionSetupDocumentServiceImpl with mocked repository.
 */
public class CompetitionSetupCompetitionDocumentServiceImplTest extends BaseServiceUnitTest<CompetitionSetupDocumentServiceImpl> {

    @Mock
    private CompetitionDocumentMapper competitionDocumentMapperMock;

    @Mock
    private CompetitionDocumentConfigRepository competitionDocumentConfigRepositoryMock;

    @Override
    protected CompetitionSetupDocumentServiceImpl supplyServiceUnderTest() {
        return new CompetitionSetupDocumentServiceImpl();
    }

    @Test
    public void saveWhenNoFileTypeSelected() {

        CompetitionDocumentResource competitionDocumentResource = new CompetitionDocumentResource();
        ServiceResult<CompetitionDocumentResource> result = service.save(competitionDocumentResource);

        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(FILES_SELECT_AT_LEAST_ONE_FILE_TYPE));

        verify(competitionDocumentConfigRepositoryMock, never()).save(any(CompetitionDocument.class));
    }

    @Test
    public void save() {

        CompetitionDocumentResource competitionDocumentResource = CompetitionDocumentResourceBuilder.neCompetitionDocumentResource()
                .withFileType(singletonList(1L))
                .build();
        CompetitionDocument competitionDocument = new CompetitionDocument();

        when(competitionDocumentMapperMock.mapToDomain(competitionDocumentResource)).thenReturn(competitionDocument);
        when(competitionDocumentConfigRepositoryMock.save(competitionDocument)).thenReturn(competitionDocument);
        when(competitionDocumentMapperMock.mapToResource(competitionDocument)).thenReturn(competitionDocumentResource);

        ServiceResult<CompetitionDocumentResource> result = service.save(competitionDocumentResource);

        assertTrue(result.isSuccess());
        assertEquals(competitionDocumentResource, result.getSuccess());

        verify(competitionDocumentConfigRepositoryMock).save(competitionDocument);
    }

    @Test
    public void saveAllWhenNoFileTypeSelected() {
        List<CompetitionDocumentResource> competitionDocumentResources = CompetitionDocumentResourceBuilder.neCompetitionDocumentResource()
                .withFileType(singletonList(1L), emptyList())
                .build(2);

        ServiceResult<List<CompetitionDocumentResource>> result = service.saveAll(competitionDocumentResources);

        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(FILES_SELECT_AT_LEAST_ONE_FILE_TYPE));

        verify(competitionDocumentConfigRepositoryMock, never()).save(any(List.class));
    }

    @Test
    public void saveAll() {

        List<CompetitionDocumentResource> competitionDocumentResources = CompetitionDocumentResourceBuilder.neCompetitionDocumentResource()
                .withId(1L, 2L)
                .withFileType(singletonList(1L))
                .build(2);
        CompetitionDocument competitionDocument1 = new CompetitionDocument();
        competitionDocument1.setId(1L);
        CompetitionDocument competitionDocument2 = new CompetitionDocument();
        competitionDocument2.setId(2L);
        List<CompetitionDocument> competitionDocuments = new ArrayList<>();
        competitionDocuments.add(competitionDocument1);
        competitionDocuments.add(competitionDocument2);

        when(competitionDocumentMapperMock.mapToDomain(competitionDocumentResources.get(0))).thenReturn(competitionDocument1);
        when(competitionDocumentMapperMock.mapToDomain(competitionDocumentResources.get(1))).thenReturn(competitionDocument2);
        when(competitionDocumentConfigRepositoryMock.save(competitionDocuments)).thenReturn(competitionDocuments);
        when(competitionDocumentMapperMock.mapToResource(competitionDocument1)).thenReturn(competitionDocumentResources.get(0));
        when(competitionDocumentMapperMock.mapToResource(competitionDocument2)).thenReturn(competitionDocumentResources.get(1));

        ServiceResult<List<CompetitionDocumentResource>> result = service.saveAll(competitionDocumentResources);

        assertTrue(result.isSuccess());
        assertEquals(competitionDocumentResources.get(0), result.getSuccess().get(0));
        assertEquals(competitionDocumentResources.get(1), result.getSuccess().get(1));

        verify(competitionDocumentConfigRepositoryMock).save(competitionDocuments);
    }

    @Test
    public void findOne() {

        Long projectDocumentId = 1L;

        CompetitionDocument competitionDocument = new CompetitionDocument();
        CompetitionDocumentResource competitionDocumentResource = new CompetitionDocumentResource();

        when(competitionDocumentConfigRepositoryMock.findOne(projectDocumentId)).thenReturn(competitionDocument);
        when(competitionDocumentMapperMock.mapToResource(competitionDocument)).thenReturn(competitionDocumentResource);

        ServiceResult<CompetitionDocumentResource> result = service.findOne(projectDocumentId);

        assertTrue(result.isSuccess());
        assertEquals(competitionDocumentResource, result.getSuccess());

        verify(competitionDocumentConfigRepositoryMock).findOne(projectDocumentId);
    }

    @Test
    public void findByCompetitionId() {

        Long competitionId = 1L;

        CompetitionDocument competitionDocument1 = new CompetitionDocument();
        CompetitionDocument competitionDocument2 = new CompetitionDocument();

        List<CompetitionDocument> competitionDocuments = Arrays.asList(competitionDocument1, competitionDocument2);

        CompetitionDocumentResource competitionDocumentResource1 = new CompetitionDocumentResource();
        CompetitionDocumentResource competitionDocumentResource2 = new CompetitionDocumentResource();

        when(competitionDocumentConfigRepositoryMock.findByCompetitionId(competitionId)).thenReturn(competitionDocuments);
        when(competitionDocumentMapperMock.mapToResource(competitionDocument1)).thenReturn(competitionDocumentResource1);
        when(competitionDocumentMapperMock.mapToResource(competitionDocument2)).thenReturn(competitionDocumentResource2);

        ServiceResult<List<CompetitionDocumentResource>> result = service.findByCompetitionId(competitionId);

        assertTrue(result.isSuccess());
        assertEquals(competitionDocumentResource1, result.getSuccess().get(0));
        assertEquals(competitionDocumentResource2, result.getSuccess().get(1));

        verify(competitionDocumentConfigRepositoryMock).findByCompetitionId(competitionId);
    }

    @Test
    public void delete() {

        Long projectDocumentId = 1L;

        ServiceResult<Void> result = service.delete(projectDocumentId);

        assertTrue(result.isSuccess());
        verify(competitionDocumentConfigRepositoryMock).delete(projectDocumentId);
    }
}
