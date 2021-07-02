package org.innovateuk.ifs.project.monitoringofficer.viewmodel;

import java.util.List;

/**
 * Monitoring officer dashboard view model
 */
public class MonitoringOfficerDashboardViewModel {

    private final List<ProjectDashboardRowViewModel> projects;
    private final MonitoringOfficerSummaryViewModel monitoringOfficerSummaryView;

    public MonitoringOfficerDashboardViewModel(List<ProjectDashboardRowViewModel> projects,
                                               MonitoringOfficerSummaryViewModel monitoringOfficerSummaryView) {
        this.projects = projects;
        this.monitoringOfficerSummaryView = monitoringOfficerSummaryView;
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
}
