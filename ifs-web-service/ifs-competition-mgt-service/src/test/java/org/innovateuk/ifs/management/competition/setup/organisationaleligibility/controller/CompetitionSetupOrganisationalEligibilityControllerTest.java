package org.innovateuk.ifs.management.competition.setup.organisationaleligibility.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.competition.resource.CompetitionOrganisationConfigResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionOrganisationConfigRestService;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.management.competition.setup.core.form.CompetitionSetupForm;
import org.innovateuk.ifs.management.competition.setup.core.service.CompetitionSetupService;
import org.innovateuk.ifs.management.competition.setup.organisationaleligibility.form.LeadInternationalOrganisationForm;
import org.innovateuk.ifs.management.competition.setup.organisationaleligibility.form.OrganisationalEligibilityForm;
import org.innovateuk.ifs.management.competition.setup.organisationaleligibility.populator.LeadInternationalOrganisationFormPopulator;
import org.innovateuk.ifs.management.competition.setup.organisationaleligibility.populator.LeadInternationalOrganisationViewModelPopulator;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static java.lang.String.format;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionOrganisationConfigResourceBuilder.newCompetitionOrganisationConfigResource;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.competition.resource.CompetitionSetupSection.ORGANISATIONAL_ELIGIBILITY;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(MockitoJUnitRunner.Silent.class)
public class CompetitionSetupOrganisationalEligibilityControllerTest extends BaseControllerMockMVCTest<CompetitionSetupOrganisationalEligibilityController> {

    private static final String URL = "/competition/setup/{competitionId}/section/organisational-eligibility";
    private CompetitionResource competition;
    private long competitionId;

    @Mock
    private CompetitionRestService competitionRestService;

    @Mock
    private CompetitionSetupService competitionSetupService;

    @Mock
    private CompetitionOrganisationConfigRestService competitionOrganisationConfigRestService;

    @Mock
    private LeadInternationalOrganisationViewModelPopulator leadInternationalOrganisationViewModelPopulator;

    @Mock
    private LeadInternationalOrganisationFormPopulator leadInternationalOrganisationFormPopulator;

    @Override
    protected CompetitionSetupOrganisationalEligibilityController supplyControllerUnderTest() {
        return new CompetitionSetupOrganisationalEligibilityController();
    }

    @Before
    public void setup() {
        competitionId = 100L;
        competition = newCompetitionResource().withId(competitionId).build();

        when(competitionRestService.getCompetitionById(competitionId)).thenReturn(restSuccess(competition));
    }

    @Test
    public void organisationalEligibilityPageDetails() throws Exception {
        when(competitionSetupService.hasInitialDetailsBeenPreviouslySubmitted(competitionId)).thenReturn(true);

        mockMvc.perform(get(URL, competitionId))
                .andExpect(status().isOk())
                .andExpect(view().name("competition/setup"));
    }

    @Test
    public void submitTrueOrganisationalEligibilitySectionDetails() throws Exception {

        OrganisationalEligibilityForm organisationalEligibilityForm = new OrganisationalEligibilityForm();
        organisationalEligibilityForm.setInternationalOrganisationsApplicable(true);

        CompetitionOrganisationConfigResource configResource = newCompetitionOrganisationConfigResource()
                .withInternationalOrganisationsAllowed(true)
                .build();

        when(competitionOrganisationConfigRestService.findByCompetitionId(competitionId)).thenReturn(restSuccess(configResource));
        when(competitionOrganisationConfigRestService.update(competitionId, configResource)).thenReturn(restSuccess(configResource));

        mockMvc.perform(post(URL, competitionId)
                .param("internationalOrganisationsApplicable", "true"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(format("/competition/setup/%d/section/%s/lead-international-organisation", competition.getId(), ORGANISATIONAL_ELIGIBILITY.getPostMarkCompletePath())));
    }

    @Test
    public void submitFalseOrganisationalEligibilitySectionDetails() throws Exception {

        OrganisationalEligibilityForm organisationalEligibilityForm = new OrganisationalEligibilityForm();
        organisationalEligibilityForm.setInternationalOrganisationsApplicable(false);

        when(competitionSetupService.saveCompetitionSetupSection(isA(CompetitionSetupForm.class), eq(competition), eq(ORGANISATIONAL_ELIGIBILITY))).thenReturn(serviceSuccess());

        mockMvc.perform(post(URL, competitionId)
                .param("internationalOrganisationsApplicable", "false"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(format("/competition/setup/%d/section/%s", competition.getId(), ORGANISATIONAL_ELIGIBILITY.getPostMarkCompletePath())));
    }

    @Test
    public void viewLeadInternationalOrganisationDetails() throws Exception {

        CompetitionOrganisationConfigResource configResource = newCompetitionOrganisationConfigResource()
                .withInternationalOrganisationsAllowed(true)
                .build();

        when(competitionOrganisationConfigRestService.findByCompetitionId(competitionId)).thenReturn(restSuccess(configResource));

        mockMvc.perform(get(URL + "/lead-international-organisation", competitionId))
                .andExpect(status().isOk())
                .andExpect(view().name("competition/setup/lead-international-organisation"));
    }

    @Test
    public void submitLeadInternationalOrganisationDetails() throws Exception {

        LeadInternationalOrganisationForm leadInternationalOrganisationForm = new LeadInternationalOrganisationForm();
        leadInternationalOrganisationForm.setLeadInternationalOrganisationsApplicable(true);

        OrganisationalEligibilityForm organisationalEligibilityForm = new OrganisationalEligibilityForm();
        organisationalEligibilityForm.setInternationalOrganisationsApplicable(true);
        organisationalEligibilityForm.setLeadInternationalOrganisationsApplicable(true);

        CompetitionOrganisationConfigResource configResource = newCompetitionOrganisationConfigResource()
                .withInternationalOrganisationsAllowed(organisationalEligibilityForm.getInternationalOrganisationsApplicable())
                .withInternationalLeadOrganisationAllowed(organisationalEligibilityForm.getLeadInternationalOrganisationsApplicable())
                .build();

        when(competitionOrganisationConfigRestService.findByCompetitionId(competitionId)).thenReturn(restSuccess(configResource));
        when(competitionSetupService.saveCompetitionSetupSection(isA(CompetitionSetupForm.class), eq(competition), eq(ORGANISATIONAL_ELIGIBILITY))).thenReturn(serviceSuccess());

        mockMvc.perform(post(URL + "/lead-international-organisation", competitionId)
                .param("leadInternationalOrganisationsApplicable", "true"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(format("/competition/setup/%d/section/%s", competition.getId(), ORGANISATIONAL_ELIGIBILITY.getPostMarkCompletePath())));
    }

    @Test
    public void submitNullLeadInternationalOrganisationDetails() throws Exception {

        LeadInternationalOrganisationForm leadInternationalOrganisationForm = new LeadInternationalOrganisationForm();
        leadInternationalOrganisationForm.setLeadInternationalOrganisationsApplicable(null);

        OrganisationalEligibilityForm organisationalEligibilityForm = new OrganisationalEligibilityForm();
        organisationalEligibilityForm.setInternationalOrganisationsApplicable(true);

        CompetitionOrganisationConfigResource configResource = newCompetitionOrganisationConfigResource()
                .withInternationalOrganisationsAllowed(organisationalEligibilityForm.getInternationalOrganisationsApplicable())
                .build();

        when(competitionOrganisationConfigRestService.findByCompetitionId(competitionId)).thenReturn(restSuccess(configResource));

        mockMvc.perform(post(URL + "/lead-international-organisation", competitionId)
                .param("leadInternationalOrganisationsApplicable", "null"))
                .andExpect(status().isOk())
                .andExpect(view().name("competition/setup/lead-international-organisation"));
    }
}