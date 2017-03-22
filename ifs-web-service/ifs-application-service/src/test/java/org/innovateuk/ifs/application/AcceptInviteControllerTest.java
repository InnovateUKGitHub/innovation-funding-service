package org.innovateuk.ifs.application;

import org.innovateuk.ifs.BaseUnitTest;
import org.innovateuk.ifs.filter.CookieFlashMessageFilter;
import org.innovateuk.ifs.registration.AbstractAcceptInviteController;
import org.innovateuk.ifs.registration.AcceptInviteController;
import org.innovateuk.ifs.registration.RegistrationController;
import org.innovateuk.ifs.registration.model.AcceptRejectApplicationInviteModelPopulator;
import org.innovateuk.ifs.registration.service.RegistrationService;
import org.innovateuk.ifs.registration.viewmodel.AcceptRejectApplicationInviteViewModel;
import org.innovateuk.ifs.registration.viewmodel.ConfirmOrganisationInviteOrganisationViewModel;
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

import javax.servlet.http.HttpServletRequest;

import static org.innovateuk.ifs.BaseControllerMockMVCTest.setupMockMvc;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.invite.builder.ApplicationInviteResourceBuilder.newApplicationInviteResource;
import static org.innovateuk.ifs.invite.builder.InviteOrganisationResourceBuilder.newInviteOrganisationResource;
import static org.innovateuk.ifs.invite.constant.InviteStatus.SENT;
import static org.innovateuk.ifs.user.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
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
    private AcceptRejectApplicationInviteModelPopulator acceptRejectApplicationInviteModelPopulator;

    @Before
    public void setUp() throws Exception {
        // Process mock annotations
        MockitoAnnotations.initMocks(this);
        super.setup();
        mockMvc = setupMockMvc(acceptInviteController, () -> null, env, messageSource);
        this.setupCompetition();
        this.setupApplicationWithRoles();
        this.setupApplicationResponses();
        this.setupFinances();
        this.setupInvites();
        this.setupCookieUtil();
    }

    @Test
    public void testInviteEntryPage() throws Exception {
        when(userAuthenticationService.getAuthenticatedUser(any(HttpServletRequest.class))).thenReturn(null);
        MvcResult result = mockMvc.perform(get(String.format("/accept-invite/%s", INVITE_HASH)))
                .andExpect(status().is2xxSuccessful())
                .andExpect(cookie().exists(AbstractAcceptInviteController.INVITE_HASH))
                .andExpect(view().name("registration/accept-invite-new-user"))
                .andReturn();

        assertTrue(result.getModelAndView().getModel().containsKey("model"));

        Object viewModel = result.getModelAndView().getModel().get("model");

        assertTrue(viewModel.getClass().equals(AcceptRejectApplicationInviteViewModel.class));
        assertEquals(INVITE_HASH, getDecryptedCookieValue(result.getResponse().getCookies(), AbstractAcceptInviteController.INVITE_HASH));
    }


    @Test
    public void testConfirmInvitedOrganisation() throws Exception {
        final Long organisationId = 3L;
        when(userAuthenticationService.getAuthenticatedUser(any(HttpServletRequest.class))).thenReturn(newUserResource().withEmail("email@test.com").build());
        when(inviteRestService.getInviteByHash(anyString())).thenReturn(restSuccess(newApplicationInviteResource()
                .withStatus(SENT)
                .withEmail("email@test.com")
                .withInviteOrganisation(organisationId)
                .build()));
        when(inviteRestService.getInviteOrganisationByHash(anyString())).thenReturn(restSuccess(newInviteOrganisationResource().withOrganisation(organisationId).build()));
        when(organisationService.getOrganisationByIdForAnonymousUserFlow(organisationId)).thenReturn(newOrganisationResource().withId(organisationId).build());

        MvcResult result = mockMvc.perform(get(String.format("/accept-invite/confirm-invited-organisation")))
                .andExpect(status().is2xxSuccessful())
                .andExpect(cookie().exists(RegistrationController.ORGANISATION_ID_PARAMETER_NAME))
                .andExpect(view().name("registration/confirm-invited-organisation"))
                .andReturn();

        assertTrue(result.getModelAndView().getModel().containsKey("model"));

        Object viewModel = result.getModelAndView().getModel().get("model");

        assertTrue(viewModel.getClass().equals(ConfirmOrganisationInviteOrganisationViewModel.class));
    }


    @Test
    public void testInviteEntryPageInvalid() throws Exception {
        mockMvc.perform(get(String.format("/accept-invite/%s", INVALID_INVITE_HASH)))
                .andExpect(status().is2xxSuccessful())
                .andExpect(cookie().exists(AbstractAcceptInviteController.INVITE_HASH))
                .andExpect(cookie().value(AbstractAcceptInviteController.INVITE_HASH, ""))
                .andExpect(view().name("url-hash-invalid"));
    }

    @Test
    public void testInviteEntryPageAccepted() throws Exception {
        when(inviteRestService.getInviteOrganisationByHash(ACCEPTED_INVITE_HASH)).thenReturn(restSuccess(newInviteOrganisationResource().build()));
        mockMvc.perform(get(String.format("/accept-invite/%s", ACCEPTED_INVITE_HASH)))
                .andExpect(status().is3xxRedirection())
                .andExpect(cookie().exists(AbstractAcceptInviteController.INVITE_HASH))
                .andExpect(cookie().value(AbstractAcceptInviteController.INVITE_HASH, ""))
                .andExpect(view().name("redirect:/login"));
    }
}