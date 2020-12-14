package org.innovateuk.ifs.competition.resource;

public enum FundingRules {

    SUBSIDY_CONTROL("Subsidy control"),
    STATE_AID("State aid"),
    NOT_AID("Non-aid");

    private String displayName;

    FundingRules(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
