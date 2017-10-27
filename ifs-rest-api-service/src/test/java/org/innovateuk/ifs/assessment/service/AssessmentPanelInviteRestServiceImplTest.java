package org.innovateuk.ifs.assessment.service;

import org.innovateuk.ifs.BaseRestServiceUnitTest;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.ParameterizedTypeReferences;
import org.innovateuk.ifs.invite.resource.*;
import org.junit.Test;
import java.util.Arrays;
import java.util.List;
import static java.lang.Boolean.TRUE;
import static java.lang.String.format;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.invite.builder.AssessmentPanelParticipantResourceBuilder.newAssessmentPanelParticipantResource;
import static org.innovateuk.ifs.invite.builder.AssessorCreatedInvitePageResourceBuilder.newAssessorCreatedInvitePageResource;
import static org.innovateuk.ifs.invite.builder.AssessorCreatedInviteResourceBuilder.newAssessorCreatedInviteResource;
import static org.innovateuk.ifs.invite.builder.AssessorInviteOverviewPageResourceBuilder.newAssessorInviteOverviewPageResource;
import static org.innovateuk.ifs.invite.builder.AssessorInviteSendResourceBuilder.newAssessorInviteSendResource;
import static org.innovateuk.ifs.invite.builder.AssessorInvitesToSendResourceBuilder.newAssessorInvitesToSendResource;
import static org.innovateuk.ifs.invite.builder.AvailableAssessorPageResourceBuilder.newAvailableAssessorPageResource;
import static org.innovateuk.ifs.invite.builder.AvailableAssessorResourceBuilder.newAvailableAssessorResource;
import static org.innovateuk.ifs.invite.builder.ExistingUserStagedInviteListResourceBuilder.newExistingUserStagedInviteListResource;
import static org.innovateuk.ifs.invite.builder.ExistingUserStagedInviteResourceBuilder.newExistingUserStagedInviteResource;
import static org.innovateuk.ifs.invite.resource.CompetitionParticipantRoleResource.PANEL_ASSESSOR;
import static org.innovateuk.ifs.invite.resource.ParticipantStatusResource.ACCEPTED;
import static org.innovateuk.ifs.invite.resource.ParticipantStatusResource.PENDING;
import static org.innovateuk.ifs.invite.constant.InviteStatus.SENT;
import static org.innovateuk.ifs.user.builder.AssessmentPanelInviteResourceBuilder.newAssessmentPanelInviteResource;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;

public class AssessmentPanelInviteRestServiceImplTest extends BaseRestServiceUnitTest<AssessmentPanelInviteRestServiceImpl> {

    private static final String restUrl = "/assessmentpanelinvite";

    @Override
    protected AssessmentPanelInviteRestServiceImpl registerRestServiceUnderTest() {
        AssessmentPanelInviteRestServiceImpl assessmentPanelInviteRestService = new AssessmentPanelInviteRestServiceImpl();
        return assessmentPanelInviteRestService;
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
                format("%s/%s/%s?page=1", restUrl, "getAvailableAssessors", competitionId),
                AvailableAssessorPageResource.class,
                expected
        );

        AvailableAssessorPageResource actual = service.getAvailableAssessors(competitionId, page).getSuccessObject();
        assertEquals(expected, actual);
    }

    @Test
    public void getAvailableAssessorIds() throws Exception {
        long competitionId = 1L;

        List<Long> assessorItems = asList(1L, 2L);

        setupGetWithRestResultExpectations(
                format("%s/%s/%s", restUrl, "getAvailableAssessorIds", competitionId),
                ParameterizedTypeReferences.longsListType(),
                assessorItems
        );

        List<Long> actual = service.getAvailableAssessorIds(competitionId).getSuccessObject();
        assertEquals(assessorItems, actual);
    }

    @Test
    public void getCreatedInvites() throws Exception {
        long competitionId = 1L;
        int page = 1;
        AssessorCreatedInvitePageResource expected = newAssessorCreatedInvitePageResource()
                .withContent(newAssessorCreatedInviteResource().build(2))
                .build();

        setupGetWithRestResultExpectations(format("%s/%s/%s?page=1", restUrl, "getCreatedInvites", competitionId), AssessorCreatedInvitePageResource.class, expected);

        AssessorCreatedInvitePageResource actual = service.getCreatedInvites(competitionId, page).getSuccessObject();
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

        setupPostWithRestResultExpectations(format("%s/%s", restUrl, "inviteUsers"), existingUserStagedInviteListResource, OK);

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

        setupPostWithRestResultExpectations(format("%s/%s/%s", restUrl, "sendAllInvites", competitionId), assessorInviteSendResource, OK);
        assertTrue(service.sendAllInvites(competitionId, assessorInviteSendResource).isSuccess());
        setupPostWithRestResultVerifications(format("%s/%s/%s", restUrl, "sendAllInvites", competitionId), Void.class, assessorInviteSendResource);
    }

    @Test
    public void getAllInvitesToResend() throws Exception {
        long competitionId = 1L;
        List<Long> inviteIds = asList(1L, 2L);

        AssessorInvitesToSendResource expected = newAssessorInvitesToSendResource()
                .withRecipients(asList("James", "John"))
                .build();

        setupGetWithRestResultExpectations(format("%s/%s/%s%s", restUrl, "getAllInvitesToResend", competitionId, "?inviteIds=1,2"), AssessorInvitesToSendResource.class, expected);
        AssessorInvitesToSendResource actual = service.getAllInvitesToResend(competitionId, inviteIds).getSuccessObject();
        assertEquals(expected, actual);
    }

    @Test
    public void getAllInvitesToSend() throws Exception {
        long competitionId = 1L;

        AssessorInvitesToSendResource expected = newAssessorInvitesToSendResource()
                .withRecipients(asList("James", "John"))
                .build();

        setupGetWithRestResultExpectations(format("%s/%s/%s", restUrl, "getAllInvitesToSend", competitionId), AssessorInvitesToSendResource.class, expected);
        AssessorInvitesToSendResource actual = service.getAllInvitesToSend(competitionId).getSuccessObject();
        assertEquals(expected, actual);
    }

    @Test
    public void resendInvites() {
        List<Long> inviteIds = asList(1L, 2L);
        AssessorInviteSendResource assessorInviteSendResource = newAssessorInviteSendResource()
                .withSubject("subject")
                .withContent("content")
                .build();

        setupPostWithRestResultExpectations(format("%s/%s%s", restUrl, "resendInvites", "?inviteIds=1,2"), assessorInviteSendResource, OK);

        assertTrue(service.resendInvites(inviteIds, assessorInviteSendResource).isSuccess());
    }

    @Test
    public void getInvitationOverview() throws Exception {
        long competitionId = 1L;
        int page = 1;

        AssessorInviteOverviewPageResource expected = newAssessorInviteOverviewPageResource().build();

        String expectedUrl = format("%s/%s/%s?page=1&statuses=ACCEPTED,PENDING", restUrl, "getInvitationOverview", competitionId);

        setupGetWithRestResultExpectations(expectedUrl, AssessorInviteOverviewPageResource.class, expected);

        AssessorInviteOverviewPageResource actual = service.getInvitationOverview(competitionId, page, Arrays.asList(ACCEPTED, PENDING))
                .getSuccessObject();

        assertEquals(expected, actual);
    }

    @Test
    public void getAllInvitesByUser() throws Exception {
        long userId = 1L;
        AssessmentPanelParticipantResource assessmentPanelParticipantResource = newAssessmentPanelParticipantResource()
                .withUser(userId)
                .withCompetitionParticipantRole(PANEL_ASSESSOR)
                .withCompetitionName("Competition Name")
                .build();
        List<AssessmentPanelParticipantResource> expected = singletonList(assessmentPanelParticipantResource);

        setupGetWithRestResultExpectations(format("%s/%s/%s", restUrl, "getAllInvitesByUser", userId), ParameterizedTypeReferences.assessmentPanelParticipantResourceListType(), expected, OK);

        List<AssessmentPanelParticipantResource> actual = service.getAllInvitesByUser(userId).getSuccessObjectOrThrowException();
        assertEquals(expected, actual);
    }

    @Test
    public void openInvite() {
        AssessmentPanelInviteResource expected = newAssessmentPanelInviteResource().build();
        expected.setCompetitionName("my competition");
        setupPostWithRestResultAnonymousExpectations(format("%s/%s/%s", restUrl, "openInvite", "hash"), AssessmentPanelInviteResource.class, null, expected, OK);
        AssessmentPanelInviteResource actual = service.openInvite("hash").getSuccessObject();
        assertEquals(expected, actual);
    }

    @Test
    public void openInvite_hashNotExists() {
        setupPostWithRestResultAnonymousExpectations(format("%s/%s/%s", restUrl, "openInvite", "hashNotExists"), AssessmentPanelInviteResource.class, null, null, NOT_FOUND);
        RestResult<AssessmentPanelInviteResource> restResult = service.openInvite("hashNotExists");
        assertTrue(restResult.isFailure());
    }

    @Test
    public void acceptInvite() {
        setupPostWithRestResultExpectations(format("%s/%s/%s", restUrl, "acceptInvite", "hash"), OK);
        RestResult<Void> restResult = service.acceptInvite("hash");
        assertTrue(restResult.isSuccess());
    }

    @Test
    public void rejectInvite() {
        setupPostWithRestResultAnonymousExpectations(format("%s/%s/%s", restUrl, "rejectInvite", "hash"), Void.class, null, null, OK);

        RestResult<Void> restResult = service.rejectInvite("hash");
        assertTrue(restResult.isSuccess());
    }

    @Test
    public void checkExistingUser() {
        setupGetWithRestResultAnonymousExpectations(format("%s/%s/%s", restUrl, "checkExistingUser", "hash"), Boolean.class, TRUE);
        assertTrue(service.checkExistingUser("hash").getSuccessObject());
    }

    @Test
    public void deleteInvite() {
        String email = "firstname.lastname@example.com";
        long competitionId = 1L;

        setupDeleteWithRestResultExpectations(format("%s/%s?competitionId=%s&email=%s", restUrl, "deleteInvite", competitionId, email));

        RestResult<Void> resultResult = service.deleteInvite(email, competitionId);
        assertTrue(resultResult.isSuccess());
    }

    @Test
    public void deleteAllInvites() throws Exception {
        long competitionId = 1L;

        setupDeleteWithRestResultExpectations(format("%s/%s?competitionId=%s", restUrl, "deleteAllInvites", competitionId));

        RestResult<Void> resultResult = service.deleteAllInvites(competitionId);
        assertTrue(resultResult.isSuccess());
    }

}
