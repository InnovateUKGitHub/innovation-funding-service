package org.innovateuk.ifs.management.application.viewmodel;

import org.innovateuk.ifs.management.core.viewmodel.PaginationViewModel;

import java.util.List;

/**
 * Base view model for creating Competition Management Applications pages.
 */
abstract class BaseApplicationsViewModel<ApplicationRowViewModel extends BaseApplicationsRowViewModel> {

    protected long competitionId;
    protected String competitionName;
    protected List<ApplicationRowViewModel> applications;
    protected PaginationViewModel pagination;
    protected String sorting;
    protected String filter;

    BaseApplicationsViewModel(long competitionId, String competitionName, List<ApplicationRowViewModel> applications, PaginationViewModel pagination, String sorting, String filter) {
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

    public PaginationViewModel getPagination() {
        return pagination;
    }

    public String getSorting() {
        return sorting;
    }

    public String getFilter() {
        return filter;
    }
}
