package org.innovateuk.ifs.application.forms.questions.granttransferdetails.viewmodel;

public class GrantTransferDetailsViewModel {

    private final long applicationId;
    private final String applicationName;
    private final long questionId;
    private final boolean open;
    private final boolean complete;

    public GrantTransferDetailsViewModel(long applicationId, String applicationName, long questionId, boolean open, boolean complete) {
        this.applicationId = applicationId;
        this.applicationName = applicationName;
        this.questionId = questionId;
        this.open = open;
        this.complete = complete;
    }

    public long getApplicationId() {
        return applicationId;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public long getQuestionId() {
        return questionId;
    }

    public boolean isOpen() {
        return open;
    }

    public boolean isComplete() {
        return complete;
    }

    /* view logic */
    public boolean isReadonly() {
        return complete || !open;
    }
}
