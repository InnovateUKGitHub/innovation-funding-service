package org.innovateuk.ifs.dashboard.viewmodel;

import static com.google.common.base.Strings.isNullOrEmpty;

/**
 * View model for each project row in the 'Project' section of the applicant dashboard.
 */
public class InSetupDashboardRowViewModel extends AbstractApplicantDashboardRowViewModel<InSetupDashboardRowViewModel> {

    private final long projectId;
    private final String projectTitle;

    public InSetupDashboardRowViewModel(String title,
                                        long applicationId,
                                        String competitionTitle,
                                        long projectId,
                                        String projectTitle) {
        super(title, applicationId, competitionTitle);
        this.projectId = projectId;
        this.projectTitle = projectTitle;
    }

    public long getProjectId() {
        return projectId;
    }

    public String getProjectTitle() {
        return projectTitle;
    }

    @Override
    public String getLinkUrl() {
        return String.format("/project-setup/project/%s", projectId);
    }

    @Override
    public String getTitle() {
        return isNullOrEmpty(projectTitle) ? super.getCompetitionTitle() : projectTitle;
    }

    @Override
    public int compareTo(InSetupDashboardRowViewModel o) {
        return Long.compare(projectId, o.getProjectId());
    }
}
