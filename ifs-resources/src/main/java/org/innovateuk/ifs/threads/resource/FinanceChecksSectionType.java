package org.innovateuk.ifs.threads.resource;

public enum FinanceChecksSectionType {
    ELIGIBILITY("Eligibility"), VIABILITY("Viability"), PAYMENT_MILESTONES("Payment milestones"), FUNDING_RULES("Funding rules");

    FinanceChecksSectionType(String displayName) {
        this.displayName = displayName;
    }

    private String displayName;

    public String getDisplayName() {
        return displayName;
    }
}