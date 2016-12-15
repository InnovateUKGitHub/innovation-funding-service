package org.innovateuk.ifs.competitionsetup.viewmodel;

import org.innovateuk.ifs.competition.resource.CompetitionFunderResource;
import org.innovateuk.ifs.competitionsetup.form.InitialDetailsForm;
import org.junit.Test;

import java.math.BigDecimal;

import static org.innovateuk.ifs.competition.builder.CompetitionFunderResourceBuilder.newCompetitionFunderResource;
import static org.junit.Assert.assertEquals;

public class FunderRowFormTest {

    @Test
    public void testGetAppendixFileDescription() throws Exception {
        Boolean coFunder = Boolean.FALSE;
        String funder = "Funder";
        BigDecimal funderBudget = BigDecimal.valueOf(123.45678);

        CompetitionFunderResource funderResource = newCompetitionFunderResource()
                .withCoFunder(coFunder)
                .withCompetitionId(123L)
                .withFunder(funder)
                .withFunderBudget(funderBudget)
                .build();

        InitialDetailsForm.FunderRowForm funderRowForm = new InitialDetailsForm.FunderRowForm(funderResource);

        assertEquals(coFunder, funderRowForm.getCoFunder());
        assertEquals(funder, funderRowForm.getFunder());
        assertEquals(funderBudget, funderRowForm.getFunderBudget());
    }
}
