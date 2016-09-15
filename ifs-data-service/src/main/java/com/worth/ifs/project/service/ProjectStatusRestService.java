package com.worth.ifs.project.service;

import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.project.resource.CompetitionProjectsStatusResource;

public interface ProjectStatusRestService {
    RestResult<CompetitionProjectsStatusResource> getCompetitionStatus(final Long competitionId);
}
