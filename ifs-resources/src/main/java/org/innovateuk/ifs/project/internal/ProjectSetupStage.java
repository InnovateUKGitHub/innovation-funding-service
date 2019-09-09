package org.innovateuk.ifs.project.internal;

public enum ProjectSetupStage {
    PROJECT_DETAILS("Project details", "project-details", 1),
    PROJECT_TEAM("Project team", "project-team", 2),
    DOCUMENTS("Documents", "documents", 3),
    MONITORING_OFFICER("MO", "MO", 4),
    BANK_DETAILS("Bank details", "bank-details", 5),
    FINANCE_CHECKS("Finance checks", "finance-checks", 6),
    SPEND_PROFILE("Spend profile", "spend-profile", 7),
    GRANT_OFFER_LETTER("GOL", "grant-offer-letter", 8);

    private String columnName;
    private String columnType;
    private int priority;

    ProjectSetupStage(String columnName, String columnType, int priority) {
        this.columnName = columnName;
        this.columnType = columnType;
        this.priority = priority;
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

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }
}
