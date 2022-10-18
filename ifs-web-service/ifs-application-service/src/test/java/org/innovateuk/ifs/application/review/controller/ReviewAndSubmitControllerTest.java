package org.innovateuk.ifs.application.review.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.application.resource.ApplicationEoiEvidenceResponseResource;
import org.innovateuk.ifs.application.resource.ApplicationExpressionOfInterestConfigResource;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.resource.ApplicationState;
import org.innovateuk.ifs.application.review.populator.TrackViewModelPopulator;
import org.innovateuk.ifs.application.review.viewmodel.TrackViewModel;
import org.innovateuk.ifs.application.service.ApplicationEoiEvidenceResponseRestService;
import org.innovateuk.ifs.application.service.ApplicationRestService;
import org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions;
import org.innovateuk.ifs.competition.publiccontent.resource.FundingType;
import org.innovateuk.ifs.competition.resource.CompetitionCompletionStage;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionStatus;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.filter.CookieFlashMessageFilter;
import org.innovateuk.ifs.user.service.ProcessRoleRestService;
import org.innovateuk.ifs.user.service.UserService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

import static org.innovateuk.ifs.application.builder.ApplicationExpressionOfInterestConfigResourceBuilder.newApplicationExpressionOfInterestConfigResource;
import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.application.resource.ApplicationState.SUBMITTED;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.competition.publiccontent.resource.FundingType.LOAN;
import static org.innovateuk.ifs.competition.resource.CompetitionTypeEnum.*;
import static org.innovateuk.ifs.file.builder.FileEntryResourceBuilder.newFileEntryResource;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(MockitoJUnitRunner.Silent.class)
public class ReviewAndSubmitControllerTest extends BaseControllerMockMVCTest<ReviewAndSubmitController> {

    @Mock
    private ApplicationRestService applicationRestService;
    @Mock
    private CompetitionRestService competitionRestService;
    @Mock
    private CookieFlashMessageFilter cookieFlashMessageFilter;
    @Mock
    private UserService userService;
    @Mock
    private TrackViewModelPopulator trackViewModelPopulator;
    @Mock
    private ProcessRoleRestService processRoleRestService;
    @Mock
    private ApplicationEoiEvidenceResponseRestService applicationEoiEvidenceResponseRestService;

    private static final String EARLY_METRICS_URL = "www.early-metrics.com" ;

    @Before
    public void reset() {
        BaseBuilderAmendFunctions.clearUniqueIds();
    }

    @Override
    protected ReviewAndSubmitController supplyControllerUnderTest() {
        return new ReviewAndSubmitController();
    }

    @Test
    public void applicationConfirmSubmit() throws Exception {
        long applicationId = 2L;

        mockMvc.perform(get("/application/" + applicationId + "/confirm-submit?termsAgreed=true"))
                .andExpect(view().name("application-confirm-submit"))
                .andExpect(model().attribute("applicationId", applicationId));
    }

    @Test
    public void applicationSubmit() throws Exception {
        ApplicationResource application = newApplicationResource()
                .withCompetitionStatus(CompetitionStatus.OPEN)
                .withApplicationState(ApplicationState.OPENED)
                .build();

        when(applicationRestService.getApplicationById(application.getId())).thenReturn(restSuccess(application));
        when(userService.isLeadApplicant( loggedInUser.getId(), application)).thenReturn(true);
        when(applicationRestService.updateApplicationState(application.getId(), SUBMITTED)).thenReturn(restSuccess());

        mockMvc.perform(post("/application/" + application.getId() + "/confirm-submit"))
                .andExpect(redirectedUrl("/application/" + application.getId() + "/track"));

        verify(applicationRestService).updateApplicationState(application.getId(), SUBMITTED);
    }

    @Test
    public void applicationSubmitAppisNotSubmittable() throws Exception {
        ApplicationResource application = newApplicationResource()
                .withCompetitionStatus(CompetitionStatus.CLOSED)
                .withApplicationState(ApplicationState.OPENED)
                .build();

        when(applicationRestService.getApplicationById(application.getId())).thenReturn(restSuccess(application));
        when(userService.isLeadApplicant(loggedInUser.getId(), application)).thenReturn(true);
        when(applicationRestService.updateApplicationState(application.getId(), SUBMITTED)).thenReturn(restSuccess());

        mockMvc.perform(post("/application/" + application.getId() + "/confirm-submit")
                .param("agreeTerms", "yes"))
                .andExpect(redirectedUrl("/application/1"));

        verify(cookieFlashMessageFilter).setFlashMessage(isA(HttpServletResponse.class), eq("cannotSubmit"));
        verify(applicationRestService, never()).updateApplicationState(any(Long.class), any(ApplicationState.class));
    }

    @Test
    public void alwaysOpenApplicationTrackNoReopen() throws Exception {
        CompetitionResource competition = newCompetitionResource().withAlwaysOpen(true).build();

        ApplicationResource application = newApplicationResource()
                .withApplicationState(SUBMITTED)
                .withCompetition(competition.getId())
                .withCompetitionStatus(CompetitionStatus.OPEN)
                .build();

        TrackViewModel model = new TrackViewModel(competition, application,  EARLY_METRICS_URL, application.getCompletion(), false);

        when(applicationRestService.getApplicationById(application.getId())).thenReturn(restSuccess(application));
        when(competitionRestService.getCompetitionById(competition.getId())).thenReturn(restSuccess(competition));
        when(userService.isLeadApplicant(loggedInUser.getId(), application)).thenReturn(true);
        when(trackViewModelPopulator.populate(application.getId(), false, loggedInUser)).thenReturn(model);
        when(applicationRestService.applicationHasAssessment(application.getId()))
                .thenReturn(restSuccess(true));

        mockMvc.perform(get("/application/" + application.getId() + "/track"))
                .andExpect(view().name("always-open-track"))
                .andReturn();
        assertFalse(model.isReopenLinkVisible());
    }

    @Test
    public void alwaysOpenApplicationTrackReopen() throws Exception {
        CompetitionResource competition = newCompetitionResource()
                .withAlwaysOpen(true).build();

        ApplicationResource application = newApplicationResource()
                .withApplicationState(SUBMITTED)
                .withCompetitionStatus(CompetitionStatus.OPEN)
                .withCompetition(competition.getId())
                .build();

        TrackViewModel model = new TrackViewModel(competition, application,  EARLY_METRICS_URL, application.getCompletion(), false);

        when(applicationRestService.getApplicationById(application.getId())).thenReturn(restSuccess(application));
        when(competitionRestService.getCompetitionById(competition.getId())).thenReturn(restSuccess(competition));
        when(userService.isLeadApplicant(loggedInUser.getId(), application)).thenReturn(true);
        when(trackViewModelPopulator.populate(application.getId(), false, loggedInUser)).thenReturn(model);

        mockMvc.perform(get("/application/" + application.getId() + "/track"))
                .andExpect(view().name("always-open-track"))
                .andReturn();
        assertFalse(model.isReopenLinkVisible());
    }

    @Test
    public void horizonEuropeApplicationTrackReopen() throws Exception {
        CompetitionResource competition = newCompetitionResource()
                .withCompetitionTypeEnum(HORIZON_EUROPE_GUARANTEE)
                .withAlwaysOpen(true)
                .build();

        ApplicationResource application = newApplicationResource()
                .withApplicationState(SUBMITTED)
                .withCompetitionStatus(CompetitionStatus.OPEN)
                .withCompetition(competition.getId())
                .build();

        TrackViewModel model = new TrackViewModel(competition, application,  EARLY_METRICS_URL, application.getCompletion(), false);

        when(applicationRestService.getApplicationById(application.getId())).thenReturn(restSuccess(application));
        when(competitionRestService.getCompetitionById(competition.getId())).thenReturn(restSuccess(competition));
        when(userService.isLeadApplicant(loggedInUser.getId(), application)).thenReturn(true);
        when(trackViewModelPopulator.populate(application.getId(), false, loggedInUser)).thenReturn(model);

        mockMvc.perform(get("/application/" + application.getId() + "/track"))
                .andExpect(view().name("horizon-europe-guarantee-application-track"))
                .andReturn();
        assertFalse(model.isReopenLinkVisible());
    }

    @Test
    public void horizonEuropeExpressionOfInterestTrackReopen() throws Exception {
        CompetitionResource competition = newCompetitionResource()
                .withEnabledForExpressionOfInterest(true)
                .withCompetitionTypeEnum(HORIZON_EUROPE_GUARANTEE)
                .withAlwaysOpen(true)
                .build();

        ApplicationExpressionOfInterestConfigResource applicationExpressionOfInterestConfig = newApplicationExpressionOfInterestConfigResource()
                .withEnabledForExpressionOfInterest(true)
                .build();

        ApplicationResource application = newApplicationResource()
                .withApplicationExpressionOfInterestConfigResource(applicationExpressionOfInterestConfig)
                .withApplicationState(SUBMITTED)
                .withCompetitionStatus(CompetitionStatus.OPEN)
                .withCompetition(competition.getId())
                .build();
        TrackViewModel model = new TrackViewModel(competition, application,  EARLY_METRICS_URL, application.getCompletion(), false);

        when(applicationRestService.getApplicationById(application.getId())).thenReturn(restSuccess(application));
        when(competitionRestService.getCompetitionById(competition.getId())).thenReturn(restSuccess(competition));
        when(userService.isLeadApplicant(loggedInUser.getId(), application)).thenReturn(true);
        when(trackViewModelPopulator.populate(application.getId(), false, loggedInUser)).thenReturn(model);

         mockMvc.perform(get("/application/" + application.getId() + "/track"))
                .andExpect(view().name("horizon-europe-guarantee-eoi-application-track"))
                .andReturn();

        assertFalse(model.isReopenLinkVisible());
    }

    @Test
    public void thirdPartyOfgemApplicationTrackReopen() throws Exception {
        CompetitionResource competition = newCompetitionResource()
                .withFundingType(FundingType.THIRDPARTY)
                .withCompetitionTypeEnum(OFGEM)
                .build();

        ApplicationResource application = newApplicationResource()
                .withApplicationState(SUBMITTED)
                .withCompetitionStatus(CompetitionStatus.OPEN)
                .withCompetition(competition.getId())
                .build();
        TrackViewModel model = new TrackViewModel(competition, application,  EARLY_METRICS_URL, application.getCompletion(), true);

        when(applicationRestService.getApplicationById(application.getId())).thenReturn(restSuccess(application));
        when(competitionRestService.getCompetitionById(competition.getId())).thenReturn(restSuccess(competition));
        when(userService.isLeadApplicant(loggedInUser.getId(), application)).thenReturn(true);
        when(trackViewModelPopulator.populate(application.getId(), true, loggedInUser)).thenReturn(model);

        mockMvc.perform(get("/application/" + application.getId() + "/track"))
                .andExpect(view().name("third-party-ofgem-application-track"))
                .andReturn();

        assertTrue(model.isReopenLinkVisible());
    }

    @Test
    public void applicationTrack() throws Exception {
        CompetitionResource competition = newCompetitionResource()
                .build();

        ApplicationResource application = newApplicationResource()
                .withApplicationState(SUBMITTED)
                .withCompetition(competition.getId())
                .build();
        when(applicationRestService.getApplicationById(application.getId())).thenReturn(restSuccess(application));
        when(competitionRestService.getCompetitionById(competition.getId())).thenReturn(restSuccess(competition));

        mockMvc.perform(get("/application/" + application.getId() + "/track"))
                .andExpect(view().name("application-track"));
    }

    @Test
    public void loanApplicationTrack() throws Exception {
        CompetitionResource competition = newCompetitionResource()
                .withFundingType(LOAN)
                .build();

        ApplicationResource application = newApplicationResource()
                .withApplicationState(SUBMITTED)
                .withCompetition(competition.getId())
                .build();
        when(applicationRestService.getApplicationById(application.getId())).thenReturn(restSuccess(application));
        when(competitionRestService.getCompetitionById(competition.getId())).thenReturn(restSuccess(competition));

        mockMvc.perform(get("/application/" + application.getId() + "/track"))
                .andExpect(view().name("loan-application-track"));
    }

    @Test
    public void h2020GrantTransferTrack() throws Exception {
        CompetitionResource competition = newCompetitionResource()
                .withFundingType(FundingType.GRANT)
                .withCompetitionTypeEnum(HORIZON_2020)
                .build();

        ApplicationResource application = newApplicationResource()
                .withApplicationState(SUBMITTED)
                .withCompetition(competition.getId())
                .build();
        when(applicationRestService.getApplicationById(application.getId())).thenReturn(restSuccess(application));
        when(competitionRestService.getCompetitionById(competition.getId())).thenReturn(restSuccess(competition));


        mockMvc.perform(get("/application/" + application.getId() + "/track"))
                .andExpect(view().name("h2020-grant-transfer-track"));
    }

    @Test
    public void notSubmittedApplicationTrack() throws Exception {

        CompetitionResource competition = newCompetitionResource()
                .withFundingType(FundingType.KTP)
                .withCompletionStage(CompetitionCompletionStage.PROJECT_SETUP)
                .build();

        ApplicationResource application = newApplicationResource()
                .withApplicationState(ApplicationState.OPENED)
                .withCompetition(competition.getId())
                .build();

        when(applicationRestService.getApplicationById(application.getId())).thenReturn(restSuccess(application));
        when(competitionRestService.getCompetitionById(competition.getId())).thenReturn(restSuccess(competition));

        mockMvc.perform(get("/application/" + application.getId() + "/track"))
                .andExpect(status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/application/" + application.getId()));
    }

    @Test
    public void ktpApplicationTrackForProjectSetupCompletion() throws Exception {
        CompetitionResource competition = newCompetitionResource()
                .withFundingType(FundingType.KTP)
                .withCompletionStage(CompetitionCompletionStage.PROJECT_SETUP)
                .build();

        ApplicationResource application = newApplicationResource()
                .withApplicationState(SUBMITTED)
                .withCompetition(competition.getId())
                .build();
        TrackViewModel model = new TrackViewModel(competition, application,  EARLY_METRICS_URL, application.getCompletion(), true);
        when(applicationRestService.getApplicationById(application.getId())).thenReturn(restSuccess(application));
        when(competitionRestService.getCompetitionById(competition.getId())).thenReturn(restSuccess(competition));
        when(trackViewModelPopulator.populate(application.getId(), true, loggedInUser)).thenReturn(model);

        mockMvc.perform(get("/application/" + application.getId() + "/track"))
                .andExpect(view().name("application-track"))
                .andExpect(status().isOk())
                .andReturn();

        assertFalse(model.isDisplayIfsAssessmentInformation());
    }

    @Test
    public void ktpApplicationTrackForCompetitionCloseCompletion() throws Exception {
        CompetitionResource competition = newCompetitionResource()
                .withFundingType(FundingType.KTP)
                .withCompletionStage(CompetitionCompletionStage.COMPETITION_CLOSE)
                .build();

        ApplicationResource application = newApplicationResource()
                .withApplicationState(SUBMITTED)
                .withCompetition(competition.getId())
                .build();
        TrackViewModel model = new TrackViewModel(competition, application,  EARLY_METRICS_URL, application.getCompletion(), false);

        when(applicationRestService.getApplicationById(application.getId())).thenReturn(restSuccess(application));
        when(competitionRestService.getCompetitionById(competition.getId())).thenReturn(restSuccess(competition));
        when(trackViewModelPopulator.populate(application.getId(), true, loggedInUser)).thenReturn(model);

        mockMvc.perform(get("/application/" + application.getId() + "/track"))
                .andExpect(view().name("application-track"))
                .andReturn();

        assertFalse(model.isDisplayIfsAssessmentInformation());
    }

    @Test
    public void removeEoiEvidenceResponse() throws Exception {
        long competitionId = 1L;
        long applicationId = 2L;
        long organisationId = 8L;
        long fileId = 101L;

        ApplicationEoiEvidenceResponseResource applicationEoiEvidenceResponseResource = ApplicationEoiEvidenceResponseResource.builder()
                .id(3L)
                .applicationId(applicationId)
                .organisationId(organisationId)
                .fileEntryId(fileId)
                .build();

        ApplicationResource application = newApplicationResource()
                .withId(applicationId)
                .withApplicationState(SUBMITTED)
                .withCompetition(competitionId)
                .withLeadOrganisationId(organisationId)
                .withApplicationEoiEvidenceResponseResource(applicationEoiEvidenceResponseResource)
                .build();

        when(applicationRestService.getApplicationById(applicationId)).thenReturn(restSuccess(application));
        when(applicationEoiEvidenceResponseRestService.remove(applicationEoiEvidenceResponseResource, loggedInUser)).thenReturn(restSuccess(applicationEoiEvidenceResponseResource));

        mockMvc.perform(post("/application/" + applicationId + "/track")
                        .param("remove-eoi-evidence", String.valueOf(fileId)))
                .andExpect(redirectedUrl("/application/" + applicationId + "/track"));
    }

    @Test
    public void submitEoiEvidenceResponse() throws Exception {
        long competitionId = 1L;
        long applicationId = 2L;
        long organisationId = 8L;
        long fileId = 101L;

        ApplicationEoiEvidenceResponseResource applicationEoiEvidenceResponseResource = ApplicationEoiEvidenceResponseResource.builder()
                .id(3L)
                .applicationId(applicationId)
                .organisationId(organisationId)
                .fileEntryId(fileId)
                .build();

        ApplicationResource application = newApplicationResource()
                .withId(applicationId)
                .withApplicationState(SUBMITTED)
                .withCompetition(competitionId)
                .withLeadOrganisationId(organisationId)
                .withApplicationEoiEvidenceResponseResource(applicationEoiEvidenceResponseResource)
                .build();

        when(applicationEoiEvidenceResponseRestService.findOneByApplicationId(applicationId)).thenReturn(restSuccess(Optional.of(applicationEoiEvidenceResponseResource)));
        when(applicationEoiEvidenceResponseRestService.submitEoiEvidence(applicationEoiEvidenceResponseResource, loggedInUser)).thenReturn(restSuccess());

        mockMvc.perform(post("/application/" + applicationId + "/track")
                        .param("submit-eoi-evidence", String.valueOf(fileId)))
                .andExpect(redirectedUrl("/application/" + applicationId + "/track"));
    }

    @Test
    public void downloadEOIEvidenceFile() throws Exception {
        long competitionId = 1L;
        long applicationId = 2L;
        long organisationId = 8L;
        long fileId = 101L;
        MultipartFile file = new MockMultipartFile("Evidence", "Evidence response".getBytes());
        ByteArrayResource byteArrayResource = new ByteArrayResource(file.getBytes());
        FileEntryResource fileEntryResource = newFileEntryResource().withId(fileId).build();

        ApplicationEoiEvidenceResponseResource applicationEoiEvidenceResponseResource = ApplicationEoiEvidenceResponseResource.builder()
                .id(3L)
                .applicationId(applicationId)
                .organisationId(organisationId)
                .fileEntryId(fileId)
                .build();

        ApplicationResource application = newApplicationResource()
                .withId(applicationId)
                .withApplicationState(SUBMITTED)
                .withCompetition(competitionId)
                .withLeadOrganisationId(organisationId)
                .withApplicationEoiEvidenceResponseResource(applicationEoiEvidenceResponseResource)
                .build();

        when(applicationEoiEvidenceResponseRestService.getEvidenceByApplication(applicationId)).thenReturn(restSuccess(byteArrayResource));
        when(applicationEoiEvidenceResponseRestService.getEvidenceDetailsByApplication(applicationId)).thenReturn(restSuccess(fileEntryResource));

        MvcResult result = mockMvc.perform(get("/application/" + applicationId + "/view-eoi-evidence"))
                .andExpect(status().isOk())
                .andReturn();

        MockHttpServletResponse response = result.getResponse();

        assertEquals("Evidence response", response.getContentAsString());
        assertEquals(200, response.getStatus());

    }














}
