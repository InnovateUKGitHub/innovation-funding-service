package org.innovateuk.ifs.competitionsetup.fundinginformation.populator;

import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competitionsetup.core.form.CompetitionSetupForm;
import org.innovateuk.ifs.competitionsetup.fundinginformation.form.AdditionalInfoForm;
import org.innovateuk.ifs.fixtures.CompetitionFundersFixture;
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
        assertEquals("coFunder1", form.getFunders().get(0).getFunder());
    }
}
