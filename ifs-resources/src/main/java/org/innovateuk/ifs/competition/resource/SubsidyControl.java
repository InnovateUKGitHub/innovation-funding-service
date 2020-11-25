package org.innovateuk.ifs.competition.resource;

public enum SubsidyControl {

    STATE_AID("State Aid Rules"),
    WTO_RULES("WTO Rules"),
    NOT_AID("Not Aid");

    private String displayName;

    SubsidyControl(String displayName) {
        this.displayName = displayName;
    }
}
