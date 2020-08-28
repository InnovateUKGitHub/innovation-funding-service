package org.innovateuk.ifs.project.internal;

public enum ProjectSetupStage {
    PROJECT_DETAILS("Project details", 1),
    PROJECT_TEAM("Project team", 2),
    DOCUMENTS("Documents", 3),
    MONITORING_OFFICER("MO", 4),
    BANK_DETAILS("Bank details", 5),
    FINANCE_CHECKS("Finance checks", 6),
    SPEND_PROFILE("Spend profile", 7), //Some competitions don't have this stage. They automatically approve the spend profile.
    GRANT_OFFER_LETTER("GOL", 8),
    PROJECT_SETUP_COMPLETE("Project setup complete", 9);

    private int priority;
    private String shortName;

    ProjectSetupStage(String shortName, int priority) {
        this.shortName = shortName;
        this.priority = priority;
    }

    public int getPriority() {
        return priority;
    }

    public String getShortName() {
        return shortName;
    }
}
