package com.worth.ifs.address.resource;

/**
 * This enum represents data in address_type entity.
 */
public enum OrganisationAddressType {
    ADD_NEW(0),                                  // Used for signalling a new address will be added.  Not stored in DB.
    REGISTERED(1), OPERATING(2), // Used for organistaiton creation
    PROJECT(3),                                  // Used for project address (project setup)
    BANK_DETAILS(4);                        // Used for bank details associated with project

    private final int ordinal;

    OrganisationAddressType(int ordinal) {
        this.ordinal = ordinal;
    }

    public int getOrdinal() {
        return ordinal;
    }
}
