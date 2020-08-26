package org.innovateuk.ifs.invite.service;

import org.innovateuk.ifs.BaseRestServiceUnitTest;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.invite.resource.ApplicationKtaInviteResource;
import org.junit.Test;

import static org.innovateuk.ifs.invite.builder.ApplicationKtaInviteResourceBuilder.newApplicationKtaInviteResource;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.springframework.http.HttpStatus.OK;

public class ApplicationKtaInviteRestServiceImplTest extends BaseRestServiceUnitTest<ApplicationKtaInviteRestServiceImpl> {

    private static final String inviteKtaRestURL = "/kta-invite";

    @Override
    protected ApplicationKtaInviteRestServiceImpl registerRestServiceUnderTest() {
        return new ApplicationKtaInviteRestServiceImpl();
    }

    @Test
    public void saveKtaInvite() {
        final ApplicationKtaInviteResource invite = newApplicationKtaInviteResource().build();

        setupPostWithRestResultExpectations(inviteKtaRestURL +  "/save-kta-invite", invite, OK);
        RestResult<Void> response = service.saveKtaInvite(invite);
        assertTrue(response.isSuccess());

        setupPostWithRestResultVerifications(inviteKtaRestURL +  "/save-kta-invite", Void.class, invite);
    }

    @Test
    public void resendKtaInvite() {
        final ApplicationKtaInviteResource invite = newApplicationKtaInviteResource().build();

        setupPostWithRestResultExpectations(inviteKtaRestURL +  "/resend-kta-invite", invite, OK);
        RestResult<Void> response = service.resendKtaInvite(invite);
        assertTrue(response.isSuccess());

        setupPostWithRestResultVerifications(inviteKtaRestURL +  "/resend-kta-invite", Void.class, invite);
    }

    @Test
    public void removeKtaInvite() {
        final Long applicationId = 20310L;

        setupDeleteWithRestResultExpectations(inviteKtaRestURL +  String.format("/remove-kta-invite-by-application/%s", applicationId), OK);
        RestResult<Void> response = service.removeKtaInviteByApplication(applicationId);
        assertTrue(response.isSuccess());
    }

    @Test
    public void getKtaInvitesByApplication() {
        Long applicationId = 2341L;
        ApplicationKtaInviteResource expected = newApplicationKtaInviteResource().build();
        String url = inviteKtaRestURL + "/get-kta-invite-by-application-id/" + applicationId;
        setupGetWithRestResultExpectations(url, ApplicationKtaInviteResource.class, expected, OK);
        RestResult<ApplicationKtaInviteResource> response = service.getKtaInviteByApplication(applicationId);

        assertTrue(response.isSuccess());
        assertEquals(expected, response.getSuccess());
    }

    @Test
    public void getKtaInviteByHash() {
        String hash = "hash";
        ApplicationKtaInviteResource expected = newApplicationKtaInviteResource().build();
        String url = inviteKtaRestURL + "/hash/" + hash;
        setupGetWithRestResultAnonymousExpectations(url, ApplicationKtaInviteResource.class, expected, OK);
        RestResult<ApplicationKtaInviteResource> response = service.getKtaInviteByHash(hash);

        assertTrue(response.isSuccess());
        assertEquals(expected, response.getSuccess());
    }

    @Test
    public void acceptInvite() {
        final String hash = "Hash";

        setupPostWithRestResultExpectations(inviteKtaRestURL +  "/hash/" + hash, OK);
        RestResult<Void> response = service.acceptInvite(hash);
        assertTrue(response.isSuccess());

        setupPostWithRestResultVerifications(inviteKtaRestURL +  "/hash/" + hash, Void.class);
    }
}
