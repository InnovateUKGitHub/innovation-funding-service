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
    private final boolean pendingPartner;
    private final long organisationId;

    public InSetupDashboardRowViewModel(DashboardInSetupRowResource resource) {
        super(resource.getTitle(), resource.getApplicationId(), resource.getCompetitionTitle());
        this.projectId = resource.getProjectId();
        this.projectTitle = resource.getProjectTitle();
        this.targetStartDate = resource.getTargetStartDate();
        this.pendingPartner = resource.isPendingPartner();
        this.organisationId = resource.getOrganisationId();
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

    public boolean isPendingPartner() {
        return pendingPartner;
    }

    @Override
    public String getLinkUrl() {
        return pendingPartner
                ? String.format("/project-setup/project/%s/organisation/%d/pending-partner-progress", projectId, organisationId)
                : String.format("/project-setup/project/%s", projectId);
    }

    @Override
    public String getTitle() {
        return isNullOrEmpty(projectTitle) ? super.getCompetitionTitle() : projectTitle;
    }

}
