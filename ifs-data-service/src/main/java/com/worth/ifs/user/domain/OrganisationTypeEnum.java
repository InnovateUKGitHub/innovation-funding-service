package com.worth.ifs.user.domain;

import java.util.Map;
import java.util.TreeMap;

public enum OrganisationTypeEnum {
    BUSINESS(1, null),
    RESEARCH(2, null),
    PUBLIC_SECTOR(3, null),
    CHARITY(4, null),
    ACADEMIC(5, RESEARCH),
    RESEARCH_TECHNOLOGY_ORGANISATION(6, RESEARCH),
    CATAPULT(7, RESEARCH),
    PUBLIC_SECTOR_RESEARCH_ESTABLISHMENT(8, RESEARCH),
    RESEARCH_COUNCIL_INSTITUTE(9, RESEARCH);

    private static final Map<Long, OrganisationTypeEnum> lookup = new TreeMap<>();

    static {
        for (OrganisationTypeEnum d : OrganisationTypeEnum.values()) {
            lookup.put(d.getOrganisationTypeId(), d);
        }
    }

    private final Long organisationTypeId;
    private final OrganisationTypeEnum parentOrganisationType;

    OrganisationTypeEnum(int organisationTypeId, OrganisationTypeEnum parent) {
        this.organisationTypeId = Long.valueOf(organisationTypeId);
        this.parentOrganisationType = parent;
    }

    public static OrganisationTypeEnum getFromId(Long organisationTypeId){
        return lookup.get(organisationTypeId);
    }
    public static boolean isResearch(OrganisationTypeEnum organisationType){
        return organisationType.equals(RESEARCH) || (organisationType.getParentOrganisationType() != null && organisationType.getParentOrganisationType().equals(RESEARCH));
    }

    public static boolean isResearch(OrganisationType organisationType){
        if(organisationType!=null) {
            return isResearch(getFromId(organisationType.getId()));
        } else {
            return false;
        }
    }

    public Long getOrganisationTypeId() {
        return organisationTypeId;
    }

    public OrganisationTypeEnum getParentOrganisationType() {
        return parentOrganisationType;
    }
}
