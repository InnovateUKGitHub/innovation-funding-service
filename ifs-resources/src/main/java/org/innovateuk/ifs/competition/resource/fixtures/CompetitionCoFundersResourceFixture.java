package org.innovateuk.ifs.competition.resource.fixtures;

import org.innovateuk.ifs.competition.resource.CompetitionFunderResource;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public final class CompetitionCoFundersResourceFixture {

    private CompetitionCoFundersResourceFixture() {}

    public static List<CompetitionFunderResource> getTestCoFundersResouces(int count, Long competitionId) {
        List<CompetitionFunderResource> returnList = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            CompetitionFunderResource coFunder1 = new CompetitionFunderResource();
            coFunder1.setId(Long.valueOf(i));
            coFunder1.setFunder("coFunder-"+i);
            coFunder1.setFunderBudget(BigInteger.valueOf(1L));
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
            coFunder1.setFunderBudget(BigInteger.valueOf(1L));
            coFunder1.setCoFunder(true);
            coFunder1.setCompetitionId(competitionId != null ? competitionId : Long.valueOf(i));
            returnList.add(coFunder1);
        }

        return returnList;
    }
}
