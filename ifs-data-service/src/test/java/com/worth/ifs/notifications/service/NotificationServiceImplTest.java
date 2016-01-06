package com.worth.ifs.notifications.service;

import com.worth.ifs.BaseServiceUnitTest;
import com.worth.ifs.notifications.resource.NotificationResource;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;

import static com.worth.ifs.notifications.resource.NotificationMedium.EMAIL;
import static com.worth.ifs.notifications.resource.NotificationMedium.LOGGING;
import static com.worth.ifs.util.MapFunctions.asMap;
import static java.util.Arrays.asList;
import static org.mockito.Mockito.*;

/**
 * Tests for NotificationServiceImpl
 */
public class NotificationServiceImplTest extends BaseServiceUnitTest<NotificationService> {

    private NotificationSendingService mockLoggingNotificationSendingService;
    private NotificationSendingService mockEmailNotificationSendingService;

    @Override
    protected NotificationService supplyServiceUnderTest() {

        mockLoggingNotificationSendingService = Mockito.mock(NotificationSendingService.class);
        mockEmailNotificationSendingService = Mockito.mock(NotificationSendingService.class);

        when(mockLoggingNotificationSendingService.getNotificationMedium()).thenReturn(LOGGING);
        when(mockEmailNotificationSendingService.getNotificationMedium()).thenReturn(EMAIL);

        NotificationServiceImpl notificationService = new NotificationServiceImpl();

        ReflectionTestUtils.setField(notificationService, "notificationSendingServices",
                asList(mockLoggingNotificationSendingService, mockEmailNotificationSendingService));

        notificationService.constructServicesByMediaMap();

        return notificationService;
    }



    private enum TestMessageKeys {

        MESSAGE1, //
        MESSAGE2
    }

    @Test
    public void testSendNotificationByEmail() {

        NotificationResource notificationToSend = new NotificationResource(TestMessageKeys.MESSAGE1, asMap("firstName", "Bob"));
        service.sendNotification(notificationToSend, EMAIL);

        verify(mockEmailNotificationSendingService).sendNotification(notificationToSend);
        verify(mockLoggingNotificationSendingService, never()).sendNotification(notificationToSend);
    }

    @Test
    public void testSendNotificationByEmailAndLogging() {

        NotificationResource notificationToSend = new NotificationResource(TestMessageKeys.MESSAGE1, asMap("firstName", "Bob"));
        service.sendNotification(notificationToSend, EMAIL, LOGGING);

        verify(mockEmailNotificationSendingService).sendNotification(notificationToSend);
        verify(mockLoggingNotificationSendingService).sendNotification(notificationToSend);
    }

    @Test
    public void testSendNotificationByEmailDeclaredTwiceButOnlySendOnce() {

        NotificationResource notificationToSend = new NotificationResource(TestMessageKeys.MESSAGE1, asMap("firstName", "Bob"));
        service.sendNotification(notificationToSend, EMAIL, EMAIL);

        verify(mockEmailNotificationSendingService, times(1)).sendNotification(notificationToSend);
        verify(mockLoggingNotificationSendingService, never()).sendNotification(notificationToSend);
    }
}
