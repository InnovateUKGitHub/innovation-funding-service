package com.worth.ifs.project.service;

import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.commons.service.BaseRestService;
import com.worth.ifs.project.status.resource.CompetitionProjectsStatusResource;
import org.springframework.stereotype.Service;

@Service
public class ProjectStatusRestServiceImpl extends BaseRestService implements ProjectStatusRestService {
    private static final String competitionURL = "/project/competition";

    @Override
    public RestResult<CompetitionProjectsStatusResource> getCompetitionStatus(Long competitionId) {
        return getWithRestResult(competitionURL + "/" + competitionId, CompetitionProjectsStatusResource.class);
    }
}
