package org.innovateuk.ifs.application.resource;

public enum ApplicationSummaryOrigin {

    SET_UP_YOUR_PROJECT("/project-setup/project/{projectId}"),
    APPLICANT_DASHBOARD("/applicant/dashboard"),
    ASSESSOR_INTERVIEW("/assessment/assessor/dashboard/competition/{competitionId}/interview"),
    COMP_EXEC_INTERVIEW("/management/assessment/interview/competition/{competitionId}/applications/view-status");

    private String originUrl;

    ApplicationSummaryOrigin(String originUrl) {
        this.originUrl = originUrl;
    }

    public String getOriginUrl() {
        return originUrl;
    }
}
