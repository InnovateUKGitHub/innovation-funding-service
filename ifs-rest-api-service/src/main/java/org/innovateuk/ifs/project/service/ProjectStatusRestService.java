package org.innovateuk.ifs.project.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.project.status.resource.CompetitionProjectsStatusResource;

public interface ProjectStatusRestService {
    RestResult<CompetitionProjectsStatusResource> getCompetitionStatus(final Long competitionId);
}
