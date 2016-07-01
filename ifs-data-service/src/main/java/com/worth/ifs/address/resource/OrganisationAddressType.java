package com.worth.ifs.address.resource;

/**
 * This enum represents data in address_type entity.
 */
public enum OrganisationAddressType {
    ADD_NEW("add_new", 0),                                  // Used for signalling a new address will be added.  Not stored in DB.
    REGISTERED("registered", 1), OPERATING("operating", 2), // Used for organistaiton creation
    PROJECT("project", 3),                                  // Used for project address (project setup)
    BANK_DETAILS("bank_details", 4);                        // Used for bank details associated with project

    private final String name;
    private final int ordinal;

    OrganisationAddressType(String name, int ordinal) {
        this.name = name;
        this.ordinal = ordinal;
    }

    public int getOrdinal() {
        return ordinal;
    }
}
