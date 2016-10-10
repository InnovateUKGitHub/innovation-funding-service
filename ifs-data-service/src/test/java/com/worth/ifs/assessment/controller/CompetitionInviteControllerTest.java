package com.worth.ifs.assessment.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.worth.ifs.BaseControllerMockMVCTest;
import com.worth.ifs.assessment.transactional.CompetitionInviteService;
import com.worth.ifs.commons.error.Error;
import com.worth.ifs.commons.rest.RestErrorResponse;
import com.worth.ifs.invite.domain.CompetitionInvite;
import com.worth.ifs.invite.domain.CompetitionParticipant;
import com.worth.ifs.invite.resource.CompetitionInviteResource;
import com.worth.ifs.invite.resource.CompetitionRejectionResource;
import com.worth.ifs.invite.resource.RejectionReasonResource;
import com.worth.ifs.user.resource.UserResource;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Optional;

import static com.worth.ifs.commons.error.CommonErrors.notFoundError;
import static com.worth.ifs.commons.error.CommonFailureKeys.*;
import static com.worth.ifs.commons.error.Error.fieldError;
import static com.worth.ifs.commons.service.ServiceResult.serviceFailure;
import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;
import static com.worth.ifs.invite.builder.RejectionReasonResourceBuilder.newRejectionReasonResource;
import static com.worth.ifs.user.builder.UserResourceBuilder.newUserResource;
import static com.worth.ifs.util.JsonMappingUtil.fromJson;
import static com.worth.ifs.util.JsonMappingUtil.toJson;
import static java.util.Collections.nCopies;
import static java.util.Optional.empty;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
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
    public void openInvite_hashNotExists() throws Exception {
        when(competitionInviteService.openInvite("hashNotExists")).thenReturn(serviceFailure(notFoundError(CompetitionInvite.class, "hashNotExists")));

        MvcResult result = mockMvc.perform(post("/competitioninvite/openInvite/{inviteHash}", "hashNotExists").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andReturn();

        RestErrorResponse response = fromJson(result.getResponse().getContentAsString(), RestErrorResponse.class);
        assertEqualsUpNoIncludingStatusCode(response, notFoundError(CompetitionInvite.class, "hashNotExists"));

        verify(competitionInviteService, times(1)).openInvite("hashNotExists");
    }

    @Test
    public void acceptInvite() throws Exception {
        UserResource userResource = newUserResource().withId(7L).build();
        login(userResource);
        when(competitionInviteService.acceptInvite("hash", userResource)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/competitioninvite/acceptInvite/{inviteHash}", "hash").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(competitionInviteService, times(1)).acceptInvite("hash", userResource);
    }

    @Test
    public void acceptInvite_hashNotExists() throws Exception {
        UserResource userResource = newUserResource().withId(7L).build();
        login(userResource);

        when(competitionInviteService.acceptInvite("hashNotExists", userResource)).thenReturn(serviceFailure(notFoundError(CompetitionParticipant.class, "hashNotExists")));

        MvcResult result = mockMvc.perform(post("/competitioninvite/acceptInvite/{inviteHash}", "hashNotExists")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andReturn();

        RestErrorResponse response = fromJson(result.getResponse().getContentAsString(), RestErrorResponse.class);
        assertEqualsUpNoIncludingStatusCode(response, notFoundError(CompetitionParticipant.class, "hashNotExists"));

        verify(competitionInviteService, times(1)).acceptInvite("hashNotExists", userResource);
    }

    @Test
    public void acceptInvite_notOpened() throws Exception {
        acceptFailure(new Error(COMPETITION_PARTICIPANT_CANNOT_ACCEPT_UNOPENED_INVITE, "Connected digital additive manufacturing"));
    }

    @Test
    public void acceptInvite_alreadyAccepted() throws Exception {
        acceptFailure(new Error(COMPETITION_PARTICIPANT_CANNOT_ACCEPT_ALREADY_ACCEPTED_INVITE, "Connected digital additive manufacturing"));
    }

    @Test
    public void acceptInvite_alreadyRejected() throws Exception {
        acceptFailure(new Error(COMPETITION_PARTICIPANT_CANNOT_ACCEPT_ALREADY_REJECTED_INVITE, "Connected digital additive manufacturing"));
    }

    private void acceptFailure(Error expectedError) throws Exception {
        UserResource userResource = newUserResource().withId(7L).build();
        login(userResource);

        when(competitionInviteService.acceptInvite("hash", userResource)).thenReturn(serviceFailure(expectedError));

        mockMvc.perform(post("/competitioninvite/acceptInvite/{inviteHash}", "hash")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(toJson(new RestErrorResponse(expectedError))));

        verify(competitionInviteService, times(1)).acceptInvite("hash", userResource);
    }

    @Test
    public void rejectInvite() throws Exception {
        String comment = String.join(" ", nCopies(100, "comment"));
        RejectionReasonResource rejectionReasonResource =
                newRejectionReasonResource()
                        .withId(1L)
                        .build();
        CompetitionRejectionResource rejectionResource = new CompetitionRejectionResource(rejectionReasonResource, comment);

        when(competitionInviteService.rejectInvite("hash", rejectionReasonResource, Optional.of(comment))).thenReturn(serviceSuccess());
        mockMvc.perform(
                post("/competitioninvite/rejectInvite/{inviteHash}", "hash")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(rejectionResource))
        ).andExpect(status().isOk());

        verify(competitionInviteService, times(1)).rejectInvite("hash", rejectionReasonResource, Optional.of(comment));
    }

    @Test
    public void rejectInvite_noReason() throws Exception {
        String comment = String.join(" ", nCopies(100, "comment"));
        CompetitionRejectionResource rejectionResource = new CompetitionRejectionResource(null, comment);

        Error rejectReasonError = fieldError("rejectReason", null, "validation.competitionrejectionresource.rejectReason.required", "");

        mockMvc.perform(
                post("/competitioninvite/rejectInvite/{inviteHash}", "hash")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(rejectionResource)))
                .andExpect(status().isNotAcceptable())
                .andExpect(content().json(toJson(new RestErrorResponse(rejectReasonError))));
    }

    @Test
    public void rejectInvite_noReasonComment() throws Exception {
        RejectionReasonResource rejectionReasonResource =
                newRejectionReasonResource()
                        .withId(1L)
                        .build();
        CompetitionRejectionResource rejectionResource = new CompetitionRejectionResource(rejectionReasonResource, null);

        when(competitionInviteService.rejectInvite("hash", rejectionReasonResource, empty())).thenReturn(serviceSuccess());
        mockMvc.perform(
                post("/competitioninvite/rejectInvite/{inviteHash}", "hash")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(rejectionResource))
        ).andExpect(status().isOk());

        verify(competitionInviteService, times(1)).rejectInvite("hash", rejectionReasonResource, empty());
    }

    @Test
    public void rejectInvite_exceedsCharacterSizeLimit() throws Exception {
        String comment = RandomStringUtils.random(5001);
        CompetitionRejectionResource rejectionResource = new CompetitionRejectionResource(newRejectionReasonResource()
                .withId(1L)
                .build(), comment);

        Error rejectCommentError = fieldError("rejectComment", comment, "validation.field.too.many.characters", "", "5000", "0");

        mockMvc.perform(
                post("/competitioninvite/rejectInvite/{inviteHash}", "hash")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(rejectionResource)))
                .andExpect(status().isNotAcceptable())
                .andExpect(content().json(toJson(new RestErrorResponse(rejectCommentError))));
    }

    @Test
    public void rejectInvite_exceedsWordLimit() throws Exception {
        String comment = String.join(" ", nCopies(101, "comment"));
        CompetitionRejectionResource rejectionResource = new CompetitionRejectionResource(newRejectionReasonResource()
                .withId(1L)
                .build(), comment);

        Error rejectCommentError = fieldError("rejectComment", comment, "validation.field.max.word.count", "", "100");

        mockMvc.perform(
                post("/competitioninvite/rejectInvite/{inviteHash}", "hash")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(rejectionResource)))
                .andExpect(status().isNotAcceptable())
                .andExpect(content().json(toJson(new RestErrorResponse(rejectCommentError))));
    }

    @Test
    public void rejectInvite_hashNotExists() throws Exception {
        String comment = String.join(" ", nCopies(100, "comment"));
        RejectionReasonResource rejectionReasonResource =
                newRejectionReasonResource()
                        .withId(1L)
                        .build();
        CompetitionRejectionResource rejectionResource = new CompetitionRejectionResource(rejectionReasonResource, comment);

        when(competitionInviteService.rejectInvite("hashNotExists", rejectionReasonResource, Optional.of(comment))).thenReturn(serviceFailure(notFoundError(CompetitionParticipant.class, "hashNotExists")));

        MvcResult result = mockMvc.perform(
                post("/competitioninvite/rejectInvite/{inviteHash}", "hashNotExists")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(rejectionResource))
        ).andExpect(status().isNotFound())
                .andReturn();

        RestErrorResponse response = fromJson(result.getResponse().getContentAsString(), RestErrorResponse.class);
        assertEqualsUpNoIncludingStatusCode(response, notFoundError(CompetitionParticipant.class, "hashNotExists"));

        verify(competitionInviteService, times(1)).rejectInvite("hashNotExists", rejectionReasonResource, Optional.of(comment));
    }

    @Test
    public void rejectInvite_notOpened() throws Exception {
        rejectFailure(new Error(COMPETITION_PARTICIPANT_CANNOT_REJECT_UNOPENED_INVITE, "Connected digital additive manufacturing"));
    }

    @Test
    public void rejectInvite_alreadyAccepted() throws Exception {
        rejectFailure(new Error(COMPETITION_PARTICIPANT_CANNOT_REJECT_ALREADY_ACCEPTED_INVITE, "Connected digital additive manufacturing"));
    }

    @Test
    public void rejectInvite_alreadyRejected() throws Exception {
        rejectFailure(new Error(COMPETITION_PARTICIPANT_CANNOT_REJECT_ALREADY_REJECTED_INVITE, "Connected digital additive manufacturing"));
    }

    @Test
    public void checkExistingUser() throws Exception {
        when(competitionInviteService.checkExistingUser("hash")).thenReturn(serviceSuccess(Boolean.TRUE));

        mockMvc.perform(
                get("/competitioninvite/checkExistingUser/{inviteHash}", "hash"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));

        verify(competitionInviteService).checkExistingUser("hash");
    }

    @Test
    public void checkExistingUser_userNotExists() throws Exception {
        when(competitionInviteService.checkExistingUser("hash")).thenReturn(serviceSuccess(Boolean.FALSE));

        mockMvc.perform(
                get("/competitioninvite/checkExistingUser/{inviteHash}", "hash"))
                .andExpect(status().isOk())
                .andExpect(content().string("false"));

        verify(competitionInviteService).checkExistingUser("hash");
    }

    @Test
    public void checkExistingUser_hashNotExists() throws Exception {
        when(competitionInviteService.checkExistingUser("hashNotExists")).thenReturn(serviceFailure(notFoundError(CompetitionInvite.class, "hashNotExists")));

        MvcResult result = mockMvc.perform(
                get("/competitioninvite/checkExistingUser/{inviteHash}", "hashNotExists"))
                .andExpect(status().isNotFound())
                .andReturn();

        RestErrorResponse response = fromJson(result.getResponse().getContentAsString(), RestErrorResponse.class);
        assertEqualsUpNoIncludingStatusCode(response, notFoundError(CompetitionInvite.class, "hashNotExists"));

        verify(competitionInviteService).checkExistingUser("hashNotExists");
    }

    private void rejectFailure(Error expectedError) throws Exception {
        String comment = String.join(" ", nCopies(100, "comment"));
        RejectionReasonResource rejectionReasonResource =
                newRejectionReasonResource()
                        .withId(1L)
                        .build();
        CompetitionRejectionResource rejectionResource = new CompetitionRejectionResource(rejectionReasonResource, comment);

        when(competitionInviteService.rejectInvite("hash", rejectionReasonResource, Optional.of(comment))).thenReturn(serviceFailure(expectedError));
        mockMvc.perform(
                post("/competitioninvite/rejectInvite/{inviteHash}", "hash")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(rejectionResource)))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(toJson(new RestErrorResponse(expectedError))));

        verify(competitionInviteService, times(1)).rejectInvite("hash", rejectionReasonResource, Optional.of(comment));
    }
}
