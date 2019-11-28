package org.innovateuk.ifs.project.core.transactional;

import org.innovateuk.ifs.application.resource.FundingDecision;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.project.core.domain.ProjectUser;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.resource.ProjectUserResource;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;
import java.util.Map;

/**
 * Transactional and secure service for Project processing work
 */
public interface ProjectService {

    @PostAuthorize("hasPermission(returnObject, 'READ')")
    ServiceResult<ProjectResource> getProjectById(long projectId);

    @PostAuthorize("hasPermission(returnObject, 'READ')")
    ServiceResult<ProjectResource> getByApplicationId(long applicationId);

    @PostFilter("hasPermission(filterObject, 'READ')")
    ServiceResult<List<ProjectResource>> findAll();

    @PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance')")
    @SecuredBySpring(value = "UPDATE", securedType = ProjectResource.class, description = "Only comp admin and project finance user are able to create a project (by making decision)" )
    ServiceResult<ProjectResource> createProjectFromApplication(long applicationId);

    @PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance')")
    @SecuredBySpring(value = "UPDATE", securedType = ProjectResource.class, description = "Only comp admin and project finance user are able to create projects (by making decisions)" )
    ServiceResult<Void> createProjectsFromFundingDecisions(Map<Long, FundingDecision> applicationFundingDecisions);

    @PostFilter("hasPermission(filterObject, 'READ')")
    ServiceResult<List<ProjectResource>> findByUserId(long userId);

    @PreAuthorize("hasPermission(#projectId, 'org.innovateuk.ifs.project.resource.ProjectResource', 'READ')")
    ServiceResult<List<ProjectUserResource>> getProjectUsers(long projectId);

 	@PostAuthorize("hasPermission(returnObject, 'READ')")
    ServiceResult<OrganisationResource> getOrganisationByProjectAndUser(long projectId, long userId);

    @PreAuthorize("hasPermission(#projectId, 'org.innovateuk.ifs.project.resource.ProjectResource', 'ADD_PARTNER')")
    ServiceResult<ProjectUser> addPartner(long projectId, long userId, long organisationId);

    @PostAuthorize("hasPermission(returnObject, 'READ')")
    ServiceResult<OrganisationResource> getLeadOrganisation(long projectId);

    @PreAuthorize("hasPermission(#projectId, 'org.innovateuk.ifs.project.resource.ProjectResource', 'READ')")
    ServiceResult<Boolean> existsOnApplication(long projectId, long organisationId);
}
