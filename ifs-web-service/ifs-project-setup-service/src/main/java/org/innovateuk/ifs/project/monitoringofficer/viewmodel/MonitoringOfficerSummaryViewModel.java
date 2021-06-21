package org.innovateuk.ifs.project.monitoringofficer.viewmodel;

public class MonitoringOfficerSummaryViewModel {

    private final int inSetupProjectCount;
    private final int previousProjectCount;

    public MonitoringOfficerSummaryViewModel(int inSetupProjectCount, int previousProjectCount) {
        this.inSetupProjectCount = inSetupProjectCount;
        this.previousProjectCount = previousProjectCount;
    }

    public int getInSetupProjectCount() {
        return inSetupProjectCount;
    }

    public int getPreviousProjectCount() {
        return previousProjectCount;
    }
}
