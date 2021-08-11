package org.innovateuk.ifs.project.monitoringofficer.viewmodel;

/**
 * Monitoring officer dashboard view model for Documents section
 */
public class MonitoringOfficerDashboardSpendProfileSectionViewModel {

    private final String spendProfileStatus;
    private final boolean hasSpendProfileSection;
    private final long projectId;
    private final boolean hasSpendProfileForReview;

    public MonitoringOfficerDashboardSpendProfileSectionViewModel(String spendProfileStatus, boolean hasSpendProfileSection, long projectId, boolean hasSpendProfileForReview) {
        this.spendProfileStatus = spendProfileStatus;
        this.hasSpendProfileSection = hasSpendProfileSection;
        this.projectId = projectId;
        this.hasSpendProfileForReview = hasSpendProfileForReview;
    }

    public String getSpendProfileStatus() {
        return spendProfileStatus;
    }

    public boolean isHasSpendProfileSection() {
        return hasSpendProfileSection;
    }

    public long getProjectId() {
        return projectId;
    }

    public boolean isHasSpendProfileForReview() {
        return hasSpendProfileForReview;
    }

    public String getSpendProfileLinkUrl() {
        return String.format("/project-setup/project/%s", projectId);
    }
}
