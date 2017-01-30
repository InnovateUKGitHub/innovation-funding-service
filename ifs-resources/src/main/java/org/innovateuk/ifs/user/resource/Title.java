package org.innovateuk.ifs.user.resource;


import static java.util.Arrays.asList;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleFindFirst;

/**
 * The title of a User.
 */
public enum Title {
    MR(1, "Mr"),
    MISS(2, "Miss"),
    MRS(3, "Mrs"),
    MS(4, "Ms"),
    DR(5, "Dr");

    private final String displayName;
    private final long id;

    Title(long id, String displayName) {
        this.displayName = displayName;
        this.id = id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public Long getId() {
        return id;
    }

    public static Title fromDisplayName(String name) {
        return simpleFindFirst(asList(values()), v -> v.getDisplayName().equals(name)).orElse(null);
    }
}
