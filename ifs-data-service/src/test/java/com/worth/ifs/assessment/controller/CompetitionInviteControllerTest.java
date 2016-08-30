package com.worth.ifs.assessment.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.worth.ifs.BaseControllerMockMVCTest;
import com.worth.ifs.assessment.transactional.CompetitionInviteService;
import com.worth.ifs.commons.error.CommonFailureKeys;
import com.worth.ifs.invite.builder.RejectionReasonResourceBuilder;
import com.worth.ifs.invite.domain.CompetitionInvite;
import com.worth.ifs.invite.domain.CompetitionParticipant;
import com.worth.ifs.invite.resource.CompetitionInviteResource;
import com.worth.ifs.invite.resource.CompetitionRejectionResource;
import com.worth.ifs.invite.resource.RejectionReasonResource;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.http.MediaType;

import static com.worth.ifs.commons.error.CommonErrors.notFoundError;
import static com.worth.ifs.commons.error.CommonFailureKeys.*;
import static com.worth.ifs.commons.service.ServiceResult.serviceFailure;
import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class CompetitionInviteControllerTest extends BaseControllerMockMVCTest<CompetitionInviteController> {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private CompetitionInviteService competitionInviteService;

    @Override
    protected CompetitionInviteController supplyControllerUnderTest() {
        return new CompetitionInviteController();
    }

    @Test
    public void getInvite() throws Exception {
        CompetitionInviteResource resource = new CompetitionInviteResource();

        when(competitionInviteService.getInvite("hash")).thenReturn(serviceSuccess(resource));
        mockMvc.perform(get("/competitioninvite/getInvite/{inviteHash}", "hash").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(competitionInviteService, times(1)).getInvite("hash");
    }

    @Test
    public void openInvite() throws Exception {
        CompetitionInviteResource resource = new CompetitionInviteResource();

        when(competitionInviteService.openInvite("hash")).thenReturn(serviceSuccess(resource));
        mockMvc.perform(post("/competitioninvite/openInvite/{inviteHash}", "hash").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(competitionInviteService, times(1)).openInvite("hash");
    }

    @Test
    public void openInvite_notExists() throws Exception {
        when(competitionInviteService.openInvite("hash")).thenReturn(serviceFailure(notFoundError(CompetitionInvite.class)));
        mockMvc.perform(post("/competitioninvite/openInvite/{inviteHash}", "hash").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(competitionInviteService, times(1)).openInvite("hash");
    }


    @Test
    public void acceptInvite() throws Exception {
        when(competitionInviteService.acceptInvite("hash")).thenReturn(serviceSuccess());
        mockMvc.perform(post("/competitioninvite/acceptInvite/{inviteHash}", "hash").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        verify(competitionInviteService, times(1)).acceptInvite("hash");
    }

    @Test
    public void acceptInvite_hashNotExists() throws Exception {
        when(competitionInviteService.acceptInvite("hashNotExists")).thenReturn(serviceFailure(notFoundError(CompetitionParticipant.class)));

        mockMvc.perform(post("/competitioninvite/acceptInvite/{inviteHash}", "hashNotExists")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
        verify(competitionInviteService, times(1)).acceptInvite("hashNotExists");
    }

    @Test
    public void acceptInvite_notOpened() throws Exception {
        acceptFailure(COMPETITION_PARTICIPANT_CANNOT_ACCEPT_UNOPENED_INVITE);
    }

    @Test
    public void acceptInvite_alreadyAccepted() throws Exception {
        acceptFailure(COMPETITION_PARTICIPANT_CANNOT_ACCEPT_ALREADY_ACCEPTED_INVITE);
    }

    @Test
    public void acceptInvite_alreadyRejected() throws Exception {
        acceptFailure(COMPETITION_PARTICIPANT_CANNOT_ACCEPT_ALREADY_REJECTED_INVITE);
    }

    private void acceptFailure(CommonFailureKeys key) throws Exception {
        when(competitionInviteService.acceptInvite("hash")).thenReturn(serviceFailure(key));

        mockMvc.perform(post("/competitioninvite/acceptInvite/{inviteHash}", "hash")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(competitionInviteService, times(1)).acceptInvite("hash");
    }

    @Test
    public void rejectInvite() throws Exception {
        RejectionReasonResource rejectionReasonResource = RejectionReasonResourceBuilder
                .newRejectionReasonResource()
                .withId(1L)
                .build();
        CompetitionRejectionResource rejectionResource = new CompetitionRejectionResource(rejectionReasonResource, "too busy");

        when(competitionInviteService.rejectInvite("hash", rejectionReasonResource, "too busy")).thenReturn(serviceSuccess());
        mockMvc.perform(
                post("/competitioninvite/rejectInvite/{inviteHash}", "hash")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(rejectionResource))
        ).andExpect(status().isOk());

        verify(competitionInviteService, times(1)).rejectInvite("hash", rejectionReasonResource, "too busy");
    }

    @Test
    public void rejectInvite_hashNotExists() throws Exception {
        RejectionReasonResource rejectionReasonResource = RejectionReasonResourceBuilder
                .newRejectionReasonResource()
                .withId(1L)
                .build();
        CompetitionRejectionResource rejectionResource = new CompetitionRejectionResource(rejectionReasonResource, "too busy");

        when(competitionInviteService.rejectInvite("hashNotExists", rejectionReasonResource, "too busy")).thenReturn(serviceFailure(notFoundError(CompetitionParticipant.class)));
        mockMvc.perform(
                post("/competitioninvite/rejectInvite/{inviteHash}", "hashNotExists")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(rejectionResource))
        ).andExpect(status().isNotFound());

        verify(competitionInviteService, times(1)).rejectInvite("hashNotExists", rejectionReasonResource, "too busy");
    }

    @Test
    public void rejectInvite_notOpened() throws Exception {
        rejectFailure(COMPETITION_PARTICIPANT_CANNOT_REJECT_UNOPENED_INVITE);
    }

    @Test
    public void rejectInvite_alreadyAccepted() throws Exception {
        rejectFailure(COMPETITION_PARTICIPANT_CANNOT_REJECT_ALREADY_ACCEPTED_INVITE);
    }

    @Test
    public void rejectInvite_alreadyRejected() throws Exception {
        rejectFailure(COMPETITION_PARTICIPANT_CANNOT_REJECT_ALREADY_REJECTED_INVITE);
    }

    private void rejectFailure(CommonFailureKeys key) throws Exception {
        RejectionReasonResource rejectionReasonResource = RejectionReasonResourceBuilder
                .newRejectionReasonResource()
                .withId(1L)
                .build();
        CompetitionRejectionResource rejectionResource = new CompetitionRejectionResource(rejectionReasonResource, "too busy");

        when(competitionInviteService.rejectInvite("hash", rejectionReasonResource, "too busy")).thenReturn(serviceFailure(key));
        mockMvc.perform(
                post("/competitioninvite/rejectInvite/{inviteHash}", "hash")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(rejectionResource))
        ).andExpect(status().isBadRequest());

        verify(competitionInviteService, times(1)).rejectInvite("hash", rejectionReasonResource, "too busy");
    }
}
