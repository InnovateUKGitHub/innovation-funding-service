package org.innovateuk.ifs.form.resource;

import java.util.Optional;

/**
 * This enum marks sections as a given type.
 */
public enum SectionType {
	FINANCE(Optional.empty()),
	PROJECT_COST_FINANCES(Optional.of(FINANCE)),
    PROJECT_LOCATION(Optional.of(FINANCE)),
	ORGANISATION_FINANCES(Optional.of(FINANCE)),
	FUNDING_FINANCES(Optional.of(FINANCE)),
    OVERVIEW_FINANCES(Optional.empty()),
	GENERAL(Optional.empty());

    private final Optional<SectionType> parent;

    SectionType(Optional<SectionType> parent) {
        this.parent = parent;
    }

    public Optional<SectionType> getParent() {
        return parent;
    }

    public String getNameLower() {
        return this.name().toLowerCase();
    }
}
