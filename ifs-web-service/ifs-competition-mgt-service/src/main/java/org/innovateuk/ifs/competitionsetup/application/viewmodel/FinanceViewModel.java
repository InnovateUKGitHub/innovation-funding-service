package org.innovateuk.ifs.competitionsetup.application.viewmodel;

import org.innovateuk.ifs.competitionsetup.core.viewmodel.CompetitionSetupSubsectionViewModel;

public class FinanceViewModel implements CompetitionSetupSubsectionViewModel {

    private boolean noFinancesCompetition;

    public FinanceViewModel(boolean noFinancesCompetition) {
        this.noFinancesCompetition = noFinancesCompetition;
    }

    public boolean isNoFinancesCompetition() {
        return noFinancesCompetition;
    }
}
