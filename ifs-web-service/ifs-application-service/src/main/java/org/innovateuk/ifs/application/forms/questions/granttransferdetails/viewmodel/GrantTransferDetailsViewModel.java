package org.innovateuk.ifs.application.forms.questions.granttransferdetails.viewmodel;

import org.innovateuk.ifs.granttransfer.resource.EuActionTypeResource;

import java.util.List;

public class GrantTransferDetailsViewModel {

    private final long applicationId;
    private final String applicationName;
    private final long questionId;
    private final boolean open;
    private final boolean complete;
    private final List<EuActionTypeResource> actionTypes;

    public GrantTransferDetailsViewModel(long applicationId, String applicationName, long questionId, boolean open, boolean complete, List<EuActionTypeResource> actionTypes) {
        this.applicationId = applicationId;
        this.applicationName = applicationName;
        this.questionId = questionId;
        this.open = open;
        this.complete = complete;
        this.actionTypes = actionTypes;
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

    public List<EuActionTypeResource> getActionTypes() {
        return actionTypes;
    }

    /* view logic */
    public boolean isReadonly() {
        return complete || !open;
    }
}
