package org.innovateuk.ifs.user.service;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.authentication.service.IdentityProviderService;
import org.innovateuk.ifs.authentication.validator.PasswordPolicyValidator;
import org.innovateuk.ifs.commons.error.CommonErrors;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.resource.SiteTermsAndConditionsResource;
import org.innovateuk.ifs.competition.transactional.TermsAndConditionsService;
import org.innovateuk.ifs.invite.constant.InviteStatus;
import org.innovateuk.ifs.invite.domain.*;
import org.innovateuk.ifs.invite.repository.ApplicationInviteRepository;
import org.innovateuk.ifs.invite.repository.ProjectUserInviteRepository;
import org.innovateuk.ifs.invite.repository.UserInviteRepository;
import org.innovateuk.ifs.notifications.resource.Notification;
import org.innovateuk.ifs.notifications.resource.SystemNotificationSource;
import org.innovateuk.ifs.notifications.resource.UserNotificationTarget;
import org.innovateuk.ifs.notifications.service.NotificationService;
import org.innovateuk.ifs.organisation.builder.OrganisationBuilder;
import org.innovateuk.ifs.project.core.domain.Project;
import org.innovateuk.ifs.project.core.domain.ProjectUser;
import org.innovateuk.ifs.project.core.repository.ProjectRepository;
import org.innovateuk.ifs.project.core.repository.ProjectUserRepository;
import org.innovateuk.ifs.project.invite.repository.ProjectPartnerInviteRepository;
import org.innovateuk.ifs.token.domain.Token;
import org.innovateuk.ifs.token.repository.TokenRepository;
import org.innovateuk.ifs.token.resource.TokenType;
import org.innovateuk.ifs.token.transactional.TokenService;
import org.innovateuk.ifs.user.command.GrantRoleCommand;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.user.domain.RoleProfileStatus;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.mapper.RoleProfileStatusMapper;
import org.innovateuk.ifs.user.mapper.UserMapper;
import org.innovateuk.ifs.user.repository.ProcessRoleRepository;
import org.innovateuk.ifs.user.repository.RoleProfileStatusRepository;
import org.innovateuk.ifs.user.repository.UserRepository;
import org.innovateuk.ifs.user.resource.*;
import org.innovateuk.ifs.user.transactional.RegistrationService;
import org.innovateuk.ifs.user.transactional.UserService;
import org.innovateuk.ifs.user.transactional.UserServiceImpl;
import org.innovateuk.ifs.userorganisation.domain.UserOrganisation;
import org.innovateuk.ifs.userorganisation.mapper.UserOrganisationMapper;
import org.innovateuk.ifs.userorganisation.repository.UserOrganisationRepository;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.*;
import java.util.function.Supplier;

import static java.util.Arrays.asList;
import static java.util.Collections.*;
import static java.util.Optional.of;
import static java.util.stream.Collectors.toSet;
import static org.innovateuk.ifs.LambdaMatcher.createLambdaMatcher;
import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.*;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.competition.builder.SiteTermsAndConditionsResourceBuilder.newSiteTermsAndConditionsResource;
import static org.innovateuk.ifs.invite.builder.ApplicationInviteBuilder.newApplicationInvite;
import static org.innovateuk.ifs.invite.builder.ProjectUserInviteBuilder.newProjectUserInvite;
import static org.innovateuk.ifs.invite.constant.InviteStatus.OPENED;
import static org.innovateuk.ifs.notifications.resource.NotificationMedium.EMAIL;
import static org.innovateuk.ifs.project.core.builder.ProjectBuilder.newProject;
import static org.innovateuk.ifs.project.core.builder.ProjectUserBuilder.newProjectUser;
import static org.innovateuk.ifs.user.builder.ProcessRoleBuilder.newProcessRole;
import static org.innovateuk.ifs.user.builder.RoleProfileStatusBuilder.newRoleProfileStatus;
import static org.innovateuk.ifs.user.builder.RoleProfileStatusResourceBuilder.newRoleProfileStatusResource;
import static org.innovateuk.ifs.user.builder.UserBuilder.newUser;
import static org.innovateuk.ifs.user.builder.UserOrganisationResourceBuilder.newUserOrganisationResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.user.resource.Role.APPLICANT;
import static org.innovateuk.ifs.user.resource.Role.externalApplicantRoles;
import static org.innovateuk.ifs.userorganisation.builder.UserOrganisationBuilder.newUserOrganisation;
import static org.innovateuk.ifs.util.MapFunctions.asMap;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Tests of the UserService class
 */
public class UserServiceImplTest extends BaseServiceUnitTest<UserService> {

    private static final String WEB_BASE_URL = "baseUrl";

    @Captor
    private ArgumentCaptor<Notification> notificationArgumentCaptor;

    @Mock
    private UserOrganisationMapper userOrganisationMapperMock;

    @Mock
    private RoleProfileStatusMapper roleProfileStatusMapper;

    @Mock
    private TermsAndConditionsService termsAndConditionsServiceMock;

    @Mock
    private TokenService tokenServiceMock;

    @Mock
    private UserRepository userRepositoryMock;

    @Mock
    private UserMapper userMapperMock;

    @Mock
    private PasswordPolicyValidator passwordPolicyValidatorMock;

    @Mock
    private IdentityProviderService idpServiceMock;

    @Mock
    private TokenRepository tokenRepositoryMock;

    @Mock
    private NotificationService notificationServiceMock;

    @Mock
    private RegistrationService registrationServiceMock;

    @Mock
    private UserOrganisationRepository userOrganisationRepositoryMock;

    @Mock
    private SystemNotificationSource systemNotificationSource;

    @Mock
    private UserInviteRepository userInviteRepositoryMock;

    @Mock
    private ProcessRoleRepository processRoleRepository;

    @Mock
    private ApplicationInviteRepository applicationInviteRepository;

    @Mock
    private ProjectUserRepository projectUserRepository;

    @Mock
    private ProjectUserInviteRepository projectUserInviteRepository;

    @Mock
    private ProjectPartnerInviteRepository projectPartnerInviteRepository;

    @Mock(name = "randomHashSupplier")
    private Supplier<String> randomHashSupplierMock;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private RoleProfileStatusRepository roleProfileStatusRepositoryMock;

    @Override
    protected UserService supplyServiceUnderTest() {
        UserServiceImpl spendProfileService = new UserServiceImpl();
        ReflectionTestUtils.setField(spendProfileService, "webBaseUrl", WEB_BASE_URL);
        return spendProfileService;
    }

    @Test
    public void testChangePassword() {

        User user = newUser().build();
        UserResource userResource = newUserResource().withUID("myuid").build();

        Token token = new Token(TokenType.RESET_PASSWORD, null, 123L, null, null, null);
        when(tokenServiceMock.getPasswordResetToken("myhash")).thenReturn(serviceSuccess(token));
        when(userRepositoryMock.findById(123L)).thenReturn(Optional.of(user));
        when(userMapperMock.mapToResource(user)).thenReturn(userResource);
        when(passwordPolicyValidatorMock.validatePassword("mypassword", userResource)).thenReturn(serviceSuccess());
        when(idpServiceMock.updateUserPassword("myuid", "mypassword")).thenReturn(serviceSuccess("mypassword"));

        service.changePassword("myhash", "mypassword").getSuccess();

        verify(tokenRepositoryMock).delete(token);
    }

    @Test
    public void testChangePasswordButPasswordValidationFails() {

        User user = newUser().build();
        UserResource userResource = newUserResource().withUID("myuid").build();

        Token token = new Token(TokenType.RESET_PASSWORD, null, 123L, null, null, null);
        when(tokenServiceMock.getPasswordResetToken("myhash")).thenReturn(serviceSuccess(token));
        when(userRepositoryMock.findById(123L)).thenReturn(Optional.of(user));
        when(userMapperMock.mapToResource(user)).thenReturn(userResource);
        when(passwordPolicyValidatorMock.validatePassword("mypassword", userResource)).thenReturn(ServiceResult.serviceFailure(CommonErrors.badRequestError("bad password")));

        ServiceResult<Void> result = service.changePassword("myhash", "mypassword");
        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(CommonErrors.badRequestError("bad password")));
        verify(tokenRepositoryMock, never()).delete(token);
    }

    @Test
    public void testChangePasswordButPasswordValidationFailsOnIDP() {
        final User user = newUser().build();
        final UserResource userResource = newUserResource().withUID("myuid").build();
        final String password = "mypassword";

        Token token = new Token(TokenType.RESET_PASSWORD, null, 123L, null, null, null);
        when(tokenServiceMock.getPasswordResetToken("myhash")).thenReturn(serviceSuccess(token));
        when(userRepositoryMock.findById(123L)).thenReturn(Optional.of(user));
        when(userMapperMock.mapToResource(user)).thenReturn(userResource);

        when(passwordPolicyValidatorMock.validatePassword(password, userResource)).thenReturn(serviceSuccess());
        when(idpServiceMock.updateUserPassword(anyString(), anyString())).thenReturn(ServiceResult.serviceFailure(CommonErrors.badRequestError("bad password")));

        ServiceResult<Void> result = service.changePassword("myhash", password);
        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(CommonErrors.badRequestError("bad password")));
        verify(tokenRepositoryMock, never()).delete(token);
    }

    @Test
    public void testFindInactiveByEmail() {
        final User user = newUser().build();
        final UserResource userResource = newUserResource()
                .withEmail("a@b.c")
                .withLastName("A")
                .withLastName("Bee")
                .build();
        final String email = "sample@me.com";

        when(userRepositoryMock.findByEmailAndStatus(email, UserStatus.INACTIVE)).thenReturn(of(user));
        when(userMapperMock.mapToResource(user)).thenReturn(userResource);

        final ServiceResult<UserResource> result = service.findInactiveByEmail(email);
        assertTrue(result.isSuccess());
        assertSame(userResource, result.getSuccess());
        verify(userRepositoryMock, only()).findByEmailAndStatus(email, UserStatus.INACTIVE);
    }

    @Test
    public void testSendPasswordResetNotification() {

        String hash = "1234";

        UserResource user = newUserResource()
                .withStatus(UserStatus.ACTIVE)
                .withEmail("a@b.c")
                .withFirstName("A")
                .withLastName("Bee")
                .build();

        UserNotificationTarget to = new UserNotificationTarget(user.getName(), user.getEmail());

        Map<String, Object> notificationArgs = asMap("passwordResetLink", "baseUrl/login/reset-password/hash/" + hash);

        Notification notification = new Notification(systemNotificationSource, to, UserServiceImpl.Notifications.RESET_PASSWORD, notificationArgs);

        when(notificationServiceMock.sendNotificationWithFlush(notification, EMAIL)).thenReturn(serviceSuccess());
        when(randomHashSupplierMock.get()).thenReturn(hash);
        service.sendPasswordResetNotification(user).getSuccess();

        verify(notificationServiceMock).sendNotificationWithFlush(notification, EMAIL);
        verify(randomHashSupplierMock).get();
    }

    @Test
    public void testSendPasswordResetNotificationInactiveApplicantNoVerifyToken() {
        final UserResource user = newUserResource()
                .withStatus(UserStatus.INACTIVE)
                .withRolesGlobal(
                        singletonList(Role.APPLICANT))
                .withEmail("a@b.c")
                .withFirstName("A")
                .withLastName("Bee")
                .build();

        when(tokenRepositoryMock.findByTypeAndClassNameAndClassPk(TokenType.VERIFY_EMAIL_ADDRESS, User.class.getCanonicalName(), user.getId())).thenReturn(Optional.empty());
        ServiceResult<Void> result = service.sendPasswordResetNotification(user);

        verify(tokenRepositoryMock).findByTypeAndClassNameAndClassPk(any(), any(), any());

        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(notFoundError(UserResource.class, user.getEmail(),UserStatus.ACTIVE)));
    }

    @Test
    public void testSendPasswordResetNotificationInactiveApplicantHasVerifyToken() {
        final UserResource user = newUserResource()
                .withStatus(UserStatus.INACTIVE)
                .withRolesGlobal(
                        asList(Role.APPLICANT, Role.ASSESSOR))
                .withEmail("a@b.c")
                .withFirstName("A")
                .withLastName("Bee")
                .build();

        when(tokenRepositoryMock.findByTypeAndClassNameAndClassPk(TokenType.VERIFY_EMAIL_ADDRESS, User.class.getCanonicalName(), user.getId())).thenReturn(Optional.of(new Token()));
        when(registrationServiceMock.resendUserVerificationEmail(user)).thenReturn(serviceSuccess());

        service.sendPasswordResetNotification(user).getSuccess();

        verify(tokenRepositoryMock).findByTypeAndClassNameAndClassPk(any(), any(), any());
    }

    @Test
    public void testSendPasswordResetNotificationInactiveAssessor() {
        final UserResource user = newUserResource()
                .withStatus(UserStatus.INACTIVE)
                .withRolesGlobal(
                        singletonList(Role.ASSESSOR))
                .withEmail("a@b.c")
                .withFirstName("A")
                .withLastName("Bee")
                .build();

        ServiceResult<Void> result = service.sendPasswordResetNotification(user);

        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(notFoundError(UserResource.class, user.getEmail(),UserStatus.ACTIVE)));
    }

    @Test
    public void testSendPasswordResetNotificationInactiveProjectFinance() {
        final UserResource user = newUserResource()
                .withStatus(UserStatus.INACTIVE)
                .withRolesGlobal(
                        singletonList(Role.PROJECT_FINANCE))
                .withEmail("a@b.c")
                .withFirstName("A")
                .withLastName("Bee")
                .build();

        ServiceResult<Void> result = service.sendPasswordResetNotification(user);

        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(notFoundError(UserResource.class, user.getEmail(),UserStatus.ACTIVE)));
    }

    @Test
    public void testSendPasswordResetNotificationInactiveCompAdmin() {
        final UserResource user = newUserResource()
                .withStatus(UserStatus.INACTIVE)
                .withRolesGlobal(
                        singletonList(Role.COMP_ADMIN))
                .withEmail("a@b.c")
                .withFirstName("A")
                .withLastName("Bee")
                .build();

        ServiceResult<Void> result = service.sendPasswordResetNotification(user);

        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(notFoundError(UserResource.class, user.getEmail(),UserStatus.ACTIVE)));
    }

    @Test
    public void testSendPasswordResetNotificationInactiveCompExec() {
        final UserResource user = newUserResource()
                .withStatus(UserStatus.INACTIVE)
                .withRolesGlobal(
                        singletonList(Role.COMP_EXEC))
                .withEmail("a@b.c")
                .withFirstName("A")
                .withLastName("Bee")
                .build();

        ServiceResult<Void> result = service.sendPasswordResetNotification(user);

        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(notFoundError(UserResource.class, user.getEmail(),UserStatus.ACTIVE)));
    }

    @Test
    public void testSendPasswordResetNotificationInactiveCompTechnologist() {
        final UserResource user = newUserResource()
                .withStatus(UserStatus.INACTIVE)
                .withRolesGlobal(
                        singletonList(Role.INNOVATION_LEAD))
                .withEmail("a@b.c")
                .withFirstName("A")
                .withLastName("Bee")
                .build();

        ServiceResult<Void> result = service.sendPasswordResetNotification(user);

        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(notFoundError(UserResource.class, user.getEmail(),UserStatus.ACTIVE)));
    }

    @Test
    public void testSendPasswordResetNotificationInactiveLeadApplicantNoVerifyToken() {
        final UserResource user = newUserResource()
                .withStatus(UserStatus.INACTIVE).withRolesGlobal(
                        singletonList(Role.LEADAPPLICANT))
                .withEmail("a@b.c")
                .withFirstName("A")
                .withLastName("Bee")
                .build();

        when(tokenRepositoryMock.findByTypeAndClassNameAndClassPk(TokenType.VERIFY_EMAIL_ADDRESS, User.class.getCanonicalName(), user.getId())).thenReturn(Optional.empty());
        ServiceResult<Void> result = service.sendPasswordResetNotification(user);

        verify(tokenRepositoryMock).findByTypeAndClassNameAndClassPk(any(), any(), any());

        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(notFoundError(UserResource.class, user.getEmail(),UserStatus.ACTIVE)));
    }

    @Test
    public void testSendPasswordResetNotificationInactivePartnerNoVerifyToken() {
        final UserResource user = newUserResource()
                .withStatus(UserStatus.INACTIVE).withRolesGlobal(
                        singletonList(Role.PARTNER))
                .withEmail("a@b.c")
                .withFirstName("A")
                .withLastName("Bee")
                .build();

        when(tokenRepositoryMock.findByTypeAndClassNameAndClassPk(TokenType.VERIFY_EMAIL_ADDRESS, User.class.getCanonicalName(), user.getId())).thenReturn(Optional.empty());
        ServiceResult<Void> result = service.sendPasswordResetNotification(user);

        verify(tokenRepositoryMock).findByTypeAndClassNameAndClassPk(any(), any(), any());

        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(notFoundError(UserResource.class, user.getEmail(),UserStatus.ACTIVE)));
    }

    @Test
    public void testSendPasswordResetNotificationInactiveProjectManagerNoVerifyToken() {
        final UserResource user = newUserResource()
                .withStatus(UserStatus.INACTIVE).withRolesGlobal(
                        singletonList(Role.PROJECT_MANAGER))
                .withEmail("a@b.c")
                .withFirstName("A")
                .withLastName("Bee")
                .build();

        when(tokenRepositoryMock.findByTypeAndClassNameAndClassPk(TokenType.VERIFY_EMAIL_ADDRESS, User.class.getCanonicalName(), user.getId())).thenReturn(Optional.empty());
        ServiceResult<Void> result = service.sendPasswordResetNotification(user);

        verify(tokenRepositoryMock).findByTypeAndClassNameAndClassPk(any(), any(), any());

        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(notFoundError(UserResource.class, user.getEmail(),UserStatus.ACTIVE)));
    }

    @Test
    public void testSendPasswordResetNotificationInactiveCollaboratorNoVerifyToken() {
        final UserResource user = newUserResource()
                .withStatus(UserStatus.INACTIVE)
                .withRolesGlobal(
                        singletonList(Role.COLLABORATOR))
                .withEmail("a@b.c")
                .withFirstName("A")
                .withLastName("Bee")
                .build();

        when(tokenRepositoryMock.findByTypeAndClassNameAndClassPk(TokenType.VERIFY_EMAIL_ADDRESS, User.class.getCanonicalName(), user.getId())).thenReturn(Optional.empty());
        ServiceResult<Void> result = service.sendPasswordResetNotification(user);

        verify(tokenRepositoryMock).findByTypeAndClassNameAndClassPk(any(), any(), any());

        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(notFoundError(UserResource.class, user.getEmail(),UserStatus.ACTIVE)));
    }

    @Test
    public void testSendPasswordResetNotificationInactiveFinanceContactNoVerifyToken() {
        final UserResource user = newUserResource()
                .withStatus(UserStatus.INACTIVE)
                .withRolesGlobal(
                        singletonList(Role.FINANCE_CONTACT))
                .withEmail("a@b.c")
                .withFirstName("A")
                .withLastName("Bee")
                .build();

        when(tokenRepositoryMock.findByTypeAndClassNameAndClassPk(TokenType.VERIFY_EMAIL_ADDRESS, User.class.getCanonicalName(), user.getId())).thenReturn(Optional.empty());
        ServiceResult<Void> result = service.sendPasswordResetNotification(user);

        verify(tokenRepositoryMock).findByTypeAndClassNameAndClassPk(any(), any(), any());

        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(notFoundError(UserResource.class, user.getEmail(),UserStatus.ACTIVE)));
    }

    @Test
    public void testFindActive(){
        Set<Role> internalRoles = singleton(Role.PROJECT_FINANCE);
        Pageable pageable = PageRequest.of(0, 5);
        User createdByUser = newUser().withFirstName("first").withLastName("last").build();
        Set<RoleProfileStatus> roleProfileStatuses = newRoleProfileStatus().build(1).stream().collect(toSet());

        List<User> activeUsers = newUser().withStatus(UserStatus.ACTIVE)
                .withCreatedBy(createdByUser)
                .withRoles(internalRoles)
                .withRoleProfileStatuses(roleProfileStatuses)
                .build(6);
        Page<User> expectedPage = new PageImpl<>(activeUsers, pageable, 6L);

        when(userRepositoryMock.findByEmailContainingAndStatus("", UserStatus.ACTIVE, pageable)).thenReturn(expectedPage);
        when(roleProfileStatusMapper.mapToResource(any(RoleProfileStatus.class))).thenReturn(newRoleProfileStatusResource().build());

        ServiceResult<ManageUserPageResource> result = service.findActive("", pageable);

        assertTrue(result.isSuccess());
        assertEquals(5, result.getSuccess().getSize());
        assertEquals(2, result.getSuccess().getTotalPages());
        assertEquals(6, result.getSuccess().getContent().size());
    }

    @Test
    public void testFindInactive(){
        Set<Role> internalRoles = singleton(Role.COMP_ADMIN);
        Pageable pageable = PageRequest.of(0, 5);
        User createdByUser = newUser().withFirstName("first").withLastName("last").build();
        Set<RoleProfileStatus> roleProfileStatuses = newRoleProfileStatus().build(1).stream().collect(toSet());

        List<User> inactiveUsers = newUser()
                .withStatus(UserStatus.INACTIVE)
                .withRoleProfileStatuses(roleProfileStatuses)
                .withCreatedBy(createdByUser)
                .withRoles(internalRoles).build(4);
        Page<User> expectedPage = new PageImpl<>(inactiveUsers, pageable, 4L);

        when(userRepositoryMock.findByEmailContainingAndStatus("", UserStatus.INACTIVE, pageable)).thenReturn(expectedPage);
        when(roleProfileStatusMapper.mapToResource(any(RoleProfileStatus.class))).thenReturn(newRoleProfileStatusResource().build());

        ServiceResult<ManageUserPageResource> result = service.findInactive("", pageable);

        assertTrue(result.isSuccess());
        assertEquals(5, result.getSuccess().getSize());
        assertEquals(1, result.getSuccess().getTotalPages());
        assertEquals(4, result.getSuccess().getContent().size());
    }

    @Test
    public void findByProcessRolesAndSearchCriteriaWhenSearchStringIsNull(){

        String searchString = null;
        SearchCategory searchCategory = SearchCategory.NAME;

        ServiceResult<List<UserOrganisationResource>> result = service.findByProcessRolesAndSearchCriteria(externalApplicantRoles(), searchString, searchCategory);

        assertTrue(result.isFailure());
        assertEquals(USER_SEARCH_INVALID_INPUT_LENGTH.getErrorKey(), result.getFailure().getErrors().get(0).getErrorKey());

        verify(userOrganisationRepositoryMock, never()).findByUserFirstNameLikeOrUserLastNameLikeAndUserRolesInOrderByUserEmailAsc(anyString(), anyString(), anySet());
        verify(userOrganisationRepositoryMock, never()).findByOrganisationNameLikeAndUserRolesInOrderByUserEmailAsc(anyString(), anySet());
        verify(userOrganisationRepositoryMock, never()).findByUserEmailLikeAndUserRolesInOrderByUserEmailAsc(anyString(), anySet());
        verify(userOrganisationMapperMock, never()).mapToResource(any(UserOrganisation.class));
    }

    @Test
    public void findByProcessRolesAndSearchCriteriaWhenSearchStringIsEmpty(){

        String searchString = "";
        SearchCategory searchCategory = SearchCategory.NAME;

        ServiceResult<List<UserOrganisationResource>> result = service.findByProcessRolesAndSearchCriteria(externalApplicantRoles(), searchString, searchCategory);

        assertTrue(result.isFailure());
        assertEquals(USER_SEARCH_INVALID_INPUT_LENGTH.getErrorKey(), result.getFailure().getErrors().get(0).getErrorKey());

        verify(userOrganisationRepositoryMock, never()).findByUserFirstNameLikeOrUserLastNameLikeAndUserRolesInOrderByUserEmailAsc(anyString(), anyString(), anySet());
        verify(userOrganisationRepositoryMock, never()).findByOrganisationNameLikeAndUserRolesInOrderByUserEmailAsc(anyString(), anySet());
        verify(userOrganisationRepositoryMock, never()).findByUserEmailLikeAndUserRolesInOrderByUserEmailAsc(anyString(), anySet());
        verify(userOrganisationMapperMock, never()).mapToResource(any(UserOrganisation.class));
    }

    @Test
    public void findByProcessRolesAndSearchCriteriaWhenSearchStringLengthLessThan5(){

        String searchString = "a";
        SearchCategory searchCategory = SearchCategory.NAME;

        ServiceResult<List<UserOrganisationResource>> result = service.findByProcessRolesAndSearchCriteria(externalApplicantRoles(), searchString, searchCategory);

        assertTrue(result.isFailure());
        assertEquals(USER_SEARCH_INVALID_INPUT_LENGTH.getErrorKey(), result.getFailure().getErrors().get(0).getErrorKey());

        verify(userOrganisationRepositoryMock, never()).findByUserFirstNameLikeOrUserLastNameLikeAndUserRolesInOrderByUserEmailAsc(anyString(), anyString(), anySet());
        verify(userOrganisationRepositoryMock, never()).findByOrganisationNameLikeAndUserRolesInOrderByUserEmailAsc(anyString(), anySet());
        verify(userOrganisationRepositoryMock, never()).findByUserEmailLikeAndUserRolesInOrderByUserEmailAsc(anyString(), anySet());
        verify(userOrganisationMapperMock, never()).mapToResource(any(UserOrganisation.class));
    }

    @Test
    public void findByProcessRolesAndSearchCriteriaWhenSearchStringIsAllSpaces(){

        String searchString = "          ";
        SearchCategory searchCategory = SearchCategory.NAME;

        ServiceResult<List<UserOrganisationResource>> result = service.findByProcessRolesAndSearchCriteria(externalApplicantRoles(), searchString, searchCategory);

        assertTrue(result.isFailure());
        assertEquals(USER_SEARCH_INVALID_INPUT_LENGTH.getErrorKey(), result.getFailure().getErrors().get(0).getErrorKey());

        verify(userOrganisationRepositoryMock, never()).findByUserFirstNameLikeOrUserLastNameLikeAndUserRolesInOrderByUserEmailAsc(anyString(), anyString(), anySet());
        verify(userOrganisationRepositoryMock, never()).findByOrganisationNameLikeAndUserRolesInOrderByUserEmailAsc(anyString(), anySet());
        verify(userOrganisationRepositoryMock, never()).findByUserEmailLikeAndUserRolesInOrderByUserEmailAsc(anyString(), anySet());
        verify(userOrganisationMapperMock, never()).mapToResource(any(UserOrganisation.class));
    }

    @Test
    public void findByProcessRolesAndSearchCriteriaWhenSearchCategoryIsName(){

        String searchString = "%well%";
        SearchCategory searchCategory = SearchCategory.NAME;

        Set<UserOrganisation> userOrganisations = setUpMockingFindByProcessRolesAndSearchCriteria();

        when(userOrganisationRepositoryMock.findByUserFirstNameLikeOrUserLastNameLikeAndUserRolesInOrderByUserEmailAsc(anyString(), anyString(), anySet())).thenReturn(userOrganisations);

        ServiceResult<List<UserOrganisationResource>> result = service.findByProcessRolesAndSearchCriteria(externalApplicantRoles(), searchString, searchCategory);

        assertTrue(result.isSuccess());
        assertEquals(2, result.getSuccess().size());
        assertEquals("Aaron Powell", result.getSuccess().get(0).getName());
        assertEquals("Guitar Gods Ltd", result.getSuccess().get(0).getOrganisationName());
        assertEquals("Business", result.getSuccess().get(0).getOrganisationType());
        assertEquals("David Wellington", result.getSuccess().get(1).getName());
        assertEquals("Engine Equations Ltd", result.getSuccess().get(1).getOrganisationName());
        assertEquals("Research", result.getSuccess().get(1).getOrganisationType());

        verify(userOrganisationRepositoryMock).findByUserFirstNameLikeOrUserLastNameLikeAndUserRolesInOrderByUserEmailAsc(anyString(), anyString(), anySet());
        verify(userOrganisationRepositoryMock, never()).findByOrganisationNameLikeAndUserRolesInOrderByUserEmailAsc(anyString(), anySet());
        verify(userOrganisationRepositoryMock, never()).findByUserEmailLikeAndUserRolesInOrderByUserEmailAsc(anyString(), anySet());
    }

    private Set<UserOrganisation> setUpMockingFindByProcessRolesAndSearchCriteria() {

        UserOrganisationResource userOrganisationResource1 = newUserOrganisationResource().withName("Aaron Powell")
                .withOrganisationName("Guitar Gods Ltd")
                .withOrganisationType("Business")
                .withEmail("aaron.powell@example.com").withStatus(UserStatus.ACTIVE)
                .withOrganisationId(1L).withOrganisationName("Guitar Gods Ltd").build();
        UserOrganisationResource userOrganisationResource2 = newUserOrganisationResource().withName("David Wellington")
                .withOrganisationName("Engine Equations Ltd")
                .withOrganisationType("Research")
                .withEmail("david.wellington@load.example.com").withStatus(UserStatus.ACTIVE)
                .withOrganisationId(2L).withOrganisationName("Engine Equations Ltd").build();

        Set<UserOrganisation> userOrganisations = new HashSet<>(newUserOrganisation()
                .withUser(newUser().withEmailAddress("aaron.powell@example.com").build(),
                        newUser().withEmailAddress("david.wellington@load.example.com").build())
                .withOrganisation(OrganisationBuilder.newOrganisation().withId(1L).withName("Guitar Gods Ltd").build(),
                        OrganisationBuilder.newOrganisation().withId(2L).withName("Engine Equations Ltd").build())
                .build(2));

        Iterator<UserOrganisation> iterator = userOrganisations.iterator();
        when(userOrganisationMapperMock.mapToResource(iterator.next())).thenReturn(userOrganisationResource1);
        when(userOrganisationMapperMock.mapToResource(iterator.next())).thenReturn(userOrganisationResource2);

        return userOrganisations;
    }

    @Test
    public void findByProcessRolesAndSearchCriteriaWhenSearchCategoryIsOrganisationName(){

        String searchString = "%Ltd%";
        SearchCategory searchCategory = SearchCategory.ORGANISATION_NAME;

        Set<UserOrganisation> userOrganisations = setUpMockingFindByProcessRolesAndSearchCriteria();

        when(userOrganisationRepositoryMock.findByOrganisationNameLikeAndUserRolesInOrderByUserEmailAsc(anyString(), anySet())).thenReturn(userOrganisations);

        ServiceResult<List<UserOrganisationResource>> result = service.findByProcessRolesAndSearchCriteria(externalApplicantRoles(), searchString, searchCategory);

        assertTrue(result.isSuccess());
        assertEquals(2, result.getSuccess().size());
        assertEquals("Guitar Gods Ltd", result.getSuccess().get(0).getOrganisationName());
        assertEquals("Engine Equations Ltd", result.getSuccess().get(1).getOrganisationName());

        verify(userOrganisationRepositoryMock, never()).findByUserFirstNameLikeOrUserLastNameLikeAndUserRolesInOrderByUserEmailAsc(anyString(), anyString(), anySet());
        verify(userOrganisationRepositoryMock).findByOrganisationNameLikeAndUserRolesInOrderByUserEmailAsc(anyString(), anySet());
        verify(userOrganisationRepositoryMock, never()).findByUserEmailLikeAndUserRolesInOrderByUserEmailAsc(anyString(), anySet());
    }

    @Test
    public void findByProcessRolesAndSearchCriteriaWhenSearchCategoryIsEmail(){

        String searchString = "%com%";
        SearchCategory searchCategory = SearchCategory.EMAIL;

        Set<UserOrganisation> userOrganisations = setUpMockingFindByProcessRolesAndSearchCriteria();

        when(userOrganisationRepositoryMock.findByUserEmailLikeAndUserRolesInOrderByUserEmailAsc(anyString(), anySet())).thenReturn(userOrganisations);

        ServiceResult<List<UserOrganisationResource>> result = service.findByProcessRolesAndSearchCriteria(externalApplicantRoles(), searchString, searchCategory);

        assertTrue(result.isSuccess());
        assertEquals(2, result.getSuccess().size());
        assertEquals("aaron.powell@example.com", result.getSuccess().get(0).getEmail());
        assertEquals("david.wellington@load.example.com", result.getSuccess().get(1).getEmail());

        verify(userOrganisationRepositoryMock, never()).findByUserFirstNameLikeOrUserLastNameLikeAndUserRolesInOrderByUserEmailAsc(anyString(), anyString(), anySet());
        verify(userOrganisationRepositoryMock, never()).findByOrganisationNameLikeAndUserRolesInOrderByUserEmailAsc(anyString(), anySet());
        verify(userOrganisationRepositoryMock).findByUserEmailLikeAndUserRolesInOrderByUserEmailAsc(anyString(), anySet());
    }

    @Test
    public void agreeNewTermsAndConditions() {
        List<SiteTermsAndConditionsResource> siteTermsAndConditions = newSiteTermsAndConditionsResource().build(3);

        User existingUser = newUser()
                .withTermsAndConditionsIds(new LinkedHashSet<>(asList(
                        siteTermsAndConditions.get(0).getId(),
                        siteTermsAndConditions.get(1).getId())))
                .build();

        Set<Long> expectedTermsAndConditionsIds = new LinkedHashSet<>(asList(
                siteTermsAndConditions.get(0).getId(),
                siteTermsAndConditions.get(1).getId(),
                siteTermsAndConditions.get(2).getId()
        ));

        when(termsAndConditionsServiceMock.getLatestSiteTermsAndConditions()).thenReturn(
                serviceSuccess(siteTermsAndConditions.get(2)));
        when(userRepositoryMock.findById(existingUser.getId())).thenReturn(Optional.of(existingUser));

        User userToSave = createUserExpectations(existingUser.getId(), expectedTermsAndConditionsIds);

        when(userRepositoryMock.save(userToSave)).thenReturn(userToSave);

        assertTrue(service.agreeNewTermsAndConditions(existingUser.getId()).isSuccess());

        InOrder inOrder = inOrder(termsAndConditionsServiceMock, userRepositoryMock);
        inOrder.verify(termsAndConditionsServiceMock).getLatestSiteTermsAndConditions();
        inOrder.verify(userRepositoryMock).findById(existingUser.getId());
        inOrder.verify(userRepositoryMock).save(createUserExpectations(existingUser.getId(),
                expectedTermsAndConditionsIds));
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void grantRole() {
        GrantRoleCommand grantRoleCommand = new GrantRoleCommand(1L, APPLICANT);
        User user = newUser().build();
        when(userRepositoryMock.findById(grantRoleCommand.getUserId())).thenReturn(Optional.of(user));

        ServiceResult<UserResource> result = service.grantRole(grantRoleCommand);

        assertTrue(result.isSuccess());
        assertTrue(user.hasRole(APPLICANT));
    }

    @Test
    public void updateEmail() {
        String oldEmail = "old@gmail.com";
        String updateEmail = "new@gmail.com";
        User user = newUser().withUid("uid").withFirstName("Bob").withLastName("Man").withEmailAddress(oldEmail).build();

        List<Invite> invite = singletonList(new RoleInvite("Mister", oldEmail, "", APPLICANT, OPENED));

        when(userRepositoryMock.findById(user.getId())).thenReturn(Optional.of(user));
        when(userRepositoryMock.save(user)).thenReturn(user);
        when(userInviteRepositoryMock.findByEmail(oldEmail)).thenReturn(invite);
        when(idpServiceMock.updateUserEmail(user.getUid(), updateEmail)).thenReturn(serviceSuccess(user.getUid()));
        when(notificationServiceMock.sendNotificationWithFlush(any(), eq(EMAIL))).thenReturn(serviceSuccess());
        when(processRoleRepository.findByUser(user)).thenReturn(emptyList());
        when(applicationInviteRepository.findByEmail(oldEmail)).thenReturn(emptyList());
        when(projectUserRepository.findByUserId(user.getId())).thenReturn(emptyList());
        when(projectUserInviteRepository.findByEmail(oldEmail)).thenReturn(emptyList());
        when(projectPartnerInviteRepository.findByEmail(oldEmail)).thenReturn(emptyList());

        ServiceResult<UserResource> result = service.updateEmail(user.getId(), updateEmail);

        assertTrue(result.isSuccess());
        assertEquals(updateEmail, user.getEmail());
        verify(notificationServiceMock, times(2)).sendNotificationWithFlush(any(), eq(EMAIL));
    }

    @Test
    public void updateEmailForNoInviteUsers() {
        String oldEmail = "old@gmail.com";
        User user = newUser().withUid("uid").withFirstName("Bob").withLastName("Man").withEmailAddress(oldEmail).build();
        String updateEmail = "new@gmail.com";

        when(userRepositoryMock.findById(user.getId())).thenReturn(Optional.of(user));
        when(userInviteRepositoryMock.findByEmail(user.getEmail())).thenReturn(emptyList());
        user.setEmail(updateEmail);
        when(userRepositoryMock.save(user)).thenReturn(user);
        when(idpServiceMock.updateUserEmail(user.getUid(), user.getEmail())).thenReturn(serviceSuccess(user.getUid()));
        when(notificationServiceMock.sendNotificationWithFlush(any(), eq(EMAIL))).thenReturn(serviceSuccess());
        when(processRoleRepository.findByUser(user)).thenReturn(emptyList());
        when(applicationInviteRepository.findByEmail(oldEmail)).thenReturn(emptyList());
        when(projectUserRepository.findByUserId(user.getId())).thenReturn(emptyList());
        when(projectUserInviteRepository.findByEmail(oldEmail)).thenReturn(emptyList());
        when(projectPartnerInviteRepository.findByEmail(oldEmail)).thenReturn(emptyList());

        ServiceResult<UserResource> result = service.updateEmail(user.getId(), updateEmail);

        assertTrue(result.isSuccess());
        assertEquals(updateEmail, user.getEmail());
        verify(notificationServiceMock, times(2)).sendNotificationWithFlush(any(), eq(EMAIL));
    }

    @Test
    public void updateEmailAndDisplayErrorIfDuplicateEmailHasBeenFound() {
        String oldEmail = "old@gmail.com";
        User user = newUser().withUid("uid").withEmailAddress("new@gmail.com").build();
        String updateEmail = "new@gmail.com";

        when(userRepositoryMock.findById(user.getId())).thenReturn(Optional.of(user));
        when(idpServiceMock.updateUserEmail(user.getUid(),user.getEmail())).thenReturn(ServiceResult.serviceFailure(USERS_DUPLICATE_EMAIL_ADDRESS));
        when(processRoleRepository.findByUser(user)).thenReturn(emptyList());
        when(applicationInviteRepository.findByEmail(oldEmail)).thenReturn(emptyList());
        when(projectUserRepository.findByUserId(user.getId())).thenReturn(emptyList());
        when(projectUserInviteRepository.findByEmail(oldEmail)).thenReturn(emptyList());
        when(projectPartnerInviteRepository.findByEmail(oldEmail)).thenReturn(emptyList());

        ServiceResult<UserResource> result = service.updateEmail(user.getId(), updateEmail);

        assertTrue(result.isFailure());
    }

    @Test
    public void updateEmailAndDisplayErrorIfNoExistingEmailHasBeenFound() {

        User user = newUser().withEmailAddress("master@gmail.co.uk").build();
        String updateEmail = "new@gmail.com";
        String emailToFind = "master@gmail.com";

        when(userRepositoryMock.findByEmail(emailToFind)).thenReturn(Optional.empty());
        when(idpServiceMock.updateUserEmail(anyString(), anyString())).thenReturn(ServiceResult.serviceFailure(GENERAL_NOT_FOUND));

        ServiceResult<UserResource> result = service.updateEmail(user.getId(), updateEmail);

        assertTrue(result.isFailure());
        assertEquals("master@gmail.co.uk", user.getEmail());
    }

    @Test
    public void updateEmailFailsApplicationInvite() {
        String oldEmail = "old@gmail.com";
        User user = newUser().withUid("uid").withFirstName("Bob").withLastName("Man").withEmailAddress(oldEmail).build();
        String updateEmail = "new@gmail.com";
        long applicationId = 1L;

        Application application = newApplication().withId(applicationId).build();
        ProcessRole processRole = newProcessRole().withApplication(application).build();
        ApplicationInvite applicationInvite = newApplicationInvite().withStatus(InviteStatus.SENT).withApplication(application).build();

        when(userRepositoryMock.findById(user.getId())).thenReturn(Optional.of(user));
        when(userInviteRepositoryMock.findByEmail(user.getEmail())).thenReturn(emptyList());
        user.setEmail(updateEmail);
        when(userRepositoryMock.save(user)).thenReturn(user);
        when(idpServiceMock.updateUserEmail(user.getUid(), user.getEmail())).thenReturn(serviceSuccess(user.getUid()));
        when(notificationServiceMock.sendNotificationWithFlush(any(), eq(EMAIL))).thenReturn(serviceSuccess());
        when(processRoleRepository.findByUser(user)).thenReturn(singletonList(processRole));
        when(applicationInviteRepository.findByEmail(updateEmail)).thenReturn(singletonList(applicationInvite));
        when(projectUserRepository.findByUserId(user.getId())).thenReturn(emptyList());
        when(projectUserInviteRepository.findByEmail(oldEmail)).thenReturn(emptyList());

        ServiceResult<UserResource> result = service.updateEmail(user.getId(), updateEmail);

        assertTrue(result.isFailure());
    }

    @Test
    public void updateEmailFailsProjectInvite() {
        String oldEmail = "old@gmail.com";
        User user = newUser().withUid("uid").withFirstName("Bob").withLastName("Man").withEmailAddress(oldEmail).build();
        String updateEmail = "new@gmail.com";
        long projectId = 1L;

        Application application = newApplication().withId(1l).build();
        Project project = newProject().withId(projectId).withApplication(application).build();
        ProjectUser projectUser = newProjectUser().withProject(project).build();
        ProjectUserInvite projectInvite = newProjectUserInvite().withStatus(InviteStatus.SENT).withProject(project).build();

        when(userRepositoryMock.findById(user.getId())).thenReturn(Optional.of(user));
        when(userInviteRepositoryMock.findByEmail(user.getEmail())).thenReturn(emptyList());
        user.setEmail(updateEmail);
        when(userRepositoryMock.save(user)).thenReturn(user);
        when(idpServiceMock.updateUserEmail(user.getUid(), user.getEmail())).thenReturn(serviceSuccess(user.getUid()));
        when(notificationServiceMock.sendNotificationWithFlush(any(), eq(EMAIL))).thenReturn(serviceSuccess());
        when(processRoleRepository.findByUser(user)).thenReturn(emptyList());
        when(applicationInviteRepository.findByEmail(oldEmail)).thenReturn(emptyList());
        when(projectUserRepository.findByUserId(user.getId())).thenReturn(singletonList(projectUser));
        when(projectUserInviteRepository.findByEmail(updateEmail)).thenReturn(singletonList(projectInvite));
        when(projectRepository.findById(any())).thenReturn(Optional.of(project));

        ServiceResult<UserResource> result = service.updateEmail(user.getId(), updateEmail);

        assertTrue(result.isFailure());
    }

    private User createUserExpectations(Long userId, Set<Long> termsAndConditionsIds) {
        return createLambdaMatcher(user -> {
            assertEquals(userId, user.getId());
            assertEquals(termsAndConditionsIds, user.getTermsAndConditionsIds());
            return true;
        });
    }
}
