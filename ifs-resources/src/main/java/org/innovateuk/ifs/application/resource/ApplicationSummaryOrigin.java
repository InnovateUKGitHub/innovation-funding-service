package org.innovateuk.ifs.application.resource;

public enum ApplicationSummaryOrigin {

    PROJECT_SETUP_STATUS("/project-setup/project/{projectId}"),
    APPLICANT_DASHBOARD("/applicant/dashboard");

    private String originUrl;

    ApplicationSummaryOrigin(String originUrl) {
        this.originUrl = originUrl;
    }

    public String getOriginUrl() {
        return originUrl;
    }
}
