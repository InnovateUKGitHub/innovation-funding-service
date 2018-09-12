package org.innovateuk.ifs.project.status.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.innovateuk.ifs.project.status.resource.CompetitionProjectsStatusResource;
import org.innovateuk.ifs.project.status.resource.ProjectStatusResource;
import org.innovateuk.ifs.project.status.resource.ProjectTeamStatusResource;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class StatusRestServiceImpl extends BaseRestService implements StatusRestService {
    private static final String COMPETITION_URL = "/project/competition";
    private static final String PROJECT_REST_URL = "/project";

    @Override
    public RestResult<CompetitionProjectsStatusResource> getCompetitionStatus(Long competitionId, String applicationSearchString) {
        return getWithRestResult(COMPETITION_URL + "/" + competitionId + "?applicationSearchString=" + applicationSearchString, CompetitionProjectsStatusResource.class);
    }

    @Override
    public RestResult<ProjectTeamStatusResource> getProjectTeamStatus(Long projectId, Optional<Long> filterByUserId){
        return filterByUserId.
                map(userId -> getWithRestResult(PROJECT_REST_URL + "/" + projectId + "/team-status?filterByUserId=" + userId, ProjectTeamStatusResource.class))
                .orElseGet(() -> getWithRestResult(PROJECT_REST_URL + "/" + projectId + "/team-status", ProjectTeamStatusResource.class));
    }

    @Override
    public RestResult<ProjectStatusResource> getProjectStatus(Long projectId) {
        return getWithRestResult(PROJECT_REST_URL + "/" + projectId + "/status", ProjectStatusResource.class);
    }
}
