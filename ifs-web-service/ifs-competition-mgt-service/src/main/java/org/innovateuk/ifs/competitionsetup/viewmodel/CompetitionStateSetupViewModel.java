package org.innovateuk.ifs.competitionsetup.viewmodel;

public class CompetitionStateSetupViewModel {
    private boolean preventEdit;
    private boolean isSetupAndLive;
    private boolean setupComplete;
    private boolean inPanelState;

    public CompetitionStateSetupViewModel(boolean preventEdit, boolean isSetupAndLive, boolean setupComplete, boolean inPanelState) {
        this.preventEdit = preventEdit;
        this.isSetupAndLive = isSetupAndLive;
        this.setupComplete = setupComplete;
        this.inPanelState = inPanelState;
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

    public boolean isInAssessmentState() { return inPanelState; }
}
