package org.innovateuk.ifs.user.transactional;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.notifications.resource.Notification;
import org.innovateuk.ifs.notifications.resource.SystemNotificationSource;
import org.innovateuk.ifs.notifications.resource.UserNotificationTarget;
import org.innovateuk.ifs.notifications.service.NotificationService;
import org.innovateuk.ifs.token.domain.Token;
import org.innovateuk.ifs.token.repository.TokenRepository;
import org.innovateuk.ifs.token.resource.TokenType;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.StandardPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

import static java.lang.String.format;
import static java.time.ZonedDateTime.now;
import static org.innovateuk.ifs.notifications.resource.NotificationMedium.EMAIL;
import static org.innovateuk.ifs.util.MapFunctions.asMap;

/**
 * Service for managing notifications around registration
 */
@Service
class RegistrationNotificationService {

    final JsonNodeFactory factory = JsonNodeFactory.instance;

    private PasswordEncoder passwordEncoder = new PasswordEncoder();

    public enum ServiceFailures {
        UNABLE_TO_CREATE_USER
    }

    enum Notifications {
        VERIFY_EMAIL_ADDRESS
    }

    @Autowired
    private TokenRepository tokenRepository;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private SystemNotificationSource systemNotificationSource;

    @Value("${ifs.web.baseURL}")
    private String webBaseUrl;

    ServiceResult<Void> sendUserVerificationEmail(final UserResource user, final Optional<Long> competitionId, final Optional<Long> organisationId,
                                                  final Optional<Long> inviteId, final Optional<Long> projectId) {
        final Token token = createEmailVerificationToken(user, competitionId, organisationId, inviteId, projectId);
        final Notification notification = getEmailVerificationNotification(user, token);
        return notificationService.sendNotificationWithFlush(notification, EMAIL);
    }

    ServiceResult<Void> resendUserVerificationEmail(final UserResource user) {
        final Token token = refreshEmailVerificationToken(user);
        final Notification notification = getEmailVerificationNotification(user, token);
        return notificationService.sendNotificationWithFlush(notification, EMAIL);
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
        return passwordEncoder.encode(hash);
    }

    private Notification getEmailVerificationNotification(final UserResource user, final Token token) {
        return new Notification(systemNotificationSource, new UserNotificationTarget(user.getName(), user.getEmail()), Notifications.VERIFY_EMAIL_ADDRESS, asMap("verificationLink", format("%s/registration/verify-email/%s", webBaseUrl, token.getHash())));
    }

    private Token createEmailVerificationToken(final UserResource user, final Optional<Long> competitionId, final Optional<Long> organisationId,
                                               final Optional<Long> inviteId, final Optional<Long> projectId) {
        final String emailVerificationHash = getEmailVerificationHash(user);

        final ObjectNode extraInfo = factory.objectNode();
        competitionId.ifPresent(aLong -> extraInfo.put("competitionId", aLong));
        organisationId.ifPresent(aLong -> extraInfo.put("organisationId", aLong));
        inviteId.ifPresent(aLong -> extraInfo.put("inviteId", aLong));
        projectId.ifPresent(aLong -> extraInfo.put("projectId", aLong));
        final Token token = new Token(TokenType.VERIFY_EMAIL_ADDRESS, User.class.getName(), user.getId(), emailVerificationHash, now(), extraInfo);
        return tokenRepository.save(token);
    }

    static class PasswordEncoder {
        private StandardPasswordEncoder encoder = new StandardPasswordEncoder(UUID.randomUUID().toString());

        public String encode(String hash) {
            return encoder.encode(hash);
        }
    }
}


