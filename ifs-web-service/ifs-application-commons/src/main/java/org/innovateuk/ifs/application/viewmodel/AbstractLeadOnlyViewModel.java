package org.innovateuk.ifs.application.viewmodel;

/**
 * Generic ViewModel for common fields in LeadOnlyViewModels
 */
public abstract class AbstractLeadOnlyViewModel {

    private Long questionId;
    private Long applicationId;
    private boolean closed;
    private boolean complete;
    private boolean canMarkAsComplete;
    private boolean allReadOnly;

    protected AbstractLeadOnlyViewModel(Long questionId,
                                        Long applicationId,
                                        boolean closed,
                                        boolean complete,
                                        boolean canMarkAsComplete,
                                        boolean allReadOnly) {
        this.questionId = questionId;
        this.applicationId = applicationId;
        this.closed = closed;
        this.complete = complete;
        this.canMarkAsComplete = canMarkAsComplete;
        this.allReadOnly = allReadOnly;
    }

    public Long getQuestionId() {
        return questionId;
    }

    public Long getApplicationId() {
        return applicationId;
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
