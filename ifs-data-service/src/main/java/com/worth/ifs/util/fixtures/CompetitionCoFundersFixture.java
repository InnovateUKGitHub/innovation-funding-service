package com.worth.ifs.util.fixtures;

import com.worth.ifs.competition.domain.CompetitionFunder;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by worth on 15/11/2016.
 */
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
}
