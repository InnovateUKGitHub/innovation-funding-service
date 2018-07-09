package org.innovateuk.ifs.application.service;


import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.organisation.resource.OrganisationSearchResult;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;

import java.util.List;
import java.util.Optional;

/**
 * Interface for CRUD operations on {@link OrganisationResource} related data.
 */
public interface OrganisationService {

    OrganisationResource getOrganisationById(Long organisationId);

    OrganisationResource getOrganisationForUser(Long userId);

    OrganisationResource getOrganisationByIdForAnonymousUserFlow(Long organisationId);

    OrganisationResource createOrMatch(OrganisationResource organisation);

    OrganisationResource createAndLinkByInvite(OrganisationResource organisation, String inviteHash);

    OrganisationResource updateNameAndRegistration(OrganisationResource organisation);

    OrganisationSearchResult getCompanyHouseOrganisation(String organisationId);

    Long getOrganisationType(Long userId, Long applicationId);

    Optional<OrganisationResource> getOrganisationForUser(Long userId, List<ProcessRoleResource> userApplicationRoles);
}
