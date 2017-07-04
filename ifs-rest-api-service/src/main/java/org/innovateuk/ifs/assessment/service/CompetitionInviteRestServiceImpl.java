package org.innovateuk.ifs.assessment.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.innovateuk.ifs.commons.service.ParameterizedTypeReferences;
import org.innovateuk.ifs.invite.resource.*;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Optional;

import static java.lang.String.format;

/**
 * REST service for managing {@link org.innovateuk.ifs.invite.resource.InviteResource} to {@link org.innovateuk.ifs.competition.resource.CompetitionResource}s
 */
@Service
public class CompetitionInviteRestServiceImpl extends BaseRestService implements CompetitionInviteRestService {

    private static final String competitionInviteRestUrl = "/competitioninvite";

    @Override
    public RestResult<AssessorInvitesToSendResource> getAllInvitesToSend(long competitionId) {
        return getWithRestResult(format("%s/%s/%s", competitionInviteRestUrl, "getAllInvitesToSend", competitionId), AssessorInvitesToSendResource.class);
    }

    @Override
    public RestResult<AssessorInvitesToSendResource> getInviteToSend(long inviteId) {
        return getWithRestResult(format("%s/%s/%s", competitionInviteRestUrl, "getInviteToSend", inviteId), AssessorInvitesToSendResource.class);
    }

    @Override
    public RestResult<CompetitionInviteResource> getInvite(String inviteHash) {
        return getWithRestResultAnonymous(format("%s/%s/%s", competitionInviteRestUrl, "getInvite", inviteHash), CompetitionInviteResource.class);
    }

    @Override
    public RestResult<CompetitionInviteResource> openInvite(String inviteHash) {
        return postWithRestResultAnonymous(format("%s/%s/%s", competitionInviteRestUrl, "openInvite", inviteHash), CompetitionInviteResource.class);
    }

    @Override
    public RestResult<Void> acceptInvite(String inviteHash) {
        return postWithRestResult(format("%s/%s/%s", competitionInviteRestUrl, "acceptInvite", inviteHash), Void.class);
    }

    @Override
    public RestResult<Void> rejectInvite(String inviteHash, CompetitionRejectionResource rejectionReason) {
        return postWithRestResultAnonymous(format("%s/%s/%s", competitionInviteRestUrl, "rejectInvite", inviteHash), rejectionReason, Void.class);
    }

    @Override
    public RestResult<Boolean> checkExistingUser(String inviteHash) {
        return getWithRestResultAnonymous(format("%s/%s/%s", competitionInviteRestUrl, "checkExistingUser", inviteHash), Boolean.class);
    }

    @Override
    public RestResult<AvailableAssessorPageResource> getAvailableAssessors(long competitionId, int page, Optional<Long> innovationArea) {
        String baseUrl = format("%s/%s/%s", competitionInviteRestUrl, "getAvailableAssessors", competitionId);

        UriComponentsBuilder builder = UriComponentsBuilder.fromPath(baseUrl)
                .queryParam("page", page);

        innovationArea.ifPresent(innovationAreaId -> builder.queryParam("innovationArea", innovationAreaId));

        return getWithRestResult(builder.toUriString(), AvailableAssessorPageResource.class);
    }

    @Override
    public RestResult<List<Long>> getAvailableAssessorIds(long competitionId, Optional<Long> innovationArea) {
        String baseUrl = format("%s/%s/%s", competitionInviteRestUrl, "getAvailableAssessors", competitionId);

        UriComponentsBuilder builder = UriComponentsBuilder
                .fromPath(baseUrl)
                .queryParam("all");
        innovationArea.ifPresent(innovationAreaId -> builder.queryParam("innovationArea", innovationAreaId));

        return getWithRestResult(builder.toUriString(), ParameterizedTypeReferences.longsListType());
    }

    @Override
    public RestResult<AssessorCreatedInvitePageResource> getCreatedInvites(long competitionId, int page) {
        String baseUrl = format("%s/%s/%s", competitionInviteRestUrl, "getCreatedInvites", competitionId);

        UriComponentsBuilder builder = UriComponentsBuilder.fromPath(baseUrl)
                .queryParam("page", page);

        return getWithRestResult(builder.toUriString(), AssessorCreatedInvitePageResource.class);
    }

    @Override
    public RestResult<AssessorInviteOverviewPageResource> getInvitationOverview(long competitionId,
                                                                                int page,
                                                                                Optional<Long> innovationArea,
                                                                                Optional<ParticipantStatusResource> participantStatus,
                                                                                Optional<Boolean> compliant) {
        String baseUrl = format("%s/%s/%s", competitionInviteRestUrl, "getInvitationOverview", competitionId);

        UriComponentsBuilder builder = UriComponentsBuilder.fromPath(baseUrl)
                .queryParam("page", page);

        innovationArea.ifPresent(innovationAreaId -> builder.queryParam("innovationArea", innovationAreaId));
        participantStatus.ifPresent(status -> builder.queryParam("status", status.toString()));
        compliant.ifPresent(hasContract -> builder.queryParam("compliant", hasContract));

        return getWithRestResult(builder.toUriString(), AssessorInviteOverviewPageResource.class);
    }

    @Override
    public RestResult<CompetitionInviteStatisticsResource> getInviteStatistics(long competitionId) {
        return getWithRestResult(format("%s/%s/%s", competitionInviteRestUrl, "getInviteStatistics", competitionId), CompetitionInviteStatisticsResource.class);
    }

    @Override
    public RestResult<CompetitionInviteResource> inviteUser(ExistingUserStagedInviteResource existingUserStagedInvite) {
        return postWithRestResult(format("%s/%s", competitionInviteRestUrl, "inviteUser"), existingUserStagedInvite, CompetitionInviteResource.class);
    }

    @Override
    public RestResult<Void> inviteUsers(ExistingUserStagedInviteListResource existingUserStagedInvites) {
        return postWithRestResult(format("%s/%s", competitionInviteRestUrl, "inviteUsers"), existingUserStagedInvites, Void.class);
    }

    @Override
    public RestResult<Void> inviteNewUsers(NewUserStagedInviteListResource newUserStagedInvites, long competitionId) {
        return postWithRestResult(format("%s/%s/%s", competitionInviteRestUrl, "inviteNewUsers", competitionId), newUserStagedInvites, Void.class);
    }

    @Override
    public RestResult<Void> deleteInvite(String email, long competitionId) {
        return deleteWithRestResult(format("%s/%s?competitionId=%s&email=%s", competitionInviteRestUrl, "deleteInvite", competitionId, email), Void.class);
    }

    @Override
    public RestResult<Void> deleteAllInvites(long competitionId) {
        return deleteWithRestResult(format("%s/%s?competitionId=%s", competitionInviteRestUrl, "deleteAllInvites", competitionId), Void.class);
    }

    @Override
    public RestResult<Void> sendAllInvites(long competitionId, AssessorInviteSendResource assessorInviteSendResource) {
        return postWithRestResult(format("%s/%s/%s", competitionInviteRestUrl, "sendAllInvites", competitionId), assessorInviteSendResource, Void.class);
    }

    @Override
    public RestResult<Void> resendInvite(long inviteId, AssessorInviteSendResource assessorInviteSendResource) {
        return postWithRestResult(format("%s/%s/%s", competitionInviteRestUrl, "resendInvite", inviteId), assessorInviteSendResource, Void.class);
    }
}
