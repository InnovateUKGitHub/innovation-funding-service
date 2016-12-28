package org.innovateuk.ifs.project.status;

import org.innovateuk.ifs.project.status.resource.CompetitionProjectsStatusResource;

public interface ProjectStatusService {
    CompetitionProjectsStatusResource getCompetitionStatus(final Long competitionId);
}
