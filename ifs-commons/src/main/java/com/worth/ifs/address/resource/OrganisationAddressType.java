package com.worth.ifs.address.resource;

/**
 * This enum represents data in address_type entity.
 */
public enum OrganisationAddressType {
    ADD_NEW(0L),                                  // Used for signalling a new address will be added.  Not stored in DB.
    REGISTERED(1L), OPERATING(2L), // Used for organistaiton creation
    PROJECT(3L),                                  // Used for project address (project setup)
    BANK_DETAILS(4L);                        // Used for bank details associated with project

    private final long ordinal;

    OrganisationAddressType(long ordinal) {
        this.ordinal = ordinal;
    }

    public long getOrdinal() {
        return ordinal;
    }
}
