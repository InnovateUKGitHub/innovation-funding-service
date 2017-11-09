package org.innovateuk.ifs.competition.publiccontent.resource;

/**
 * Enum to represent the funding type options displayed in competition public content.
 */
public enum FundingType {
    GRANT("Grant"),
    LOAN("Loan"),
    PROCUREMENT("Procurement");

    private String displayName;

    FundingType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static FundingType fromDisplayName(String displayName) {
        for(FundingType type: FundingType.values()) {
            if(type.getDisplayName().equals(displayName)) {
                return type;
            }
        }
        return null;
    }
}
