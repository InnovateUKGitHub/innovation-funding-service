package com.worth.ifs.assessment.service;

import com.worth.ifs.assessment.resource.CompetitionRejectionReasonResource;
import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.invite.resource.CompetitionInviteResource;

/**
 * REST service for managing {@link com.worth.ifs.invite.domain.Invite}s to {@link com.worth.ifs.competition.domain.Competition}s
 */
public interface CompetitionInviteRestService {

    RestResult<CompetitionInviteResource> openInvite(String inviteHash);

    RestResult<Void> acceptInvite(String inviteHash);

    RestResult<Void> rejectInvite(String inviteHash, CompetitionRejectionReasonResource rejectionReason);
}
