package org.innovateuk.ifs.application.team.controller;

import org.innovateuk.ifs.AbstractApplicationMockMVCTest;
import org.innovateuk.ifs.commons.security.UserAuthenticationService;
import org.innovateuk.ifs.filter.CookieFlashMessageFilter;
import org.innovateuk.ifs.invite.resource.ApplicationInviteResource;
import org.innovateuk.ifs.invite.resource.InviteOrganisationResource;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.organisation.resource.OrganisationTypeEnum;
import org.innovateuk.ifs.registration.controller.AcceptInviteController;
import org.innovateuk.ifs.registration.populator.AcceptRejectApplicationInviteModelPopulator;
import org.innovateuk.ifs.registration.populator.ConfirmOrganisationInviteModelPopulator;
import org.innovateuk.ifs.registration.service.RegistrationCookieService;
import org.innovateuk.ifs.registration.viewmodel.AcceptRejectApplicationInviteViewModel;
import org.innovateuk.ifs.registration.viewmodel.ConfirmOrganisationInviteOrganisationViewModel;
import org.innovateuk.ifs.util.CookieUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.context.TestPropertySource;
import org.springframework.validation.Validator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

import static java.lang.String.format;
import static org.innovateuk.ifs.CookieTestUtil.setupCookieUtil;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.invite.builder.ApplicationInviteResourceBuilder.newApplicationInviteResource;
import static org.innovateuk.ifs.invite.builder.InviteOrganisationResourceBuilder.newInviteOrganisationResource;
import static org.innovateuk.ifs.invite.constant.InviteStatus.SENT;
import static org.innovateuk.ifs.organisation.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(MockitoJUnitRunner.class)
@TestPropertySource(locations = "classpath:application.properties")
public class AcceptInviteControllerTest extends AbstractApplicationMockMVCTest<AcceptInviteController> {

    @Mock
    private Validator validator;

    @Mock
    private CookieFlashMessageFilter cookieFlashMessageFilter;

    @Mock
    private RegistrationCookieService registrationCookieService;

    @Mock
    private CookieUtil cookieUtil;

    @Mock
    private UserAuthenticationService userAuthenticationService;

    @Mock
    private AcceptRejectApplicationInviteModelPopulator acceptRejectApplicationInviteModelPopulator;

    @Mock
    private ConfirmOrganisationInviteModelPopulator confirmOrganisationInviteModelPopulator;

    @Override
    protected AcceptInviteController supplyControllerUnderTest() {
        return new AcceptInviteController(organisationRestService, inviteRestService,
                acceptRejectApplicationInviteModelPopulator, confirmOrganisationInviteModelPopulator);
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
    public void inviteEntryPage() throws Exception {
        AcceptRejectApplicationInviteViewModel expectedModel =
                new AcceptRejectApplicationInviteViewModel(1L, "Evolution of the global phosphorus cycle",
                        "Empire Ltd", "Steve Smith", "Empire Ltd", "steve.smith@empire.com", true, true);

        when(acceptRejectApplicationInviteModelPopulator.populateModel(eq(invite),
                isA(InviteOrganisationResource.class))).thenReturn(expectedModel);

        mockMvc.perform(get(format("/accept-invite/%s", INVITE_HASH)))
                .andExpect(status().isOk())
                .andExpect(view().name("registration/accept-invite-new-user"))
                .andExpect(model().attribute("model", expectedModel));

        verify(registrationCookieService).deleteAllRegistrationJourneyCookies(any(HttpServletResponse.class));
        verify(inviteRestService).getInviteByHash(INVITE_HASH);
        verify(inviteRestService).getInviteOrganisationByHash(INVITE_HASH);
        verify(registrationCookieService).saveToInviteHashCookie(eq(INVITE_HASH), isA(HttpServletResponse.class));
        verify(acceptRejectApplicationInviteModelPopulator).populateModel(eq(invite),
                isA(InviteOrganisationResource.class));
    }

    @Test
    public void confirmInvitedOrganisation() throws Exception {
        OrganisationResource organisation = newOrganisationResource()
                .withOrganisationType(OrganisationTypeEnum.BUSINESS.getId()).build();

        ApplicationInviteResource applicationInvite = newApplicationInviteResource()
                .withStatus(SENT)
                .withEmail("email@test.com")
                .withInviteOrganisation(organisation.getId())
                .build();

        ConfirmOrganisationInviteOrganisationViewModel expectedModel =
                new ConfirmOrganisationInviteOrganisationViewModel("Empire Ltd", "Business", "Empire Ltd", "09422981"
                        , "steve.smith@empire.com", true, true, "/registration/register");

        when(registrationCookieService.getInviteHashCookieValue(isA(HttpServletRequest.class))).thenReturn(Optional.of(INVITE_HASH));
        when(inviteRestService.getInviteByHash(INVITE_HASH)).thenReturn(restSuccess(applicationInvite));
        when(inviteRestService.getInviteOrganisationByHash(INVITE_HASH)).thenReturn(restSuccess(
                newInviteOrganisationResource()
                        .withOrganisation(organisation.getId())
                        .build()));
        when(organisationRestService.getOrganisationByIdForAnonymousUserFlow(organisation.getId()))
                .thenReturn(restSuccess(organisation));
        when(confirmOrganisationInviteModelPopulator.populate(applicationInvite, organisation, "/registration/register")).thenReturn(expectedModel);

        mockMvc.perform(get(format("/accept-invite/confirm-invited-organisation")))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("registration/confirm-invited-organisation"))
                .andExpect(model().attribute("model", expectedModel));

        verify(registrationCookieService).getInviteHashCookieValue(isA(HttpServletRequest.class));
        verify(inviteRestService).getInviteByHash(INVITE_HASH);
        verify(inviteRestService).getInviteOrganisationByHash(INVITE_HASH);
        verify(organisationRestService).getOrganisationByIdForAnonymousUserFlow(organisation.getId());
        verify(registrationCookieService).saveToOrganisationIdCookie(eq(organisation.getId()),
                isA(HttpServletResponse.class));
        verify(confirmOrganisationInviteModelPopulator).populate(applicationInvite, organisation, "/registration/register");
    }

    @Test
    public void inviteEntryPageInvalid() throws Exception {
        mockMvc.perform(get(format("/accept-invite/%s", INVALID_INVITE_HASH)))
                .andExpect(status().isOk())
                .andExpect(view().name("url-hash-invalid"));

        verify(inviteRestService).getInviteByHash(INVALID_INVITE_HASH);
        verify(registrationCookieService).deleteOrganisationCreationCookie(any());
        verify(registrationCookieService).deleteOrganisationIdCookie(any());
        verify(registrationCookieService).deleteInviteHashCookie(any());
    }

    @Test
    public void inviteEntryPageAccepted() throws Exception {
        when(inviteRestService.getInviteOrganisationByHash(ACCEPTED_INVITE_HASH)).thenReturn(restSuccess(newInviteOrganisationResource().build()));
        mockMvc.perform(get(format("/accept-invite/%s", ACCEPTED_INVITE_HASH)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));

        verify(inviteRestService).getInviteByHash(ACCEPTED_INVITE_HASH);
        verify(registrationCookieService).deleteOrganisationCreationCookie(any());
        verify(registrationCookieService).deleteOrganisationIdCookie(any());
        verify(registrationCookieService).deleteInviteHashCookie(any());
    }
}