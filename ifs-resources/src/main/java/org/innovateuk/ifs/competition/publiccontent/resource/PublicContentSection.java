package org.innovateuk.ifs.competition.publiccontent.resource;

/**
 * Enum to represent the competition setup sections displayed for public content.
 */
public enum PublicContentSection {
    SEARCH("Competition information and search"),
    SUMMARY("Summary"),
    ELIGIBILITY("Eligibility"),
    SCOPE("Scope"),
    DATES("Dates"),
    HOW_TO_APPLY("How to apply"),
    SUPPORTING_INFORMATION("Supporting information");

    private String text;

    PublicContentSection(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

}
