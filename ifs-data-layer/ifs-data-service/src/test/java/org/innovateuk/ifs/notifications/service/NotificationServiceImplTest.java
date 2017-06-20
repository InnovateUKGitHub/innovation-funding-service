package org.innovateuk.ifs.notifications.service;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.notifications.resource.Notification;
import org.innovateuk.ifs.notifications.service.senders.NotificationSender;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;

import static org.innovateuk.ifs.commons.error.CommonFailureKeys.EMAILS_NOT_SENT_MULTIPLE;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.NOTIFICATIONS_UNABLE_TO_SEND_MULTIPLE;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.notifications.builders.NotificationBuilder.newNotification;
import static org.innovateuk.ifs.notifications.resource.NotificationMedium.EMAIL;
import static org.innovateuk.ifs.notifications.resource.NotificationMedium.LOGGING;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

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

        Notification notificationToSend = newNotification().build();

        when(mockEmailNotificationSender.sendNotification(notificationToSend)).thenReturn(serviceSuccess(notificationToSend));

        ServiceResult<Void> result = service.sendNotification(notificationToSend, EMAIL);
        assertTrue(result.isSuccess());

        verify(mockEmailNotificationSender).sendNotification(notificationToSend);
        verify(mockLoggingNotificationSender, never()).sendNotification(notificationToSend);
    }

    @Test
    public void testSendNotificationByEmailAndLogging() {

        Notification notificationToSend = newNotification().build();

        when(mockEmailNotificationSender.sendNotification(notificationToSend)).thenReturn(serviceSuccess(notificationToSend));
        when(mockLoggingNotificationSender.sendNotification(notificationToSend)).thenReturn(serviceSuccess(notificationToSend));

        ServiceResult<Void> result = service.sendNotification(notificationToSend, EMAIL, LOGGING);
        assertTrue(result.isSuccess());

        verify(mockEmailNotificationSender).sendNotification(notificationToSend);
        verify(mockLoggingNotificationSender).sendNotification(notificationToSend);
    }

    @Test
    public void testSendNotificationByEmailDeclaredTwiceButOnlySendOnce() {

        Notification notificationToSend = newNotification().build();

        when(mockEmailNotificationSender.sendNotification(notificationToSend)).thenReturn(serviceSuccess(notificationToSend));

        ServiceResult<Void> result = service.sendNotification(notificationToSend, EMAIL, EMAIL);
        assertTrue(result.isSuccess());

        verify(mockEmailNotificationSender, times(1)).sendNotification(notificationToSend);
        verify(mockLoggingNotificationSender, never()).sendNotification(notificationToSend);
    }

    @Test
    public void testSendNotificationByUnknownMedium() {

        ReflectionTestUtils.setField(service, "notificationSendingServices",
                asList(mockLoggingNotificationSender));

        service.constructServicesByMediaMap();

        Notification notificationToSend = newNotification().build();
        ServiceResult<Void> result = service.sendNotification(notificationToSend, EMAIL);
        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(NOTIFICATIONS_UNABLE_TO_SEND_MULTIPLE));
    }

    @Test
    public void testSendNotificationByEmailButSenderFails() {

        Notification notificationToSend = newNotification().build();

        when(mockEmailNotificationSender.sendNotification(notificationToSend)).thenReturn(serviceFailure(new Error(EMAILS_NOT_SENT_MULTIPLE, INTERNAL_SERVER_ERROR)));

        ServiceResult<Void> result = service.sendNotification(notificationToSend, EMAIL);
        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(NOTIFICATIONS_UNABLE_TO_SEND_MULTIPLE));
    }
}
