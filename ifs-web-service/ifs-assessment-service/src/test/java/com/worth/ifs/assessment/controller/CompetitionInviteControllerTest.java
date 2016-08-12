package com.worth.ifs.assessment.controller;

import com.worth.ifs.BaseControllerMockMVCTest;
import com.worth.ifs.assessment.viewmodel.CompetitionInviteViewModel;
import com.worth.ifs.invite.resource.CompetitionInviteResource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.context.TestPropertySource;

import static com.worth.ifs.assessment.builder.CompetitionInviteResourceBuilder.newCompetitionInviteResource;
import static com.worth.ifs.commons.error.CommonErrors.notFoundError;
import static com.worth.ifs.commons.rest.RestResult.restFailure;
import static com.worth.ifs.commons.rest.RestResult.restSuccess;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@RunWith(MockitoJUnitRunner.class)
@TestPropertySource(locations = "classpath:application.properties")
public class CompetitionInviteControllerTest extends BaseControllerMockMVCTest<CompetitionInviteController> {

    private static final String restUrl = "/invite/competition/";

    @Override
    protected CompetitionInviteController supplyControllerUnderTest() {
        return new CompetitionInviteController();
    }

    @Test
    public void openInvite() throws Exception {
        CompetitionInviteResource inviteResource = newCompetitionInviteResource().withCompetitionName("my competition").build();

        when(competitionInviteRestService.openInvite("hash")).thenReturn(restSuccess(inviteResource));
        mockMvc.perform(get(restUrl + "hash"))
                .andExpect(status().isOk())
                .andExpect(view().name("assessor-competition-invite"))
                .andExpect(model().attribute("model", new CompetitionInviteViewModel("my competition")));

        verify(competitionInviteRestService).openInvite("hash");
    }

    @Test
    public void openInvite_hashNotExists() throws Exception {
        when(competitionInviteRestService.openInvite("notExistHash")).thenReturn(restFailure(notFoundError(CompetitionInviteResource.class, "notExistHash")));
        mockMvc.perform(get(restUrl + "notExistHash"))
                .andExpect(model().attributeDoesNotExist("model"))
                .andExpect(status().is2xxSuccessful());

        verify(competitionInviteRestService).openInvite("notExistHash");
    }
}