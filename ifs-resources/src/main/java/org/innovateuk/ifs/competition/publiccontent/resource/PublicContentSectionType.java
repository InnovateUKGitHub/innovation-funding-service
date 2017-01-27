package org.innovateuk.ifs.competition.publiccontent.resource;

/**
 * Enum to represent the competition setup sections displayed for public content.
 */
public enum PublicContentSectionType {
    SEARCH("Competition information and search", "search"),
    SUMMARY("Summary", "summary"),
    ELIGIBILITY("Eligibility", "eligibility"),
    SCOPE("Scope", "scope"),
    DATES("Dates", "dates"),
    HOW_TO_APPLY("How to apply", "how-to-apply"),
    SUPPORTING_INFORMATION("Supporting information", "supporting-information");

    private String text;
    private String path;


    PublicContentSectionType(String text, String path) {
        this.text = text;
        this.path = path;
    }

    public String getText() {
        return text;
    }

    public String getPath() {
        return path;
    }

}
