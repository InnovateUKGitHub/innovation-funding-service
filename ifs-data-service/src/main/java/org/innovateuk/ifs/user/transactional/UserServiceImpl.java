package org.innovateuk.ifs.user.transactional;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import org.innovateuk.ifs.authentication.service.IdentityProviderService;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.notifications.resource.*;
import org.innovateuk.ifs.notifications.service.NotificationService;
import org.innovateuk.ifs.token.domain.Token;
import org.innovateuk.ifs.token.repository.TokenRepository;
import org.innovateuk.ifs.token.resource.TokenType;
import org.innovateuk.ifs.token.transactional.TokenService;
import org.innovateuk.ifs.transactional.UserTransactionalService;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.mapper.UserMapper;
import org.innovateuk.ifs.user.repository.ProcessRoleRepository;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.resource.UserStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;

import static java.time.ZonedDateTime.now;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toSet;
import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.notifications.resource.NotificationMedium.EMAIL;
import static org.innovateuk.ifs.user.resource.UserStatus.INACTIVE;
import static org.innovateuk.ifs.util.EntityLookupCallbacks.find;

/**
 * A Service that covers basic operations concerning Users
 */
@Service
public class UserServiceImpl extends UserTransactionalService implements UserService {
    final JsonNodeFactory factory = JsonNodeFactory.instance;

    enum Notifications {
        VERIFY_EMAIL_ADDRESS,
        RESET_PASSWORD
    }

    @Value("${ifs.web.baseURL}")
    private String webBaseUrl;

    @Autowired
    private ProcessRoleRepository processRoleRepository;

    @Autowired
    private TokenRepository tokenRepository;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private SystemNotificationSource systemNotificationSource;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private IdentityProviderService identityProviderService;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordPolicyValidator passwordPolicyValidator;

    @Override
    public ServiceResult<UserResource> findByEmail(final String email) {
        return find(userRepository.findByEmail(email), notFoundError(User.class, email)).andOnSuccessReturn(userMapper::mapToResource);
    }

    @Override
    public ServiceResult<UserResource> findInactiveByEmail(String email) {
        return find(userRepository.findByEmailAndStatus(email, INACTIVE), notFoundError(User.class, email, INACTIVE)).andOnSuccessReturn(userMapper::mapToResource);
    }

    @Override
    public ServiceResult<Set<UserResource>> findAssignableUsers(final Long applicationId) {

        List<ProcessRole> roles = processRoleRepository.findByApplicationId(applicationId);
        Set<UserResource> assignables = roles.stream()
                .filter(r -> "leadapplicant".equals(r.getRole().getName()) || "collaborator".equals(r.getRole().getName()))
                .map(ProcessRole::getUser)
                .map(userMapper::mapToResource)
                .collect(toSet());

        return serviceSuccess(assignables);
    }

    @Override
    public ServiceResult<Set<UserResource>> findRelatedUsers(final Long applicationId) {

        List<ProcessRole> roles = processRoleRepository.findByApplicationId(applicationId);

        Set<UserResource> related = roles.stream()
                .map(ProcessRole::getUser)
                .map(userMapper::mapToResource)
                .collect(toSet());

        return serviceSuccess(related);
    }

    @Override
    public ServiceResult<Void> sendPasswordResetNotification(UserResource user) {
        if (UserStatus.ACTIVE.equals(user.getStatus())){
            String hash = getAndSavePasswordResetToken(user);

            NotificationSource from = systemNotificationSource;
            NotificationTarget to = new ExternalUserNotificationTarget(user.getName(), user.getEmail());

            Map<String, Object> notificationArguments = new HashMap<>();
            notificationArguments.put("passwordResetLink", getPasswordResetLink(hash));

            Notification notification = new Notification(from, singletonList(to), Notifications.RESET_PASSWORD, notificationArguments);
            return notificationService.sendNotification(notification, EMAIL);
        } else {
            return serviceFailure(notFoundError(UserResource.class, user.getEmail(), UserStatus.ACTIVE));
        }
    }

    private String getAndSavePasswordResetToken(UserResource user) {
        String hash = getRandomHash();
        Token token = new Token(TokenType.RESET_PASSWORD, User.class.getName(), user.getId(), hash, now(), factory.objectNode());
        tokenRepository.save(token);
        return hash;
    }

    @Override
    public ServiceResult<Void> changePassword(String hash, String password) {
        return tokenService.getPasswordResetToken(hash).andOnSuccess(token ->
            find(user(token.getClassPk())).andOnSuccess(user -> {

                UserResource userResource = userMapper.mapToResource(user);

                return passwordPolicyValidator.validatePassword(password, userResource).andOnSuccessReturnVoid(() ->
                        identityProviderService.updateUserPassword(userResource.getUid(), password).andOnSuccess(() ->
                            tokenRepository.delete(token))
                );
            })
        );
    }

    private String getRandomHash() {
        return UUID.randomUUID().toString();
    }

    private String getPasswordResetLink(String hash) {
        return String.format("%s/login/reset-password/hash/%s", webBaseUrl, hash);
    }

    @Override
    public ServiceResult<Void> updateDetails(UserResource userResource) {
        return find(userRepository.findByEmail(userResource.getEmail()), notFoundError(User.class, userResource.getEmail()))
                .andOnSuccess( user -> {
                    UserResource existingUserResource = userMapper.mapToResource(user);
                    return updateUser(existingUserResource, userResource);
                });
    }

    private ServiceResult<Void> updateUser(UserResource existingUserResource, UserResource updatedUserResource) {
        existingUserResource.setPhoneNumber(updatedUserResource.getPhoneNumber());
        existingUserResource.setTitle(updatedUserResource.getTitle());
        existingUserResource.setLastName(updatedUserResource.getLastName());
        existingUserResource.setFirstName(updatedUserResource.getFirstName());
        existingUserResource.setGender(updatedUserResource.getGender());
        existingUserResource.setDisability(updatedUserResource.getDisability());
        existingUserResource.setEthnicity(updatedUserResource.getEthnicity());
        User existingUser = userMapper.mapToDomain(existingUserResource);
        return serviceSuccess(userRepository.save(existingUser)).andOnSuccessReturnVoid();
    }
}
