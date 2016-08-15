package com.worth.ifs.assessment.service;

import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.commons.service.BaseRestService;
import com.worth.ifs.invite.resource.CompetitionInviteResource;
import org.springframework.stereotype.Service;

import static java.lang.String.format;

/**
 * REST service for managing {@link com.worth.ifs.invite.domain.Invite}s to {@link com.worth.ifs.competition.domain.Competition}s
 */
@Service
public class CompetitionInviteRestServiceImpl extends BaseRestService implements CompetitionInviteRestService {

    private static final String competitionInviteRestUrl = "/competitioninvite";

    @Override
    public RestResult<CompetitionInviteResource> openInvite(String inviteHash) {
        return postWithRestResultAnonymous(format("%s/%s/%s", competitionInviteRestUrl, "/openInvite", inviteHash), CompetitionInviteResource.class);
    }
}
