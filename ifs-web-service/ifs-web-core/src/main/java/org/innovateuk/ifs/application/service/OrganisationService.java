package org.innovateuk.ifs.application.service;

import org.innovateuk.ifs.commons.error.exception.ForbiddenActionException;
import org.innovateuk.ifs.commons.security.NotSecured;
import org.innovateuk.ifs.organisation.resource.OrganisationSearchResult;
import org.innovateuk.ifs.user.resource.OrganisationResource;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.resource.UserResource;

import java.util.List;
import java.util.Optional;

/**
 * Interface for CRUD operations on {@link OrganisationResource} related data.
 */
public interface OrganisationService {

    @NotSecured("Not currently secured")
    OrganisationResource getOrganisationById(Long organisationId);

    @NotSecured("Not currently secured")
    OrganisationResource getOrganisationForUser(Long userId);

    @NotSecured("Not currently secured")
    OrganisationResource getOrganisationByIdForAnonymousUserFlow(Long organisationId);

    @NotSecured("Not currently secured")
    OrganisationResource createOrMatch(OrganisationResource organisation);

    @NotSecured("Not currently secured")
    OrganisationResource createAndLinkByInvite(OrganisationResource organisation, String inviteHash);

    @NotSecured("Not currently secured")
    OrganisationResource updateNameAndRegistration(OrganisationResource organisation);

    @NotSecured("Not currently secured")
    OrganisationSearchResult getCompanyHouseOrganisation(String organisationId);

    @NotSecured("Not currently secured")
    Long getOrganisationType(Long userId, Long applicationId);

    @NotSecured("Not currently secured")
    Optional<OrganisationResource> getOrganisationForUser(Long userId, List<ProcessRoleResource> userApplicationRoles);

    @NotSecured("Not currently secured")
    boolean userIsPartnerInOrganisationForProject(Long projectId, Long organisationId, Long userId);

    @NotSecured("Not currently secured")
    Long getOrganisationIdFromUser(Long projectId, UserResource user) throws ForbiddenActionException;
}
