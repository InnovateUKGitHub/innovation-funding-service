package org.innovateuk.ifs.application.resource;

/**
 * Applications to procurement competitions must have a referral source.  These possibilities are expressed in this enum.
 */
public enum CompanyPrimaryFocus {
    AEROSPACE_AND_DEFENCE("Aerospace and defence"),
    AGRICULTURE_FORESTRY_AND_PAPER("Agriculture, forestry and paper"),
    AUTOMOTIVE_VEHICLES_AND_PARTS("Automotive: vehicles and parts"),
    BANKS_AND_INSURANCE("Banks and insurance"),
    CHEMICALS("Chemicals"),
    CONSTRUCTION_AND_MATERIALS("Construction and materials"),
    ELECTRONICS_AND_ELECTRICAL_EQUIPMENT("Electronics and electrical equipment"),
    FOOD_AND_BEVERAGE("Food and beverage"),
    GENERAL_INDUSTRIAL("General industrial"),
    HEALTHCARE("Healthcare"),
    INDUSTRIAL_AND_GENERAL_ENGINEERING("Industrial and general engineering"),
    INDUSTRIAL_GOODS_AND_SERVICES("Industrial goods and services"),
    INDUSTRIAL_METALS("Industrial metals"),
    MEDIA("Media"),
    MINING("Mining"),
    OIL_AND_GAS("Oil and gas"),
    PERSONAL_AND_HOUSEHOLD_GOODS("Personal and household goods"),
    PHARMACEUTICALS_AND_BIOTECHNOLOGY("Pharmaceuticals and biotechnology"),
    RETAIL("Retail"),
    TECHNOLOGY_HARDWARE_SOFTWARE_AND_SERVICES("Technology: hardware, software and services"),
    TELECOMMUNICATIONS("Telecommunications"),
    TRANSPORTATION("Transportation"),
    TRAVEL_LEISURE_AND_LEISURE_GOODS("Travel, leisure and leisure goods"),
    UTILITIES("Utilities"),
    OTHER("Other");

    private String name;

    CompanyPrimaryFocus(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
