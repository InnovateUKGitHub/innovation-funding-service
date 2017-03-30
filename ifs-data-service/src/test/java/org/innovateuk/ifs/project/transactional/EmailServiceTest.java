package org.innovateuk.ifs.project.transactional;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.commons.error.CommonFailureKeys;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.notifications.resource.*;
import org.junit.Test;

import java.util.Collections;
import java.util.Map;

import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.util.MapFunctions.asMap;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class EmailServiceTest extends BaseServiceUnitTest<EmailService> {

    @Override
    protected EmailService supplyServiceUnderTest() {
        EmailService emailService = new EmailService();
        return emailService;
    }

    enum Notifications {
        TEST_ENUM
    }

    @Test
    public void sendEmailSuccess() {
        Map<String, Object> expectedNotificationArguments = asMap(
                "dashboardUrl", "https://ifs-local-dev/dashboard"
        );
        SystemNotificationSource from = systemNotificationSourceMock;
        NotificationTarget to1 = new ExternalUserNotificationTarget("A Z", "z@abc.com");
        Notification notification1 = new Notification(from, Collections.singletonList(to1), Notifications.TEST_ENUM, expectedNotificationArguments);

        when(notificationServiceMock.sendNotification(notification1, NotificationMedium.EMAIL)).thenReturn(ServiceResult.serviceSuccess());

        ServiceResult<Void> result = service.sendEmail(singletonList(to1), expectedNotificationArguments, Notifications.TEST_ENUM);

        verify(notificationServiceMock).sendNotification(notification1, NotificationMedium.EMAIL);
        assertTrue(result.isSuccess());
    }

    @Test
    public void sendEmailFailure() {
        Map<String, Object> expectedNotificationArguments = asMap(
                "dashboardUrl", "https://ifs-local-dev/dashboard"
        );
        SystemNotificationSource from = systemNotificationSourceMock;
        NotificationTarget to1 = new ExternalUserNotificationTarget("A Z", "z@abc.com");
        Notification notification1 = new Notification(from, Collections.singletonList(to1), Notifications.TEST_ENUM, expectedNotificationArguments);

        when(notificationServiceMock.sendNotification(notification1, NotificationMedium.EMAIL)).thenReturn(ServiceResult.serviceFailure(CommonFailureKeys.GENERAL_FORBIDDEN));

        ServiceResult<Void> result = service.sendEmail(singletonList(to1), expectedNotificationArguments, Notifications.TEST_ENUM);

        verify(notificationServiceMock).sendNotification(notification1, NotificationMedium.EMAIL);

        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(CommonFailureKeys.GENERAL_FORBIDDEN));
    }
}
