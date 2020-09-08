package org.innovateuk.ifs.invite.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.filter.CookieFlashMessageFilter;
import org.innovateuk.ifs.invite.populator.AcceptRejectApplicationKtaInviteModelPopulator;
import org.innovateuk.ifs.invite.resource.ApplicationKtaInviteResource;
import org.innovateuk.ifs.invite.service.ApplicationKtaInviteRestService;
import org.innovateuk.ifs.registration.service.RegistrationCookieService;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.invite.builder.ApplicationKtaInviteResourceBuilder.newApplicationKtaInviteResource;
import static org.innovateuk.ifs.invite.constant.InviteStatus.OPENED;
import static org.innovateuk.ifs.invite.constant.InviteStatus.SENT;
import static org.innovateuk.ifs.invite.controller.AbstractAcceptInviteController.INVITE_ALREADY_ACCEPTED;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.user.resource.Role.KNOWLEDGE_TRANSFER_ADVISER;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class AcceptApplicationKtaInviteControllerTest extends BaseControllerMockMVCTest<AcceptApplicationKtaInviteController> {

    @Mock
    private ApplicationKtaInviteRestService ktaInviteRestService;

    @Mock
    private AcceptRejectApplicationKtaInviteModelPopulator acceptRejectApplicationKtaInviteModelPopulator;

    @Mock
    private RegistrationCookieService registrationCookieService;

    @Mock
    private CookieFlashMessageFilter cookieFlashMessageFilter;

    @Override
    protected AcceptApplicationKtaInviteController supplyControllerUnderTest() {
        return new AcceptApplicationKtaInviteController();
    }

    @Test
    public void inviteEntryPage() throws Exception {
        String email = "user@ktn-uk.org";
        String hash = "hash123";
        UserResource ktaUser = newUserResource().withRoleGlobal(KNOWLEDGE_TRANSFER_ADVISER).withEmail(email).build();
        ApplicationKtaInviteResource applicationKtaInviteResource = newApplicationKtaInviteResource().withEmail(email).withStatus(SENT).build();
        setLoggedInUser(ktaUser);

        when(ktaInviteRestService.getKtaInviteByHash(hash)).thenReturn(restSuccess(applicationKtaInviteResource));

        mockMvc.perform(get("/kta/accept-invite/{hash}", hash))
                .andExpect(status().isOk())
                .andExpect(view().name("registration/accept-invite-kta-user"))
                .andReturn();
    }

    @Test
    public void acceptKtaPage() throws Exception {
        String email = "ktaUser@ktn-uk.org";
        String hash = "hash123";
        UserResource ktaUser = newUserResource().withRoleGlobal(KNOWLEDGE_TRANSFER_ADVISER).withEmail(email).build();
        ApplicationKtaInviteResource applicationKtaInviteResource = newApplicationKtaInviteResource().withEmail(email).withStatus(SENT).build();
        setLoggedInUser(ktaUser);

        when(ktaInviteRestService.getKtaInviteByHash(hash)).thenReturn(restSuccess(applicationKtaInviteResource));
        when(ktaInviteRestService.acceptInvite(hash)).thenReturn(restSuccess());

        mockMvc.perform(get("/kta/accept-invite/{hash}/accept", hash))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
    }

    @Test
    public void inviteAlreadyOpened() throws Exception {
        String email = "kta@ktn-uk.org";
        String hash = "hash123";
        UserResource ktaUser = newUserResource().withRoleGlobal(KNOWLEDGE_TRANSFER_ADVISER).withEmail(email).build();
        ApplicationKtaInviteResource applicationKtaInviteResource = newApplicationKtaInviteResource().withEmail(email).withStatus(OPENED).build();
        setLoggedInUser(ktaUser);
        MockHttpServletResponse response = new MockHttpServletResponse();

        when(ktaInviteRestService.getKtaInviteByHash(hash)).thenReturn(restSuccess(applicationKtaInviteResource));
        registrationCookieService.deleteAllRegistrationJourneyCookies(response);
        cookieFlashMessageFilter.setFlashMessage(response, INVITE_ALREADY_ACCEPTED);

        mockMvc.perform(get("/kta/accept-invite/{hash}", hash))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/not-found"));
    }
}