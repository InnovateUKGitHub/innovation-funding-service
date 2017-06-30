package org.innovateuk.ifs.invite.service;

import org.innovateuk.ifs.BaseRestServiceUnitTest;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.invite.resource.InviteUserResource;
import org.junit.Test;
import org.springframework.http.HttpStatus;

import static org.junit.Assert.assertTrue;

public class InviteUserRestServiceImplTest extends BaseRestServiceUnitTest<InviteUserRestServiceImpl> {

    @Override
    protected InviteUserRestServiceImpl registerRestServiceUnderTest() {
        final InviteUserRestServiceImpl inviteUserRestService = new InviteUserRestServiceImpl();
        return inviteUserRestService;
    }

    @Test
    public void saveUserInvite() throws Exception {

        InviteUserResource inviteUserResource = new InviteUserResource();
        String url = "/inviteUser" + "/saveInvite";
        setupPostWithRestResultExpectations(url, inviteUserResource, HttpStatus.OK);

        RestResult<Void> result = service.saveUserInvite(inviteUserResource);
        assertTrue(result.isSuccess());
    }
}

