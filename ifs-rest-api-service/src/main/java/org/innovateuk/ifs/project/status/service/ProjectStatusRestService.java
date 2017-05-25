package org.innovateuk.ifs.project.status.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.project.status.resource.ProjectTeamStatusResource;
import org.innovateuk.ifs.project.status.resource.CompetitionProjectsStatusResource;
import org.innovateuk.ifs.project.status.resource.ProjectStatusResource;

import java.util.Optional;

public interface ProjectStatusRestService {

    RestResult<CompetitionProjectsStatusResource> getCompetitionStatus(final Long competitionId);

    RestResult<ProjectTeamStatusResource> getProjectTeamStatus(Long projectId, Optional<Long> filterByUserId);

    RestResult<ProjectStatusResource> getProjectStatus(Long projectId);
}
