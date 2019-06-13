package org.innovateuk.ifs.project.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.springframework.stereotype.Service;

@Service
public class ProjectStateServiceImpl extends BaseRestService implements ProjectStateRestService {

    private String projectRestURL = "/project";

    @Override
    public RestResult<Void> withdrawProject(long projectId) {
        return postWithRestResult(projectRestURL + "/" + projectId + "/withdraw");
    }

    @Override
    public RestResult<Void> handleProjectOffline(long projectId) {
        return postWithRestResult(projectRestURL + "/" + projectId + "/handle-offline");
    }

    @Override
    public RestResult<Void> completeProjectOffline(long projectId) {
        return postWithRestResult(projectRestURL + "/" + projectId + "/complete-offline");
    }
}
