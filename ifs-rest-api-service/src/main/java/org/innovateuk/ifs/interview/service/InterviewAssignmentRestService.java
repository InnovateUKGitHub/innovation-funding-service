package org.innovateuk.ifs.interview.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.invite.resource.AvailableApplicationPageResource;
import org.innovateuk.ifs.invite.resource.ExistingUserStagedInviteListResource;
import org.innovateuk.ifs.invite.resource.InterviewAssignmentStagedApplicationPageResource;

import java.util.List;

/**
 * REST service for managing to interview panels
 */
public interface InterviewAssignmentRestService {

    RestResult<AvailableApplicationPageResource> getAvailableApplications(long competitionId, int page);

    RestResult<List<Long>> getAvailableApplicationIds(long competitionId);

    // TODO rename the resource or create a new one?
    RestResult<Void> assignApplications(ExistingUserStagedInviteListResource existingUserStagedInviteListResource);

    RestResult<InterviewAssignmentStagedApplicationPageResource> getStagedApplications(long competitionId, int page);
}