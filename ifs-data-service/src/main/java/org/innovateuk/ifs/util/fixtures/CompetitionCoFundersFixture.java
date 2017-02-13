package org.innovateuk.ifs.util.fixtures;

import org.innovateuk.ifs.competition.domain.CompetitionFunder;

import java.math.BigInteger;
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
            coFunder.setFunderBudget(BigInteger.valueOf(1));
            coFunder.setCoFunder(true);
            returnList.add(coFunder);
        }
        return returnList;
    }
}
