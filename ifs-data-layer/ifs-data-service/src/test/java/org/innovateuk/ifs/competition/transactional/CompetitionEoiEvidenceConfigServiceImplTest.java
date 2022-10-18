package org.innovateuk.ifs.competition.transactional;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.domain.CompetitionEoiDocument;
import org.innovateuk.ifs.competition.domain.CompetitionEoiEvidenceConfig;
import org.innovateuk.ifs.competition.mapper.CompetitionEoiDocumentMapper;
import org.innovateuk.ifs.competition.mapper.CompetitionEoiEvidenceConfigMapper;
import org.innovateuk.ifs.competition.repository.CompetitionEoiDocumentRepository;
import org.innovateuk.ifs.competition.repository.CompetitionEoiEvidenceConfigRepository;
import org.innovateuk.ifs.competition.repository.CompetitionRepository;
import org.innovateuk.ifs.competition.resource.CompetitionEoiDocumentResource;
import org.innovateuk.ifs.competition.resource.CompetitionEoiEvidenceConfigResource;
import org.innovateuk.ifs.file.domain.FileType;
import org.innovateuk.ifs.file.repository.FileTypeRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.innovateuk.ifs.file.builder.FileTypeBuilder.newFileType;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

public class CompetitionEoiEvidenceConfigServiceImplTest extends BaseServiceUnitTest<CompetitionEoiEvidenceConfigServiceImpl> {

    @Mock
    private CompetitionRepository competitionRepository;

    @Mock
    private CompetitionEoiDocumentRepository competitionEoiDocumentRepository;

    @Mock
    private CompetitionEoiEvidenceConfigMapper competitionEoiEvidenceConfigMapper;

    @Mock
    private CompetitionEoiDocumentMapper competitionEoiDocumentMapper;

    @Mock
    private FileTypeRepository fileTypeRepository;

    @Mock
    private CompetitionEoiEvidenceConfigRepository competitionEoiEvidenceConfigRepository;

    private Long competitionId;
    private Long fileTypeId;
    private String evidenceTitle = "title";
    private String evidenceGuidance = "guidance";
    private CompetitionEoiEvidenceConfigResource competitionEoiEvidenceConfigResource;
    private CompetitionEoiDocumentResource competitionEoiDocumentResource;
    private Competition competition;
    private CompetitionEoiEvidenceConfig competitionEoiEvidenceConfig;

    @Override
    protected CompetitionEoiEvidenceConfigServiceImpl supplyServiceUnderTest() {
        return new CompetitionEoiEvidenceConfigServiceImpl();
    }

    @Before
    public void setup() {

        competitionId = 1L;
        fileTypeId = 2L;
        competitionEoiEvidenceConfigResource = CompetitionEoiEvidenceConfigResource.builder()
                .id(3L)
                .competitionId(competitionId)
                .evidenceTitle(evidenceTitle)
                .evidenceGuidance(evidenceGuidance)
                .build();
        competitionEoiDocumentResource = CompetitionEoiDocumentResource.builder()
                .fileTypeId(fileTypeId)
                .build();
        competition = newCompetition()
                .withId(competitionId)
                .build();
        competitionEoiEvidenceConfig = CompetitionEoiEvidenceConfig.builder()
                .id(2L)
                .competition(competition)
                .evidenceRequired(true)
                .evidenceTitle(evidenceTitle)
                .evidenceGuidance(evidenceGuidance)
                .build();
    }

    @Test
    public void create() {

        when(competitionRepository.findById(competitionId)).thenReturn(Optional.of(competition));
        when(competitionEoiEvidenceConfigMapper.mapToDomain(competitionEoiEvidenceConfigResource)).thenReturn(competitionEoiEvidenceConfig);
        when(competitionEoiEvidenceConfigMapper.mapToResource(competitionEoiEvidenceConfig)).thenReturn(competitionEoiEvidenceConfigResource);

        ServiceResult<CompetitionEoiEvidenceConfigResource> result = service.create(competitionEoiEvidenceConfigResource);

        assertTrue(result.isSuccess());
    }

    @Test
    public void createThrowsCompetitionNotFound() {

        when(competitionRepository.findById(competitionId)).thenReturn(Optional.empty());

        ServiceResult<CompetitionEoiEvidenceConfigResource> result = service.create(competitionEoiEvidenceConfigResource);

        assertTrue(result.isFailure());

        assertEquals(1, result.getErrors().size());
        assertEquals(HttpStatus.NOT_FOUND, result.getErrors().get(0).getStatusCode());
        assertEquals("GENERAL_NOT_FOUND", result.getErrors().get(0).getErrorKey());
    }

    @Test
    public void createDocument() {

        FileType pdfFileType = newFileType()
                .withName("PDF")
                .withExtension(".pdf")
                .build();
        CompetitionEoiDocument competitionEoiDocument = CompetitionEoiDocument.builder()
                .fileType(pdfFileType)
                .build();

        when(fileTypeRepository.findById(fileTypeId)).thenReturn(Optional.of(pdfFileType));
        when(competitionEoiDocumentMapper.mapToDomain(competitionEoiDocumentResource)).thenReturn(competitionEoiDocument);
        when(competitionEoiDocumentRepository.save(competitionEoiDocument)).thenReturn(competitionEoiDocument);
        when(competitionEoiDocumentMapper.mapToResource(competitionEoiDocument)).thenReturn(competitionEoiDocumentResource);

        ServiceResult<CompetitionEoiDocumentResource> result = service.createDocument(competitionEoiDocumentResource);

        assertTrue(result.isSuccess());
    }

    @Test
    public void createDocumentThrowsFileTypeNotFound() {

        when(fileTypeRepository.findById(fileTypeId)).thenReturn(Optional.empty());

        ServiceResult<CompetitionEoiDocumentResource> result = service.createDocument(competitionEoiDocumentResource);

        assertTrue(result.isFailure());

        assertEquals(1, result.getErrors().size());
        assertEquals(HttpStatus.NOT_FOUND, result.getErrors().get(0).getStatusCode());
        assertEquals("GENERAL_NOT_FOUND", result.getErrors().get(0).getErrorKey());
    }

    @Test
    public void findAllByCompetitionEoiDocumentResources() {
        List<CompetitionEoiDocument> competitionEoiDocument = new ArrayList<>();
        competitionEoiDocument.add(new CompetitionEoiDocument(1L, competitionEoiEvidenceConfig, newFileType().withName("PDF").build()));

        competitionEoiDocumentResource.setCompetitionEoiEvidenceConfigId(competitionEoiEvidenceConfig.getId());
        competitionEoiDocumentResource.setFileTypeId(competitionEoiDocument.get(0).getFileType().getId());

        when(competitionEoiDocumentRepository.findByCompetitionEoiEvidenceConfigId(competitionEoiEvidenceConfig.getId())).thenReturn(competitionEoiDocument);
        when(competitionEoiDocumentMapper.mapToResource(competitionEoiDocument)).thenReturn(Collections.singletonList(competitionEoiDocumentResource));

        ServiceResult<List<CompetitionEoiDocumentResource>> result = service.findAllByCompetitionEoiDocumentResources(competitionEoiEvidenceConfig.getId());
        assertTrue(result.isSuccess());

        assertEquals(1, result.getSuccess().size());
    }

    @Test
    public void getValidFileTypesIdsForEoiEvidence() {
        List<CompetitionEoiDocument> competitionEoiDocument = new ArrayList<>();
        competitionEoiDocument.add(new CompetitionEoiDocument(1L, competitionEoiEvidenceConfig, newFileType().withName("PDF").build()));

        competitionEoiDocumentResource.setCompetitionEoiEvidenceConfigId(competitionEoiEvidenceConfig.getId());
        competitionEoiDocumentResource.setFileTypeId(competitionEoiDocument.get(0).getFileType().getId());

        when(competitionEoiDocumentRepository.findByCompetitionEoiEvidenceConfigId(competitionEoiEvidenceConfig.getId())).thenReturn(competitionEoiDocument);
        when(competitionEoiDocumentMapper.mapToResource(competitionEoiDocument)).thenReturn(Collections.singletonList(competitionEoiDocumentResource));

        ServiceResult<List<Long>> result = service.getValidFileTypesIdsForEoiEvidence(competitionEoiEvidenceConfig.getId());
        assertTrue(result.isSuccess());

        assertEquals(1, result.getSuccess().size());
    }

    @Test
    public void findOneByCompetitionId() {
        CompetitionEoiEvidenceConfig competitionEoiEvidenceConfig = CompetitionEoiEvidenceConfig.builder()
                .competition(competition)
                .build();

        CompetitionEoiEvidenceConfigResource competitionEoiEvidenceConfigResource = CompetitionEoiEvidenceConfigResource.builder()
                .competitionId(competitionId)
                .build();

        when(competitionEoiEvidenceConfigRepository.findOneByCompetitionId(competitionId)).thenReturn(Optional.of(competitionEoiEvidenceConfig));
        when(competitionEoiEvidenceConfigMapper.mapToResource(competitionEoiEvidenceConfig)).thenReturn(competitionEoiEvidenceConfigResource);

        ServiceResult<CompetitionEoiEvidenceConfigResource> result = service.findOneByCompetitionId(competitionId);
        assertTrue(result.isSuccess());
        assertNotNull(result.getSuccess());
        assertEquals(competitionId, result.getSuccess().getCompetitionId());
    }

    @Test
    public void findOneByCompetitionIdNotFound() {
        when(competitionEoiEvidenceConfigRepository.findOneByCompetitionId(competitionId)).thenReturn(Optional.empty());

        ServiceResult<CompetitionEoiEvidenceConfigResource> result = service.findOneByCompetitionId(competitionId);
        assertTrue(result.isFailure());

        assertEquals(1, result.getErrors().size());
        assertEquals(HttpStatus.NOT_FOUND, result.getErrors().get(0).getStatusCode());
        assertEquals("GENERAL_NOT_FOUND", result.getErrors().get(0).getErrorKey());
    }

}
