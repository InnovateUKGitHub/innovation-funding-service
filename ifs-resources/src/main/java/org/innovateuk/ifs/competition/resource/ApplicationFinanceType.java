package org.innovateuk.ifs.competition.resource;

public enum ApplicationFinanceType {

    // TODO IFS-4143 Change label to "Stanard"
    STANDARD("Full application finances"),
    STANDARD_WITH_VAT("Standard with V.A.T."),
    // TODO IFS-4143 Change label to "No finance required"
    NO_FINANCES("No finances");

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
