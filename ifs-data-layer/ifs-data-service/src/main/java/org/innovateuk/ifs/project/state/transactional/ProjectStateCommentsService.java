package org.innovateuk.ifs.project.state.transactional;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.project.resource.ProjectState;
import org.innovateuk.ifs.threads.resource.PostResource;
import org.innovateuk.ifs.threads.resource.ProjectStateHistoryResource;
import org.innovateuk.ifs.threads.service.ThreadService;

public interface ProjectStateCommentsService extends ThreadService<ProjectStateHistoryResource, PostResource> {

    ServiceResult<Long> create(long projectId, ProjectState state);

}