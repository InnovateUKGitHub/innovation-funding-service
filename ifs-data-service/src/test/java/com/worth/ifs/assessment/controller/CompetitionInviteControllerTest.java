package com.worth.ifs.assessment.controller;

import com.worth.ifs.BaseControllerMockMVCTest;
import com.worth.ifs.assessment.transactional.CompetitionInviteService;
import com.worth.ifs.invite.resource.CompetitionInviteResource;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.http.MediaType;

import static com.worth.ifs.commons.error.CommonErrors.notFoundError;
import static com.worth.ifs.commons.service.ServiceResult.serviceFailure;
import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class CompetitionInviteControllerTest extends BaseControllerMockMVCTest<CompetitionInviteController> {

    @Mock
    private CompetitionInviteService competitionInviteService;

    @Override
    protected CompetitionInviteController supplyControllerUnderTest() {
        return new CompetitionInviteController();
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
        when(competitionInviteService.openInvite("hash")).thenReturn(serviceFailure(notFoundError(String.class)));
        mockMvc.perform(post("/competitioninvite/openInvite/{inviteHash}", "hash").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(competitionInviteService, times(1)).openInvite("hash");
    }
}
