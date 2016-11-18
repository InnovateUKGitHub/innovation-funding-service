package com.worth.ifs.testdata.builders.data;

import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.finance.resource.ApplicationFinanceResource;

/**
 * Running data context for generating Academic costs
 */
public class AcademicCostData {
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
