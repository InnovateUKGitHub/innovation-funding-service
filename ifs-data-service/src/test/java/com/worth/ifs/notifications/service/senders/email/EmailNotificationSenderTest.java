package com.worth.ifs.notifications.service.senders.email;

import com.worth.ifs.BaseUnitTestMocksTest;
import com.worth.ifs.email.resource.EmailAddress;
import com.worth.ifs.notifications.resource.Notification;
import com.worth.ifs.notifications.resource.UserNotificationSource;
import com.worth.ifs.notifications.resource.UserNotificationTarget;
import com.worth.ifs.notifications.service.NotificationTemplateRenderer;
import com.worth.ifs.transactional.ServiceResult;
import com.worth.ifs.user.domain.User;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static com.worth.ifs.BuilderAmendFunctions.name;
import static com.worth.ifs.notifications.builders.NotificationBuilder.newNotification;
import static com.worth.ifs.notifications.resource.NotificationMedium.EMAIL;
import static com.worth.ifs.notifications.service.FreemarkerNotificationTemplateRenderer.ServiceErrors.UNABLE_TO_RENDER_TEMPLATE;
import static com.worth.ifs.notifications.service.senders.email.EmailNotificationSender.EMAIL_NOTIFICATION_TEMPLATES_PATH;
import static com.worth.ifs.notifications.service.senders.email.EmailNotificationSender.ServiceFailures.EMAILS_NOT_SENT;
import static com.worth.ifs.transactional.ServiceResult.failure;
import static com.worth.ifs.transactional.ServiceResult.success;
import static com.worth.ifs.user.builder.UserBuilder.newUser;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

    private User senderUser = newUser().with(name("Sender")).withEmailAddress("sender@email.com").build();
    private User recipientUser1 = newUser().with(name("Recipient 1")).withEmailAddress("recipient1@email.com").build();
    private User recipientUser2 = newUser().with(name("Recipient 2")).withEmailAddress("recipient2@email.com").build();

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

        when(rendererMock.renderTemplate(sender, recipient1, EMAIL_NOTIFICATION_TEMPLATES_PATH + "dummy_message_key_subject.txt", notification.getArguments())).thenReturn(success("My subject"));
        when(rendererMock.renderTemplate(sender, recipient1, EMAIL_NOTIFICATION_TEMPLATES_PATH + "dummy_message_key_text_plain.txt", notification.getArguments())).thenReturn(success("Plain text body"));
        when(rendererMock.renderTemplate(sender, recipient1, EMAIL_NOTIFICATION_TEMPLATES_PATH + "dummy_message_key_text_html.html", notification.getArguments())).thenReturn(success("HTML body"));

        when(rendererMock.renderTemplate(sender, recipient2, EMAIL_NOTIFICATION_TEMPLATES_PATH + "dummy_message_key_subject.txt", notification.getArguments())).thenReturn(success("My subject 2"));
        when(rendererMock.renderTemplate(sender, recipient2, EMAIL_NOTIFICATION_TEMPLATES_PATH + "dummy_message_key_text_plain.txt", notification.getArguments())).thenReturn(success("Plain text body 2"));
        when(rendererMock.renderTemplate(sender, recipient2, EMAIL_NOTIFICATION_TEMPLATES_PATH + "dummy_message_key_text_html.html", notification.getArguments())).thenReturn(success("HTML body 2"));

        when(emailServiceMock.sendEmail(senderEmail, singletonList(recipient1Email), "My subject", "Plain text body", "HTML body")).thenReturn(success(singletonList(recipient1Email)));
        when(emailServiceMock.sendEmail(senderEmail, singletonList(recipient2Email), "My subject 2", "Plain text body 2", "HTML body 2")).thenReturn(success(singletonList(recipient2Email)));

        ServiceResult<Notification> results = notificationSender.sendNotification(notification);
        assertTrue(results.isRight());

        verify(emailServiceMock).sendEmail(senderEmail, singletonList(recipient1Email), "My subject", "Plain text body", "HTML body");
        verify(emailServiceMock).sendEmail(senderEmail, singletonList(recipient2Email), "My subject 2", "Plain text body 2", "HTML body 2");
    }

    @Test
    public void testSendNotificationButEmailServiceFails() {

        when(rendererMock.renderTemplate(sender, recipient1, EMAIL_NOTIFICATION_TEMPLATES_PATH + "dummy_message_key_subject.txt", notification.getArguments())).thenReturn(success("My subject"));
        when(rendererMock.renderTemplate(sender, recipient1, EMAIL_NOTIFICATION_TEMPLATES_PATH + "dummy_message_key_text_plain.txt", notification.getArguments())).thenReturn(success("Plain text body"));
        when(rendererMock.renderTemplate(sender, recipient1, EMAIL_NOTIFICATION_TEMPLATES_PATH + "dummy_message_key_text_html.html", notification.getArguments())).thenReturn(success("HTML body"));

        when(rendererMock.renderTemplate(sender, recipient2, EMAIL_NOTIFICATION_TEMPLATES_PATH + "dummy_message_key_subject.txt", notification.getArguments())).thenReturn(success("My subject 2"));
        when(rendererMock.renderTemplate(sender, recipient2, EMAIL_NOTIFICATION_TEMPLATES_PATH + "dummy_message_key_text_plain.txt", notification.getArguments())).thenReturn(success("Plain text body 2"));
        when(rendererMock.renderTemplate(sender, recipient2, EMAIL_NOTIFICATION_TEMPLATES_PATH + "dummy_message_key_text_html.html", notification.getArguments())).thenReturn(success("HTML body 2"));

        when(emailServiceMock.sendEmail(senderEmail, singletonList(recipient1Email), "My subject", "Plain text body", "HTML body")).thenReturn(failure(EMAILS_NOT_SENT));
        when(emailServiceMock.sendEmail(senderEmail, singletonList(recipient2Email), "My subject 2", "Plain text body 2", "HTML body 2")).thenReturn(failure(EMAILS_NOT_SENT));

        ServiceResult<Notification> results = notificationSender.sendNotification(notification);
        assertTrue(results.isLeft());
        assertTrue(results.getLeft().is(EMAILS_NOT_SENT));

        verify(emailServiceMock).sendEmail(senderEmail, singletonList(recipient1Email), "My subject", "Plain text body", "HTML body");
        verify(emailServiceMock).sendEmail(senderEmail, singletonList(recipient2Email), "My subject 2", "Plain text body 2", "HTML body 2");
    }

    @Test
    public void testSendNotificationButEmailServiceFailsPartially() {

        when(rendererMock.renderTemplate(sender, recipient1, EMAIL_NOTIFICATION_TEMPLATES_PATH + "dummy_message_key_subject.txt", notification.getArguments())).thenReturn(success("My subject"));
        when(rendererMock.renderTemplate(sender, recipient1, EMAIL_NOTIFICATION_TEMPLATES_PATH + "dummy_message_key_text_plain.txt", notification.getArguments())).thenReturn(success("Plain text body"));
        when(rendererMock.renderTemplate(sender, recipient1, EMAIL_NOTIFICATION_TEMPLATES_PATH + "dummy_message_key_text_html.html", notification.getArguments())).thenReturn(success("HTML body"));

        when(rendererMock.renderTemplate(sender, recipient2, EMAIL_NOTIFICATION_TEMPLATES_PATH + "dummy_message_key_subject.txt", notification.getArguments())).thenReturn(success("My subject 2"));
        when(rendererMock.renderTemplate(sender, recipient2, EMAIL_NOTIFICATION_TEMPLATES_PATH + "dummy_message_key_text_plain.txt", notification.getArguments())).thenReturn(success("Plain text body 2"));
        when(rendererMock.renderTemplate(sender, recipient2, EMAIL_NOTIFICATION_TEMPLATES_PATH + "dummy_message_key_text_html.html", notification.getArguments())).thenReturn(success("HTML body 2"));

        when(emailServiceMock.sendEmail(senderEmail, asList(recipient1Email), "My subject", "Plain text body", "HTML body")).thenReturn(failure(EMAILS_NOT_SENT));
        when(emailServiceMock.sendEmail(senderEmail, asList(recipient2Email), "My subject 2", "Plain text body 2", "HTML body 2")).thenReturn(success(singletonList(recipient2Email)));

        ServiceResult<Notification> results = notificationSender.sendNotification(notification);
        assertTrue(results.isLeft());
        assertTrue(results.getLeft().is(EMAILS_NOT_SENT));

        verify(emailServiceMock).sendEmail(senderEmail, asList(recipient1Email), "My subject", "Plain text body", "HTML body");
        verify(emailServiceMock).sendEmail(senderEmail, asList(recipient2Email), "My subject 2", "Plain text body 2", "HTML body 2");
    }

    @Test
    public void testSendNotificationButRenderTemplateFails() {

        when(rendererMock.renderTemplate(sender, recipient1, EMAIL_NOTIFICATION_TEMPLATES_PATH + "dummy_message_key_subject.txt", notification.getArguments())).thenReturn(failure(UNABLE_TO_RENDER_TEMPLATE));

        when(rendererMock.renderTemplate(sender, recipient2, EMAIL_NOTIFICATION_TEMPLATES_PATH + "dummy_message_key_subject.txt", notification.getArguments())).thenReturn(success("My subject 2"));
        when(rendererMock.renderTemplate(sender, recipient2, EMAIL_NOTIFICATION_TEMPLATES_PATH + "dummy_message_key_text_plain.txt", notification.getArguments())).thenReturn(failure(UNABLE_TO_RENDER_TEMPLATE));

        ServiceResult<Notification> results = notificationSender.sendNotification(notification);
        assertTrue(results.isLeft());
        assertTrue(results.getLeft().is(EMAILS_NOT_SENT));

        verify(emailServiceMock, never()).sendEmail(senderEmail, asList(recipient1Email), "My subject", "Plain text body", "HTML body");
        verify(emailServiceMock, never()).sendEmail(senderEmail, asList(recipient2Email), "My subject 2", "Plain text body 2", "HTML body 2");
    }

}
