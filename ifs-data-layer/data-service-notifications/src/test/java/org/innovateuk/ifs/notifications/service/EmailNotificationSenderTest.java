package org.innovateuk.ifs.notifications.service;

import org.innovateuk.ifs.BaseUnitTestMocksTest;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.email.resource.EmailAddress;
import org.innovateuk.ifs.email.service.EmailService;
import org.innovateuk.ifs.notifications.resource.Notification;
import org.innovateuk.ifs.notifications.resource.NotificationMessage;
import org.innovateuk.ifs.notifications.resource.UserNotificationSource;
import org.innovateuk.ifs.notifications.resource.UserNotificationTarget;
import org.innovateuk.ifs.user.domain.User;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.*;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.notifications.builders.NotificationBuilder.newNotification;
import static org.innovateuk.ifs.notifications.resource.NotificationMedium.EMAIL;
import static org.innovateuk.ifs.notifications.service.NotificationTemplateRenderer.EMAIL_NOTIFICATION_TEMPLATES_PATH;
import static org.innovateuk.ifs.user.builder.UserBuilder.newUser;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

public class EmailNotificationSenderTest extends BaseUnitTestMocksTest {

    @InjectMocks
    private EmailNotificationSender notificationSender = new EmailNotificationSender();

    @Mock
    private NotificationTemplateRenderer notificationTemplateRendererMock;

    @Mock
    private EmailService emailServiceMock;

    @Mock
    private WhiteBlackDomainFilter whiteBlackDomainFilter;

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

    private UserNotificationSource sender = new UserNotificationSource(senderUser.getName(), senderUser.getEmail());
    private UserNotificationTarget recipient1 = new UserNotificationTarget(recipientUser1.getName(), recipientUser1.getEmail());
    private UserNotificationTarget recipient2 = new UserNotificationTarget(recipientUser2.getName(), recipientUser2.getEmail());

    private EmailAddress senderEmail = EmailAddressResolver.fromNotificationSource(sender);
    private EmailAddress recipient1Email = EmailAddressResolver.fromNotificationTarget(recipient1);
    private EmailAddress recipient2Email = EmailAddressResolver.fromNotificationTarget(recipient2);

    private Notification notification = newNotification().
            withMessageKey(MessageKeys.DUMMY_MESSAGE_KEY).
            withSource(sender).
            withTargets(asList(new NotificationMessage(recipient1), new NotificationMessage(recipient2))).
            build();

    @Test
    public void testSendNotification() {

        when(notificationTemplateRendererMock.renderTemplate(sender, recipient1, EMAIL_NOTIFICATION_TEMPLATES_PATH + "dummy_message_key_subject.txt", notification.getGlobalArguments())).thenReturn(serviceSuccess("My subject"));
        when(notificationTemplateRendererMock.renderTemplate(sender, recipient1, EMAIL_NOTIFICATION_TEMPLATES_PATH + "dummy_message_key_text_plain.txt", notification.getGlobalArguments())).thenReturn(serviceSuccess("Plain text body"));
        when(notificationTemplateRendererMock.renderTemplate(sender, recipient1, EMAIL_NOTIFICATION_TEMPLATES_PATH + "dummy_message_key_text_html.html", notification.getGlobalArguments())).thenReturn(serviceSuccess("HTML body"));

        when(notificationTemplateRendererMock.renderTemplate(sender, recipient2, EMAIL_NOTIFICATION_TEMPLATES_PATH + "dummy_message_key_subject.txt", notification.getGlobalArguments())).thenReturn(serviceSuccess("My subject 2"));
        when(notificationTemplateRendererMock.renderTemplate(sender, recipient2, EMAIL_NOTIFICATION_TEMPLATES_PATH + "dummy_message_key_text_plain.txt", notification.getGlobalArguments())).thenReturn(serviceSuccess("Plain text body 2"));
        when(notificationTemplateRendererMock.renderTemplate(sender, recipient2, EMAIL_NOTIFICATION_TEMPLATES_PATH + "dummy_message_key_text_html.html", notification.getGlobalArguments())).thenReturn(serviceSuccess("HTML body 2"));

        when(emailServiceMock.sendEmail(senderEmail, singletonList(recipient1Email), "My subject", "Plain text body", "HTML body")).thenReturn(serviceSuccess(singletonList(recipient1Email)));
        when(emailServiceMock.sendEmail(senderEmail, singletonList(recipient2Email), "My subject 2", "Plain text body 2", "HTML body 2")).thenReturn(serviceSuccess(singletonList(recipient2Email)));

        when(whiteBlackDomainFilter.passesFilterCheck(any())).thenReturn(true);

        ServiceResult<Notification> results = notificationSender.sendNotification(notification);
        assertTrue(results.isSuccess());

        verify(emailServiceMock).sendEmail(senderEmail, singletonList(recipient1Email), "My subject", "Plain text body", "HTML body");
        verify(emailServiceMock).sendEmail(senderEmail, singletonList(recipient2Email), "My subject 2", "Plain text body 2", "HTML body 2");
    }

    @Test
    public void testSendNotificationButEmailServiceFails() {

        when(notificationTemplateRendererMock.renderTemplate(sender, recipient1, EMAIL_NOTIFICATION_TEMPLATES_PATH + "dummy_message_key_subject.txt", notification.getGlobalArguments())).thenReturn(serviceSuccess("My subject"));
        when(notificationTemplateRendererMock.renderTemplate(sender, recipient1, EMAIL_NOTIFICATION_TEMPLATES_PATH + "dummy_message_key_text_plain.txt", notification.getGlobalArguments())).thenReturn(serviceSuccess("Plain text body"));
        when(notificationTemplateRendererMock.renderTemplate(sender, recipient1, EMAIL_NOTIFICATION_TEMPLATES_PATH + "dummy_message_key_text_html.html", notification.getGlobalArguments())).thenReturn(serviceSuccess("HTML body"));

        when(notificationTemplateRendererMock.renderTemplate(sender, recipient2, EMAIL_NOTIFICATION_TEMPLATES_PATH + "dummy_message_key_subject.txt", notification.getGlobalArguments())).thenReturn(serviceSuccess("My subject 2"));
        when(notificationTemplateRendererMock.renderTemplate(sender, recipient2, EMAIL_NOTIFICATION_TEMPLATES_PATH + "dummy_message_key_text_plain.txt", notification.getGlobalArguments())).thenReturn(serviceSuccess("Plain text body 2"));
        when(notificationTemplateRendererMock.renderTemplate(sender, recipient2, EMAIL_NOTIFICATION_TEMPLATES_PATH + "dummy_message_key_text_html.html", notification.getGlobalArguments())).thenReturn(serviceSuccess("HTML body 2"));

        when(emailServiceMock.sendEmail(senderEmail, singletonList(recipient1Email), "My subject", "Plain text body", "HTML body")).thenReturn(serviceFailure(new Error(EMAILS_NOT_SENT_MULTIPLE, INTERNAL_SERVER_ERROR)));
        when(emailServiceMock.sendEmail(senderEmail, singletonList(recipient2Email), "My subject 2", "Plain text body 2", "HTML body 2")).thenReturn(serviceFailure(new Error(EMAILS_NOT_SENT_MULTIPLE, INTERNAL_SERVER_ERROR)));

        when(whiteBlackDomainFilter.passesFilterCheck(any())).thenReturn(true);

        ServiceResult<Notification> results = notificationSender.sendNotification(notification);
        assertTrue(results.isFailure());
        assertTrue(results.getFailure().is(NOTIFICATIONS_UNABLE_TO_SEND_SINGLE));

        verify(emailServiceMock).sendEmail(senderEmail, singletonList(recipient1Email), "My subject", "Plain text body", "HTML body");
        verify(emailServiceMock).sendEmail(senderEmail, singletonList(recipient2Email), "My subject 2", "Plain text body 2", "HTML body 2");
    }

    @Test
    public void testSendNotificationButEmailServiceFailsPartially() {

        when(notificationTemplateRendererMock.renderTemplate(sender, recipient1, EMAIL_NOTIFICATION_TEMPLATES_PATH + "dummy_message_key_subject.txt", notification.getGlobalArguments())).thenReturn(serviceSuccess("My subject"));
        when(notificationTemplateRendererMock.renderTemplate(sender, recipient1, EMAIL_NOTIFICATION_TEMPLATES_PATH + "dummy_message_key_text_plain.txt", notification.getGlobalArguments())).thenReturn(serviceSuccess("Plain text body"));
        when(notificationTemplateRendererMock.renderTemplate(sender, recipient1, EMAIL_NOTIFICATION_TEMPLATES_PATH + "dummy_message_key_text_html.html", notification.getGlobalArguments())).thenReturn(serviceSuccess("HTML body"));

        when(notificationTemplateRendererMock.renderTemplate(sender, recipient2, EMAIL_NOTIFICATION_TEMPLATES_PATH + "dummy_message_key_subject.txt", notification.getGlobalArguments())).thenReturn(serviceSuccess("My subject 2"));
        when(notificationTemplateRendererMock.renderTemplate(sender, recipient2, EMAIL_NOTIFICATION_TEMPLATES_PATH + "dummy_message_key_text_plain.txt", notification.getGlobalArguments())).thenReturn(serviceSuccess("Plain text body 2"));
        when(notificationTemplateRendererMock.renderTemplate(sender, recipient2, EMAIL_NOTIFICATION_TEMPLATES_PATH + "dummy_message_key_text_html.html", notification.getGlobalArguments())).thenReturn(serviceSuccess("HTML body 2"));

        when(emailServiceMock.sendEmail(senderEmail, singletonList(recipient1Email), "My subject", "Plain text body", "HTML body")).thenReturn(serviceFailure(new Error(NOTIFICATIONS_UNABLE_TO_SEND_SINGLE, INTERNAL_SERVER_ERROR)));
        when(emailServiceMock.sendEmail(senderEmail, singletonList(recipient2Email), "My subject 2", "Plain text body 2", "HTML body 2")).thenReturn(serviceSuccess(singletonList(recipient2Email)));

        when(whiteBlackDomainFilter.passesFilterCheck(any())).thenReturn(true);

        ServiceResult<Notification> results = notificationSender.sendNotification(notification);
        assertTrue(results.isFailure());
        assertTrue(results.getFailure().is(NOTIFICATIONS_UNABLE_TO_SEND_SINGLE));

        verify(emailServiceMock).sendEmail(senderEmail, singletonList(recipient1Email), "My subject", "Plain text body", "HTML body");
        verify(emailServiceMock).sendEmail(senderEmail, singletonList(recipient2Email), "My subject 2", "Plain text body 2", "HTML body 2");
    }

    @Test
    public void testSendNotificationButRenderTemplateFails() {

        when(notificationTemplateRendererMock.renderTemplate(sender, recipient1, EMAIL_NOTIFICATION_TEMPLATES_PATH + "dummy_message_key_subject.txt", notification.getGlobalArguments())).thenReturn(serviceFailure(new Error(NOTIFICATIONS_UNABLE_TO_RENDER_TEMPLATE, "subject")));
        when(notificationTemplateRendererMock.renderTemplate(sender, recipient2, EMAIL_NOTIFICATION_TEMPLATES_PATH + "dummy_message_key_subject.txt", notification.getGlobalArguments())).thenReturn(serviceSuccess("My subject 2"));
        when(notificationTemplateRendererMock.renderTemplate(sender, recipient2, EMAIL_NOTIFICATION_TEMPLATES_PATH + "dummy_message_key_text_plain.txt", notification.getGlobalArguments())).thenReturn(serviceFailure(new Error(NOTIFICATIONS_UNABLE_TO_RENDER_TEMPLATE, "text")));

        when(whiteBlackDomainFilter.passesFilterCheck(any())).thenReturn(true);

        ServiceResult<Notification> results = notificationSender.sendNotification(notification);
        assertTrue(results.isFailure());
        assertEquals(NOTIFICATIONS_UNABLE_TO_RENDER_TEMPLATE.name(), results.getFailure().getErrors().get(0).getErrorKey());
        assertEquals("subject", results.getFailure().getErrors().get(0).getArguments().get(0));
        assertEquals(NOTIFICATIONS_UNABLE_TO_RENDER_TEMPLATE.name(), results.getFailure().getErrors().get(1).getErrorKey());
        assertEquals("text", results.getFailure().getErrors().get(1).getArguments().get(0));

        verify(emailServiceMock, never()).sendEmail(senderEmail, singletonList(recipient1Email), "My subject", "Plain text body", "HTML body");
        verify(emailServiceMock, never()).sendEmail(senderEmail, singletonList(recipient2Email), "My subject 2", "Plain text body 2", "HTML body 2");
    }

}
