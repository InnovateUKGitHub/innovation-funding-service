package com.worth.ifs.assessment.service;

import com.worth.ifs.BaseRestServiceUnitTest;
import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.invite.resource.*;
import org.junit.Test;

import java.util.List;

import static com.worth.ifs.assessment.builder.CompetitionInviteResourceBuilder.newCompetitionInviteResource;
import static com.worth.ifs.commons.service.ParameterizedTypeReferences.availableAssessorResourceListType;
import static com.worth.ifs.invite.builder.AvailableAssessorResourceBuilder.newAvailableAssessorResource;
import static com.worth.ifs.util.MapFunctions.asMap;
import static java.lang.String.format;
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
    public void getInvite() throws Exception {
        CompetitionInviteResource expected = new CompetitionInviteResource();
        expected.setCompetitionName("my competition");
        setupGetWithRestResultAnonymousExpectations(format("%s/%s/%s", restUrl, "/getInvite", "hash"), CompetitionInviteResource.class, expected);
        CompetitionInviteResource actual = service.getInvite("hash").getSuccessObject();
        assertEquals(expected, actual);
    }

    @Test
    public void openInvite() {
        CompetitionInviteResource expected = new CompetitionInviteResource();
        expected.setCompetitionName("my competition");
        setupPostWithRestResultAnonymousExpectations(format("%s/%s/%s", restUrl, "/openInvite", "hash"), CompetitionInviteResource.class, null, expected, OK);
        CompetitionInviteResource actual = service.openInvite("hash").getSuccessObject();
        assertEquals(expected, actual);
    }

    @Test
    public void openInvite_hashNotExists() {
        setupPostWithRestResultAnonymousExpectations(format("%s/%s/%s", restUrl, "/openInvite", "hashNotExists"), CompetitionInviteResource.class, null, null, NOT_FOUND);
        RestResult<CompetitionInviteResource> restResult = service.openInvite("hashNotExists");
        assertTrue(restResult.isFailure());
    }

    @Test
    public void acceptInvite() {
        setupPostWithRestResultExpectations(format("%s/%s/%s", restUrl, "/acceptInvite", "hash"), OK);
        RestResult<Void> restResult = service.acceptInvite("hash");
        assertTrue(restResult.isSuccess());
    }

    @Test
    public void rejectInvite() {
        RejectionReasonResource rejectionReasonResource = new RejectionReasonResource();
        rejectionReasonResource.setId(1L);
        CompetitionRejectionResource rejectionResource = new CompetitionRejectionResource(rejectionReasonResource, "too busy");

        setupPostWithRestResultAnonymousExpectations(format("%s/%s/%s", restUrl, "/rejectInvite", "hash"), Void.class, rejectionResource, null, OK);

        RestResult<Void> restResult = service.rejectInvite("hash", rejectionResource);
        assertTrue(restResult.isSuccess());
    }

    @Test
    public void checkExistingUser() {
        setupGetWithRestResultAnonymousExpectations(format("%s/%s/%s", restUrl, "/checkExistingUser", "hash"), Boolean.class, Boolean.TRUE);
        assertTrue(service.checkExistingUser("hash").getSuccessObject());
    }

    @Test
    public void getAvailableAssessors() throws Exception {
        Long competitionId = 1L;
        List<AvailableAssessorResource> expected = newAvailableAssessorResource().build(2);

        setupGetWithRestResultExpectations(format("%s/%s/%s", restUrl, "/getAvailableAssessors", competitionId), availableAssessorResourceListType(), expected);

        List<AvailableAssessorResource> actual = service.getAvailableAssessors(competitionId).getSuccessObject();
        assertEquals(expected, actual);
    }

    @Test
    public void inviteUser() {
        ExistingUserStagedInviteResource existingUserStagesInviteResource = new ExistingUserStagedInviteResource("firstname.example@example.com", 1L);
        CompetitionInviteResource expected = newCompetitionInviteResource().build();

        setupPostWithRestResultExpectations(format("%s/%s", restUrl, "/inviteUser"), CompetitionInviteResource.class, existingUserStagesInviteResource, expected, OK);

        CompetitionInviteResource actual = service.inviteUser(existingUserStagesInviteResource).getSuccessObject();
        assertEquals(expected, actual);
    }

    @Test
    public void deleteInvite() {
        String email = "firstname.lastname@example.com";
        Long competitionId = 1L;

        setupPostWithRestResultExpectations(format("%s/%s", restUrl, "/deleteInvite"), asMap("email", email, "competitionId", competitionId), OK);

        RestResult<Void> resultResult = service.deleteInvite(email, competitionId);
        assertTrue(resultResult.isSuccess());
    }
}