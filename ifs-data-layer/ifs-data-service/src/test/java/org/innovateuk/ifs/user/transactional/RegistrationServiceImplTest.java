package org.innovateuk.ifs.user.transactional;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.address.domain.Address;
import org.innovateuk.ifs.address.mapper.AddressMapper;
import org.innovateuk.ifs.address.resource.AddressResource;
import org.innovateuk.ifs.authentication.service.IdentityProviderService;
import org.innovateuk.ifs.authentication.service.RestIdentityProviderService;
import org.innovateuk.ifs.authentication.validator.PasswordPolicyValidator;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.resource.SiteTermsAndConditionsResource;
import org.innovateuk.ifs.competition.transactional.TermsAndConditionsService;
import org.innovateuk.ifs.invite.constant.InviteStatus;
import org.innovateuk.ifs.invite.domain.RoleInvite;
import org.innovateuk.ifs.invite.repository.RoleInviteRepository;
import org.innovateuk.ifs.notifications.resource.*;
import org.innovateuk.ifs.notifications.service.NotificationService;
import org.innovateuk.ifs.organisation.domain.Organisation;
import org.innovateuk.ifs.organisation.repository.OrganisationRepository;
import org.innovateuk.ifs.profile.domain.Profile;
import org.innovateuk.ifs.profile.repository.ProfileRepository;
import org.innovateuk.ifs.registration.resource.InternalUserRegistrationResource;
import org.innovateuk.ifs.registration.resource.UserRegistrationResource;
import org.innovateuk.ifs.token.domain.Token;
import org.innovateuk.ifs.token.repository.TokenRepository;
import org.innovateuk.ifs.token.resource.TokenType;
import org.innovateuk.ifs.user.builder.UserBuilder;
import org.innovateuk.ifs.user.builder.UserResourceBuilder;
import org.innovateuk.ifs.user.domain.Ethnicity;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.mapper.EthnicityMapper;
import org.innovateuk.ifs.user.mapper.UserMapper;
import org.innovateuk.ifs.user.repository.UserRepository;
import org.innovateuk.ifs.user.resource.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.security.crypto.password.StandardPasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.ZonedDateTime;
import java.util.*;

import static java.time.ZonedDateTime.now;
import static java.util.Arrays.asList;
import static java.util.Collections.singleton;
import static java.util.Collections.singletonList;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.innovateuk.ifs.LambdaMatcher.createLambdaMatcher;
import static org.innovateuk.ifs.address.builder.AddressBuilder.newAddress;
import static org.innovateuk.ifs.address.builder.AddressResourceBuilder.newAddressResource;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.id;
import static org.innovateuk.ifs.commons.error.CommonErrors.badRequestError;
import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.GENERAL_NOT_FOUND;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.NOT_AN_INTERNAL_USER_ROLE;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.competition.builder.SiteTermsAndConditionsResourceBuilder.newSiteTermsAndConditionsResource;
import static org.innovateuk.ifs.invite.builder.RoleInviteBuilder.newRoleInvite;
import static org.innovateuk.ifs.notifications.resource.NotificationMedium.EMAIL;
import static org.innovateuk.ifs.organisation.builder.OrganisationBuilder.newOrganisation;
import static org.innovateuk.ifs.profile.builder.ProfileBuilder.newProfile;
import static org.innovateuk.ifs.registration.builder.InternalUserRegistrationResourceBuilder.newInternalUserRegistrationResource;
import static org.innovateuk.ifs.registration.builder.UserRegistrationResourceBuilder.newUserRegistrationResource;
import static org.innovateuk.ifs.user.builder.EthnicityBuilder.newEthnicity;
import static org.innovateuk.ifs.user.builder.EthnicityResourceBuilder.newEthnicityResource;
import static org.innovateuk.ifs.user.builder.UserBuilder.newUser;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.user.resource.Disability.NO;
import static org.innovateuk.ifs.user.resource.Gender.NOT_STATED;
import static org.innovateuk.ifs.user.resource.Title.Mr;
import static org.innovateuk.ifs.util.MapFunctions.asMap;
import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

/**
 * Tests around Registration Service
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({RegistrationServiceImpl.class, StandardPasswordEncoder.class})
public class RegistrationServiceImplTest extends BaseServiceUnitTest<RegistrationServiceImpl> {

    private static final String webBaseUrl = "http://ifs-local-dev";

    private UserResource userToEdit;

    private UserResource userResourceInDB;

    private User userInDB;

    private User updatedUserInDB;

    @Mock
    private TermsAndConditionsService termsAndConditionsServiceMock;

    @Mock
    private StandardPasswordEncoder standardPasswordEncoder;

    @Mock
    private ProfileRepository profileRepositoryMock;

    @Mock
    private PasswordPolicyValidator passwordPolicyValidatorMock;

    @Mock
    private UserMapper userMapperMock;

    @Mock
    private IdentityProviderService idpServiceMock;

    @Mock
    private AddressMapper addressMapperMock;

    @Mock
    private EthnicityMapper ethnicityMapperMock;

    @Mock
    private UserRepository userRepositoryMock;

    @Mock
    private TokenRepository tokenRepositoryMock;

    @Mock
    private OrganisationRepository organisationRepositoryMock;

    @Mock
    private NotificationService notificationServiceMock;

    @Mock
    private SystemNotificationSource systemNotificationSourceMock;

    @Mock
    private BaseUserService baseUserServiceMock;

    @Mock
    private RoleInviteRepository roleInviteRepositoryMock;

    @Override
    protected RegistrationServiceImpl supplyServiceUnderTest() {
        final RegistrationServiceImpl service = new RegistrationServiceImpl();
        ReflectionTestUtils.setField(service, "webBaseUrl", webBaseUrl);
        return service;
    }

    @Test
    public void createUser() {
        Set<Role> roles = singleton(Role.ASSESSOR);
        EthnicityResource ethnicityResource = newEthnicityResource().with(id(1L)).build();
        Ethnicity ethnicity = newEthnicity().withId(1L).build();
        AddressResource addressResource = newAddressResource().withAddressLine1("Electric Works").withTown("Sheffield").withPostcode("S1 2BJ").build();
        Address address = newAddress().withAddressLine1("Electric Works").withTown("Sheffield").withPostcode("S1 2BJ").build();

        UserRegistrationResource userToCreateResource = newUserRegistrationResource()
                .withTitle(Mr)
                .withFirstName("First")
                .withLastName("Last")
                .withGender(NOT_STATED)
                .withEthnicity(ethnicityResource)
                .withDisability(NO)
                .withPhoneNumber("01234 567890")
                .withEmail("email@example.com")
                .withPassword("Passw0rd123")
                .withAddress(addressResource)
                .withRoles(new ArrayList(roles))
                .build();

        Long profileId = 1L;
        Profile userProfile = newProfile()
                .withId(profileId)
                .withAddress(address)
                .build();

        User userToCreate = newUser()
                .withId((Long) null)
                .withTitle(Mr)
                .withFirstName("First")
                .withLastName("Last")
                .withGender(NOT_STATED)
                .withEthnicity(ethnicity)
                .withDisability(NO)
                .withPhoneNumber("01234 567890")
                .withEmailAddress("email@example.com")
                .withRoles(roles)
                .withProfileId(userProfile.getId())
                .build();

        when(profileRepositoryMock.findOne(userToCreate.getProfileId())).thenReturn(userProfile);
        when(profileRepositoryMock.save(any(Profile.class))).thenReturn(userProfile);
        when(passwordPolicyValidatorMock.validatePassword("Passw0rd123", userToCreateResource.toUserResource())).thenReturn(serviceSuccess());
        when(userMapperMock.mapToDomain(userToCreateResource.toUserResource())).thenReturn(userToCreate);
        when(addressMapperMock.mapToDomain(userToCreateResource.getAddress())).thenReturn(
                newAddress().withAddressLine1("Electric Works").withTown("Sheffield").withPostcode("S1 2BJ").build());
        when(idpServiceMock.createUserRecordWithUid("email@example.com", "Passw0rd123")).thenReturn(serviceSuccess("new-uid"));

        User userToSave = createLambdaMatcher(user -> {
            assertNull(user.getId());
            assertEquals(Mr, user.getTitle());
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

            assertNotNull(user.getProfileId());
            Profile profile = profileRepositoryMock.findOne(user.getProfileId());
            assertEquals(profileId, profile.getId());
            assertNull(profile.getSkillsAreas());
            assertNull(profile.getBusinessType());
            assertNull(profile.getAgreement());
            assertNull(profile.getCreatedBy());
            assertNull(profile.getCreatedOn());
            assertNull(profile.getModifiedBy());
            assertNull(profile.getModifiedOn());
            assertEquals(address, profile.getAddress());


            return true;
        });

        User savedUser = newUser().build();
        UserResource savedUserResource = newUserResource().build();

        when(userRepositoryMock.save(userToSave)).thenReturn(savedUser);
        when(userMapperMock.mapToResource(savedUser)).thenReturn(savedUserResource);

        UserResource result = service.createUser(userToCreateResource).getSuccess();

        assertEquals(savedUserResource, result);
    }

    @Test
    public void testCreateOrganisationUser() {

        UserResource userToCreate = newUserResource().
                withFirstName("First").
                withLastName("Last").
                withEmail("email@example.com").
                withPhoneNumber("01234 567890").
                withPassword("thepassword").
                withTitle(Mr).
                withDisability(Disability.YES).
                withGender(Gender.MALE).
                withEthnicity(2L).
                build();

        SiteTermsAndConditionsResource siteTermsAndConditions = newSiteTermsAndConditionsResource().build();
        Organisation selectedOrganisation = newOrganisation().withId(123L).build();
        Role applicantRole = Role.APPLICANT;


        when(termsAndConditionsServiceMock.getLatestSiteTermsAndConditions()).thenReturn(serviceSuccess(siteTermsAndConditions));
        when(ethnicityMapperMock.mapIdToDomain(2L)).thenReturn(newEthnicity().withId(2L).build());
        when(organisationRepositoryMock.findOne(123L)).thenReturn(selectedOrganisation);
        when(organisationRepositoryMock.findByUsersId(anyLong())).thenReturn(singletonList(selectedOrganisation));
        when(idpServiceMock.createUserRecordWithUid("email@example.com", "thepassword")).thenReturn(serviceSuccess("new-uid"));

        Profile expectedProfile = newProfile().withId(7L).build();
        when(profileRepositoryMock.save(any(Profile.class))).thenReturn(expectedProfile);

        User expectedCreatedUser = createLambdaMatcher(user -> {

            assertNull(user.getId());
            assertEquals("First Last", user.getName());
            assertEquals("First", user.getFirstName());
            assertEquals("Last", user.getLastName());

            assertEquals("email@example.com", user.getEmail());
            assertEquals("01234 567890", user.getPhoneNumber());
            assertEquals(Mr, user.getTitle());
            assertEquals("new-uid", user.getUid());
            assertEquals(Gender.MALE, user.getGender());
            assertEquals(Disability.YES, user.getDisability());
            assertEquals(Long.valueOf(2), user.getEthnicity().getId());
            assertEquals(1, user.getRoles().size());
            assertTrue(user.getRoles().contains(applicantRole));
            List<Organisation> orgs = organisationRepositoryMock.findByUsersId(user.getId());
            assertEquals(1, orgs.size());
            assertEquals(selectedOrganisation, orgs.get(0));
            assertEquals(expectedProfile.getId(), user.getProfileId());
            assertEquals(new LinkedHashSet<>(asList(siteTermsAndConditions.getId())), user.getTermsAndConditionsIds());

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
        when(userMapperMock.mapToResource(isA(User.class))).thenReturn(userToCreate);
        when(passwordPolicyValidatorMock.validatePassword("thepassword", userToCreate)).thenReturn(serviceSuccess());

        UserResource result = service.createOrganisationUser(123L, userToCreate).getSuccess();

        assertEquals(userToCreate, result);
    }

    @Test
    public void testCreateApplicantUserButOrganisationNotFound() {

        UserResource userToCreate = newUserResource().
                withFirstName("First").
                withLastName("Last").
                withEmail("email@example.com").
                withPhoneNumber("01234 567890").
                withPassword("thepassword").
                withTitle(Mr).
                build();

        SiteTermsAndConditionsResource siteTermsAndConditions = newSiteTermsAndConditionsResource().build();

        when(termsAndConditionsServiceMock.getLatestSiteTermsAndConditions()).thenReturn(serviceSuccess(siteTermsAndConditions));
        when(organisationRepositoryMock.findOne(123L)).thenReturn(null);
        when(userMapperMock.mapToResource(isA(User.class))).thenReturn(userToCreate);
        when(passwordPolicyValidatorMock.validatePassword("thepassword", userToCreate)).thenReturn(serviceSuccess());

        ServiceResult<UserResource> result = service.createOrganisationUser(123L, userToCreate);
        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(notFoundError(Organisation.class, 123L)));
    }

    @Test
    public void testCreateApplicantUserButIdpCallFails() {

        UserResource userToCreate = newUserResource().
                withFirstName("First").
                withLastName("Last").
                withEmail("email@example.com").
                withPhoneNumber("01234 567890").
                withPassword("thepassword").
                withTitle(Mr).
                build();

        SiteTermsAndConditionsResource siteTermsAndConditions = newSiteTermsAndConditionsResource().build();
        Organisation selectedOrganisation = newOrganisation().build();

        when(termsAndConditionsServiceMock.getLatestSiteTermsAndConditions()).thenReturn(serviceSuccess(siteTermsAndConditions));
        when(organisationRepositoryMock.findOne(123L)).thenReturn(selectedOrganisation);
        when(idpServiceMock.createUserRecordWithUid("email@example.com", "thepassword")).thenReturn(serviceFailure(new Error(RestIdentityProviderService.ServiceFailures.UNABLE_TO_CREATE_USER, INTERNAL_SERVER_ERROR)));
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

        when(organisationRepositoryMock.findOne(123L)).thenReturn(selectedOrganisation);
        when(idpServiceMock.createUserRecordWithUid("email@example.com", "thepassword")).thenReturn(serviceFailure(new Error(RestIdentityProviderService.ServiceFailures.UNABLE_TO_CREATE_USER, INTERNAL_SERVER_ERROR)));
        when(userMapperMock.mapToResource(isA(User.class))).thenReturn(userToCreate);
        when(passwordPolicyValidatorMock.validatePassword("thepassword", userToCreate)).thenReturn(serviceFailure(badRequestError("bad password")));

        ServiceResult<UserResource> result = service.createOrganisationUser(123L, userToCreate);
        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(Error.fieldError("password", null, "bad password")));
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
        final NotificationTarget to = new UserNotificationTarget(userResource.getName(), userResource.getEmail());

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
        final NotificationTarget to = new UserNotificationTarget(userResource.getName(), userResource.getEmail());

        final Notification notification = new Notification(from, singletonList(to), RegistrationServiceImpl.Notifications.VERIFY_EMAIL_ADDRESS, expectedNotificationArguments);
        when(tokenRepositoryMock.findByTypeAndClassNameAndClassPk(TokenType.VERIFY_EMAIL_ADDRESS, User.class.getName(), 1L)).thenReturn(of(existingToken));
        when(tokenRepositoryMock.save(isA(Token.class))).thenReturn(newToken);
        when(notificationServiceMock.sendNotification(notification, EMAIL)).thenReturn(serviceSuccess());

        final ServiceResult<Void> result = service.resendUserVerificationEmail(userResource);
        assertTrue(result.isSuccess());
    }

    @Test
    public void testCreateInternalUser() throws Exception {
        RoleInvite roleInvite = newRoleInvite().withRole(Role.PROJECT_FINANCE).build();
        InternalUserRegistrationResource internalUserRegistrationResource = newInternalUserRegistrationResource()
                .withFirstName("First")
                .withLastName("Last")
                .withEmail("email@example.com")
                .withPassword("Passw0rd123")
                .withRoles(singletonList(Role.PROJECT_FINANCE))
                .build();

        User userToCreate = newUser()
                .withId((Long) null)
                .withFirstName("First")
                .withLastName("Last")
                .withEmailAddress("email@example.com")
                .withRoles(singleton(Role.PROJECT_FINANCE))
                .build();

        when(roleInviteRepositoryMock.getByHash("SomeInviteHash")).thenReturn(roleInvite);
        when(passwordPolicyValidatorMock.validatePassword(anyString(), any(UserResource.class))).thenReturn(serviceSuccess());
        when(idpServiceMock.createUserRecordWithUid("email@example.com", "Passw0rd123")).thenReturn(serviceSuccess("new-uid"));
        when(profileRepositoryMock.save(any(Profile.class))).thenReturn(newProfile().build());
        when(userMapperMock.mapToDomain(any(UserResource.class))).thenReturn(userToCreate);
        when(idpServiceMock.activateUser("new-uid")).thenReturn(serviceSuccess("new-uid"));
        when(userRepositoryMock.save(any(User.class))).thenReturn(userToCreate);
        ServiceResult<Void> result = service.createInternalUser("SomeInviteHash", internalUserRegistrationResource);
        assertTrue(result.isSuccess());
        assertEquals(InviteStatus.OPENED, roleInvite.getStatus());
    }

    @Test
    public void editInternalUserWhenNewRoleIsNotInternalRole() {

        UserResource userToEdit = UserResourceBuilder.newUserResource().build();

        ServiceResult<Void> result = service.editInternalUser(userToEdit, Role.COLLABORATOR);

        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(NOT_AN_INTERNAL_USER_ROLE));

    }

    @Test
    public void editInternalUserWhenUserDoesNotExist() {

        UserResource userToEdit = UserResourceBuilder.newUserResource().build();

        when(baseUserServiceMock.getUserById(userToEdit.getId())).thenReturn(serviceFailure(notFoundError(User.class, userToEdit.getId())));

        ServiceResult<Void> result = service.editInternalUser(userToEdit, Role.SUPPORT);

        assertTrue(result.isFailure());
        assertEquals(GENERAL_NOT_FOUND.getErrorKey(), result.getErrors().get(0).getErrorKey());

    }

    @Test
    public void editInternalUserSuccess() {

        setUpUsersForEditInternalUserSuccess();

        Role newRole = Role.SUPPORT;
        
        when(userRepositoryMock.findOne(userToEdit.getId())).thenReturn(userInDB);
        when(userMapperMock.mapToDomain(userResourceInDB)).thenReturn(userInDB);

        ServiceResult<Void> result = service.editInternalUser(userToEdit, newRole);

        assertTrue(result.isSuccess());
        verify(userRepositoryMock).save(userInDB);
        assertTrue(userInDB.getRoles().stream().anyMatch(role1 -> role1.equals(Role.SUPPORT)));
        assertEquals(userInDB.getFirstName(), userToEdit.getFirstName());
        assertEquals(userInDB.getLastName(), userToEdit.getLastName());

    }

    @Test
    public void deactivateUserSuccess() {

        setUpUsersForEditInternalUserSuccess();

        when(userRepositoryMock.findOne(userToEdit.getId())).thenReturn(userInDB);
        when(idpServiceMock.deactivateUser(userToEdit.getUid())).thenReturn(ServiceResult.serviceSuccess(""));
        userInDB.setStatus(UserStatus.INACTIVE);
        when(userRepositoryMock.save(userInDB)).thenReturn(userInDB);

        ServiceResult<Void> result = service.deactivateUser(userToEdit.getId());

        verify(userRepositoryMock).save(userInDB);

        assertTrue(result.isSuccess());
    }

    @Test
    public void deactivateUserIdpFails() {

        setUpUsersForEditInternalUserSuccess();

        when(userRepositoryMock.findOne(userToEdit.getId())).thenReturn(userInDB);
        when(idpServiceMock.deactivateUser(userToEdit.getUid())).thenReturn(ServiceResult.serviceFailure(GENERAL_NOT_FOUND));

        ServiceResult<Void> result = service.deactivateUser(userToEdit.getId());

        assertNull(result.getSuccess());
    }

    @Test
    public void deactivateUserNoUser() {

        setUpUsersForEditInternalUserSuccess();

        when(userRepositoryMock.findOne(userToEdit.getId())).thenReturn(null);

        ServiceResult<Void> result = service.deactivateUser(userToEdit.getId());

        assertTrue(result.isFailure());
    }

    @Test
    public void activateUserSuccess() {

        setUpUsersForEditInternalUserSuccess();

        when(userRepositoryMock.findOne(userToEdit.getId())).thenReturn(userInDB);
        when(idpServiceMock.activateUser(userToEdit.getUid())).thenReturn(ServiceResult.serviceSuccess(""));
        userInDB.setStatus(UserStatus.ACTIVE);
        when(userRepositoryMock.save(userInDB)).thenReturn(updatedUserInDB);

        ServiceResult<Void> result = service.activateUser(userToEdit.getId());

        verify(userRepositoryMock).save(userInDB);

        assertTrue(result.isSuccess());
    }

    @Test
    public void activateUserIdpFails() {

        setUpUsersForEditInternalUserSuccess();

        when(userRepositoryMock.findOne(userToEdit.getId())).thenReturn(userInDB);
        when(idpServiceMock.activateUser(userToEdit.getUid())).thenReturn(ServiceResult.serviceFailure(GENERAL_NOT_FOUND));

        ServiceResult<Void> result = service.activateUser(userToEdit.getId());

        assertNull(result.getSuccess());
    }

    @Test
    public void activateUserNoUser() {

        setUpUsersForEditInternalUserSuccess();

        when(userRepositoryMock.findOne(userToEdit.getId())).thenReturn(null);

        ServiceResult<Void> result = service.activateUser(userToEdit.getId());

        assertTrue(result.isFailure());
    }

    @Test
    public void testCreateCompAdminOrganisationUser() {

        Role roleResource = Role.COMP_ADMIN;
        UserResource userToCreate = newUserResource().
                withFirstName("First").
                withLastName("Last").
                withEmail("email@example.com").
                withPhoneNumber("01234 567890").
                withPassword("thepassword").
                withTitle(Mr).
                withDisability(Disability.YES).
                withGender(Gender.MALE).
                withEthnicity(2L).
                withRolesGlobal(singletonList(roleResource)).
                build();

        Organisation selectedOrganisation = newOrganisation().withId(123L).build();

        when(ethnicityMapperMock.mapIdToDomain(2L)).thenReturn(newEthnicity().withId(2L).build());
        when(organisationRepositoryMock.findOne(123L)).thenReturn(selectedOrganisation);
        when(organisationRepositoryMock.findByUsersId(anyLong())).thenReturn(singletonList(selectedOrganisation));
        when(idpServiceMock.createUserRecordWithUid("email@example.com", "thepassword")).thenReturn(serviceSuccess("new-uid"));

        Profile expectedProfile = newProfile().withId(7L).build();
        when(profileRepositoryMock.save(any(Profile.class))).thenReturn(expectedProfile);

        User expectedCreatedUser = createLambdaMatcher(user -> {

            assertNull(user.getId());
            assertEquals("First Last", user.getName());
            assertEquals("First", user.getFirstName());
            assertEquals("Last", user.getLastName());

            assertEquals("email@example.com", user.getEmail());
            assertEquals("01234 567890", user.getPhoneNumber());
            assertEquals(Mr, user.getTitle());
            assertEquals("new-uid", user.getUid());
            assertEquals(Gender.MALE, user.getGender());
            assertEquals(Disability.YES, user.getDisability());
            assertEquals(Long.valueOf(2), user.getEthnicity().getId());
            assertEquals(1, user.getRoles().size());
            assertTrue(user.getRoles().contains(Role.COMP_ADMIN));
            List<Organisation> orgs = organisationRepositoryMock.findByUsersId(user.getId());
            assertEquals(1, orgs.size());
            assertEquals(selectedOrganisation, orgs.get(0));
            assertEquals(expectedProfile.getId(), user.getProfileId());

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
        when(userMapperMock.mapToResource(isA(User.class))).thenReturn(userToCreate);
        when(passwordPolicyValidatorMock.validatePassword("thepassword", userToCreate)).thenReturn(serviceSuccess());

        UserResource result = service.createOrganisationUser(123L, userToCreate).getSuccess();

        assertEquals(userToCreate, result);
    }

    private void setUpUsersForEditInternalUserSuccess() {

        userToEdit = UserResourceBuilder.newUserResource()
                .withFirstName("Johnathan")
                .withLastName("Dow")
                .build();

        userResourceInDB = UserResourceBuilder.newUserResource()
                .withFirstName("John")
                .withLastName("Doe")
                .build();

        userInDB = UserBuilder.newUser()
                .withFirstName("John")
                .withLastName("Doe")
                .withCreatedOn(ZonedDateTime.now().minusHours(2))
                .withModifiedOn(ZonedDateTime.now().minusHours(2))
                .build();

        updatedUserInDB = UserBuilder.newUser()
                .withFirstName("John")
                .withLastName("Doe")
                .withCreatedOn(ZonedDateTime.now())
                .withModifiedOn(ZonedDateTime.now())
                .build();
    }
}
