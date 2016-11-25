package com.worth.ifs.user.resource;


import static com.worth.ifs.util.CollectionFunctions.simpleFindFirst;
import static java.util.Arrays.asList;

/**
 * The disabilities of an User.
 */
public enum Disability {
    YES(1L, "Yes"),
    NO(2L, "No"),
    NOT_STATED(3L, "Prefer not to say");

    private String displayName;
    private Long id;

    Disability(Long id, String displayName) {
        this.displayName = displayName;
        this.id = id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public Long getId() {
        return id;
    }

    public static Disability fromDisplayName(String name) {
        return simpleFindFirst(asList(values()), v -> v.getDisplayName().equals(name)).orElse(null);
    }
}
