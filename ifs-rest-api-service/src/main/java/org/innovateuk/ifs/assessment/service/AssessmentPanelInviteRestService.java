package org.innovateuk.ifs.assessment.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.invite.resource.*;

import java.util.List;
import java.util.Optional;

/**
 * REST service for managing {@link InviteResource} to {@link org.innovateuk.ifs.competition.resource.CompetitionResource }
 */
public interface AssessmentPanelInviteRestService {

    RestResult<AssessorCreatedInvitePageResource> getCreatedInvites(long competitionId, int page);

    RestResult<Void> inviteUsers(ExistingUserStagedInviteListResource existingUserStagedInvite);

    RestResult<AvailableAssessorPageResource> getAvailableAssessors(long competitionId, int page);
}
