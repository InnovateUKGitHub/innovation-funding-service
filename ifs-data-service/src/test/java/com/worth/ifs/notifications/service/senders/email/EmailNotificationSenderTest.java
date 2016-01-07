package com.worth.ifs.notifications.service.senders.email;

import com.worth.ifs.BaseUnitTestMocksTest;
import com.worth.ifs.notifications.resource.NotificationResource;
import org.junit.Test;
import org.mockito.InjectMocks;

import static com.worth.ifs.notifications.builders.NotificationResourceBuilder.newNotificationResource;
import static com.worth.ifs.notifications.resource.NotificationMedium.EMAIL;
import static org.junit.Assert.assertEquals;

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

        NotificationResource notification = newNotificationResource().build();
        sender.sendNotification(notification);

//        verify(emailServiceMock).sendEmail();
    }

}
