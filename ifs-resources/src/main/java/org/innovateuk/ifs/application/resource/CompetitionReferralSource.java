package org.innovateuk.ifs.application.resource;

/**
 * Applications to procurement competitions must have a referral source.  These possibilities are expressed in this enum.
 */
public enum CompetitionReferralSource {
    KNOWLEDGE_TRANSFER_NETWORK_KTN("Knowledge Transfer Network (KTN)"),
    INNOVATE_UK_WEBSITE("Innovate UK website"),
    INNOVATE_UK_DIRECT_CONTACT("Innovate UK direct contact"),
    UK_ACADEMIA_SCIENCE_PARKS_INCUBATOR_HUBS("UK academia, Science Parks, Incubator Hubs"),
    KNOWLEDGE_TRANSFER_PARTNERSHIPS_KTP("Knowledge Transfer Partnerships (KTP)"),
    DEVOLVED_ADMINISTRATIONS_REGIONAL_BODY_LOCAL_ENTERPRISE_PARTNERSHIP_LEP("Devolved administrations, regional body, local enterprise partnership (LEP)"),
    GOV_UK("GOV.UK"),
    ENTERPRISE_EUROPE_NETWORK_EEN("Enterprise Europe Network (EEN)"),
    CATAPULT("Catapult"),
    FINANCE_BANK_BUSINESS_ANGEL_VENTURE_CAPITAL("Finance (bank, business angel, venture capital)"),
    GOVERNMENT_DEPARTMENT_OR_AGENCY("Government department or agency"),
    PROFESSIONAL_BODY_SUCH_AS_CONFEDERATION_OF_BRITISH_INDUSTRY("Professional body (such as Confederation of British Industry)"),
    TRADE_ASSOCIATION("Trade association"),
    PUBLIC_SECTOR_PROCUREMENT_WEBSITE("Public sector procurement website"),
    BUSINESS_CONTACT("Business contact"),
    OTHER("Other");

    private String name;

    CompetitionReferralSource(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
