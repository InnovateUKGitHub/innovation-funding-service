package org.innovateuk.ifs.user.service;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.commons.error.CommonErrors;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.exception.GeneralUnexpectedErrorException;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.List;
import java.util.Optional;

import static java.util.Collections.singletonList;
import static junit.framework.Assert.assertEquals;
import static org.innovateuk.ifs.commons.error.CommonErrors.internalServerErrorError;
import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.rest.RestResult.restFailure;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.user.builder.ProcessRoleResourceBuilder.newProcessRoleResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.user.resource.Role.*;
import static org.junit.Assert.*;
import static org.mockito.AdditionalMatchers.not;
import static org.mockito.AdditionalMatchers.or;
import static org.mockito.Mockito.*;

/**
 * Test Class for functionality in {@link UserServiceImpl}
 */
public class UserServiceImplTest extends BaseServiceUnitTest<UserService> {
    private static final String EMAIL_THAT_EXISTS_FOR_USER = "sample@me.com";

    private static final String EMAIL_THAT_EXISTS_FOR_USER_BUT_CAUSES_OTHER_ERROR = "i-am-bad@me.com";
    @Mock
    private UserRestService userRestService;

    private Role roleResource;
    private Long applicationId;
    private UserResource leadUser;
    private ApplicationResource application;
    private List<ProcessRoleResource> processRoles;

    @Before
    public void setUp() {
        super.setup();

        applicationId = 123L;
        leadUser = newUserResource().withId(87L).build();
        UserResource collaborator = newUserResource().withId(34L).build();

        application = new ApplicationResource();
        application.setId(applicationId);

        processRoles = newProcessRoleResource()
                .withApplication(applicationId)
                .withUser(leadUser, collaborator)
                .withRole(LEADAPPLICANT, COLLABORATOR)
                .withOrganisation(13L, 24L)
                .build(2);

        when(userRestService.resendEmailVerificationNotification(eq(EMAIL_THAT_EXISTS_FOR_USER))).thenReturn(restSuccess());
        when(userRestService.resendEmailVerificationNotification(eq(EMAIL_THAT_EXISTS_FOR_USER_BUT_CAUSES_OTHER_ERROR))).thenReturn(restFailure(internalServerErrorError()));
        when(userRestService.resendEmailVerificationNotification(not(or(eq(EMAIL_THAT_EXISTS_FOR_USER), eq(EMAIL_THAT_EXISTS_FOR_USER_BUT_CAUSES_OTHER_ERROR))))).thenReturn(restFailure(notFoundError(UserResource.class)));
    }

    @Override
    protected UserService supplyServiceUnderTest() {
        return new UserServiceImpl();
    }

    @Test
    public void resendEmailVerificationNotification() {
        service.resendEmailVerificationNotification(EMAIL_THAT_EXISTS_FOR_USER);
        verify(userRestService, only()).resendEmailVerificationNotification(EMAIL_THAT_EXISTS_FOR_USER);
    }

    @Test(expected = Test.None.class /* No exception expected here even though the mock returns an ObjectNotFoundException. We don't want to reveal that an email address was not recognised. */)
    public void resendEmailVerificationNotification_notExists() {
        // Try sending the verification link to an email address which doesn't exist for a user
        final String email = "i-dont-exist@me.com";

        service.resendEmailVerificationNotification(email);
        verify(userRestService, only()).resendEmailVerificationNotification(email);
    }

    @Test(expected = GeneralUnexpectedErrorException.class)
    public void resendEmailVerificationNotification_otherError() {
        // Try sending the verification link to an email address that exists but cause another error to occur

        service.resendEmailVerificationNotification(EMAIL_THAT_EXISTS_FOR_USER_BUT_CAUSES_OTHER_ERROR);
        verify(userRestService, only()).resendEmailVerificationNotification(EMAIL_THAT_EXISTS_FOR_USER_BUT_CAUSES_OTHER_ERROR);
    }

    @Test
    public void userHasApplicationForCompetition() {
        Long userId = 1L;
        Long competitionId = 2L;
        Boolean expected = true;

        when(userRestService.userHasApplicationForCompetition(userId, competitionId)).thenReturn(restSuccess(expected));

        Boolean response = service.userHasApplicationForCompetition(userId, competitionId);
        assertEquals(expected, response);

        verify(userRestService, only()).userHasApplicationForCompetition(userId, competitionId);
    }

    @Test
    public void existsAndHasRole() {
        Long userId = 1L;
        roleResource = COMP_ADMIN;
        UserResource userResource = newUserResource()
                .withId(userId)
                .withRolesGlobal(singletonList(roleResource))
                .build();

        when(userRestService.retrieveUserById(userId)).thenReturn(restSuccess(userResource));

        assertTrue(service.existsAndHasRole(userId, COMP_ADMIN));
    }

    @Test
    public void existsAndHasRole_wrongRole() {
        Long userId = 1L;
        roleResource = Role.FINANCE_CONTACT;
        UserResource userResource = newUserResource()
                .withId(userId)
                .withRolesGlobal(singletonList(roleResource))
                .build();

        when(userRestService.retrieveUserById(userId)).thenReturn(restSuccess(userResource));

        assertFalse(service.existsAndHasRole(userId, COMP_ADMIN));
    }

    @Test
    public void existsAndHasRole_userNotFound() {
        Long userId = 1L;

        Error error = CommonErrors.notFoundError(UserResource.class, userId);
        when(userRestService.retrieveUserById(userId)).thenReturn(restFailure(error));

        assertFalse(service.existsAndHasRole(userId, COMP_ADMIN));
    }

    @Test
    public void isLeadApplicant() {
        when(userRestService.findProcessRole(applicationId)).thenReturn(restSuccess(processRoles));
        assertTrue(service.isLeadApplicant(leadUser.getId(), application));
    }

    @Test
    public void getLeadApplicantProcessRole() {
        when(userRestService.findProcessRole(applicationId)).thenReturn(restSuccess(processRoles));
        assertEquals(processRoles.get(0), service.getLeadApplicantProcessRole(applicationId));
    }

    @Test
    public void getOrganisationProcessRoles() {
        when(userRestService.findProcessRole(applicationId)).thenReturn(restSuccess(processRoles));
        List<ProcessRoleResource> result = service.getOrganisationProcessRoles(application, 13L);

        verify(userRestService, times(1)).findProcessRole(applicationId);
        verifyNoMoreInteractions(userRestService);
        assertEquals(singletonList(processRoles.get(0)), result);
    }

    @Test
    public void getLeadPartnerOrganisationProcessRoles() {
        when(userRestService.findProcessRole(applicationId)).thenReturn(restSuccess(processRoles));
        List<ProcessRoleResource> result = service.getLeadPartnerOrganisationProcessRoles(application);

        verify(userRestService, times(2)).findProcessRole(applicationId);
        verifyNoMoreInteractions(userRestService);
        assertEquals(singletonList(processRoles.get(0)), result);
    }

    @Test
    public void getUserOrganisationId() {
        when(userRestService.findProcessRole(leadUser.getId(), applicationId)).thenReturn(restSuccess(processRoles.get(0)));
        Long result = service.getUserOrganisationId(leadUser.getId(), applicationId);

        verify(userRestService, times(1)).findProcessRole(leadUser.getId(), applicationId);
        verifyNoMoreInteractions(userRestService);
        assertEquals(processRoles.get(0).getOrganisationId(), result);
    }

    @Test
    public void updateDetails() {
        when(userRestService.updateDetails(anyLong(), anyString(), anyString(), anyString(), anyString(), anyString(), anyBoolean())).thenReturn(restSuccess(new UserResource()));
        ServiceResult<UserResource> result = service.updateDetails(anyLong(), anyString(), anyString(), anyString(), anyString(), anyString(), anyBoolean());

        verify(userRestService, times(1)).updateDetails(anyLong(), anyString(), anyString(), anyString(), anyString(), anyString(), anyBoolean());
        verifyNoMoreInteractions(userRestService);
        assertTrue(result.isSuccess());
    }

    @Test
    public void sendPasswordResetNotification() {
        String email = "bill@Email.com";
        service.sendPasswordResetNotification(email);

        verify(userRestService).sendPasswordResetNotification(email);
    }

    @Test
    public void findUserByEmail() {
        String email = "bill@Email.com";
        UserResource user = newUserResource().withEmail(email).build();
        when(userRestService.findUserByEmail(email)).thenReturn(restSuccess(user));
        Optional<UserResource> result = service.findUserByEmail(email);

        verify(userRestService).findUserByEmail(email);
        verifyNoMoreInteractions(userRestService);
        assertEquals(user, result.get());
    }
}