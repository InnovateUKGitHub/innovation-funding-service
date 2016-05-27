package com.worth.ifs.project.transactional;

import com.worth.ifs.application.resource.FundingDecision;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.project.resource.ProjectResource;
import org.springframework.security.access.method.P;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;
import java.util.Map;

/**
 * Transactional and secure service for Project processing work
 */
public interface ProjectService {
    @PreAuthorize("hasPermission(#projectId, 'com.worth.ifs.project.resource.ProjectResource', 'READ')")
    ServiceResult<ProjectResource> getProjectById(@P("projectId") final Long projectId);

    @PostFilter("hasPermission(filterObject, 'READ')")
    ServiceResult<List<ProjectResource>> findAll();

    @PreAuthorize("hasAuthority('comp_admin')")
    ServiceResult<ProjectResource> createProjectFromApplication(final Long applicationId);

    @PreAuthorize("hasAuthority('comp_admin')")
    ServiceResult<Void> createProjectsFromFundingDecisions(Map<Long, FundingDecision> applicationFundingDecisions);
}
