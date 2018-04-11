package org.innovateuk.ifs.dashboard.viewmodel;

import static com.google.common.base.Strings.isNullOrEmpty;

/**
 * Applicant dashboard row view model
 */
public class ProjectDashboardRowViewModel extends AbstractApplicantDashboardRowViewModel {

    private final Long projectId;
    private final String projectTitle;

    public ProjectDashboardRowViewModel(String title, Long applicationId, String competitionTitle, Long projectId, String projectTitle) {
        super(title, applicationId, competitionTitle);
        this.projectId = projectId;
        this.projectTitle = projectTitle;
    }

    public Long getProjectId() {
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
}
