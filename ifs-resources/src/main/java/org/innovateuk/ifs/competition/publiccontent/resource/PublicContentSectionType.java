package org.innovateuk.ifs.competition.publiccontent.resource;

/**
 * Enum to represent the competition setup sections displayed for public content.
 */
public enum PublicContentSectionType {
    SEARCH("Competition information and search", "search", true),
    SUMMARY("Summary", "summary" , true),
    ELIGIBILITY("Eligibility", "eligibility", false),
    SCOPE("Scope", "scope", false),
    DATES("Dates", "dates", true),
    HOW_TO_APPLY("How to apply", "how-to-apply", false),
    SUPPORTING_INFORMATION("Supporting information", "supporting-information", false);

    private String text;
    private String path;
    private boolean allowEmptyContentGroups;


    PublicContentSectionType(String text, String path, boolean allowEmptyContentGroups) {
        this.text = text;
        this.path = path;
        this.allowEmptyContentGroups = allowEmptyContentGroups;
    }

    public String getText() {
        return text;
    }

    public String getPath() {
        return path;
    }

    public boolean isAllowEmptyContentGroups() {
        return allowEmptyContentGroups;
    }
}
