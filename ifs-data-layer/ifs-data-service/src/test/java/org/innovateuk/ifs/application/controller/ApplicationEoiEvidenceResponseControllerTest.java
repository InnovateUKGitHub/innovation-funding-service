package org.innovateuk.ifs.application.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.application.resource.ApplicationEoiEvidenceResponseResource;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.transactional.ApplicationEoiEvidenceResponseService;
import org.innovateuk.ifs.application.transactional.ApplicationService;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.competition.resource.CompetitionEoiEvidenceConfigResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.transactional.CompetitionEoiEvidenceConfigService;
import org.innovateuk.ifs.competition.transactional.CompetitionService;
import org.innovateuk.ifs.file.controller.FileControllerUtils;
import org.innovateuk.ifs.file.controller.FilesizeAndTypeFileValidator;
import org.innovateuk.ifs.file.repository.FileTypeRepository;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.user.mapper.UserMapper;
import org.innovateuk.ifs.user.repository.UserRepository;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.transactional.BaseUserService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Supplier;

import static java.util.Arrays.asList;
import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.file.builder.FileEntryResourceBuilder.newFileEntryResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
    public void setuo() {
        competitionEoiEvidenceConfigResource = new CompetitionEoiEvidenceConfigResource(1L, competitionId, true, evidenceTitle, evidenceGuidance);
        applicationResource = newApplicationResource().withId(applicationId).withCompetition(competitionId).build();
        competitionResource = newCompetitionResource().withId(competitionId).withCompetitionEoiEvidenceConfigResource(competitionEoiEvidenceConfigResource).build();
    }

    @Test
    public void uploadEoiEvidence() throws Exception {

        UserResource userResource = newUserResource().build();
        FileEntryResource fileEntryResource = newFileEntryResource().build();
        ApplicationEoiEvidenceResponseResource applicationEoiEvidenceResponseResource = new ApplicationEoiEvidenceResponseResource(1L, applicationId, organisationId, fileEntryResource.getId());

        when(applicationService.getApplicationById(applicationId)).thenReturn(serviceSuccess(applicationResource));
        when(competitionService.getCompetitionById(competitionId)).thenReturn(serviceSuccess(competitionResource));
        when(competitionEoiEvidenceConfigService.getValidFileTypesIdsForEoiEvidence(competitionEoiEvidenceConfigResource.getId())).thenReturn(serviceSuccess(validFileTypesIdsForEoiEvidence));
        when(fileControllerUtils.handleFileUpload(nullable(String.class), nullable(String.class), nullable(String.class), any(FilesizeAndTypeFileValidator.class), any(List.class), any(Long.class), any(HttpServletRequest.class), any(BiFunction.class)))
                .thenReturn(RestResult.restSuccess(applicationEoiEvidenceResponseResource, HttpStatus.OK));
        when(applicationEoiEvidenceResponseService.upload(applicationId,organisationId,userResource, fileEntryResource, any(Supplier.class))).thenReturn(serviceSuccess(applicationEoiEvidenceResponseResource));

        mockMvc.perform(post(BASE_URL + applicationId + "/eoi-evidence-response/" + organisationId + "/upload")
                .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(applicationEoiEvidenceResponseResource)));

    }

    @Override
    protected ApplicationEoiEvidenceResponseController supplyControllerUnderTest() {
        return new ApplicationEoiEvidenceResponseController();
    }
}