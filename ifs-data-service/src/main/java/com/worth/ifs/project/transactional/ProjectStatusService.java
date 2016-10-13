package com.worth.ifs.project.transactional;

import com.worth.ifs.commons.security.SecuredBySpring;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.project.status.resource.CompetitionProjectsStatusResource;
import com.worth.ifs.project.status.resource.ProjectStatusResource;
import org.springframework.security.access.prepost.PreAuthorize;

public interface ProjectStatusService {
    @PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance')")
    @SecuredBySpring(value = "READ_COMPETITION_STATUS", securedType = CompetitionProjectsStatusResource.class,
            description = "Comp Admins and project finance users should be able to access the current status of the competition")
    ServiceResult<CompetitionProjectsStatusResource> getCompetitionStatus(final Long competitionId);

    @PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance')")
    @SecuredBySpring(value = "READ_PROJECT_STATUS", securedType = ProjectStatusResource.class,
            description = "Comp Admins and project finance users should be able to access the current status of the project")
    ServiceResult<ProjectStatusResource> getProjectStatusByProjectId(Long projectId);
}
