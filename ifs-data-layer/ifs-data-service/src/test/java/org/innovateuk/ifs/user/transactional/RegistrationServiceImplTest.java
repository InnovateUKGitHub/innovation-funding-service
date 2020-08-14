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
import org.innovateuk.ifs.competition.repository.CompetitionFinanceInviteRepository;
import org.innovateuk.ifs.competition.repository.StakeholderInviteRepository;
import org.innovateuk.ifs.competition.repository.StakeholderRepository;
import org.innovateuk.ifs.competition.resource.SiteTermsAndConditionsResource;
import org.innovateuk.ifs.competition.transactional.TermsAndConditionsService;
import org.innovateuk.ifs.invite.constant.InviteStatus;
import org.innovateuk.ifs.invite.domain.RoleInvite;
import org.innovateuk.ifs.invite.repository.RoleInviteRepository;
import org.innovateuk.ifs.invite.resource.MonitoringOfficerCreateResource;
import org.innovateuk.ifs.organisation.repository.OrganisationRepository;
import org.innovateuk.ifs.profile.domain.Profile;
import org.innovateuk.ifs.profile.repository.ProfileRepository;
import org.innovateuk.ifs.project.monitoring.repository.MonitoringOfficerInviteRepository;
import org.innovateuk.ifs.token.repository.TokenRepository;
import org.innovateuk.ifs.user.builder.UserBuilder;
import org.innovateuk.ifs.user.builder.UserResourceBuilder;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.mapper.UserMapper;
import org.innovateuk.ifs.user.repository.UserRepository;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.UserCreationResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.resource.UserStatus;
import org.junit.Test;
import org.mockito.Mock;

import java.time.ZonedDateTime;
import java.util.Optional;

import static java.util.Collections.singleton;
import static org.innovateuk.ifs.LambdaMatcher.createLambdaMatcher;
import static org.innovateuk.ifs.address.builder.AddressBuilder.newAddress;
import static org.innovateuk.ifs.address.builder.AddressResourceBuilder.newAddressResource;
import static org.innovateuk.ifs.commons.error.CommonErrors.badRequestError;
import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.GENERAL_NOT_FOUND;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.NOT_AN_INTERNAL_USER_ROLE;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.competition.builder.SiteTermsAndConditionsResourceBuilder.newSiteTermsAndConditionsResource;
import static org.innovateuk.ifs.invite.builder.RoleInviteBuilder.newRoleInvite;
import static org.innovateuk.ifs.profile.builder.ProfileBuilder.newProfile;
import static org.innovateuk.ifs.user.builder.UserBuilder.newUser;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.user.resource.UserCreationResource.UserCreationResourceBuilder.anUserCreationResource;
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

    @Mock
    private CompetitionFinanceInviteRepository competitionFinanceInviteRepository;


    @Test
    public void createUser() {
        AddressResource addressResource = newAddressResource().withAddressLine1("Electric Works").withTown("Sheffield").withPostcode("S1 2BJ").build();
        Address address = newAddress().withAddressLine1("Electric Works").withTown("Sheffield").withPostcode("S1 2BJ").build();

        UserCreationResource userToCreateResource = anUserCreationResource()
                .withFirstName("First")
                .withLastName("Last")
                .withPhoneNumber("01234 567890")
                .withEmail("email@example.com")
                .withPassword("Passw0rd123")
                .withAddress(addressResource)
                .withRole(Role.ASSESSOR)
                .build();

        Long profileId = 1L;
        Profile userProfile = newProfile()
                .withId(profileId)
                .withAddress(address)
                .build();

        User userToCreate = newUser()
                .withId((Long) null)
                .withFirstName("First")
                .withLastName("Last")
                .withPhoneNumber("01234 567890")
                .withEmailAddress("email@example.com")
                .withRoles(singleton(Role.ASSESSOR))
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
            assertEquals("First Last", user.getName());
            assertEquals("First", user.getFirstName());
            assertEquals("Last", user.getLastName());
            assertEquals("01234 567890", user.getPhoneNumber());
            assertEquals("email@example.com", user.getEmail());

            assertEquals("new-uid", user.getUid());
            assertEquals(1, user.getRoles().size());
            assertEquals(userToCreate.getRoles(), user.getRoles());

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
        when(userRepositoryMock.save(savedUser)).thenReturn(savedUser);
        when(userMapperMock.mapToResource(savedUser)).thenReturn(savedUserResource);

        UserResource result = service.createUser(userToCreateResource).getSuccess();

        assertEquals(savedUserResource, result);
    }

    @Test
    public void createUser_applicantUserButIdpCallFails() {

        UserCreationResource userToCreateResource = anUserCreationResource()
                .withFirstName("First")
                .withLastName("Last")
                .withPhoneNumber("01234 567890")
                .withEmail("email@example.com")
                .withPassword("thepassword")
                .withRole(Role.APPLICANT)
                .build();
        UserResource user = new UserResource();

        SiteTermsAndConditionsResource siteTermsAndConditions = newSiteTermsAndConditionsResource().build();

        when(termsAndConditionsServiceMock.getLatestSiteTermsAndConditions()).thenReturn(serviceSuccess(siteTermsAndConditions));
        when(idpServiceMock.createUserRecordWithUid("email@example.com", "thepassword")).thenReturn(serviceFailure(new Error(RestIdentityProviderService.ServiceFailures.UNABLE_TO_CREATE_USER, INTERNAL_SERVER_ERROR)));
        when(userMapperMock.mapToResource(isA(User.class))).thenReturn(user);
        when(passwordPolicyValidatorMock.validatePassword(eq("thepassword"), any(UserResource.class))).thenReturn(serviceSuccess());

        ServiceResult<UserResource> result = service.createUser(userToCreateResource);
        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(new Error(RestIdentityProviderService.ServiceFailures.UNABLE_TO_CREATE_USER, INTERNAL_SERVER_ERROR)));
    }

    @Test
    public void createUser_applicantUserButPasswordValidationFails() {

        UserCreationResource userToCreateResource = anUserCreationResource()
                .withFirstName("First")
                .withLastName("Last")
                .withPhoneNumber("01234 567890")
                .withEmail("email@example.com")
                .withPassword("thepassword")
                .withRole(Role.APPLICANT)
                .build();
        UserResource user = new UserResource();

        when(idpServiceMock.createUserRecordWithUid("email@example.com", "thepassword")).thenReturn(serviceFailure(new Error(RestIdentityProviderService.ServiceFailures.UNABLE_TO_CREATE_USER, INTERNAL_SERVER_ERROR)));
        when(userMapperMock.mapToResource(isA(User.class))).thenReturn(user);
        when(passwordPolicyValidatorMock.validatePassword(eq("thepassword"), any(UserResource.class))).thenReturn(serviceFailure(badRequestError("bad password")));

        ServiceResult<UserResource> result = service.createUser(userToCreateResource);
        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(Error.fieldError("password", null, "bad password")));
    }

    @Test
    public void createInternalUser() {
        UserCreationResource userToCreateResource = anUserCreationResource()
                .withFirstName("First")
                .withLastName("Last")
                .withPhoneNumber("01234 567890")
                .withPassword("thepassword")
                .withInviteHash("SomeInviteHash")
                .build();
        RoleInvite roleInvite = newRoleInvite()
                .withRole(Role.PROJECT_FINANCE)
                .withEmail("email@example.com")
                .build();

        User userToCreate = newUser()
                .withId((Long) null)
                .withFirstName("First")
                .withLastName("Last")
                .withEmailAddress("email@example.com")
                .withUid("new-uid")
                .withRoles(singleton(Role.PROJECT_FINANCE))
                .build();

        when(roleInviteRepositoryMock.getByHash("SomeInviteHash")).thenReturn(roleInvite);
        when(passwordPolicyValidatorMock.validatePassword(anyString(), any(UserResource.class))).thenReturn(serviceSuccess());
        when(idpServiceMock.createUserRecordWithUid("email@example.com", "thepassword")).thenReturn(serviceSuccess("new-uid"));
        when(profileRepositoryMock.save(any(Profile.class))).thenReturn(newProfile().build());
        when(userMapperMock.mapToDomain(any(UserResource.class))).thenReturn(userToCreate);
        when(idpServiceMock.activateUser("new-uid")).thenReturn(serviceSuccess("new-uid"));
        when(userRepositoryMock.save(any(User.class))).thenReturn(userToCreate);
        ServiceResult<UserResource> result = service.createUser(userToCreateResource);
        assertTrue(result.isSuccess());
        assertEquals(InviteStatus.OPENED, roleInvite.getStatus());
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
    public void createPendingMonitoringOfficer() {
        UserCreationResource userToCreateResource = anUserCreationResource()
                .withFirstName("First")
                .withLastName("Last")
                .withPhoneNumber("01234 567890")
                .withEmail("email@example.com")
                .withPassword("thepassword")
                .withRole(Role.MONITORING_OFFICER)
                .build();

        User user = newUser().withEmailAddress("test@test.test").build();
        MonitoringOfficerCreateResource resource = new MonitoringOfficerCreateResource(
                "Steve", "Smith", "011432333333", "test@test.test");
        String password = "superSecurePassword";
        when(idpServiceMock.createUserRecordWithUid(anyString(), anyString())).thenReturn(serviceSuccess("uid"));
        when(profileRepositoryMock.save(any(Profile.class))).thenReturn(newProfile().build());
        when(userRepositoryMock.save(any(User.class))).thenReturn(user);

        service.createUser(userToCreateResource).getSuccess();

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