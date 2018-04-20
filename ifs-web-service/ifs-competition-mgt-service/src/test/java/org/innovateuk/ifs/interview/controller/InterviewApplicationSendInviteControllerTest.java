package org.innovateuk.ifs.interview.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.interview.model.InterviewAssignmentApplicationsSendModelPopulator;
import org.innovateuk.ifs.interview.viewmodel.InterviewAssignmentApplicationInviteRowViewModel;
import org.innovateuk.ifs.interview.viewmodel.InterviewAssignmentApplicationsSendViewModel;
import org.innovateuk.ifs.invite.resource.ApplicantInterviewInviteResource;
import org.innovateuk.ifs.invite.resource.AssessorInviteSendResource;
import org.innovateuk.ifs.invite.resource.InterviewAssignmentStagedApplicationPageResource;
import org.innovateuk.ifs.invite.resource.InterviewAssignmentStagedApplicationResource;
import org.innovateuk.ifs.management.form.SendInviteForm;
import org.innovateuk.ifs.management.viewmodel.PaginationViewModel;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.context.TestPropertySource;

import java.util.List;

import static java.lang.String.format;
import static java.util.Arrays.asList;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.competition.resource.CompetitionStatus.IN_ASSESSMENT;
import static org.innovateuk.ifs.interview.builder.InterviewAssignmentKeyStatisticsResourceBuilder.newInterviewAssignmentKeyStatisticsResource;
import static org.innovateuk.ifs.invite.builder.AssessorInviteSendResourceBuilder.newAssessorInviteSendResource;
import static org.innovateuk.ifs.invite.builder.InterviewAssignmentCreatedInviteResourceBuilder.newInterviewAssignmentStagedApplicationResource;
import static org.innovateuk.ifs.invite.builder.InterviewAssignmentStagedApplicationPageResourceBuilder.newInterviewAssignmentStagedApplicationPageResource;
import static org.innovateuk.ifs.util.CollectionFunctions.asLinkedSet;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(MockitoJUnitRunner.class)
@TestPropertySource(locations = "classpath:application.properties")
public class InterviewApplicationSendInviteControllerTest extends BaseControllerMockMVCTest<InterviewApplicationSendInviteController> {

    private CompetitionResource competition;

    @Spy
    @InjectMocks
    private InterviewAssignmentApplicationsSendModelPopulator interviewAssignmentApplicationsSendModelPopulator;

    @Override
    protected InterviewApplicationSendInviteController supplyControllerUnderTest() {
        return new InterviewApplicationSendInviteController();
    }

    @Override
    @Before
    public void setUp() {
        super.setUp();
        this.setupCookieUtil();

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
        int page = 0;

        List<InterviewAssignmentStagedApplicationResource> interviewAssignmentStagedApplicationResources = setUpApplicationCreatedInviteResources();
        InterviewAssignmentStagedApplicationPageResource invites = newInterviewAssignmentStagedApplicationPageResource()
                .withContent(interviewAssignmentStagedApplicationResources)
                .build();

        when(competitionRestService.getCompetitionById(competitionId)).thenReturn(restSuccess(competition));
        when(interviewAssignmentRestService.getStagedApplications(competitionId, page)).thenReturn(restSuccess(invites));
        when(interviewAssignmentRestService.getEmailTemplate()).thenReturn(restSuccess(new ApplicantInterviewInviteResource("Some content")));
        when(interviewAssignmentRestService.getKeyStatistics(competitionId)).thenReturn(restSuccess(newInterviewAssignmentKeyStatisticsResource().build()));

        SendInviteForm expectedForm = new SendInviteForm();
        expectedForm.setSubject("Please attend an interview for an Innovate UK funding competition");

        InterviewAssignmentApplicationsSendViewModel expectedViewModel = expectedViewModel(invites);

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

    private InterviewAssignmentApplicationsSendViewModel expectedViewModel(InterviewAssignmentStagedApplicationPageResource invites) {
        return new InterviewAssignmentApplicationsSendViewModel(competition.getId(), "Technology inspired",
                "Transport Systems, Urban living",  "Infrastructure systems",
                asList(
                        new InterviewAssignmentApplicationInviteRowViewModel(1L, 3L,
                                "App 1", "Org 1"),
                        new InterviewAssignmentApplicationInviteRowViewModel(2L, 4L,
                                "App 2", "Org 2")),
                newInterviewAssignmentKeyStatisticsResource().build(),  new PaginationViewModel(invites, ""),
                "?origin=INTERVIEW_PANEL_SEND", "Some content"
        );
    }

    private List<InterviewAssignmentStagedApplicationResource> setUpApplicationCreatedInviteResources() {
        return newInterviewAssignmentStagedApplicationResource()
                .withId(1L, 2L)
                .withApplicationId(3L, 4L)
                .withApplicationName("App 1", "App 2")
                .withLeadOrganisationName("Org 1", "Org 2")
                .build(2);
    }
}