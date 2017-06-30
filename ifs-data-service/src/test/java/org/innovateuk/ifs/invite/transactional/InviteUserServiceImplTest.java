package org.innovateuk.ifs.invite.transactional;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.commons.error.CommonFailureKeys;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.invite.constant.InviteStatus;
import org.innovateuk.ifs.invite.domain.RoleInvite;
import org.innovateuk.ifs.notifications.resource.ExternalUserNotificationTarget;
import org.innovateuk.ifs.notifications.resource.NotificationTarget;
import org.innovateuk.ifs.project.transactional.EmailService;
import org.innovateuk.ifs.user.domain.Role;
import org.innovateuk.ifs.user.resource.AdminRoleType;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.resource.UserRoleType;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.user.builder.UserBuilder.newUser;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static org.innovateuk.ifs.invite.builder.RoleInviteBuilder.newRoleInvite;
import static org.innovateuk.ifs.user.builder.RoleBuilder.newRole;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.junit.Assert.assertTrue;


public class InviteUserServiceImplTest extends BaseServiceUnitTest<InviteUserServiceImpl> {

    @Mock
    private EmailService emailService;
    @Captor
    private ArgumentCaptor<RoleInvite> roleInviteArgumentCaptor;

    @Captor
    private ArgumentCaptor<Map<String, Object>> paramsArgumentCaptor;

    private static String webBaseUrl = "base";

    @Override
    protected InviteUserServiceImpl supplyServiceUnderTest() {
        InviteUserServiceImpl inviteService = new InviteUserServiceImpl();
        ReflectionTestUtils.setField(inviteService, "webBaseUrl", webBaseUrl);
        return inviteService;
    }

    @Test
    public void inviteInternalUserSendEmailSucceeds() throws Exception {
        UserResource userResource = newUserResource().withFirstName("a").withLastName("Bee").withEmail("a@b.com").withRolesGlobal().build();
        Role role = newRole().withName("Role1").build();
        RoleInvite expectedRoleInvite = newRoleInvite().withEmail("a@b.com").withName("a Bee").withRole(role).withStatus(InviteStatus.CREATED).withHash("").build();
        when(roleRepositoryMock.findOneByName(UserRoleType.IFS_ADMINISTRATOR.getName())).thenReturn(role);
        when(inviteRoleRepositoryMock.findByRoleIdAndEmail(role.getId(), "a@b.com")).thenReturn(emptyList());
        // hash is random, so capture RoleInvite value to verify other fields
        when(inviteRoleRepositoryMock.save(any(RoleInvite.class))).thenReturn(expectedRoleInvite);

        NotificationTarget notificationTarget = new ExternalUserNotificationTarget("a Bee", "a@b.com");
        when(emailService.sendEmail(eq(singletonList(notificationTarget)), any(), eq(InviteUserServiceImpl.Notifications.INVITE_INTERNAL_USER))).thenReturn(ServiceResult.serviceSuccess());

        when(loggedInUserSupplierMock.get()).thenReturn(newUser().build());

        expectedRoleInvite.setHash("1234");
        when(inviteRoleRepositoryMock.save(any(RoleInvite.class))).thenReturn(expectedRoleInvite);

        ServiceResult<Void> result = service.saveUserInvite(userResource, AdminRoleType.IFS_ADMINISTRATOR);

        verify(inviteRoleRepositoryMock, times(2)).save(roleInviteArgumentCaptor.capture());
        verify(emailService).sendEmail(any(), paramsArgumentCaptor.capture(), any());

        List<RoleInvite> captured = roleInviteArgumentCaptor.getAllValues();
        assertEquals("a@b.com", captured.get(0).getEmail());
        assertEquals("a Bee", captured.get(0).getName());
        assertEquals(role, captured.get(0).getTarget());
        assertEquals(InviteStatus.CREATED, captured.get(0).getStatus());

        assertEquals("a@b.com", captured.get(1).getEmail());
        assertEquals("a Bee", captured.get(1).getName());
        assertEquals(role, captured.get(1).getTarget());
        assertEquals(loggedInUserSupplierMock.get(), captured.get(1).getSentBy());
        assertFalse(ZonedDateTime.now().isBefore(captured.get(1).getSentOn()));
        assertEquals(InviteStatus.SENT, captured.get(1).getStatus());
        assertFalse(captured.get(1).getHash().isEmpty());

        Map<String, Object> capturedParams = paramsArgumentCaptor.getValue();
        assertTrue(capturedParams.containsKey("role"));
        assertTrue(capturedParams.get("role").equals("Role1"));
        assertTrue(capturedParams.containsKey("inviteUrl"));
        assertTrue(((String)capturedParams.get("inviteUrl")).startsWith(webBaseUrl + InviteUserServiceImpl.WEB_CONTEXT + "/accept-invite/"));

        assertTrue(result.isSuccess());
    }

    @Test
    public void inviteInternalUserSendEmailFails() throws Exception {
        UserResource userResource = newUserResource().withFirstName("a").withLastName("Bee").withEmail("a@b.com").withRolesGlobal().build();
        Role role = newRole().withName("Role1").build();
        RoleInvite expectedRoleInvite = newRoleInvite().withEmail("a@b.com").withName("a Bee").withRole(role).withStatus(InviteStatus.CREATED).withHash("").build();
        when(roleRepositoryMock.findOneByName(UserRoleType.IFS_ADMINISTRATOR.getName())).thenReturn(role);
        when(inviteRoleRepositoryMock.findByRoleIdAndEmail(role.getId(), "a@b.com")).thenReturn(emptyList());
        // hash is random, so capture RoleInvite value to verify other fields
        when(inviteRoleRepositoryMock.save(any(RoleInvite.class))).thenReturn(expectedRoleInvite);

        NotificationTarget notificationTarget = new ExternalUserNotificationTarget("a Bee", "a@b.com");
        when(emailService.sendEmail(eq(singletonList(notificationTarget)), any(), eq(InviteUserServiceImpl.Notifications.INVITE_INTERNAL_USER))).thenReturn(ServiceResult.serviceFailure(CommonFailureKeys.GENERAL_UNEXPECTED_ERROR));

        when(loggedInUserSupplierMock.get()).thenReturn(newUser().build());

        expectedRoleInvite.setHash("1234");
        when(inviteRoleRepositoryMock.save(any(RoleInvite.class))).thenReturn(expectedRoleInvite);

        ServiceResult<Void> result = service.saveUserInvite(userResource, AdminRoleType.IFS_ADMINISTRATOR);

        verify(inviteRoleRepositoryMock, times(1)).save(roleInviteArgumentCaptor.capture());
        verify(emailService).sendEmail(any(), paramsArgumentCaptor.capture(), any());

        List<RoleInvite> captured = roleInviteArgumentCaptor.getAllValues();
        assertEquals("a@b.com", captured.get(0).getEmail());
        assertEquals("a Bee", captured.get(0).getName());
        assertEquals(role, captured.get(0).getTarget());
        assertEquals(InviteStatus.CREATED, captured.get(0).getStatus());

        Map<String, Object> capturedParams = paramsArgumentCaptor.getValue();
        assertTrue(capturedParams.containsKey("role"));
        assertTrue(capturedParams.get("role").equals("Role1"));
        assertTrue(capturedParams.containsKey("inviteUrl"));
        assertTrue(((String)capturedParams.get("inviteUrl")).startsWith(webBaseUrl + InviteUserServiceImpl.WEB_CONTEXT + "/accept-invite/"));

        assertTrue(result.isFailure());
        assertEquals(1, result.getErrors().size());
        assertEquals(CommonFailureKeys.GENERAL_UNEXPECTED_ERROR.name(), result.getErrors().get(0).getErrorKey());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getErrors().get(0).getStatusCode());
    }

}
