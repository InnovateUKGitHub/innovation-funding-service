package org.innovateuk.ifs.project.monitoringofficer.viewmodel;

import org.innovateuk.ifs.project.resource.ProjectState;

import java.util.List;

import static org.innovateuk.ifs.util.CollectionFunctions.negate;

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

    public boolean hasAnyInSetup() {
        return projects.stream().map(ProjectDashboardRowViewModel::getProjectState).anyMatch(negate(ProjectState::isComplete));
    }

    public boolean hasAnyInPrevious() {
        return projects.stream().map(ProjectDashboardRowViewModel::getProjectState).anyMatch(ProjectState::isComplete);
    }
}
