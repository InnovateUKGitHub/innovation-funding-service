package org.innovateuk.ifs.project.monitoringofficer.viewmodel;

public class MonitoringOfficerSummaryViewModel {

    private int inSetupProjectCount;
    private int previousProjectCount;

    private int documentsCompleteCount;
    private int documentsIncompleteCount;
    private int documentsAwaitingReviewCount;

    private int spendProfileCompleteCount;
    private int spendProfileIncompleteCount;
    private int spendProfileAwaitingReviewCount;

    public MonitoringOfficerSummaryViewModel() {
    }

    public MonitoringOfficerSummaryViewModel(int inSetupProjectCount
            , int previousProjectCount
            , int documentsCompleteCount
            , int documentsIncompleteCount
            , int documentsAwaitingReviewCount
            , int spendProfileCompleteCount
            , int spendProfileIncompleteCount
            , int spendProfileAwaitingReviewCount) {
        this.inSetupProjectCount = inSetupProjectCount;
        this.previousProjectCount = previousProjectCount;
        this.documentsCompleteCount = documentsCompleteCount;
        this.documentsIncompleteCount = documentsIncompleteCount;
        this.documentsAwaitingReviewCount = documentsAwaitingReviewCount;
        this.spendProfileCompleteCount = spendProfileCompleteCount;
        this.spendProfileIncompleteCount = spendProfileIncompleteCount;
        this.spendProfileAwaitingReviewCount = spendProfileAwaitingReviewCount;
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

    public int getSpendProfileCompleteCount() {
        return spendProfileCompleteCount;
    }

    public int getSpendProfileIncompleteCount() {
        return spendProfileIncompleteCount;
    }

    public int getSpendProfileAwaitingReviewCount() {
        return spendProfileAwaitingReviewCount;
    }
}
