package org.innovateuk.ifs.management.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.invite.resource.AssessorInviteSendResource;
import org.innovateuk.ifs.invite.resource.AssessorInvitesToSendResource;
import org.innovateuk.ifs.management.form.SendInviteForm;
import org.innovateuk.ifs.management.viewmodel.SendInviteViewModel;
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
import static org.innovateuk.ifs.invite.builder.AssessorInviteSendResourceBuilder.newAssessorInviteSendResource;
import static org.innovateuk.ifs.invite.builder.AssessorInvitesToSendResourceBuilder.newAssessorInvitesToSendResource;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(MockitoJUnitRunner.class)
@TestPropertySource(locations = "classpath:application.properties")
public class CompetitionManagementSendInviteControllerTest extends BaseControllerMockMVCTest<CompetitionManagementSendInviteController> {

    @Override
    protected CompetitionManagementSendInviteController supplyControllerUnderTest() {
        return new CompetitionManagementSendInviteController();
    }

    @Override
    @Before
    public void setUp() {
        super.setUp();

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

        when(competitionInviteRestService.getAllInvitesToSend(competitionId)).thenReturn(restSuccess(invites));

        SendInviteForm expectedForm = new SendInviteForm();
        expectedForm.setSubject("Invitation to assess 'Photonics for health'");

        SendInvitesViewModel expectedViewModel = new SendInvitesViewModel(competitionId, "Photonics for health", singletonList( "Jessica Doe"), "Readonly content");

        mockMvc.perform(get("/competition/{competitionId}/assessors/invite/send", competitionId))
                .andExpect(status().isOk())
                .andExpect(model().attribute("form", expectedForm))
                .andExpect(model().attribute("model", expectedViewModel))
                .andExpect(view().name("assessors/send-invites"));

        verify(competitionInviteRestService, only()).getAllInvitesToSend(competitionId);
    }


    @Test
    public void getInviteToResend() throws Exception {
        long competitionId = 1L;
        long inviteId = 4L;
        AssessorInvitesToSendResource invite = newAssessorInvitesToSendResource()
                .withRecipients(singletonList("Jessica Doe"))
                .withCompetitionId(1L)
                .withCompetitionName("Photonics for health")
                .withContent("Editable content...")
                .build();

        when(competitionInviteRestService.getInviteToSend(inviteId)).thenReturn(restSuccess(invite));

        SendInviteForm expectedForm = new SendInviteForm();
        expectedForm.setSubject("Invitation to assess 'Photonics for health'");
        expectedForm.setContent("Editable content...");

        SendInviteViewModel expectedViewModel = new SendInviteViewModel(competitionId, inviteId, "Photonics for health", "Jessica Doe", "Editable content...");

        mockMvc.perform(get("/competition/{competitionId}/assessors/invite/{inviteId}/resend", competitionId, inviteId))
                .andExpect(status().isOk())
                .andExpect(model().attribute("form", expectedForm))
                .andExpect(model().attribute("model", expectedViewModel))
                .andExpect(view().name("assessors/resend-invite"));

        verify(competitionInviteRestService, only()).getInviteToSend(inviteId);
    }


    @Test
    public void sendInvites() throws Exception {
        long competitionId = 5L;

        AssessorInviteSendResource expectedAssessorInviteSendResource = newAssessorInviteSendResource()
                .withSubject("Subject...")
                .withContent("Editable content...")
                .build();

        when(competitionInviteRestService.sendAllInvites(competitionId, expectedAssessorInviteSendResource)).thenReturn(restSuccess());

        mockMvc.perform(post("/competition/{competitionId}/assessors/invite/send", competitionId)
                .contentType(APPLICATION_FORM_URLENCODED)
                .param("subject", "Subject...")
                .param("content", "Editable content..."))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(format("/competition/%s/assessors/invite", competitionId)));

        InOrder inOrder = inOrder(competitionInviteRestService);
        inOrder.verify(competitionInviteRestService).sendAllInvites(competitionId, expectedAssessorInviteSendResource);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void resendInvite() throws Exception {
        long inviteId = 4L;
        long competitionId = 5L;

        AssessorInvitesToSendResource invite = newAssessorInvitesToSendResource().withCompetitionId(competitionId).build();

        AssessorInviteSendResource expectedAssessorInviteSendResource = newAssessorInviteSendResource()
                .withSubject("Subject...")
                .withContent("Editable content...")
                .build();

        when(competitionInviteRestService.getInviteToSend(inviteId)).thenReturn(restSuccess(invite));
        when(competitionInviteRestService.resendInvite(inviteId, expectedAssessorInviteSendResource)).thenReturn(restSuccess());

        mockMvc.perform(post("/competition/{competitionId}/assessors/invite/{inviteId}/resend", competitionId, inviteId)
                .contentType(APPLICATION_FORM_URLENCODED)
                .param("subject", "Subject...")
                .param("content", "Editable content..."))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(format("/competition/%s/assessors/overview", competitionId)));

        InOrder inOrder = inOrder(competitionInviteRestService);
        inOrder.verify(competitionInviteRestService).getInviteToSend(inviteId);
        inOrder.verify(competitionInviteRestService).resendInvite(inviteId, expectedAssessorInviteSendResource);
        inOrder.verifyNoMoreInteractions();
    }
}
