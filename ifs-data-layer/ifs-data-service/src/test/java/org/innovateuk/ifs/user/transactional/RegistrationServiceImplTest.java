package org.innovateuk.ifs.user.transactional;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.address.domain.Address;
import org.innovateuk.ifs.address.mapper.AddressMapper;
import org.innovateuk.ifs.address.resource.AddressResource;
import org.innovateuk.ifs.authentication.service.IdentityProviderService;
import org.innovateuk.ifs.authentication.service.RestIdentityProviderService;
import org.innovateuk.ifs.authentication.validator.PasswordPolicyValidator;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.domain.Stakeholder;
import org.innovateuk.ifs.competition.domain.StakeholderInvite;
import org.innovateuk.ifs.competition.repository.StakeholderInviteRepository;
import org.innovateuk.ifs.competition.repository.StakeholderRepository;
import org.innovateuk.ifs.competition.resource.SiteTermsAndConditionsResource;
import org.innovateuk.ifs.competition.transactional.TermsAndConditionsService;
import org.innovateuk.ifs.invite.constant.InviteStatus;
import org.innovateuk.ifs.invite.domain.RoleInvite;
import org.innovateuk.ifs.invite.repository.RoleInviteRepository;
import org.innovateuk.ifs.invite.resource.MonitoringOfficerCreateResource;
import org.innovateuk.ifs.organisation.domain.Organisation;
import org.innovateuk.ifs.organisation.repository.OrganisationRepository;
import org.innovateuk.ifs.profile.domain.Profile;
import org.innovateuk.ifs.profile.repository.ProfileRepository;
import org.innovateuk.ifs.project.monitoring.domain.MonitoringOfficerInvite;
import org.innovateuk.ifs.project.monitoring.repository.MonitoringOfficerInviteRepository;
import org.innovateuk.ifs.registration.resource.InternalUserRegistrationResource;
import org.innovateuk.ifs.registration.resource.MonitoringOfficerRegistrationResource;
import org.innovateuk.ifs.registration.resource.StakeholderRegistrationResource;
import org.innovateuk.ifs.registration.resource.UserRegistrationResource;
import org.innovateuk.ifs.token.domain.Token;
import org.innovateuk.ifs.token.repository.TokenRepository;
import org.innovateuk.ifs.token.resource.TokenType;
import org.innovateuk.ifs.user.builder.UserBuilder;
import org.innovateuk.ifs.user.builder.UserResourceBuilder;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.mapper.UserMapper;
import org.innovateuk.ifs.user.repository.UserRepository;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.resource.UserStatus;
import org.junit.Test;
import org.mockito.Mock;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;

import static java.util.Collections.singleton;
import static java.util.Collections.singletonList;
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
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.innovateuk.ifs.competition.builder.SiteTermsAndConditionsResourceBuilder.newSiteTermsAndConditionsResource;
import static org.innovateuk.ifs.competition.builder.StakeholderInviteBuilder.newStakeholderInvite;
import static org.innovateuk.ifs.invite.builder.RoleInviteBuilder.newRoleInvite;
import static org.innovateuk.ifs.organisation.builder.OrganisationBuilder.newOrganisation;
import static org.innovateuk.ifs.profile.builder.ProfileBuilder.newProfile;
import static org.innovateuk.ifs.registration.builder.InternalUserRegistrationResourceBuilder.newInternalUserRegistrationResource;
import static org.innovateuk.ifs.registration.builder.UserRegistrationResourceBuilder.newUserRegistrationResource;
import static org.innovateuk.ifs.stakeholder.builder.StakeholderRegistrationResourceBuilder.newStakeholderRegistrationResource;
import static org.innovateuk.ifs.user.builder.UserBuilder.newUser;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.user.resource.Title.Mr;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

/**
 * Tests around Registration Service
 */
public class RegistrationServiceImplTest extends BaseServiceUnitTest<RegistrationServiceImpl> {

    private UserResource userToEdit;

    private UserResource userResourceInDB;

    private User userInDB;

    private User updatedUserInDB;

    @Mock
    private TermsAndConditionsService termsAndConditionsServiceMock;

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
    private UserRepository userRepositoryMock;

    @Mock
    private TokenRepository tokenRepositoryMock;

    @Mock
    private OrganisationRepository organisationRepositoryMock;

    @Mock
    private BaseUserService baseUserServiceMock;

    @Mock
    private RoleInviteRepository roleInviteRepositoryMock;

    @Mock
    private RegistrationNotificationService registrationEmailServiceMock;

    @Mock
    private StakeholderInviteRepository stakeholderInviteRepository;

    @Mock
    private StakeholderRepository stakeholderRepository;

    @Mock
    private MonitoringOfficerInviteRepository monitoringOfficerInviteRepositoryMock;


    @Test
    public void createUser() {
        Set<Role> roles = singleton(Role.ASSESSOR);
        AddressResource addressResource = newAddressResource().withAddressLine1("Electric Works").withTown("Sheffield").withPostcode("S1 2BJ").build();
        Address address = newAddress().withAddressLine1("Electric Works").withTown("Sheffield").withPostcode("S1 2BJ").build();

        UserRegistrationResource userToCreateResource = newUserRegistrationResource()
                .withTitle(Mr)
                .withFirstName("First")
                .withLastName("Last")
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
                .withPhoneNumber("01234 567890")
                .withEmailAddress("email@example.com")
                .withRoles(roles)
                .withProfileId(userProfile.getId())
                .build();

        when(profileRepositoryMock.findById(userToCreate.getProfileId())).thenReturn(Optional.of(userProfile));
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
            assertEquals("01234 567890", user.getPhoneNumber());
            assertEquals("email@example.com", user.getEmail());

            assertEquals("new-uid", user.getUid());
            assertEquals(1, user.getRoles().size());
            assertEquals(roles, user.getRoles());

            assertNotNull(user.getProfileId());
            Profile profile = profileRepositoryMock.findById(user.getProfileId()).get();
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
    public void createUser_organisation() {

        UserResourceBuilder userBuilder = newUserResource().
                withFirstName("First").
                withLastName("Last").
                withEmail("email@example.com").
                withPhoneNumber("01234 567890").
                withPassword("thepassword").
                withTitle(Mr);

        UserResource userToCreate = userBuilder.build();

        SiteTermsAndConditionsResource siteTermsAndConditions = newSiteTermsAndConditionsResource().build();
        Organisation selectedOrganisation = newOrganisation().withId(123L).build();


        when(termsAndConditionsServiceMock.getLatestSiteTermsAndConditions()).thenReturn(serviceSuccess(siteTermsAndConditions));
        when(organisationRepositoryMock.findById(123L)).thenReturn(Optional.of(selectedOrganisation));
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
            assertEquals(1, user.getRoles().size());
            assertTrue(user.getRoles().contains(Role.APPLICANT));
            assertEquals(expectedProfile.getId(), user.getProfileId());
            assertEquals(new LinkedHashSet<>(singletonList(siteTermsAndConditions.getId())), user.getTermsAndConditionsIds());

            return true;
        });

        User savedUser = newUser().with(id(999L)).build();

        UserResource savedUserResource = userBuilder.withId(savedUser.getId()).build();

        when(userRepositoryMock.save(expectedCreatedUser)).thenReturn(savedUser);

        when(userMapperMock.mapToResource(savedUser)).thenReturn(savedUserResource);
        when(passwordPolicyValidatorMock.validatePassword("thepassword", userToCreate)).thenReturn(serviceSuccess());
        when(registrationEmailServiceMock.sendUserVerificationEmail(savedUserResource, Optional.empty(), Optional.empty())).thenReturn(serviceSuccess());

        UserResource result = service.createUser(userToCreate).getSuccess();
        assertEquals(savedUserResource, result);

        verify(userMapperMock).mapToResource(savedUser);
        verify(passwordPolicyValidatorMock).validatePassword("thepassword", userToCreate);
        verify(registrationEmailServiceMock).sendUserVerificationEmail(savedUserResource, Optional.empty(), Optional.empty());
    }

    @Test
    public void createUser_applicantUserButIdpCallFails() {

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
        when(idpServiceMock.createUserRecordWithUid("email@example.com", "thepassword")).thenReturn(serviceFailure(new Error(RestIdentityProviderService.ServiceFailures.UNABLE_TO_CREATE_USER, INTERNAL_SERVER_ERROR)));
        when(userMapperMock.mapToResource(isA(User.class))).thenReturn(userToCreate);
        when(passwordPolicyValidatorMock.validatePassword("thepassword", userToCreate)).thenReturn(serviceSuccess());

        ServiceResult<UserResource> result = service.createUser(userToCreate);
        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(new Error(RestIdentityProviderService.ServiceFailures.UNABLE_TO_CREATE_USER, INTERNAL_SERVER_ERROR)));
    }

    @Test
    public void createUser_applicantUserButPasswordValidationFails() {

        UserResource userToCreate = newUserResource().withPassword("thepassword").build();
        Organisation selectedOrganisation = newOrganisation().build();

        when(organisationRepositoryMock.findById(123L)).thenReturn(Optional.of(selectedOrganisation));
        when(idpServiceMock.createUserRecordWithUid("email@example.com", "thepassword")).thenReturn(serviceFailure(new Error(RestIdentityProviderService.ServiceFailures.UNABLE_TO_CREATE_USER, INTERNAL_SERVER_ERROR)));
        when(userMapperMock.mapToResource(isA(User.class))).thenReturn(userToCreate);
        when(passwordPolicyValidatorMock.validatePassword("thepassword", userToCreate)).thenReturn(serviceFailure(badRequestError("bad password")));

        ServiceResult<UserResource> result = service.createUser(userToCreate);
        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(Error.fieldError("password", null, "bad password")));
    }

    @Test
    public void createInternalUser() {
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
    public void createStakeholderUser() {
        StakeholderRegistrationResource stakeholderRegistrationResource = newStakeholderRegistrationResource()
                .withFirstName("First")
                .withLastName("Last")
                .withPassword("Passw0rd")
                .build();

        User userToCreate = newUser()
                .withId((Long) null)
                .withFirstName("First")
                .withLastName("Last")
                .withEmailAddress("test@test.test")
                .withRoles(singleton(Role.STAKEHOLDER))
                .build();

        StakeholderInvite invite = newStakeholderInvite().build();
        Stakeholder stakeholder = new Stakeholder(newCompetition().build(), newUser().build());

        when(stakeholderInviteRepository.getByHash("hash1234")).thenReturn(invite);
        when(passwordPolicyValidatorMock.validatePassword(anyString(), any(UserResource.class))).thenReturn(serviceSuccess());
        when(idpServiceMock.createUserRecordWithUid("test@test.test", "Passw0rd")).thenReturn(serviceSuccess("new-uid"));
        when(profileRepositoryMock.save(any(Profile.class))).thenReturn(newProfile().build());
        when(userMapperMock.mapToDomain(any(UserResource.class))).thenReturn(userToCreate);
        when(idpServiceMock.activateUser("new-uid")).thenReturn(serviceSuccess("new-uid"));
        when(userRepositoryMock.save(any(User.class))).thenReturn(userToCreate);
        when(stakeholderRepository.save(any(Stakeholder.class))).thenReturn(stakeholder);

        ServiceResult<Void> result = service.createStakeholder("hash1234", stakeholderRegistrationResource);

        assertTrue(result.isSuccess());
        assertEquals(InviteStatus.OPENED, invite.getStatus());
    }

    @Test
    public void editInternalUser_whenNewRoleIsNotInternalRole() {

        UserResource userToEdit = UserResourceBuilder.newUserResource().build();

        ServiceResult<UserResource> result = service.editInternalUser(userToEdit, Role.COLLABORATOR);

        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(NOT_AN_INTERNAL_USER_ROLE));
    }

    @Test
    public void editInternalUser_whenUserDoesNotExist() {

        UserResource userToEdit = UserResourceBuilder.newUserResource().build();

        when(baseUserServiceMock.getUserById(userToEdit.getId())).thenReturn(serviceFailure(notFoundError(User.class, userToEdit.getId())));

        ServiceResult<UserResource> result = service.editInternalUser(userToEdit, Role.SUPPORT);

        assertTrue(result.isFailure());
        assertEquals(GENERAL_NOT_FOUND.getErrorKey(), result.getErrors().get(0).getErrorKey());
    }

    @Test
    public void editInternalUser_success() {

        setUpUsersForEditInternalUserSuccess();

        Role newRole = Role.SUPPORT;

        when(userRepositoryMock.findById(userToEdit.getId())).thenReturn(Optional.of(userInDB));
        when(userMapperMock.mapToDomain(userResourceInDB)).thenReturn(userInDB);

        ServiceResult<UserResource> result = service.editInternalUser(userToEdit, newRole);

        assertTrue(result.isSuccess());
        verify(userRepositoryMock).save(userInDB);
        assertTrue(userInDB.getRoles().stream().anyMatch(role1 -> role1.equals(Role.SUPPORT)));
        assertEquals(userInDB.getFirstName(), userToEdit.getFirstName());
        assertEquals(userInDB.getLastName(), userToEdit.getLastName());
    }

    @Test
    public void deactivateUser_success() {

        setUpUsersForEditInternalUserSuccess();

        when(userRepositoryMock.findById(userToEdit.getId())).thenReturn(Optional.of(userInDB));
        when(idpServiceMock.deactivateUser(userToEdit.getUid())).thenReturn(ServiceResult.serviceSuccess(""));
        userInDB.setStatus(UserStatus.INACTIVE);
        when(userRepositoryMock.save(userInDB)).thenReturn(userInDB);

        ServiceResult<UserResource> result = service.deactivateUser(userToEdit.getId());

        verify(userRepositoryMock).save(userInDB);

        assertTrue(result.isSuccess());
    }

    @Test
    public void deactivateUser_idpFails() {

        setUpUsersForEditInternalUserSuccess();

        when(userRepositoryMock.findById(userToEdit.getId())).thenReturn(Optional.of(userInDB));
        when(idpServiceMock.deactivateUser(userToEdit.getUid())).thenReturn(ServiceResult.serviceFailure(GENERAL_NOT_FOUND));

        ServiceResult<UserResource> result = service.deactivateUser(userToEdit.getId());

        assertTrue(result.isFailure());
    }

    @Test
    public void deactivateUser_noUser() {

        setUpUsersForEditInternalUserSuccess();

        when(userRepositoryMock.findById(userToEdit.getId())).thenReturn(Optional.empty());

        ServiceResult<UserResource> result = service.deactivateUser(userToEdit.getId());

        assertTrue(result.isFailure());
    }

    @Test
    public void activateUser_success() {

        setUpUsersForEditInternalUserSuccess();

        when(userRepositoryMock.findById(userToEdit.getId())).thenReturn(Optional.of(userInDB));
        when(idpServiceMock.activateUser(userToEdit.getUid())).thenReturn(ServiceResult.serviceSuccess(""));
        userInDB.setStatus(UserStatus.ACTIVE);
        when(userRepositoryMock.save(userInDB)).thenReturn(updatedUserInDB);

        ServiceResult<UserResource> result = service.activateUser(userToEdit.getId());

        verify(userRepositoryMock).save(userInDB);

        assertTrue(result.isSuccess());
    }

    @Test
    public void activateUser_idpFails() {

        setUpUsersForEditInternalUserSuccess();

        when(userRepositoryMock.findById(userToEdit.getId())).thenReturn(Optional.of(userInDB));
        when(idpServiceMock.activateUser(userToEdit.getUid())).thenReturn(ServiceResult.serviceFailure(GENERAL_NOT_FOUND));

        ServiceResult<UserResource> result = service.activateUser(userToEdit.getId());

        assertTrue(result.isFailure());
    }

    @Test
    public void activateUser_noUser() {

        setUpUsersForEditInternalUserSuccess();

        when(userRepositoryMock.findById(userToEdit.getId())).thenReturn(Optional.empty());

        ServiceResult<UserResource> result = service.activateUser(userToEdit.getId());

        assertTrue(result.isFailure());
    }

    @Test
    public void createUser_compAdminOrganisationUser() {

        Role roleResource = Role.COMP_ADMIN;
        UserResource userToCreate = newUserResource().
                withFirstName("First").
                withLastName("Last").
                withEmail("email@example.com").
                withPhoneNumber("01234 567890").
                withPassword("thepassword").
                withTitle(Mr).
                withRolesGlobal(singletonList(roleResource)).
                build();

        Organisation selectedOrganisation = newOrganisation().withId(123L).build();

        when(organisationRepositoryMock.findById(123L)).thenReturn(Optional.of(selectedOrganisation));
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
            assertEquals(1, user.getRoles().size());
            assertTrue(user.getRoles().contains(Role.COMP_ADMIN));
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
        when(registrationEmailServiceMock.sendUserVerificationEmail(userToCreate, Optional.empty(), Optional.empty())).thenReturn(serviceSuccess());

        UserResource result = service.createUser(userToCreate).getSuccess();

        assertEquals(userToCreate, result);

        verify(userMapperMock).mapToResource(savedUser);
        verify(passwordPolicyValidatorMock).validatePassword("thepassword", userToCreate);
        verify(registrationEmailServiceMock).sendUserVerificationEmail(userToCreate, Optional.empty(), Optional.empty());
    }

    @Test
    public void createMonitoringOfficer() {
        String email = "test@test.test";
        String hash = "hash";
        String password = "password";
        String uid = "uid";

        MonitoringOfficerRegistrationResource registrationResource =
                new MonitoringOfficerRegistrationResource("first", "last", "phone", password);

        User userToCreate = newUser()
                .withId((Long) null)
                .withFirstName(registrationResource.getFirstName())
                .withLastName(registrationResource.getLastName())
                .withPhoneNumber(registrationResource.getPhoneNumber())
                .withEmailAddress(email)
                .withRoles(singleton(Role.MONITORING_OFFICER))
                .withUid(uid)
                .build();

        MonitoringOfficerInvite invite = new MonitoringOfficerInvite("name", email, hash, InviteStatus.OPENED );

        when(monitoringOfficerInviteRepositoryMock.getByHash(hash)).thenReturn(invite);
        when(passwordPolicyValidatorMock.validatePassword(anyString(), any(UserResource.class))).thenReturn(serviceSuccess());
        when(idpServiceMock.createUserRecordWithUid(email, password)).thenReturn(serviceSuccess(uid));
        when(profileRepositoryMock.save(any(Profile.class))).thenReturn(newProfile().build());
        when(userMapperMock.mapToDomain(any(UserResource.class))).thenReturn(userToCreate);
        when(idpServiceMock.activateUser(uid)).thenReturn(serviceSuccess(uid));
        when(userRepositoryMock.save(any(User.class))).thenReturn(userToCreate);

        service.createMonitoringOfficer(hash, registrationResource).getSuccess();
    }

    @Test
    public void createPendingMonitoringOfficer() {
        User user = newUser().withEmailAddress("test@test.test").build();
        MonitoringOfficerCreateResource resource = new MonitoringOfficerCreateResource(
                "Steve", "Smith", "011432333333", "test@test.test");
        String password = "superSecurePassword";
        when(idpServiceMock.createUserRecordWithUid(anyString(), anyString())).thenReturn(serviceSuccess("uid"));
        when(profileRepositoryMock.save(any(Profile.class))).thenReturn(newProfile().build());
        when(userRepositoryMock.save(any(User.class))).thenReturn(user);

        service.createPendingMonitoringOfficer(resource).getSuccess();

        verify(idpServiceMock).createUserRecordWithUid(anyString(), anyString());
        verify(profileRepositoryMock).save(any(Profile.class));
        verify(userRepositoryMock, times(2)).save(any(User.class));
    }

    @Test
    public void activatePendingUser() {
        User user = newUser().withUid("uid").build();

        when(monitoringOfficerInviteRepositoryMock.existsByHash("hash")).thenReturn(true);
        when(idpServiceMock.activateUser(anyString())).thenReturn(serviceSuccess("uid"));
        when(userRepositoryMock.save(any(User.class))).thenReturn(user);
        when(idpServiceMock.updateUserPassword("uid", "password")).thenReturn(serviceSuccess("uid"));

        service.activatePendingUser(user, "password", "hash").getSuccess();

        verify(monitoringOfficerInviteRepositoryMock).existsByHash("hash");
        verify(idpServiceMock).activateUser(anyString());
        verify(userRepositoryMock).save(any(User.class));
        verify(idpServiceMock).updateUserPassword("uid", "password");
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

    @Override
    protected RegistrationServiceImpl supplyServiceUnderTest() {
        return new RegistrationServiceImpl();
    }
}