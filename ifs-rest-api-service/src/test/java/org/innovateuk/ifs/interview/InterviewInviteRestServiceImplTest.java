package org.innovateuk.ifs.interview;

import org.innovateuk.ifs.BaseRestServiceUnitTest;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.ParameterizedTypeReferences;
import org.innovateuk.ifs.interview.service.InterviewInviteRestServiceImpl;
import org.innovateuk.ifs.invite.resource.*;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static java.lang.Boolean.TRUE;
import static java.lang.String.format;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.interview.builder.InterviewInviteResourceBuilder.newInterviewInviteResource;
import static org.innovateuk.ifs.interview.builder.InterviewParticipantResourceBuilder.newInterviewParticipantResource;
import static org.innovateuk.ifs.invite.builder.AssessorCreatedInvitePageResourceBuilder.newAssessorCreatedInvitePageResource;
import static org.innovateuk.ifs.invite.builder.AssessorCreatedInviteResourceBuilder.newAssessorCreatedInviteResource;
import static org.innovateuk.ifs.invite.builder.AssessorInviteOverviewPageResourceBuilder.newAssessorInviteOverviewPageResource;
import static org.innovateuk.ifs.invite.builder.AssessorInviteSendResourceBuilder.newAssessorInviteSendResource;
import static org.innovateuk.ifs.invite.builder.AssessorInvitesToSendResourceBuilder.newAssessorInvitesToSendResource;
import static org.innovateuk.ifs.invite.builder.AvailableAssessorPageResourceBuilder.newAvailableAssessorPageResource;
import static org.innovateuk.ifs.invite.builder.AvailableAssessorResourceBuilder.newAvailableAssessorResource;
import static org.innovateuk.ifs.invite.builder.ExistingUserStagedInviteListResourceBuilder.newExistingUserStagedInviteListResource;
import static org.innovateuk.ifs.invite.builder.ExistingUserStagedInviteResourceBuilder.newExistingUserStagedInviteResource;
import static org.innovateuk.ifs.invite.resource.CompetitionParticipantRoleResource.INTERVIEW_ASSESSOR;
import static org.innovateuk.ifs.invite.resource.ParticipantStatusResource.ACCEPTED;
import static org.innovateuk.ifs.invite.resource.ParticipantStatusResource.PENDING;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;

public class InterviewInviteRestServiceImplTest extends BaseRestServiceUnitTest<InterviewInviteRestServiceImpl> {

    private static final String restUrl = "/interview-panel-invite";

    @Override
    protected InterviewInviteRestServiceImpl registerRestServiceUnderTest() {
        InterviewInviteRestServiceImpl interviewPanelInviteRestService = new InterviewInviteRestServiceImpl();
        return interviewPanelInviteRestService;
    }

    @Test
    public void getAvailableAssessors() throws Exception {
        long competitionId = 1L;
        int page = 1;

        List<AvailableAssessorResource> assessorItems = newAvailableAssessorResource().build(2);

        AvailableAssessorPageResource expected = newAvailableAssessorPageResource()
                .withContent(assessorItems)
                .build();

        setupGetWithRestResultExpectations(
                format("%s/%s/%s?page=1", restUrl, "get-available-assessors", competitionId),
                AvailableAssessorPageResource.class,
                expected
        );

        AvailableAssessorPageResource actual = service.getAvailableAssessors(competitionId, page).getSuccess();
        assertEquals(expected, actual);
    }

    @Test
    public void getAvailableAssessorIds() throws Exception {
        long competitionId = 1L;

        List<Long> assessorItems = asList(1L, 2L);

        setupGetWithRestResultExpectations(
                format("%s/%s/%s", restUrl, "get-available-assessor-ids", competitionId),
                ParameterizedTypeReferences.longsListType(),
                assessorItems
        );

        List<Long> actual = service.getAvailableAssessorIds(competitionId).getSuccess();
        assertEquals(assessorItems, actual);
    }

    @Test
    public void getCreatedInvites() throws Exception {
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
    public void inviteUsers() throws Exception {
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
    public void sendAllInvites() {
        long competitionId = 1L;
        AssessorInviteSendResource assessorInviteSendResource = newAssessorInviteSendResource()
                .withSubject("subject")
                .withContent("content")
                .build();

        setupPostWithRestResultExpectations(format("%s/%s/%s", restUrl, "send-all-invites", competitionId), assessorInviteSendResource, OK);
        assertTrue(service.sendAllInvites(competitionId, assessorInviteSendResource).isSuccess());
        setupPostWithRestResultVerifications(format("%s/%s/%s", restUrl, "send-all-invites", competitionId), Void.class, assessorInviteSendResource);
    }

    @Test
    public void getAllInvitesToResend() throws Exception {
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
    public void getAllInvitesToSend() throws Exception {
        long competitionId = 1L;

        AssessorInvitesToSendResource expected = newAssessorInvitesToSendResource()
                .withRecipients(asList("James", "John"))
                .build();

        setupGetWithRestResultExpectations(format("%s/%s/%s", restUrl, "get-all-invites-to-send", competitionId), AssessorInvitesToSendResource.class, expected);
        AssessorInvitesToSendResource actual = service.getAllInvitesToSend(competitionId).getSuccess();
        assertEquals(expected, actual);
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

    @Test
    public void getInvitationOverview() throws Exception {
        long competitionId = 1L;
        int page = 1;

        AssessorInviteOverviewPageResource expected = newAssessorInviteOverviewPageResource().build();

        String expectedUrl = format("%s/%s/%s?page=1&statuses=ACCEPTED,PENDING", restUrl, "get-invitation-overview", competitionId);

        setupGetWithRestResultExpectations(expectedUrl, AssessorInviteOverviewPageResource.class, expected);

        AssessorInviteOverviewPageResource actual = service.getInvitationOverview(competitionId, page, Arrays.asList(ACCEPTED, PENDING))
                .getSuccess();

        assertEquals(expected, actual);
    }

    @Test
    public void getAllInvitesByUser() throws Exception {
        long userId = 1L;
        InterviewParticipantResource interviewParticipantResource = newInterviewParticipantResource()
                .withUser(userId)
                .withCompetitionParticipantRole(INTERVIEW_ASSESSOR)
                .withCompetitionName("Competition Name")
                .build();
        List<InterviewParticipantResource> expected = singletonList(interviewParticipantResource);

        setupGetWithRestResultExpectations(format("%s/%s/%s", restUrl, "get-all-invites-by-user", userId), ParameterizedTypeReferences.assessmentInterviewPanelParticipantResourceListType(), expected, OK);

        List<InterviewParticipantResource> actual = service.getAllInvitesByUser(userId).getSuccess();
        assertEquals(expected, actual);
    }

    @Test
    public void openInvite() {
        InterviewInviteResource expected = newInterviewInviteResource().build();
        expected.setCompetitionName("my competition");
        setupPostWithRestResultAnonymousExpectations(format("%s/%s/%s", restUrl, "open-invite", "hash"), InterviewInviteResource.class, null, expected, OK);
        InterviewInviteResource actual = service.openInvite("hash").getSuccess();
        assertEquals(expected, actual);
    }

    @Test
    public void openInvite_hashNotExists() {
        setupPostWithRestResultAnonymousExpectations(format("%s/%s/%s", restUrl, "open-invite", "hashNotExists"), InterviewInviteResource.class, null, null, NOT_FOUND);
        RestResult<InterviewInviteResource> restResult = service.openInvite("hashNotExists");
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
        setupPostWithRestResultAnonymousExpectations(format("%s/%s/%s", restUrl, "reject-invite", "hash"), Void.class, null, null, OK);

        RestResult<Void> restResult = service.rejectInvite("hash");
        assertTrue(restResult.isSuccess());
    }

    @Test
    public void checkExistingUser() {
        setupGetWithRestResultAnonymousExpectations(format("%s/%s/%s", restUrl, "check-existing-user", "hash"), Boolean.class, TRUE);
        assertTrue(service.checkExistingUser("hash").getSuccess());
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
    public void deleteAllInvites() throws Exception {
        long competitionId = 1L;

        setupDeleteWithRestResultExpectations(format("%s/%s?competitionId=%s", restUrl, "delete-all-invites", competitionId));

        RestResult<Void> resultResult = service.deleteAllInvites(competitionId);
        assertTrue(resultResult.isSuccess());
    }

}
