package org.innovateuk.ifs.assessment.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.innovateuk.ifs.commons.service.ParameterizedTypeReferences;
import org.innovateuk.ifs.invite.resource.*;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

import static java.lang.String.format;

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

    @Override
    public RestResult<List<Long>> getAvailableAssessorIds(long competitionId) {
        String baseUrl = format("%s/%s/%s", assessmentPanelInviteRestUrl, "getAvailableAssessorIds", competitionId);

        UriComponentsBuilder builder = UriComponentsBuilder
                .fromPath(baseUrl);

        return getWithRestResult(builder.toUriString(), ParameterizedTypeReferences.longsListType());
    }

    @Override
    public RestResult<CompetitionInviteResource> openInvite(String inviteHash) {
        return postWithRestResultAnonymous(format("%s/%s/%s", assessmentPanelInviteRestUrl, "openInvite", inviteHash), CompetitionInviteResource.class);
    }

    @Override
    public RestResult<Void> acceptInvite(String inviteHash) {
        return postWithRestResult(format("%s/%s/%s", assessmentPanelInviteRestUrl, "acceptInvite", inviteHash), Void.class);
    }

    @Override
    public RestResult<Void> rejectInvite(String inviteHash, CompetitionRejectionResource rejectionReason) {
        return postWithRestResultAnonymous(format("%s/%s/%s", assessmentPanelInviteRestUrl, "rejectInvite", inviteHash), rejectionReason, Void.class);
    }

    @Override
    public RestResult<Boolean> checkExistingUser(String inviteHash) {
        return getWithRestResultAnonymous(format("%s/%s/%s", assessmentPanelInviteRestUrl, "checkExistingUser", inviteHash), Boolean.class);
    }
}

