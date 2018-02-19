package org.innovateuk.ifs.assessment.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.innovateuk.ifs.commons.service.ParameterizedTypeReferences;
import org.innovateuk.ifs.invite.resource.*;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

import static java.lang.String.format;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleJoiner;

/**
 * REST service for managing {@link InviteResource} to {@link org.innovateuk.ifs.competition.resource.CompetitionResource}s
 */

@Service
public class AssessmentPanelInviteRestServiceImpl extends BaseRestService implements AssessmentPanelInviteRestService {

    private static final String assessmentPanelInviteRestUrl = "/assessment-panel-invite";


    @Override
    public RestResult<AssessorInvitesToSendResource> getAllInvitesToSend(long competitionId) {
        return getWithRestResult(format("%s/%s/%s", assessmentPanelInviteRestUrl, "get-all-invites-to-send", competitionId), AssessorInvitesToSendResource.class);
    }

    @Override
    public RestResult<AssessorInvitesToSendResource> getAllInvitesToResend(long competitionId, List<Long> inviteIds) {
        String baseUrl = format("%s/%s/%s", assessmentPanelInviteRestUrl, "get-all-invites-to-resend", competitionId);

        UriComponentsBuilder builder = UriComponentsBuilder.fromPath(baseUrl)
                .queryParam("inviteIds", simpleJoiner(inviteIds, ","));

        return getWithRestResult(builder.toUriString(), AssessorInvitesToSendResource.class);
    }

    @Override
    public RestResult<Void> sendAllInvites(long competitionId, AssessorInviteSendResource assessorInviteSendResource) {
        return postWithRestResult(format("%s/%s/%s", assessmentPanelInviteRestUrl, "send-all-invites", competitionId), assessorInviteSendResource, Void.class);
    }

    @Override
    public RestResult<Void> resendInvites(List<Long> inviteIds, AssessorInviteSendResource assessorInviteSendResource) {
        String baseUrl = format("%s/%s", assessmentPanelInviteRestUrl, "resend-invites");

        UriComponentsBuilder builder = UriComponentsBuilder.fromPath(baseUrl)
                .queryParam("inviteIds", simpleJoiner(inviteIds, ","));

        return postWithRestResult(builder.toUriString(), assessorInviteSendResource, Void.class);
    }

    @Override
    public RestResult<AssessorCreatedInvitePageResource> getCreatedInvites(long competitionId, int page) {
        String baseUrl = format("%s/%s/%s", assessmentPanelInviteRestUrl, "get-created-invites", competitionId);

        UriComponentsBuilder builder = UriComponentsBuilder.fromPath(baseUrl)
                .queryParam("page", page);

        return getWithRestResult(builder.toUriString(), AssessorCreatedInvitePageResource.class);
    }

    @Override
    public RestResult<Void> inviteUsers(ExistingUserStagedInviteListResource existingUserStagedInvites) {
        return postWithRestResult(format("%s/%s", assessmentPanelInviteRestUrl, "invite-users"), existingUserStagedInvites, Void.class);
    }

    @Override
    public RestResult<AvailableAssessorPageResource> getAvailableAssessors(long competitionId, int page) {
        String baseUrl = format("%s/%s/%s", assessmentPanelInviteRestUrl, "get-available-assessors", competitionId);

        UriComponentsBuilder builder = UriComponentsBuilder.fromPath(baseUrl)
                .queryParam("page", page);

        return getWithRestResult(builder.toUriString(), AvailableAssessorPageResource.class);
    }

    @Override
    public RestResult<List<Long>> getAvailableAssessorIds(long competitionId) {
        String baseUrl = format("%s/%s/%s", assessmentPanelInviteRestUrl, "get-available-assessor-ids", competitionId);

        UriComponentsBuilder builder = UriComponentsBuilder
                .fromPath(baseUrl);

        return getWithRestResult(builder.toUriString(), ParameterizedTypeReferences.longsListType());
    }

    @Override
    public RestResult<List<AssessmentReviewPanelParticipantResource>> getAllInvitesByUser(long userId) {
        String baseUrl = format("%s/%s/%s", assessmentPanelInviteRestUrl, "get-all-invites-by-user", userId);

        UriComponentsBuilder builder = UriComponentsBuilder
                .fromPath(baseUrl);

        return getWithRestResult(builder.toUriString(), ParameterizedTypeReferences.assessmentPanelParticipantResourceListType());
    }

    @Override
    public RestResult<List<Long>> getNonAcceptedAssessorInviteIds(long competitionId) {
        String baseUrl = format("%s/%s/%s", assessmentPanelInviteRestUrl, "get-non-accepted-assessor-invite-ids", competitionId);

        UriComponentsBuilder builder = UriComponentsBuilder
                .fromPath(baseUrl);

        return getWithRestResult(builder.toUriString(), ParameterizedTypeReferences.longsListType());
    }

    @Override
    public RestResult<AssessorInviteOverviewPageResource> getInvitationOverview(long competitionId,
                                                                                int page,
                                                                                List<ParticipantStatusResource> participantStatuses) {
        String baseUrl = format("%s/%s/%s", assessmentPanelInviteRestUrl, "get-invitation-overview", competitionId);

        UriComponentsBuilder builder = UriComponentsBuilder.fromPath(baseUrl)
                .queryParam("page", page);

        String convertedStatusesList = simpleJoiner(participantStatuses, ",");
        builder.queryParam("statuses", convertedStatusesList);

        return getWithRestResult(builder.toUriString(), AssessorInviteOverviewPageResource.class);
    }

    @Override
    public RestResult<AssessmentReviewPanelInviteResource> openInvite(String inviteHash) {
        return postWithRestResultAnonymous(format("%s/%s/%s", assessmentPanelInviteRestUrl, "open-invite", inviteHash), AssessmentReviewPanelInviteResource.class);
    }

    @Override
    public RestResult<Void> acceptInvite(String inviteHash) {
        return postWithRestResult(format("%s/%s/%s", assessmentPanelInviteRestUrl, "accept-invite", inviteHash), Void.class);
    }

    @Override
    public RestResult<Void> rejectInvite(String inviteHash) {
        return postWithRestResultAnonymous(format("%s/%s/%s", assessmentPanelInviteRestUrl, "reject-invite", inviteHash), Void.class);
    }

    @Override
    public RestResult<Boolean> checkExistingUser(String inviteHash) {
        return getWithRestResultAnonymous(format("%s/%s/%s", assessmentPanelInviteRestUrl, "check-existing-user", inviteHash), Boolean.class);
    }

    @Override
    public RestResult<Void> deleteInvite(String email, long competitionId) {
        String baseUrl = format("%s/%s", assessmentPanelInviteRestUrl, "delete-invite");

        UriComponentsBuilder builder = UriComponentsBuilder.fromPath(baseUrl)
                .queryParam("competitionId", competitionId)
                .queryParam("email", email);

        return deleteWithRestResult(builder.toUriString(), Void.class);
    }

    @Override
    public RestResult<Void> deleteAllInvites(long competitionId) {
        String baseUrl = format("%s/%s", assessmentPanelInviteRestUrl, "delete-all-invites");

        UriComponentsBuilder builder = UriComponentsBuilder.fromPath(baseUrl)
                .queryParam("competitionId", competitionId);

        return deleteWithRestResult(builder.toUriString(), Void.class);
    }
}
