package org.innovateuk.ifs.competitionsetup.service.formpopulator;

import org.innovateuk.ifs.BaseUnitTestMocksTest;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSection;
import org.innovateuk.ifs.competition.resource.GrantTermsAndConditionsResource;
import org.innovateuk.ifs.competitionsetup.form.CompetitionSetupForm;
import org.innovateuk.ifs.competitionsetup.form.TermsAndConditionsForm;
import org.junit.Test;
import org.mockito.InjectMocks;

import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.competition.builder.GrantTermsAndConditionsResourceBuilder.newGrantTermsAndConditionsResource;
import static org.junit.Assert.*;

public class TermsAndConditionsFormPopulatorTest extends BaseUnitTestMocksTest {

    @InjectMocks
    private TermsAndConditionsFormPopulator service;

    @Test
    public void testSectionToFill() {
        CompetitionSetupSection result = service.sectionToFill();
        assertEquals(CompetitionSetupSection.TERMS_AND_CONDITIONS, result);
    }

    @Test
    public void testGetSectionFormDataTermsAndConditions() {
        GrantTermsAndConditionsResource termsAndConditions = newGrantTermsAndConditionsResource().build();

        CompetitionResource competition = newCompetitionResource()
                .withTermsAndConditions(termsAndConditions).build();

        CompetitionSetupForm result = service.populateForm(competition);

        assertTrue(result instanceof TermsAndConditionsForm);
        TermsAndConditionsForm form = (TermsAndConditionsForm) result;

        assertNotNull(form.getTermsAndConditionsId());
        assertEquals(form.getTermsAndConditionsId(), termsAndConditions.getId());
    }
}
