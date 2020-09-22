package org.innovateuk.ifs.organisation.resource;

import java.util.EnumSet;

import static org.innovateuk.ifs.util.CollectionFunctions.simpleFindFirst;

public enum OrganisationTypeEnum {
    BUSINESS(1),
    RESEARCH(2),
    RTO(3),
    PUBLIC_SECTOR_OR_CHARITY(4),
    KNOWLEDGE_BASE(5);

    private static final EnumSet<OrganisationTypeEnum> researchParticipationTypes =
            EnumSet.of(RESEARCH, RTO, PUBLIC_SECTOR_OR_CHARITY);

    private static final EnumSet<OrganisationTypeEnum> ktpCollaboratorTypes =
            EnumSet.of(BUSINESS, PUBLIC_SECTOR_OR_CHARITY);

    private final long id;

    OrganisationTypeEnum(long organisationTypeId) {
        this.id = organisationTypeId;
    }

    public long getId() {
        return id;
    }

    public static OrganisationTypeEnum getFromId(long organisationTypeId) {
        return simpleFindFirst(values(), v -> v.id == organisationTypeId).orElse(null);
    }

    public static boolean isResearch(OrganisationTypeEnum organisationType) {
        return organisationType.equals(RESEARCH);
    }

    public static boolean isResearch(long organisationTypeId) {
        return isResearch(getFromId(organisationTypeId));
    }

    public static boolean isKnowledgeBase(OrganisationTypeEnum organisationType) {
        return organisationType.equals(KNOWLEDGE_BASE);
    }

    public static boolean isKnowledgeBase(long organisationTypeId) {
        return isKnowledgeBase(getFromId(organisationTypeId));
    }

    public static boolean isResearchParticipationType(OrganisationTypeEnum organisationType) {
        return researchParticipationTypes.contains(organisationType);
    }

    public static boolean isResearchParticipationOrganisation(long organisationTypeId) {
        return isResearchParticipationType(getFromId(organisationTypeId));
    }

    public static boolean isKtpCollaborator(OrganisationTypeEnum organisationType) {
        return ktpCollaboratorTypes.contains(organisationType);
    }

    public static boolean isValidKtpCollaborator(long organisationTypeId) {
        return isKtpCollaborator(getFromId(organisationTypeId));
    }

    public static boolean isValidCollaborator(long organisationTypeId) {
        return !getFromId(organisationTypeId).equals(KNOWLEDGE_BASE);
    }
}