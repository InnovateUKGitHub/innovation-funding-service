package org.innovateuk.ifs.application.resource;

/**
 * Applications to a competition either receive funding or they do not.  These possibilities are expressed in this enum.
 */
public enum Decision {
    FUNDED("Successful"),
    UNFUNDED("Unsuccessful"),
    EOI_APPROVED("Approved"),
    EOI_REJECTED("Rejected"),
    UNDECIDED("-"),
    ON_HOLD("On hold");

    private String name;

    Decision(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
