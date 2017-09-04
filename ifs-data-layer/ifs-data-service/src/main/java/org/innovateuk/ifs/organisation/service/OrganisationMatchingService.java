package org.innovateuk.ifs.organisation.service;

import org.innovateuk.ifs.commons.security.NotSecured;
import org.innovateuk.ifs.user.domain.Organisation;
import org.innovateuk.ifs.user.resource.OrganisationResource;

import java.util.Optional;

public interface OrganisationMatchingService {
    @NotSecured(value = "Service compares organisation instances and is called form other services always")
    Optional<Organisation> findOrganisationMatch(OrganisationResource organisationResource);
}
