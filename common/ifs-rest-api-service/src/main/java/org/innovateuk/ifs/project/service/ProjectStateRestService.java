package org.innovateuk.ifs.project.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.project.state.OnHoldReasonResource;
import org.innovateuk.ifs.threads.resource.PostResource;
import org.innovateuk.ifs.threads.resource.ProjectStateCommentsResource;

public interface ProjectStateRestService {
    RestResult<Void> withdrawProject(long projectId);

    RestResult<Void> handleProjectOffline(long projectId);

    RestResult<Void> completeProjectOffline(long projectId);

    RestResult<Void> putProjectOnHold(long projectId, OnHoldReasonResource reason);

    RestResult<Void> resumeProject(long projectId);

    RestResult<Void> markAsSuccessful(long projectId);

    RestResult<Void> markAsUnsuccessful(long projectId);

    RestResult<ProjectStateCommentsResource> findOpenComments(long projectId);

    RestResult<Void> addPost(PostResource post, long projectId, long threadId);
}