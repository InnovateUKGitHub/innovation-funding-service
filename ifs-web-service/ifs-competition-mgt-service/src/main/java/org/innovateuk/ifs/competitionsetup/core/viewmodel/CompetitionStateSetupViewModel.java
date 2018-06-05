package org.innovateuk.ifs.competitionsetup.core.viewmodel;

import org.innovateuk.ifs.competition.resource.CompetitionStatus;

public class CompetitionStateSetupViewModel {
    private boolean preventEdit;
    private boolean isSetupAndLive;
    private boolean setupComplete;
    private CompetitionStatus competitionStatus;

    public CompetitionStateSetupViewModel(boolean preventEdit, boolean isSetupAndLive, boolean setupComplete, CompetitionStatus competitionStatus) {
        this.preventEdit = preventEdit;
        this.isSetupAndLive = isSetupAndLive;
        this.setupComplete = setupComplete;
        this.competitionStatus = competitionStatus;
    }

    public boolean isPreventEdit() {
        return preventEdit;
    }

    public boolean isSetupAndLive() {
        return isSetupAndLive;
    }

    public boolean isSetupComplete() {
        return setupComplete;
    }

    public CompetitionStatus getCompetitionStatus() {
        return competitionStatus;
    }
}
