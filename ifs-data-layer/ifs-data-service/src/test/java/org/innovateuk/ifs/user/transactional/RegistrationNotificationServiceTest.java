package org.innovateuk.ifs.user.transactional;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.notifications.resource.*;
import org.innovateuk.ifs.notifications.service.NotificationService;
import org.innovateuk.ifs.token.domain.Token;
import org.innovateuk.ifs.token.repository.TokenRepository;
import org.innovateuk.ifs.token.resource.TokenType;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.StandardPasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Map;

import static java.time.ZonedDateTime.now;
import static java.util.Collections.singletonList;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.notifications.resource.NotificationMedium.EMAIL;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.util.MapFunctions.asMap;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RegistrationNotificationServiceTest extends BaseServiceUnitTest<RegistrationNotificationService> {

    @Mock
    private PasswordEncoder passwordEncoder;

    private static final String webBaseUrl = "http://ifs-local-dev";

    @Mock
    private TokenRepository tokenRepositoryMock;

    @Mock
    private NotificationService notificationServiceMock;

    @Mock
    private SystemNotificationSource systemNotificationSourceMock;

    @Test
    public void testSendUserVerificationEmailNoCompetitionId() {

        final UserResource userResource = newUserResource()
                .withId(1L)
                .withFirstName("Sample")
                .withLastName("User")
                .withEmail("sample@me.com")
                .build();

        final String hash = "1e627a59879066b44781ca584a23be742d3197dff291245150e62f3d4d3d303e1a87d34fc8a3a2e0";
        ReflectionTestUtils.setField(service, "passwordEncoder", passwordEncoder);
        when(passwordEncoder.encode("1==sample@me.com==700")).thenReturn(hash);

        final Token newToken = new Token(TokenType.VERIFY_EMAIL_ADDRESS, User.class.getName(), userResource.getId(), hash, now(), JsonNodeFactory.instance.objectNode());
        final String verificationLink = String.format("%s/registration/verify-email/%s", webBaseUrl, hash);

        final Map<String, Object> expectedNotificationArguments = asMap("verificationLink", verificationLink);

        final NotificationSource from = systemNotificationSourceMock;
        final NotificationTarget to = new UserNotificationTarget(userResource.getName(), userResource.getEmail());

        final Notification notification = new Notification(from, singletonList(to), RegistrationNotificationService.Notifications.VERIFY_EMAIL_ADDRESS, expectedNotificationArguments);
        when(tokenRepositoryMock.findByTypeAndClassNameAndClassPk(TokenType.VERIFY_EMAIL_ADDRESS, User.class.getName(), 1L)).thenReturn(empty());
        when(tokenRepositoryMock.save(isA(Token.class))).thenReturn(newToken);
        when(notificationServiceMock.sendNotificationWithFlush(notification, EMAIL)).thenReturn(serviceSuccess());

        final ServiceResult<Void> result = service.sendUserVerificationEmail(userResource, empty());
        assertTrue(result.isSuccess());

        verify(tokenRepositoryMock).save(isA(Token.class));
        verify(notificationServiceMock).sendNotificationWithFlush(notification, EMAIL);
    }

    @Test
    public void testSendUserVerificationEmailWithCompetitionId() {

        final UserResource userResource = newUserResource()
                .withId(1L)
                .withFirstName("Sample")
                .withLastName("User")
                .withEmail("sample@me.com")
                .build();

        final String hash = "1e627a59879066b44781ca584a23be742d3197dff291245150e62f3d4d3d303e1a87d34fc8a3a2e0";
        ReflectionTestUtils.setField(service, "encoder", passwordEncoder);
        when(passwordEncoder.encode("1==sample@me.com==700")).thenReturn(hash);

        final Token newToken = new Token(TokenType.VERIFY_EMAIL_ADDRESS, User.class.getName(), userResource.getId(), hash, now(), JsonNodeFactory.instance.objectNode());
        final String verificationLink = String.format("%s/registration/verify-email/%s", webBaseUrl, hash);

        final Map<String, Object> expectedNotificationArguments = asMap("verificationLink", verificationLink);

        final NotificationSource from = systemNotificationSourceMock;
        final NotificationTarget to = new UserNotificationTarget(userResource.getName(), userResource.getEmail());

        final Notification notification = new Notification(from, singletonList(to), RegistrationNotificationService.Notifications.VERIFY_EMAIL_ADDRESS, expectedNotificationArguments);
        when(tokenRepositoryMock.findByTypeAndClassNameAndClassPk(TokenType.VERIFY_EMAIL_ADDRESS, User.class.getName(), 1L)).thenReturn(empty());
        when(tokenRepositoryMock.save(isA(Token.class))).thenReturn(newToken);
        when(notificationServiceMock.sendNotificationWithFlush(notification, EMAIL)).thenReturn(serviceSuccess());

        final ServiceResult<Void> result = service.sendUserVerificationEmail(userResource, of(123L));
        assertTrue(result.isSuccess());

        verify(tokenRepositoryMock).save(isA(Token.class));
        verify(notificationServiceMock).sendNotificationWithFlush(notification, EMAIL);
    }

    @Test
    public void testResendUserVerificationEmail() {

        final UserResource userResource = newUserResource()
                .withId(1L)
                .withFirstName("Sample")
                .withLastName("User")
                .withEmail("sample@me.com")
                .build();

        final String hash = "1e627a59879066b44781ca584a23be742d3197dff291245150e62f3d4d3d303e1a87d34fc8a3a2e0";
        ReflectionTestUtils.setField(service, "encoder", passwordEncoder);
        when(passwordEncoder.encode("1==sample@me.com==700")).thenReturn(hash);

        final Token existingToken = new Token(TokenType.VERIFY_EMAIL_ADDRESS, User.class.getName(), userResource.getId(), "existing-token", now(), JsonNodeFactory.instance.objectNode());
        final Token newToken = new Token(TokenType.VERIFY_EMAIL_ADDRESS, User.class.getName(), userResource.getId(), hash, now(), JsonNodeFactory.instance.objectNode());
        final String verificationLink = String.format("%s/registration/verify-email/%s", webBaseUrl, hash);

        final Map<String, Object> expectedNotificationArguments = asMap("verificationLink", verificationLink);

        final NotificationSource from = systemNotificationSourceMock;
        final NotificationTarget to = new UserNotificationTarget(userResource.getName(), userResource.getEmail());

        final Notification notification = new Notification(from, singletonList(to), RegistrationNotificationService.Notifications.VERIFY_EMAIL_ADDRESS, expectedNotificationArguments);
        when(tokenRepositoryMock.findByTypeAndClassNameAndClassPk(TokenType.VERIFY_EMAIL_ADDRESS, User.class.getName(), 1L)).thenReturn(of(existingToken));
        when(tokenRepositoryMock.save(isA(Token.class))).thenReturn(newToken);
        when(notificationServiceMock.sendNotificationWithFlush(notification, EMAIL)).thenReturn(serviceSuccess());

        final ServiceResult<Void> result = service.resendUserVerificationEmail(userResource);
        assertTrue(result.isSuccess());

        verify(tokenRepositoryMock).save(isA(Token.class));
        verify(notificationServiceMock).sendNotificationWithFlush(notification, EMAIL);
    }

    @Override
    protected RegistrationNotificationService supplyServiceUnderTest() {
        RegistrationNotificationService service = new RegistrationNotificationService();
        ReflectionTestUtils.setField(service, "webBaseUrl", webBaseUrl);
        return service;
    }
}
