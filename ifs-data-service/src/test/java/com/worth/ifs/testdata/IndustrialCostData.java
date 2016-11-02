package com.worth.ifs.testdata;

import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.finance.resource.ApplicationFinanceResource;

/**
 * TODO DW - document this class
 */
public class IndustrialCostData {
    private ApplicationFinanceResource applicationFinance;
    private CompetitionResource competition;

    public void setApplicationFinance(ApplicationFinanceResource applicationFinance) {
        this.applicationFinance = applicationFinance;
    }

    public ApplicationFinanceResource getApplicationFinance() {
        return applicationFinance;
    }

    public void setCompetition(CompetitionResource competition) {
        this.competition = competition;
    }

    public CompetitionResource getCompetition() {
        return competition;
    }
}
