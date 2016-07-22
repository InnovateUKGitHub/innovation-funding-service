package com.worth.ifs.util.fixtures;

import com.worth.ifs.competition.domain.CompetitionCoFunder;
import com.worth.ifs.competition.resource.CompetitionCoFunderResource;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by skistapur on 21/07/2016.
 */
public class CompetitionCoFundersFixture {


    public static List<CompetitionCoFunder> getTestCoFunders(int count) {
        List<CompetitionCoFunder> returnList = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            CompetitionCoFunder coFunder = new CompetitionCoFunder();
            coFunder.setId(Long.valueOf(i));
            coFunder.setCoFunder("coFunder-"+i);
            coFunder.setCoFunderBudget(new BigDecimal(1));
            returnList.add(coFunder);
        }
        return returnList;
    }


    public static List<CompetitionCoFunderResource> getTestCoFundersResouces(int count, Long competitionId) {
        List<CompetitionCoFunderResource> returnList = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            CompetitionCoFunderResource coFunder1 = new CompetitionCoFunderResource();
            coFunder1.setId(Long.valueOf(i));
            coFunder1.setCoFunder("coFunder-"+i);
            coFunder1.setCoFunderBudget(new BigDecimal(1));
            coFunder1.setCompetitionId(competitionId != null ? competitionId : Long.valueOf(i));
            returnList.add(coFunder1);
        }

        return returnList;
    }

    public static List<CompetitionCoFunderResource> getNewTestCoFundersResouces(int count, Long competitionId) {
        List<CompetitionCoFunderResource> returnList = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            CompetitionCoFunderResource coFunder1 = new CompetitionCoFunderResource();
            coFunder1.setCoFunder("coFunder-"+i);
            coFunder1.setCoFunderBudget(new BigDecimal(1));
            coFunder1.setCompetitionId(competitionId != null ? competitionId : Long.valueOf(i));
            returnList.add(coFunder1);
        }

        return returnList;
    }
}
