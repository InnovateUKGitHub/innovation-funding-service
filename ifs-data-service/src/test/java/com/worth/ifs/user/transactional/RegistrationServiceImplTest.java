package com.worth.ifs.user.transactional;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.worth.ifs.BaseServiceUnitTest;
import com.worth.ifs.authentication.service.RestIdentityProviderService;
import com.worth.ifs.commons.error.CommonErrors;
import com.worth.ifs.commons.error.Error;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.notifications.resource.ExternalUserNotificationTarget;
import com.worth.ifs.notifications.resource.Notification;
import com.worth.ifs.notifications.resource.NotificationSource;
import com.worth.ifs.notifications.resource.NotificationTarget;
import com.worth.ifs.token.domain.Token;
import com.worth.ifs.token.resource.TokenType;
import com.worth.ifs.user.domain.*;
import com.worth.ifs.user.resource.RoleResource;
import com.worth.ifs.user.resource.UserResource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.security.crypto.password.StandardPasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Map;

import static com.worth.ifs.BaseBuilderAmendFunctions.id;
import static com.worth.ifs.LambdaMatcher.createLambdaMatcher;
import static com.worth.ifs.commons.error.CommonErrors.badRequestError;
import static com.worth.ifs.commons.error.CommonErrors.notFoundError;
import static com.worth.ifs.commons.service.ServiceResult.serviceFailure;
import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;
import static com.worth.ifs.user.builder.EthnicityBuilder.newEthnicity;
import static com.worth.ifs.notifications.resource.NotificationMedium.EMAIL;
import static com.worth.ifs.user.builder.CompAdminEmailBuilder.newCompAdminEmail;
import static com.worth.ifs.user.builder.OrganisationBuilder.newOrganisation;
import static com.worth.ifs.user.builder.ProjectFinanceEmailBuilder.newProjectFinanceEmail;
import static com.worth.ifs.user.builder.RoleBuilder.newRole;
import static com.worth.ifs.user.builder.RoleResourceBuilder.newRoleResource;
import static com.worth.ifs.user.builder.UserBuilder.newUser;
import static com.worth.ifs.user.builder.UserResourceBuilder.newUserResource;
import static com.worth.ifs.user.resource.Disability.NO;
import static com.worth.ifs.user.resource.Gender.NOT_STATED;
import static com.worth.ifs.user.resource.UserRoleType.*;
import static com.worth.ifs.util.MapFunctions.asMap;
import static java.time.LocalDateTime.now;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.junit.Assert.*;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

/**
 * Tests around Registration Service
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({RegistrationServiceImpl.class, StandardPasswordEncoder.class})
public class RegistrationServiceImplTest extends BaseServiceUnitTest<RegistrationServiceImpl> {

    private static final String webBaseUrl = "http://ifs-local-dev";

    @Mock
    private StandardPasswordEncoder standardPasswordEncoder;

    @Override
    protected RegistrationServiceImpl supplyServiceUnderTest() {
        final RegistrationServiceImpl service = new RegistrationServiceImpl();
        ReflectionTestUtils.setField(service, "webBaseUrl", webBaseUrl);
        return service;
    }

    @Test
    public void createUser() throws Exception {
        RoleResource roleResource = newRoleResource().build();
        List<Role> roles = newRole().build(1);
        Ethnicity ethnicity = newEthnicity().with(id(1L)).build();

        UserResource userToCreateResource = newUserResource()
                .withId((Long) null)
                .withTitle("Mr")
                .withFirstName("First")
                .withLastName("Last")
                .withGender(NOT_STATED)
                .withEthnicity(ethnicity.getId())
                .withDisability(NO)
                .withPhoneNumber("01234 567890")
                .withEmail("email@example.com")
                .withPassword("Passw0rd123")
                .withRolesGlobal(asList(roleResource))
                .build();

        User userToCreate = newUser()
                .withId((Long) null)
                .withTitle("Mr")
                .withFirstName("First")
                .withLastName("Last")
                .withGender(NOT_STATED)
                .withEthnicity(ethnicity)
                .withDisability(NO)
                .withPhoneNumber("01234 567890")
                .withEmailAddress("email@example.com")
                .withRoles(roles)
                .build();

        when(passwordPolicyValidatorMock.validatePassword("Passw0rd123", userToCreateResource)).thenReturn(serviceSuccess());
        when(userMapperMock.mapToDomain(userToCreateResource)).thenReturn(userToCreate);
        when(idpServiceMock.createUserRecordWithUid("email@example.com", "Passw0rd123")).thenReturn(serviceSuccess("new-uid"));

        User userToSave = createLambdaMatcher(user -> {
            assertNull(user.getId());
            assertEquals("Mr", user.getTitle());
            assertEquals("First Last", user.getName());
            assertEquals("First", user.getFirstName());
            assertEquals("Last", user.getLastName());
            assertEquals(NOT_STATED, user.getGender());
            assertEquals(ethnicity, user.getEthnicity());
            assertEquals(NO, user.getDisability());
            assertEquals("01234 567890", user.getPhoneNumber());
            assertEquals("email@example.com", user.getEmail());

            assertEquals("new-uid", user.getUid());
            assertEquals(1, user.getRoles().size());
            assertEquals(roles, user.getRoles());
            assertTrue(user.getOrganisations().isEmpty());

            return true;
        });

        User savedUser = newUser().build();
        UserResource savedUserResource = newUserResource().build();

        when(userRepositoryMock.save(userToSave)).thenReturn(savedUser);
        when(userMapperMock.mapToResource(savedUser)).thenReturn(savedUserResource);

        ServiceResult<UserResource> result = service.createUser(userToCreateResource);
        assertTrue(result.isSuccess());
        assertEquals(savedUserResource, result.getSuccessObject());
    }

    @Test
    public void testCreateOrganisationUser() {

        UserResource userToCreate = newUserResource().
                withFirstName("First").
                withLastName("Last").
                withEmail("email@example.com").
                withPhoneNumber("01234 567890").
                withPassword("thepassword").
                withTitle("Mr").
                build();

        Organisation selectedOrganisation = newOrganisation().build();
        Role applicantRole = newRole().build();

        when(organisationRepositoryMock.findOne(123L)).thenReturn(selectedOrganisation);
        when(roleRepositoryMock.findOneByName(APPLICANT.getName())).thenReturn(applicantRole);
        when(idpServiceMock.createUserRecordWithUid("email@example.com", "thepassword")).thenReturn(serviceSuccess("new-uid"));

        User expectedCreatedUser = createLambdaMatcher(user -> {

            assertNull(user.getId());
            assertEquals("First Last", user.getName());
            assertEquals("First", user.getFirstName());
            assertEquals("Last", user.getLastName());

            assertEquals("email@example.com", user.getEmail());
            assertEquals("01234 567890", user.getPhoneNumber());
            assertEquals("Mr", user.getTitle());
            assertEquals("new-uid", user.getUid());
            assertEquals(1, user.getRoles().size());
            assertEquals(applicantRole, user.getRoles().get(0));
            assertEquals(1, user.getOrganisations().size());
            assertEquals(selectedOrganisation, user.getOrganisations().get(0));

            return true;
        });

        User savedUser = newUser().with(id(999L)).build();

        when(userRepositoryMock.save(expectedCreatedUser)).thenReturn(savedUser);

        Token expectedToken = createLambdaMatcher(token -> {
            assertEquals(TokenType.VERIFY_EMAIL_ADDRESS, token.getType());
            assertEquals(User.class.getName(), token.getClassName());
            assertEquals(savedUser.getId(), token.getClassPk());
            assertFalse(token.getHash().isEmpty());
            return true;
        });

        when(tokenRepositoryMock.save(expectedToken)).thenReturn(expectedToken);
        when(compAdminEmailRepositoryMock.findOneByEmail(userToCreate.getEmail())).thenReturn(null);
        when(userMapperMock.mapToResource(isA(User.class))).thenReturn(userToCreate);
        when(passwordPolicyValidatorMock.validatePassword("thepassword", userToCreate)).thenReturn(serviceSuccess());

        ServiceResult<UserResource> result = service.createOrganisationUser(123L, userToCreate);
        assertTrue(result.isSuccess());
        assertEquals(userToCreate, result.getSuccessObject());
    }

    @Test
    public void testCreateApplicantUserButOrganisationNotFound() {

        UserResource userToCreate = newUserResource().
                withFirstName("First").
                withLastName("Last").
                withEmail("email@example.com").
                withPhoneNumber("01234 567890").
                withPassword("thepassword").
                withTitle("Mr").
                build();

        when(organisationRepositoryMock.findOne(123L)).thenReturn(null);
        when(compAdminEmailRepositoryMock.findOneByEmail(userToCreate.getEmail())).thenReturn(null);
        when(userMapperMock.mapToResource(isA(User.class))).thenReturn(userToCreate);
        when(passwordPolicyValidatorMock.validatePassword("thepassword", userToCreate)).thenReturn(serviceSuccess());

        ServiceResult<UserResource> result = service.createOrganisationUser(123L, userToCreate);
        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(notFoundError(Organisation.class, 123L)));
    }

    @Test
    public void testCreateApplicantUserButRoleNotFound() {

        UserResource userToCreate = newUserResource().
                withFirstName("First").
                withLastName("Last").
                withEmail("email@example.com").
                withPhoneNumber("01234 567890").
                withPassword("thepassword").
                withTitle("Mr").
                build();

        Organisation selectedOrganisation = newOrganisation().build();

        when(organisationRepositoryMock.findOne(123L)).thenReturn(selectedOrganisation);
        when(roleRepositoryMock.findOneByName(APPLICANT.getName())).thenReturn(null);
        when(compAdminEmailRepositoryMock.findOneByEmail(userToCreate.getEmail())).thenReturn(null);
        when(userMapperMock.mapToResource(isA(User.class))).thenReturn(userToCreate);
        when(passwordPolicyValidatorMock.validatePassword("thepassword", userToCreate)).thenReturn(serviceSuccess());

        ServiceResult<UserResource> result = service.createOrganisationUser(123L, userToCreate);
        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(notFoundError(Role.class, APPLICANT.getName())));
    }

    @Test
    public void testCreateApplicantUserButIdpCallFails() {

        UserResource userToCreate = newUserResource().
                withFirstName("First").
                withLastName("Last").
                withEmail("email@example.com").
                withPhoneNumber("01234 567890").
                withPassword("thepassword").
                withTitle("Mr").
                build();

        Organisation selectedOrganisation = newOrganisation().build();
        Role applicantRole = newRole().build();

        when(organisationRepositoryMock.findOne(123L)).thenReturn(selectedOrganisation);
        when(roleRepositoryMock.findOneByName(APPLICANT.getName())).thenReturn(applicantRole);
        when(idpServiceMock.createUserRecordWithUid("email@example.com", "thepassword")).thenReturn(serviceFailure(new Error(RestIdentityProviderService.ServiceFailures.UNABLE_TO_CREATE_USER, INTERNAL_SERVER_ERROR)));
        when(compAdminEmailRepositoryMock.findOneByEmail(userToCreate.getEmail())).thenReturn(null);
        when(userMapperMock.mapToResource(isA(User.class))).thenReturn(userToCreate);
        when(passwordPolicyValidatorMock.validatePassword("thepassword", userToCreate)).thenReturn(serviceSuccess());

        ServiceResult<UserResource> result = service.createOrganisationUser(123L, userToCreate);
        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(new Error(RestIdentityProviderService.ServiceFailures.UNABLE_TO_CREATE_USER, INTERNAL_SERVER_ERROR)));
    }

    @Test
    public void testCreateApplicantUserButPasswordValidationFails() {

        UserResource userToCreate = newUserResource().withPassword("thepassword").build();
        Organisation selectedOrganisation = newOrganisation().build();
        Role applicantRole = newRole().build();

        when(organisationRepositoryMock.findOne(123L)).thenReturn(selectedOrganisation);
        when(roleRepositoryMock.findOneByName(APPLICANT.getName())).thenReturn(applicantRole);
        when(idpServiceMock.createUserRecordWithUid("email@example.com", "thepassword")).thenReturn(serviceFailure(new Error(RestIdentityProviderService.ServiceFailures.UNABLE_TO_CREATE_USER, INTERNAL_SERVER_ERROR)));
        when(compAdminEmailRepositoryMock.findOneByEmail(userToCreate.getEmail())).thenReturn(null);
        when(userMapperMock.mapToResource(isA(User.class))).thenReturn(userToCreate);
        when(passwordPolicyValidatorMock.validatePassword("thepassword", userToCreate)).thenReturn(serviceFailure(badRequestError("bad password")));

        ServiceResult<UserResource> result = service.createOrganisationUser(123L, userToCreate);
        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(CommonErrors.badRequestError("bad password")));
    }

    @Test
    public void testCreateCompAdminUserForOrganisation() {
        UserResource userToCreate = newUserResource().
                withFirstName("First").
                withLastName("Last").
                withEmail("email@example.com").
                withPhoneNumber("01234 567890").
                withPassword("thepassword").
                withTitle("Mr").
                build();

        Organisation selectedOrganisation = newOrganisation().build();
        Role compAdminRole = newRole().build();

        when(organisationRepositoryMock.findOne(123L)).thenReturn(selectedOrganisation);
        when(roleRepositoryMock.findOneByName(COMP_ADMIN.getName())).thenReturn(compAdminRole);
        when(idpServiceMock.createUserRecordWithUid("email@example.com", "thepassword")).thenReturn(serviceSuccess("new-uid"));

        User expectedCreatedUser = createLambdaMatcher(user -> {

            assertNull(user.getId());
            assertEquals("First Last", user.getName());
            assertEquals("First", user.getFirstName());
            assertEquals("Last", user.getLastName());

            assertEquals("email@example.com", user.getEmail());
            assertEquals("01234 567890", user.getPhoneNumber());
            assertEquals("Mr", user.getTitle());
            assertEquals("new-uid", user.getUid());
            assertEquals(1, user.getRoles().size());
            assertEquals(compAdminRole, user.getRoles().get(0));
            assertEquals(1, user.getOrganisations().size());
            assertEquals(selectedOrganisation, user.getOrganisations().get(0));

            return true;
        });

        User savedUser = newUser().with(id(999L)).build();

        when(userRepositoryMock.save(expectedCreatedUser)).thenReturn(savedUser);

        Token expectedToken = createLambdaMatcher(token -> {
            assertEquals(TokenType.VERIFY_EMAIL_ADDRESS, token.getType());
            assertEquals(User.class.getName(), token.getClassName());
            assertEquals(savedUser.getId(), token.getClassPk());
            assertFalse(token.getHash().isEmpty());
            return true;
        });

        CompAdminEmail compAdminEmail = newCompAdminEmail().withEmail("email@example.com").build();

        when(tokenRepositoryMock.save(expectedToken)).thenReturn(expectedToken);
        when(compAdminEmailRepositoryMock.findOneByEmail(userToCreate.getEmail())).thenReturn(compAdminEmail);
        when(userMapperMock.mapToResource(isA(User.class))).thenReturn(userToCreate);
        when(passwordPolicyValidatorMock.validatePassword("thepassword", userToCreate)).thenReturn(serviceSuccess());

        ServiceResult<UserResource> result = service.createOrganisationUser(123L, userToCreate);
        assertTrue(result.isSuccess());
        assertEquals(userToCreate, result.getSuccessObject());
    }

    @Test
    public void testCreateCompAdminUserForOrganisationButRoleNotFound() {

        UserResource userToCreate = newUserResource().
                withFirstName("First").
                withLastName("Last").
                withEmail("email@example.com").
                withPhoneNumber("01234 567890").
                withPassword("thepassword").
                withTitle("Mr").
                build();

        Organisation selectedOrganisation = newOrganisation().build();
        CompAdminEmail compAdminEmail = newCompAdminEmail().withEmail("email@example.com").build();

        when(organisationRepositoryMock.findOne(123L)).thenReturn(selectedOrganisation);
        when(roleRepositoryMock.findOneByName(COMP_ADMIN.getName())).thenReturn(null);
        when(compAdminEmailRepositoryMock.findOneByEmail(userToCreate.getEmail())).thenReturn(compAdminEmail);
        when(userMapperMock.mapToResource(isA(User.class))).thenReturn(userToCreate);
        when(passwordPolicyValidatorMock.validatePassword("thepassword", userToCreate)).thenReturn(serviceSuccess());

        ServiceResult<UserResource> result = service.createOrganisationUser(123L, userToCreate);
        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(notFoundError(Role.class, COMP_ADMIN.getName())));
    }

    @Test
    public void testCreateProjectFinanceUserForOrganisation() {
        UserResource userToCreate = newUserResource().
                withFirstName("First").
                withLastName("Last").
                withEmail("email@example.com").
                withPhoneNumber("01234 567890").
                withPassword("thepassword").
                withTitle("Mr").
                build();

        Organisation selectedOrganisation = newOrganisation().build();
        Role projectFinanceRole = newRole().build();

        when(organisationRepositoryMock.findOne(123L)).thenReturn(selectedOrganisation);
        when(roleRepositoryMock.findOneByName(PROJECT_FINANCE.getName())).thenReturn(projectFinanceRole);
        when(idpServiceMock.createUserRecordWithUid("email@example.com", "thepassword")).thenReturn(serviceSuccess("new-uid"));

        User expectedCreatedUser = createLambdaMatcher(user -> {

            assertNull(user.getId());
            assertEquals("First Last", user.getName());
            assertEquals("First", user.getFirstName());
            assertEquals("Last", user.getLastName());

            assertEquals("email@example.com", user.getEmail());
            assertEquals("01234 567890", user.getPhoneNumber());
            assertEquals("Mr", user.getTitle());
            assertEquals("new-uid", user.getUid());
            assertEquals(1, user.getRoles().size());
            assertEquals(projectFinanceRole, user.getRoles().get(0));
            assertEquals(1, user.getOrganisations().size());
            assertEquals(selectedOrganisation, user.getOrganisations().get(0));

            return true;
        });

        User savedUser = newUser().with(id(999L)).build();

        when(userRepositoryMock.save(expectedCreatedUser)).thenReturn(savedUser);

        Token expectedToken = createLambdaMatcher(token -> {
            assertEquals(TokenType.VERIFY_EMAIL_ADDRESS, token.getType());
            assertEquals(User.class.getName(), token.getClassName());
            assertEquals(savedUser.getId(), token.getClassPk());
            assertFalse(token.getHash().isEmpty());
            return true;
        });

        ProjectFinanceEmail projectFinanceEmail = newProjectFinanceEmail().withEmail("email@example.com").build();

        when(tokenRepositoryMock.save(expectedToken)).thenReturn(expectedToken);
        when(projectFinanceEmailRepositoryMock.findOneByEmail(userToCreate.getEmail())).thenReturn(projectFinanceEmail);
        when(userMapperMock.mapToResource(isA(User.class))).thenReturn(userToCreate);
        when(passwordPolicyValidatorMock.validatePassword("thepassword", userToCreate)).thenReturn(serviceSuccess());

        ServiceResult<UserResource> result = service.createOrganisationUser(123L, userToCreate);
        assertTrue(result.isSuccess());
        assertEquals(userToCreate, result.getSuccessObject());
    }

    @Test
    public void testSendUserVerificationEmail() {
        final UserResource userResource = newUserResource()
                .withId(1L)
                .withFirstName("Sample")
                .withLastName("User")
                .withEmail("sample@me.com")
                .build();

        // mock the random number that will be used to create the hash
        final double random = 0.6996293870272714;
        PowerMockito.mockStatic(Math.class);
        when(Math.random()).thenReturn(random);
        when(Math.ceil(random * 1000)).thenReturn(700d);

        final String hash = "1e627a59879066b44781ca584a23be742d3197dff291245150e62f3d4d3d303e1a87d34fc8a3a2e0";
        ReflectionTestUtils.setField(service, "encoder", standardPasswordEncoder);
        when(standardPasswordEncoder.encode("1==sample@me.com==700")).thenReturn(hash);

        final Token token = new Token(TokenType.VERIFY_EMAIL_ADDRESS, User.class.getName(), userResource.getId(), hash, now(), JsonNodeFactory.instance.objectNode());
        final String verificationLink = String.format("%s/registration/verify-email/%s", webBaseUrl, hash);

        final Map<String, Object> expectedNotificationArguments = asMap("verificationLink", verificationLink);

        final NotificationSource from = systemNotificationSourceMock;
        final NotificationTarget to = new ExternalUserNotificationTarget(userResource.getName(), userResource.getEmail());

        final Notification notification = new Notification(from, singletonList(to), RegistrationServiceImpl.Notifications.VERIFY_EMAIL_ADDRESS, expectedNotificationArguments);
        when(tokenRepositoryMock.save(isA(Token.class))).thenReturn(token);
        when(notificationServiceMock.sendNotification(notification, EMAIL)).thenReturn(serviceSuccess());

        final ServiceResult<Void> result = service.sendUserVerificationEmail(userResource, empty());
        assertTrue(result.isSuccess());
    }

    @Test
    public void testResendUserVerificationEmail() {
        final UserResource userResource = newUserResource()
                .withId(1L)
                .withFirstName("Sample")
                .withLastName("User")
                .withEmail("sample@me.com")
                .build();

        // mock the random number that will be used to create the hash
        final double random = 0.6996293870272714;
        PowerMockito.mockStatic(Math.class);
        when(Math.random()).thenReturn(random);
        when(Math.ceil(random * 1000)).thenReturn(700d);

        final String hash = "1e627a59879066b44781ca584a23be742d3197dff291245150e62f3d4d3d303e1a87d34fc8a3a2e0";
        ReflectionTestUtils.setField(service, "encoder", standardPasswordEncoder);
        when(standardPasswordEncoder.encode("1==sample@me.com==700")).thenReturn(hash);

        final Token existingToken = new Token(TokenType.VERIFY_EMAIL_ADDRESS, User.class.getName(), userResource.getId(), "existing-token", now(), JsonNodeFactory.instance.objectNode());
        final Token newToken = new Token(TokenType.VERIFY_EMAIL_ADDRESS, User.class.getName(), userResource.getId(), hash, now(), JsonNodeFactory.instance.objectNode());
        final String verificationLink = String.format("%s/registration/verify-email/%s", webBaseUrl, hash);

        final Map<String, Object> expectedNotificationArguments = asMap("verificationLink", verificationLink);

        final NotificationSource from = systemNotificationSourceMock;
        final NotificationTarget to = new ExternalUserNotificationTarget(userResource.getName(), userResource.getEmail());

        final Notification notification = new Notification(from, singletonList(to), RegistrationServiceImpl.Notifications.VERIFY_EMAIL_ADDRESS, expectedNotificationArguments);
        when(tokenRepositoryMock.findByTypeAndClassNameAndClassPk(TokenType.VERIFY_EMAIL_ADDRESS, User.class.getName(), 1L)).thenReturn(of(existingToken));
        when(tokenRepositoryMock.save(isA(Token.class))).thenReturn(newToken);
        when(notificationServiceMock.sendNotification(notification, EMAIL)).thenReturn(serviceSuccess());

        final ServiceResult<Void> result = service.resendUserVerificationEmail(userResource);
        assertTrue(result.isSuccess());
    }
}
