package org.innovateuk.ifs.testdata.builders.data;

import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.user.resource.OrganisationResource;
import org.innovateuk.ifs.user.resource.UserResource;

/**
 * Running data context for generating Application Finances (either academic or industrial)
 */
public class ApplicationFinanceData {
    private ApplicationResource application;
    private OrganisationResource organisation;
    private UserResource user;
    private CompetitionResource competition;

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
}
