package com.worth.ifs.registration;

import com.worth.ifs.BaseServiceUnitTest;
import com.worth.ifs.application.service.OrganisationService;
import com.worth.ifs.invite.resource.InviteOrganisationResource;
import com.worth.ifs.invite.resource.ApplicationInviteResource;
import com.worth.ifs.registration.service.RegistrationService;
import com.worth.ifs.registration.service.RegistrationServiceImpl;
import com.worth.ifs.user.resource.OrganisationResource;
import com.worth.ifs.user.resource.UserResource;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.Map;

import static com.worth.ifs.invite.builder.InviteOrganisationResourceBuilder.newInviteOrganisationResource;
import static com.worth.ifs.invite.builder.InviteResourceBuilder.newInviteResource;
import static com.worth.ifs.user.builder.OrganisationResourceBuilder.newOrganisationResource;
import static java.util.Arrays.asList;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.*;


/**
 * Test Class for functionality in {@link com.worth.ifs.registration.service.RegistrationServiceImpl}
 */
public class RegistrationServiceImplTest extends BaseServiceUnitTest<RegistrationService> {

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
    public void acceptInviteDifferentLogin() throws Exception {
        UserResource userOne = new UserResource();
        userOne.setEmail("email@testOne.com");

        ApplicationInviteResource inviteResource = newInviteResource().withEmail("email@testTwo.com").build();
        InviteOrganisationResource inviteOrganisationResource = newInviteOrganisationResource().build();
        Map<String, String> result = service.getInvalidInviteMessages(userOne, inviteResource, inviteOrganisationResource);
        assertEquals(1, result.size());
        assertTrue(result.containsKey("failureMessageKey"));
        assertTrue(result.containsValue("registration.LOGGED_IN_WITH_OTHER_ACCOUNT"));
    }

    @Test
    public void acceptInviteDifferentOrganisation() throws Exception {
        OrganisationResource expected = newOrganisationResource().withName("Name One").build();
        when(organisationService.getOrganisationByIdForAnonymousUserFlow(anyLong())).thenReturn(expected);

        UserResource userOne = new UserResource();
        userOne.setEmail("email@testOne.com");
        userOne.setOrganisations(asList(1L));

        ApplicationInviteResource inviteResource = newInviteResource().withEmail("email@testOne.com").build();
        InviteOrganisationResource inviteOrganisationResource = newInviteOrganisationResource().withOrganisation(2L).build();
        inviteOrganisationResource.setOrganisationNameConfirmed("Name Two");
        Map<String, String> result = service.getInvalidInviteMessages(userOne, inviteResource, inviteOrganisationResource);
        assertEquals(1, result.size());
        assertTrue(result.containsKey("failureMessageKey"));
        assertTrue(result.containsValue("registration.MULTIPLE_ORGANISATIONS"));

    }

    @Test
    public void acceptInviteDifferentOrganisationSameName() throws Exception {
        OrganisationResource expected = newOrganisationResource().withName("Name Two").build();
        when(organisationService.getOrganisationByIdForAnonymousUserFlow(anyLong())).thenReturn(expected);

        UserResource userOne = new UserResource();
        userOne.setOrganisations(asList(1L));
        userOne.setEmail("email@testOne.com");

        ApplicationInviteResource inviteResource = newInviteResource().withEmail("email@testOne.com").build();
        InviteOrganisationResource inviteOrganisationResource = newInviteOrganisationResource().withOrganisation(2L).build();
        inviteOrganisationResource.setOrganisationNameConfirmed("Name Two");
        Map<String, String> result = service.getInvalidInviteMessages(userOne, inviteResource, inviteOrganisationResource);
        assertEquals(1, result.size());
        assertTrue(result.containsKey("failureMessageKey"));
        assertTrue(result.containsValue("registration.JOINING_SAME_ORGANISATIONS"));
    }

    @Test
    public void validAcceptInvite() throws Exception {
        OrganisationResource expected = newOrganisationResource().build();
        when(organisationService.getOrganisationByIdForAnonymousUserFlow(anyLong())).thenReturn(expected);

        UserResource userOne = new UserResource();
        userOne.setEmail("email@testOne.com");

        ApplicationInviteResource inviteResource = newInviteResource().withEmail("email@testOne.com").build();
        InviteOrganisationResource inviteOrganisationResource = newInviteOrganisationResource().build();
        Map<String, String> result = service.getInvalidInviteMessages(userOne, inviteResource, inviteOrganisationResource);
        assertEquals(0, result.size());
    }
}