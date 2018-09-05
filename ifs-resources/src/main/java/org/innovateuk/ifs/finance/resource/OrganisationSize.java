package org.innovateuk.ifs.finance.resource;

import org.innovateuk.ifs.identity.IdentifiableEnum;

import java.util.stream.Stream;

/**
 * Reference data that describes the different organisation sizes businesses can apply as.
 */
public enum OrganisationSize implements IdentifiableEnum {

    SMALL(1, "Micro or small"),
    MEDIUM(2, "Medium"),
    LARGE(3, "Large");

    private long id;
    private String description;

    OrganisationSize(long id, String description) {
        this.id = id;
        this.description = description;
    }

    @Override
    public long getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public static OrganisationSize findById(long id) {
        return Stream.of(values())
                .filter(organisationSize -> organisationSize.id == id)
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("No OrganisationSize found for id: " + id));
    }
}
