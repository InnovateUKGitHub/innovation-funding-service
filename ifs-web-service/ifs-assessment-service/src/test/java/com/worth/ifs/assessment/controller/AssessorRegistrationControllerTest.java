package com.worth.ifs.assessment.controller;

import com.worth.ifs.BaseControllerMockMVCTest;
import com.worth.ifs.assessment.model.*;
import com.worth.ifs.assessment.viewmodel.AssessorRegistrationBecomeAnAssessorViewModel;
import com.worth.ifs.assessment.viewmodel.AssessorRegistrationYourDetailsViewModel;
import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.invite.resource.CompetitionInviteResource;
import com.worth.ifs.invite.service.EthnicityRestService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.context.TestPropertySource;

import static com.worth.ifs.assessment.builder.CompetitionInviteResourceBuilder.newCompetitionInviteResource;
import static com.worth.ifs.commons.rest.RestResult.restSuccess;
import static com.worth.ifs.user.builder.EthnicityResourceBuilder.newEthnicityResource;
import static org.codehaus.groovy.runtime.InvokerHelper.asList;
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
    private AssessorRegistrationYourDetailsModelPopulator registrationModelPopulator;

    @Spy
    @InjectMocks
    private AssessorRegistrationSkillsModelPopulator assessorRegistrationSkillsModelPopulator;

    @Spy
    @InjectMocks
    private AssessorRegistrationDeclarationModelPopulator assessorRegistrationDeclarationModelPopulator;

    @Spy
    @InjectMocks
    private AssessorRegistrationTermsModelPopulator assessorRegistrationTermsModelPopulator;

    @Mock
    private EthnicityRestService ethnicityRestService;

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
        CompetitionInviteResource competitionInviteResource = newCompetitionInviteResource().withEmail("test@test.com").build();

        when(competitionInviteRestService.getInvite("hash")).thenReturn(RestResult.restSuccess(competitionInviteResource));
        when(ethnicityRestService.findAllActive()).thenReturn(RestResult.restSuccess(asList(newEthnicityResource())));
        AssessorRegistrationYourDetailsViewModel expectedViewModel = new AssessorRegistrationYourDetailsViewModel("hash", "test@test.com");

        mockMvc.perform(get("/registration/{inviteHash}/register", "hash"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("model", expectedViewModel))
                .andExpect(view().name("registration/register"));
    }

    @Test
    public void profileSkills() throws Exception {
        mockMvc.perform(get("/registration/skills"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("model"))
                .andExpect(view().name("registration/innovation-areas"));
    }

    @Test
    public void profileDeclaration() throws Exception {
        mockMvc.perform(get("/registration/declaration"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("model"))
                .andExpect(view().name("registration/declaration-of-interest"));
    }

    @Test
    public void profileTerms() throws Exception {
        mockMvc.perform(get("/registration/terms"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("model"))
                .andExpect(view().name("registration/terms"));
    }
}