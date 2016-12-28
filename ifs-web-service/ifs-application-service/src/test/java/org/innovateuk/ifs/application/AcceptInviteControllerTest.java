package org.innovateuk.ifs.application;

import org.innovateuk.ifs.BaseUnitTest;
import org.innovateuk.ifs.filter.CookieFlashMessageFilter;
import org.innovateuk.ifs.invite.resource.ApplicationInviteResource;
import org.innovateuk.ifs.invite.resource.InviteOrganisationResource;
import org.innovateuk.ifs.invite.service.InviteServiceImpl;
import org.innovateuk.ifs.registration.AcceptInviteController;
import org.innovateuk.ifs.registration.service.RegistrationService;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.validation.Validator;

import java.util.HashMap;
import java.util.Map;

import static org.innovateuk.ifs.BaseControllerMockMVCTest.setupMockMvc;
import static org.junit.Assert.*;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(MockitoJUnitRunner.class)
@TestPropertySource(locations = "classpath:application.properties")
public class AcceptInviteControllerTest extends BaseUnitTest {

    @InjectMocks
    private AcceptInviteController acceptInviteController;

    @Mock
    private Validator validator;
    @Mock
    private CookieFlashMessageFilter cookieFlashMessageFilter;

    @Mock
    private RegistrationService registrationService;

    @Spy
    @InjectMocks
    public InviteServiceImpl inviteService;

    @Before
    public void setUp() throws Exception {

        // Process mock annotations
        MockitoAnnotations.initMocks(this);

        super.setup();

        mockMvc = setupMockMvc(acceptInviteController, () -> loggedInUser, env, messageSource);

        this.setupCompetition();
        this.setupApplicationWithRoles();
        this.setupApplicationResponses();
        this.loginDefaultUser();
        this.setupFinances();
        this.setupInvites();
        this.setupCookieUtil();
    }

    @Test
    public void testInviteEntryPage() throws Exception {
        MvcResult result = mockMvc.perform(
                get(String.format("/accept-invite/%s", INVITE_HASH))
        )
                .andExpect(status().is2xxSuccessful())
                .andExpect(cookie().exists(InviteServiceImpl.INVITE_HASH))
                .andExpect(view().name("registration/accept-invite"))
                .andReturn();

        assertEquals(INVITE_HASH, getDecryptedCookieValue(result.getResponse().getCookies(), InviteServiceImpl.INVITE_HASH));
    }

    @Test
    public void testInviteEntryPageExistingUser() throws Exception {
        Map<String, String> errors = new HashMap<>();
        errors.put("errorkey", "errorvalue");
        when(registrationService.getInvalidInviteMessages(isA(UserResource.class), isA(ApplicationInviteResource.class), isA(InviteOrganisationResource.class))).thenReturn(errors);

        MvcResult result = mockMvc.perform(
                get(String.format("/accept-invite/%s", INVITE_HASH_EXISTING_USER))
        )
                .andExpect(status().is2xxSuccessful())
                .andExpect(cookie().exists(InviteServiceImpl.INVITE_HASH))
                .andExpect(model().attribute("emailAddressRegistered", "true"))
                .andExpect(model().attribute("errorkey", "errorvalue"))
                .andExpect(view().name("registration/accept-invite-failure"))
                .andReturn();

        assertEquals(INVITE_HASH_EXISTING_USER, getDecryptedCookieValue(result.getResponse().getCookies(), InviteServiceImpl.INVITE_HASH));
    }

    @Test
    public void testInviteEntryPageInvalid() throws Exception {
        mockMvc.perform(
                get(String.format("/accept-invite/%s", INVALID_INVITE_HASH))
        )
                .andExpect(status().is2xxSuccessful())
                .andExpect(cookie().exists(InviteServiceImpl.INVITE_HASH))
                .andExpect(cookie().value(InviteServiceImpl.INVITE_HASH, ""))
                .andExpect(view().name("url-hash-invalid"));
    }

    @Test
    public void testInviteEntryPageAccepted() throws Exception {
        mockMvc.perform(
                get(String.format("/accept-invite/%s", ACCEPTED_INVITE_HASH))
        )
                .andExpect(status().is3xxRedirection())
                .andExpect(cookie().exists(InviteServiceImpl.INVITE_HASH))
                .andExpect(cookie().value(InviteServiceImpl.INVITE_HASH, ""))
                .andExpect(view().name("redirect:/login"));
    }
}