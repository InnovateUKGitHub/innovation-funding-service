package org.innovateuk.ifs.assessment.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.innovateuk.ifs.email.resource.EmailContent;
import org.innovateuk.ifs.invite.resource.*;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Optional;

import static java.lang.String.format;
import static org.innovateuk.ifs.commons.service.ParameterizedTypeReferences.*;

/**
 * REST service for managing {@link org.innovateuk.ifs.invite.resource.InviteResource} to {@link org.innovateuk.ifs.competition.resource.CompetitionResource}s
 */
@Service
public class CompetitionInviteRestServiceImpl extends BaseRestService implements CompetitionInviteRestService {

    private static final String competitionInviteRestUrl = "/competitioninvite";

    @Override
    public RestResult<AssessorInviteToSendResource> getCreated(long inviteId) {
        return getWithRestResult(format("%s/%s/%s", competitionInviteRestUrl, "getCreated", inviteId), AssessorInviteToSendResource.class);
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
        String url = format("%s/%s/%s", competitionInviteRestUrl, "getAvailableAssessors", competitionId);
        String filteredUrl = addAvailableAssessorsQueryParams(url, page, innovationArea);

        return getWithRestResult(filteredUrl, AvailableAssessorPageResource.class);
    }

    private String addAvailableAssessorsQueryParams(String url, int page, Optional<Long> innovationArea) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromPath(url);

        builder.queryParam("page", page);
        innovationArea.ifPresent(innovationAreaId -> builder.queryParam("innovationArea", innovationAreaId));

        return builder.toUriString();
    }

    @Override
    public RestResult<List<AssessorCreatedInviteResource>> getCreatedInvites(long competitionId) {
        return getWithRestResult(format("%s/%s/%s", competitionInviteRestUrl, "getCreatedInvites", competitionId), assessorCreatedInviteResourceListType());
    }

    public RestResult<List<AssessorInviteOverviewResource>> getInvitationOverview(long competitionId) {
        return getWithRestResult(format("%s/%s/%s", competitionInviteRestUrl, "getInvitationOverview", competitionId), assessorInviteOverviewResourceListType());
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
    public RestResult<Void> inviteNewUsers(NewUserStagedInviteListResource newUserStagedInvites,  long competitionId) {
        return postWithRestResult(format("%s/%s/%s", competitionInviteRestUrl, "inviteNewUsers", competitionId), newUserStagedInvites, Void.class);
    }

    @Override
    public RestResult<Void> deleteInvite(String email, long competitionId) {
        return deleteWithRestResult(format("%s/%s?competitionId=%s&email=%s", competitionInviteRestUrl, "deleteInvite", competitionId, email), Void.class);
    }

    @Override
    public RestResult<AssessorInviteToSendResource> sendInvite(long inviteId, EmailContent content) {
        return postWithRestResult(format("%s/%s/%s", competitionInviteRestUrl, "sendInvite", inviteId), content, AssessorInviteToSendResource.class);
    }


}
