package org.innovateuk.ifs.application.resource;

public enum ApplicationSummaryOrigin {

    SET_UP_YOUR_PROJECT("/project-setup/project/{projectId}"),
    APPLICANT_DASHBOARD("/applicant/dashboard");

    private String originUrl;

    ApplicationSummaryOrigin(String originUrl) {
        this.originUrl = originUrl;
    }

    public String getOriginUrl() {
        return originUrl;
    }
}
