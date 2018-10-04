package org.innovateuk.ifs.origin;

public enum ApplicationSummaryOrigin implements BackLinkOrigin {

    SET_UP_YOUR_PROJECT("/project-setup/project/{projectId}", "Set up your project"),
    APPLICANT_DASHBOARD("/applicant/dashboard", "Dashboard"),
    ASSESSOR_INTERVIEW("/assessment/assessor/dashboard/competition/{competitionId}/interview", "Interview"),
    MANAGE_APPLICATIONS("/assessment/competition/{competitionId}/applications", "Back"),
    APPLICATION_PROGRESS("/assessment/competition/{competitionId}/application/{applicationId}/assessors", "Back"),
    ASSESSOR_PROGRESS("/assessment/competition/{competitionId}/assessors/{assessorId}", "Application overview"),
    COMP_EXEC_INTERVIEW("/management/assessment/interview/competition/{competitionId}/applications/view-status", "Applications"),
    PREVIOUS_APPLICATIONS("/competition/{competitionId}/applications/previous", "Back"),
    ALL_APPLICATIONS("/competition/{competitionId}/applications/all", "All Applications"),
    APPLICATION_SUMMARY("/application/{applicationId}/summary", "Application Overview"),
    SUBMITTED_APPLICATIONS("/competition/{competitionId}/applications/submitted", "Application overview"),
    PROJECT_SETUP_MANAGEMENT_STATUS("/project-setup-management/competition/{competitionId}/status", "Back"),
    INELIGIBLE_APPLICATIONS("/competition/{competitionId}/applications/ineligible", "Application overview"),
    APPLICATION("/application/{applicationId}", "Application overview");

    private String originUrl;
    private String title;

    ApplicationSummaryOrigin(String originUrl, String title) {
        this.originUrl = originUrl;
        this.title = title;
    }

    @Override
    public String getOriginUrl() {
        return originUrl;
    }

    public String getTitle() {
        return title;
    }
}
