package org.innovateuk.ifs.project.status.transactional;

import org.innovateuk.ifs.commons.security.NotSecured;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.project.domain.Project;
import org.innovateuk.ifs.project.status.resource.CompetitionProjectsStatusResource;
import org.innovateuk.ifs.project.status.resource.ProjectStatusResource;
import org.innovateuk.ifs.project.status.resource.ProjectTeamStatusResource;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.Optional;

public interface StatusService {
    @PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance')")
    @SecuredBySpring(value = "READ_COMPETITION_STATUS", securedType = CompetitionProjectsStatusResource.class,
            description = "Comp Admins and project finance users should be able to access the current status of the competition")
    ServiceResult<CompetitionProjectsStatusResource> getCompetitionStatus(final Long competitionId);

    @PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance')")
    @SecuredBySpring(value = "READ_PROJECT_STATUS", securedType = ProjectStatusResource.class,
            description = "Comp Admins and project finance users should be able to access the current status of the project")
    ServiceResult<ProjectStatusResource> getProjectStatusByProjectId(Long projectId);

    @NotSecured(value = "This Service is only used within a secured service for performing validation checks (update of project manager and address)", mustBeSecuredByOtherServices = true)
    ServiceResult<ProjectStatusResource> getProjectStatusByProject(Project project);

    @PreAuthorize("hasPermission(#projectId, 'org.innovateuk.ifs.project.resource.ProjectResource', 'VIEW_TEAM_STATUS')")
    ServiceResult<ProjectTeamStatusResource> getProjectTeamStatus(Long projectId, Optional<Long> filterByUserId);
}
