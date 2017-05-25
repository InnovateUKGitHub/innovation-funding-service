package org.innovateuk.ifs.project.status.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.innovateuk.ifs.project.status.resource.ProjectTeamStatusResource;
import org.innovateuk.ifs.project.status.resource.CompetitionProjectsStatusResource;
import org.innovateuk.ifs.project.status.resource.ProjectStatusResource;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ProjectStatusRestServiceImpl extends BaseRestService implements ProjectStatusRestService {
    private static final String competitionURL = "/project/competition";
    private static final String projectRestURL = "/project";

    @Override
    public RestResult<CompetitionProjectsStatusResource> getCompetitionStatus(Long competitionId) {
        return getWithRestResult(competitionURL + "/" + competitionId, CompetitionProjectsStatusResource.class);
    }

    @Override
    public RestResult<ProjectTeamStatusResource> getProjectTeamStatus(Long projectId, Optional<Long> filterByUserId){
        return filterByUserId.
                map(userId -> getWithRestResult(projectRestURL + "/" + projectId + "/team-status?filterByUserId=" + userId, ProjectTeamStatusResource.class))
                .orElseGet(() -> getWithRestResult(projectRestURL + "/" + projectId + "/team-status", ProjectTeamStatusResource.class));
    }

    @Override
    public RestResult<ProjectStatusResource> getProjectStatus(Long projectId) {
        return getWithRestResult(projectRestURL + "/" + projectId + "/status", ProjectStatusResource.class);
    }
}
