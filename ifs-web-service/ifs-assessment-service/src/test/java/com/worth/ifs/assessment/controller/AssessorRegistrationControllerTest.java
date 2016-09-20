package com.worth.ifs.assessment.controller;

import com.worth.ifs.BaseControllerMockMVCTest;
import com.worth.ifs.assessment.model.AssessorRegistrationBecomeAnAssessorModelPopulator;
import com.worth.ifs.assessment.model.AssessorRegistrationModelPopulator;
import com.worth.ifs.assessment.viewmodel.AssessorRegistrationBecomeAnAssessorViewModel;
import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.invite.resource.CompetitionInviteResource;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Spy;

import static com.worth.ifs.assessment.builder.CompetitionInviteResourceBuilder.newCompetitionInviteResource;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


public class AssessorRegistrationControllerTest extends BaseControllerMockMVCTest<AssessorRegistrationController> {

    @Spy
    @InjectMocks
    private AssessorRegistrationModelPopulator registrationModelPopulator;

    @Spy
    @InjectMocks
    private AssessorRegistrationBecomeAnAssessorModelPopulator becomeAnAssessorModelPopulator;

    @Override
    protected AssessorRegistrationController supplyControllerUnderTest() {
        return new AssessorRegistrationController();
    }

    @Test
    public void becomeAnAssessor() throws Exception {
        CompetitionInviteResource competitionInviteResource = newCompetitionInviteResource().build();

        when(competitionInviteRestService.getInvite("hash")).thenReturn(RestResult.restSuccess(competitionInviteResource));

        AssessorRegistrationBecomeAnAssessorViewModel expectedViewModel = new AssessorRegistrationBecomeAnAssessorViewModel("hash");

        mockMvc.perform(get("/registration/{inviteHash}/start", "hash"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("model", expectedViewModel))
                .andExpect(view().name("registration/become-assessor"));
    }
}