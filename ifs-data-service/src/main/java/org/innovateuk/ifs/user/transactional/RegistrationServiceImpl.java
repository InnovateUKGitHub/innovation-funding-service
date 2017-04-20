package org.innovateuk.ifs.user.transactional;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.innovateuk.ifs.address.mapper.AddressMapper;
import org.innovateuk.ifs.address.resource.AddressResource;
import org.innovateuk.ifs.authentication.service.IdentityProviderService;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.notifications.resource.ExternalUserNotificationTarget;
import org.innovateuk.ifs.notifications.resource.Notification;
import org.innovateuk.ifs.notifications.resource.NotificationTarget;
import org.innovateuk.ifs.notifications.resource.SystemNotificationSource;
import org.innovateuk.ifs.notifications.service.NotificationService;
import org.innovateuk.ifs.registration.resource.UserRegistrationResource;
import org.innovateuk.ifs.token.domain.Token;
import org.innovateuk.ifs.token.repository.TokenRepository;
import org.innovateuk.ifs.token.resource.TokenType;
import org.innovateuk.ifs.transactional.BaseTransactionalService;
import org.innovateuk.ifs.user.domain.*;
import org.innovateuk.ifs.user.mapper.EthnicityMapper;
import org.innovateuk.ifs.user.mapper.UserMapper;
import org.innovateuk.ifs.user.repository.CompAdminEmailRepository;
import org.innovateuk.ifs.user.repository.ProfileRepository;
import org.innovateuk.ifs.user.repository.ProjectFinanceEmailRepository;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.resource.UserStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.method.P;
import org.springframework.security.crypto.password.StandardPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;

import static java.lang.String.format;
import static java.time.ZonedDateTime.now;
import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.notifications.resource.NotificationMedium.EMAIL;
import static org.innovateuk.ifs.user.resource.UserRoleType.*;
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
    private CompAdminEmailRepository compAdminEmailRepository;

    @Autowired
    private ProjectFinanceEmailRepository projectFinanceEmailRepository;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private SystemNotificationSource systemNotificationSource;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private AddressMapper addressMapper;

    @Autowired
    private PasswordPolicyValidator passwordPolicyValidator;

    @Autowired
    private EthnicityMapper ethnicityMapper;

    @Value("${ifs.web.baseURL}")
    private String webBaseUrl;

    private boolean isUserCompAdmin(final String email) {
        if (StringUtils.hasText(email)) {
            CompAdminEmail existingUserSearch = compAdminEmailRepository.findOneByEmail(email);
            if (existingUserSearch != null) {
                return true;
            }
        }
        return false;
    }

    private boolean isUserProjectFinance(String email) {
        if (StringUtils.hasText(email)) {
            ProjectFinanceEmail existingUserSearch = projectFinanceEmailRepository.findOneByEmail(email);
            if (existingUserSearch != null) {
                return true;
            }
        }
        return false;
    }

    @Override
    public ServiceResult<UserResource> createUser(@P("user") UserRegistrationResource userRegistrationResource) {
        final UserResource userResource = userRegistrationResource.toUserResource();

        return validateUser(userResource, userResource.getPassword()).
                andOnSuccess(validUser -> {
                        final User user = userMapper.mapToDomain(userResource);
                        return createUserWithUid(user, userResource.getPassword(), userRegistrationResource.getAddress());
                });
    }

    @Override
    public ServiceResult<UserResource> createOrganisationUser(Long organisationId, UserResource userResource) {
        String roleName;
        if (isUserCompAdmin(userResource.getEmail())) {
            roleName = COMP_ADMIN.getName();
        } else if (isUserProjectFinance(userResource.getEmail())) {
            roleName = PROJECT_FINANCE.getName();
        } else {
            roleName = APPLICANT.getName();
        }
        User newUser = assembleUserFromResource(userResource);
        return validateUser(userResource, userResource.getPassword()).
                andOnSuccess(
                        () -> addUserToOrganisation(newUser, organisationId).
                                andOnSuccess(user -> addRoleToUser(user, roleName))).
                andOnSuccess(
                        () -> createUserWithUid(newUser, userResource.getPassword(), null)
                );
    }

    private ServiceResult<UserResource> validateUser(UserResource userResource, String password) {
        return passwordPolicyValidator.validatePassword(password, userResource).andOnSuccessReturn(() -> userResource);
    }

    @Override
    public ServiceResult<Void> activateUser(Long userId) {
        return getUser(userId).andOnSuccessReturnVoid(u -> {
            idpService.activateUser(u.getUid());
            u.setStatus(UserStatus.ACTIVE);
            userRepository.save(u);
        });
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
            final UserResource userResource = userMapper.mapToResource(savedUser);
            return userResource;
        });
    }

    private ServiceResult<User> addRoleToUser(User user, String roleName) {
        return getRole(roleName).andOnSuccessReturn(role -> {
            user.addRole(role);
            return user;
        });

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

        return newUser;
    }

    @Override
    public ServiceResult<Void> sendUserVerificationEmail(final UserResource user, final Optional<Long> competitionId) {
        final Token token = createEmailVerificationToken(user, competitionId);
        final Notification notification = getEmailVerificationNotification(user, token);
        return notificationService.sendNotification(notification, EMAIL);
    }

    @Override
    public ServiceResult<Void> resendUserVerificationEmail(final UserResource user) {
        final Token token = refreshEmailVerificationToken(user);
        final Notification notification = getEmailVerificationNotification(user, token);
        return notificationService.sendNotification(notification, EMAIL);
    }

    private Notification getEmailVerificationNotification(final UserResource user, final Token token) {
        final List<NotificationTarget> to = singletonList(new ExternalUserNotificationTarget(user.getName(), user.getEmail()));
        return new Notification(systemNotificationSource, to, Notifications.VERIFY_EMAIL_ADDRESS, asMap("verificationLink", format("%s/registration/verify-email/%s", webBaseUrl, token.getHash())));
    }

    private Token createEmailVerificationToken(final UserResource user, final Optional<Long> competitionId) {
        final String emailVerificationHash = getEmailVerificationHash(user);

        final ObjectNode extraInfo = factory.objectNode();
        if (competitionId.isPresent()) {
            extraInfo.put("competitionId", competitionId.get());
        }
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
}
