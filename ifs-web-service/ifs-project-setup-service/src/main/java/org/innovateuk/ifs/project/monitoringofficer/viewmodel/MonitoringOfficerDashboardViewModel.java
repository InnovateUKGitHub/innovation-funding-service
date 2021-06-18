package org.innovateuk.ifs.project.monitoringofficer.viewmodel;

import java.util.List;
import java.util.stream.Collectors;

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

    public int inSetupProjectCount() {
        List<ProjectDashboardRowViewModel> inSetupProjects = projects.stream()
                .filter(project -> !project.getProjectState().isComplete())
                .collect(Collectors.toList());

        return inSetupProjects.size();
    }

    public int previousProjectCount() {
        List<ProjectDashboardRowViewModel> previousProjects = projects.stream()
                .filter(project -> project.getProjectState().isComplete())
                .collect(Collectors.toList());

        return previousProjects.size();
    }

    public int projectCount() {
        return projects.size();
    }
}
