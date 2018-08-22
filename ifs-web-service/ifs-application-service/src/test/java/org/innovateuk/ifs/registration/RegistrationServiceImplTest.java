package org.innovateuk.ifs.registration;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.invite.resource.ApplicationInviteResource;
import org.innovateuk.ifs.invite.resource.InviteOrganisationResource;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.registration.service.RegistrationService;
import org.innovateuk.ifs.registration.service.RegistrationServiceImpl;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.OrganisationRestService;
import org.innovateuk.ifs.user.service.UserService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static java.util.Optional.of;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.invite.builder.ApplicationInviteResourceBuilder.newApplicationInviteResource;
import static org.innovateuk.ifs.invite.builder.InviteOrganisationResourceBuilder.newInviteOrganisationResource;
import static org.innovateuk.ifs.organisation.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

/**
 * Test Class for functionality in {@link org.innovateuk.ifs.registration.service.RegistrationServiceImpl}
 */
public class RegistrationServiceImplTest extends BaseServiceUnitTest<RegistrationService> {

    @Mock
    private UserService userService;

    @Mock
    private OrganisationRestService organisationRestService;

    @Before
    public void setUp() {
        super.setup();
    }

    @Override
    protected RegistrationService supplyServiceUnderTest() {
        return new RegistrationServiceImpl();
    }


    @Test
    public void acceptInviteDifferentOrganisation() {
        OrganisationResource expected = newOrganisationResource().withName("Name One").build();

        UserResource userOne = newUserResource().withEmail("email@testOne.com").build();

        ApplicationInviteResource inviteResource = newApplicationInviteResource().withEmail("email@testOne.com").build();
        InviteOrganisationResource inviteOrganisationResource = newInviteOrganisationResource().withOrganisation(2L).build();
        inviteOrganisationResource.setOrganisationNameConfirmed("Name Two");

        when(organisationRestService.getOrganisationByUserId(userOne.getId())).thenReturn(restSuccess(expected));
        when(userService.findUserByEmail(userOne.getEmail())).thenReturn(of(userOne));

        assertTrue(service.isInviteForDifferentOrganisationThanUsersAndDifferentName(inviteResource, inviteOrganisationResource));
    }

    @Test
    public void acceptInviteDifferentOrganisationSameName() {
        OrganisationResource expected = newOrganisationResource().withName("Name Two").build();

        UserResource userOne = newUserResource().withEmail("email@testOne.com").build();

        ApplicationInviteResource inviteResource = newApplicationInviteResource().withEmail("email@testOne.com").build();
        InviteOrganisationResource inviteOrganisationResource = newInviteOrganisationResource().withOrganisation(2L).build();
        inviteOrganisationResource.setOrganisationNameConfirmed("Name Two");

        when(organisationRestService.getOrganisationByUserId(userOne.getId())).thenReturn(restSuccess(expected));
        when(userService.findUserByEmail(userOne.getEmail())).thenReturn(of(userOne));

        assertTrue(service.isInviteForDifferentOrganisationThanUsersButSameName(inviteResource, inviteOrganisationResource));
    }

    @Test
    public void validAcceptInvite() {
        OrganisationResource expected = newOrganisationResource().build();

        UserResource userOne = newUserResource().withEmail("email@testOne.com").build();

        ApplicationInviteResource inviteResource = newApplicationInviteResource().withEmail("email@testOne.com").build();
        InviteOrganisationResource inviteOrganisationResource = newInviteOrganisationResource().build();

        when(organisationRestService.getOrganisationByUserId(userOne.getId())).thenReturn(restSuccess(expected));
        when(userService.findUserByEmail(userOne.getEmail())).thenReturn(of(userOne));

        assertFalse(service.isInviteForDifferentOrganisationThanUsersButSameName(inviteResource, inviteOrganisationResource));
        assertFalse(service.isInviteForDifferentOrganisationThanUsersAndDifferentName(inviteResource, inviteOrganisationResource));
    }
}
