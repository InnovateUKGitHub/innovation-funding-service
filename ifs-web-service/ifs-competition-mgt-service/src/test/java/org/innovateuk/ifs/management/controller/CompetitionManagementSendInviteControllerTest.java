package org.innovateuk.ifs.management.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.invite.resource.AssessorInviteSendResource;
import org.innovateuk.ifs.invite.resource.AssessorInviteToSendResource;
import org.innovateuk.ifs.management.form.SendInviteForm;
import org.innovateuk.ifs.management.model.SendInviteModelPopulator;
import org.innovateuk.ifs.management.viewmodel.SendInviteViewModel;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.context.TestPropertySource;

import static java.lang.String.format;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.invite.builder.AssessorInviteSendResourceBuilder.newAssessorInviteSendResource;
import static org.innovateuk.ifs.invite.builder.AssessorInviteToSendResourceBuilder.newAssessorInviteToSendResource;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(MockitoJUnitRunner.class)
@TestPropertySource(locations = "classpath:application.properties")
public class CompetitionManagementSendInviteControllerTest extends BaseControllerMockMVCTest<CompetitionManagementSendInviteController> {


    @Spy
    @InjectMocks
    private SendInviteModelPopulator sendInviteModelPopulator;

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
    public void getInviteToSend() throws Exception {
        long inviteId = 4L;
        AssessorInviteToSendResource invite = newAssessorInviteToSendResource()
                .withRecipient("Jessica Doe")
                .withCompetitionId(1L)
                .withCompetitionName("Photonics for health")
                .withContent("Editable content...")
                .build();

        when(competitionInviteRestService.getCreatedInviteToSend(inviteId)).thenReturn(restSuccess(invite));

        SendInviteForm expectedForm = new SendInviteForm();
        expectedForm.setSubject("Invitation to assess 'Photonics for health'");
        expectedForm.setContent("Editable content...");

        SendInviteViewModel expectedViewModel = new SendInviteViewModel(1L, inviteId, "Photonics for health", "Jessica Doe");

        mockMvc.perform(get("/competition/assessors/invite/{inviteId}", inviteId))
                .andExpect(status().isOk())
                .andExpect(model().attribute("form", expectedForm))
                .andExpect(model().attribute("model", expectedViewModel))
                .andExpect(view().name("assessors/send-invites"));

        verify(competitionInviteRestService, only()).getCreatedInviteToSend(inviteId);
    }

    @Test
    public void getInviteToResend() throws Exception {
        long inviteId = 4L;
        AssessorInviteToSendResource invite = newAssessorInviteToSendResource()
                .withRecipient("Jessica Doe")
                .withCompetitionId(1L)
                .withCompetitionName("Photonics for health")
                .withContent("Editable content...")
                .build();

        when(competitionInviteRestService.getInviteToSend(inviteId)).thenReturn(restSuccess(invite));

        SendInviteForm expectedForm = new SendInviteForm();
        expectedForm.setSubject("Invitation to assess 'Photonics for health'");
        expectedForm.setContent("Editable content...");

        SendInviteViewModel expectedViewModel = new SendInviteViewModel(1L, inviteId, "Photonics for health", "Jessica Doe");

        mockMvc.perform(get("/competition/assessors/invite/{inviteId}/resend", inviteId))
                .andExpect(status().isOk())
                .andExpect(model().attribute("form", expectedForm))
                .andExpect(model().attribute("model", expectedViewModel))
                .andExpect(view().name("assessors/resend-invite"));

        verify(competitionInviteRestService, only()).getInviteToSend(inviteId);
    }


    @Test
    public void sendInvite() throws Exception {
        long inviteId = 4L;
        long competitionId = 5L;

        AssessorInviteToSendResource invite = newAssessorInviteToSendResource().withCompetitionId(competitionId).build();

        AssessorInviteSendResource expectedAssessorInviteSendResource = newAssessorInviteSendResource()
                .withSubject("Subject...")
                .withContent("Editable content...")
                .build();

        when(competitionInviteRestService.getInviteToSend(inviteId)).thenReturn(restSuccess(invite));
        when(competitionInviteRestService.sendInvite(inviteId, expectedAssessorInviteSendResource)).thenReturn(restSuccess());

        mockMvc.perform(post("/competition/assessors/invite/{inviteId}/send", inviteId)
                .contentType(APPLICATION_FORM_URLENCODED)
                .param("subject", "Subject...")
                .param("content", "Editable content..."))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(format("/competition/%s/assessors/invite", competitionId)));

        InOrder inOrder = inOrder(competitionInviteRestService);
        inOrder.verify(competitionInviteRestService).getInviteToSend(inviteId);
        inOrder.verify(competitionInviteRestService).sendInvite(inviteId, expectedAssessorInviteSendResource);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void resendInvite() throws Exception {
        long inviteId = 4L;
        long competitionId = 5L;

        AssessorInviteToSendResource invite = newAssessorInviteToSendResource().withCompetitionId(competitionId).build();

        AssessorInviteSendResource expectedAssessorInviteSendResource = newAssessorInviteSendResource()
                .withSubject("Subject...")
                .withContent("Editable content...")
                .build();

        when(competitionInviteRestService.getInviteToSend(inviteId)).thenReturn(restSuccess(invite));
        when(competitionInviteRestService.resendInvite(inviteId, expectedAssessorInviteSendResource)).thenReturn(restSuccess());

        mockMvc.perform(post("/competition/assessors/invite/{inviteId}/resend", inviteId)
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
