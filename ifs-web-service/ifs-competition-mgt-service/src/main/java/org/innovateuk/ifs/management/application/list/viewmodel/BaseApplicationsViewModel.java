package org.innovateuk.ifs.management.application.list.viewmodel;

import org.innovateuk.ifs.management.navigation.Pagination;

import java.util.List;

/**
 * Base view model for creating Competition Management Applications pages.
 */
abstract class BaseApplicationsViewModel<ApplicationRowViewModel extends BaseApplicationsRowViewModel> {

    protected long competitionId;
    protected String competitionName;
    protected List<ApplicationRowViewModel> applications;
    protected Pagination pagination;
    protected String sorting;
    protected String filter;

    BaseApplicationsViewModel(long competitionId, String competitionName, List<ApplicationRowViewModel> applications, Pagination pagination, String sorting, String filter) {
        this.competitionId = competitionId;
        this.competitionName = competitionName;
        this.applications = applications;
        this.pagination = pagination;
        this.sorting = sorting;
        this.filter = filter;
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

    public Pagination getPagination() {
        return pagination;
    }

    public String getSorting() {
        return sorting;
    }

    public String getFilter() {
        return filter;
    }
}
