package org.innovateuk.ifs.dashboard.viewmodel;

import java.util.List;

/**
 * Applicant dashboard view model
 */
public class ApplicantDashboardViewModel {

    private final List<ProjectDashboardRowViewModel> projects;
    private final List<InProgressDashboardRowViewModel> inProgress;
    private final List<PreviousDashboardRowViewModel> previous;
    private final String originQuery;
    private final boolean monitoringOfficer;

    public ApplicantDashboardViewModel(List<ProjectDashboardRowViewModel> projects,
                                       List<InProgressDashboardRowViewModel> inProgress,
                                       List<PreviousDashboardRowViewModel> previous,
                                       String originQuery,
                                       boolean monitoringOfficer) {
        this.projects = projects;
        this.inProgress = inProgress;
        this.previous = previous;
        this.originQuery = originQuery;
        this.monitoringOfficer = monitoringOfficer;
    }

    public List<ProjectDashboardRowViewModel> getProjects() {
        return projects;
    }

    public List<InProgressDashboardRowViewModel> getInProgress() {
        return inProgress;
    }

    public List<PreviousDashboardRowViewModel> getPrevious() {
        return previous;
    }

    public String getOriginQuery() {
        return originQuery;
    }

    public boolean isMonitoringOfficer() {
        return monitoringOfficer;
    }

    /* View logic */
    public String getApplicationInProgressText() {

        return inProgress.size() == 1 ?
                "Application in progress" : "Applications in progress";
    }

    public String getProjectSetupContainerText() {
        return monitoringOfficer ? "Projects in setup" : "Set up your project";
    }
}
