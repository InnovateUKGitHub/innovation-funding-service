package org.innovateuk.ifs.organisation.service;

import org.innovateuk.ifs.commons.security.NotSecured;
import org.innovateuk.ifs.organisation.domain.Organisation;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;

import java.util.List;
import java.util.Optional;

public interface OrganisationMatchingService {

    @NotSecured(mustBeSecuredByOtherServices = false, value = "Service compares organisation instances and is called form other services always")
    Optional<Organisation> findOrganisationMatch(OrganisationResource organisationResource);

    @NotSecured(value = "Service compares organisation instances and is called form other services always")
    List<OrganisationResource> findOrganisationsByName(String organisationName);

    @NotSecured(value = "Service compares organisation instances and is called form other services always")
    List<OrganisationResource> findOrganisationsByCompaniesHouseId(String companiesHouseId);
}
