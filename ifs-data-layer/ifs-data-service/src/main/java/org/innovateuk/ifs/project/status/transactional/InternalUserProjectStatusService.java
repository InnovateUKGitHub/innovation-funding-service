package org.innovateuk.ifs.project.status.transactional;

import org.innovateuk.ifs.commons.security.NotSecured;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.project.core.domain.Project;
import org.innovateuk.ifs.project.status.resource.ProjectStatusPageResource;
import org.innovateuk.ifs.project.status.resource.ProjectStatusResource;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

public interface InternalUserProjectStatusService {
    @PreAuthorize("hasPermission(#competitionId, 'org.innovateuk.ifs.competition.resource.CompetitionResource', 'VIEW_PROJECT_SETUP_COMPETITION_STATUS')")
    ServiceResult<ProjectStatusPageResource> getCompetitionStatus(long competitionId, String applicationSearchString, int page, int size);

    @PreAuthorize("hasPermission(#competitionId, 'org.innovateuk.ifs.competition.resource.CompetitionResource', 'VIEW_PROJECT_SETUP_COMPETITION_STATUS')")
    ServiceResult<List<ProjectStatusResource>> getPreviousCompetitionStatus(long competitionId);

    @NotSecured(value = "This Service is only used within a secured service for performing validation checks (update of project manager and address)", mustBeSecuredByOtherServices = true)
    ServiceResult<ProjectStatusResource> getProjectStatusByProject(Project project);

    @PreAuthorize("hasPermission(#projectId, 'org.innovateuk.ifs.project.resource.ProjectResource', 'VIEW_PROJECT_STATUS')")
    ServiceResult<ProjectStatusResource> getProjectStatusByProjectId(Long projectId);
}
