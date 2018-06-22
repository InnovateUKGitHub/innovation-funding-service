package org.innovateuk.ifs.application.viewmodel;

/**
 * Generic ViewModel for common fields in LeadOnlyViewModels
 */
public abstract class AbstractLeadOnlyViewModel {

    private Long questionId;
    private boolean closed;
    private boolean complete;
    private boolean canMarkAsComplete;

    protected AbstractLeadOnlyViewModel(Long questionId,
                                        boolean closed,
                                        boolean complete,
                                        boolean canMarkAsComplete) {
        this.questionId = questionId;
        this.closed = closed;
        this.complete = complete;
        this.canMarkAsComplete = canMarkAsComplete;
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

    public abstract boolean isSummary();
}
