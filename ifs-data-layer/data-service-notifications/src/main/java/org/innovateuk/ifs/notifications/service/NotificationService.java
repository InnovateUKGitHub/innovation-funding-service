package org.innovateuk.ifs.notifications.service;

import org.innovateuk.ifs.commons.security.NotSecured;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.notifications.resource.Notification;
import org.innovateuk.ifs.notifications.resource.NotificationMedium;

/**
 * A service responsible for sending generic notifications out from the IFS application using various mediums
 */
public interface NotificationService {

    @NotSecured(value = "Not secured", mustBeSecuredByOtherServices = false)
    ServiceResult<Void> sendNotification(Notification notification, NotificationMedium notificationMedium, NotificationMedium... otherNotificationMedia);

    @NotSecured(value = "Not secured", mustBeSecuredByOtherServices = false)
    ServiceResult<Void> sendNotificationWithFlush(Notification notification, NotificationMedium notificationMedium, NotificationMedium... otherNotificationMedia);
}
