package org.innovateuk.ifs.project.status.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.innovateuk.ifs.project.status.resource.ProjectStatusPageResource;
import org.innovateuk.ifs.project.status.resource.ProjectStatusResource;
import org.innovateuk.ifs.project.status.resource.ProjectTeamStatusResource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static org.innovateuk.ifs.commons.service.ParameterizedTypeReferences.projectStatusResourceListType;

@Service
public class StatusRestServiceImpl extends BaseRestService implements StatusRestService {
    private static final String COMPETITION_URL = "/project/competition";
    private static final String PREVIOUS_COMPETITION_URL = "/project/previous/competition";
    private static final String PROJECT_REST_URL = "/project";

    @Override
    public RestResult<ProjectStatusPageResource> getCompetitionStatus(Long competitionId, String applicationSearchString, int page) {
        return getWithRestResult(COMPETITION_URL + "/" + competitionId + "?applicationSearchString=" + applicationSearchString + "&page=" + page, ProjectStatusPageResource.class);
    }

    @Override
    public RestResult<List<ProjectStatusResource>> getPreviousCompetitionStatus(Long competitionId) {
        return getWithRestResult(PREVIOUS_COMPETITION_URL + "/" + competitionId, projectStatusResourceListType());
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
