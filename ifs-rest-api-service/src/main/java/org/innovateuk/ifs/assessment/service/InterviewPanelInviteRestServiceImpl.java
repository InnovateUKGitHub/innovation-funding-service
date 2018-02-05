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
public class InterviewPanelInviteRestServiceImpl extends BaseRestService implements InterviewPanelInviteRestService {

    private static final String interviewPanelInviteRestUrl = "/interviewpanelinvite";


    @Override
    public RestResult<AssessorInvitesToSendResource> getAllInvitesToSend(long competitionId) {
        return getWithRestResult(format("%s/%s/%s", interviewPanelInviteRestUrl, "getAllInvitesToSend", competitionId), AssessorInvitesToSendResource.class);
    }

    @Override
    public RestResult<AssessorInvitesToSendResource> getAllInvitesToResend(long competitionId, List<Long> inviteIds) {
        String baseUrl = format("%s/%s/%s", interviewPanelInviteRestUrl, "getAllInvitesToResend", competitionId);

        UriComponentsBuilder builder = UriComponentsBuilder.fromPath(baseUrl)
                .queryParam("inviteIds", simpleJoiner(inviteIds, ","));

        return getWithRestResult(builder.toUriString(), AssessorInvitesToSendResource.class);
    }

    @Override
    public RestResult<Void> sendAllInvites(long competitionId, AssessorInviteSendResource assessorInviteSendResource) {
        return postWithRestResult(format("%s/%s/%s", interviewPanelInviteRestUrl, "sendAllInvites", competitionId), assessorInviteSendResource, Void.class);
    }

    @Override
    public RestResult<Void> resendInvites(List<Long> inviteIds, AssessorInviteSendResource assessorInviteSendResource) {
        String baseUrl = format("%s/%s", interviewPanelInviteRestUrl, "resendInvites");

        UriComponentsBuilder builder = UriComponentsBuilder.fromPath(baseUrl)
                .queryParam("inviteIds", simpleJoiner(inviteIds, ","));

        return postWithRestResult(builder.toUriString(), assessorInviteSendResource, Void.class);
    }

    @Override
    public RestResult<AssessorCreatedInvitePageResource> getCreatedInvites(long competitionId, int page) {
        String baseUrl = format("%s/%s/%s", interviewPanelInviteRestUrl, "getCreatedInvites", competitionId);

        UriComponentsBuilder builder = UriComponentsBuilder.fromPath(baseUrl)
                .queryParam("page", page);

        return getWithRestResult(builder.toUriString(), AssessorCreatedInvitePageResource.class);
    }

    @Override
    public RestResult<Void> inviteUsers(ExistingUserStagedInviteListResource existingUserStagedInvites) {
        return postWithRestResult(format("%s/%s", interviewPanelInviteRestUrl, "inviteUsers"), existingUserStagedInvites, Void.class);
    }

    @Override
    public RestResult<AvailableAssessorPageResource> getAvailableAssessors(long competitionId, int page) {
        String baseUrl = format("%s/%s/%s", interviewPanelInviteRestUrl, "getAvailableAssessors", competitionId);

        UriComponentsBuilder builder = UriComponentsBuilder.fromPath(baseUrl)
                .queryParam("page", page);

        return getWithRestResult(builder.toUriString(), AvailableAssessorPageResource.class);
    }

    @Override
    public RestResult<List<Long>> getAvailableAssessorIds(long competitionId) {
        String baseUrl = format("%s/%s/%s", interviewPanelInviteRestUrl, "getAvailableAssessorIds", competitionId);

        UriComponentsBuilder builder = UriComponentsBuilder
                .fromPath(baseUrl);

        return getWithRestResult(builder.toUriString(), ParameterizedTypeReferences.longsListType());
    }

    @Override
    public RestResult<List<AssessmentPanelParticipantResource>> getAllInvitesByUser(long userId) {
        String baseUrl = format("%s/%s/%s", interviewPanelInviteRestUrl, "getAllInvitesByUser", userId);

        UriComponentsBuilder builder = UriComponentsBuilder
                .fromPath(baseUrl);

        return getWithRestResult(builder.toUriString(), ParameterizedTypeReferences.assessmentPanelParticipantResourceListType());
    }

    @Override
    public RestResult<List<Long>> getNonAcceptedAssessorInviteIds(long competitionId) {
        String baseUrl = format("%s/%s/%s", interviewPanelInviteRestUrl, "getNonAcceptedAssessorInviteIds", competitionId);

        UriComponentsBuilder builder = UriComponentsBuilder
                .fromPath(baseUrl);

        return getWithRestResult(builder.toUriString(), ParameterizedTypeReferences.longsListType());
    }

    @Override
    public RestResult<AssessorInviteOverviewPageResource> getInvitationOverview(long competitionId,
                                                                                int page,
                                                                                List<ParticipantStatusResource> participantStatuses) {
        String baseUrl = format("%s/%s/%s", interviewPanelInviteRestUrl, "getInvitationOverview", competitionId);

        UriComponentsBuilder builder = UriComponentsBuilder.fromPath(baseUrl)
                .queryParam("page", page);

        String convertedStatusesList = simpleJoiner(participantStatuses, ",");
        builder.queryParam("statuses", convertedStatusesList);

        return getWithRestResult(builder.toUriString(), AssessorInviteOverviewPageResource.class);
    }

    @Override
    public RestResult<AssessmentPanelInviteResource> openInvite(String inviteHash) {
        return postWithRestResultAnonymous(format("%s/%s/%s", interviewPanelInviteRestUrl, "openInvite", inviteHash), AssessmentPanelInviteResource.class);
    }

    @Override
    public RestResult<Void> acceptInvite(String inviteHash) {
        return postWithRestResult(format("%s/%s/%s", interviewPanelInviteRestUrl, "acceptInvite", inviteHash), Void.class);
    }

    @Override
    public RestResult<Void> rejectInvite(String inviteHash) {
        return postWithRestResultAnonymous(format("%s/%s/%s", interviewPanelInviteRestUrl, "rejectInvite", inviteHash), Void.class);
    }

    @Override
    public RestResult<Boolean> checkExistingUser(String inviteHash) {
        return getWithRestResultAnonymous(format("%s/%s/%s", interviewPanelInviteRestUrl, "checkExistingUser", inviteHash), Boolean.class);
    }

    @Override
    public RestResult<Void> deleteInvite(String email, long competitionId) {
        String baseUrl = format("%s/%s", interviewPanelInviteRestUrl, "deleteInvite");

        UriComponentsBuilder builder = UriComponentsBuilder.fromPath(baseUrl)
                .queryParam("competitionId", competitionId)
                .queryParam("email", email);

        return deleteWithRestResult(builder.toUriString(), Void.class);
    }

    @Override
    public RestResult<Void> deleteAllInvites(long competitionId) {
        String baseUrl = format("%s/%s", interviewPanelInviteRestUrl, "deleteAllInvites");

        UriComponentsBuilder builder = UriComponentsBuilder.fromPath(baseUrl)
                .queryParam("competitionId", competitionId);

        return deleteWithRestResult(builder.toUriString(), Void.class);
    }
}

