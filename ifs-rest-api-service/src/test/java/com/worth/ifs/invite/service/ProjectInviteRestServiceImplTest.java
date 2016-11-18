package com.worth.ifs.invite.service;

import com.worth.ifs.BaseRestServiceUnitTest;
import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.invite.resource.InviteProjectResource;
import com.worth.ifs.user.resource.UserResource;
import org.junit.Test;


import static com.worth.ifs.invite.resource.InviteProjectConstants.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.springframework.http.HttpStatus.OK;

public class ProjectInviteRestServiceImplTest extends BaseRestServiceUnitTest<ProjectInviteRestServiceImpl> {

    @Override
    protected ProjectInviteRestServiceImpl registerRestServiceUnderTest() {
        return new ProjectInviteRestServiceImpl();
    }

    @Test
    public void testGetProjectById() {
        String inviteHash = "hash";
        Long userId = 1L;
        setupPutWithRestResultAnonymousExpectations(PROJECT_INVITE_BASE_URL + ACCEPT_INVITE + inviteHash + "/" + userId, null, OK);
        RestResult<Void> returnedResponse = service.acceptInvite(inviteHash, userId);
        assertTrue(returnedResponse.isSuccess());

    }

    @Test
    public void testCheckExistingUser() {
        String inviteHash = "hash";
        setupGetWithRestResultAnonymousExpectations(PROJECT_INVITE_BASE_URL + CHECK_EXISTING_USER_URL + inviteHash, Boolean.class, true);
        RestResult<Boolean> returnedResponse = service.checkExistingUser(inviteHash);
        assertTrue(returnedResponse.isSuccess());
        assertTrue(returnedResponse.getSuccessObject());
    }


    @Test
    public void testGetInviteByHash() {
        InviteProjectResource invite = new InviteProjectResource();
        invite.setHash("hash");//.build();
        setupGetWithRestResultAnonymousExpectations(PROJECT_INVITE_BASE_URL + GET_INVITE_BY_HASH + invite.getHash(), InviteProjectResource.class, invite);
        RestResult<InviteProjectResource> returnedResponse = service.getInviteByHash(invite.getHash());
        assertTrue(returnedResponse.isSuccess());
        assertEquals(invite, returnedResponse.getSuccessObject());
    }

    @Test
    public void testGetUser() {
        UserResource user = new UserResource();
        String inviteHash = "hash";
        setupGetWithRestResultAnonymousExpectations(PROJECT_INVITE_BASE_URL + GET_USER_BY_HASH_MAPPING + inviteHash, UserResource.class, user);
        RestResult<UserResource> returnedResponse = service.getUser(inviteHash);
        assertTrue(returnedResponse.isSuccess());
        assertEquals(user, returnedResponse.getSuccessObject());
    }
}
