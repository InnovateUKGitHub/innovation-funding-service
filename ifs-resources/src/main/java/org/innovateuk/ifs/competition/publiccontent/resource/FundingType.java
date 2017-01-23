package org.innovateuk.ifs.competition.publiccontent.resource;

public enum FundingType {
    GRANT("Grant"),
    PROCUREMENT("Procurement");

    private String text;

    FundingType(String text) {
        this.text = text;
    }
}
