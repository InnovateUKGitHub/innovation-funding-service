package org.innovateuk.ifs.application.forms.questions.team.viewmodel;

import org.innovateuk.ifs.analytics.BaseAnalyticsViewModel;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.organisation.resource.OrganisationTypeResource;

import java.util.List;

public class ApplicationTeamAddOrganisationTypeViewModel implements BaseAnalyticsViewModel {

    private final long questionId;
    private final long applicationId;
    private final String applicationName;
    private final String competitionName;
    private final List<OrganisationTypeResource> types;

    public ApplicationTeamAddOrganisationTypeViewModel(ApplicationResource application, long questionId,
                                                       List<OrganisationTypeResource> types) {
        this.questionId = questionId;
        this.applicationId = application.getId();
        this.applicationName = application.getName();
        this.competitionName = application.getCompetitionName();
        this.types = types;
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

    public List<OrganisationTypeResource> getTypes() {
        return types;
    }
}
