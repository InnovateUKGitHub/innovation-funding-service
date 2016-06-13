package com.worth.ifs.address.resource;

public enum AddressType {
    REGISTERED("registered", 2), OPERATING("operating", 1), // Used for organistaiton creation
    PROJECT("project", 3), ADD_NEW("add_new", 4);           // Used for project address (project setup)

    private final String name;
    private final int ordinal;

    AddressType(String name, int ordinal) {
        this.name = name;
        this.ordinal = ordinal;
    }
}
