package com.worth.ifs.application.constant;


import com.worth.ifs.application.domain.Application;

/**
 * Java enumeration of the current available Application workflow statuses.
 * The value of these entries are used when saving to the database.
 */
public enum ApplicationStatusConstants {
    CREATED(Long.valueOf(1), "created"),
    SUBMITTED(Long.valueOf(2), "submitted"),
    APPROVED(Long.valueOf(3), "approved"),
    REJECTED(Long.valueOf(4), "rejected");

    private final Long id;
    private final String name;

    private ApplicationStatusConstants(Long id, String name){
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
