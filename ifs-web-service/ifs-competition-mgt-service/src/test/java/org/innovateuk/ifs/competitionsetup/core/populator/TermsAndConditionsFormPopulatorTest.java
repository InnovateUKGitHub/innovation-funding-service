package org.innovateuk.ifs.competitionsetup.core.populator;

import org.innovateuk.ifs.BaseUnitTestMocksTest;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSection;
import org.innovateuk.ifs.competition.resource.TermsAndConditionsResource;
import org.innovateuk.ifs.competitionsetup.core.form.CompetitionSetupForm;
import org.innovateuk.ifs.competitionsetup.core.form.TermsAndConditionsForm;
import org.junit.Test;
import org.mockito.InjectMocks;

import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.competition.builder.TermsAndConditionsResourceBuilder.newTermsAndConditionsResource;
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
        Long id = 1L;
        TermsAndConditionsResource termsAndConditions = newTermsAndConditionsResource()
                .withId(id).build();

        CompetitionResource competition = newCompetitionResource()
                .withTermsAndConditions(termsAndConditions).build();

        CompetitionSetupForm result = service.populateForm(competition);

        assertTrue(result instanceof TermsAndConditionsForm);
        TermsAndConditionsForm form = (TermsAndConditionsForm) result;

        assertNotNull(form.getTermsAndConditionsId());
        assertEquals(form.getTermsAndConditionsId(), id);
    }
}
