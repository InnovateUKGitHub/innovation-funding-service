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

    @NotSecured(value = "Not currently secured", mustBeSecuredByOtherServices = false)
    OrganisationResource getOrganisationById(Long organisationId);

    @NotSecured(value = "Not currently secured", mustBeSecuredByOtherServices = false)
    OrganisationResource getOrganisationForUser(Long userId);

    @NotSecured(value = "Not currently secured", mustBeSecuredByOtherServices = false)
    OrganisationResource getOrganisationByIdForAnonymousUserFlow(Long organisationId);

    @NotSecured(value = "Not currently secured", mustBeSecuredByOtherServices = false)
    OrganisationResource createOrMatch(OrganisationResource organisation);

    @NotSecured(value = "Not currently secured", mustBeSecuredByOtherServices = false)
    OrganisationResource createAndLinkByInvite(OrganisationResource organisation, String inviteHash);

    @NotSecured(value = "Not currently secured", mustBeSecuredByOtherServices = false)
    OrganisationResource updateNameAndRegistration(OrganisationResource organisation);

    @NotSecured(value = "Not currently secured", mustBeSecuredByOtherServices = false)
    OrganisationSearchResult getCompanyHouseOrganisation(String organisationId);

    @NotSecured(value = "Not currently secured", mustBeSecuredByOtherServices = false)
    Long getOrganisationType(Long userId, Long applicationId);

    @NotSecured(value = "Not currently secured", mustBeSecuredByOtherServices = false)
    Optional<OrganisationResource> getOrganisationForUser(Long userId, List<ProcessRoleResource> userApplicationRoles);

    @NotSecured(value = "Not currently secured", mustBeSecuredByOtherServices = false)
    boolean userIsPartnerInOrganisationForProject(Long projectId, Long organisationId, Long userId);

    @NotSecured(value = "Not currently secured", mustBeSecuredByOtherServices = false)
    Long getOrganisationIdFromUser(Long projectId, UserResource user) throws ForbiddenActionException;
}
