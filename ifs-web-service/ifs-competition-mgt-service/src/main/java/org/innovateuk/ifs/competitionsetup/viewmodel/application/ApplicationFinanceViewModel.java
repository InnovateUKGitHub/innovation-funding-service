package org.innovateuk.ifs.competitionsetup.viewmodel.application;

import org.innovateuk.ifs.competitionsetup.viewmodel.CompetitionSetupSubsectionViewModel;

public class ApplicationFinanceViewModel extends CompetitionSetupSubsectionViewModel {
    private boolean isSectorCompetition;

    public ApplicationFinanceViewModel(boolean isSectorCompetition) {
        this.isSectorCompetition = isSectorCompetition;
    }

    public boolean isSectorCompetition() {
        return isSectorCompetition;
    }
}
