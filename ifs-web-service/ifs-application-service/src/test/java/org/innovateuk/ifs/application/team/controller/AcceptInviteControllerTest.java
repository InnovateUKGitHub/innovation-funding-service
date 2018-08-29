package org.innovateuk.ifs.application.team.controller;

import org.innovateuk.ifs.AbstractApplicationMockMVCTest;
import org.innovateuk.ifs.commons.security.UserAuthenticationService;
import org.innovateuk.ifs.filter.CookieFlashMessageFilter;
import org.innovateuk.ifs.organisation.resource.OrganisationTypeEnum;
import org.innovateuk.ifs.registration.controller.AcceptInviteController;
import org.innovateuk.ifs.registration.populator.AcceptRejectApplicationInviteModelPopulator;
import org.innovateuk.ifs.registration.service.RegistrationCookieService;
import org.innovateuk.ifs.registration.service.RegistrationService;
import org.innovateuk.ifs.registration.viewmodel.AcceptRejectApplicationInviteViewModel;
import org.innovateuk.ifs.registration.viewmodel.ConfirmOrganisationInviteOrganisationViewModel;
import org.innovateuk.ifs.util.CookieUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.validation.Validator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

import static org.innovateuk.ifs.CookieTestUtil.setupCookieUtil;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.invite.builder.ApplicationInviteResourceBuilder.newApplicationInviteResource;
import static org.innovateuk.ifs.invite.builder.InviteOrganisationResourceBuilder.newInviteOrganisationResource;
import static org.innovateuk.ifs.invite.constant.InviteStatus.SENT;
import static org.innovateuk.ifs.organisation.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@RunWith(MockitoJUnitRunner.class)
@TestPropertySource(locations = "classpath:application.properties")
public class AcceptInviteControllerTest extends AbstractApplicationMockMVCTest<AcceptInviteController> {

    @Mock
    private Validator validator;

    @Mock
    private CookieFlashMessageFilter cookieFlashMessageFilter;

    @Mock
    private RegistrationService registrationService;

    @Mock
    private RegistrationCookieService registrationCookieService;

    @Mock
    private CookieUtil cookieUtil;

    @Mock
    private UserAuthenticationService userAuthenticationService;

    @Spy
    @InjectMocks
    private AcceptRejectApplicationInviteModelPopulator acceptRejectApplicationInviteModelPopulator;

    @Override
    protected AcceptInviteController supplyControllerUnderTest() {
        return new AcceptInviteController();
    }

    @Before
    public void setUp() {
        super.setUp();
        setLoggedInUser(null);
        this.setupCompetition();
        this.setupApplicationWithRoles();
        this.setupApplicationResponses();
        this.setupFinances();
        this.setupInvites();
        setupCookieUtil(cookieUtil);
    }

    @Test
    public void testInviteEntryPage() throws Exception {
        when(userAuthenticationService.getAuthenticatedUser(any(HttpServletRequest.class))).thenReturn(null);

        MvcResult result = mockMvc.perform(get(String.format("/accept-invite/%s", INVITE_HASH)))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("registration/accept-invite-new-user"))
                .andReturn();

        assertTrue(result.getModelAndView().getModel().containsKey("model"));

        Object viewModel = result.getModelAndView().getModel().get("model");

        assertTrue(viewModel.getClass().equals(AcceptRejectApplicationInviteViewModel.class));

        verify(registrationCookieService, times(1)).deleteAllRegistrationJourneyCookies(any(HttpServletResponse.class));
        verify(registrationCookieService, times(1)).saveToInviteHashCookie(eq(INVITE_HASH), any());
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
        when(organisationRestService.getOrganisationByIdForAnonymousUserFlow(organisationId)).thenReturn(restSuccess(newOrganisationResource()
                .withId(organisationId).withOrganisationType(OrganisationTypeEnum.BUSINESS.getId()).build()));
        when(registrationCookieService.getInviteHashCookieValue(any())).thenReturn(Optional.of(INVITE_HASH));

        MvcResult result = mockMvc.perform(get(String.format("/accept-invite/confirm-invited-organisation")))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("registration/confirm-invited-organisation"))
                .andReturn();

        assertTrue(result.getModelAndView().getModel().containsKey("model"));

        Object viewModel = result.getModelAndView().getModel().get("model");

        assertTrue(viewModel.getClass().equals(ConfirmOrganisationInviteOrganisationViewModel.class));

        verify(registrationCookieService, times(1)).saveToOrganisationIdCookie(eq(organisationId), any());
    }

    @Test
    public void testInviteEntryPageInvalid() throws Exception {
        mockMvc.perform(get(String.format("/accept-invite/%s", INVALID_INVITE_HASH)))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("url-hash-invalid"));

        verify(registrationCookieService, times(1)).deleteOrganisationCreationCookie(any());
        verify(registrationCookieService, times(1)).deleteOrganisationIdCookie(any());
        verify(registrationCookieService, times(1)).deleteInviteHashCookie(any());
    }

    @Test
    public void testInviteEntryPageAccepted() throws Exception {
        when(inviteRestService.getInviteOrganisationByHash(ACCEPTED_INVITE_HASH)).thenReturn(restSuccess(newInviteOrganisationResource().build()));
        mockMvc.perform(get(String.format("/accept-invite/%s", ACCEPTED_INVITE_HASH)))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/login"));

        verify(registrationCookieService, times(1)).deleteOrganisationCreationCookie(any());
        verify(registrationCookieService, times(1)).deleteOrganisationIdCookie(any());
        verify(registrationCookieService, times(1)).deleteInviteHashCookie(any());
    }
}