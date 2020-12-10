package org.innovateuk.ifs.competition.publiccontent.resource;

import static org.innovateuk.ifs.project.grantofferletter.template.resource.GolTemplateResource.DEFAULT_GOL_TEMPLATE;

/**
 * Enum to represent the funding type options displayed in competition public content.
 */
public enum FundingType {

    GRANT("Grant", "Innovate UK"),
    INVESTOR_PARTNERSHIPS("Investor Partnerships", "Investor Partnerships"),
    KTP("Knowledge Transfer Partnership (KTP)", "Knowledge Transfer Partnership (KTP)", "KTP GOL Template"),
    LOAN("Loan", "Loans"),
    PROCUREMENT("Procurement", "Procurement");

    private final String displayName;
    private final String defaultTermsName;
    private final String golType;

    FundingType(String displayName, String defaultTermsName) {
        this(displayName, defaultTermsName, DEFAULT_GOL_TEMPLATE);
    }

    FundingType(String displayName, String defaultTermsName, String golType) {
        this.displayName = displayName;
        this.defaultTermsName = defaultTermsName;
        this.golType = golType;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDefaultTermsName() {
        return defaultTermsName;
    }

    public String getGolType() {
        return golType;
    }
}
