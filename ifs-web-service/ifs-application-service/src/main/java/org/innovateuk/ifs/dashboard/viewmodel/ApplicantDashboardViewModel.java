package org.innovateuk.ifs.dashboard.viewmodel;

import java.util.List;

/**
 * Applicant dashboard view model
 */
public class ApplicantDashboardViewModel {

    private final List<ProjectDashboardRowViewModel> projects;
    private final List<InProgressDashboardRowViewModel> inProgress;
    private final List<PreviousDashboardRowViewModel> previous;

    public ApplicantDashboardViewModel(List<ProjectDashboardRowViewModel> projects,
                                       List<InProgressDashboardRowViewModel> inProgress,
                                       List<PreviousDashboardRowViewModel> previous) {
        this.projects = projects;
        this.inProgress = inProgress;
        this.previous = previous;
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

    /* View logic */
    public String getApplicationInProgressText() {

        return inProgress.size() == 1 ?
                "Application in progress" : "Applications in progress";
    }
}
