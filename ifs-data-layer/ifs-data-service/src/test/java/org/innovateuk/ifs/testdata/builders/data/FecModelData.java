package org.innovateuk.ifs.testdata.builders.data;

import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.finance.resource.ApplicationFinanceResource;

/**
 * Running data context for generating fec model
 */
public class FecModelData {
    private ApplicationFinanceResource applicationFinance;
    private CompetitionResource competition;

    public ApplicationFinanceResource getApplicationFinance() {
        return applicationFinance;
    }

    public void setApplicationFinance(ApplicationFinanceResource applicationFinance) {
        this.applicationFinance = applicationFinance;
    }

    public CompetitionResource getCompetition() {
        return competition;
    }

    public void setCompetition(CompetitionResource competition) {
        this.competition = competition;
    }
}
