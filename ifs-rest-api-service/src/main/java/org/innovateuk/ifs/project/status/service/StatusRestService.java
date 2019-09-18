package org.innovateuk.ifs.project.status.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.project.status.resource.ProjectStatusResource;
import org.innovateuk.ifs.project.status.resource.ProjectTeamStatusResource;

import java.util.List;
import java.util.Optional;

public interface StatusRestService {

    RestResult<List<ProjectStatusResource>> getCompetitionStatus(final Long competitionId, String applicationSearchString);

    RestResult<List<ProjectStatusResource>> getPreviousCompetitionStatus(final Long competitionId);

    RestResult<ProjectTeamStatusResource> getProjectTeamStatus(Long projectId, Optional<Long> filterByUserId);

    RestResult<ProjectStatusResource> getProjectStatus(Long projectId);
}
