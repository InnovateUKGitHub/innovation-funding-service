package org.innovateuk.ifs.management.competition.setup.core.populator;

import org.innovateuk.ifs.BaseUnitTest;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.GrantTermsAndConditionsResource;
import org.innovateuk.ifs.management.competition.setup.core.form.TermsAndConditionsForm;
import org.junit.Test;
import org.mockito.InjectMocks;

import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.competition.builder.GrantTermsAndConditionsResourceBuilder.newGrantTermsAndConditionsResource;
import static org.junit.Assert.*;

public class TermsAndConditionsFormPopulatorTest extends BaseUnitTest {

    @InjectMocks
    private TermsAndConditionsFormPopulator service;

    @Test
    public void testGetSectionFormDataTermsAndConditions() {
        GrantTermsAndConditionsResource termsAndConditions = newGrantTermsAndConditionsResource().build();

        CompetitionResource competition = newCompetitionResource()
                .withTermsAndConditions(termsAndConditions).build();

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
                .withOtherFundingRulesTermsAndConditions(null).build();

        TermsAndConditionsForm result = service.populateFormForStateAid(competition);

        assertNull(result.getTermsAndConditionsId());
    }
}
