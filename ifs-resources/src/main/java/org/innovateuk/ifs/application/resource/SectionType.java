package org.innovateuk.ifs.application.resource;

import org.innovateuk.ifs.user.resource.OrganisationTypeEnum;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static java.util.Arrays.asList;

/**
 * This enum marks sections as a given type.
 */
public enum SectionType {
	FINANCE(Optional.empty()),
	PROJECT_COST_FINANCES(Optional.of(FINANCE)),
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
            return asList(ORGANISATION_FINANCES, FUNDING_FINANCES);
        } else {
            return Collections.emptyList();
        }
    }
    public Optional<SectionType> getParent() {
        return parent;
    }

    public String getNameLower() {
        return this.name().toLowerCase();
    }
}
