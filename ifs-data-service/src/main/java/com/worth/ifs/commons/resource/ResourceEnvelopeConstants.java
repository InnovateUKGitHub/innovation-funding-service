package com.worth.ifs.commons.resource;

public enum ResourceEnvelopeConstants {
    OK(1L, "OK"),
    ERROR(2L, "ERROR");

    private final Long id;
    private final String name;

    ResourceEnvelopeConstants(Long id, String name){
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
