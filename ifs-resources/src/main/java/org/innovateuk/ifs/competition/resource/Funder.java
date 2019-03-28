package org.innovateuk.ifs.competition.resource;

/**
 * Enum representing the different funders. An enum is in preference to free text to prevent bad data.
 */
public enum Funder {

    // Order is alphabetical, except for "other" categories which appear at the bottom.
    ADVANCED_PROPULSION_CENTRE_APC("Advanced Propulsion Centre (APC)"),
    AEROSPACE_TECHNOLOGY_INSTITUTE_ATI("Aerospace Technology Institute (ATI)"),
    CENTRE_FOR_CONNECTED_AND_AUTONOMOUS_VEHICLES_CCAV("Centre for Connected and Autonomous Vehicles (CCAV)"),
    DEPARTMENT_FOR_BUSINESS_ENERGY_AND_INDUSTRIAL_STRATEGY_BEIS("Department for Business, Energy and Industrial Strategy (BEIS)"),
    DEPARTMENT_FOR_DIGITAL_CULTURE_MEDIA_AND_SPORT_DCMS("Department for Digital, Culture, Media and Sport (DCMS)"),
    EUROPEAN_EUREKA_EUROSTARS_AND_OTHER_EU("European: Eureka, Eurostars and other EU"),
    INDUSTRIAL_STRATEGY_CHALLENGE_FUND_ISCF("Industrial Strategy Challenge Fund (ISCF)"),
    INNOVATE_UK_CORE_BUDGET("Innovate UK core budget"),
    INTEGRATED_DELIVERY_PLATFORM_IDP("Integrated Delivery Platform (IDP)"),
    INTERNATIONAL("International"),
    OFFICE_FOR_LIFE_SCIENCES_OLS("Office for Life Sciences (OLS)"),
    SMALL_BUSINESS_RESEARCH_INITIATIVE_SBRI("Small Business Research Initiative (SBRI)"),
    SMART_OPEN("Smart Open"),
    OTHER_DELIVERY_PARTNERS("Other delivery partners"),
    OTHER_STAKEHOLDERS("Other stakeholders");


    private final String displayName;

    Funder(String displayName){
        this.displayName = displayName;
    }


    public String getDisplayName() {
        return displayName;
    }

    public static Funder fromDisplayName(String displayName) {
        for (Funder funder: Funder.values()) {
            if (funder.getDisplayName().equals(displayName)) {
                return funder;
            }
        }
        return null;
    }
}
