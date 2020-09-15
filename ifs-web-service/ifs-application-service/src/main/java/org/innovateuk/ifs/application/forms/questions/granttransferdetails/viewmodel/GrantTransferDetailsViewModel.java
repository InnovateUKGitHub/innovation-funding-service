package org.innovateuk.ifs.application.forms.questions.granttransferdetails.viewmodel;

import org.innovateuk.ifs.analytics.BaseAnalyticsViewModel;
import org.innovateuk.ifs.granttransfer.resource.EuActionTypeResource;

import java.util.List;

public class GrantTransferDetailsViewModel implements BaseAnalyticsViewModel {

    private final String applicationName;
    private final String competitionName;
    private final long questionId;
    private final long applicationId;
    private final boolean open;
    private final boolean complete;
    private final List<EuActionTypeResource> actionTypes;

    public GrantTransferDetailsViewModel(long applicationId, String competitionName, String applicationName, long questionId, boolean open, boolean complete, List<EuActionTypeResource> actionTypes) {
        this.applicationId = applicationId;
        this.competitionName = competitionName;
        this.applicationName = applicationName;
        this.questionId = questionId;
        this.open = open;
        this.complete = complete;
        this.actionTypes = actionTypes;
    }

    @Override
    public Long getApplicationId() {
        return applicationId;
    }

    @Override
    public String getCompetitionName() {
        return competitionName;
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
