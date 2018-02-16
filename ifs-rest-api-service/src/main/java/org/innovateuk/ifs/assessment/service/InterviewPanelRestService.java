package org.innovateuk.ifs.assessment.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.invite.resource.AvailableApplicationPageResource;
import org.innovateuk.ifs.invite.resource.ExistingUserStagedInviteListResource;
import org.innovateuk.ifs.invite.resource.InterviewPanelCreatedInvitePageResource;

import java.util.List;

/**
 * REST service for managing to interview panels
 */
public interface InterviewPanelRestService {

    RestResult<AvailableApplicationPageResource> getAvailableApplications(long competitionId, int page);

    RestResult<List<Long>> getAvailableAssessorsIds(long competitionId);

    // TODO rename the resource or create a new one?
    RestResult<Void> assignApplications(ExistingUserStagedInviteListResource existingUserStagedInviteListResource);

    RestResult<InterviewPanelCreatedInvitePageResource> getCreatedInvites(long competitionId, int page);
}