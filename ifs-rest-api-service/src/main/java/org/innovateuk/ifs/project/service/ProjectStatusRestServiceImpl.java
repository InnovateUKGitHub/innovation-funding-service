package org.innovateuk.ifs.project.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.innovateuk.ifs.project.status.resource.CompetitionProjectsStatusResource;
import org.springframework.stereotype.Service;

@Service
public class ProjectStatusRestServiceImpl extends BaseRestService implements ProjectStatusRestService {
    private static final String competitionURL = "/project/competition";

    @Override
    public RestResult<CompetitionProjectsStatusResource> getCompetitionStatus(Long competitionId) {
        return getWithRestResult(competitionURL + "/" + competitionId, CompetitionProjectsStatusResource.class);
    }
}
