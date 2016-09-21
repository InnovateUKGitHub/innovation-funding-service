package com.worth.ifs.assessment.controller;

import com.worth.ifs.BaseControllerMockMVCTest;
import com.worth.ifs.assessment.model.*;
import com.worth.ifs.assessment.viewmodel.AssessorRegistrationBecomeAnAssessorViewModel;
import com.worth.ifs.invite.resource.CompetitionInviteResource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.context.TestPropertySource;

import static com.worth.ifs.assessment.builder.CompetitionInviteResourceBuilder.newCompetitionInviteResource;
import static com.worth.ifs.commons.rest.RestResult.restSuccess;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(MockitoJUnitRunner.class)
@TestPropertySource(locations = "classpath:application.properties")
public class AssessorRegistrationControllerTest extends BaseControllerMockMVCTest<AssessorRegistrationController> {

    @Spy
    @InjectMocks
    private AssessorRegistrationBecomeAnAssessorModelPopulator becomeAnAssessorModelPopulator;

    @Spy
    @InjectMocks
    private AssessorRegistrationModelPopulator registrationModelPopulator;

    @Spy
    @InjectMocks
    private AssessorSkillsModelPopulator assessorSkillsModelPopulator;

    @Spy
    @InjectMocks
    private AssessorDeclarationModelPopulator assessorDeclarationModelPopulator;

    @Spy
    @InjectMocks
    private AssessorTermsModelPopulator assessorTermsModelPopulator;

    @Override
    protected AssessorRegistrationController supplyControllerUnderTest() {
        return new AssessorRegistrationController();
    }

    @Test
    public void becomeAnAssessor() throws Exception {
        CompetitionInviteResource competitionInviteResource = newCompetitionInviteResource().build();

        when(competitionInviteRestService.getInvite("hash")).thenReturn(restSuccess(competitionInviteResource));

        AssessorRegistrationBecomeAnAssessorViewModel expectedViewModel = new AssessorRegistrationBecomeAnAssessorViewModel("hash");

        mockMvc.perform(get("/registration/{inviteHash}/start", "hash"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("model", expectedViewModel))
                .andExpect(view().name("registration/become-assessor"));
    }

    @Test
    public void yourDetails() throws Exception {
        // TODO
    }

    @Test
    public void skills() throws Exception {
        mockMvc.perform(get("/registration/skills"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("model"))
                .andExpect(view().name("registration/innovation-areas"));
    }

    @Test
    public void declaration() throws Exception {
        mockMvc.perform(get("/registration/declaration"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("model"))
                .andExpect(view().name("registration/declaration-of-interest"));
    }

    @Test
    public void terms() throws Exception {
        mockMvc.perform(get("/registration/terms"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("model"))
                .andExpect(view().name("registration/terms"));
    }
}