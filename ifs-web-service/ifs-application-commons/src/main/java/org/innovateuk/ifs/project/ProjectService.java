package org.innovateuk.ifs.project;

import org.innovateuk.ifs.commons.exception.ForbiddenActionException;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.resource.ProjectUserResource;
import org.innovateuk.ifs.user.resource.UserResource;

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

    OrganisationResource getLeadOrganisation(Long projectId);

    boolean isUserLeadPartner(Long projectId, Long userId);

    List<ProjectUserResource> getLeadPartners(Long projectId);

    List<ProjectUserResource> getPartners(Long projectId);

    List<ProjectUserResource> getProjectUsersWithPartnerRole(Long projectId);

    Optional<ProjectUserResource> getProjectManager(Long projectId);

    Boolean isProjectManager(Long userId, Long projectId);

    boolean userIsPartnerInOrganisationForProject(Long projectId, Long organisationId, Long userId);

    Long getOrganisationIdFromUser(Long projectId, UserResource user) throws ForbiddenActionException;
}
