package com.worth.ifs.invite.service;

import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.invite.resource.CompetitionAssessorInviteResource;

/**
 *
 */
public interface CompetitionAssessorInviteRestService {

    RestResult<CompetitionAssessorInviteResource> accessInvite(String inviteHash);
}
