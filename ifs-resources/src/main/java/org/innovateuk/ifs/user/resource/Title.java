package org.innovateuk.ifs.user.resource;


import static java.util.Arrays.asList;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleFindFirst;

/**
 * The title of a User.
 */
public enum Title {
    Mr(1, "Mr"),
    Miss(2, "Miss"),
    Mrs(3, "Mrs"),
    Ms(4, "Ms"),
    Dr(5, "Dr");

    private final String displayName;
    private final long id;

    Title(long id, String displayName) {
        this.displayName = displayName;
        this.id = id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public long getId() {
        return id;
    }

    public static Title fromDisplayName(String name) {
        return simpleFindFirst(asList(values()), v -> v.getDisplayName().equals(name)).orElse(null);
    }
}
