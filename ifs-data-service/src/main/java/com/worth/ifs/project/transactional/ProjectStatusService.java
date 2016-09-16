package com.worth.ifs.project.transactional;

import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.project.status.resource.CompetitionProjectsStatusResource;
import org.springframework.security.access.prepost.PreAuthorize;

public interface ProjectStatusService {
    @PreAuthorize("hasAuthority('comp_admin') || hasAuthority('project_finance')")
    ServiceResult<CompetitionProjectsStatusResource> getCompetitionStatus(final Long competitionId);
}
