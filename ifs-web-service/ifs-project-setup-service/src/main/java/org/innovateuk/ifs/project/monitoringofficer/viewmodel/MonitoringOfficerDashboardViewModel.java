package org.innovateuk.ifs.project.monitoringofficer.viewmodel;

import java.util.List;

/**
 * Monitoring officer dashboard view model
 */
public class MonitoringOfficerDashboardViewModel {

    private final List<ProjectDashboardRowViewModel> projects;

    public MonitoringOfficerDashboardViewModel(List<ProjectDashboardRowViewModel> projects) {
        this.projects = projects;
    }

    public List<ProjectDashboardRowViewModel> getProjects() {
        return projects;
    }
}
