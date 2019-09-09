package org.innovateuk.ifs.project.internal;

public enum ProjectSetupStage {
    PROJECT_DETAILS("Project details", "project-details"),
    PROJECT_TEAM("Project team", "project-team"),
    DOCUMENTS("Documents", "documents"),
    MONITORING_OFFICER("MO", "MO"),
    BANK_DETAILS("Bank details", "bank-details"),
    FINANCE_CHECKS("Finance checks", "finance-checks"),
    SPEND_PROFILE("Spend profile", "spend-profile"),
    GRANT_OFFER_LETTER("GOL", "grant-offer-letter");

    public static final int PROJECT_DETAILS_PRIORITY = 1;
    public static final int PROJECT_TEAM_PRIORITY = 2;
    public static final int DOCUMENTS_PRIORITY = 3;
    public static final int MONITORING_OFFICER_PRIORITY = 4;
    public static final int BANK_DETAILS_PRIORITY = 5;
    public static final int FINANCE_CHECKS_PRIORITY = 6;
    public static final int SPEND_PROFILE_PRIORITY = 7;
    public static final int GRANT_OFFER_LETTER_PRIORITY = 8;


    private String columnName;
    private String columnType;

    ProjectSetupStage(String columnName, String columnType) {
        this.columnName = columnName;
        this.columnType = columnType;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public String getColumnType() {
        return columnType;
    }

    public void setColumnType(String columnType) {
        this.columnType = columnType;
    }
}
