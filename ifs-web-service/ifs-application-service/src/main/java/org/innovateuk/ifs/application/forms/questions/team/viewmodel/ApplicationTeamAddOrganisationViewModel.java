package org.innovateuk.ifs.application.forms.questions.team.viewmodel;

import org.innovateuk.ifs.application.resource.ApplicationResource;

public class ApplicationTeamAddOrganisationViewModel {

    private final long applicationId;
    private final long questionId;
    private final String applicationName;

    public ApplicationTeamAddOrganisationViewModel(ApplicationResource application, long questionId) {
        this.applicationId = application.getId();
        this.questionId = questionId;
        this.applicationName = application.getName();
    }

    public long getApplicationId() {
        return applicationId;
    }

    public long getQuestionId() {
        return questionId;
    }

    public String getApplicationName() {
        return applicationName;
    }
}
