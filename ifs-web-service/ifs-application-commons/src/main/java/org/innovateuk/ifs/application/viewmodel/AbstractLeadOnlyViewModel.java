package org.innovateuk.ifs.application.viewmodel;

import org.innovateuk.ifs.analytics.BaseAnalyticsViewModel;

/**
 * Generic ViewModel for common fields in LeadOnlyViewModels
 */
public abstract class AbstractLeadOnlyViewModel implements BaseAnalyticsViewModel {

    private Long questionId;
    private Long applicationId;
    private String competitionName;
    private boolean closed;
    private boolean complete;
    private boolean canMarkAsComplete;
    private boolean allReadOnly;

    protected AbstractLeadOnlyViewModel(Long questionId,
                                        Long applicationId,
                                        String competitionName,
                                        boolean closed,
                                        boolean complete,
                                        boolean canMarkAsComplete,
                                        boolean allReadOnly) {
        this.questionId = questionId;
        this.competitionName = competitionName;
        this.applicationId = applicationId;
        this.closed = closed;
        this.complete = complete;
        this.canMarkAsComplete = canMarkAsComplete;
        this.allReadOnly = allReadOnly;
    }

    @Override
    public Long getApplicationId() {
        return null;
    }

    @Override
    public String getCompetitionName() {
        return null;
    }

    public Long getQuestionId() {
        return questionId;
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

    public boolean isAllReadOnly() {
        return allReadOnly;
    }

    public abstract boolean isSummary();
}
