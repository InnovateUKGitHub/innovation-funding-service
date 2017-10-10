package org.innovateuk.ifs.competitionsetup.viewmodel;

public class CompetitionStateSetupViewModel {
    private boolean preventEdit;
    private boolean isSetupAndLive;
    private boolean setupComplete;

    public CompetitionStateSetupViewModel(boolean preventEdit, boolean isSetupAndLive, boolean setupComplete) {
        this.preventEdit = preventEdit;
        this.isSetupAndLive = isSetupAndLive;
        this.setupComplete = setupComplete;
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
}
