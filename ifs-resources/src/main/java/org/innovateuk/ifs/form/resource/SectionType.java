package org.innovateuk.ifs.form.resource;

import java.util.Optional;

/**
 * This enum marks sections as a given type.
 */
public enum SectionType {
	FINANCE,
	PROJECT_COST_FINANCES(FINANCE),
    PROJECT_LOCATION(FINANCE),
	ORGANISATION_FINANCES(FINANCE),
	FUNDING_FINANCES(FINANCE),
    OVERVIEW_FINANCES,
	GENERAL,
    TERMS_AND_CONDITIONS,
    KTP_ASSESSMENT;

    private final SectionType parent;

    SectionType() {
        this.parent = null;
    }
    SectionType(SectionType parent) {
        this.parent = parent;
    }

    public Optional<SectionType> getParent() {
        return Optional.ofNullable(parent);
    }

    public String getNameLower() {
        return this.name().toLowerCase();
    }
}
