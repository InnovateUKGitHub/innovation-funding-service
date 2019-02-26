package org.innovateuk.ifs.application.forms.questions.grantagreement.model;

public class GrantAgreementViewModel {

    private final long applicationId;
    private final String applicationName;
    private final long questionId;
    private final String filename;
    private final boolean open;
    private final boolean complete;

    public GrantAgreementViewModel(long applicationId, String applicationName, long questionId, String filename, boolean open, boolean complete) {
        this.applicationId = applicationId;
        this.applicationName = applicationName;
        this.questionId = questionId;
        this.filename = filename;
        this.open = open;
        this.complete = complete;
    }

    public long getQuestionId() {
        return questionId;
    }
    public String getFilename() {
        return filename;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public long getApplicationId() {
        return applicationId;
    }

    public boolean isOpen() {
        return open;
    }

    public boolean isComplete() {
        return complete;
    }

    /* view logic */
    public boolean isUploaded() {
        return filename != null;
    }

    public boolean isReadonly() {
        return complete || !open;
    }
}
