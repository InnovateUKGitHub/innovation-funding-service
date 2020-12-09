package org.innovateuk.ifs.competition.resource;

public enum FundingRules {

    STATE_AID("State Aid Rules"),
    SUBSIDY_CONTROL("Subsidy Control Rules"),
    NOT_AID("Not Aid");

    private String displayName;

    FundingRules(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
