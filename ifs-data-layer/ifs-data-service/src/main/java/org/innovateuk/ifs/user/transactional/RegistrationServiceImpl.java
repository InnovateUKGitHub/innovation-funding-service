package org.innovateuk.ifs.user.transactional;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.innovateuk.ifs.address.mapper.AddressMapper;
import org.innovateuk.ifs.address.resource.AddressResource;
import org.innovateuk.ifs.authentication.service.IdentityProviderService;
import org.innovateuk.ifs.authentication.validator.PasswordPolicyValidator;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.transactional.TermsAndConditionsService;
import org.innovateuk.ifs.invite.domain.RoleInvite;
import org.innovateuk.ifs.invite.repository.RoleInviteRepository;
import org.innovateuk.ifs.notifications.resource.Notification;
import org.innovateuk.ifs.notifications.resource.NotificationTarget;
import org.innovateuk.ifs.notifications.resource.SystemNotificationSource;
import org.innovateuk.ifs.notifications.resource.UserNotificationTarget;
import org.innovateuk.ifs.notifications.service.NotificationService;
import org.innovateuk.ifs.profile.domain.Profile;
import org.innovateuk.ifs.profile.repository.ProfileRepository;
import org.innovateuk.ifs.registration.resource.InternalUserRegistrationResource;
import org.innovateuk.ifs.registration.resource.UserRegistrationResource;
import org.innovateuk.ifs.token.domain.Token;
import org.innovateuk.ifs.token.repository.TokenRepository;
import org.innovateuk.ifs.token.resource.TokenType;
import org.innovateuk.ifs.transactional.BaseTransactionalService;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.mapper.EthnicityMapper;
import org.innovateuk.ifs.user.mapper.UserMapper;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.resource.UserStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.method.P;
import org.springframework.security.crypto.password.StandardPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static java.lang.String.format;
import static java.time.ZonedDateTime.now;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.NOT_AN_INTERNAL_USER_ROLE;
import static org.innovateuk.ifs.commons.error.Error.fieldError;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.notifications.resource.NotificationMedium.EMAIL;
import static org.innovateuk.ifs.user.resource.Role.APPLICANT;
import static org.innovateuk.ifs.user.resource.Role.IFS_ADMINISTRATOR;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;
import static org.innovateuk.ifs.util.MapFunctions.asMap;

/**
 * A service around Registration and general user-creation operations
 */
@Service
public class RegistrationServiceImpl extends BaseTransactionalService implements RegistrationService {

    final JsonNodeFactory factory = JsonNodeFactory.instance;

    private StandardPasswordEncoder encoder = new StandardPasswordEncoder(UUID.randomUUID().toString());

    public enum ServiceFailures {
        UNABLE_TO_CREATE_USER
    }

    enum Notifications {
        VERIFY_EMAIL_ADDRESS
    }

    @Autowired
    private IdentityProviderService idpService;

    @Autowired
    private TokenRepository tokenRepository;

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private SystemNotificationSource systemNotificationSource;

    @Autowired
    private BaseUserService baseUserService;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private AddressMapper addressMapper;

    @Autowired
    private PasswordPolicyValidator passwordPolicyValidator;

    @Autowired
    private EthnicityMapper ethnicityMapper;

    @Autowired
    private UserSurveyService userSurveyService;

    @Autowired
    private RoleInviteRepository roleInviteRepository;

    @Autowired
    private TermsAndConditionsService termsAndConditionsService;

    @Value("${ifs.web.baseURL}")
    private String webBaseUrl;

    @Override
    @Transactional
    public ServiceResult<UserResource> createUser(@P("user") UserRegistrationResource userRegistrationResource) {
        final UserResource userResource = userRegistrationResource.toUserResource();

        return validateUser(userResource).
                andOnSuccess(validUser -> {
                    final User user = userMapper.mapToDomain(userResource);
                    return createUserWithUid(user, userResource.getPassword(), userRegistrationResource.getAddress());
                });
    }

    @Override
    @Transactional
    public ServiceResult<UserResource> createOrganisationUser(long organisationId, UserResource userResource) {
        User newUser = assembleUserFromResource(userResource);
        return validateUserWithOrganisation(userResource, organisationId).
                andOnSuccess(
                        () -> addUserToOrganisation(newUser, organisationId)).
                andOnSuccess(
                        user -> userResource.getRoles().isEmpty() ? addRoleToUser(user, APPLICANT) : serviceSuccess(user)).
                andOnSuccess(
                        userWithRole -> userWithRole.hasRole(Role.APPLICANT) ?
                                agreeLatestSiteTermsAndConditionsForUser(userWithRole) : serviceSuccess(userWithRole)).
                andOnSuccess(
                        () -> createUserWithUid(newUser, userResource.getPassword(), null)
                );
    }

    private ServiceResult<UserResource> validateUser(UserResource userResource) {
        return passwordPolicyValidator.validatePassword(userResource.getPassword(), userResource)
                .handleSuccessOrFailure(
                        failure -> serviceFailure(
                                simpleMap(
                                        failure.getErrors(),
                                        error -> fieldError("password", error.getFieldRejectedValue(), error.getErrorKey())
                                )
                        ),
                        success -> serviceSuccess(userResource)
                );
    }

    private ServiceResult<UserResource> validateUserWithOrganisation(UserResource userResource, Long organisationId) {
        return passwordPolicyValidator.validatePassword(userResource.getPassword(), userResource, organisationId)
                .handleSuccessOrFailure(
                        failure -> serviceFailure(
                                simpleMap(
                                        failure.getErrors(),
                                        error -> fieldError("password", error.getFieldRejectedValue(), error.getErrorKey())
                                )
                        ),
                        success -> serviceSuccess(userResource)
                );
    }

    @Override
    @Transactional
    public ServiceResult<Void> activateUser(long userId) {
        return getUser(userId).andOnSuccessReturnVoid(this::activateUser);
    }

    private ServiceResult<User> activateUser(User user) {
        return idpService
                .activateUser(user.getUid())
                .andOnSuccessReturn(() -> {
                    user.setStatus(UserStatus.ACTIVE);
                    return userRepository.save(user);
                });
    }

    @Override
    @Transactional
    public ServiceResult<Void> deactivateUser(long userId) {
        return getUser(userId).andOnSuccessReturnVoid(this::deactivateUser);
    }

    private ServiceResult<User> deactivateUser(User user) {
        return idpService
                .deactivateUser(user.getUid())
                .andOnSuccessReturn(() -> {
                    user.setStatus(UserStatus.INACTIVE);
                    return userRepository.save(user);
                });
    }

    @Override
    @Transactional
    public ServiceResult<Void> activateApplicantAndSendDiversitySurvey(long userId) {
        return getUser(userId)
                .andOnSuccess(this::activateUser)
                .andOnSuccessReturnVoid(this::sendApplicantDiversitySurvey);
    }

    @Override
    @Transactional
    public ServiceResult<Void> activateAssessorAndSendDiversitySurvey(long userId) {
        return getUser(userId)
                .andOnSuccess(this::activateUser)
                .andOnSuccessReturnVoid(this::sendAssessorDiversitySurvey);
    }

    private ServiceResult<Void> sendApplicantDiversitySurvey(User user) {
        return userSurveyService.sendApplicantDiversitySurvey(user);
    }

    private ServiceResult<Void> sendAssessorDiversitySurvey(User user) {
        return userSurveyService.sendAssessorDiversitySurvey(user);
    }

    private ServiceResult<UserResource> createUserWithUid(User user, String password, AddressResource addressResource) {

        ServiceResult<String> uidFromIdpResult = idpService.createUserRecordWithUid(user.getEmail(), password);

        return uidFromIdpResult.andOnSuccessReturn(uidFromIdp -> {
            user.setUid(uidFromIdp);
            user.setStatus(UserStatus.INACTIVE);
            Profile profile = new Profile();
            if (addressResource != null) profile.setAddress(addressMapper.mapToDomain(addressResource));
            Profile savedProfile = profileRepository.save(profile);
            user.setProfileId(savedProfile.getId());
            User savedUser = userRepository.save(user);

            return userMapper.mapToResource(savedUser);
        });
    }

    private ServiceResult<User> addRoleToUser(User user, Role role) {
        if (!user.hasRole(role)) {
            user.addRole(role);
        }
        return serviceSuccess(user);
    }

    private ServiceResult<User> addUserToOrganisation(User user, Long organisationId) {
        return find(organisation(organisationId)).andOnSuccessReturn(org -> {
            org.addUser(user);
            return user;
        });
    }

    private User assembleUserFromResource(UserResource userResource) {
        User newUser = new User();
        newUser.setFirstName(userResource.getFirstName());
        newUser.setLastName(userResource.getLastName());
        newUser.setEmail(userResource.getEmail());
        newUser.setTitle(userResource.getTitle());
        newUser.setPhoneNumber(userResource.getPhoneNumber());
        newUser.setDisability(userResource.getDisability());
        newUser.setGender(userResource.getGender());
        newUser.setEthnicity(ethnicityMapper.mapIdToDomain(userResource.getEthnicity()));
        newUser.setAllowMarketingEmails(userResource.getAllowMarketingEmails());
        newUser.setRoles(new HashSet<>(userResource.getRoles()));

        return newUser;
    }

    private ServiceResult<User> agreeLatestSiteTermsAndConditionsForUser(User user) {
        return termsAndConditionsService.getLatestSiteTermsAndConditions().andOnSuccessReturn(termsAndConditions -> {
            if (user.getTermsAndConditionsIds() == null) {
                user.setTermsAndConditionsIds(new LinkedHashSet<>());
            }
            user.getTermsAndConditionsIds().add(termsAndConditions.getId());
            return user;
        });
    }

    @Override
    @Transactional
    public ServiceResult<Void> sendUserVerificationEmail(final UserResource user, final Optional<Long> competitionId) {
        final Token token = createEmailVerificationToken(user, competitionId);
        final Notification notification = getEmailVerificationNotification(user, token);
        return notificationService.sendNotification(notification, EMAIL);
    }

    @Override
    @Transactional
    public ServiceResult<Void> resendUserVerificationEmail(final UserResource user) {
        final Token token = refreshEmailVerificationToken(user);
        final Notification notification = getEmailVerificationNotification(user, token);
        return notificationService.sendNotification(notification, EMAIL);
    }

    private Notification getEmailVerificationNotification(final UserResource user, final Token token) {
        final List<NotificationTarget> to = singletonList(new UserNotificationTarget(user.getName(), user.getEmail()));
        return new Notification(systemNotificationSource, to, Notifications.VERIFY_EMAIL_ADDRESS, asMap("verificationLink", format("%s/registration/verify-email/%s", webBaseUrl, token.getHash())));
    }

    private Token createEmailVerificationToken(final UserResource user, final Optional<Long> competitionId) {
        final String emailVerificationHash = getEmailVerificationHash(user);

        final ObjectNode extraInfo = factory.objectNode();
        competitionId.ifPresent(aLong -> extraInfo.put("competitionId", aLong));
        final Token token = new Token(TokenType.VERIFY_EMAIL_ADDRESS, User.class.getName(), user.getId(), emailVerificationHash, now(), extraInfo);
        return tokenRepository.save(token);
    }

    private Token refreshEmailVerificationToken(final UserResource user) {
        final String emailVerificationHash = getEmailVerificationHash(user);
        final Token token = tokenRepository.findByTypeAndClassNameAndClassPk(TokenType.VERIFY_EMAIL_ADDRESS, User.class.getName(), user.getId()).get();
        token.setHash(emailVerificationHash);
        token.setUpdated(now());
        return tokenRepository.save(token);
    }

    private String getEmailVerificationHash(final UserResource user) {
        final int random = (int) Math.ceil(Math.random() * 1000); // random number from 1 to 1000
        final String hash = format("%s==%s==%s", user.getId(), user.getEmail(), random);
        return encoder.encode(hash);
    }

    @Override
    @Transactional
    public ServiceResult<Void> createInternalUser(String inviteHash, InternalUserRegistrationResource internalUserRegistrationResource) {
        return getByHash(inviteHash).andOnSuccess(roleInvite ->
                getInternalRoleResources(roleInvite.getTarget()).andOnSuccess(roleResource -> {
                    internalUserRegistrationResource.setEmail(roleInvite.getEmail());
                    internalUserRegistrationResource.setRoles(roleResource);
                    return createUser(internalUserRegistrationResource)
                            .andOnSuccess(() -> updateInviteStatus(roleInvite))
                            .andOnSuccessReturnVoid();
                }));
    }

    private ServiceResult<List<Role>> getInternalRoleResources(Role role) {
        if (role == IFS_ADMINISTRATOR){
            return getIFSAdminRoles(role); // IFS Admin has multiple roles
        } else {
            return serviceSuccess(singletonList(role));
        }
    }

    private ServiceResult<Void> createUser(InternalUserRegistrationResource internalUserRegistrationResource) {
        final UserResource userResource = internalUserRegistrationResource.toUserResource();

        return validateUser(userResource).
                andOnSuccess(validUser -> {
                    final User user = userMapper.mapToDomain(userResource);
                    return createUserWithUid(user, userResource.getPassword()).
                            andOnSuccess(this::activateUser).andOnSuccessReturnVoid();
                });
    }

    private ServiceResult<Void> updateInviteStatus(RoleInvite roleInvite) {
        roleInvite.open();
        roleInviteRepository.save(roleInvite);
        return serviceSuccess();
    }

    private ServiceResult<List<Role>> getIFSAdminRoles(Role roleType) {
        return serviceSuccess( asList(roleType, Role.PROJECT_FINANCE) );
    }

    private ServiceResult<RoleInvite> getByHash(String hash) {
        return find(roleInviteRepository.getByHash(hash), notFoundError(RoleInvite.class, hash));
    }

    private ServiceResult<User> createUserWithUid(User user, String password) {
        ServiceResult<String> uidFromIdpResult = idpService.createUserRecordWithUid(user.getEmail(), password);

        return uidFromIdpResult.andOnSuccess(uidFromIdp -> {
            user.setUid(uidFromIdp);
            Profile profile = new Profile();
            Profile savedProfile = profileRepository.save(profile);
            user.setProfileId(savedProfile.getId());
            User createdUser = userRepository.save(user);
            return serviceSuccess(createdUser);
        });
    }

    @Override
    @Transactional
    public ServiceResult<Void> editInternalUser(UserResource userToEdit, Role userRoleType) {

        return validateInternalUserRole(userRoleType)
                .andOnSuccess(() -> ServiceResult.getNonNullValue(userRepository.findOne(userToEdit.getId()), notFoundError(User.class)))
                .andOnSuccess(user -> getInternalRoleResources(userRoleType)
                    .andOnSuccess(roleResources -> {
                        Set<Role> roleList = new HashSet<>(roleResources);
                        user.setFirstName(userToEdit.getFirstName());
                        user.setLastName(userToEdit.getLastName());
                        user.setRoles(roleList);
                        userRepository.save(user);
                        return serviceSuccess();
                    })
                );
    }

    private ServiceResult<Void> validateInternalUserRole(Role userRoleType) {

        return Role.internalRoles().contains(userRoleType) ?
                serviceSuccess() : serviceFailure(NOT_AN_INTERNAL_USER_ROLE);
    }
}
