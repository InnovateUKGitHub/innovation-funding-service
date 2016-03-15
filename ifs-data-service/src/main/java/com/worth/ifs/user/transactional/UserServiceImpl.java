package com.worth.ifs.user.transactional;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.worth.ifs.authentication.service.IdentityProviderService;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.notifications.resource.*;
import com.worth.ifs.notifications.service.NotificationService;
import com.worth.ifs.token.domain.Token;
import com.worth.ifs.token.domain.TokenType;
import com.worth.ifs.token.repository.TokenRepository;
import com.worth.ifs.transactional.BaseTransactionalService;
import com.worth.ifs.user.domain.ProcessRole;
import com.worth.ifs.user.domain.User;
import com.worth.ifs.user.domain.UserStatus;
import com.worth.ifs.user.repository.ProcessRoleRepository;
import com.worth.ifs.user.repository.UserRepository;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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

/**
 * A Service that covers basic operations concerning Users
 */
@Service
public class UserServiceImpl extends BaseTransactionalService implements UserService {
    final JsonNodeFactory factory = JsonNodeFactory.instance;

    private static final Log LOG = LogFactory.getLog(UserServiceImpl.class);

    enum Notifications {
        VERIFY_EMAIL_ADDRESS,
        RESET_PASSWORD
    }

    @Value("${ifs.web.baseURL}")
    private String webBaseUrl;

    @Autowired
    private UserRepository repository;

    @Autowired
    private ProcessRoleRepository processRoleRepository;

    @Autowired
    private TokenRepository tokenRepository;

    @Autowired
    private SystemNotificationSource systemNotificationSource;

    @Autowired
    private NotificationService notificationService;
    @Autowired
    private IdentityProviderService identityProviderService;

    @Override
    public ServiceResult<User> getUserByUid(final String uid) {
        return find(repository.findOneByUid(uid), notFoundError(User.class, uid));
    }

    @Override
    public ServiceResult<User> getUserById(final Long id) {
        return super.getUser(id);
    }

    @Override
    public ServiceResult<List<User>> getUserByName(final String name) {
        return find(repository.findByName(name), notFoundError(User.class, name));
    }

    @Override
    public ServiceResult<List<User>> findAll() {
        return serviceSuccess(repository.findAll());
    }

    @Override
    public ServiceResult<User> findByEmail(final String email) {
        Optional<User> user = repository.findByEmail(email);
        if(user.isPresent()){
            return serviceSuccess(user.get());
        }
        return serviceFailure(notFoundError(User.class, email));
    }

    @Override
    public ServiceResult<Set<User>> findAssignableUsers(final Long applicationId) {

        List<ProcessRole> roles = processRoleRepository.findByApplicationId(applicationId);
        Set<User> assignables = roles.stream()
                .filter(r -> r.getRole().getName().equals("leadapplicant") || r.getRole().getName().equals("collaborator"))
                .map(ProcessRole::getUser)
                .collect(toSet());

        return serviceSuccess(assignables);
    }

    @Override
    public ServiceResult<Set<User>> findRelatedUsers(final Long applicationId) {

        List<ProcessRole> roles = processRoleRepository.findByApplicationId(applicationId);

        Set<User> related = roles.stream()
                .map(ProcessRole::getUser)
                .collect(toSet());

        return serviceSuccess(related);
    }

    @Override
    public ServiceResult<Void> sendPasswordResetNotification(User user) {
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
            return serviceFailure(notFoundError(User.class, user.getEmail(), UserStatus.ACTIVE));
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

    private String getAndSavePasswordResetToken(User user) {
        String hash = getRandomHash();
        Token token = new Token(TokenType.RESET_PASSWORD, User.class.getName(), user.getId(), hash, factory.objectNode());
        tokenRepository.save(token);
        return hash;
    }

    @Override
    public ServiceResult<Void> changePassword(String hash, String password){
        Optional<Token> token = tokenRepository.findByHash(hash);
        if(token.isPresent() && TokenType.RESET_PASSWORD.equals(token.get().getType())) {
            User user = userRepository.findOne(token.get().getClassPk());
            identityProviderService.updateUserPassword(user.getUid(), password);
            userRepository.save(user);
            tokenRepository.delete(token.get());
            return serviceSuccess();
        }
        return serviceFailure(notFoundError(Token.class, hash));
    }


    private String getRandomHash() {
        return UUID.randomUUID().toString();
    }

    private String getPasswordResetLink(String hash) {
        return String.format("%s/login/reset-password/hash/%s", webBaseUrl, hash);
    }

}
