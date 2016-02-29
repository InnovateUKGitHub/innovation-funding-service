package com.worth.ifs.application;

import com.worth.ifs.BaseUnitTest;
import com.worth.ifs.exception.ErrorController;
import com.worth.ifs.invite.constant.InviteStatusConstants;
import com.worth.ifs.invite.resource.InviteOrganisationResource;
import com.worth.ifs.invite.resource.InviteResource;
import com.worth.ifs.security.CookieFlashMessageFilter;
import com.worth.ifs.user.resource.UserResource;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.Validator;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;

import javax.servlet.http.Cookie;

import static com.worth.ifs.commons.rest.RestResult.restFailure;
import static com.worth.ifs.commons.rest.RestResult.restSuccess;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(MockitoJUnitRunner.class)
@TestPropertySource(locations = "classpath:application.properties")
public class AcceptInviteControllerTest extends BaseUnitTest {
    public static final String INVITE_HASH = "b157879c18511630f220325b7a64cf3eb782759326d3cbb85e546e0d03e663ec711ec7ca65827a96";
    public static final String INVITE_HASH_EXISTING_USER = "cccccccccc630f220325b7a64cf3eb782759326d3cbb85e546e0d03e663ec711ec7ca65827a96";
    public static final String INVALID_INVITE_HASH = "aaaaaaa7a64cf3eb782759326d3cbb85e546e0d03e663ec711ec7ca65827a96";
    public static final String ACCEPTED_INVITE_HASH = "BBBBBBBBB7a64cf3eb782759326d3cbb85e546e0d03e663ec711ec7ca65827a96";

    @InjectMocks
    private AcceptInviteController acceptInviteController;


    @Mock
    private Validator validator;
    @Mock
    CookieFlashMessageFilter cookieFlashMessageFilter;
    private Long applicationId;

    @Before
    public void setUp() throws Exception {

        super.setup();
        // Process mock annotations
        MockitoAnnotations.initMocks(this);

        CookieLocaleResolver localeResolver = new CookieLocaleResolver();
        localeResolver.setCookieDomain("domain");

        mockMvc = MockMvcBuilders.standaloneSetup(acceptInviteController, new ErrorController())
                .setViewResolvers(viewResolver())
                .setLocaleResolver(localeResolver)
                .addFilters(new CookieFlashMessageFilter())
                .build();


        this.setupCompetition();
        this.setupApplicationWithRoles();
        this.setupApplicationResponses();
        this.loginDefaultUser();
        this.setupFinances();
        this.setupInvites();
        this.setupOrganisationTypes();


        InviteResource invite = new InviteResource();
        invite.setStatus(InviteStatusConstants.SEND);
        invite.setApplication(1L);
        invite.setName("Some Invitee");
        invite.setHash(INVITE_HASH);
        String email = "invited@email.com";
        invite.setEmail(email);
        when(inviteRestService.getInviteByHash(eq(INVITE_HASH))).thenReturn(restSuccess(invite));
        when(userService.findUserByEmail(eq(email))).thenReturn(restSuccess(emptyList()));

        when(inviteRestService.getInviteByHash(eq(INVALID_INVITE_HASH))).thenReturn(restFailure(emptyList()));


        InviteResource acceptedInvite = new InviteResource();
        acceptedInvite.setStatus(InviteStatusConstants.ACCEPTED);
        acceptedInvite.setApplication(1L);
        acceptedInvite.setName("Some Invitee");
        acceptedInvite.setHash(ACCEPTED_INVITE_HASH);
        acceptedInvite.setEmail(email);
        when(inviteRestService.getInviteByHash(eq(ACCEPTED_INVITE_HASH))).thenReturn(restSuccess(acceptedInvite));



        InviteResource existingUserInvite = new InviteResource();
        existingUserInvite.setStatus(InviteStatusConstants.SEND);
        existingUserInvite.setApplication(1L);
        existingUserInvite.setName("Some Invitee");
        existingUserInvite.setHash(INVITE_HASH_EXISTING_USER);
        existingUserInvite.setEmail("existing@email.com");
        when(userService.findUserByEmail(eq("existing@email.com"))).thenReturn(restSuccess(asList(new UserResource())));
        when(inviteRestService.getInviteByHash(eq(INVITE_HASH_EXISTING_USER))).thenReturn(restSuccess(existingUserInvite));

        when(inviteRestService.getInvitesByApplication(isA(Long.class))).thenReturn(restSuccess(emptyList()));
        when(inviteRestService.getInviteOrganisationByHash(INVITE_HASH)).thenReturn(restSuccess(new InviteOrganisationResource()));

        applicationId = applications.get(0).getId();
    }

    @Test
    public void testInviteEntryPage() throws Exception {
        mockMvc.perform(
                get(String.format("/accept-invite/%s", INVITE_HASH))
        )
                .andExpect(status().is2xxSuccessful())
                .andExpect(cookie().exists(AcceptInviteController.INVITE_HASH))
                .andExpect(cookie().value(AcceptInviteController.INVITE_HASH, INVITE_HASH))
                .andExpect(view().name("application-contributors/invite/accept-invite"));
    }

    @Test
    public void testInviteEntryPageExistingUser() throws Exception {
        mockMvc.perform(
                get(String.format("/accept-invite/%s", INVITE_HASH_EXISTING_USER))
        )
                .andExpect(status().is2xxSuccessful())
                .andExpect(cookie().exists(AcceptInviteController.INVITE_HASH))
                .andExpect(cookie().value(AcceptInviteController.INVITE_HASH, INVITE_HASH_EXISTING_USER))
                .andExpect(model().attribute("emailAddressRegistered", "true"))
                .andExpect(view().name("application-contributors/invite/accept-invite"));
    }

    @Test
    public void testInviteEntryPageInvalid() throws Exception {
        mockMvc.perform(
                get(String.format("/accept-invite/%s", INVALID_INVITE_HASH))
        )
                .andExpect(status().is3xxRedirection())
                .andExpect(cookie().value(AcceptInviteController.INVITE_HASH, ""))
                .andExpect(view().name("redirect:/login"));
    }
    @Test
    public void testInviteEntryPageAccepted() throws Exception {
        mockMvc.perform(
                get(String.format("/accept-invite/%s", ACCEPTED_INVITE_HASH))
        )
                .andExpect(status().is3xxRedirection())
                .andExpect(cookie().value(AcceptInviteController.INVITE_HASH, ""))
                .andExpect(view().name("redirect:/login"));
    }

    @Test
    public void testChooseOrganisationType() throws Exception {
        mockMvc.perform(
                get("/accept-invite/new-account-organisation-type")
                .cookie(new Cookie(AcceptInviteController.INVITE_HASH, INVITE_HASH))
        )
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("application-contributors/invite/organisation-type"))
                .andExpect(model().attributeExists("organisationTypeForm", "organisationTypes", "organisationTypeForm", "invite"))
                .andExpect(model().attribute("organisationTypes", Matchers.hasSize(4)));

    }
    @Test
    public void testChooseOrganisationTypeResearchSelected() throws Exception {
        mockMvc.perform(
                get("/accept-invite/new-account-organisation-type").param("organisationType", "2")
                        .cookie(new Cookie(AcceptInviteController.INVITE_HASH, INVITE_HASH))
        )
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("application-contributors/invite/organisation-type"))
                .andExpect(model().attributeExists("organisationTypeForm", "organisationTypes", "organisationTypeForm", "invite"))
                .andExpect(model().attribute("organisationTypes", Matchers.hasSize(5)));

    }

    /**
     * Test if the request is redirected forward, to the organisation creation controller.
     */
    @Test
    public void chooseOrganisationTypePostBusiness() throws Exception {
        mockMvc.perform(
                post("/accept-invite/new-account-organisation-type")
                        .cookie(new Cookie(AcceptInviteController.INVITE_HASH, INVITE_HASH))
                        .param("organisationType", "1")

        )
                .andExpect(status().is3xxRedirection())
                .andExpect(cookie().value("organisationType", "{\"organisationType\":1}"))
                .andExpect(view().name("redirect:/organisation/create/find-organisation"));
    }


    /**
     * test if request is redirected back to the form for selecting the subtype of research.
     */
    @Test
    public void chooseOrganisationTypePostResearch() throws Exception {
        mockMvc.perform(
                post("/accept-invite/new-account-organisation-type")
                        .cookie(new Cookie(AcceptInviteController.INVITE_HASH, INVITE_HASH))
                        .param("organisationType", "2")

        )
                .andExpect(status().is3xxRedirection())
                .andExpect(cookie().value("organisationType", "{\"organisationType\":2}"))
                .andExpect(view().name("redirect:/accept-invite/new-account-organisation-type/?organisationType=2"));
    }
}