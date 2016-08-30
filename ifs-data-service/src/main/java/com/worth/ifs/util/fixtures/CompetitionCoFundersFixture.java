package com.worth.ifs.util.fixtures;

import com.worth.ifs.competition.domain.CompetitionFunder;
import com.worth.ifs.competition.resource.CompetitionFunderResource;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class CompetitionCoFundersFixture {


    public static List<CompetitionFunder> getTestCoFunders(int count) {
        List<CompetitionFunder> returnList = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            CompetitionFunder coFunder = new CompetitionFunder();
            coFunder.setId(Long.valueOf(i));
            coFunder.setFunder("coFunder-"+i);
            coFunder.setFunderBudget(new BigDecimal(1));
            coFunder.setCoFunder(true);
            returnList.add(coFunder);
        }
        return returnList;
    }


    public static List<CompetitionFunderResource> getTestCoFundersResouces(int count, Long competitionId) {
        List<CompetitionFunderResource> returnList = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            CompetitionFunderResource coFunder1 = new CompetitionFunderResource();
            coFunder1.setId(Long.valueOf(i));
            coFunder1.setFunder("coFunder-"+i);
            coFunder1.setFunderBudget(new BigDecimal(1));
            coFunder1.setCoFunder(true);
            coFunder1.setCompetitionId(competitionId != null ? competitionId : Long.valueOf(i));
            returnList.add(coFunder1);
        }

        return returnList;
    }

    public static List<CompetitionFunderResource> getNewTestCoFundersResouces(int count, Long competitionId) {
        List<CompetitionFunderResource> returnList = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            CompetitionFunderResource coFunder1 = new CompetitionFunderResource();
            coFunder1.setFunder("coFunder-"+i);
            coFunder1.setFunderBudget(new BigDecimal(1));
            coFunder1.setCoFunder(true);
            coFunder1.setCompetitionId(competitionId != null ? competitionId : Long.valueOf(i));
            returnList.add(coFunder1);
        }

        return returnList;
    }
}
