package org.innovateuk.ifs.competition.publiccontent.resource;

/**
 * Enum to represent the funding type options displayed in competition public content.
 */
public enum FundingType {
    GRANT("Grant"),
    PROCUREMENT("Procurement");

    private String text;

    FundingType(String text) {
        this.text = text;
    }
}
