package com.worth.ifs.user.domain;

/**
 * Organisation size as defined by the EU
 * http://ec.europa.eu/growth/smes/business-friendly-environment/sme-definition/index_en.htm
 */
public enum OrganisationSize {
    SMALL("Micro / small"),
    MEDIUM("Medium"),
    LARGE("Large");

    private final String name;

    OrganisationSize(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
