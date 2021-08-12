package org.innovateuk.ifs.management.competition.setup.core.populator;

import org.innovateuk.ifs.BaseUnitTest;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionThirdPartyConfigResource;
import org.innovateuk.ifs.competition.resource.GrantTermsAndConditionsResource;
import org.innovateuk.ifs.competition.service.TermsAndConditionsRestService;
import org.innovateuk.ifs.management.competition.setup.core.form.TermsAndConditionsForm;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.competition.builder.CompetitionThirdPartyConfigResourceBuilder.newCompetitionThirdPartyConfigResource;
import static org.innovateuk.ifs.competition.builder.GrantTermsAndConditionsResourceBuilder.newGrantTermsAndConditionsResource;
import static org.junit.Assert.*;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class TermsAndConditionsFormPopulatorTest extends BaseUnitTest {

    @InjectMocks
    private TermsAndConditionsFormPopulator service;

    @Mock
    private TermsAndConditionsRestService termsAndConditionsRestService;

    @Test
    public void testGetSectionFormDataTermsAndConditions() {
        GrantTermsAndConditionsResource termsAndConditions = newGrantTermsAndConditionsResource().withName("Non procurement").build();
        CompetitionResource competition = newCompetitionResource()
                .withTermsAndConditions(termsAndConditions).build();

        when(termsAndConditionsRestService.getById(any())).thenReturn(restSuccess(termsAndConditions));
        TermsAndConditionsForm result = service.populateForm(competition);

        assertNotNull(result.getTermsAndConditionsId());
        assertEquals(result.getTermsAndConditionsId(), termsAndConditions.getId());
    }

    @Test
    public void testGetSectionFormDataStateAidTermsAndConditions() {
        GrantTermsAndConditionsResource termsAndConditions = newGrantTermsAndConditionsResource().build();

        CompetitionResource competition = newCompetitionResource()
                .withOtherFundingRulesTermsAndConditions(termsAndConditions).build();

        TermsAndConditionsForm result = service.populateFormForStateAid(competition);

        assertNotNull(result.getTermsAndConditionsId());
        assertEquals(result.getTermsAndConditionsId(), termsAndConditions.getId());
    }

    @Test
    public void testGetSectionFormDataNullStateAidTermsAndConditions() {
        CompetitionResource competition = newCompetitionResource()
                .withOtherFundingRulesTermsAndConditions((GrantTermsAndConditionsResource) null).build();

        TermsAndConditionsForm result = service.populateFormForStateAid(competition);

        assertNull(result.getTermsAndConditionsId());
    }

    @Test
    public void populateThirdPartyConfigData() {
        Long competitionId = 1L;

        CompetitionResource competition = newCompetitionResource()
                .withId(competitionId)
                .withCompetitionThirdPartyConfig(newCompetitionThirdPartyConfigResource().build())
                .build();

        TermsAndConditionsForm termsAndConditionsForm = new TermsAndConditionsForm();
        termsAndConditionsForm.setThirdPartyTermsAndConditionsLabel("label");
        termsAndConditionsForm.setThirdPartyTermsAndConditionsText("text");
        termsAndConditionsForm.setProjectCostGuidanceLink("cost");

        service.populateThirdPartyConfigData(termsAndConditionsForm, competition);

        assertNotNull(competition.getCompetitionThirdPartyConfigResource());

        CompetitionThirdPartyConfigResource competitionThirdPartyConfigResource = competition.getCompetitionThirdPartyConfigResource();

        assertEquals("label", competitionThirdPartyConfigResource.getTermsAndConditionsLabel());
        assertEquals("text", competitionThirdPartyConfigResource.getTermsAndConditionsGuidance());
        assertEquals("cost", competitionThirdPartyConfigResource.getProjectCostGuidanceUrl());
        assertEquals(competitionId, competitionThirdPartyConfigResource.getCompetitionId());
    }
}
