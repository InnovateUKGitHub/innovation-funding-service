package org.innovateuk.ifs.competitionsetup.common.form;

import org.innovateuk.ifs.competition.resource.CompetitionFunderResource;
import org.junit.Test;

import java.math.BigInteger;

import static org.innovateuk.ifs.competition.builder.CompetitionFunderResourceBuilder.newCompetitionFunderResource;
import static org.junit.Assert.assertEquals;

public class FunderRowFormTest {

    @Test
    public void testGetAppendixFileDescription() throws Exception {
        Boolean coFunder = Boolean.FALSE;
        String funder = "Funder";
        BigInteger funderBudget = BigInteger.valueOf(12345678);

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
