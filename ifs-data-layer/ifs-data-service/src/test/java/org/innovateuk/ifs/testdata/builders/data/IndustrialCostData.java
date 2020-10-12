package org.innovateuk.ifs.testdata.builders.data;

import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.finance.resource.ApplicationFinanceResource;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;

/**
 * Running data context for generating Industrial costs
 */
public class IndustrialCostData {
    private ApplicationFinanceResource applicationFinance;
    private CompetitionResource competition;
    private OrganisationResource organisation;

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

    public OrganisationResource getOrganisation() {
        return organisation;
    }

    public void setOrganisation(OrganisationResource organisation) {
        this.organisation = organisation;
    }
}
