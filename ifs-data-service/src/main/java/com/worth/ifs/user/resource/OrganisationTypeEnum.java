package com.worth.ifs.user.resource;

import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

public enum OrganisationTypeEnum {
    BUSINESS(1, false, true, null),
    RESEARCH(2, false, false, null),
    PUBLIC_SECTOR(3, false, false, null),
    CHARITY(4, false, false, null),
    ACADEMIC(5, true, true, RESEARCH),
    RESEARCH_TECHNOLOGY_ORGANISATION(6, false, false, RESEARCH),
    CATAPULT(7, false, false, RESEARCH),
    PUBLIC_SECTOR_RESEARCH_ESTABLISHMENT(8, false, false, RESEARCH),
    RESEARCH_COUNCIL_INSTITUTE(9, false, false, RESEARCH);

    private static final Map<Long, OrganisationTypeEnum> lookup = new TreeMap<>();

    static {
        for (OrganisationTypeEnum d : OrganisationTypeEnum.values()) {
            lookup.put(d.getOrganisationTypeId(), d);
        }
    }

    private final Long organisationTypeId;
    private final boolean restrictOrganisationName; // if true, the user won't be able to enter his organisation name, and should use the search to find his organisation
    private final boolean useOrganisationSearch; // if true there is a search available to search for organisations of this type.
    private final OrganisationTypeEnum parentOrganisationType;

    OrganisationTypeEnum(int organisationTypeId, boolean restrictOrganisationName, boolean useOrganisationSearch, OrganisationTypeEnum parent){
        this.organisationTypeId = Long.valueOf(organisationTypeId);
        this.restrictOrganisationName = restrictOrganisationName;
        this.useOrganisationSearch = useOrganisationSearch;
        this.parentOrganisationType = parent;

        if(this.restrictOrganisationName && !this.useOrganisationSearch){
            throw new IllegalArgumentException("Can't restrict the organisation name, without enabling organisation search.");
        }
    }

    public static OrganisationTypeEnum getFromId(Long organisationTypeId){
        return lookup.get(organisationTypeId);
    }

    public static boolean isResearch(OrganisationTypeEnum organisationType){
        return organisationType.equals(RESEARCH) || (organisationType.getParentOrganisationType() != null && organisationType.getParentOrganisationType().equals(RESEARCH));
    }

    public static boolean isResearch(Long organisationTypeId){
        if(organisationTypeId!=null) {
            return isResearch(getFromId(organisationTypeId));
        } else {
            return false;
        }
    }

    public boolean hasChildren(){
        Optional<OrganisationTypeEnum> child = lookup.values().stream().filter(o -> o.getParentOrganisationType() != null && o.getParentOrganisationType().equals(this)).findAny();
        return child.isPresent();
    }

    public Long getOrganisationTypeId() {
        return organisationTypeId;
    }

    public OrganisationTypeEnum getParentOrganisationType() {
        return parentOrganisationType;
    }

    public boolean isRestrictOrganisationName() {
        return restrictOrganisationName;
    }

    public boolean isUseOrganisationSearch() {
        return useOrganisationSearch;
    }
}
