package com.worth.ifs.user.transactional;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.worth.ifs.address.mapper.AddressMapper;
import com.worth.ifs.authentication.service.IdentityProviderService;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.notifications.resource.ExternalUserNotificationTarget;
import com.worth.ifs.notifications.resource.Notification;
import com.worth.ifs.notifications.resource.NotificationTarget;
import com.worth.ifs.notifications.resource.SystemNotificationSource;
import com.worth.ifs.notifications.service.NotificationService;
import com.worth.ifs.registration.resource.UserRegistrationResource;
import com.worth.ifs.token.domain.Token;
import com.worth.ifs.token.repository.TokenRepository;
import com.worth.ifs.token.resource.TokenType;
import com.worth.ifs.transactional.BaseTransactionalService;
import com.worth.ifs.user.domain.*;
import com.worth.ifs.user.mapper.UserMapper;
import com.worth.ifs.user.repository.CompAdminEmailRepository;
import com.worth.ifs.user.repository.ProjectFinanceEmailRepository;
import com.worth.ifs.user.resource.UserResource;
import com.worth.ifs.user.resource.UserStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.method.P;
import org.springframework.security.crypto.password.StandardPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.worth.ifs.notifications.resource.NotificationMedium.EMAIL;
import static com.worth.ifs.user.resource.UserRoleType.*;
import static com.worth.ifs.util.EntityLookupCallbacks.find;
import static com.worth.ifs.util.MapFunctions.asMap;
import static java.lang.String.format;
import static java.time.LocalDateTime.now;
import static java.util.Collections.singletonList;

/**
 * A service around Registration and general user-creation operations
 */
@Service
public class RegistrationServiceImpl extends BaseTransactionalService implements RegistrationService {

    final JsonNodeFactory factory = JsonNodeFactory.instance;

    private static final CharSequence HASH_SALT = "klj12nm6nsdgfnlk12ctw476kl";

    private StandardPasswordEncoder encoder = new StandardPasswordEncoder(HASH_SALT);

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

        return validateUser(userResource, userResource.getPassword()).andOnSuccess(validUser -> {
                    final User user = userMapper.mapToDomain(userResource);
                    user.setProfile(new Profile());
                    user.getProfile().setAddress(addressMapper.mapToDomain(userRegistrationResource.getAddress()));
                    return createUserWithUid(user, userResource.getPassword());
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
        return validateUser(userResource, userResource.getPassword()).andOnSuccess(validUser -> addOrganisationToUser(newUser, organisationId).andOnSuccess(user ->
                addRoleToUser(user, roleName))).andOnSuccess(() ->
                createUserWithUid(newUser, userResource.getPassword()));
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

    private ServiceResult<UserResource> createUserWithUid(User user, String password) {

        ServiceResult<String> uidFromIdpResult = idpService.createUserRecordWithUid(user.getEmail(), password);

        return uidFromIdpResult.andOnSuccessReturn(uidFromIdp -> {
            user.setUid(uidFromIdp);
            user.setStatus(UserStatus.INACTIVE);
            User savedUser = userRepository.save(user);
            final UserResource userResource = userMapper.mapToResource(savedUser);
            return userResource;
        });
    }

    private ServiceResult<User> addRoleToUser(User user, String roleName) {

        return getRole(roleName).andOnSuccessReturn(role -> {

            List<Role> newRoles = user.getRoles() != null ? new ArrayList<>(user.getRoles()) : new ArrayList<>();

            if (!newRoles.contains(role)) {
                newRoles.add(role);
            }

            user.setRoles(newRoles);
            return user;
        });

    }

    private ServiceResult<User> addOrganisationToUser(User user, Long organisationId) {

        return find(organisation(organisationId)).andOnSuccessReturn(userOrganisation -> {

            List<Organisation> userOrganisationList = new ArrayList<>();
            userOrganisationList.add(userOrganisation);
            user.setOrganisations(userOrganisationList);
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
