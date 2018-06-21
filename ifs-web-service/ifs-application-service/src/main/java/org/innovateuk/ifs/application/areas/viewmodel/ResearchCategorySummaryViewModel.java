package org.innovateuk.ifs.application.areas.viewmodel;

public class ResearchCategorySummaryViewModel {

    private long applicationId;
    private String researchCategory;
    private boolean canMarkAsComplete;
    private boolean closed;
    private boolean complete;

    public ResearchCategorySummaryViewModel(long applicationId,
                                            String researchCategory,
                                            boolean canMarkAsComplete,
                                            boolean closed,
                                            boolean complete) {
        this.applicationId = applicationId;
        this.researchCategory = researchCategory;
        this.canMarkAsComplete = canMarkAsComplete;
        this.closed = closed;
        this.complete = complete;
    }

    public long getApplicationId() {
        return applicationId;
    }

    public String getResearchCategory() {
        return researchCategory;
    }

    public boolean isCanMarkAsComplete() {
        return canMarkAsComplete;
    }

    public boolean isClosed() {
        return closed;
    }

    public boolean isComplete() {
        return complete;
    }

    public boolean isSummary() {
        return true;
    }
}
