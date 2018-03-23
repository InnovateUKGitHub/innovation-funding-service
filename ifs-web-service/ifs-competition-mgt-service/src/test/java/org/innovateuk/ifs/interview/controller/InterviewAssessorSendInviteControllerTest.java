package org.innovateuk.ifs.interview.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.invite.resource.AssessorInviteSendResource;
import org.innovateuk.ifs.invite.resource.AssessorInvitesToSendResource;
import org.innovateuk.ifs.management.form.SendInviteForm;
import org.innovateuk.ifs.management.viewmodel.SendInvitesViewModel;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.context.TestPropertySource;

import static java.lang.String.format;
import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.competition.resource.CompetitionStatus.IN_ASSESSMENT;
import static org.innovateuk.ifs.invite.builder.AssessorInviteSendResourceBuilder.newAssessorInviteSendResource;
import static org.innovateuk.ifs.invite.builder.AssessorInvitesToSendResourceBuilder.newAssessorInvitesToSendResource;
import static org.innovateuk.ifs.util.CollectionFunctions.asLinkedSet;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(MockitoJUnitRunner.class)
@TestPropertySource(locations = "classpath:application.properties")
public class InterviewAssessorSendInviteControllerTest extends BaseControllerMockMVCTest<InterviewAssessorSendInviteController> {

    private CompetitionResource competition;

    @Override
    protected InterviewAssessorSendInviteController supplyControllerUnderTest() {
        return new InterviewAssessorSendInviteController();
    }

    @Override
    @Before
    public void setUp() {
        super.setUp();
        this.setupCookieUtil();

        competition = newCompetitionResource()
                .withCompetitionStatus(IN_ASSESSMENT)
                .withName("Technology inspired")
                .withInnovationSectorName("Infrastructure systems")
                .withInnovationAreaNames(asLinkedSet("Transport Systems", "Urban living"))
                .build();
    }

    @Test
    public void getInvitesToSend() throws Exception {
        long competitionId = 1L;

        AssessorInvitesToSendResource invites = newAssessorInvitesToSendResource()
                .withRecipients(singletonList("Jessica Doe"))
                .withCompetitionId(competitionId)
                .withCompetitionName("Photonics for health")
                .withContent("Readonly content")
                .build();

        when(interviewInviteRestService.getAllInvitesToSend(competitionId)).thenReturn(restSuccess(invites));

        SendInviteForm expectedForm = new SendInviteForm();
        expectedForm.setSubject("Invitation to Innovate UK interview panel for 'Photonics for health'");

        SendInvitesViewModel expectedViewModel = new SendInvitesViewModel(competitionId, "Photonics for health", singletonList( "Jessica Doe"), "Readonly content");

        mockMvc.perform(get("/assessment/interview/competition/{competitionId}/assessors/invite/send", competitionId))
                .andExpect(status().isOk())
                .andExpect(model().attribute("form", expectedForm))
                .andExpect(model().attribute("model", expectedViewModel))
                .andExpect(view().name("assessors/interview/assessor-send-invites"));

        verify(interviewInviteRestService, only()).getAllInvitesToSend(competitionId);
    }

    @Test
    public void sendInvites() throws Exception {
        long competitionId = 5L;

        AssessorInviteSendResource expectedAssessorInviteSendResource = newAssessorInviteSendResource()
                .withSubject("Subject...")
                .withContent("Editable content...")
                .build();

        when(interviewInviteRestService.sendAllInvites(competitionId, expectedAssessorInviteSendResource)).thenReturn(restSuccess());

        mockMvc.perform(post("/assessment/interview/competition/{competitionId}/assessors/invite/send", competitionId)
                .contentType(APPLICATION_FORM_URLENCODED)
                .param("subject", "Subject...")
                .param("content", "Editable content..."))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(format("/assessment/interview/competition/%s/assessors/find", competitionId)));

        InOrder inOrder = inOrder(interviewInviteRestService);
        inOrder.verify(interviewInviteRestService).sendAllInvites(competitionId, expectedAssessorInviteSendResource);
        inOrder.verifyNoMoreInteractions();
    }
}
