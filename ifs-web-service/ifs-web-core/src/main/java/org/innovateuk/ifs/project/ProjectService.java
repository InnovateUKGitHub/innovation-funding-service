package org.innovateuk.ifs.project;

import org.innovateuk.ifs.commons.security.NotSecured;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.project.resource.*;
import org.innovateuk.ifs.user.resource.OrganisationResource;

import java.util.List;
import java.util.Optional;

/**
 * A service for dealing with ProjectResources via the appropriate Rest services
 */
public interface ProjectService {

    @NotSecured(value = "Not currently secured", mustBeSecuredByOtherServices = false)
    List<ProjectUserResource> getProjectUsersForProject(Long projectId);

    @NotSecured(value = "Not currently secured", mustBeSecuredByOtherServices = false)
    List<OrganisationResource> getPartnerOrganisationsForProject(Long projectId);

    @NotSecured(value = "Not currently secured", mustBeSecuredByOtherServices = false)
    ProjectResource getById(Long projectId);

    @NotSecured(value = "Not currently secured", mustBeSecuredByOtherServices = false)
    ProjectResource getByApplicationId(Long applicationId);

    @NotSecured(value = "Not currently secured", mustBeSecuredByOtherServices = false)
    ServiceResult<List<ProjectResource>> findByUser(Long userId);

    @NotSecured(value = "Not currently secured", mustBeSecuredByOtherServices = false)
    OrganisationResource getLeadOrganisation(Long projectId);

    @NotSecured(value = "Not currently secured", mustBeSecuredByOtherServices = false)
    OrganisationResource getOrganisationByProjectAndUser(Long projectId, Long userId);

    @NotSecured(value = "Not currently secured", mustBeSecuredByOtherServices = false)
    boolean isUserLeadPartner(Long projectId, Long userId);

    @NotSecured(value = "Not currently secured", mustBeSecuredByOtherServices = false)
    List<ProjectUserResource> getLeadPartners(Long projectId);

    @NotSecured(value = "Not currently secured", mustBeSecuredByOtherServices = false)
    List<ProjectUserResource> getPartners(Long projectId);

    @NotSecured(value = "Not currently secured", mustBeSecuredByOtherServices = false)
    List<ProjectUserResource> getProjectUsersWithPartnerRole(Long projectId);

    @NotSecured(value = "Not currently secured", mustBeSecuredByOtherServices = false)
    Optional<ProjectUserResource> getProjectManager(Long projectId);

    @NotSecured(value = "Not currently secured", mustBeSecuredByOtherServices = false)
    Boolean isProjectManager(Long userId, Long projectId);

    @NotSecured(value = "Not currently secured", mustBeSecuredByOtherServices = false)
    PartnerOrganisationResource getPartnerOrganisation(Long projectId, Long organisationId);
}
