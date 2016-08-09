package com.worth.ifs.assessment.service;

import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.commons.service.BaseRestService;
import com.worth.ifs.invite.resource.CompetitionInviteResource;

import static java.lang.String.format;

/**
 * REST service for managing {@link com.worth.ifs.invite.domain.Invite}s to {@link com.worth.ifs.competition.domain.Competition}s
 */
public class CompetitionInviteRestServiceImpl extends BaseRestService implements CompetitionInviteRestService {

    private static final String competitionInviteRestUrl = "competitioninvite/openinvite";

    @Override
    public RestResult<CompetitionInviteResource> accessInvite(String inviteHash) {
        return postWithRestResult(format("%s/%s", competitionInviteRestUrl, inviteHash), CompetitionInviteResource.class);
    }
}
