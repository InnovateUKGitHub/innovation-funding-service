package org.innovateuk.ifs.project.monitoringofficer.viewmodel;

public class MonitoringOfficerSummaryViewModel {

    private final int inSetupProjectCount;
    private final int previousProjectCount;

    private int documentsCompleteCount;
    private int documentsIncompleteCount;
    private int documentsAwaitingReviewCount;

    // when toggle is false
    public MonitoringOfficerSummaryViewModel(int inSetupProjectCount, int previousProjectCount) {
        this.inSetupProjectCount = inSetupProjectCount;
        this.previousProjectCount = previousProjectCount;
    }

    public MonitoringOfficerSummaryViewModel(int inSetupProjectCount, int previousProjectCount, int documentsCompleteCount, int documentsIncompleteCount, int documentsAwaitingReviewCount) {
        this.inSetupProjectCount = inSetupProjectCount;
        this.previousProjectCount = previousProjectCount;
        this.documentsCompleteCount = documentsCompleteCount;
        this.documentsIncompleteCount = documentsIncompleteCount;
        this.documentsAwaitingReviewCount = documentsAwaitingReviewCount;
    }

    public int getInSetupProjectCount() {
        return inSetupProjectCount;
    }

    public int getPreviousProjectCount() {
        return previousProjectCount;
    }

    public int getDocumentsCompleteCount() {
        return documentsCompleteCount;
    }

    public int getDocumentsIncompleteCount() {
        return documentsIncompleteCount;
    }

    public int getDocumentsAwaitingReviewCount() {
        return documentsAwaitingReviewCount;
    }

}