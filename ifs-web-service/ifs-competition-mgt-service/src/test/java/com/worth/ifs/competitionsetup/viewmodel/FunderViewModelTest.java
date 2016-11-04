package com.worth.ifs.competitionsetup.viewmodel;

import com.worth.ifs.competition.resource.CompetitionFunderResource;
import org.junit.Test;

import java.math.BigDecimal;

import static com.worth.ifs.competition.builder.CompetitionFunderResourceBuilder.newCompetitionFunderResource;
import static org.junit.Assert.assertEquals;

public class FunderViewModelTest {

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

        FunderViewModel funderViewModel = new FunderViewModel(funderResource);

        assertEquals(coFunder, funderViewModel.getCoFunder());
        assertEquals(funder, funderViewModel.getFunder());
        assertEquals(funderBudget, funderViewModel.getFunderBudget());
    }
}