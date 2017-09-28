package org.innovateuk.ifs.competitionsetup.viewmodel.application;

import org.innovateuk.ifs.competitionsetup.viewmodel.CompetitionSetupSubsectionViewModel;
import org.innovateuk.ifs.competitionsetup.viewmodel.fragments.GeneralSetupViewModel;

public class ApplicationFinanceViewModel extends CompetitionSetupSubsectionViewModel {
    private boolean isSectorCompetition;
    private Long competitionId;

    public ApplicationFinanceViewModel(GeneralSetupViewModel generalViewModel, boolean isSectorCompetition, Long competitionId) {
        this.generalSetupViewModel = generalViewModel;
        this.isSectorCompetition = isSectorCompetition;
        this.competitionId = competitionId;
    }

    public boolean isSectorCompetition() {
        return isSectorCompetition;
    }

    public Long getCompetitionId() {
        return competitionId;
    }
}
