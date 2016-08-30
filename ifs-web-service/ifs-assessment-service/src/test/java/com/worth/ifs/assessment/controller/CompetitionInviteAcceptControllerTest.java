package com.worth.ifs.assessment.controller;

import com.worth.ifs.BaseControllerMockMVCTest;
import com.worth.ifs.invite.resource.CompetitionInviteResource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.context.TestPropertySource;

import static com.worth.ifs.commons.error.CommonErrors.notFoundError;
import static com.worth.ifs.commons.rest.RestResult.restFailure;
import static com.worth.ifs.commons.rest.RestResult.restSuccess;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(MockitoJUnitRunner.class)
@TestPropertySource(locations = "classpath:application.properties")
public class CompetitionInviteAcceptControllerTest extends BaseControllerMockMVCTest<CompetitionInviteAcceptController> {

    @Override
    protected CompetitionInviteAcceptController supplyControllerUnderTest() {
        return new CompetitionInviteAcceptController();
    }

    private static final String restUrl = "/invite-accept/";

    @Test
    public void acceptInvite() throws Exception {
        when(competitionInviteRestService.acceptInvite("hash")).thenReturn(restSuccess());

        mockMvc.perform(post(restUrl + "competition/{inviteHash}/accept", "hash"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/assessor/dashboard"));

        verify(competitionInviteRestService).acceptInvite("hash");
    }

    @Test
    public void acceptInvite_hashNotExists() throws Exception {
        when(competitionInviteRestService.acceptInvite("notExistHash")).thenReturn(restFailure(notFoundError(CompetitionInviteResource.class, "notExistHash")));

        mockMvc.perform(post(restUrl + "competition/{inviteHash}/accept", "notExistHash"))
                .andExpect(status().isNotFound());

        verify(competitionInviteRestService).acceptInvite("notExistHash");
    }
}