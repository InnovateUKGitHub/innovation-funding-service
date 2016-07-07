package com.worth.ifs.registration;

import com.worth.ifs.BaseServiceUnitTest;
import com.worth.ifs.application.service.OrganisationService;
import com.worth.ifs.commons.error.exception.GeneralUnexpectedErrorException;
import com.worth.ifs.invite.resource.InviteOrganisationResource;
import com.worth.ifs.invite.resource.InviteResource;
import com.worth.ifs.registration.service.RegistrationService;
import com.worth.ifs.registration.service.RegistrationServiceImpl;
import com.worth.ifs.user.resource.OrganisationResource;
import com.worth.ifs.user.resource.UserResource;
import com.worth.ifs.user.resource.UserRoleType;
import com.worth.ifs.user.service.UserRestService;
import com.worth.ifs.user.service.UserService;
import com.worth.ifs.user.service.UserServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static com.worth.ifs.commons.error.CommonErrors.internalServerErrorError;
import static com.worth.ifs.commons.error.CommonErrors.notFoundError;
import static com.worth.ifs.commons.rest.RestResult.restFailure;
import static com.worth.ifs.commons.rest.RestResult.restSuccess;
import static com.worth.ifs.invite.builder.InviteOrganisationResourceBuilder.newInviteOrganisationResource;
import static com.worth.ifs.invite.builder.InviteResourceBuilder.newInviteResource;
import static com.worth.ifs.user.builder.OrganisationResourceBuilder.newOrganisationResource;
import static java.util.Arrays.asList;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.mockito.AdditionalMatchers.not;
import static org.mockito.AdditionalMatchers.or;
import static org.mockito.Matchers.eq;
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

        InviteResource inviteResource = newInviteResource().withEmail("email@testTwo.com").build();
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

        InviteResource inviteResource = newInviteResource().withEmail("email@testOne.com").build();
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

        InviteResource inviteResource = newInviteResource().withEmail("email@testOne.com").build();
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

        InviteResource inviteResource = newInviteResource().withEmail("email@testOne.com").build();
        InviteOrganisationResource inviteOrganisationResource = newInviteOrganisationResource().build();
        Map<String, String> result = service.getInvalidInviteMessages(userOne, inviteResource, inviteOrganisationResource);
        assertEquals(0, result.size());
    }
}