package org.innovateuk.ifs.assessment.service;

import org.innovateuk.ifs.BaseRestServiceUnitTest;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.ParameterizedTypeReferences;
import org.innovateuk.ifs.invite.resource.*;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static java.lang.Boolean.TRUE;
import static java.lang.String.format;
import static java.util.Arrays.asList;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.innovateuk.ifs.assessment.builder.CompetitionInviteResourceBuilder.newCompetitionInviteResource;
import static org.innovateuk.ifs.invite.builder.AssessorCreatedInvitePageResourceBuilder.newAssessorCreatedInvitePageResource;
import static org.innovateuk.ifs.invite.builder.AssessorCreatedInviteResourceBuilder.newAssessorCreatedInviteResource;
import static org.innovateuk.ifs.invite.builder.AssessorInviteOverviewPageResourceBuilder.newAssessorInviteOverviewPageResource;
import static org.innovateuk.ifs.invite.builder.AssessorInviteSendResourceBuilder.newAssessorInviteSendResource;
import static org.innovateuk.ifs.invite.builder.AssessorInvitesToSendResourceBuilder.newAssessorInvitesToSendResource;
import static org.innovateuk.ifs.invite.builder.CompetitionInviteStatisticsResourceBuilder.newCompetitionInviteStatisticsResource;
import static org.innovateuk.ifs.invite.builder.ExistingUserStagedInviteListResourceBuilder.newExistingUserStagedInviteListResource;
import static org.innovateuk.ifs.invite.builder.ExistingUserStagedInviteResourceBuilder.newExistingUserStagedInviteResource;
import static org.innovateuk.ifs.invite.builder.NewUserStagedInviteListResourceBuilder.newNewUserStagedInviteListResource;
import static org.innovateuk.ifs.invite.builder.NewUserStagedInviteResourceBuilder.newNewUserStagedInviteResource;
import static org.innovateuk.ifs.invite.resource.ParticipantStatusResource.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;

public class CompetitionInviteRestServiceImplTest extends BaseRestServiceUnitTest<CompetitionInviteRestServiceImpl> {

    private static final String restUrl = "/competitioninvite";

    @Override
    protected CompetitionInviteRestServiceImpl registerRestServiceUnderTest() {
        CompetitionInviteRestServiceImpl competitionInviteRestService = new CompetitionInviteRestServiceImpl();
        return competitionInviteRestService;
    }

    @Test
    public void getAllInvitesToSend() {
        long competitionId = 1L;

        AssessorInvitesToSendResource expected = newAssessorInvitesToSendResource()
                .withRecipients(asList("James", "John"))
                .build();

        setupGetWithRestResultExpectations(format("%s/%s/%s", restUrl, "get-all-invites-to-send", competitionId), AssessorInvitesToSendResource.class, expected);
        AssessorInvitesToSendResource actual = service.getAllInvitesToSend(competitionId).getSuccess();
        assertEquals(expected, actual);
    }

    @Test
    public void getAllInvitesToResend() {
        long competitionId = 1L;
        List<Long> inviteIds = asList(1L, 2L);

        AssessorInvitesToSendResource expected = newAssessorInvitesToSendResource()
                .withRecipients(asList("James", "John"))
                .build();

        setupGetWithRestResultExpectations(format("%s/%s/%s%s", restUrl, "get-all-invites-to-resend", competitionId, "?inviteIds=1,2"), AssessorInvitesToSendResource.class, expected);
        AssessorInvitesToSendResource actual = service.getAllInvitesToResend(competitionId, inviteIds).getSuccess();
        assertEquals(expected, actual);
    }

    @Test
    public void getInviteToSend() {
        long inviteId = 1L;
        AssessorInvitesToSendResource expected = newAssessorInvitesToSendResource().build();
        setupGetWithRestResultExpectations(format("%s/%s/%s", restUrl, "get-invite-to-send", inviteId), AssessorInvitesToSendResource.class, expected);
        AssessorInvitesToSendResource actual = service.getInviteToSend(inviteId).getSuccess();
        assertEquals(expected, actual);
    }

    @Test
    public void getInvite() {
        CompetitionInviteResource expected = new CompetitionInviteResource();
        expected.setCompetitionName("my competition");
        setupGetWithRestResultAnonymousExpectations(format("%s/%s/%s", restUrl, "get-invite", "hash"), CompetitionInviteResource.class, expected);
        CompetitionInviteResource actual = service.getInvite("hash").getSuccess();
        assertEquals(expected, actual);
    }

    @Test
    public void openInvite() {
        CompetitionInviteResource expected = new CompetitionInviteResource();
        expected.setCompetitionName("my competition");
        setupPostWithRestResultAnonymousExpectations(format("%s/%s/%s", restUrl, "open-invite", "hash"), CompetitionInviteResource.class, null, expected, OK);
        CompetitionInviteResource actual = service.openInvite("hash").getSuccess();
        assertEquals(expected, actual);
    }

    @Test
    public void openInvite_hashNotExists() {
        setupPostWithRestResultAnonymousExpectations(format("%s/%s/%s", restUrl, "open-invite", "hashNotExists"), CompetitionInviteResource.class, null, null, NOT_FOUND);
        RestResult<CompetitionInviteResource> restResult = service.openInvite("hashNotExists");
        assertTrue(restResult.isFailure());
    }

    @Test
    public void acceptInvite() {
        setupPostWithRestResultExpectations(format("%s/%s/%s", restUrl, "accept-invite", "hash"), OK);
        RestResult<Void> restResult = service.acceptInvite("hash");
        assertTrue(restResult.isSuccess());
    }

    @Test
    public void rejectInvite() {
        RejectionReasonResource rejectionReasonResource = new RejectionReasonResource();
        rejectionReasonResource.setId(1L);
        CompetitionRejectionResource rejectionResource = new CompetitionRejectionResource(rejectionReasonResource, "too busy");

        setupPostWithRestResultAnonymousExpectations(format("%s/%s/%s", restUrl, "reject-invite", "hash"), Void.class, rejectionResource, null, OK);

        RestResult<Void> restResult = service.rejectInvite("hash", rejectionResource);
        assertTrue(restResult.isSuccess());
    }

    @Test
    public void checkExistingUser() {
        setupGetWithRestResultAnonymousExpectations(format("%s/%s/%s", restUrl, "check-existing-user", "hash"), Boolean.class, TRUE);
        assertTrue(service.checkExistingUser("hash").getSuccess());
    }

//    @Test
//    public void getAvailableAssessors() {
//        long competitionId = 1L;
//        int page = 1;
//        String assessorNameFilter = "Test";
//
//        List<AvailableAssessorResource> assessorItems = newAvailableAssessorResource()
//                .withName("Test")
//                .build(2);
//
//        AvailableAssessorPageResource expected = newAvailableAssessorPageResource()
//                .withContent(assessorItems)
//                .build();
//
//        setupGetWithRestResultExpectations(
//                format("%s/%s/%s1?page=1&assessorNameFilter=Test", restUrl, "get-available-assessors", competitionId),
//                AvailableAssessorPageResource.class,
//                expected
//        );
//
//        AvailableAssessorPageResource actual = service.getAvailableAssessors(competitionId, page, assessorNameFilter).getSuccess();
//        assertEquals(expected, actual);
//    }
//
//    @Test
//    public void getAvailableAssessors_noAssessorNameFilter() {
//        long competitionId = 1L;
//        int page = 1;
//        String assessorNameFilter = "";
//
//        List<AvailableAssessorResource> assessorItems = newAvailableAssessorResource().build(2);
//
//        AvailableAssessorPageResource expected = newAvailableAssessorPageResource()
//                .withContent(assessorItems)
//                .build();
//
//        setupGetWithRestResultExpectations(
//                format("%s/%s/%s?page=1", restUrl, "get-available-assessors", competitionId),
//                AvailableAssessorPageResource.class,
//                expected
//        );
//
//        AvailableAssessorPageResource actual = service.getAvailableAssessors(competitionId, page, assessorNameFilter).getSuccess();
//        assertEquals(expected, actual);
//    }
//
//    @Test
//    public void getAvailableAssessors_all() {
//        long competitionId = 1L;
//        String assessorNameFilter = "";
//
//        List<Long> assessorItems = asList(1L, 2L);
//
//        setupGetWithRestResultExpectations(
//                format("%s/%s/%s?all", restUrl, "get-available-assessors", competitionId),
//                ParameterizedTypeReferences.longsListType(),
//                assessorItems
//        );
//
//        List<Long> actual = service.getAvailableAssessorIds(competitionId, assessorNameFilter).getSuccess();
//        assertEquals(assessorItems, actual);
//    }

    @Test
    public void getCreatedInvites() {
        long competitionId = 1L;
        int page = 1;
        AssessorCreatedInvitePageResource expected = newAssessorCreatedInvitePageResource()
                .withContent(newAssessorCreatedInviteResource().build(2))
                .build();

        setupGetWithRestResultExpectations(format("%s/%s/%s?page=1", restUrl, "get-created-invites", competitionId), AssessorCreatedInvitePageResource.class, expected);

        AssessorCreatedInvitePageResource actual = service.getCreatedInvites(competitionId, page).getSuccess();
        assertEquals(expected, actual);
    }

    @Test
    public void getInvitationOverview() {
        long competitionId = 1L;
        int page = 1;
        Optional<Long> innovationArea = of(10L);
        List<ParticipantStatusResource> participantStatus = Collections.singletonList(ACCEPTED);
        Optional<Boolean> compliant = of(TRUE);

        AssessorInviteOverviewPageResource expected = newAssessorInviteOverviewPageResource().build();

        String expectedUrl = format("%s/%s/%s?page=1&innovationArea=10&statuses=ACCEPTED&compliant=true", restUrl, "get-invitation-overview", competitionId);

        setupGetWithRestResultExpectations(expectedUrl, AssessorInviteOverviewPageResource.class, expected);

        AssessorInviteOverviewPageResource actual = service.getInvitationOverview(competitionId, page, innovationArea, participantStatus, compliant)
                .getSuccess();

        assertEquals(expected, actual);
    }

    @Test
    public void getInvitationOverview_noExtraParams() {
        long competitionId = 1L;
        int page = 1;

        AssessorInviteOverviewPageResource expected = newAssessorInviteOverviewPageResource().build();

        String expectedUrl = format("%s/%s/%s?page=1&statuses=ACCEPTED,PENDING", restUrl, "get-invitation-overview", competitionId);

        setupGetWithRestResultExpectations(expectedUrl, AssessorInviteOverviewPageResource.class, expected);

        AssessorInviteOverviewPageResource actual = service.getInvitationOverview(competitionId, page, empty(), Arrays.asList(ACCEPTED, PENDING), empty())
                .getSuccess();

        assertEquals(expected, actual);
    }

    @Test
    public void getAssessorsNotAcceptedInviteIds() {
        long competitionId = 1L;
        List<Long> expected = asList(1L, 2L);
        List<ParticipantStatusResource> statuses = Arrays.asList(PENDING, REJECTED);

        String expectedUrl = format("%s/%s/%s?statuses=%s", restUrl, "get-assessors-not-accepted-invite-ids", competitionId, "PENDING,REJECTED");

        setupGetWithRestResultExpectations(expectedUrl, ParameterizedTypeReferences.longsListType(), expected);

        List<Long> actual = service.getAssessorsNotAcceptedInviteIds(competitionId, empty(), statuses, empty())
                .getSuccess();

        assertEquals(expected, actual);
    }

    @Test
    public void getInviteStatistics() {
        long competitionId = 1L;
        CompetitionInviteStatisticsResource expected = newCompetitionInviteStatisticsResource().build();
        setupGetWithRestResultExpectations(format("%s/%s/%s", restUrl, "get-invite-statistics", competitionId), CompetitionInviteStatisticsResource.class, expected);

        CompetitionInviteStatisticsResource actual = service.getInviteStatistics(competitionId).getSuccess();
        assertEquals(expected, actual);
    }

    @Test
    public void inviteUser() {
        ExistingUserStagedInviteResource existingUserStagesInviteResource = new ExistingUserStagedInviteResource(2L, 1L);
        CompetitionInviteResource expected = newCompetitionInviteResource().build();

        setupPostWithRestResultExpectations(format("%s/%s", restUrl, "invite-user"), CompetitionInviteResource.class, existingUserStagesInviteResource, expected, OK);

        CompetitionInviteResource actual = service.inviteUser(existingUserStagesInviteResource).getSuccess();
        assertEquals(expected, actual);
    }

    @Test
    public void inviteNewUsers() {
        long competitionId = 1L;

        NewUserStagedInviteListResource newUserStagedInviteListResource = newNewUserStagedInviteListResource()
                .withInvites(
                        newNewUserStagedInviteResource()
                                .withName("Tester 1", "Tester 2")
                                .withEmail("test1@test.com", "test2@test.com")
                                .withInnovationAreaId(1L)
                                .build(2)
                )
                .build();

        setupPostWithRestResultExpectations(format("%s/%s/%s", restUrl, "invite-new-users", competitionId), newUserStagedInviteListResource, OK);

        RestResult<Void> restResult = service.inviteNewUsers(newUserStagedInviteListResource, competitionId);
        assertTrue(restResult.isSuccess());
    }

    @Test
    public void inviteUsers() {
        long competitionId = 1L;

        ExistingUserStagedInviteListResource existingUserStagedInviteListResource = newExistingUserStagedInviteListResource()
                .withInvites(
                        newExistingUserStagedInviteResource()
                                .withUserId(1L, 2L)
                                .withCompetitionId(competitionId)
                                .build(2)
                )
                .build();

        setupPostWithRestResultExpectations(format("%s/%s", restUrl, "invite-users"), existingUserStagedInviteListResource, OK);

        RestResult<Void> restResult = service.inviteUsers(existingUserStagedInviteListResource);
        assertTrue(restResult.isSuccess());
    }

    @Test
    public void deleteInvite() {
        String email = "firstname.lastname@example.com";
        long competitionId = 1L;

        setupDeleteWithRestResultExpectations(format("%s/%s?competitionId=%s&email=%s", restUrl, "delete-invite", competitionId, email));

        RestResult<Void> resultResult = service.deleteInvite(email, competitionId);
        assertTrue(resultResult.isSuccess());
    }

    @Test
    public void deleteAllInvites() {
        long competitionId = 1L;

        setupDeleteWithRestResultExpectations(format("%s/%s?competitionId=%s", restUrl, "delete-all-invites", competitionId));

        RestResult<Void> resultResult = service.deleteAllInvites(competitionId);
        assertTrue(resultResult.isSuccess());
    }

    @Test
    public void sendAllInvites() {
        long competitionId = 1L;
        AssessorInviteSendResource assessorInviteSendResource = newAssessorInviteSendResource()
                .withSubject("subject")
                .withContent("content")
                .build();

        setupPostWithRestResultExpectations(format("%s/%s/%s", restUrl, "send-all-invites", competitionId), assessorInviteSendResource, OK);

        assertTrue(service.sendAllInvites(competitionId, assessorInviteSendResource).isSuccess());
    }

    @Test
    public void resendInvite() {
        long inviteId = 5L;
        AssessorInviteSendResource assessorInviteSendResource = newAssessorInviteSendResource()
                .withSubject("subject")
                .withContent("content")
                .build();

        setupPostWithRestResultExpectations(format("%s/%s/%s", restUrl, "resend-invite", inviteId), assessorInviteSendResource, OK);

        assertTrue(service.resendInvite(inviteId, assessorInviteSendResource).isSuccess());
    }

    @Test
    public void resendInvites() {
        List<Long> inviteIds = asList(1L, 2L);
        AssessorInviteSendResource assessorInviteSendResource = newAssessorInviteSendResource()
                .withSubject("subject")
                .withContent("content")
                .build();

        setupPostWithRestResultExpectations(format("%s/%s%s", restUrl, "resend-invites", "?inviteIds=1,2"), assessorInviteSendResource, OK);

        assertTrue(service.resendInvites(inviteIds, assessorInviteSendResource).isSuccess());
    }
}
