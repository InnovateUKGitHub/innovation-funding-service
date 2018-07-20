package org.innovateuk.ifs.finance.resource;

public enum FundingLevel {

    TEN(10, "10%"),
    TWENTY(20, "20%"),
    THIRTY(30, "30%"),
    FOURTY(40, "40%"),
    FIFTY(50, "50%"),
    SIXTY(60, "60%"),
    SEVENTY(70, "70%"),
    EIGHTY(80, "80%"),
    NINETY(90, "90%"),
    HUNDRED(100, "100%");

    private int percentage;
    private String textValue;

    FundingLevel(int percentage, String textValue) {
        this.percentage = percentage;
        this.textValue = textValue;
    }

    public int getPercentage() {
        return percentage;
    }

    public String getTextValue() {
        return textValue;
    }
}
