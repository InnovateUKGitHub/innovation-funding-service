package com.worth.ifs.project.status;

import com.worth.ifs.project.resource.CompetitionProjectsStatusResource;

public interface ProjectStatusService {
    CompetitionProjectsStatusResource getCompetitionStatus(final Long competitionId);
}
