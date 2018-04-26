package org.innovateuk.ifs.competitionsetup.service.formpopulator;

import org.innovateuk.ifs.BaseUnitTestMocksTest;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionSetupSection;
import org.innovateuk.ifs.competition.resource.TermsAndConditionsResource;
import org.innovateuk.ifs.competitionsetup.form.CompetitionSetupForm;
import org.innovateuk.ifs.competitionsetup.form.TermsAndConditionsForm;
import org.junit.Test;
import org.mockito.InjectMocks;

import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.competition.builder.TermsAndConditionsResourceBuilder.newTermsAndConditionsResource;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

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
        String termsAndConditionsName = "default";
        String template = "default-template";
        String version = "1";
        TermsAndConditionsResource termsAndConditions = newTermsAndConditionsResource()
                .withName(termsAndConditionsName)
                .withTemplate(template)
                .withVersion(version).build();

        CompetitionResource competition = newCompetitionResource()
                .withTermsAndConditions(termsAndConditions).build();

        CompetitionSetupForm result = service.populateForm(competition);

        assertTrue(result instanceof TermsAndConditionsForm);
        TermsAndConditionsForm form = (TermsAndConditionsForm) result;

        assertFalse(form.getTermsAndConditions() == null);
        assertTrue(form.getTermsAndConditions().getName().equals(termsAndConditionsName));
        assertTrue(form.getTermsAndConditions().getTemplate().equals(template));
        assertTrue(form.getTermsAndConditions().getVersion().equals(version));
    }
}
