package org.innovateuk.ifs.competitionsetup.stakeholder.viewmodel;

import org.innovateuk.ifs.competitionsetup.core.viewmodel.CompetitionSetupViewModel;

public class ManageStakeholderViewModel extends CompetitionSetupViewModel {

    private Long competitionId;
    private String competitionName;

    public ManageStakeholderViewModel(Long competitionId, String competitionName) {
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

