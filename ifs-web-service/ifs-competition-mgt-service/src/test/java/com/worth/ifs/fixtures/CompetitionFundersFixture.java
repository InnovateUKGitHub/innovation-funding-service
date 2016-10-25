package com.worth.ifs.fixtures;

import com.worth.ifs.competition.resource.CompetitionFunderResource;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class CompetitionFundersFixture {

    public static List<CompetitionFunderResource> getTestCoFunders() {
        List<CompetitionFunderResource> returnList = new ArrayList<>();
        CompetitionFunderResource coFunder1 = new CompetitionFunderResource();
        coFunder1.setId(1L);
        coFunder1.setFunder("coFunder1");
        coFunder1.setFunderBudget(new BigDecimal(1));
        coFunder1.setCoFunder(true);
        coFunder1.setCompetitionId(1L);
        returnList.add(coFunder1);

        CompetitionFunderResource coFunder2 = new CompetitionFunderResource();
        coFunder2.setId(2L);
        coFunder2.setFunder("coFunder2");
        coFunder2.setFunderBudget(new BigDecimal(2));
        coFunder2.setCoFunder(true);
        coFunder2.setCompetitionId(1L);
        returnList.add(coFunder2);

        return returnList;
    }
}
