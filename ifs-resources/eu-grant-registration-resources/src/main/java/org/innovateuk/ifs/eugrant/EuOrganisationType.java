package org.innovateuk.ifs.eugrant;

public enum EuOrganisationType {

    BUSINESS("Business",
             "UK based business."),

    RESEARCH("Research",
             "Higher education and organisations registered with Je-S."),

    RTO("Research and technology organisation (RTO)",
        "Organisations which solely promote and conduct collaborative research and innovation."),

    PUBLIC_SECTOR_OR_CHARITY("Public sector, charity or non Je-S registered research organisation",
                             "A not-for-profit public sector body or charity working on innovation.");

    private final String displayName;
    private final String description;

    EuOrganisationType(final String displayName, final String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    public boolean isResearch() {
        return this == RESEARCH;
    }
}