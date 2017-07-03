package org.innovateuk.ifs.assessment.service;

import org.innovateuk.ifs.BaseRestServiceUnitTest;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.ParameterizedTypeReferences;
import org.innovateuk.ifs.invite.resource.*;
import org.junit.Test;

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
import static org.innovateuk.ifs.invite.builder.AvailableAssessorPageResourceBuilder.newAvailableAssessorPageResource;
import static org.innovateuk.ifs.invite.builder.AvailableAssessorResourceBuilder.newAvailableAssessorResource;
import static org.innovateuk.ifs.invite.builder.CompetitionInviteStatisticsResourceBuilder.newCompetitionInviteStatisticsResource;
import static org.innovateuk.ifs.invite.builder.ExistingUserStagedInviteListResourceBuilder.newExistingUserStagedInviteListResource;
import static org.innovateuk.ifs.invite.builder.ExistingUserStagedInviteResourceBuilder.newExistingUserStagedInviteResource;
import static org.innovateuk.ifs.invite.builder.NewUserStagedInviteListResourceBuilder.newNewUserStagedInviteListResource;
import static org.innovateuk.ifs.invite.builder.NewUserStagedInviteResourceBuilder.newNewUserStagedInviteResource;
import static org.innovateuk.ifs.invite.resource.ParticipantStatusResource.ACCEPTED;
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
    public void getAllInvitesToSend() throws Exception {
        long competitionId = 1L;

        AssessorInvitesToSendResource expected = newAssessorInvitesToSendResource()
                .withRecipients(asList("James", "John"))
                .build();

        setupGetWithRestResultExpectations(format("%s/%s/%s", restUrl, "getAllInvitesToSend", competitionId), AssessorInvitesToSendResource.class, expected);
        AssessorInvitesToSendResource actual = service.getAllInvitesToSend(competitionId).getSuccessObject();
    }

    @Test
    public void getInviteToSend() throws Exception {
        long inviteId = 1L;
        AssessorInvitesToSendResource expected = newAssessorInvitesToSendResource().build();
        setupGetWithRestResultExpectations(format("%s/%s/%s", restUrl, "getInviteToSend", inviteId), AssessorInvitesToSendResource.class, expected);
        AssessorInvitesToSendResource actual = service.getInviteToSend(inviteId).getSuccessObject();
        assertEquals(expected, actual);
    }

    @Test
    public void getInvite() throws Exception {
        CompetitionInviteResource expected = new CompetitionInviteResource();
        expected.setCompetitionName("my competition");
        setupGetWithRestResultAnonymousExpectations(format("%s/%s/%s", restUrl, "getInvite", "hash"), CompetitionInviteResource.class, expected);
        CompetitionInviteResource actual = service.getInvite("hash").getSuccessObject();
        assertEquals(expected, actual);
    }

    @Test
    public void openInvite() {
        CompetitionInviteResource expected = new CompetitionInviteResource();
        expected.setCompetitionName("my competition");
        setupPostWithRestResultAnonymousExpectations(format("%s/%s/%s", restUrl, "openInvite", "hash"), CompetitionInviteResource.class, null, expected, OK);
        CompetitionInviteResource actual = service.openInvite("hash").getSuccessObject();
        assertEquals(expected, actual);
    }

    @Test
    public void openInvite_hashNotExists() {
        setupPostWithRestResultAnonymousExpectations(format("%s/%s/%s", restUrl, "openInvite", "hashNotExists"), CompetitionInviteResource.class, null, null, NOT_FOUND);
        RestResult<CompetitionInviteResource> restResult = service.openInvite("hashNotExists");
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
        RejectionReasonResource rejectionReasonResource = new RejectionReasonResource();
        rejectionReasonResource.setId(1L);
        CompetitionRejectionResource rejectionResource = new CompetitionRejectionResource(rejectionReasonResource, "too busy");

        setupPostWithRestResultAnonymousExpectations(format("%s/%s/%s", restUrl, "rejectInvite", "hash"), Void.class, rejectionResource, null, OK);

        RestResult<Void> restResult = service.rejectInvite("hash", rejectionResource);
        assertTrue(restResult.isSuccess());
    }

    @Test
    public void checkExistingUser() {
        setupGetWithRestResultAnonymousExpectations(format("%s/%s/%s", restUrl, "checkExistingUser", "hash"), Boolean.class, TRUE);
        assertTrue(service.checkExistingUser("hash").getSuccessObject());
    }

    @Test
    public void getAvailableAssessors() throws Exception {
        long competitionId = 1L;
        int page = 1;
        Optional<Long> innovationArea = of(2L);

        List<AvailableAssessorResource> assessorItems = newAvailableAssessorResource().build(2);

        AvailableAssessorPageResource expected = newAvailableAssessorPageResource()
                .withContent(assessorItems)
                .build();

        setupGetWithRestResultExpectations(
                format("%s/%s/%s?page=1&innovationArea=2", restUrl, "getAvailableAssessors", competitionId),
                AvailableAssessorPageResource.class,
                expected
        );

        AvailableAssessorPageResource actual = service.getAvailableAssessors(competitionId, page, innovationArea).getSuccessObject();
        assertEquals(expected, actual);
    }

    @Test
    public void getAvailableAssessors_noInnovationArea() throws Exception {
        long competitionId = 1L;
        int page = 1;
        Optional<Long> innovationArea = empty();

        List<AvailableAssessorResource> assessorItems = newAvailableAssessorResource().build(2);

        AvailableAssessorPageResource expected = newAvailableAssessorPageResource()
                .withContent(assessorItems)
                .build();

        setupGetWithRestResultExpectations(
                format("%s/%s/%s?page=1", restUrl, "getAvailableAssessors", competitionId),
                AvailableAssessorPageResource.class,
                expected
        );

        AvailableAssessorPageResource actual = service.getAvailableAssessors(competitionId, page, innovationArea).getSuccessObject();
        assertEquals(expected, actual);
    }

    @Test
    public void getAvailableAssessors_all() throws Exception {
        long competitionId = 1L;
        Optional<Long> innovationArea = of(2L);

        List<Long> assessorItems = asList(1L, 2L);

        setupGetWithRestResultExpectations(
                format("%s/%s/%s?all&innovationArea=2", restUrl, "getAvailableAssessors", competitionId),
                ParameterizedTypeReferences.longsListType(),
                assessorItems
        );

        List<Long> actual = service.getAvailableAssessorIds(competitionId, innovationArea).getSuccessObject();
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
    public void getInvitationOverview() throws Exception {
        long competitionId = 1L;
        int page = 5;
        Optional<Long> innovationArea = of(10L);
        Optional<ParticipantStatusResource> participantStatus = of(ACCEPTED);
        Optional<Boolean> compliant = of(TRUE);

        AssessorInviteOverviewPageResource expected = newAssessorInviteOverviewPageResource().build();

        String expectedUrl = format("%s/%s/%s?page=5&innovationArea=10&status=ACCEPTED&compliant=true", restUrl, "getInvitationOverview", competitionId);

        setupGetWithRestResultExpectations(expectedUrl, AssessorInviteOverviewPageResource.class, expected);

        AssessorInviteOverviewPageResource actual = service.getInvitationOverview(competitionId, page, innovationArea, participantStatus, compliant)
                .getSuccessObject();

        assertEquals(expected, actual);
    }

    @Test
    public void getInvitationOverview_noExtraParams() throws Exception {
        long competitionId = 1L;
        int page = 5;

        AssessorInviteOverviewPageResource expected = newAssessorInviteOverviewPageResource().build();

        String expectedUrl = format("%s/%s/%s?page=5", restUrl, "getInvitationOverview", competitionId);

        setupGetWithRestResultExpectations(expectedUrl, AssessorInviteOverviewPageResource.class, expected);

        AssessorInviteOverviewPageResource actual = service.getInvitationOverview(competitionId, page, empty(), empty(), empty())
                .getSuccessObject();

        assertEquals(expected, actual);
    }

    @Test
    public void getInviteStatistics() throws Exception {
        long competitionId = 1L;
        CompetitionInviteStatisticsResource expected = newCompetitionInviteStatisticsResource().build();
        setupGetWithRestResultExpectations(format("%s/%s/%s", restUrl, "getInviteStatistics", competitionId), CompetitionInviteStatisticsResource.class, expected);

        CompetitionInviteStatisticsResource actual = service.getInviteStatistics(competitionId).getSuccessObject();
        assertEquals(expected, actual);
    }

    @Test
    public void inviteUser() {
        ExistingUserStagedInviteResource existingUserStagesInviteResource = new ExistingUserStagedInviteResource(2L, 1L);
        CompetitionInviteResource expected = newCompetitionInviteResource().build();

        setupPostWithRestResultExpectations(format("%s/%s", restUrl, "inviteUser"), CompetitionInviteResource.class, existingUserStagesInviteResource, expected, OK);

        CompetitionInviteResource actual = service.inviteUser(existingUserStagesInviteResource).getSuccessObject();
        assertEquals(expected, actual);
    }

    @Test
    public void inviteNewUsers() throws Exception {
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

        setupPostWithRestResultExpectations(format("%s/%s/%s", restUrl, "inviteNewUsers", competitionId), newUserStagedInviteListResource, OK);

        RestResult<Void> restResult = service.inviteNewUsers(newUserStagedInviteListResource, competitionId);
        assertTrue(restResult.isSuccess());
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

    @Test
    public void sendAllInvites() {
        long competitionId = 1L;
        AssessorInviteSendResource assessorInviteSendResource = newAssessorInviteSendResource()
                .withSubject("subject")
                .withContent("content")
                .build();

        setupPostWithRestResultExpectations(format("%s/%s/%s", restUrl, "sendAllInvites", competitionId), assessorInviteSendResource, OK);

        assertTrue(service.sendAllInvites(competitionId, assessorInviteSendResource).isSuccess());
    }

    @Test
    public void resendInvite() {
        long inviteId = 5L;
        AssessorInviteSendResource assessorInviteSendResource = newAssessorInviteSendResource()
                .withSubject("subject")
                .withContent("content")
                .build();

        setupPostWithRestResultExpectations(format("%s/%s/%s", restUrl, "resendInvite", inviteId), assessorInviteSendResource, OK);

        assertTrue(service.resendInvite(inviteId, assessorInviteSendResource).isSuccess());
    }
}
