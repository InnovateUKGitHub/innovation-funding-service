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

    private final String name;
    private final String description;

    EuOrganisationType(final String name, final String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}