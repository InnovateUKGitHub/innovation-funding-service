package org.innovateuk.ifs.application.forms.questions.team.viewmodel;

import org.innovateuk.ifs.address.resource.Countries;
import org.innovateuk.ifs.analytics.BaseAnalyticsViewModel;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;

import java.util.List;

public class ApplicationTeamAddressViewModel implements BaseAnalyticsViewModel {

    private final long questionId;
    private final long applicationId;
    private final long organisationId;
    private final String competitionName;
    private final String organisationType;
    private final String organisationName;
    private final List<String> countries = Countries.COUNTRIES;

    public ApplicationTeamAddressViewModel(ApplicationResource application, OrganisationResource organisation, long questionId) {
        this.questionId = questionId;
        this.applicationId = application.getId();
        this.organisationId = organisation.getId();
        this.organisationType = organisation.getOrganisationTypeName();
        this.organisationName = organisation.getName();
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

    public long getOrganisationId() {
        return organisationId;
    }

    public String getOrganisationType() {
        return organisationType;
    }

    public String getOrganisationName() {
        return organisationName;
    }

    public List<String> getCountries() {
        return countries;
    }
}
