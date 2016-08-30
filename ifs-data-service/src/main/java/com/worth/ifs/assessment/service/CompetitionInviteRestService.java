package com.worth.ifs.assessment.service;

import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.invite.resource.CompetitionInviteResource;
import com.worth.ifs.invite.resource.CompetitionRejectionResource;

/**
 * REST service for managing {@link com.worth.ifs.invite.domain.Invite}s to {@link com.worth.ifs.competition.domain.Competition}s
 */
public interface CompetitionInviteRestService {

    RestResult<CompetitionInviteResource> getInvite(String inviteHash);

    RestResult<CompetitionInviteResource> openInvite(String inviteHash);

    RestResult<Void> acceptInvite(String inviteHash);

    RestResult<Void> rejectInvite(String inviteHash, CompetitionRejectionResource rejectionReason);

    RestResult<Boolean> checkExistingUser(String inviteHash);
}
