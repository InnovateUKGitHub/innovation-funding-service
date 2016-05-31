package com.worth.ifs.project.transactional;

import com.worth.ifs.application.resource.FundingDecision;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.project.resource.ProjectResource;
import com.worth.ifs.security.SecuredBySpring;
import org.springframework.security.access.method.P;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;
import java.util.Map;

/**
 * Transactional and secure service for Project processing work
 */
public interface ProjectService {
    @PostAuthorize("hasPermission(#projectId, 'com.worth.ifs.project.resource.ProjectResource', 'READ')")
    @SecuredBySpring(value = "READ", securedType = ProjectResource.class, description = "Any users associated with project can see project details")
    ServiceResult<ProjectResource> getProjectById(@P("projectId") final Long projectId);

    @PostFilter("hasPermission(filterObject, 'READ')")
    @SecuredBySpring(value = "FIND_ALL_PROJECTS", securedType = ProjectResource.class, description = "Any users associated with project can see project details for all their projects")
    ServiceResult<List<ProjectResource>> findAll();

    @PreAuthorize("hasAuthority('comp_admin')")
    @SecuredBySpring(value = "UPDATE", securedType = ProjectResource.class, description = "Only comp admin is able to create a project (by making decision)" )
    ServiceResult<ProjectResource> createProjectFromApplication(final Long applicationId);

    @PreAuthorize("hasAuthority('comp_admin')")
    @SecuredBySpring(value = "UPDATE", securedType = ProjectResource.class, description = "Only comp admin is able to create a projects (by making decisions)" )
    ServiceResult<Void> createProjectsFromFundingDecisions(Map<Long, FundingDecision> applicationFundingDecisions);
}
