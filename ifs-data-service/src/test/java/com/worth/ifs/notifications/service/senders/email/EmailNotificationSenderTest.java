package com.worth.ifs.notifications.service.senders.email;

import com.worth.ifs.BaseUnitTestMocksTest;
import com.worth.ifs.email.resource.EmailAddressResource;
import com.worth.ifs.notifications.resource.NotificationResource;
import com.worth.ifs.notifications.resource.UserNotificationSourceResource;
import com.worth.ifs.notifications.resource.UserNotificationTargetResource;
import com.worth.ifs.user.domain.User;
import org.junit.Test;
import org.mockito.InjectMocks;

import static com.worth.ifs.BuilderAmendFunctions.name;
import static com.worth.ifs.notifications.builders.NotificationResourceBuilder.newNotificationResource;
import static com.worth.ifs.notifications.resource.NotificationMedium.EMAIL;
import static com.worth.ifs.user.builder.UserBuilder.newUser;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.verify;

/**
 *
 */
public class EmailNotificationSenderTest extends BaseUnitTestMocksTest {

    @InjectMocks
    private EmailNotificationSender sender = new EmailNotificationSender();

    @Test
    public void testGetNotificationMedium() {
        assertEquals(EMAIL, sender.getNotificationMedium());
    }

    @Test
    public void testSendNotification() {

        User user = newUser().with(name("Sender")).withEmailAddress("sender@email.com").build();
        UserNotificationSourceResource sender = new UserNotificationSourceResource(user);
        UserNotificationTargetResource recipient1 = new UserNotificationTargetResource(user);
        UserNotificationTargetResource recipient2 = new UserNotificationTargetResource(user);

        NotificationResource notification = newNotificationResource().
                withSource(sender).
                withTargets(asList(recipient1, recipient2)).
                build();

        this.sender.sendNotification(notification);

        EmailAddressResource senderEmail = EmailAddressResourceResolver.fromNotificationSource(sender);
        EmailAddressResource recipient1Email = EmailAddressResourceResolver.fromNotificationTarget(recipient1);
        EmailAddressResource recipient2Email = EmailAddressResourceResolver.fromNotificationTarget(recipient2);

        verify(emailServiceMock).sendEmail(eq(senderEmail), isA(String.class), isA(String.class), isA(String.class), eq(asList(recipient1Email, recipient2Email)));
    }

}
