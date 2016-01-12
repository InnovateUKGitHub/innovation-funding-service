package com.worth.ifs.notifications.service;

import com.worth.ifs.BaseServiceUnitTest;
import com.worth.ifs.notifications.resource.NotificationResource;
import com.worth.ifs.transactional.ServiceResult;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;

import static com.worth.ifs.notifications.builders.NotificationResourceBuilder.newNotificationResource;
import static com.worth.ifs.notifications.resource.NotificationMedium.EMAIL;
import static com.worth.ifs.notifications.resource.NotificationMedium.LOGGING;
import static com.worth.ifs.transactional.ServiceResult.success;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

/**
 * Tests for NotificationServiceImpl
 */
public class NotificationServiceImplTest extends BaseServiceUnitTest<NotificationServiceImpl> {

    private NotificationSender mockLoggingNotificationSender;
    private NotificationSender mockEmailNotificationSender;

    @Override
    protected NotificationServiceImpl supplyServiceUnderTest() {

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

        when(mockEmailNotificationSender.sendNotification(notificationToSend)).thenReturn(success(notificationToSend));

        ServiceResult<NotificationResource> result = service.sendNotification(notificationToSend, EMAIL);
        assertTrue(result.isRight());

        verify(mockEmailNotificationSender).sendNotification(notificationToSend);
        verify(mockLoggingNotificationSender, never()).sendNotification(notificationToSend);
    }

    @Test
    public void testSendNotificationByEmailAndLogging() {

        NotificationResource notificationToSend = newNotificationResource().build();

        when(mockEmailNotificationSender.sendNotification(notificationToSend)).thenReturn(success(notificationToSend));
        when(mockLoggingNotificationSender.sendNotification(notificationToSend)).thenReturn(success(notificationToSend));

        ServiceResult<NotificationResource> result = service.sendNotification(notificationToSend, EMAIL, LOGGING);
        assertTrue(result.isRight());

        verify(mockEmailNotificationSender).sendNotification(notificationToSend);
        verify(mockLoggingNotificationSender).sendNotification(notificationToSend);
    }

    @Test
    public void testSendNotificationByEmailDeclaredTwiceButOnlySendOnce() {

        NotificationResource notificationToSend = newNotificationResource().build();

        when(mockEmailNotificationSender.sendNotification(notificationToSend)).thenReturn(success(notificationToSend));

        ServiceResult<NotificationResource> result = service.sendNotification(notificationToSend, EMAIL, EMAIL);
        assertTrue(result.isRight());

        verify(mockEmailNotificationSender, times(1)).sendNotification(notificationToSend);
        verify(mockLoggingNotificationSender, never()).sendNotification(notificationToSend);
    }

    @Test
    public void testSendNotificationByUnknownMedium() {

        ReflectionTestUtils.setField(service, "notificationSendingServices",
                asList(mockLoggingNotificationSender));

        service.constructServicesByMediaMap();

        NotificationResource notificationToSend = newNotificationResource().build();
        ServiceResult<NotificationResource> result = service.sendNotification(notificationToSend, EMAIL);
        assertTrue(result.isLeft());
    }
}
