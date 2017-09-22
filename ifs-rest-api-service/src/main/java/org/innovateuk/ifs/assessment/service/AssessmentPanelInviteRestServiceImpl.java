package org.innovateuk.ifs.assessment.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.innovateuk.ifs.invite.resource.AssessorInviteSendResource;
import org.innovateuk.ifs.invite.resource.AssessorInvitesToSendResource;
import org.springframework.stereotype.Service;

import static java.lang.String.format;
import org.innovateuk.ifs.invite.resource.*;
import org.springframework.web.util.UriComponentsBuilder;


/**
 * REST service for managing {@link InviteResource} to {@link org.innovateuk.ifs.competition.resource.CompetitionResource}s
 */


@Service
public class AssessmentPanelInviteRestServiceImpl extends BaseRestService implements AssessmentPanelInviteRestService {

    private static final String assessmentPanelInviteRestUrl = "/assessmentpanelinvite";


    @Override
    public RestResult<AssessorInvitesToSendResource> getAllInvitesToSend(long competitionId) {
        return getWithRestResult(format("%s/%s/%s", assessmentPanelInviteRestUrl, "getAllInvitesToSend", competitionId), AssessorInvitesToSendResource.class);
    }

    @Override
    public RestResult<Void> sendAllInvites(long competitionId, AssessorInviteSendResource assessorInviteSendResource) {
        return postWithRestResult(format("%s/%s/%s", assessmentPanelInviteRestUrl, "sendAllInvites", competitionId), assessorInviteSendResource, Void.class);
    }


    @Override
    public RestResult<AssessorCreatedInvitePageResource> getCreatedInvites(long competitionId, int page) {
        String baseUrl = format("%s/%s/%s", assessmentPanelInviteRestUrl, "getCreatedInvites", competitionId);

        UriComponentsBuilder builder = UriComponentsBuilder.fromPath(baseUrl)
                .queryParam("page", page);

        return getWithRestResult(builder.toUriString(), AssessorCreatedInvitePageResource.class);
    }

    @Override
    public RestResult<Void> inviteUsers(ExistingUserStagedInviteListResource existingUserStagedInvites) {
        return postWithRestResult(format("%s/%s", assessmentPanelInviteRestUrl, "inviteUsers"), existingUserStagedInvites, Void.class);
    }

    @Override
    public RestResult<AvailableAssessorPageResource> getAvailableAssessors(long competitionId, int page) {
        String baseUrl = format("%s/%s/%s", assessmentPanelInviteRestUrl, "getAvailableAssessors", competitionId);

        UriComponentsBuilder builder = UriComponentsBuilder.fromPath(baseUrl)
                .queryParam("page", page);

        return getWithRestResult(builder.toUriString(), AvailableAssessorPageResource.class);
    }
}

