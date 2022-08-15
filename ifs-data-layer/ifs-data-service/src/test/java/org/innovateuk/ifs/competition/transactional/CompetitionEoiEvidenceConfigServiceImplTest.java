package org.innovateuk.ifs.competition.transactional;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.domain.CompetitionEoiDocument;
import org.innovateuk.ifs.competition.domain.CompetitionEoiEvidenceConfig;
import org.innovateuk.ifs.competition.mapper.CompetitionEoiDocumentMapper;
import org.innovateuk.ifs.competition.mapper.CompetitionEoiEvidenceConfigMapper;
import org.innovateuk.ifs.competition.repository.CompetitionEoiDocumentRepository;
import org.innovateuk.ifs.competition.repository.CompetitionRepository;
import org.innovateuk.ifs.competition.resource.CompetitionEoiDocumentResource;
import org.innovateuk.ifs.competition.resource.CompetitionEoiEvidenceConfigResource;
import org.innovateuk.ifs.file.builder.FileTypeBuilder;
import org.innovateuk.ifs.file.domain.FileType;
import org.innovateuk.ifs.file.repository.FileTypeRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;

import java.util.Optional;

import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
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

    private Long competitionId;
    private Long fileTypeId;
    private String evidenceTitle = "title";
    private String evidenceGuidance = "guidance";
    private CompetitionEoiEvidenceConfigResource competitionEoiEvidenceConfigResource;
    private CompetitionEoiDocumentResource competitionEoiDocumentResource;

    @Override
    protected CompetitionEoiEvidenceConfigServiceImpl supplyServiceUnderTest() {
        return new CompetitionEoiEvidenceConfigServiceImpl();
    }

    @Before
    public void setup() {

        competitionId = 1L;
        fileTypeId = 2L;
        competitionEoiEvidenceConfigResource = CompetitionEoiEvidenceConfigResource.builder()
                .competitionId(competitionId)
                .evidenceTitle(evidenceTitle)
                .evidenceGuidance(evidenceGuidance)
                .build();
        competitionEoiDocumentResource = CompetitionEoiDocumentResource.builder()
                .fileTypeId(fileTypeId)
                .build();
    }

    @Test
    public void create() {

        Competition competition = newCompetition()
                .withId(competitionId)
                .build();
        CompetitionEoiEvidenceConfig competitionEoiEvidenceConfig = CompetitionEoiEvidenceConfig.builder()
                .competition(competition)
                .evidenceRequired(true)
                .evidenceTitle(evidenceTitle)
                .evidenceGuidance(evidenceGuidance)
                .build();

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

        FileType pdfFileType = FileTypeBuilder.newFileType()
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
}
