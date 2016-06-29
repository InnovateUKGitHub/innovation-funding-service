package com.worth.ifs.address.resource;

public enum OrganisationAddressType {
    REGISTERED("registered", 1), OPERATING("operating", 2), // Used for organistaiton creation
    PROJECT("project", 3), ADD_NEW("add_new", 4),           // Used for project address (project setup)
    BANK_DETAILS("bank_details", 5);                        // Used for bank details associated with project

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
