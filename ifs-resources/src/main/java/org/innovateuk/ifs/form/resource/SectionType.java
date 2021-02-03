package org.innovateuk.ifs.form.resource;

import org.innovateuk.ifs.competition.resource.ApplicationConfiguration;
import org.innovateuk.ifs.organisation.resource.OrganisationTypeEnum;

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
    PAYMENT_MILESTONES(FINANCE),
    OVERVIEW_FINANCES,
	GENERAL,
    TERMS_AND_CONDITIONS,
    KTP_ASSESSMENT,
    INITIAL_DETAILS;

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

    public boolean isSectionTypeNotRequiredForOrganisationAndCompetition(ApplicationConfiguration competition, OrganisationTypeEnum organisationType, boolean lead) {
        if (competition.isKtp()) {
            if (lead) {
                return this == ORGANISATION_FINANCES;
            } else {
                return this == PROJECT_COST_FINANCES;
            }
        }
        if (this == SectionType.TERMS_AND_CONDITIONS) {
            return competition.isExpressionOfInterest();
        }
        if (this == SectionType.ORGANISATION_FINANCES) {
            return !competition.applicantShouldSeeYourOrganisationSection(organisationType);
        }
        return this == SectionType.FUNDING_FINANCES && competition.isFullyFunded();
    }
}
