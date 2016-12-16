package org.innovateuk.ifs.competitionsetup.form;

import org.innovateuk.ifs.competition.resource.CompetitionFunderResource;
import org.junit.Test;

import java.math.BigDecimal;

import static org.innovateuk.ifs.competition.builder.CompetitionFunderResourceBuilder.newCompetitionFunderResource;
import static org.junit.Assert.*;

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

        FunderRowForm funderRowForm = new FunderRowForm(funderResource);

        assertEquals(coFunder, funderRowForm.getCoFunder());
        assertEquals(funder, funderRowForm.getFunder());
        assertEquals(funderBudget, funderRowForm.getFunderBudget());
    }
}
