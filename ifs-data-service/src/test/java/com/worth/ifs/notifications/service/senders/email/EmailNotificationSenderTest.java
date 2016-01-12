package com.worth.ifs.notifications.service.senders.email;

import com.worth.ifs.BaseUnitTestMocksTest;
import com.worth.ifs.email.resource.EmailAddressResource;
import com.worth.ifs.notifications.resource.NotificationResource;
import com.worth.ifs.notifications.resource.UserNotificationSourceResource;
import com.worth.ifs.notifications.resource.UserNotificationTargetResource;
import com.worth.ifs.notifications.service.NotificationTemplateRenderer;
import com.worth.ifs.user.domain.User;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static com.worth.ifs.BuilderAmendFunctions.name;
import static com.worth.ifs.notifications.builders.NotificationResourceBuilder.newNotificationResource;
import static com.worth.ifs.notifications.resource.NotificationMedium.EMAIL;
import static com.worth.ifs.notifications.service.senders.email.EmailNotificationSender.EMAIL_NOTIFICATION_TEMPLATES_PATH;
import static com.worth.ifs.user.builder.UserBuilder.newUser;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 *
 */
public class EmailNotificationSenderTest extends BaseUnitTestMocksTest {

    @InjectMocks
    private EmailNotificationSender sender = new EmailNotificationSender();

    @Mock
    private NotificationTemplateRenderer rendererMock;

    @Test
    public void testGetNotificationMedium() {
        assertEquals(EMAIL, sender.getNotificationMedium());
    }

    private enum MessageKeys {
        DUMMY_MESSAGE_KEY
    }

    @Test
    public void testSendNotification() {

        User user = newUser().with(name("Sender")).withEmailAddress("sender@email.com").build();
        UserNotificationSourceResource sender = new UserNotificationSourceResource(user);
        UserNotificationTargetResource recipient1 = new UserNotificationTargetResource(user);
        UserNotificationTargetResource recipient2 = new UserNotificationTargetResource(user);

        NotificationResource notification = newNotificationResource().
                withMessageKey(MessageKeys.DUMMY_MESSAGE_KEY).
                withSource(sender).
                withTargets(asList(recipient1, recipient2)).
                build();

        when(rendererMock.renderTemplate(sender, recipient1, EMAIL_NOTIFICATION_TEMPLATES_PATH + "dummy_message_key_subject", notification.getArguments())).thenReturn("My subject");
        when(rendererMock.renderTemplate(sender, recipient1, EMAIL_NOTIFICATION_TEMPLATES_PATH + "dummy_message_key_text_plain", notification.getArguments())).thenReturn("Plain text body");
        when(rendererMock.renderTemplate(sender, recipient1, EMAIL_NOTIFICATION_TEMPLATES_PATH + "dummy_message_key_text_html", notification.getArguments())).thenReturn("HTML body");

        this.sender.sendNotification(notification);

        EmailAddressResource senderEmail = EmailAddressResourceResolver.fromNotificationSource(sender);
        EmailAddressResource recipient1Email = EmailAddressResourceResolver.fromNotificationTarget(recipient1);
        EmailAddressResource recipient2Email = EmailAddressResourceResolver.fromNotificationTarget(recipient2);

        verify(emailServiceMock).sendEmail(senderEmail, asList(recipient1Email), "My subject", "Plain text body", "HTML body");
        verify(emailServiceMock).sendEmail(senderEmail, asList(recipient2Email), "My subject", "Plain text body", "HTML body");
    }

}
