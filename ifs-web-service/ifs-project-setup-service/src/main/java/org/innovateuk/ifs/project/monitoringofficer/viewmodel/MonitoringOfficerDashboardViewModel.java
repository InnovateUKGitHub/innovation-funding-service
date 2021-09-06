package org.innovateuk.ifs.project.monitoringofficer.viewmodel;


import org.innovateuk.ifs.project.navigation.Pagination;

import java.util.List;

/**
 * Monitoring officer dashboard view model
 */
public class MonitoringOfficerDashboardViewModel {

    private final List<ProjectDashboardRowViewModel> projects;
    private final MonitoringOfficerSummaryViewModel monitoringOfficerSummaryView;
    private final boolean isMOJourneyUpdateEnabled;
    private final boolean isMOSpendProfileUpdateEnabled;
    private Pagination pagination;

    public MonitoringOfficerDashboardViewModel( List<ProjectDashboardRowViewModel> projects,
                                               MonitoringOfficerSummaryViewModel monitoringOfficerSummaryView,
                                               boolean isMOJourneyUpdateEnabled,
                                               boolean isMOSpendProfileUpdateEnabled,
                                               Pagination pagination) {
        this.projects = projects;
        this.monitoringOfficerSummaryView = monitoringOfficerSummaryView;
        this.isMOJourneyUpdateEnabled = isMOJourneyUpdateEnabled;
        this.isMOSpendProfileUpdateEnabled = isMOSpendProfileUpdateEnabled;
        this.pagination = pagination;
    }

    public List<ProjectDashboardRowViewModel> getProjects() {
        return projects;
    }

    public MonitoringOfficerSummaryViewModel getMonitoringOfficerSummaryView() {
        return monitoringOfficerSummaryView;
    }

    public long projectCount() {
        return pagination.getTotalCount();
    }

    public boolean isEmptyResults() {
        return pagination.getTotalCount() == 0;
    }

    public boolean isMOJourneyUpdateEnabled() {
        return isMOJourneyUpdateEnabled;
    }

    public boolean isMOSpendProfileUpdateEnabled() {
        return isMOSpendProfileUpdateEnabled;
    }

    public Pagination getPagination() {
        return pagination;
    }

    public void setPagination(Pagination pagination) {
        this.pagination = pagination;
    }
}
