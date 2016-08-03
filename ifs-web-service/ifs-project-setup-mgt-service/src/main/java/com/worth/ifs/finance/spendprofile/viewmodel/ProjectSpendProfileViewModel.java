package com.worth.ifs.finance.spendprofile.viewmodel;

import com.worth.ifs.application.resource.CompetitionSummaryResource;

/**
 * View model backing the internal Finance Team members view of the Spend Profile summary page
 */
public class ProjectSpendProfileViewModel {

    private CompetitionSummaryResource competitionSummary;

    public ProjectSpendProfileViewModel(CompetitionSummaryResource competitionSummary) {
        this.competitionSummary = competitionSummary;
    }

    public CompetitionSummaryResource getCompetitionSummary() {
        return competitionSummary;
    }
}
