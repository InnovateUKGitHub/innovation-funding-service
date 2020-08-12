package org.innovateuk.ifs.competition.publiccontent.resource;

/**
 * Enum to represent the funding type options displayed in competition public content.
 */
public enum FundingType {
    GRANT("Grant", "Innovate UK"),
    INVESTOR_PARTNERSHIPS("Investor Partnerships", "Investor Partnerships"),
    KTP("Knowledge Transfer Partnership (KTP)", "Knowledge Transfer Partnership (KTP)"),
    LOAN("Loan", "Loans"),
    PROCUREMENT("Procurement", "Procurement");

    private final String displayName;
    private final String defaultTermsName;

    FundingType(String displayName, String defaultTermsName) {
        this.displayName = displayName;
        this.defaultTermsName = defaultTermsName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDefaultTermsName() {
        return defaultTermsName;
    }

}
