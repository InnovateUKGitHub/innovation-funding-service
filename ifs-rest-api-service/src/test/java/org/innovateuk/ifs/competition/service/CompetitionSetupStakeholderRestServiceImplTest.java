package org.innovateuk.ifs.competition.service;

import org.innovateuk.ifs.BaseRestServiceUnitTest;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.invite.resource.InviteUserResource;
import org.innovateuk.ifs.invite.resource.StakeholderInviteResource;
import org.innovateuk.ifs.registration.resource.StakeholderRegistrationResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Test;
import org.springframework.http.HttpStatus;

import java.util.List;

import static org.innovateuk.ifs.commons.service.ParameterizedTypeReferences.userListType;
import static org.innovateuk.ifs.stakeholder.builder.StakeholderInviteResourceBuilder.newStakeholderInviteResource;
import static org.innovateuk.ifs.stakeholder.builder.StakeholderRegistrationResourceBuilder.newStakeholderRegistrationResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CompetitionSetupStakeholderRestServiceImplTest extends BaseRestServiceUnitTest<CompetitionSetupStakeholderRestServiceImpl> {

    private static final String competitionSetupStakeholderRestURL = "/competition/setup/";

    @Override
    protected CompetitionSetupStakeholderRestServiceImpl registerRestServiceUnderTest() {
        return new CompetitionSetupStakeholderRestServiceImpl();
    }

    @Test
    public void inviteStakeholder() {
        long competitionId = 1L;
        InviteUserResource inviteUserResource = new InviteUserResource();

        String url = competitionSetupStakeholderRestURL + competitionId + "/stakeholder/invite";
        setupPostWithRestResultExpectations(url, inviteUserResource, HttpStatus.OK);

        RestResult<Void> result = service.inviteStakeholder(inviteUserResource, competitionId);
        assertTrue(result.isSuccess());

        setupPostWithRestResultVerifications(url, Void.class, inviteUserResource);
    }

    @Test
    public void findStakeholders() {

        long competitionId = 1L;
        List<UserResource> responseBody = newUserResource().build(2);

        String url = competitionSetupStakeholderRestURL + competitionId + "/stakeholder/find-all";
        setupGetWithRestResultExpectations(url, userListType(), responseBody);

        List<UserResource> response = service.findStakeholders(competitionId).getSuccess();
        assertEquals(responseBody, response);
    }

    @Test
    public void getInvite() {

        String hash = "hash1234";
        StakeholderInviteResource invite = newStakeholderInviteResource().build();
        String url = competitionSetupStakeholderRestURL + "get-invite/" + hash;
        setupGetWithRestResultAnonymousExpectations(url, StakeholderInviteResource.class, invite);

        StakeholderInviteResource response = service.getInvite(hash).getSuccess();
        assertEquals(invite, response);
    }

    @Test
    public void createStakeholder() {

        String hash = "hash1234";
        StakeholderRegistrationResource resource = newStakeholderRegistrationResource().build();
        String url = competitionSetupStakeholderRestURL + "stakeholder/create/" + hash;
        setupPostWithRestResultAnonymousExpectations(url, Void.class, resource, null, HttpStatus.OK);

        RestResult<Void> result = service.createStakeholder(hash, resource);
        assertTrue(result.isSuccess());

    }

    @Test
    public void addStakeholder() {
        long competitionId = 1L;
        long stakeholderUserId = 2L;

        String url = competitionSetupStakeholderRestURL + competitionId + "/stakeholder/" + stakeholderUserId + "/add";
        setupPostWithRestResultExpectations(url, HttpStatus.OK);

        RestResult<Void> result = service.addStakeholder(competitionId, stakeholderUserId);
        assertTrue(result.isSuccess());

        setupPostWithRestResultVerifications(url, Void.class);
    }

    @Test
    public void removeStakeholder() {
        long competitionId = 1L;
        long stakeholderUserId = 2L;

        String url = competitionSetupStakeholderRestURL + competitionId + "/stakeholder/" + stakeholderUserId + "/remove";
        setupPostWithRestResultExpectations(url, HttpStatus.OK);

        RestResult<Void> result = service.removeStakeholder(competitionId, stakeholderUserId);
        assertTrue(result.isSuccess());

        setupPostWithRestResultVerifications(url, Void.class);
    }

    @Test
    public void findPendingStakeholderInvites() {

        long competitionId = 1L;
        List<UserResource> responseBody = newUserResource().build(2);

        String url = competitionSetupStakeholderRestURL + competitionId + "/stakeholder/pending-invites";
        setupGetWithRestResultExpectations(url, userListType(), responseBody);

        List<UserResource> response = service.findPendingStakeholderInvites(competitionId).getSuccess();
        assertEquals(responseBody, response);
    }
}

