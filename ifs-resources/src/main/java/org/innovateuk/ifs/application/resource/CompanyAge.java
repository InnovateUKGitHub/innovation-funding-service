package org.innovateuk.ifs.application.resource;

/**
 * Applications to procurement competitions must have a company age.  These possibilities are expressed in this enum.
 */
public enum CompanyAge {
    PRE_START_UP("Pre-start-up"),
    START_UP_ESTABLISHED_FOR_LESS_THAN_A_YEAR("Start-up, established for less than a year"),
    ESTABLISHED_1_TO_5_YEARS("Established 1 to 5 years"),
    ESTABLISHED_5_TO_10_YEARS("Established 5 to 10 years"),
    ESTABLISHED_MORE_THAN_10_YEARS("Established more than 10 years");

    private String name;

    CompanyAge(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
