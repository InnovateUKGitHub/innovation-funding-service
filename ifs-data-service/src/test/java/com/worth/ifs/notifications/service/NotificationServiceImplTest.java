package com.worth.ifs.notifications.service;

import com.worth.ifs.BaseServiceUnitTest;
import com.worth.ifs.notifications.resource.NotificationResource;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;

import static com.worth.ifs.notifications.builders.NotificationResourceBuilder.newNotificationResource;
import static com.worth.ifs.notifications.resource.NotificationMedium.EMAIL;
import static com.worth.ifs.notifications.resource.NotificationMedium.LOGGING;
import static java.util.Arrays.asList;
import static org.mockito.Mockito.*;

/**
 * Tests for NotificationServiceImpl
 */
public class NotificationServiceImplTest extends BaseServiceUnitTest<NotificationService> {

    private NotificationSender mockLoggingNotificationSender;
    private NotificationSender mockEmailNotificationSender;

    @Override
    protected NotificationService supplyServiceUnderTest() {

        mockLoggingNotificationSender = Mockito.mock(NotificationSender.class);
        mockEmailNotificationSender = Mockito.mock(NotificationSender.class);

        when(mockLoggingNotificationSender.getNotificationMedium()).thenReturn(LOGGING);
        when(mockEmailNotificationSender.getNotificationMedium()).thenReturn(EMAIL);

        NotificationServiceImpl notificationService = new NotificationServiceImpl();

        ReflectionTestUtils.setField(notificationService, "notificationSendingServices",
                asList(mockLoggingNotificationSender, mockEmailNotificationSender));

        notificationService.constructServicesByMediaMap();

        return notificationService;
    }

    @Test
    public void testSendNotificationByEmail() {

        NotificationResource notificationToSend = newNotificationResource().build();
        service.sendNotification(notificationToSend, EMAIL);

        verify(mockEmailNotificationSender).sendNotification(notificationToSend);
        verify(mockLoggingNotificationSender, never()).sendNotification(notificationToSend);
    }

    @Test
    public void testSendNotificationByEmailAndLogging() {

        NotificationResource notificationToSend = newNotificationResource().build();
        service.sendNotification(notificationToSend, EMAIL, LOGGING);

        verify(mockEmailNotificationSender).sendNotification(notificationToSend);
        verify(mockLoggingNotificationSender).sendNotification(notificationToSend);
    }

    @Test
    public void testSendNotificationByEmailDeclaredTwiceButOnlySendOnce() {

        NotificationResource notificationToSend = newNotificationResource().build();
        service.sendNotification(notificationToSend, EMAIL, EMAIL);

        verify(mockEmailNotificationSender, times(1)).sendNotification(notificationToSend);
        verify(mockLoggingNotificationSender, never()).sendNotification(notificationToSend);
    }
}
