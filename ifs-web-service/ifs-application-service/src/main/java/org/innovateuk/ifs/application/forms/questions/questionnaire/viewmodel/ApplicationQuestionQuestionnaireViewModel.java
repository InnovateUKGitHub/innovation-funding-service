package org.innovateuk.ifs.application.forms.questions.questionnaire.viewmodel;

public class ApplicationQuestionQuestionnaireViewModel {
    private final long applicationId;
    private final long questionId;
    private final long organisationId;
    private final boolean open;
    private final boolean complete;

    public ApplicationQuestionQuestionnaireViewModel(long applicationId, long questionId, long organisationId, boolean open, boolean complete) {
        this.applicationId = applicationId;
        this.questionId = questionId;
        this.organisationId = organisationId;
        this.open = open;
        this.complete = complete;
    }

    public long getApplicationId() {
        return applicationId;
    }

    public long getQuestionId() {
        return questionId;
    }

    public long getOrganisationId() {
        return organisationId;
    }

    public boolean isOpen() {
        return open;
    }

    public boolean isComplete() {
        return complete;
    }
    public boolean isReadOnly() {
        return complete || !open;
    }
}
