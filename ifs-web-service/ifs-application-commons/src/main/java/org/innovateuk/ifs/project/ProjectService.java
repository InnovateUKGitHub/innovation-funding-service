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

    List<ProjectUserResource> getProjectUsersForProject(long projectId);

    List<ProjectUserResource> getDisplayProjectUsersForProject(long projectId);

    List<OrganisationResource> getPartnerOrganisationsForProject(long projectId);

    ProjectResource getById(long projectId);

    ProjectResource getByApplicationId(long applicationId);

    OrganisationResource getLeadOrganisation(long projectId);

    boolean isUserLeadPartner(long projectId, long userId);

    List<ProjectUserResource> getLeadPartners(long projectId);

    List<ProjectUserResource> getPartners(long projectId);

    List<ProjectUserResource> getProjectUsersWithPartnerRole(long projectId);

    Optional<ProjectUserResource> getProjectManager(long projectId);

    Boolean isProjectManager(long userId, long projectId);

    boolean userIsPartnerInOrganisationForProject(long projectId, long organisationId, long userId);

    Long getOrganisationIdFromUser(long projectId, UserResource user) throws ForbiddenActionException;
}
