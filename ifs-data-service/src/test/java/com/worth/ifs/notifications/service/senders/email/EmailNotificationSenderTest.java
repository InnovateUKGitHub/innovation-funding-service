package com.worth.ifs.notifications.service.senders.email;

import com.worth.ifs.BaseUnitTestMocksTest;
import com.worth.ifs.commons.error.Error;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.email.resource.EmailAddress;
import com.worth.ifs.notifications.resource.Notification;
import com.worth.ifs.notifications.resource.UserNotificationSource;
import com.worth.ifs.notifications.resource.UserNotificationTarget;
import com.worth.ifs.notifications.service.NotificationTemplateRenderer;
import com.worth.ifs.user.domain.User;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static com.worth.ifs.commons.error.CommonFailureKeys.EMAILS_NOT_SENT_MULTIPLE;
import static com.worth.ifs.commons.error.CommonFailureKeys.NOTIFICATIONS_UNABLE_TO_RENDER_TEMPLATE;
import static com.worth.ifs.commons.service.ServiceResult.serviceFailure;
import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;
import static com.worth.ifs.notifications.builders.NotificationBuilder.newNotification;
import static com.worth.ifs.notifications.resource.NotificationMedium.EMAIL;
import static com.worth.ifs.notifications.service.senders.email.EmailNotificationSender.EMAIL_NOTIFICATION_TEMPLATES_PATH;
import static com.worth.ifs.user.builder.UserBuilder.newUser;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

/**
 *
 */
public class EmailNotificationSenderTest extends BaseUnitTestMocksTest {

    @InjectMocks
    private EmailNotificationSender notificationSender = new EmailNotificationSender();

    @Mock
    private NotificationTemplateRenderer rendererMock;

    @Test
    public void testGetNotificationMedium() {
        assertEquals(EMAIL, notificationSender.getNotificationMedium());
    }

    private enum MessageKeys {
        DUMMY_MESSAGE_KEY
    }

    private User senderUser = newUser().withFirstName("Sender").withLastName("Some").withEmailAddress("sender@email.com").build();
    private User recipientUser1 = newUser().withFirstName("Recipient").withLastName("1").withEmailAddress("recipient1@email.com").build();
    private User recipientUser2 = newUser().withFirstName("Recipient").withLastName("2").withEmailAddress("recipient2@email.com").build();

    private UserNotificationSource sender = new UserNotificationSource(senderUser);
    private UserNotificationTarget recipient1 = new UserNotificationTarget(recipientUser1);
    private UserNotificationTarget recipient2 = new UserNotificationTarget(recipientUser2);

    private EmailAddress senderEmail = EmailAddressResolver.fromNotificationSource(sender);
    private EmailAddress recipient1Email = EmailAddressResolver.fromNotificationTarget(recipient1);
    private EmailAddress recipient2Email = EmailAddressResolver.fromNotificationTarget(recipient2);

    private Notification notification = newNotification().
            withMessageKey(MessageKeys.DUMMY_MESSAGE_KEY).
            withSource(sender).
            withTargets(asList(recipient1, recipient2)).
            build();

    @Test
    public void testSendNotification() {

        when(rendererMock.renderTemplate(sender, recipient1, EMAIL_NOTIFICATION_TEMPLATES_PATH + "dummy_message_key_subject.txt", notification.getGlobalArguments())).thenReturn(serviceSuccess("My subject"));
        when(rendererMock.renderTemplate(sender, recipient1, EMAIL_NOTIFICATION_TEMPLATES_PATH + "dummy_message_key_text_plain.txt", notification.getGlobalArguments())).thenReturn(serviceSuccess("Plain text body"));
        when(rendererMock.renderTemplate(sender, recipient1, EMAIL_NOTIFICATION_TEMPLATES_PATH + "dummy_message_key_text_html.html", notification.getGlobalArguments())).thenReturn(serviceSuccess("HTML body"));

        when(rendererMock.renderTemplate(sender, recipient2, EMAIL_NOTIFICATION_TEMPLATES_PATH + "dummy_message_key_subject.txt", notification.getGlobalArguments())).thenReturn(serviceSuccess("My subject 2"));
        when(rendererMock.renderTemplate(sender, recipient2, EMAIL_NOTIFICATION_TEMPLATES_PATH + "dummy_message_key_text_plain.txt", notification.getGlobalArguments())).thenReturn(serviceSuccess("Plain text body 2"));
        when(rendererMock.renderTemplate(sender, recipient2, EMAIL_NOTIFICATION_TEMPLATES_PATH + "dummy_message_key_text_html.html", notification.getGlobalArguments())).thenReturn(serviceSuccess("HTML body 2"));

        when(emailServiceMock.sendEmail(senderEmail, singletonList(recipient1Email), "My subject", "Plain text body", "HTML body")).thenReturn(serviceSuccess(singletonList(recipient1Email)));
        when(emailServiceMock.sendEmail(senderEmail, singletonList(recipient2Email), "My subject 2", "Plain text body 2", "HTML body 2")).thenReturn(serviceSuccess(singletonList(recipient2Email)));

        ServiceResult<Notification> results = notificationSender.sendNotification(notification);
        assertTrue(results.isSuccess());

        verify(emailServiceMock).sendEmail(senderEmail, singletonList(recipient1Email), "My subject", "Plain text body", "HTML body");
        verify(emailServiceMock).sendEmail(senderEmail, singletonList(recipient2Email), "My subject 2", "Plain text body 2", "HTML body 2");
    }

    @Test
    public void testSendNotificationButEmailServiceFails() {

        when(rendererMock.renderTemplate(sender, recipient1, EMAIL_NOTIFICATION_TEMPLATES_PATH + "dummy_message_key_subject.txt", notification.getGlobalArguments())).thenReturn(serviceSuccess("My subject"));
        when(rendererMock.renderTemplate(sender, recipient1, EMAIL_NOTIFICATION_TEMPLATES_PATH + "dummy_message_key_text_plain.txt", notification.getGlobalArguments())).thenReturn(serviceSuccess("Plain text body"));
        when(rendererMock.renderTemplate(sender, recipient1, EMAIL_NOTIFICATION_TEMPLATES_PATH + "dummy_message_key_text_html.html", notification.getGlobalArguments())).thenReturn(serviceSuccess("HTML body"));

        when(rendererMock.renderTemplate(sender, recipient2, EMAIL_NOTIFICATION_TEMPLATES_PATH + "dummy_message_key_subject.txt", notification.getGlobalArguments())).thenReturn(serviceSuccess("My subject 2"));
        when(rendererMock.renderTemplate(sender, recipient2, EMAIL_NOTIFICATION_TEMPLATES_PATH + "dummy_message_key_text_plain.txt", notification.getGlobalArguments())).thenReturn(serviceSuccess("Plain text body 2"));
        when(rendererMock.renderTemplate(sender, recipient2, EMAIL_NOTIFICATION_TEMPLATES_PATH + "dummy_message_key_text_html.html", notification.getGlobalArguments())).thenReturn(serviceSuccess("HTML body 2"));

        when(emailServiceMock.sendEmail(senderEmail, singletonList(recipient1Email), "My subject", "Plain text body", "HTML body")).thenReturn(serviceFailure(new Error(EMAILS_NOT_SENT_MULTIPLE, INTERNAL_SERVER_ERROR)));
        when(emailServiceMock.sendEmail(senderEmail, singletonList(recipient2Email), "My subject 2", "Plain text body 2", "HTML body 2")).thenReturn(serviceFailure(new Error(EMAILS_NOT_SENT_MULTIPLE, INTERNAL_SERVER_ERROR)));

        ServiceResult<Notification> results = notificationSender.sendNotification(notification);
        assertTrue(results.isFailure());
        assertTrue(results.getFailure().is(EMAILS_NOT_SENT_MULTIPLE));

        verify(emailServiceMock).sendEmail(senderEmail, singletonList(recipient1Email), "My subject", "Plain text body", "HTML body");
        verify(emailServiceMock).sendEmail(senderEmail, singletonList(recipient2Email), "My subject 2", "Plain text body 2", "HTML body 2");
    }

    @Test
    public void testSendNotificationButEmailServiceFailsPartially() {

        when(rendererMock.renderTemplate(sender, recipient1, EMAIL_NOTIFICATION_TEMPLATES_PATH + "dummy_message_key_subject.txt", notification.getGlobalArguments())).thenReturn(serviceSuccess("My subject"));
        when(rendererMock.renderTemplate(sender, recipient1, EMAIL_NOTIFICATION_TEMPLATES_PATH + "dummy_message_key_text_plain.txt", notification.getGlobalArguments())).thenReturn(serviceSuccess("Plain text body"));
        when(rendererMock.renderTemplate(sender, recipient1, EMAIL_NOTIFICATION_TEMPLATES_PATH + "dummy_message_key_text_html.html", notification.getGlobalArguments())).thenReturn(serviceSuccess("HTML body"));

        when(rendererMock.renderTemplate(sender, recipient2, EMAIL_NOTIFICATION_TEMPLATES_PATH + "dummy_message_key_subject.txt", notification.getGlobalArguments())).thenReturn(serviceSuccess("My subject 2"));
        when(rendererMock.renderTemplate(sender, recipient2, EMAIL_NOTIFICATION_TEMPLATES_PATH + "dummy_message_key_text_plain.txt", notification.getGlobalArguments())).thenReturn(serviceSuccess("Plain text body 2"));
        when(rendererMock.renderTemplate(sender, recipient2, EMAIL_NOTIFICATION_TEMPLATES_PATH + "dummy_message_key_text_html.html", notification.getGlobalArguments())).thenReturn(serviceSuccess("HTML body 2"));

        when(emailServiceMock.sendEmail(senderEmail, singletonList(recipient1Email), "My subject", "Plain text body", "HTML body")).thenReturn(serviceFailure(new Error(EMAILS_NOT_SENT_MULTIPLE, INTERNAL_SERVER_ERROR)));
        when(emailServiceMock.sendEmail(senderEmail, singletonList(recipient2Email), "My subject 2", "Plain text body 2", "HTML body 2")).thenReturn(serviceSuccess(singletonList(recipient2Email)));

        ServiceResult<Notification> results = notificationSender.sendNotification(notification);
        assertTrue(results.isFailure());
        assertTrue(results.getFailure().is(EMAILS_NOT_SENT_MULTIPLE));

        verify(emailServiceMock).sendEmail(senderEmail, singletonList(recipient1Email), "My subject", "Plain text body", "HTML body");
        verify(emailServiceMock).sendEmail(senderEmail, singletonList(recipient2Email), "My subject 2", "Plain text body 2", "HTML body 2");
    }

    @Test
    public void testSendNotificationButRenderTemplateFails() {

        when(rendererMock.renderTemplate(sender, recipient1, EMAIL_NOTIFICATION_TEMPLATES_PATH + "dummy_message_key_subject.txt", notification.getGlobalArguments())).thenReturn(serviceFailure(new Error(NOTIFICATIONS_UNABLE_TO_RENDER_TEMPLATE, INTERNAL_SERVER_ERROR)));

        when(rendererMock.renderTemplate(sender, recipient2, EMAIL_NOTIFICATION_TEMPLATES_PATH + "dummy_message_key_subject.txt", notification.getGlobalArguments())).thenReturn(serviceSuccess("My subject 2"));
        when(rendererMock.renderTemplate(sender, recipient2, EMAIL_NOTIFICATION_TEMPLATES_PATH + "dummy_message_key_text_plain.txt", notification.getGlobalArguments())).thenReturn(serviceFailure(new Error(NOTIFICATIONS_UNABLE_TO_RENDER_TEMPLATE, INTERNAL_SERVER_ERROR)));

        ServiceResult<Notification> results = notificationSender.sendNotification(notification);
        assertTrue(results.isFailure());
        assertTrue(results.getFailure().is(EMAILS_NOT_SENT_MULTIPLE));

        verify(emailServiceMock, never()).sendEmail(senderEmail, singletonList(recipient1Email), "My subject", "Plain text body", "HTML body");
        verify(emailServiceMock, never()).sendEmail(senderEmail, singletonList(recipient2Email), "My subject 2", "Plain text body 2", "HTML body 2");
    }

}
