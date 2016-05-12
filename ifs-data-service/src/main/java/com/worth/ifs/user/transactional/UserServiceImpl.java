package com.worth.ifs.user.transactional;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.worth.ifs.authentication.service.IdentityProviderService;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.notifications.resource.*;
import com.worth.ifs.notifications.service.NotificationService;
import com.worth.ifs.token.domain.Token;
import com.worth.ifs.token.resource.TokenType;
import com.worth.ifs.token.repository.TokenRepository;
import com.worth.ifs.transactional.BaseTransactionalService;
import com.worth.ifs.user.domain.ProcessRole;
import com.worth.ifs.user.domain.User;
import com.worth.ifs.user.resource.UserStatus;
import com.worth.ifs.user.mapper.UserMapper;
import com.worth.ifs.user.repository.UserRepository;
import com.worth.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.worth.ifs.commons.error.CommonErrors.notFoundError;
import static com.worth.ifs.commons.service.ServiceResult.serviceFailure;
import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;
import static com.worth.ifs.notifications.resource.NotificationMedium.EMAIL;
import static com.worth.ifs.util.EntityLookupCallbacks.find;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toSet;
import static com.worth.ifs.util.CollectionFunctions.simpleMap;

/**
 * A Service that covers basic operations concerning Users
 */
@Service
public class UserServiceImpl extends BaseTransactionalService implements UserService {
    final JsonNodeFactory factory = JsonNodeFactory.instance;

    enum Notifications {
        VERIFY_EMAIL_ADDRESS,
        RESET_PASSWORD
    }

    @Value("${ifs.web.baseURL}")
    private String webBaseUrl;

    @Autowired
    private UserRepository repository;

    @Autowired
    private TokenRepository tokenRepository;

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
    public ServiceResult<UserResource> getUserResourceByUid(final String uid) {
        return find(repository.findOneByUid(uid), notFoundError(UserResource.class, uid)).andOnSuccessReturn(userMapper::mapToResource);
    }

    @Override
    public ServiceResult<UserResource> getUserById(final Long id) {
        return super.getUser(id).andOnSuccessReturn(userMapper::mapToResource);
    }

    @Override
    public ServiceResult<List<UserResource>> findAll() {
        return serviceSuccess(usersToResources(repository.findAll()));
    }

    @Override
    public ServiceResult<UserResource> findByEmail(final String email) {
        return find(repository.findByEmail(email), notFoundError(User.class, email)).andOnSuccessReturn(userMapper::mapToResource);
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
        if(UserStatus.ACTIVE.equals(user.getStatus())){
            String hash = getAndSavePasswordResetToken(user);

            NotificationSource from = systemNotificationSource;
            NotificationTarget to = new ExternalUserNotificationTarget(user.getName(), user.getEmail());

            Map<String, Object> notificationArguments = new HashMap<>();
            notificationArguments.put("passwordResetLink", getPasswordResetLink(hash));

            Notification notification = new Notification(from, singletonList(to), Notifications.RESET_PASSWORD, notificationArguments);
            ServiceResult<Notification> result = notificationService.sendNotification(notification, EMAIL);
            return result.andOnSuccessReturnVoid();
        }else{
            return serviceFailure(notFoundError(UserResource.class, user.getEmail(), UserStatus.ACTIVE));
        }
    }

    @Override
    public ServiceResult<Void> checkPasswordResetHashValidity(String hash) {
        Optional<Token> token = tokenRepository.findByHash(hash);
        if(token.isPresent() && TokenType.RESET_PASSWORD.equals(token.get().getType())) {
            return serviceSuccess();
        }
        return serviceFailure(notFoundError(Token.class, hash));
    }

    private String getAndSavePasswordResetToken(UserResource user) {
        String hash = getRandomHash();
        Token token = new Token(TokenType.RESET_PASSWORD, User.class.getName(), user.getId(), hash, factory.objectNode());
        tokenRepository.save(token);
        return hash;
    }

    @Override
    public ServiceResult<Void> changePassword(String hash, String password){
        Optional<Token> token = tokenRepository.findByHash(hash);
        if(token.isPresent() && TokenType.RESET_PASSWORD.equals(token.get().getType())) {

            return find(user(token.get().getClassPk())).andOnSuccess(user -> {

                UserResource userResource = userMapper.mapToResource(user);

                return passwordPolicyValidator.validatePassword(password, userResource).andOnSuccessReturnVoid(() ->
                    identityProviderService.updateUserPassword(userResource.getUid(), password).andOnSuccess(() -> {
                        tokenRepository.delete(token.get());
                    })
                );
            });
        }
        return serviceFailure(notFoundError(Token.class, hash));
    }


    private String getRandomHash() {
        return UUID.randomUUID().toString();
    }

    private String getPasswordResetLink(String hash) {
        return String.format("%s/login/reset-password/hash/%s", webBaseUrl, hash);
    }

    private List<UserResource> usersToResources(List<User> filtered) {
        return simpleMap(filtered, user -> userMapper.mapToResource(user));
    }
}
