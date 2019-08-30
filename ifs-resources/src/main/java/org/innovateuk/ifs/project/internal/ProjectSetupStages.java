package org.innovateuk.ifs.project.internal;

public enum ProjectSetupStages {
    PROJECT_DETAILS("Project details", "project-details"),
    PROJECT_TEAM("Project team", "project-team"),
    DOCUMENTS("Documents", "documents"),
    MONITORING_OFFICER("MO", "MO"),
    BANK_DETAILS("Bank details", "bank-details"),
    FINANCE_CHECKS("Finance checks", "finance-checks"),
    SPEND_PROFILE("Spend profile", "spend-profile"),
    GRANT_OFFER_LETTER("GOL", "grant-offer-letter");

    private String columnName;
    private String columntype;

    ProjectSetupStages(String columnName, String columntype) {
        this.columnName = columnName;
        this.columntype = columntype;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public String getColumntype() {
        return columntype;
    }

    public void setColumntype(String columntype) {
        this.columntype = columntype;
    }
}
