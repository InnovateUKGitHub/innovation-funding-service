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

    @NotSecured("Not currently secured")
    List<ProjectUserResource> getProjectUsersForProject(Long projectId);

    @NotSecured("Not currently secured")
    List<OrganisationResource> getPartnerOrganisationsForProject(Long projectId);

    @NotSecured("Not currently secured")
    ProjectResource getById(Long projectId);

    @NotSecured("Not currently secured")
    ProjectResource getByApplicationId(Long applicationId);

    @NotSecured("Not currently secured")
    ServiceResult<List<ProjectResource>> findByUser(Long userId);

    @NotSecured("Not currently secured")
    OrganisationResource getLeadOrganisation(Long projectId);

    @NotSecured("Not currently secured")
    OrganisationResource getOrganisationByProjectAndUser(Long projectId, Long userId);

    @NotSecured("Not currently secured")
    boolean isUserLeadPartner(Long projectId, Long userId);

    @NotSecured("Not currently secured")
    List<ProjectUserResource> getLeadPartners(Long projectId);

    @NotSecured("Not currently secured")
    List<ProjectUserResource> getPartners(Long projectId);

    @NotSecured("Not currently secured")
    List<ProjectUserResource> getProjectUsersWithPartnerRole(Long projectId);

    @NotSecured("Not currently secured")
    Optional<ProjectUserResource> getProjectManager(Long projectId);

    @NotSecured("Not currently secured")
    Boolean isProjectManager(Long userId, Long projectId);

    @NotSecured("Not currently secured")
    PartnerOrganisationResource getPartnerOrganisation(Long projectId, Long organisationId);
}
