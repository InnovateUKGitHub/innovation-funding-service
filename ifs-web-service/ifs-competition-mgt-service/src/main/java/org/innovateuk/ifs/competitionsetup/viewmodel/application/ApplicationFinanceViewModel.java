package org.innovateuk.ifs.competitionsetup.viewmodel.application;

import org.innovateuk.ifs.competitionsetup.viewmodel.CompetitionSetupSubsectionViewModel;

public class ApplicationFinanceViewModel extends CompetitionSetupSubsectionViewModel {
    private boolean sectorCompetition;
    private boolean noneFinanceCompetition;

    public ApplicationFinanceViewModel(boolean sectorCompetition, boolean noneFinanceCompetition) {
        this.sectorCompetition = sectorCompetition;
        this.noneFinanceCompetition = noneFinanceCompetition;
    }

    public boolean isSectorCompetition() {
        return sectorCompetition;
    }

    public boolean isNoneFinanceCompetition() {
        return noneFinanceCompetition;
    }
}
