package org.innovateuk.ifs.competitionsetup.application.viewmodel;

import org.innovateuk.ifs.competitionsetup.core.viewmodel.CompetitionSetupSubsectionViewModel;

public class FinanceViewModel implements CompetitionSetupSubsectionViewModel {

    private boolean sectorCompetition;
    private boolean noFinancesCompetition;

    public FinanceViewModel(boolean sectorCompetition, boolean noFinancesCompetition) {
        this.sectorCompetition = sectorCompetition;
        this.noFinancesCompetition = noFinancesCompetition;
    }

    public boolean isSectorCompetition() {
        return sectorCompetition;
    }

    public boolean isNoFinancesCompetition() {
        return noFinancesCompetition;
    }
}
