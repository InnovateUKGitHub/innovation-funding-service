package com.worth.ifs.user.resource;

/**
 * organisation size as defined by the EU
 * http://ec.europa.eu/growth/smes/business-friendly-environment/sme-definition/index_en.htm
 */
public enum OrganisationSize {
    SMALL("Micro / small - claim up to 70%", 70),
    MEDIUM("Medium - claim up to 60%", 60),
    LARGE("Large - claim up to 50%", 50);

    private final String name;
    private final int maxGrantClaimPercentage;

    OrganisationSize(String name, int maxGrantClaimPercentage) {
        this.name = name;
        this.maxGrantClaimPercentage = maxGrantClaimPercentage;
    }

    public String getName() {
        return name;
    }

    public int getMaxGrantClaimPercentage() {
        return maxGrantClaimPercentage;
    }
}
