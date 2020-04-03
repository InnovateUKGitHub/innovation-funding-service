package org.innovateuk.ifs.application.forms.questions.team.viewmodel;

import org.innovateuk.ifs.analytics.BaseAnalyticsViewModel;
import org.innovateuk.ifs.application.resource.ApplicationResource;

public class ApplicationTeamAddOrganisationViewModel implements BaseAnalyticsViewModel {

    private final long questionId;
    private final long applicationId;
    private final String applicationName;
    private final String competitionName;

    public ApplicationTeamAddOrganisationViewModel(ApplicationResource application, long questionId) {
        this.questionId = questionId;
        this.applicationName = application.getName();
        this.applicationId = application.getId();
        this.competitionName = application.getCompetitionName();
    }

    @Override
    public Long getApplicationId() {
        return applicationId;
    }

    @Override
    public String getCompetitionName() {
        return competitionName;
    }

    public long getQuestionId() {
        return questionId;
    }

    public String getApplicationName() {
        return applicationName;
    }
}
