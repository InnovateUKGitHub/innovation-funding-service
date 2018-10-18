package org.innovateuk.ifs.competition.resource;

public enum ApplicationFinanceType {

    STANDARD("Standard"),
    STANDARD_WITH_VAT("Standard with VAT"),
    NO_FINANCES("No finances required");

    private String displayLabel;

    ApplicationFinanceType(String displayLabel) {
        this.displayLabel = displayLabel;
    }

    public String getDisplayLabel() {
        return displayLabel;
    }

    public void setDisplayLabel(String displayLabel) {
        this.displayLabel = displayLabel;
    }
}
