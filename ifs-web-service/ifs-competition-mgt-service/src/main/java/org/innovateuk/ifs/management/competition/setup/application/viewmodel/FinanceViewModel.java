package org.innovateuk.ifs.management.competition.setup.application.viewmodel;

import org.innovateuk.ifs.management.competition.setup.core.viewmodel.CompetitionSetupSubsectionViewModel;

public class FinanceViewModel implements CompetitionSetupSubsectionViewModel {

    private boolean noFinancesCompetition;

    public FinanceViewModel(boolean noFinancesCompetition) {
        this.noFinancesCompetition = noFinancesCompetition;
    }

    public boolean isNoFinancesCompetition() {
        return noFinancesCompetition;
    }
}
