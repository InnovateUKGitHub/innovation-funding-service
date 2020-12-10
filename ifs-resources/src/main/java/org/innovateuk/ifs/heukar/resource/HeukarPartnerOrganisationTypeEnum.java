package org.innovateuk.ifs.heukar.resource;

import static java.util.Arrays.stream;

public enum HeukarPartnerOrganisationTypeEnum {
    BUSINESS(1, "Business", "A person or organisation that provides goods or services in exchange for something of value, usually money."),
    RESEARCH(2, "Research", "Higher education and organisations registered with Je-S."),
    RTO(3, "Research and technology organisation (RTO)", "Organisations which solely promote and conduct collaborative research and innovation."),
    PUBLIC_SECTOR_OR_CHARITY(4, "Public sector, charity or non Je-S registered research organisation", "A not-for-profit organisation focusing on innovation.");

    private final long id;
    private final String name;
    private final String description;

    HeukarPartnerOrganisationTypeEnum(long typeId, String name, String description) {
        this.id = typeId;
        this.name = name;
        this.description = description;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public static HeukarPartnerOrganisationTypeEnum fromId(long typeId){
        return stream(values())
                .filter(type -> type.getId() == typeId)
                .findFirst()
                .orElse(null);
    }
}
