package org.innovateuk.ifs.project.status.viewmodel;

public enum InternalProjectSetupColumn {
    PROJECT_DETAILS("Project details"),
    PROJECT_TEAM("Project team"),
    DOCUMENTS("Documents"),
    MONITORING_OFFICER("MO"),
    BANK_DETAILS("Bank details"),
    FINANCE_CHECKS("Finance checks"),
    SPEND_PROFILE("Spend profile"),
    GRANT_OFFER_LETTER("GOL");

    private String name;
    private String url;

    InternalProjectSetupColumn(String name) {
        this.name = name;
    }

    InternalProjectSetupColumn(String name, String url) {
        this.name = name;
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
