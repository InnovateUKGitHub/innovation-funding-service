package org.innovateuk.ifs.user.resource;

/**
 * Enumeration for the possible values of search category whilst searching users.
 */
public enum SearchCategory {
    EMAIL("Email", true),
    NAME("Name", false),
    ORGANISATION_NAME("Organisation name", false);

    private String displayName;

    private boolean selectedByDefault;

    SearchCategory(String displayName, boolean selectedByDefault) {
        this.displayName = displayName;
        this.selectedByDefault = selectedByDefault;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean isSelectedByDefault() {
        return selectedByDefault;
    }
}



