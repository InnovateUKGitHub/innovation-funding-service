package org.innovateuk.ifs.invite.transactional;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.category.domain.InnovationArea;
import org.innovateuk.ifs.category.repository.InnovationAreaRepository;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.invite.builder.RoleInviteBuilder;
import org.innovateuk.ifs.invite.domain.ApplicationInvite;
import org.innovateuk.ifs.invite.domain.InviteOrganisation;
import org.innovateuk.ifs.invite.domain.ProjectUserInvite;
import org.innovateuk.ifs.invite.domain.RoleInvite;
import org.innovateuk.ifs.invite.mapper.RoleInviteMapper;
import org.innovateuk.ifs.invite.repository.ApplicationInviteRepository;
import org.innovateuk.ifs.invite.repository.ProjectUserInviteRepository;
import org.innovateuk.ifs.invite.repository.RoleInviteRepository;
import org.innovateuk.ifs.invite.resource.ExternalInviteResource;
import org.innovateuk.ifs.invite.resource.RoleInvitePageResource;
import org.innovateuk.ifs.invite.resource.RoleInviteResource;
import org.innovateuk.ifs.notifications.resource.Notification;
import org.innovateuk.ifs.notifications.resource.NotificationTarget;
import org.innovateuk.ifs.notifications.resource.SystemNotificationSource;
import org.innovateuk.ifs.notifications.resource.UserNotificationTarget;
import org.innovateuk.ifs.notifications.service.NotificationService;
import org.innovateuk.ifs.organisation.domain.Organisation;
import org.innovateuk.ifs.organisation.repository.SimpleOrganisationRepository;
import org.innovateuk.ifs.project.core.domain.Project;
import org.innovateuk.ifs.security.LoggedInUserSupplier;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.repository.UserRepository;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.SearchCategory;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.time.ZonedDateTime.now;
import static java.time.temporal.ChronoUnit.MILLIS;
import static java.util.Collections.singletonList;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.lessThanOrEqualTo;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.category.builder.InnovationAreaBuilder.newInnovationArea;
import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.*;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.invite.builder.ApplicationInviteBuilder.newApplicationInvite;
import static org.innovateuk.ifs.invite.builder.InviteOrganisationBuilder.newInviteOrganisation;
import static org.innovateuk.ifs.invite.builder.ProjectUserInviteBuilder.newProjectUserInvite;
import static org.innovateuk.ifs.invite.builder.RoleInviteBuilder.newRoleInvite;
import static org.innovateuk.ifs.invite.builder.RoleInviteResourceBuilder.newRoleInviteResource;
import static org.innovateuk.ifs.invite.constant.InviteStatus.CREATED;
import static org.innovateuk.ifs.invite.constant.InviteStatus.SENT;
import static org.innovateuk.ifs.invite.transactional.InviteUserServiceImpl.Notifications.INVITE_EXTERNAL_USER;
import static org.innovateuk.ifs.invite.transactional.InviteUserServiceImpl.Notifications.INVITE_INTERNAL_USER;
import static org.innovateuk.ifs.notifications.resource.NotificationMedium.EMAIL;
import static org.innovateuk.ifs.organisation.builder.OrganisationBuilder.newOrganisation;
import static org.innovateuk.ifs.project.core.builder.ProjectBuilder.newProject;
import static org.innovateuk.ifs.user.builder.UserBuilder.newUser;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.user.resource.Role.*;
import static org.innovateuk.ifs.util.MapFunctions.asMap;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class InviteUserServiceImplTest extends BaseServiceUnitTest<InviteUserServiceImpl> {

    @Mock
    private NotificationService notificationService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleInviteRepository roleInviteRepository;

    @Mock
    private LoggedInUserSupplier loggedInUserSupplier;

    @Mock
    private RoleInviteMapper roleInviteMapper;

    @Mock
    private ApplicationInviteRepository applicationInviteRepository;

    @Mock
    private ProjectUserInviteRepository projectUserInviteRepository;

    @Mock
    private SystemNotificationSource systemNotificationSource;

    @Mock
    private SimpleOrganisationRepository simpleOrganisationRepository;

    @Captor
    private ArgumentCaptor<RoleInvite> roleInviteArgumentCaptor;

    private static String webBaseUrl = "base";

    private static String ktaUserEmailDomain = "ktn-uk.org";

    private UserResource invitedUser = null;

    @Mock
    private InnovationAreaRepository innovationAreaRepository;

    @Before
    public void setUp() {

        invitedUser = newUserResource()
                .withFirstName("Astle")
                .withLastName("Pimenta")
                .withEmail("Astle.Pimenta@iuk.ukri.org")
                .build();

        ReflectionTestUtils.setField(service, "internalUserEmailDomains", "iuk.ukri.org");
    }

    @Override
    protected InviteUserServiceImpl supplyServiceUnderTest() {

        InviteUserServiceImpl inviteService = new InviteUserServiceImpl();
        ReflectionTestUtils.setField(inviteService, "webBaseUrl", webBaseUrl);
        ReflectionTestUtils.setField(inviteService, "ktaUserEmailDomain", ktaUserEmailDomain);

        return inviteService;
    }

    @Test
    public void saveUserInviteWhenUserDetailsMissing() throws Exception {

        UserResource invitedUser = newUserResource().build();

        ServiceResult<Void> result = service.saveUserInvite(invitedUser, Role.SUPPORT, "");
        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(USER_ROLE_INVITE_INVALID));
    }

    @Test
    public void saveUserInviteWhenUserRoleIsNotSpecified() throws Exception {

        ServiceResult<Void> result = service.saveUserInvite(invitedUser, null, "");
        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(USER_ROLE_INVITE_INVALID));
    }

    @Test
    public void saveInternalUserInviteWhenRoleSpecifiedIsNotInternalRole() {

        ServiceResult<Void> result = service.saveUserInvite(invitedUser, APPLICANT, "");

        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(NOT_AN_INTERNAL_USER_ROLE));
    }

    @Test
    public void saveInternalUserInviteWhenEmailDomainIsIncorrect() throws Exception {

        Role role = Role.SUPPORT;
        invitedUser.setEmail("Astle.Pimenta@gmail.com");

        ServiceResult<Void> result = service.saveUserInvite(invitedUser, role, "");
        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(USER_ROLE_INVITE_INVALID_EMAIL));
    }

    @Test
    public void saveInternalUserInviteWhenUserAlreadyInvited() throws Exception {

        RoleInvite roleInvite = new RoleInvite();

        when(userRepository.findByEmail(invitedUser.getEmail())).thenReturn(Optional.empty());
        when(roleInviteRepository.findByEmail(invitedUser.getEmail())).thenReturn(singletonList(roleInvite));

        ServiceResult<Void> result = service.saveUserInvite(invitedUser, SUPPORT, "");
        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(USER_ROLE_INVITE_TARGET_USER_ALREADY_INVITED));

    }

    @Test
    public void inviteInternalUserSendEmailSucceeds() throws Exception {

        Role role = Role.IFS_ADMINISTRATOR;

        RoleInvite expectedRoleInvite = newRoleInvite().
                withEmail("Astle.Pimenta@iuk.ukri.org").
                withName("Astle Pimenta").
                withRole(role).
                withStatus(CREATED).
                withHash("1234").
                build();

        // hash is random, so capture RoleInvite value to verify other fields
        when(roleInviteRepository.save(any(RoleInvite.class))).thenReturn(expectedRoleInvite);

        NotificationTarget notificationTarget = new UserNotificationTarget("Astle Pimenta", "Astle.Pimenta@iuk.ukri.org");
        Map<String, Object> expectedNotificationArgs = asMap(
                "role", role.getDisplayName(),
                "inviteUrl", webBaseUrl + InviteUserServiceImpl.INTERNAL_USER_WEB_CONTEXT + "/" + expectedRoleInvite.getHash() + "/register"
        );

        Notification expectedNotification = new Notification(systemNotificationSource, notificationTarget, INVITE_INTERNAL_USER, expectedNotificationArgs);

        when(notificationService.sendNotificationWithFlush(expectedNotification, EMAIL)).thenReturn(serviceSuccess());

        when(loggedInUserSupplier.get()).thenReturn(newUser().build());

        when(roleInviteRepository.save(any(RoleInvite.class))).thenReturn(expectedRoleInvite);

        when(userRepository.findByEmail(invitedUser.getEmail())).thenReturn(Optional.empty());

        ServiceResult<Void> result = service.saveUserInvite(invitedUser, IFS_ADMINISTRATOR, "");

        assertTrue(result.isSuccess());

        verify(roleInviteRepository, times(2)).save(roleInviteArgumentCaptor.capture());
        verify(notificationService).sendNotificationWithFlush(expectedNotification, EMAIL);

        List<RoleInvite> captured = roleInviteArgumentCaptor.getAllValues();
        assertEquals("Astle.Pimenta@iuk.ukri.org", captured.get(0).getEmail());
        assertEquals("Astle Pimenta", captured.get(0).getName());
        assertEquals(role, captured.get(0).getTarget());
        assertEquals(CREATED, captured.get(0).getStatus());

        assertEquals("Astle.Pimenta@iuk.ukri.org", captured.get(1).getEmail());
        assertEquals("Astle Pimenta", captured.get(1).getName());
        assertEquals(role, captured.get(1).getTarget());
        assertEquals(loggedInUserSupplier.get(), captured.get(1).getSentBy());
        assertFalse(now().isBefore(captured.get(1).getSentOn()));
        assertEquals(SENT, captured.get(1).getStatus());
        assertFalse(captured.get(1).getHash().isEmpty());
    }

    @Test
    public void inviteInternalUserSendEmailFails() throws Exception {

        Role role = SUPPORT;

        RoleInvite expectedRoleInvite = newRoleInvite()
                .withEmail("Astle.Pimenta@iuk.ukri.org")
                .withName("Astle Pimenta")
                .withRole(role)
                .withStatus(CREATED).withHash("")
                .build();

        // hash is random, so capture RoleInvite value to verify other fields
        when(roleInviteRepository.save(any(RoleInvite.class))).thenReturn(expectedRoleInvite);
        when(userRepository.findByEmail(invitedUser.getEmail())).thenReturn(Optional.empty());

        NotificationTarget notificationTarget = new UserNotificationTarget("Astle Pimenta", "Astle.Pimenta@iuk.ukri.org");
        Map<String, Object> expectedNotificationArgs = asMap(
                "role", role.getDisplayName(),
                "inviteUrl", webBaseUrl + InviteUserServiceImpl.INTERNAL_USER_WEB_CONTEXT + "/" + expectedRoleInvite.getHash() + "/register"
        );
        Notification expectedNotification = new Notification(systemNotificationSource, notificationTarget, INVITE_INTERNAL_USER, expectedNotificationArgs);

        when(notificationService.sendNotificationWithFlush(expectedNotification, EMAIL)).thenReturn(serviceFailure(GENERAL_UNEXPECTED_ERROR));

        ServiceResult<Void> result = service.saveUserInvite(invitedUser, SUPPORT, "");
        assertTrue(result.isFailure());

        verify(roleInviteRepository, times(1)).save(roleInviteArgumentCaptor.capture());
        verify(notificationService).sendNotificationWithFlush(expectedNotification, EMAIL);

        List<RoleInvite> captured = roleInviteArgumentCaptor.getAllValues();
        assertEquals("Astle.Pimenta@iuk.ukri.org", captured.get(0).getEmail());
        assertEquals("Astle Pimenta", captured.get(0).getName());
        assertEquals(role, captured.get(0).getTarget());
        assertEquals(CREATED, captured.get(0).getStatus());

        assertEquals(1, result.getErrors().size());
        assertEquals(GENERAL_UNEXPECTED_ERROR.name(), result.getErrors().get(0).getErrorKey());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getErrors().get(0).getStatusCode());
    }

    @Test
    public void saveInternalUserInviteWhenEmailAlreadyTaken() throws Exception {

        Role role = Role.IFS_ADMINISTRATOR;

        RoleInvite expectedRoleInvite = newRoleInvite()
                .withEmail("Astle.Pimenta@iuk.ukri.org")
                .withName("Astle Pimenta")
                .withRole(role)
                .withStatus(CREATED)
                .withHash("")
                .build();

        // hash is random, so capture RoleInvite value to verify other fields
        when(roleInviteRepository.save(any(RoleInvite.class))).thenReturn(expectedRoleInvite);

        when(loggedInUserSupplier.get()).thenReturn(newUser().build());

        expectedRoleInvite.setHash("1234");
        when(roleInviteRepository.save(any(RoleInvite.class))).thenReturn(expectedRoleInvite);
        when(userRepository.findByEmail(invitedUser.getEmail())).thenReturn(Optional.of(newUser().build()));

        ServiceResult<Void> result = service.saveUserInvite(invitedUser, IFS_ADMINISTRATOR, "");

        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(USER_ROLE_INVITE_EMAIL_TAKEN));
        verify(roleInviteRepository, never()).save(Mockito.any(RoleInvite.class));
        verify(notificationService, never()).sendNotificationWithFlush(any(Notification.class), eq(EMAIL));
    }

    @Test
    public void testGetInvite(){

        RoleInvite roleInvite = newRoleInvite().build();

        when(roleInviteRepository.getByHash("SomeInviteHash")).thenReturn(roleInvite);
        when(roleInviteMapper.mapToResource(roleInvite)).thenReturn(newRoleInviteResource().build());
        ServiceResult<RoleInviteResource> result = service.getInvite("SomeInviteHash");
        assertTrue(result.isSuccess());
    }

    @Test
    public void testCheckExistingUser(){

        RoleInvite roleInvite = newRoleInvite().build();
        when(roleInviteRepository.getByHash("SomeInviteHash")).thenReturn(roleInvite);
        when(userRepository.findByEmail(roleInvite.getEmail())).thenReturn(Optional.of(newUser().build()));
        ServiceResult<Boolean> result = service.checkExistingUser("SomeInviteHash");
        assertTrue(result.isSuccess());
        assertTrue(result.getSuccess());
    }

    @Test
    public void findPendingInternalUsers() {

        Pageable pageable = PageRequest.of(0, 5);

        Role role = Role.IFS_ADMINISTRATOR;

        List<RoleInvite> roleInvites = RoleInviteBuilder.newRoleInvite()
                .withRole(role)
                .withName("Arden Pimenta")
                .withEmail("Arden.Pimenta@innovateuk.test")
                .build(4);
        Page<RoleInvite> page = new PageImpl<>(roleInvites, pageable, 4L);

        RoleInviteResource roleInviteResource = new RoleInviteResource();
        roleInviteResource.setName("Arden Pimenta");
        roleInviteResource.setEmail("Arden.Pimenta@innovateuk.test");

        when(roleInviteRepository.findByEmailContainsAndStatus("", SENT, pageable)).thenReturn(page);
        when(roleInviteMapper.mapToResource(Mockito.any(RoleInvite.class))).thenReturn(roleInviteResource);

        ServiceResult<RoleInvitePageResource> result = service.findPendingInternalUserInvites("", pageable);
        assertTrue(result.isSuccess());

        RoleInvitePageResource resultObject = result.getSuccess();
        assertEquals(5, resultObject.getSize());
        assertEquals(1, resultObject.getTotalPages());
        assertEquals(4, resultObject.getContent().size());
        assertEquals(roleInviteResource, resultObject.getContent().get(0));

    }

    @Test
    public void findExternalInvitesWhenSearchStringIsNull(){

        String searchString = null;
        SearchCategory searchCategory = SearchCategory.NAME;

        ServiceResult<List<ExternalInviteResource>> result = service.findExternalInvites(searchString, searchCategory);

        assertTrue(result.isFailure());
        assertEquals(USER_SEARCH_INVALID_INPUT_LENGTH.getErrorKey(), result.getFailure().getErrors().get(0).getErrorKey());

        verify(applicationInviteRepository, never()).findByNameLikeAndStatusIn(searchString, EnumSet.of(CREATED, SENT));
        verify(applicationInviteRepository, never()).findByInviteOrganisationOrganisationNameLikeAndStatusIn(searchString, EnumSet.of(CREATED, SENT));
        verify(applicationInviteRepository, never()).findByEmailLikeAndStatusIn(searchString, EnumSet.of(CREATED, SENT));

        verify(projectUserInviteRepository, never()).findByNameLikeAndStatusIn(searchString, EnumSet.of(CREATED, SENT));
        verify(projectUserInviteRepository, never()).findByOrganisationNameLikeAndStatusIn(searchString, EnumSet.of(CREATED, SENT));
        verify(projectUserInviteRepository, never()).findByEmailLikeAndStatusIn(searchString, EnumSet.of(CREATED, SENT));
    }

    @Test
    public void findExternalInvitesWhenSearchStringIsEmpty(){

        String searchString = "";
        SearchCategory searchCategory = SearchCategory.NAME;

        ServiceResult<List<ExternalInviteResource>> result = service.findExternalInvites(searchString, searchCategory);

        assertTrue(result.isFailure());
        assertEquals(USER_SEARCH_INVALID_INPUT_LENGTH.getErrorKey(), result.getFailure().getErrors().get(0).getErrorKey());

        verify(applicationInviteRepository, never()).findByNameLikeAndStatusIn(searchString, EnumSet.of(CREATED, SENT));
        verify(applicationInviteRepository, never()).findByInviteOrganisationOrganisationNameLikeAndStatusIn(searchString, EnumSet.of(CREATED, SENT));
        verify(applicationInviteRepository, never()).findByEmailLikeAndStatusIn(searchString, EnumSet.of(CREATED, SENT));

        verify(projectUserInviteRepository, never()).findByNameLikeAndStatusIn(searchString, EnumSet.of(CREATED, SENT));
        verify(projectUserInviteRepository, never()).findByOrganisationNameLikeAndStatusIn(searchString, EnumSet.of(CREATED, SENT));
        verify(projectUserInviteRepository, never()).findByEmailLikeAndStatusIn(searchString, EnumSet.of(CREATED, SENT));
    }

    @Test
    public void findExternalInvitesWhenSearchStringLengthLessThan5(){

        String searchString = "a";
        String searchStringExpr = "%a%";
        SearchCategory searchCategory = SearchCategory.NAME;

        ServiceResult<List<ExternalInviteResource>> result = service.findExternalInvites(searchString, searchCategory);

        assertTrue(result.isFailure());
        assertEquals(USER_SEARCH_INVALID_INPUT_LENGTH.getErrorKey(), result.getFailure().getErrors().get(0).getErrorKey());

        verify(applicationInviteRepository, never()).findByNameLikeAndStatusIn(searchStringExpr, EnumSet.of(CREATED, SENT));
        verify(applicationInviteRepository, never()).findByInviteOrganisationOrganisationNameLikeAndStatusIn(searchStringExpr, EnumSet.of(CREATED, SENT));
        verify(applicationInviteRepository, never()).findByEmailLikeAndStatusIn(searchStringExpr, EnumSet.of(CREATED, SENT));

        verify(projectUserInviteRepository, never()).findByNameLikeAndStatusIn(searchStringExpr, EnumSet.of(CREATED, SENT));
        verify(projectUserInviteRepository, never()).findByOrganisationNameLikeAndStatusIn(searchStringExpr, EnumSet.of(CREATED, SENT));
        verify(projectUserInviteRepository, never()).findByEmailLikeAndStatusIn(searchStringExpr, EnumSet.of(CREATED, SENT));
    }

    @Test
    public void findExternalInvitesWhenSearchStringIsAllSpaces(){

        String searchString = "          ";
        SearchCategory searchCategory = SearchCategory.NAME;

        ServiceResult<List<ExternalInviteResource>> result = service.findExternalInvites(searchString, searchCategory);

        assertTrue(result.isFailure());
        assertEquals(USER_SEARCH_INVALID_INPUT_LENGTH.getErrorKey(), result.getFailure().getErrors().get(0).getErrorKey());

        verify(applicationInviteRepository, never()).findByNameLikeAndStatusIn(searchString, EnumSet.of(CREATED, SENT));
        verify(applicationInviteRepository, never()).findByInviteOrganisationOrganisationNameLikeAndStatusIn(searchString, EnumSet.of(CREATED, SENT));
        verify(applicationInviteRepository, never()).findByEmailLikeAndStatusIn(searchString, EnumSet.of(CREATED, SENT));

        verify(projectUserInviteRepository, never()).findByNameLikeAndStatusIn(searchString, EnumSet.of(CREATED, SENT));
        verify(projectUserInviteRepository, never()).findByOrganisationNameLikeAndStatusIn(searchString, EnumSet.of(CREATED, SENT));
        verify(projectUserInviteRepository, never()).findByEmailLikeAndStatusIn(searchString, EnumSet.of(CREATED, SENT));
    }

    @Test
    public void findExternalInvitesWhenSearchCategoryIsName() {

        String searchString = "smith";
        String searchStringExpr = "%smith%";
        SearchCategory searchCategory = SearchCategory.NAME;

        List<ApplicationInvite> applicationInvites = setUpMockingCreateApplicationInvites();
        List<ProjectUserInvite> projectInvites = setUpMockingCreateProjectInvites();

        when(applicationInviteRepository.findByNameLikeAndStatusIn(searchStringExpr, EnumSet.of(CREATED, SENT))).thenReturn(applicationInvites);
        when(projectUserInviteRepository.findByNameLikeAndStatusIn(searchStringExpr, EnumSet.of(CREATED, SENT))).thenReturn(projectInvites);

        ServiceResult<List<ExternalInviteResource>> result = service.findExternalInvites(searchString, searchCategory);

        assertFindExternalInvites(result);

        verify(applicationInviteRepository).findByNameLikeAndStatusIn(searchStringExpr, EnumSet.of(CREATED, SENT));
        verify(applicationInviteRepository, never()).findByInviteOrganisationOrganisationNameLikeAndStatusIn(searchStringExpr, EnumSet.of(CREATED, SENT));
        verify(applicationInviteRepository, never()).findByEmailLikeAndStatusIn(searchStringExpr, EnumSet.of(CREATED, SENT));

        verify(projectUserInviteRepository).findByNameLikeAndStatusIn(searchStringExpr, EnumSet.of(CREATED, SENT));
        verify(projectUserInviteRepository, never()).findByOrganisationNameLikeAndStatusIn(searchStringExpr, EnumSet.of(CREATED, SENT));
        verify(projectUserInviteRepository, never()).findByEmailLikeAndStatusIn(searchStringExpr, EnumSet.of(CREATED, SENT));

    }

    @Test
    public void findExternalInvitesWhenSearchCategoryIsOrganisationName() {

        String searchString = "smith";
        String searchStringExpr = "%smith%";
        SearchCategory searchCategory = SearchCategory.ORGANISATION_NAME;

        List<ApplicationInvite> applicationInvites = setUpMockingCreateApplicationInvites();
        List<ProjectUserInvite> projectInvites = setUpMockingCreateProjectInvites();

        when(applicationInviteRepository.findByInviteOrganisationOrganisationNameLikeAndStatusIn(searchStringExpr, EnumSet.of(CREATED, SENT))).thenReturn(applicationInvites);
        when(projectUserInviteRepository.findByOrganisationNameLikeAndStatusIn(searchStringExpr, EnumSet.of(CREATED, SENT))).thenReturn(projectInvites);

        ServiceResult<List<ExternalInviteResource>> result = service.findExternalInvites(searchString, searchCategory);

        assertFindExternalInvites(result);

        verify(applicationInviteRepository, never()).findByNameLikeAndStatusIn(searchStringExpr, EnumSet.of(CREATED, SENT));
        verify(applicationInviteRepository).findByInviteOrganisationOrganisationNameLikeAndStatusIn(searchStringExpr, EnumSet.of(CREATED, SENT));
        verify(applicationInviteRepository, never()).findByEmailLikeAndStatusIn(searchStringExpr, EnumSet.of(CREATED, SENT));

        verify(projectUserInviteRepository, never()).findByNameLikeAndStatusIn(searchStringExpr, EnumSet.of(CREATED, SENT));
        verify(projectUserInviteRepository).findByOrganisationNameLikeAndStatusIn(searchStringExpr, EnumSet.of(CREATED, SENT));
        verify(projectUserInviteRepository, never()).findByEmailLikeAndStatusIn(searchStringExpr, EnumSet.of(CREATED, SENT));

    }

    @Test
    public void findExternalInvitesWhenSearchCategoryIsEmail() {

        String searchString = "smith";
        String searchStringExpr = "%smith%";
        SearchCategory searchCategory = SearchCategory.EMAIL;

        List<ApplicationInvite> applicationInvites = setUpMockingCreateApplicationInvites();
        List<ProjectUserInvite> projectInvites = setUpMockingCreateProjectInvites();

        when(applicationInviteRepository.findByEmailLikeAndStatusIn(searchStringExpr, EnumSet.of(CREATED, SENT))).thenReturn(applicationInvites);
        when(projectUserInviteRepository.findByEmailLikeAndStatusIn(searchStringExpr, EnumSet.of(CREATED, SENT))).thenReturn(projectInvites);

        ServiceResult<List<ExternalInviteResource>> result = service.findExternalInvites(searchString, searchCategory);

        assertFindExternalInvites(result);

        verify(applicationInviteRepository, never()).findByNameLikeAndStatusIn(searchStringExpr, EnumSet.of(CREATED, SENT));
        verify(applicationInviteRepository, never()).findByInviteOrganisationOrganisationNameLikeAndStatusIn(searchStringExpr, EnumSet.of(CREATED, SENT));
        verify(applicationInviteRepository).findByEmailLikeAndStatusIn(searchStringExpr, EnumSet.of(CREATED, SENT));

        verify(projectUserInviteRepository, never()).findByNameLikeAndStatusIn(searchStringExpr, EnumSet.of(CREATED, SENT));
        verify(projectUserInviteRepository, never()).findByOrganisationNameLikeAndStatusIn(searchStringExpr, EnumSet.of(CREATED, SENT));
        verify(projectUserInviteRepository).findByEmailLikeAndStatusIn(searchStringExpr, EnumSet.of(CREATED, SENT));

    }

    @Test
    public void resendInternalUserInvite() {

        Role targetRole = Role.PROJECT_FINANCE;

        RoleInvite existingInvite = newRoleInvite().
                withName("name").
                withEmail("e@mail.com").
                withTarget(targetRole).
                withHash("hashhashhash").
                build();

        when(roleInviteRepository.findById(123L)).thenReturn(Optional.of(existingInvite));

        Role roleResource = Role.PROJECT_FINANCE;

        NotificationTarget notificationTarget = new UserNotificationTarget(existingInvite.getName(), existingInvite.getEmail());

        Map<String, Object> emailTemplateArgs = asMap("role", roleResource.getDisplayName(),
                "inviteUrl", "base/management/registration/hashhashhash/register");

        Notification expectedNotification = new Notification(systemNotificationSource, notificationTarget, INVITE_INTERNAL_USER, emailTemplateArgs);

        when(notificationService.sendNotificationWithFlush(expectedNotification, EMAIL)).thenReturn(serviceSuccess());

        User loggedInUser = newUser().build();
        when(loggedInUserSupplier.get()).thenReturn(loggedInUser);

        ServiceResult<Void> result = service.resendInvite(123L);
        assertTrue(result.isSuccess());

        // assert the email was sent with the correct hash, and that the invite was saved (not strictly necessary
        // in this case to explicitly save, but is reused code with creating invites also)
        verify(notificationService).sendNotificationWithFlush(expectedNotification, EMAIL);
        verify(roleInviteRepository).save(existingInvite);

        // and verify that the sent on field has been updated so that this link will not expire soon
        assertThat(existingInvite.getSentBy(), equalTo(loggedInUser));
        assertThat(existingInvite.getSentOn(), lessThanOrEqualTo(now()));
        assertThat(existingInvite.getSentOn().plus(50, MILLIS), greaterThan(now()));
    }

    @Test
    public void resendInternalUserInviteButInviteNotFound() {

        when(roleInviteRepository.findById(123L)).thenReturn(Optional.empty());

        ServiceResult<Void> result = service.resendInvite(123L);

        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(notFoundError(RoleInvite.class, 123L)));

        // assert the email was sent with the correct hash, and that the invite was saved (not strictly necessary
        // in this case to explicitly save, but is reused code with creating invites also)
        verify(roleInviteRepository).findById(123L);
        verifyNoMoreInteractions(roleInviteRepository, notificationService);
    }

    @Test
    public void saveKtaUserInviteWhenEmailDomainIsIncorrect() throws Exception {

        Role role = KNOWLEDGE_TRANSFER_ADVISER;
        invitedUser.setEmail("Astle.Pimenta@gmail.com");

        ServiceResult<Void> result = service.saveUserInvite(invitedUser, role, "");
        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(KTA_USER_ROLE_INVITE_INVALID_EMAIL));
        verify(roleInviteRepository, never()).save(Mockito.any(RoleInvite.class));
    }

    @Test
    public void saveKtaUserInviteWhenEmailAlreadyTaken() throws Exception {

        invitedUser.setEmail("Astle.Pimenta@ktn-uk.org");

        when(loggedInUserSupplier.get()).thenReturn(newUser().build());
        when(userRepository.findByEmail(invitedUser.getEmail())).thenReturn(Optional.of(newUser().build()));

        ServiceResult<Void> result = service.saveUserInvite(invitedUser, KNOWLEDGE_TRANSFER_ADVISER, "");

        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(USER_ROLE_INVITE_EMAIL_TAKEN));
        verify(roleInviteRepository, never()).save(Mockito.any(RoleInvite.class));
    }

    @Test
    public void saveKtaUserInviteWhenUserAlreadyInvited() throws Exception {

        RoleInvite roleInvite = new RoleInvite();
        invitedUser.setEmail("Astle.Pimenta@ktn-uk.org");

        when(userRepository.findByEmail(invitedUser.getEmail())).thenReturn(Optional.empty());
        when(roleInviteRepository.findByEmail(invitedUser.getEmail())).thenReturn(singletonList(roleInvite));

        ServiceResult<Void> result = service.saveUserInvite(invitedUser, KNOWLEDGE_TRANSFER_ADVISER, "");
        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(USER_ROLE_INVITE_TARGET_USER_ALREADY_INVITED));
        verify(roleInviteRepository, never()).save(Mockito.any(RoleInvite.class));
    }

    @Test
    public void saveKtaUserInviteSucceeds() throws Exception {

        Role role = KNOWLEDGE_TRANSFER_ADVISER;

        invitedUser.setEmail("Astle.Pimenta@ktn-uk.org");

        RoleInvite expectedRoleInvite = newRoleInvite().
                withEmail("Astle.Pimenta@ktn-uk.org").
                withName("Astle Pimenta").
                withRole(role).
                withStatus(CREATED).
                withHash("1234").
                build();

        when(roleInviteRepository.save(any(RoleInvite.class))).thenReturn(expectedRoleInvite);

        when(loggedInUserSupplier.get()).thenReturn(newUser().build());

        when(roleInviteRepository.save(any(RoleInvite.class))).thenReturn(expectedRoleInvite);

        when(userRepository.findByEmail(invitedUser.getEmail())).thenReturn(Optional.empty());

        NotificationTarget notificationTarget = new UserNotificationTarget(expectedRoleInvite.getName(), expectedRoleInvite.getEmail());

        String forAssessor = role.isAssessor() ? "an" : "a";
        Map<String, Object> emailTemplateArgs = asMap("isAssessor",forAssessor, "role", role.getDisplayName().toLowerCase(),
                "inviteUrl", "base/registration/1234/register");

        Notification expectedNotification = new Notification(systemNotificationSource, notificationTarget, INVITE_EXTERNAL_USER, emailTemplateArgs);

        when(notificationService.sendNotificationWithFlush(expectedNotification, EMAIL)).thenReturn(serviceSuccess());

        ServiceResult<Void> result = service.saveUserInvite(invitedUser, role, "");

        assertTrue(result.isSuccess());

        verify(roleInviteRepository, times(2)).save(roleInviteArgumentCaptor.capture());

        List<RoleInvite> captured = roleInviteArgumentCaptor.getAllValues();
        assertEquals("Astle.Pimenta@ktn-uk.org", captured.get(0).getEmail());
        assertEquals("Astle Pimenta", captured.get(0).getName());
        assertEquals(role, captured.get(0).getTarget());
        assertEquals(CREATED, captured.get(0).getStatus());
        assertNotNull(captured.get(1).getSentOn());
    }

    private List<ApplicationInvite> setUpMockingCreateApplicationInvites() {

        Application app = newApplication().build();
        List<Organisation> appOrganisations = newOrganisation().withName("Tesla", "Columbia Data Products").build(2);
        List<InviteOrganisation> inviteOrganisations = newInviteOrganisation()
                .withOrganisation(appOrganisations.get(0), appOrganisations.get(1), null)
                .withOrganisationName(null, null, "Rolls Royce Plc").build(3);

        List<ApplicationInvite> applicationInvites = newApplicationInvite().withApplication(app)
                .withEmail("x@email.com", "b@email.com", "zz@email.com")
                .withInviteOrganisation(inviteOrganisations.get(0), inviteOrganisations.get(1), inviteOrganisations.get(2)).build(3);

        return applicationInvites;
    }

    private List<ProjectUserInvite> setUpMockingCreateProjectInvites() {

        Application app = newApplication().build();
        Project prj = newProject().withApplication(app).build();

        List<Organisation> organisations = newOrganisation().withName("Cardiff Electric", "Mutiny").build(2);
        List<ProjectUserInvite> projectInvites = newProjectUserInvite().withProject(prj)
                .withOrganisation(organisations.get(0), organisations.get(1))
                .withEmail("z@email.com", "u@email.com").build(2);

        return projectInvites;
    }

    private void assertFindExternalInvites(ServiceResult<List<ExternalInviteResource>> result) {

        assertTrue(result.isSuccess());
        assertEquals(5, result.getSuccess().size());
        assertEquals("b@email.com", result.getSuccess().get(0).getEmail());
        assertEquals("Columbia Data Products", result.getSuccess().get(0).getOrganisationName());

        assertEquals("u@email.com", result.getSuccess().get(1).getEmail());
        assertEquals("Mutiny", result.getSuccess().get(1).getOrganisationName());

        assertEquals("x@email.com", result.getSuccess().get(2).getEmail());
        assertEquals("Tesla", result.getSuccess().get(2).getOrganisationName());

        assertEquals("z@email.com", result.getSuccess().get(3).getEmail());
        assertEquals("Cardiff Electric", result.getSuccess().get(3).getOrganisationName());

        // The one without pre-exiting organisation has name set correctly
        assertEquals("zz@email.com", result.getSuccess().get(4).getEmail());
        assertEquals("Rolls Royce Plc", result.getSuccess().get(4).getOrganisationName());
    }

    @Test
    public void saveAssessorUserInviteSucceeds() throws Exception {
        InnovationArea innovationArea = newInnovationArea().withName("innovation area").build();
        invitedUser = newUserResource()
                .withFirstName("Assessor")
                .withLastName("Test")
                .withEmail("assessor.test.org")
                .build();

        Role role = ASSESSOR;
        RoleInvite expectedRoleInvite = newRoleInvite().
                withEmail("assessor.test.org").
                withName("Assessor Test").
                withRole(role).
                withInnovationArea(innovationArea).
                withStatus(CREATED).
                withHash("1234").
                build();

        when(roleInviteRepository.save(any(RoleInvite.class))).thenReturn(expectedRoleInvite);

        when(loggedInUserSupplier.get()).thenReturn(newUser().build());

        when(roleInviteRepository.save(any(RoleInvite.class))).thenReturn(expectedRoleInvite);

        when(userRepository.findByEmail(invitedUser.getEmail())).thenReturn(Optional.empty());
        when(innovationAreaRepository.findById(innovationArea.getId())).thenReturn(Optional.of(innovationArea));

        NotificationTarget notificationTarget = new UserNotificationTarget(expectedRoleInvite.getName(), expectedRoleInvite.getEmail());
        String forAssessor = role.isAssessor() ? "an" : "a";
        Map<String, Object> emailTemplateArgs = asMap("isAssessor", forAssessor, "role", role.getDisplayName().toLowerCase(),
                "inviteUrl", "base/registration/1234/register");

        Notification expectedNotification = new Notification(systemNotificationSource, notificationTarget, INVITE_EXTERNAL_USER, emailTemplateArgs);

        when(notificationService.sendNotificationWithFlush(expectedNotification, EMAIL)).thenReturn(serviceSuccess());

        ServiceResult<Void> result = service.saveAssessorInvite(invitedUser, role, expectedRoleInvite.getInnovationArea().getId());

        assertTrue(result.isSuccess());

        verify(roleInviteRepository, times(2)).save(roleInviteArgumentCaptor.capture());

        List<RoleInvite> captured = roleInviteArgumentCaptor.getAllValues();
        assertEquals("assessor.test.org", captured.get(0).getEmail());
        assertEquals("Assessor Test", captured.get(0).getName());
        assertEquals(role, captured.get(0).getTarget());
        assertEquals(CREATED, captured.get(0).getStatus());
        assertNotNull(captured.get(1).getSentOn());
    }

    @Test
    public void findExternalInvitesByUser() {
        String email = "bob@email.com";
        UserResource externalUser = newUserResource().withEmail(email).build();
        List<RoleInvite> roleInvite = singletonList(newRoleInvite().withUser().withEmail(externalUser.getEmail()).build());

        when(roleInviteRepository.getByUserId(externalUser.getId())).thenReturn(roleInvite);
        ServiceResult<List<RoleInviteResource>> result = service.findExternalInvitesByUser(externalUser);
        assertTrue(result.isSuccess());
    }
}