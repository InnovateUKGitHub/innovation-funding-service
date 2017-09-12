package org.innovateuk.ifs.user.resource;

import java.util.EnumSet;

import static java.util.Arrays.stream;

public enum OrganisationTypeEnum {
    BUSINESS(1),
    RESEARCH(2),
    RTO(3),
    PUBLICSECTOR_OR_CHARITY(4);
    
    private static final EnumSet<OrganisationTypeEnum> researchParticipationTypes = EnumSet.of(RESEARCH, RTO, PUBLICSECTOR_OR_CHARITY);

    private final Long id;

    OrganisationTypeEnum(int organisationTypeId){
        this.id = Long.valueOf(organisationTypeId);
    }

    public Long getId() {
        return id;
    }

    public static OrganisationTypeEnum getFromId(Long organisationTypeId){
        return stream(values()).filter(value -> value.getId().equals(organisationTypeId))
                .findFirst()
                .orElse(null);
    }

    public static boolean isResearch(OrganisationTypeEnum organisationType){
        return organisationType.equals(RESEARCH);
    }

    public static boolean isResearch(Long organisationTypeId){
        if(organisationTypeId != null) {
            OrganisationTypeEnum organisationType = getFromId(organisationTypeId);
            return isResearch(organisationType);
        } else {
            return false;
        }
    }

    public static boolean isResearchParticipationType(OrganisationTypeEnum organisationType) {
        return researchParticipationTypes.contains(organisationType);
    }

    public static boolean isResearchParticipationOrganisation(Long organisationTypeId) {
        if(organisationTypeId != null) {
            OrganisationTypeEnum type = getFromId(organisationTypeId);
            return isResearchParticipationType(type);
        } else {
            return false;
        }
    }

}
