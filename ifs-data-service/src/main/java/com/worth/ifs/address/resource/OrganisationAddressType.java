package com.worth.ifs.address.resource;

public enum OrganisationAddressType {
    REGISTERED("registered", 1), OPERATING("operating", 2), // Used for organistaiton creation
    PROJECT("project", 3), ADD_NEW("add_new", 4);           // Used for project address (project setup)

    private final String name;
    private final int ordinal;

    OrganisationAddressType(String name, int ordinal) {
        this.name = name;
        this.ordinal = ordinal;
    }
}
