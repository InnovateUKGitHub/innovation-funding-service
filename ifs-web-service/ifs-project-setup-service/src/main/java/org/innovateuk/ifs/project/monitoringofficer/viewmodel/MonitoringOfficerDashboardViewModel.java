package org.innovateuk.ifs.project.monitoringofficer.viewmodel;

import java.util.List;

/**
 * Monitoring officer dashboard view model
 */
public class MonitoringOfficerDashboardViewModel {

    private final List<ProjectDashboardRowViewModel> projects;
    private final MonitoringOfficerSummaryViewModel monitoringOfficerSummaryView;
    private final boolean isMOJourneyUpdateEnabled;
    private final boolean isMOSpendProfileUpdateEnabled;

    public MonitoringOfficerDashboardViewModel(List<ProjectDashboardRowViewModel> projects,
                                               MonitoringOfficerSummaryViewModel monitoringOfficerSummaryView,
                                               boolean isMOJourneyUpdateEnabled,
                                               boolean isMOSpendProfileUpdateEnabled) {
        this.projects = projects;
        this.monitoringOfficerSummaryView = monitoringOfficerSummaryView;
        this.isMOJourneyUpdateEnabled = isMOJourneyUpdateEnabled;
        this.isMOSpendProfileUpdateEnabled = isMOSpendProfileUpdateEnabled;
    }

    public List<ProjectDashboardRowViewModel> getProjects() {
        return projects;
    }

    public MonitoringOfficerSummaryViewModel getMonitoringOfficerSummaryView() {
        return monitoringOfficerSummaryView;
    }

    public int projectCount() {
        return projects.size();
    }

    public boolean isEmptyResults() {
        return projects.size() == 0;
    }

    public boolean isMOJourneyUpdateEnabled() {
        return isMOJourneyUpdateEnabled;
    }

    public boolean isMOSpendProfileUpdateEnabled() {
        return isMOSpendProfileUpdateEnabled;
    }
}
