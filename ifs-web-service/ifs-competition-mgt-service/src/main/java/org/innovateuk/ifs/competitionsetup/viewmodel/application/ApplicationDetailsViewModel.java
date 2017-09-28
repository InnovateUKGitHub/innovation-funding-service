package org.innovateuk.ifs.competitionsetup.viewmodel.application;

import org.innovateuk.ifs.competitionsetup.viewmodel.CompetitionSetupSubsectionViewModel;
import org.innovateuk.ifs.competitionsetup.viewmodel.fragments.GeneralSetupViewModel;

public class ApplicationDetailsViewModel extends CompetitionSetupSubsectionViewModel {
    private Long competitionId;
    private String competitionName;

    public ApplicationDetailsViewModel(GeneralSetupViewModel generalViewModel, Long competitionId, String competitionName) {
        this.generalSetupViewModel = generalViewModel;
        this.competitionId = competitionId;
        this.competitionName = competitionName;
    }

    public Long getCompetitionId() {
        return competitionId;
    }

    public String getCompetitionName() {
        return competitionName;
    }
}
