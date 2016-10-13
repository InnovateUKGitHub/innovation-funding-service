package com.worth.ifs.project.transactional;

import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.project.status.resource.CompetitionProjectsStatusResource;
import com.worth.ifs.project.status.resource.ProjectStatusResource;
import org.springframework.security.access.prepost.PreAuthorize;

public interface ProjectStatusService {
    @PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance')")
    ServiceResult<CompetitionProjectsStatusResource> getCompetitionStatus(final Long competitionId);

    @PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance')")
    ServiceResult<ProjectStatusResource> getProjectStatusByProjectId(Long projectId);
}
