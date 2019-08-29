package org.innovateuk.ifs.project.internal;

public enum ProjectSetupStages {
    PROJECT_DETAILS("Project details"),
    PROJECT_TEAM("Project team"),
    DOCUMENTS("Documents"),
    MONITORING_OFFICER("MO"),
    BANK_DETAILS("Bank details"),
    FINANCE_CHECKS("Finance checks"),
    SPEND_PROFILE("Spend profile"),
    GRANT_OFFER_LETTER("GOL");

    private String columnName;

    ProjectSetupStages(String columnName) {
        this.columnName = columnName;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }
}
