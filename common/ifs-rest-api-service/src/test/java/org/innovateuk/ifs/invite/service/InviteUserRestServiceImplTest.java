package org.innovateuk.ifs.invite.service;

import org.innovateuk.ifs.BaseRestServiceUnitTest;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.invite.resource.ExternalInviteResource;
import org.innovateuk.ifs.invite.resource.InviteUserResource;
import org.innovateuk.ifs.invite.resource.RoleInvitePageResource;
import org.innovateuk.ifs.invite.resource.RoleInviteResource;
import org.innovateuk.ifs.user.resource.SearchCategory;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.Collections;
import java.util.List;

import static org.innovateuk.ifs.commons.service.BaseRestService.buildPaginationUri;
import static org.innovateuk.ifs.commons.service.ParameterizedTypeReferences.externalInviteResourceListType;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.springframework.http.HttpStatus.OK;

public class InviteUserRestServiceImplTest extends BaseRestServiceUnitTest<InviteUserRestServiceImpl> {

    private static final String inviteRestBaseUrl = "/invite-user";

    @Override
    protected InviteUserRestServiceImpl registerRestServiceUnderTest() {
        return new InviteUserRestServiceImpl();
    }

    @Test
    public void saveUserInvite() {
        InviteUserResource inviteUserResource = new InviteUserResource();
        String url = inviteRestBaseUrl + "/save-invite";
        setupPostWithRestResultExpectations(url, inviteUserResource, HttpStatus.OK);
        RestResult<Void> result = service.saveUserInvite(inviteUserResource);
        assertTrue(result.isSuccess());
    }

    @Test
    public void getInvite() {
        RoleInviteResource roleInviteResource = new RoleInviteResource();
        String url = inviteRestBaseUrl + "/get-invite/";
        String inviteHash = "hash";
        setupGetWithRestResultAnonymousExpectations(url + inviteHash, RoleInviteResource.class, roleInviteResource);
        RestResult<RoleInviteResource> result = service.getInvite(inviteHash);
        assertTrue(result.isSuccess());
        assertEquals(roleInviteResource, result.getSuccess());
    }

    @Test
    public void checkExistingUser() {
        String url = inviteRestBaseUrl + "/check-existing-user/";
        String inviteHash = "hash";
        setupGetWithRestResultAnonymousExpectations(url + inviteHash, Boolean.class, true);
        RestResult<Boolean> returnedResponse = service.checkExistingUser(inviteHash);
        assertTrue(returnedResponse.isSuccess());
        assertTrue(returnedResponse.getSuccess());
    }

    @Test
    public void getPendingInternalUsers() {
        RoleInvitePageResource expected = new RoleInvitePageResource();
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("filter", "");
        setupGetWithRestResultExpectations(buildPaginationUri(inviteRestBaseUrl + "/internal/pending", 0, 5, null, params), RoleInvitePageResource.class, expected, OK);
        RoleInvitePageResource result = service.getPendingInternalUserInvites("", 0, 5).getSuccess();
        assertEquals(expected, result);
    }

    @Test
    public void findExternalInvites() {

        String searchString = "%a%";
        SearchCategory searchCategory = SearchCategory.NAME;

        List<ExternalInviteResource> expected = Collections.singletonList(new ExternalInviteResource());
        setupGetWithRestResultExpectations(inviteRestBaseUrl + "/find-external-invites?searchString=" + searchString + "&searchCategory=" + searchCategory.name(), externalInviteResourceListType(), expected, OK);
        List<ExternalInviteResource> result = service.findExternalInvites(searchString, searchCategory).getSuccess();
        assertEquals(expected, result);
    }

    @Test
    public void resendInternalUserInvite() {

        setupPutWithRestResultExpectations(inviteRestBaseUrl + "/internal/pending/123/resend", OK);

        RestResult<Void> result = service.resendInternalUserInvite(123L);

        assertTrue(result.isSuccess());
        setupPutWithRestResultVerifications(inviteRestBaseUrl + "/internal/pending/123/resend");
    }
}