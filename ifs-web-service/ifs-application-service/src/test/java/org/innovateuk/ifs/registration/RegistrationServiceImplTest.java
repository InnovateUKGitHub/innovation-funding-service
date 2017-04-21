package org.innovateuk.ifs.registration;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.application.service.OrganisationService;
import org.innovateuk.ifs.invite.resource.ApplicationInviteResource;
import org.innovateuk.ifs.invite.resource.InviteOrganisationResource;
import org.innovateuk.ifs.registration.service.RegistrationService;
import org.innovateuk.ifs.registration.service.RegistrationServiceImpl;
import org.innovateuk.ifs.user.resource.OrganisationResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.UserService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static java.util.Optional.of;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.innovateuk.ifs.invite.builder.InviteOrganisationResourceBuilder.newInviteOrganisationResource;
import static org.innovateuk.ifs.invite.builder.InviteResourceBuilder.newInviteResource;
import static org.innovateuk.ifs.user.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.when;

/**
 * Test Class for functionality in {@link org.innovateuk.ifs.registration.service.RegistrationServiceImpl}
 */
public class RegistrationServiceImplTest extends BaseServiceUnitTest<RegistrationService> {

    @Mock
    private UserService userService;

    @Mock
    private OrganisationService organisationService;

    @Override
    @Before
    public void setUp() {
        super.setUp();
    }

    @Override
    protected RegistrationService supplyServiceUnderTest() {
        return new RegistrationServiceImpl();
    }


    @Test
    public void acceptInviteDifferentOrganisation() throws Exception {
        OrganisationResource expected = newOrganisationResource().withName("Name One").build();

        UserResource userOne = new UserResource();
        userOne.setEmail("email@testOne.com");

        ApplicationInviteResource inviteResource = newInviteResource().withEmail("email@testOne.com").build();
        InviteOrganisationResource inviteOrganisationResource = newInviteOrganisationResource().withOrganisation(2L).build();
        inviteOrganisationResource.setOrganisationNameConfirmed("Name Two");

        when(organisationService.getOrganisationForUser(anyLong())).thenReturn(expected);
        when(userService.findUserByEmail(userOne.getEmail())).thenReturn(of(userOne));

        assertTrue(service.isInviteForDifferentOrganisationThanUsersAndDifferentName(inviteResource, inviteOrganisationResource));
    }

    @Test
    public void acceptInviteDifferentOrganisationSameName() throws Exception {
        OrganisationResource expected = newOrganisationResource().withName("Name Two").build();
        UserResource userOne = new UserResource();
        userOne.setEmail("email@testOne.com");

        ApplicationInviteResource inviteResource = newInviteResource().withEmail("email@testOne.com").build();
        InviteOrganisationResource inviteOrganisationResource = newInviteOrganisationResource().withOrganisation(2L).build();
        inviteOrganisationResource.setOrganisationNameConfirmed("Name Two");

        when(organisationService.getOrganisationForUser(anyLong())).thenReturn(expected);
        when(userService.findUserByEmail(userOne.getEmail())).thenReturn(of(userOne));

        assertTrue(service.isInviteForDifferentOrganisationThanUsersButSameName(inviteResource, inviteOrganisationResource));
    }

    @Test
    public void validAcceptInvite() throws Exception {
        OrganisationResource expected = newOrganisationResource().build();

        UserResource userOne = new UserResource();
        userOne.setEmail("email@testOne.com");

        ApplicationInviteResource inviteResource = newInviteResource().withEmail("email@testOne.com").build();
        InviteOrganisationResource inviteOrganisationResource = newInviteOrganisationResource().build();

        when(organisationService.getOrganisationForUser(anyLong())).thenReturn(expected);
        when(userService.findUserByEmail(userOne.getEmail())).thenReturn(of(userOne));

        assertFalse(service.isInviteForDifferentOrganisationThanUsersButSameName(inviteResource, inviteOrganisationResource));
        assertFalse(service.isInviteForDifferentOrganisationThanUsersAndDifferentName(inviteResource, inviteOrganisationResource));
    }
}
