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
import static org.innovateuk.ifs.util.CollectionFunctions.simpleJoiner;

/**
 * REST service for managing {@link org.innovateuk.ifs.invite.resource.InviteResource} to {@link org.innovateuk.ifs.competition.resource.CompetitionResource}s
 */
@Service
public class CompetitionInviteRestServiceImpl extends BaseRestService implements CompetitionInviteRestService {

    private static final String COMPETITION_INVITE_REST_URL = "/competitioninvite";

    @Override
    public RestResult<AssessorInvitesToSendResource> getAllInvitesToSend(long competitionId) {
        return getWithRestResult(format("%s/%s/%s", COMPETITION_INVITE_REST_URL, "getAllInvitesToSend", competitionId), AssessorInvitesToSendResource.class);
    }

    @Override
    public RestResult<AssessorInvitesToSendResource> getInviteToSend(long inviteId) {
        return getWithRestResult(format("%s/%s/%s", COMPETITION_INVITE_REST_URL, "getInviteToSend", inviteId), AssessorInvitesToSendResource.class);
    }

    @Override
    public RestResult<AssessorInvitesToSendResource> getAllInvitesToResend(long competitionId, List<Long> inviteIds) {
        String baseUrl = format("%s/%s/%s", COMPETITION_INVITE_REST_URL, "getAllInvitesToResend", competitionId);

        UriComponentsBuilder builder = UriComponentsBuilder.fromPath(baseUrl)
                .queryParam("inviteIds", simpleJoiner(inviteIds, ","));

        return getWithRestResult(builder.toUriString(), AssessorInvitesToSendResource.class);
    }

    @Override
    public RestResult<CompetitionInviteResource> getInvite(String inviteHash) {
        return getWithRestResultAnonymous(format("%s/%s/%s", COMPETITION_INVITE_REST_URL, "getInvite", inviteHash), CompetitionInviteResource.class);
    }

    @Override
    public RestResult<CompetitionInviteResource> openInvite(String inviteHash) {
        return postWithRestResultAnonymous(format("%s/%s/%s", COMPETITION_INVITE_REST_URL, "openInvite", inviteHash), CompetitionInviteResource.class);
    }

    @Override
    public RestResult<Void> acceptInvite(String inviteHash) {
        return postWithRestResult(format("%s/%s/%s", COMPETITION_INVITE_REST_URL, "acceptInvite", inviteHash), Void.class);
    }

    @Override
    public RestResult<Void> rejectInvite(String inviteHash, CompetitionRejectionResource rejectionReason) {
        return postWithRestResultAnonymous(format("%s/%s/%s", COMPETITION_INVITE_REST_URL, "rejectInvite", inviteHash), rejectionReason, Void.class);
    }

    @Override
    public RestResult<Boolean> checkExistingUser(String inviteHash) {
        return getWithRestResultAnonymous(format("%s/%s/%s", COMPETITION_INVITE_REST_URL, "checkExistingUser", inviteHash), Boolean.class);
    }

    @Override
    public RestResult<AvailableAssessorPageResource> getAvailableAssessors(long competitionId, int page, Optional<Long> innovationArea) {
        String baseUrl = format("%s/%s/%s", COMPETITION_INVITE_REST_URL, "getAvailableAssessors", competitionId);

        UriComponentsBuilder builder = UriComponentsBuilder.fromPath(baseUrl)
                .queryParam("page", page);

        innovationArea.ifPresent(innovationAreaId -> builder.queryParam("innovationArea", innovationAreaId));

        return getWithRestResult(builder.toUriString(), AvailableAssessorPageResource.class);
    }

    @Override
    public RestResult<List<Long>> getAvailableAssessorIds(long competitionId, Optional<Long> innovationArea) {
        String baseUrl = format("%s/%s/%s", COMPETITION_INVITE_REST_URL, "getAvailableAssessors", competitionId);

        UriComponentsBuilder builder = UriComponentsBuilder
                .fromPath(baseUrl)
                .queryParam("all");
        innovationArea.ifPresent(innovationAreaId -> builder.queryParam("innovationArea", innovationAreaId));

        return getWithRestResult(builder.toUriString(), ParameterizedTypeReferences.longsListType());
    }

    @Override
    public RestResult<List<Long>> getAssessorsNotAcceptedInviteIds(long competitionId,
                                                                   Optional<Long> innovationArea,
                                                                   List<ParticipantStatusResource> participantStatuses,
                                                                   Optional<Boolean> compliant) {
        String baseUrl = format("%s/%s/%s", COMPETITION_INVITE_REST_URL, "getAssessorsNotAcceptedInviteIds", competitionId);

        UriComponentsBuilder builder = UriComponentsBuilder.fromPath(baseUrl);
        innovationArea.ifPresent(innovationAreaId -> builder.queryParam("innovationArea", innovationAreaId));
        builder.queryParam("statuses", simpleJoiner(participantStatuses, ","));
        compliant.ifPresent(hasContract -> builder.queryParam("compliant", hasContract));

        return getWithRestResult(builder.toUriString(), ParameterizedTypeReferences.longsListType());
    }

    @Override
    public RestResult<AssessorCreatedInvitePageResource> getCreatedInvites(long competitionId, int page) {
        String baseUrl = format("%s/%s/%s", COMPETITION_INVITE_REST_URL, "getCreatedInvites", competitionId);

        UriComponentsBuilder builder = UriComponentsBuilder.fromPath(baseUrl)
                .queryParam("page", page);

        return getWithRestResult(builder.toUriString(), AssessorCreatedInvitePageResource.class);
    }

    @Override
    public RestResult<AssessorInviteOverviewPageResource> getInvitationOverview(long competitionId,
                                                                                int page,
                                                                                Optional<Long> innovationArea,
                                                                                List<ParticipantStatusResource> participantStatuses,
                                                                                Optional<Boolean> compliant) {
        String baseUrl = format("%s/%s/%s", COMPETITION_INVITE_REST_URL, "getInvitationOverview", competitionId);

        UriComponentsBuilder builder = UriComponentsBuilder.fromPath(baseUrl)
                .queryParam("page", page);

        innovationArea.ifPresent(innovationAreaId -> builder.queryParam("innovationArea", innovationAreaId));
        String convertedStatusesList = simpleJoiner(participantStatuses, ",");
        builder.queryParam("statuses", convertedStatusesList);
        compliant.ifPresent(hasContract -> builder.queryParam("compliant", hasContract));

        return getWithRestResult(builder.toUriString(), AssessorInviteOverviewPageResource.class);
    }

    @Override
    public RestResult<CompetitionInviteStatisticsResource> getInviteStatistics(long competitionId) {
        return getWithRestResult(format("%s/%s/%s", COMPETITION_INVITE_REST_URL, "getInviteStatistics", competitionId), CompetitionInviteStatisticsResource.class);
    }

    @Override
    public RestResult<CompetitionInviteResource> inviteUser(ExistingUserStagedInviteResource existingUserStagedInvite) {
        return postWithRestResult(format("%s/%s", COMPETITION_INVITE_REST_URL, "inviteUser"), existingUserStagedInvite, CompetitionInviteResource.class);
    }

    @Override
    public RestResult<Void> inviteUsers(ExistingUserStagedInviteListResource existingUserStagedInvites) {
        return postWithRestResult(format("%s/%s", COMPETITION_INVITE_REST_URL, "inviteUsers"), existingUserStagedInvites, Void.class);
    }

    @Override
    public RestResult<Void> inviteNewUsers(NewUserStagedInviteListResource newUserStagedInvites, long competitionId) {
        return postWithRestResult(format("%s/%s/%s", COMPETITION_INVITE_REST_URL, "inviteNewUsers", competitionId), newUserStagedInvites, Void.class);
    }

    @Override
    public RestResult<Void> deleteInvite(String email, long competitionId) {
        return deleteWithRestResult(format("%s/%s?competitionId=%s&email=%s", COMPETITION_INVITE_REST_URL, "deleteInvite", competitionId, email), Void.class);
    }

    @Override
    public RestResult<Void> deleteAllInvites(long competitionId) {
        return deleteWithRestResult(format("%s/%s?competitionId=%s", COMPETITION_INVITE_REST_URL, "deleteAllInvites", competitionId), Void.class);
    }

    @Override
    public RestResult<Void> sendAllInvites(long competitionId, AssessorInviteSendResource assessorInviteSendResource) {
        return postWithRestResult(format("%s/%s/%s", COMPETITION_INVITE_REST_URL, "sendAllInvites", competitionId), assessorInviteSendResource, Void.class);
    }

    @Override
    public RestResult<Void> resendInvite(long inviteId, AssessorInviteSendResource assessorInviteSendResource) {
        return postWithRestResult(format("%s/%s/%s", COMPETITION_INVITE_REST_URL, "resendInvite", inviteId), assessorInviteSendResource, Void.class);
    }

    @Override
    public RestResult<Void> resendInvites(List<Long> inviteIds, AssessorInviteSendResource assessorInviteSendResource) {
        String baseUrl = format("%s/%s", COMPETITION_INVITE_REST_URL, "resendInvites");

        UriComponentsBuilder builder = UriComponentsBuilder.fromPath(baseUrl)
                .queryParam("inviteIds", simpleJoiner(inviteIds, ","));

        return postWithRestResult(builder.toUriString(), assessorInviteSendResource, Void.class);
    }
}
