package org.innovateuk.ifs.management.viewmodel;

import java.util.List;

/**
 * Base view model for creating Competition Management Applications pages.
 */
abstract class BaseApplicationsViewModel<ApplicationRowViewModel extends BaseApplicationsRowViewModel> {

    protected long competitionId;
    protected String competitionName;
    protected List<ApplicationRowViewModel> applications;
    protected PaginationViewModel pagination;

    BaseApplicationsViewModel(long competitionId, String competitionName, List<ApplicationRowViewModel> applications, PaginationViewModel pagination) {
        this.competitionId = competitionId;
        this.competitionName = competitionName;
        this.applications = applications;
        this.pagination = pagination;
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

    public PaginationViewModel getPagination() {
        return pagination;
    }
}
