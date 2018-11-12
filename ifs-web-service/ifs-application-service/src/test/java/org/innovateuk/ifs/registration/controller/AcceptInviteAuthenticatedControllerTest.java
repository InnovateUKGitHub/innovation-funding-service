package org.innovateuk.ifs.registration.controller;

import org.innovateuk.ifs.AbstractApplicationMockMVCTest;
import org.innovateuk.ifs.invite.resource.ApplicationInviteResource;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.organisation.resource.OrganisationTypeEnum;
import org.innovateuk.ifs.registration.populator.ConfirmOrganisationInviteModelPopulator;
import org.innovateuk.ifs.registration.service.RegistrationCookieService;
import org.innovateuk.ifs.registration.viewmodel.ConfirmOrganisationInviteOrganisationViewModel;
import org.innovateuk.ifs.util.CookieUtil;
import org.junit.Test;
import org.mockito.Mock;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.invite.builder.ApplicationInviteResourceBuilder.newApplicationInviteResource;
import static org.innovateuk.ifs.invite.builder.InviteOrganisationResourceBuilder.newInviteOrganisationResource;
import static org.innovateuk.ifs.invite.constant.InviteStatus.SENT;
import static org.innovateuk.ifs.organisation.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class AcceptInviteAuthenticatedControllerTest extends AbstractApplicationMockMVCTest<AcceptInviteAuthenticatedController> {

    @Mock
    private ConfirmOrganisationInviteModelPopulator confirmOrganisationInviteModelPopulator;

    @Mock
    private CookieUtil cookieUtil;

    @Mock
    private RegistrationCookieService registrationCookieService;

    @Override
    protected AcceptInviteAuthenticatedController supplyControllerUnderTest() {
        return new AcceptInviteAuthenticatedController(inviteRestService, organisationRestService,
                confirmOrganisationInviteModelPopulator, userRestService, cookieUtil);
    }

    @Test
    public void existingUserAndNewOrganisation() throws Exception {
        setLoggedInUser(newUserResource()
                .withEmail("email@test.com")
                .build());

        OrganisationResource organisation = newOrganisationResource()
                .withOrganisationType(OrganisationTypeEnum.BUSINESS.getId()).build();

        ApplicationInviteResource applicationInvite = newApplicationInviteResource()
                .withStatus(SENT)
                .withEmail("email@test.com")
                .withInviteOrganisation(organisation.getId())
                .build();

        ConfirmOrganisationInviteOrganisationViewModel expectedModel =
                new ConfirmOrganisationInviteOrganisationViewModel("Empire Ltd", "Business", "Empire Ltd", "09422981"
                        , "steve.smith@empire.com", true, true, "/accept-invite-authenticated/confirm-invited-organisation/confirm");

        when(registrationCookieService.getInviteHashCookieValue(isA(HttpServletRequest.class))).thenReturn(Optional.of(INVITE_HASH));
        when(inviteRestService.getInviteByHash(INVITE_HASH)).thenReturn(restSuccess(applicationInvite));
        when(inviteRestService.getInviteOrganisationByHash(INVITE_HASH)).thenReturn(restSuccess(
                newInviteOrganisationResource()
                        .withOrganisation(organisation.getId())
                        .build()));
        when(organisationRestService.getOrganisationById(organisation.getId())).thenReturn(restSuccess(organisation));
        when(confirmOrganisationInviteModelPopulator.populate(applicationInvite, organisation, "/accept-invite" +
                "-authenticated/confirm-invited-organisation/confirm")).thenReturn(expectedModel);

        mockMvc.perform(get("/accept-invite-authenticated/confirm-invited-organisation"))
                .andExpect(status().isOk())
                .andExpect(view().name("registration/confirm-registered-organisation"))
                .andExpect(model().attribute("model", expectedModel));

        verify(registrationCookieService).getInviteHashCookieValue(isA(HttpServletRequest.class));
        verify(inviteRestService).getInviteByHash(INVITE_HASH);
        verify(inviteRestService).getInviteOrganisationByHash(INVITE_HASH);
        verify(organisationRestService).getOrganisationById(organisation.getId());
        verify(confirmOrganisationInviteModelPopulator).populate(applicationInvite, organisation, "/accept-invite-authenticated/confirm-invited-organisation/confirm");
    }

}