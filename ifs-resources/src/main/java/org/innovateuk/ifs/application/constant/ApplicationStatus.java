package org.innovateuk.ifs.application.constant;


import java.util.Map;
import java.util.TreeMap;

import static org.innovateuk.ifs.util.CollectionFunctions.simpleFindFirst;
import static java.util.Arrays.asList;

/**
 * Java enumeration of the current available Application workflow statuses.
 * The value of these entries are used when saving to the database.
 */
public enum ApplicationStatus {
    CREATED(1L, "created"), // initial state
    SUBMITTED(2L, "submitted"),
    APPROVED(3L, "approved"),
    REJECTED(4L, "rejected"),
    OPEN(5L, "open"); // state after first time opening application.

    private final Long id;
    private final String name;

    private static final Map<Long, ApplicationStatus> lookup = new TreeMap<>();

    static {
        for (ApplicationStatus d : ApplicationStatus.values()) {
            lookup.put(d.getId(), d);
        }
    }

    public static ApplicationStatus getFromId(Long applicationStatusId){
        return lookup.get(applicationStatusId);
    }

    public static ApplicationStatus getFromName(String name){
        return simpleFindFirst(asList(values()), a -> a.getName().equals(name)).get();
    }

    ApplicationStatus(Long id, String name){
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public static Map<Long, ApplicationStatus> applicationStatusMap() {
        return lookup;
    }

    public boolean isApproved() {
        return this == APPROVED;
    }

}
