package com.worth.ifs.fixtures;

import com.worth.ifs.competition.resource.CompetitionCoFunderResource;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by skistapur on 21/07/2016.
 */
public class CompetitionCoFundersFixture {


    public static List<CompetitionCoFunderResource> getTestCoFunders() {
        List<CompetitionCoFunderResource> returnList = new ArrayList<>();
        CompetitionCoFunderResource coFunder1 = new CompetitionCoFunderResource();
        coFunder1.setId(1L);
        coFunder1.setCoFunder("coFunder1");
        coFunder1.setCoFunderBudget(new BigDecimal(1));
        coFunder1.setCompetitionId(1L);
        returnList.add(coFunder1);

        CompetitionCoFunderResource coFunder2 = new CompetitionCoFunderResource();
        coFunder2.setId(2L);
        coFunder2.setCoFunder("coFunder2");
        coFunder2.setCoFunderBudget(new BigDecimal(2));
        coFunder2.setCompetitionId(1L);
        returnList.add(coFunder2);

        return returnList;
    }
}
