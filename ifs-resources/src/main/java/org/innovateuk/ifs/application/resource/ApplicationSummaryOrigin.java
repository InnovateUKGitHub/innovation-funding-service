package org.innovateuk.ifs.application.resource;

public enum ApplicationSummaryOrigin {

    SET_UP_YOUR_PROJECT("/project-setup/project/{projectId}", "Set up your project"),
    APPLICANT_DASHBOARD("/applicant/dashboard", "Dashboard"),
    ASSESSOR_INTERVIEW("/assessment/assessor/dashboard/competition/{competitionId}/interview", "Interview"),
    COMP_EXEC_INTERVIEW("/management/assessment/interview/competition/{competitionId}/applications/view-status", "Applications");

    private String originUrl;
    private String title;

    ApplicationSummaryOrigin(String originUrl, String title) {
        this.originUrl = originUrl;
        this.title = title;
    }

    public String getOriginUrl() {
        return originUrl;
    }

    public String getTitle() {
        return title;
    }
}
