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

    public ApplicantDashboardViewModel(List<ProjectDashboardRowViewModel> projects,
                                       List<InProgressDashboardRowViewModel> inProgress,
                                       List<PreviousDashboardRowViewModel> previous,
                                       String originQuery) {
        this.projects = projects;
        this.inProgress = inProgress;
        this.previous = previous;
        this.originQuery = originQuery;
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

    /* View logic */
    public String getApplicationInProgressText() {

        return inProgress.size() == 1 ?
                "Application in progress" : "Applications in progress";
    }
}
