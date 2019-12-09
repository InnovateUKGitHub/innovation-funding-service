package org.innovateuk.ifs.project.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.project.resource.PartnerOrganisationResource;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.resource.ProjectUserResource;

import java.util.List;

public interface ProjectRestService {
    RestResult<ProjectResource> getProjectById(long projectId);

    RestResult<List<ProjectResource>> findByUserId(long userId);

    RestResult<List<ProjectUserResource>> getProjectUsersForProject(long projectId);

    RestResult<ProjectResource> getByApplicationId(long applicationId);

    RestResult<OrganisationResource> getOrganisationByProjectAndUser(long projectId, long userId);

    RestResult<ProjectUserResource> getProjectManager(long projectId);

    RestResult<PartnerOrganisationResource> getPartnerOrganisation(long projectId, long organisationId);

    RestResult<ProjectResource> createProjectFromApplicationId(long applicationId);

    RestResult<OrganisationResource> getLeadOrganisationByProject(long projectId);

    RestResult<Boolean> existsOnApplication(long projectId, long organisationId);
}