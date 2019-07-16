package org.innovateuk.ifs.management.competition.setup.fundinginformation.populator;

import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.Funder;
import org.innovateuk.ifs.management.competition.setup.core.form.CompetitionSetupForm;
import org.innovateuk.ifs.management.competition.setup.fundinginformation.form.AdditionalInfoForm;
import org.innovateuk.ifs.management.fixtures.CompetitionFundersFixture;
import org.junit.Before;
import org.junit.Test;

import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class AdditionalInfoFormPopulatorTest {

    private AdditionalInfoFormPopulator service;

    @Before
    public void setUp() {
        service = new AdditionalInfoFormPopulator();
    }

    @Test
    public void testGetSectionFormDataAdditionalInfo() {
        CompetitionResource competition = newCompetitionResource()
                .withActivityCode("Activity Code")
                .withCompetitionCode("c123")
                .withPafCode("p123")
                .withBudgetCode("b123")
                .withFunders(CompetitionFundersFixture.getTestCoFunders())
                .withId(8L).build();

        CompetitionSetupForm result = service.populateForm(competition);

        assertTrue(result instanceof AdditionalInfoForm);
        AdditionalInfoForm form = (AdditionalInfoForm) result;
        assertEquals("Activity Code", form.getActivityCode());
        assertEquals("c123", form.getCompetitionCode());
        assertEquals("p123", form.getPafNumber());
        assertEquals("b123", form.getBudgetCode());
        assertEquals(CompetitionFundersFixture.getTestCoFunders().size(), form.getFundersCount());
        assertEquals(Funder.ADVANCED_PROPULSION_CENTRE_APC, form.getFunders().get(0).getFunder());
    }
}
