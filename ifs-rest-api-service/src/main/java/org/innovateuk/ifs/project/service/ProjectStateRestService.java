package org.innovateuk.ifs.project.service;

import org.innovateuk.ifs.commons.rest.RestResult;

public interface ProjectStateRestService {
    RestResult<Void> withdrawProject(long projectId);

    RestResult<Void> handleProjectOffline(long projectId);

    RestResult<Void> completeProjectOffline(long projectId);

    RestResult<Void> putProjectOnHold(long projectId);

    RestResult<Void> resumeProject(long projectId);

}