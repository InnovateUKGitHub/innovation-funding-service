package org.innovateuk.ifs.application.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.transactional.ApplicationEoiEvidenceResponseService;
import org.innovateuk.ifs.application.transactional.ApplicationService;
import org.innovateuk.ifs.competition.resource.CompetitionEoiEvidenceConfigResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.transactional.CompetitionEoiEvidenceConfigService;
import org.innovateuk.ifs.competition.transactional.CompetitionService;
import org.innovateuk.ifs.file.controller.FileControllerUtils;
import org.innovateuk.ifs.file.controller.FilesizeAndTypeFileValidator;
import org.innovateuk.ifs.file.repository.FileTypeRepository;
import org.innovateuk.ifs.file.service.FileService;
import org.innovateuk.ifs.file.service.FileTypeService;
import org.innovateuk.ifs.user.mapper.UserMapper;
import org.innovateuk.ifs.user.repository.UserRepository;
import org.innovateuk.ifs.user.transactional.BaseUserService;
import org.junit.Before;
import org.mockito.Mock;

import java.util.List;

import static java.util.Arrays.asList;
import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;

public class ApplicationEoiEvidenceResponseControllerTest extends BaseControllerMockMVCTest<ApplicationEoiEvidenceResponseController> {

    @Mock
    private ApplicationEoiEvidenceResponseService applicationEoiEvidenceResponseService;

    @Mock
    private ApplicationService applicationService;

    @Mock
    private CompetitionService competitionService;

    @Mock
    private CompetitionEoiEvidenceConfigService competitionEoiEvidenceConfigService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private FileTypeRepository fileTypeRepository;

    @Mock
    private BaseUserService baseUserService;

    @Mock
    private FileControllerUtils fileControllerUtils;

    @Mock
    private FileTypeService fileTypeService;

    @Mock
    private FileService fileService;

    @Mock(name = "fileValidator")
    private FilesizeAndTypeFileValidator<List<String>> fileValidator;

    public static final String BASE_URL = "/application/";

    long applicationId = 100L;
    long competitionId = 1L;
    long organisationId = 8L;
    private String evidenceTitle = "Proof of success letter";
    private String evidenceGuidance = "You need to upload a proof of success to your application.";
    private List<Long> validFileTypesIdsForEoiEvidence = asList(1L, 3L, 4L);

    private CompetitionEoiEvidenceConfigResource competitionEoiEvidenceConfigResource;
    private ApplicationResource applicationResource;
    private CompetitionResource competitionResource;

    @Before
    public void setup() {
        competitionEoiEvidenceConfigResource = new CompetitionEoiEvidenceConfigResource(1L, competitionId, true, evidenceTitle, evidenceGuidance);
        applicationResource = newApplicationResource().withId(applicationId).withCompetition(competitionId).build();
        competitionResource = newCompetitionResource().withId(competitionId).withCompetitionEoiEvidenceConfigResource(competitionEoiEvidenceConfigResource).build();
    }

//    @Test
//    public void uploadEoiEvidence() throws Exception {
//
//        UserResource userResource = newUserResource().build();
//        FileEntryResource fileEntryResource = newFileEntryResource().build();
//        ApplicationEoiEvidenceResponseResource applicationEoiEvidenceResponseResource = new ApplicationEoiEvidenceResponseResource(1L, applicationId, organisationId, fileEntryResource.getId());
//        Supplier<InputStream> inputStreamSupplier = mock(Supplier.class);
//
//        long eoiEvidenceConfigId = 8L;
//        CompetitionEoiEvidenceConfig competitionEoiEvidenceConfig = new CompetitionEoiEvidenceConfig();
//        competitionEoiEvidenceConfig.setId(eoiEvidenceConfigId);
//        competitionEoiEvidenceConfig.setEvidenceRequired(true);
//        competitionEoiEvidenceConfig.setEvidenceTitle("Evidence title");
//        competitionEoiEvidenceConfig.setEvidenceGuidance("Evidence guidance");
//        Competition competition = newCompetition()
//                .withEnabledForExpressionOfInterest(true)
//                .withCompetitionEoiEvidenceConfig(competitionEoiEvidenceConfig)
//                .build();
//        competitionEoiEvidenceConfig.setCompetition(competition);
//
//        CompetitionEoiDocumentResource competitionEoiDocumentResource1 = new CompetitionEoiDocumentResource(competitionEoiEvidenceConfig.getId(), 1L);
//        CompetitionEoiDocumentResource competitionEoiDocumentResource3 = new CompetitionEoiDocumentResource(competitionEoiEvidenceConfig.getId(), 3L);
//        CompetitionEoiDocumentResource competitionEoiDocumentResource4 = new CompetitionEoiDocumentResource(competitionEoiEvidenceConfig.getId(), 4L);
//
//        FileTypeResource fileTypeResource1 = new FileTypeResource();
//        fileTypeResource1.setId(competitionEoiDocumentResource1.getFileTypeId());
//        fileTypeResource1.setName("PDF");
//        FileTypeResource fileTypeResource3 = new FileTypeResource();
//        fileTypeResource3.setId(competitionEoiDocumentResource3.getFileTypeId());
//        fileTypeResource3.setName("Spreadsheets");
//        FileTypeResource fileTypeResource4 = new FileTypeResource();
//        fileTypeResource4.setId(competitionEoiDocumentResource4.getFileTypeId());
//        fileTypeResource4.setName("Text");
//
//        FileEntry fileEntry = new FileEntry();
//        fileEntry.setId(fileEntryResource.getId());
//        fileEntry.setMediaType(fileEntryResource.getMediaType());
//
//        when(applicationService.getApplicationById(applicationId)).thenReturn(serviceSuccess(applicationResource));
//        when(competitionService.getCompetitionById(competitionId)).thenReturn(serviceSuccess(competitionResource));
//        when(competitionEoiEvidenceConfigService.getValidFileTypesIdsForEoiEvidence(competitionEoiEvidenceConfigResource.getId())).thenReturn(serviceSuccess(validFileTypesIdsForEoiEvidence));
//        when(fileControllerUtils.handleFileUpload(nullable(String.class), nullable(String.class), nullable(String.class), any(FilesizeAndTypeFileValidator.class), any(List.class), any(Long.class), any(HttpServletRequest.class), any(BiFunction.class)))
//                .thenReturn(RestResult.restSuccess(applicationEoiEvidenceResponseResource, HttpStatus.OK));
//        when(applicationEoiEvidenceResponseService.upload(applicationId,organisationId,userResource, fileEntryResource, inputStreamSupplier)).thenReturn(serviceSuccess(applicationEoiEvidenceResponseResource));
//        when(fileService.createFile(any(FileEntryResource.class), any(Supplier.class))).thenReturn(serviceSuccess(fileEntry));
//        when(fileTypeService.findOne(competitionEoiDocumentResource1.getFileTypeId())).thenReturn(serviceSuccess(fileTypeResource1));
//        when(fileTypeService.findOne(competitionEoiDocumentResource3.getFileTypeId())).thenReturn(serviceSuccess(fileTypeResource3));
//        when(fileTypeService.findOne(competitionEoiDocumentResource4.getFileTypeId())).thenReturn(serviceSuccess(fileTypeResource4));
//
//        mockMvc.perform(post(BASE_URL + applicationId + "/eoi-evidence-response/" + organisationId + "/upload")
//                .contentType(APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(content().string(objectMapper.writeValueAsString(applicationEoiEvidenceResponseResource)));
//
//    }

    @Override
    protected ApplicationEoiEvidenceResponseController supplyControllerUnderTest() {
        return new ApplicationEoiEvidenceResponseController();
    }
}