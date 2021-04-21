package org.innovateuk.ifs.form.resource;

import org.innovateuk.ifs.competition.resource.ApplicationConfiguration;
import org.innovateuk.ifs.organisation.resource.OrganisationTypeEnum;

/**
 * This enum marks sections as a given type.
 */
public enum SectionType {
    PROJECT_DETAILS,
    APPLICATION_QUESTIONS,
    FINANCES,
	FINANCE, //Your project finances
	PROJECT_COST_FINANCES, //Your project costs
    PROJECT_LOCATION, // Your project location
	ORGANISATION_FINANCES, // Your organisation
	FUNDING_FINANCES, // Your funding
    PAYMENT_MILESTONES, // Your payment milestones
    OVERVIEW_FINANCES, // Finance Overview
	GENERAL,
    TERMS_AND_CONDITIONS,
    KTP_ASSESSMENT,
    FEC_COSTS_FINANCES;

    public String getNameLower() {
        return this.name().toLowerCase();
    }

    public boolean isSectionTypeNotRequiredForOrganisationAndCompetition(ApplicationConfiguration competition, OrganisationTypeEnum organisationType, boolean lead) {
        if (competition.isKtp()) {
            if (lead) {
                return this == ORGANISATION_FINANCES;
            } else {
                return (this == PROJECT_COST_FINANCES || this == FEC_COSTS_FINANCES);
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
