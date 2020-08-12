package org.innovateuk.ifs.invite.service;

import org.innovateuk.ifs.BaseRestServiceUnitTest;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.invite.resource.ApplicationKtaInviteResource;
import org.junit.Test;

import java.util.List;

import static org.innovateuk.ifs.commons.service.ParameterizedTypeReferences.applicationKtaInviteResourceListType;
import static org.innovateuk.ifs.invite.builder.ApplicationKtaInviteResourceBuilder.newApplicationKtaInviteResource;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.springframework.http.HttpStatus.OK;

public class KtaInviteRestServiceImplTest extends BaseRestServiceUnitTest<KtaInviteRestServiceImpl> {

    private static final String inviteKtaRestURL = "/kta-invite";

    @Override
    protected KtaInviteRestServiceImpl registerRestServiceUnderTest() {
        return new KtaInviteRestServiceImpl();
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
        final Long inviteId = 20310L;

        setupDeleteWithRestResultExpectations(inviteKtaRestURL +  String.format("/remove-kta-invite/%s", inviteId), OK);
        RestResult<Void> response = service.removeKtaInvite(inviteId);
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
}
