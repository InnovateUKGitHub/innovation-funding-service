package org.innovateuk.ifs.management.interview.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.ApplicationService;
import org.innovateuk.ifs.commons.error.CommonErrors;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionKeyApplicationStatisticsRestService;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.management.interview.controller.InterviewApplicationSendInviteController;
import org.innovateuk.ifs.management.interview.form.InterviewApplicationSendForm;
import org.innovateuk.ifs.management.interview.model.InterviewApplicationSentInviteModelPopulator;
import org.innovateuk.ifs.management.interview.model.InterviewApplicationsSendModelPopulator;
import org.innovateuk.ifs.interview.resource.InterviewApplicationSentInviteResource;
import org.innovateuk.ifs.interview.service.InterviewAssignmentRestService;
import org.innovateuk.ifs.management.interview.viewmodel.InterviewAssignmentApplicationInviteSendRowViewModel;
import org.innovateuk.ifs.management.interview.viewmodel.InterviewAssignmentApplicationsSendViewModel;
import org.innovateuk.ifs.management.interview.viewmodel.InterviewAssignmentApplicationsSentInviteViewModel;
import org.innovateuk.ifs.invite.resource.ApplicantInterviewInviteResource;
import org.innovateuk.ifs.invite.resource.AssessorInviteSendResource;
import org.innovateuk.ifs.invite.resource.InterviewAssignmentStagedApplicationPageResource;
import org.innovateuk.ifs.invite.resource.InterviewAssignmentStagedApplicationResource;
import org.innovateuk.ifs.management.navigation.Pagination;
import org.innovateuk.ifs.util.CookieUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MvcResult;

import java.time.ZonedDateTime;
import java.util.List;

import static java.lang.String.format;
import static java.util.Arrays.asList;
import static org.innovateuk.ifs.CookieTestUtil.setupCookieUtil;
import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.commons.rest.RestResult.restFailure;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.competition.resource.CompetitionStatus.IN_ASSESSMENT;
import static org.innovateuk.ifs.file.builder.FileEntryResourceBuilder.newFileEntryResource;
import static org.innovateuk.ifs.interview.builder.InterviewApplicationSentInviteResourceBuilder.newInterviewApplicationSentInviteResource;
import static org.innovateuk.ifs.interview.builder.InterviewAssignmentCreatedInviteResourceBuilder.newInterviewAssignmentStagedApplicationResource;
import static org.innovateuk.ifs.interview.builder.InterviewAssignmentKeyStatisticsResourceBuilder.newInterviewAssignmentKeyStatisticsResource;
import static org.innovateuk.ifs.interview.builder.InterviewAssignmentStagedApplicationPageResourceBuilder.newInterviewAssignmentStagedApplicationPageResource;
import static org.innovateuk.ifs.invite.builder.AssessorInviteSendResourceBuilder.newAssessorInviteSendResource;
import static org.innovateuk.ifs.organisation.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.innovateuk.ifs.util.CollectionFunctions.asLinkedSet;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(MockitoJUnitRunner.Silent.class)
@TestPropertySource(locations = "classpath:application.properties")
public class InterviewApplicationSendInviteControllerTest extends BaseControllerMockMVCTest<InterviewApplicationSendInviteController> {

    private CompetitionResource competition;

    @Spy
    @InjectMocks
    private InterviewApplicationsSendModelPopulator interviewApplicationsSendModelPopulator;

    @Spy
    @InjectMocks
    private InterviewApplicationSentInviteModelPopulator interviewApplicationSentInviteModelPopulator;

    @Mock
    private ApplicationService applicationService;

    @Override
    protected InterviewApplicationSendInviteController supplyControllerUnderTest() {
        return new InterviewApplicationSendInviteController(
                interviewApplicationsSendModelPopulator,
                interviewApplicationSentInviteModelPopulator,
                interviewAssignmentRestService
        );
    }

    @Mock
    private CookieUtil cookieUtil;

    @Mock
    private InterviewAssignmentRestService interviewAssignmentRestService;

    @Mock
    private CompetitionRestService competitionRestService;

    @Mock
    private CompetitionKeyApplicationStatisticsRestService competitionKeyApplicationStatisticsRestService;

    @Before
    public void setUp() {
        setupCookieUtil(cookieUtil);

        competition = newCompetitionResource()
                .withId(1L)
                .withCompetitionStatus(IN_ASSESSMENT)
                .withName("Technology inspired")
                .withInnovationSectorName("Infrastructure systems")
                .withInnovationAreaNames(asLinkedSet("Transport Systems", "Urban living"))
                .build();
    }

    @Test
    public void getInvitesToSend() throws Exception {
        long competitionId = 1L;

        InterviewAssignmentStagedApplicationPageResource invites = setupMocksForGet(competitionId);
        InterviewAssignmentApplicationsSendViewModel expectedViewModel = expectedViewModel(invites);
        InterviewApplicationSendForm expectedForm = expectedForm();

        mockMvc.perform(get("/assessment/interview/competition/{competitionId}/applications/invite/send", competitionId))
                .andExpect(status().isOk())
                .andExpect(model().attribute("form", expectedForm))
                .andExpect(model().attribute("model", expectedViewModel))
                .andExpect(view().name("assessors/interview/application-send-invites"));

        verify(interviewAssignmentRestService).getEmailTemplate();
    }

    @Test
    public void sendInvites() throws Exception {
        long competitionId = 5L;

        AssessorInviteSendResource expectedAssessorInviteSendResource = newAssessorInviteSendResource()
                .withSubject("Subject...")
                .withContent("Editable content...")
                .build();

        when(interviewAssignmentRestService.sendAllInvites(competitionId, expectedAssessorInviteSendResource)).thenReturn(restSuccess());

        mockMvc.perform(post("/assessment/interview/competition/{competitionId}/applications/invite/send", competitionId)
                .contentType(APPLICATION_FORM_URLENCODED)
                .param("subject", "Subject...")
                .param("content", "Editable content..."))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(format("/assessment/interview/competition/%s/applications/view-status", competitionId)));

        verify(interviewAssignmentRestService).sendAllInvites(competitionId, expectedAssessorInviteSendResource);
        verifyNoMoreInteractions(interviewAssignmentRestService);
    }

    @Test
    public void uploadFeedback() throws Exception {
        long competitionId = 1L;
        long applicationId = 2L;

        when(interviewAssignmentRestService.uploadFeedback(applicationId, "application/pdf", 11, "testFile.pdf", "My content!".getBytes()))
                .thenReturn(restFailure(CommonErrors.payloadTooLargeError(1)));

        MockMultipartFile file = new MockMultipartFile("feedback[2]", "testFile.pdf", "application/pdf", "My content!".getBytes());

        setupMocksForGet(competitionId);

        mockMvc.perform(
                fileUpload("/assessment/interview/competition/{competitionId}/applications/invite/send", competitionId)
                        .file(file)
                        .param("attachFeedbackApplicationId", "2"))
                .andExpect(status().isOk())
                .andExpect(view().name("assessors/interview/application-send-invites"))
                .andExpect(model().attributeHasFieldErrors("form", "feedback[2]"))
                .andReturn();

        verify(interviewAssignmentRestService).uploadFeedback(applicationId, "application/pdf", 11, "testFile.pdf", "My content!".getBytes());
    }

    @Test
    public void removeFeedback() throws Exception {
        long competitionId = 1L;
        long applicationId = 2L;

        when(interviewAssignmentRestService.deleteFeedback(applicationId))
                .thenReturn(restFailure(new Error("", HttpStatus.NOT_FOUND)));
        setupMocksForGet(competitionId);

        mockMvc.perform(post("/assessment/interview/competition/{competitionId}/applications/invite/send", competitionId)
                .contentType(APPLICATION_FORM_URLENCODED)
                .param("removeFeedbackApplicationId", "2"))
                .andExpect(status().isOk())
                .andExpect(view().name("assessors/interview/application-send-invites"));

        verify(interviewAssignmentRestService).deleteFeedback(applicationId);
    }

    @Test
    public void viewInvite() throws Exception {
        long applicationId = 1L;
        ZonedDateTime dateAssigned = ZonedDateTime.now();
        setupViewInvite(applicationId, dateAssigned);

        MvcResult result = mockMvc.perform(get("/assessment/interview/competition/{competitionId}/applications/invite/{applicationId}/view", competition.getId(), applicationId))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("model"))
                .andExpect(view().name("assessors/interview/application-view-invite"))
                .andReturn();

        InterviewAssignmentApplicationsSentInviteViewModel model = (InterviewAssignmentApplicationsSentInviteViewModel) result.getModelAndView().getModel().get("model");

        assertEquals("Content", model.getAdditionalText());
        assertEquals("Subject", model.getSubject());
        assertEquals("Template", model.getContent());
        assertEquals("Organisation", model.getLeadOrganisation());
        assertEquals("Filename", model.getFeedbackFilename());
        assertEquals(dateAssigned, model.getDateAssigned());
        assertTrue(model.hasAttachment());
    }

    @Test
    public void editInvite_uploadFile() throws Exception {
        long applicationId = 1L;
        ZonedDateTime dateAssigned = ZonedDateTime.now();
        setupViewInvite(applicationId, dateAssigned);

        when(interviewAssignmentRestService.uploadFeedback(applicationId, "application/pdf", 11, "testFile.pdf", "My content!".getBytes()))
                .thenReturn(restSuccess());
        //Delete feedback can throw a 404, we ignore it.
        when(interviewAssignmentRestService.deleteFeedback(applicationId)).thenReturn(restFailure(CommonErrors.notFoundError(Object.class)));
        MockMultipartFile file = new MockMultipartFile("feedback", "testFile.pdf", "application/pdf", "My content!".getBytes());

        when(interviewAssignmentRestService.resendInvite(eq(applicationId), any())).thenReturn(restSuccess());

        mockMvc.perform(fileUpload("/assessment/interview/competition/{competitionId}/applications/invite/{applicationId}/edit", competition.getId(), applicationId)
                .file(file)
                .param("subject", "Subject...")
                .param("content", "Editable content..."))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(String.format("/assessment/interview/competition/%s/applications/view-status", competition.getId())));

        verify(interviewAssignmentRestService).uploadFeedback(applicationId, "application/pdf", 11, "testFile.pdf", "My content!".getBytes());
        verify(interviewAssignmentRestService).deleteFeedback(applicationId);
        verify(interviewAssignmentRestService).resendInvite(applicationId, new AssessorInviteSendResource("Subject...", "Editable content..."));
    }

    @Test
    public void editInvite_removeFile() throws Exception {
        long applicationId = 1L;
        ZonedDateTime dateAssigned = ZonedDateTime.now();
        setupViewInvite(applicationId, dateAssigned);

        when(interviewAssignmentRestService.deleteFeedback(applicationId)).thenReturn(restSuccess());
        when(interviewAssignmentRestService.resendInvite(eq(applicationId), any())).thenReturn(restSuccess());

        mockMvc.perform(post("/assessment/interview/competition/{competitionId}/applications/invite/{applicationId}/edit", competition.getId(), applicationId)
                .param("subject", "Subject...")
                .param("content", "Editable content...")
                .param("removeFile", "true"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(String.format("/assessment/interview/competition/%s/applications/view-status", competition.getId())));

        verify(interviewAssignmentRestService).deleteFeedback(applicationId);
        verify(interviewAssignmentRestService).resendInvite(applicationId, new AssessorInviteSendResource("Subject...", "Editable content..."));
    }

    private InterviewAssignmentStagedApplicationPageResource setupMocksForGet(long competitionId) {
        List<InterviewAssignmentStagedApplicationResource> interviewAssignmentStagedApplicationResources = setUpApplicationCreatedInviteResources();
        InterviewAssignmentStagedApplicationPageResource invites = newInterviewAssignmentStagedApplicationPageResource()
                .withContent(interviewAssignmentStagedApplicationResources)
                .build();

        when(competitionRestService.getCompetitionById(competitionId)).thenReturn(restSuccess(competition));
        when(interviewAssignmentRestService.getStagedApplications(competitionId, 0)).thenReturn(restSuccess(invites));
        when(interviewAssignmentRestService.getEmailTemplate()).thenReturn(restSuccess(new ApplicantInterviewInviteResource("Some content")));
        when(competitionKeyApplicationStatisticsRestService.getInterviewAssignmentStatisticsByCompetition(competitionId)).thenReturn(restSuccess(newInterviewAssignmentKeyStatisticsResource().build()));
        return invites;
    }

    private InterviewApplicationSendForm expectedForm() {
        InterviewApplicationSendForm expected = new InterviewApplicationSendForm();
        expected.setSubject("Please attend an interview for an Innovate UK funding competition");
        return expected;
    }

    private InterviewAssignmentApplicationsSendViewModel expectedViewModel(InterviewAssignmentStagedApplicationPageResource invites) {
        return new InterviewAssignmentApplicationsSendViewModel(competition.getId(), "Technology inspired",
                "Transport Systems, Urban living", "Infrastructure systems",
                asList(
                        new InterviewAssignmentApplicationInviteSendRowViewModel(1L, 3L,
                                "App 1", "Org 1", "file1"),
                        new InterviewAssignmentApplicationInviteSendRowViewModel(2L, 4L,
                                "App 2", "Org 2", "file2")),
                newInterviewAssignmentKeyStatisticsResource().build(), new Pagination(invites, ""),
                "?origin=INTERVIEW_PANEL_SEND", "Some content"
        );
    }

    private List<InterviewAssignmentStagedApplicationResource> setUpApplicationCreatedInviteResources() {
        return newInterviewAssignmentStagedApplicationResource()
                .withId(1L, 2L)
                .withApplicationId(3L, 4L)
                .withApplicationName("App 1", "App 2")
                .withLeadOrganisationName("Org 1", "Org 2")
                .withFilename("file1", "file2")
                .build(2);
    }

    private void setupViewInvite(long applicationId, ZonedDateTime dateAssigned) {
        String subject = "Subject";
        String content = "Content";
        ApplicationResource applicationResource = newApplicationResource().withName("Application").build();

        InterviewApplicationSentInviteResource sentInvite = newInterviewApplicationSentInviteResource()
                .withSubject(subject)
                .withContent(content)
                .withAssigned(dateAssigned)
                .build();

        when(interviewAssignmentRestService.getSentInvite(applicationId)).thenReturn(restSuccess(sentInvite));
        when(competitionRestService.getCompetitionById(competition.getId())).thenReturn(restSuccess(competition));
        when(applicationService.getLeadOrganisation(applicationId)).thenReturn(newOrganisationResource().withName("Organisation").build());
        when(applicationService.getById(applicationId)).thenReturn(applicationResource);
        when(interviewAssignmentRestService.getEmailTemplate()).thenReturn(restSuccess(new ApplicantInterviewInviteResource("Template")));
        when(interviewAssignmentRestService.findFeedback(applicationId)).thenReturn(restSuccess(newFileEntryResource().withName("Filename").build()));

    }
}