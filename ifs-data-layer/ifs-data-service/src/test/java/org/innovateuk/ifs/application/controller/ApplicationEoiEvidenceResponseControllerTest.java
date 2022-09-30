package org.innovateuk.ifs.application.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.application.mapper.ApplicationEoiEvidenceResponseMapper;
import org.innovateuk.ifs.application.repository.ApplicationEoiEvidenceResponseRepository;
import org.innovateuk.ifs.application.repository.ApplicationRepository;
import org.innovateuk.ifs.application.resource.ApplicationEoiEvidenceResponseResource;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.transactional.ApplicationEoiEvidenceResponseService;
import org.innovateuk.ifs.application.transactional.ApplicationService;
import org.innovateuk.ifs.application.workflow.configuration.ApplicationEoiEvidenceWorkflowHandler;
import org.innovateuk.ifs.competition.repository.CompetitionRepository;
import org.innovateuk.ifs.competition.resource.CompetitionEoiEvidenceConfigResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.transactional.CompetitionEoiEvidenceConfigService;
import org.innovateuk.ifs.competition.transactional.CompetitionService;
import org.innovateuk.ifs.file.controller.FileControllerUtils;
import org.innovateuk.ifs.file.controller.FilesizeAndTypeFileValidator;
import org.innovateuk.ifs.file.repository.FileTypeRepository;
import org.innovateuk.ifs.file.service.FileService;
import org.innovateuk.ifs.file.service.FileTypeService;
import org.innovateuk.ifs.organisation.domain.Organisation;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.mapper.UserMapper;
import org.innovateuk.ifs.user.repository.UserRepository;
import org.innovateuk.ifs.user.resource.ProcessRoleType;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.transactional.BaseUserService;
import org.junit.Before;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Value;

import java.io.InputStream;
import java.util.List;
import java.util.function.Supplier;

import static java.util.Arrays.asList;
import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.organisation.builder.OrganisationBuilder.newOrganisation;
import static org.innovateuk.ifs.user.builder.ProcessRoleBuilder.newProcessRole;
import static org.innovateuk.ifs.user.builder.UserBuilder.newUser;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;

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

    @Mock
    private ApplicationEoiEvidenceResponseMapper applicationEoiEvidenceResponseMapper;

    @Mock
    private ApplicationRepository applicationRepository;

    @Mock
    private CompetitionRepository competitionRepository;

    @Mock
    private ApplicationEoiEvidenceResponseRepository applicationEoiEvidenceResponseRepository;

    @Mock
    private ApplicationEoiEvidenceWorkflowHandler applicationEoiEvidenceWorkflowHandler;

    @Mock(name = "fileValidator")
    private FilesizeAndTypeFileValidator<List<String>> fileValidator;

    public static final String BASE_URL = "/application/";

    long applicationId = 100L;
    long competitionId = 1L;
    long organisationId = 8L;
    private String evidenceTitle = "Proof of success letter";
    private String evidenceGuidance = "You need to upload a proof of success to your application.";
    private List<Long> validFileTypesIdsForEoiEvidence = asList(1L, 3L, 4L);
    private long fileEntryId;
    private ApplicationEoiEvidenceResponseResource applicationEoiEvidenceResponseResource;
    private User user;
    private ProcessRole processRole;
    private CompetitionEoiEvidenceConfigResource competitionEoiEvidenceConfigResource;
    private ApplicationResource applicationResource;
    private CompetitionResource competitionResource;
    private UserResource userResource;
    private Organisation organisation;
    private Supplier<InputStream> inputStreamSupplier;

    @Value("${ifs.data.service.file.storage.forminputresponse.max.filesize.bytes}")
    private Long maxFilesizeBytesForEoiEvidenceResponse;

    @Before
    public void setup() {
        applicationId = 1L;
        organisationId = 2L;
        fileEntryId = 3L;

        applicationEoiEvidenceResponseResource = ApplicationEoiEvidenceResponseResource.builder()
                .applicationId(applicationId)
                .organisationId(organisationId)
                .fileEntryId(fileEntryId)
                .build();

        user = newUser().build();

        processRole = newProcessRole()
                .withRole(ProcessRoleType.LEADAPPLICANT)
                .withUser(user).build();

        userResource = newUserResource().build();
        organisation = newOrganisation().build();

        competitionEoiEvidenceConfigResource = new CompetitionEoiEvidenceConfigResource(1L, competitionId, true, evidenceTitle, evidenceGuidance);
        applicationResource = newApplicationResource().withId(applicationId).withCompetition(competitionId).build();
        competitionResource = newCompetitionResource().withId(competitionId).withCompetitionEoiEvidenceConfigResource(competitionEoiEvidenceConfigResource).build();
    }

//    @Test
//    public void uploadEoiEvidence() throws Exception {
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
//        Application application = newApplication()
//                .withId(applicationId)
//                .withCompetition(competition)
//                .withActivityState(ApplicationState.SUBMITTED)
//                .withApplicationExpressionOfInterestConfig(ApplicationExpressionOfInterestConfig.builder()
//                        .enabledForExpressionOfInterest(true)
//                        .build())
//                .withProcessRole(processRole)
//                .build();
//        ApplicationEoiEvidenceResponse applicationEoiEvidenceResponse = ApplicationEoiEvidenceResponse.builder()
//                .application(newApplication().build())
//                .organisation(newOrganisation().build())
//                .fileEntry(newFileEntry().build())
//                .build();
//        FileEntryResource fileEntryResource = newFileEntryResource().
//                withId(applicationEoiEvidenceResponse.getFileEntry().getId()).
//                withFilesizeBytes(1024).
//                withMediaType("application/pdf").
//                withName("eoi_Evidence_file").
//                build();
//        inputStreamSupplier = () -> new ByteArrayInputStream(fileEntryResource.getName().getBytes());
//        FileEntry fileEntry = new FileEntry();
//        fileEntry.setId(fileEntryResource.getId());
//        fileEntry.setMediaType(fileEntryResource.getMediaType());
//        fileEntry.setName(fileEntryResource.getName());
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
//        fileTypeResource3.setName("spreadsheets");
//        FileTypeResource fileTypeResource4 = new FileTypeResource();
//        fileTypeResource4.setId(competitionEoiDocumentResource4.getFileTypeId());
//        fileTypeResource4.setName("text");
//        List<FileType> pdfFileTypes = newFileType()
//                .withName("PDF", "spreadsheets", "text")
//                .withExtension(".pdf", ".xlsx", ".docx")
//                .build(3);
//
//        when(applicationService.getApplicationById(applicationId)).thenReturn(serviceSuccess(applicationResource));
//        when(competitionService.getCompetitionById(competitionId)).thenReturn(serviceSuccess(competitionResource));
//
//        when(fileTypeRepository.findById(fileTypeResource1.getId())).thenReturn(Optional.of(pdfFileTypes.get(0)));
//        when(fileTypeRepository.findById(fileTypeResource3.getId())).thenReturn(Optional.of(pdfFileTypes.get(1)));
//        when(fileTypeRepository.findById(fileTypeResource4.getId())).thenReturn(Optional.of(pdfFileTypes.get(2)));
//
//        when(applicationEoiEvidenceResponseService.upload(applicationId,organisationId,userResource, fileEntryResource, inputStreamSupplier)).thenReturn(serviceSuccess(applicationEoiEvidenceResponseResource));
//        when(applicationRepository.findById(applicationId)).thenReturn(Optional.of(application));
//        when(competitionRepository.findById(competition.getId())).thenReturn(Optional.of(competition));
//        when(competitionEoiEvidenceConfigService.getValidFileTypesIdsForEoiEvidence(competitionEoiEvidenceConfigResource.getId())).thenReturn(serviceSuccess(validFileTypesIdsForEoiEvidence));
//
//        when(fileControllerUtils.handleFileUpload(nullable(String.class), nullable(String.class), nullable(String.class), any(FilesizeAndTypeFileValidator.class), any(List.class), eq(maxFilesizeBytesForEoiEvidenceResponse), any(HttpServletRequest.class), any(BiFunction.class)))
//                .thenReturn(RestResult.restSuccess(applicationEoiEvidenceResponseResource, HttpStatus.OK));
//
//        when(fileTypeService.findOne(competitionEoiDocumentResource1.getFileTypeId())).thenReturn(serviceSuccess(fileTypeResource1));
//        when(fileTypeService.findOne(competitionEoiDocumentResource3.getFileTypeId())).thenReturn(serviceSuccess(fileTypeResource3));
//        when(fileTypeService.findOne(competitionEoiDocumentResource4.getFileTypeId())).thenReturn(serviceSuccess(fileTypeResource4));
//        when(fileService.createFile(any(FileEntryResource.class), any(Supplier.class))).thenReturn(serviceSuccess(fileEntry));
//        when(applicationEoiEvidenceResponseRepository.findOneByApplicationId(applicationId)).thenReturn(Optional.of(applicationEoiEvidenceResponse));
//        when(applicationEoiEvidenceResponseMapper.mapToDomain(applicationEoiEvidenceResponseResource)).thenReturn(applicationEoiEvidenceResponse);
//        when(applicationEoiEvidenceResponseRepository.save(applicationEoiEvidenceResponse)).thenReturn(applicationEoiEvidenceResponse);
//
//        when(userMapper.mapToDomain(userResource)).thenReturn(user);
//        when(applicationEoiEvidenceWorkflowHandler.documentUploaded(applicationEoiEvidenceResponse, processRole, user)).thenReturn(true);
//
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