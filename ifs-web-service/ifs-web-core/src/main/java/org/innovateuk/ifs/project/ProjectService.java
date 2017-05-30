package org.innovateuk.ifs.project;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.project.resource.*;
import org.innovateuk.ifs.user.resource.OrganisationResource;

import java.util.List;
import java.util.Optional;

/**
 * A service for dealing with ProjectResources via the appropriate Rest services
 */
public interface ProjectService {

    List<ProjectUserResource> getProjectUsersForProject(Long projectId);

    List<OrganisationResource> getPartnerOrganisationsForProject(Long projectId);

    ProjectResource getById(Long projectId);

    ProjectResource getByApplicationId(Long applicationId);

    ServiceResult<List<ProjectResource>> findByUser(Long userId);

    OrganisationResource getLeadOrganisation(Long projectId);

    OrganisationResource getOrganisationByProjectAndUser(Long projectId, Long userId);

    boolean isUserLeadPartner(Long projectId, Long userId);

    List<ProjectUserResource> getLeadPartners(Long projectId);

    List<ProjectUserResource> getPartners(Long projectId);

    List<ProjectUserResource> getProjectUsersWithPartnerRole(Long projectId);

    Optional<ProjectUserResource> getProjectManager(Long projectId);

    Boolean isProjectManager(Long userId, Long projectId);

    PartnerOrganisationResource getPartnerOrganisation(Long projectId, Long organisationId);
}
