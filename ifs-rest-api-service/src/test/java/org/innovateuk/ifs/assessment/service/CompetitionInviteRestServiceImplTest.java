package org.innovateuk.ifs.assessment.service;

import org.innovateuk.ifs.BaseRestServiceUnitTest;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.email.resource.EmailContent;
import org.innovateuk.ifs.invite.resource.*;
import org.junit.Test;

import java.util.List;

import static java.lang.String.format;
import static org.innovateuk.ifs.assessment.builder.CompetitionInviteResourceBuilder.newCompetitionInviteResource;
import static org.innovateuk.ifs.commons.service.ParameterizedTypeReferences.*;
import static org.innovateuk.ifs.email.builders.EmailContentResourceBuilder.newEmailContentResource;
import static org.innovateuk.ifs.invite.builder.AssessorCreatedInviteResourceBuilder.newAssessorCreatedInviteResource;
import static org.innovateuk.ifs.invite.builder.AssessorInviteOverviewResourceBuilder.newAssessorInviteOverviewResource;
import static org.innovateuk.ifs.invite.builder.AssessorInviteToSendResourceBuilder.newAssessorInviteToSendResource;
import static org.innovateuk.ifs.invite.builder.AvailableAssessorResourceBuilder.newAvailableAssessorResource;
import static org.innovateuk.ifs.invite.builder.NewUserStagedInviteListResourceBuilder.newNewUserStagedInviteListResource;
import static org.innovateuk.ifs.invite.builder.NewUserStagedInviteResourceBuilder.newNewUserStagedInviteResource;
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
    public void getCreatedInvite() throws Exception {
        long inviteId = 1L;
        AssessorInviteToSendResource expected = newAssessorInviteToSendResource().build();
        setupGetWithRestResultExpectations(format("%s/%s/%s", restUrl, "getCreated", inviteId), AssessorInviteToSendResource.class, expected);
        AssessorInviteToSendResource actual = service.getCreated(inviteId).getSuccessObject();
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
        setupGetWithRestResultAnonymousExpectations(format("%s/%s/%s", restUrl, "checkExistingUser", "hash"), Boolean.class, Boolean.TRUE);
        assertTrue(service.checkExistingUser("hash").getSuccessObject());
    }

    @Test
    public void getAvailableAssessors() throws Exception {
        long competitionId = 1L;
        List<AvailableAssessorResource> expected = newAvailableAssessorResource().build(2);

        setupGetWithRestResultExpectations(format("%s/%s/%s", restUrl, "getAvailableAssessors", competitionId), availableAssessorResourceListType(), expected);

        List<AvailableAssessorResource> actual = service.getAvailableAssessors(competitionId).getSuccessObject();
        assertEquals(expected, actual);
    }

    @Test
    public void getCreatedInvites() throws Exception {
        long competitionId = 1L;
        List<AssessorCreatedInviteResource> expected = newAssessorCreatedInviteResource().build(2);

        setupGetWithRestResultExpectations(format("%s/%s/%s", restUrl, "getCreatedInvites", competitionId), assessorCreatedInviteResourceListType(), expected);

        List<AssessorCreatedInviteResource> actual = service.getCreatedInvites(competitionId).getSuccessObject();
        assertEquals(expected, actual);
    }

    @Test
    public void getInvitationOverview() throws Exception {
        long competitionId = 1L;
        List<AssessorInviteOverviewResource> expected = newAssessorInviteOverviewResource().build(2);

        setupGetWithRestResultExpectations(format("%s/%s/%s", restUrl, "getInvitationOverview", competitionId), assessorInviteOverviewResourceListType(), expected);

        List<AssessorInviteOverviewResource> actual = service.getInvitationOverview(competitionId).getSuccessObject();
        assertEquals(expected, actual);
    }

    @Test
    public void inviteUser() {
        ExistingUserStagedInviteResource existingUserStagesInviteResource = new ExistingUserStagedInviteResource("firstname.example@example.com", 1L);
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
    public void deleteInvite() {
        String email = "firstname.lastname@example.com";
        long competitionId = 1L;

        setupDeleteWithRestResultExpectations(format("%s/%s?competitionId=%s&email=%s", restUrl, "deleteInvite", competitionId, email));

        RestResult<Void> resultResult = service.deleteInvite(email, competitionId);
        assertTrue(resultResult.isSuccess());
    }

    @Test
    public void sendInvite() {
        long inviteId = 5L;
        AssessorInviteToSendResource expected = newAssessorInviteToSendResource().build();
        EmailContent email = newEmailContentResource().build();
        setupPostWithRestResultExpectations(format("%s/%s/%s", restUrl, "sendInvite", inviteId), AssessorInviteToSendResource.class, email, expected, OK);

        AssessorInviteToSendResource actual = service.sendInvite(inviteId, email).getSuccessObject();
        assertEquals(expected, actual);
    }
}
