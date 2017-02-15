package org.innovateuk.ifs.management.viewmodel;

import java.util.List;

/**
 * Base view model for creating Competition Management Applications pages.
 */
abstract class BaseApplicationsViewModel<ApplicationRowViewModel extends BaseApplicationsRowViewModel> {

    protected long competitionId;
    protected String competitionName;
    protected List<ApplicationRowViewModel> applications;

    BaseApplicationsViewModel(long competitionId, String competitionName, List<ApplicationRowViewModel> applications) {
        this.competitionId = competitionId;
        this.competitionName = competitionName;
        this.applications = applications;
    }

    public long getCompetitionId() {
        return competitionId;
    }

    public String getCompetitionName() {
        return competitionName;
    }

    public List<ApplicationRowViewModel> getApplications() {
        return applications;
    }
}
