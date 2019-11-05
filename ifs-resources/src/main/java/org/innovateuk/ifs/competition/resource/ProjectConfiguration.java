package org.innovateuk.ifs.competition.resource;

import org.innovateuk.ifs.organisation.resource.OrganisationTypeEnum;

/**
 * Interface to be shared between the Competition and CompetitionResource to declare methods that define the configuration
 * of a project form.
 */
public interface ProjectConfiguration {

    ApplicationConfiguration getApplicationConfiguration();

    default boolean organisationNotRequiredForViabilityChecks(OrganisationTypeEnum organisationType) {
        return getApplicationConfiguration().isH2020() || getApplicationConfiguration().applicantShouldUseJesFinances(organisationType);
    }
}
