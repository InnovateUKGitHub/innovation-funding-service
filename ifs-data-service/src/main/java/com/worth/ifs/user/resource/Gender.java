package com.worth.ifs.user.resource;

import com.worth.ifs.user.domain.User;

import static com.worth.ifs.util.CollectionFunctions.simpleFindFirst;
import static java.util.Arrays.asList;

/**
 * The gender of a {@link User}.
 */
public enum Gender {
    FEMALE(1L, "Female"),
    MALE(2L, "Male"),
    NOT_STATED(3L, "Prefer not to say");

    private String displayName;
    private Long id;

    Gender(Long id, String displayName) {
        this.id = id;
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public Long getId() {
        return id;
    }

    public static Gender fromName(String name) {
        return simpleFindFirst(asList(values()), v -> v.displayName.equals(name)).orElse(null);
    }
}
