package org.innovateuk.ifs.project.status;

import org.innovateuk.ifs.project.resource.ProjectTeamStatusResource;
import org.innovateuk.ifs.project.service.ProjectRestService;
import org.innovateuk.ifs.project.status.resource.ProjectStatusResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * A service for dealing with Status section (Setup Status, Team Status and Competition Status) via the appropriate Rest services
 */
@Service
public class StatusServiceImpl implements StatusService {

    @Autowired
    private ProjectRestService projectRestService;

    @Override
    public ProjectTeamStatusResource getProjectTeamStatus(Long projectId, Optional<Long> filterByUserId){
        return projectRestService.getProjectTeamStatus(projectId, filterByUserId).getSuccessObjectOrThrowException();
    }

    @Override
    public ProjectStatusResource getProjectStatus(Long projectId) {
        return projectRestService.getProjectStatus(projectId).getSuccessObjectOrThrowException();
    }
}
