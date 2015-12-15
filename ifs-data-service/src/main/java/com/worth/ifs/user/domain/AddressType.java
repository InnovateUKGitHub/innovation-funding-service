package com.worth.ifs.user.domain;

public enum AddressType {
    REGISTERED("registered", 2), OPERATING("operating", 1);

    private final String name;
    private final int ordinal;

    AddressType(String name, int ordinal) {
        this.name = name;
        this.ordinal = ordinal;
    }
}
