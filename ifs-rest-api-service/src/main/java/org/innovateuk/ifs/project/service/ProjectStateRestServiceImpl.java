package org.innovateuk.ifs.project.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.innovateuk.ifs.project.state.OnHoldReasonResource;
import org.innovateuk.ifs.threads.resource.PostResource;
import org.innovateuk.ifs.threads.resource.ProjectStateCommentsResource;
import org.springframework.stereotype.Service;

import static java.lang.String.format;

@Service
public class ProjectStateRestServiceImpl extends BaseRestService implements ProjectStateRestService {

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

    @Override
    public RestResult<Void> putProjectOnHold(long projectId, OnHoldReasonResource reason) {
        return postWithRestResult(projectRestURL + "/" + projectId + "/on-hold", reason, Void.class);
    }

    @Override
    public RestResult<Void> resumeProject(long projectId) {
        return postWithRestResult(projectRestURL + "/" + projectId + "/resume");
    }

    @Override
    public RestResult<ProjectStateCommentsResource> findOpenComments(long projectId) {
        return getWithRestResult(format("/project/%d/state/comments/open", projectId), ProjectStateCommentsResource.class);
    }

    @Override
    public RestResult<Void> addPost(PostResource post, long projectId, long threadId) {
        return postWithRestResult(format("/project/%d/state/comments/%d/post", projectId, threadId), post, Void.class);
    }
}
