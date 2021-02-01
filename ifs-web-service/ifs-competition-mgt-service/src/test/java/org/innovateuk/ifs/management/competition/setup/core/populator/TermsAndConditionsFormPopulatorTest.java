package org.innovateuk.ifs.management.competition.setup.core.populator;

import org.innovateuk.ifs.BaseUnitTest;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.GrantTermsAndConditionsResource;
import org.innovateuk.ifs.management.competition.setup.core.form.CompetitionSetupForm;
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

        CompetitionSetupForm result = service.populateForm(competition);

        assertTrue(result instanceof TermsAndConditionsForm);
        TermsAndConditionsForm form = (TermsAndConditionsForm) result;

        assertNotNull(form.getTermsAndConditionsId());
        assertEquals(form.getTermsAndConditionsId(), termsAndConditions.getId());
    }
}
