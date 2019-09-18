package org.innovateuk.ifs.project.status.transactional;

import org.innovateuk.ifs.commons.security.NotSecured;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.project.core.domain.Project;
import org.innovateuk.ifs.project.status.resource.ProjectStatusResource;
import org.innovateuk.ifs.project.status.resource.ProjectTeamStatusResource;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;
import java.util.Optional;

public interface StatusService {
    @PreAuthorize("hasPermission(#competitionId, 'org.innovateuk.ifs.competition.resource.CompetitionResource', 'VIEW_PROJECT_SETUP_COMPETITION_STATUS')")
    ServiceResult<List<ProjectStatusResource>> getCompetitionStatus(long competitionId, String applicationSearchString);

    @PreAuthorize("hasPermission(#competitionId, 'org.innovateuk.ifs.competition.resource.CompetitionResource', 'VIEW_PROJECT_SETUP_COMPETITION_STATUS')")
    ServiceResult<List<ProjectStatusResource>> getPreviousCompetitionStatus(long competitionId);

    @PreAuthorize("hasPermission(#projectId, 'org.innovateuk.ifs.project.resource.ProjectResource', 'VIEW_PROJECT_STATUS')")
    ServiceResult<ProjectStatusResource> getProjectStatusByProjectId(Long projectId);

    @NotSecured(value = "This Service is only used within a secured service for performing validation checks (update of project manager and address)", mustBeSecuredByOtherServices = true)
    ServiceResult<ProjectStatusResource> getProjectStatusByProject(Project project);

    @PreAuthorize("hasPermission(#projectId, 'org.innovateuk.ifs.project.resource.ProjectResource', 'VIEW_TEAM_STATUS')")
    ServiceResult<ProjectTeamStatusResource> getProjectTeamStatus(Long projectId, Optional<Long> filterByUserId);

}
