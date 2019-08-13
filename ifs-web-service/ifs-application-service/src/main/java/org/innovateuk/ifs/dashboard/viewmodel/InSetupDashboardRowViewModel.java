package org.innovateuk.ifs.dashboard.viewmodel;

import org.innovateuk.ifs.applicant.resource.dashboard.DashboardInSetupRowResource;

import java.time.LocalDate;

import static com.google.common.base.Strings.isNullOrEmpty;

/**
 * View model for each project row in the 'Project' section of the applicant dashboard.
 */
public class InSetupDashboardRowViewModel extends AbstractApplicantDashboardRowViewModel {

    private final long projectId;
    private final String projectTitle;
    private final LocalDate targetStartDate;

    public InSetupDashboardRowViewModel(String title,
                                        long applicationId,
                                        String competitionTitle,
                                        long projectId,
                                        String projectTitle,
                                        LocalDate targetStartDate) {
        super(title, applicationId, competitionTitle);
        this.projectId = projectId;
        this.projectTitle = projectTitle;
        this.targetStartDate = targetStartDate;
    }

    public InSetupDashboardRowViewModel(DashboardInSetupRowResource resource) {
        super(resource.getTitle(), resource.getApplicationId(), resource.getCompetitionTitle());
        this.projectId = resource.getProjectId();
        this.projectTitle = resource.getProjectTitle();
        this.targetStartDate = resource.getTargetStartDate();
    }

    public long getProjectId() {
        return projectId;
    }

    public String getProjectTitle() {
        return projectTitle;
    }

    public LocalDate getTargetStartDate() {
        return targetStartDate;
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
