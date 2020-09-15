package org.innovateuk.ifs.grants;

import org.innovateuk.ifs.BaseRestServiceUnitTest;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.grants.service.GrantsInviteRestServiceImpl;
import org.innovateuk.ifs.grantsinvite.resource.GrantsInviteResource;
import org.innovateuk.ifs.grantsinvite.resource.SentGrantsInviteResource;
import org.junit.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;

import java.util.List;

import static java.lang.String.format;
import static org.innovateuk.ifs.grantsinvite.builder.SentGrantsInviteResourceBuilder.newSentGrantsInviteResource;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class GrantsInviteRestServiceImplTest extends BaseRestServiceUnitTest<GrantsInviteRestServiceImpl> {

    private static final String BASE_URL = "/project/%d/grant-invite";

    @Override
    protected GrantsInviteRestServiceImpl registerRestServiceUnderTest() {
        return new GrantsInviteRestServiceImpl();
    }

    @Test
    public void getAllForProject() {
        long projectId = 1L;
        List<SentGrantsInviteResource> invites = newSentGrantsInviteResource().build(1);

        setupGetWithRestResultExpectations(format(BASE_URL, projectId), new ParameterizedTypeReference<List<SentGrantsInviteResource>>() {}, invites);
        RestResult<List<SentGrantsInviteResource>> result = service.getAllForProject(projectId);
        assertSame(result.getSuccess(), invites);
    }

    @Test
    public void invite() {
        long projectId = 1L;
        GrantsInviteResource grantsInviteResource = new GrantsInviteResource();

        setupPostWithRestResultExpectations(format(BASE_URL, projectId), grantsInviteResource, HttpStatus.OK);
        RestResult<Void> result = service.invite(projectId, grantsInviteResource);
        assertTrue(result.isSuccess());
    }

    @Test
    public void resend() {
        long projectId = 1L;
        long inviteId = 2L;

        setupPostWithRestResultExpectations(format(BASE_URL + "/%d/resend", projectId, inviteId), HttpStatus.OK);
        RestResult<Void> result = service.resendInvite(projectId, inviteId);
        assertTrue(result.isSuccess());
    }

    @Test
    public void delete() {
        long projectId = 1L;
        long inviteId = 2L;

        setupDeleteWithRestResultExpectations(format(BASE_URL + "/%d", projectId, inviteId), HttpStatus.OK);
        RestResult<Void> result = service.deleteInvite(projectId, inviteId);
        assertTrue(result.isSuccess());
    }

    @Test
    public void getInviteByHash() {
        long projectId = 1L;
        String hash = "hashy";
        SentGrantsInviteResource expected = newSentGrantsInviteResource().build();

        setupGetWithRestResultAnonymousExpectations(format(BASE_URL + "/%s", projectId, hash), SentGrantsInviteResource.class, expected);
        RestResult<SentGrantsInviteResource> result = service.getInviteByHash(projectId, hash);
        assertSame(expected, result.getSuccess());
    }

    @Test
    public void acceptInvite() {
        long projectId = 1L;
        long inviteId = 2L;

        setupPostWithRestResultAnonymousExpectations(format(BASE_URL + "/%d/accept", projectId, inviteId), Void.class, null, null, HttpStatus.OK);
        RestResult<Void> result = service.acceptInvite(projectId, inviteId);
        assertTrue(result.isSuccess());
    }
}