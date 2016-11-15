package com.worth.ifs.application.constant;


import java.util.Map;
import java.util.TreeMap;

/**
 * Java enumeration of the current available com.worth.ifs.Application workflow statuses.
 * The value of these entries are used when saving to the database.
 */
public enum ApplicationStatusConstants {
    CREATED(1L, "created"), // initial state
    SUBMITTED(2L, "submitted"),
    APPROVED(3L, "approved"),
    REJECTED(4L, "rejected"),
    OPEN(5L, "open"); // state after first time opening application.

    private final Long id;
    private final String name;

    private static final Map<Long, ApplicationStatusConstants> lookup = new TreeMap<>();

    static {
        for (ApplicationStatusConstants d : ApplicationStatusConstants.values()) {
            lookup.put(d.getId(), d);
        }
    }
    public static ApplicationStatusConstants getFromId(Long applicationStatusId){
        return lookup.get(applicationStatusId);
    }

    ApplicationStatusConstants(Long id, String name){
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
