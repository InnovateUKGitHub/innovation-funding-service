package org.innovateuk.ifs.testdata.builders.data;

import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.user.resource.UserResource;

import java.util.List;

public class SubsidyBasisData {
    private ApplicationResource application;
    private OrganisationResource organisation;
    private UserResource user;
    private CompetitionResource competition;
    private List<String> selectedOptions;

    public void setApplication(ApplicationResource application) {
        this.application = application;
    }

    public ApplicationResource getApplication() {
        return application;
    }

    public void setOrganisation(OrganisationResource organisation) {
        this.organisation = organisation;
    }

    public OrganisationResource getOrganisation() {
        return organisation;
    }

    public void setUser(UserResource user) {
        this.user = user;
    }

    public UserResource getUser() {
        return user;
    }

    public void setCompetition(CompetitionResource competition) {
        this.competition = competition;
    }

    public CompetitionResource getCompetition() {
        return competition;
    }

    public List<String> getSelectedOptions() {
        return selectedOptions;
    }

    public void setSelectedOptions(List<String> selectedOptions) {
        this.selectedOptions = selectedOptions;
    }
}
