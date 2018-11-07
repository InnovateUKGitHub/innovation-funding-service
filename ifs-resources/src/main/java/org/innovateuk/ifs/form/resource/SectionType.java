package org.innovateuk.ifs.form.resource;

import org.innovateuk.ifs.organisation.resource.OrganisationTypeEnum;

import java.util.List;
import java.util.Optional;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

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

    public static List<SectionType> sectionsNotRequiredForOrganisationType(Long organisationTypeId) {
        if (OrganisationTypeEnum.getFromId(organisationTypeId).equals(OrganisationTypeEnum.RESEARCH)) {
            return singletonList(FUNDING_FINANCES);
        } else {
            return emptyList();
        }
    }
    public Optional<SectionType> getParent() {
        return parent;
    }

    public String getNameLower() {
        return this.name().toLowerCase();
    }
}
