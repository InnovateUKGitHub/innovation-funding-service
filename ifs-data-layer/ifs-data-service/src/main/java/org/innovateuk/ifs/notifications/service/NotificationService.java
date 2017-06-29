package org.innovateuk.ifs.notifications.service;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.notifications.resource.Notification;
import org.innovateuk.ifs.notifications.resource.NotificationMedium;
import org.innovateuk.ifs.commons.security.NotSecured;

/**
 * A service responsible for sending generic notifications out from the IFS application using various mediums
 */
public interface NotificationService {

    @NotSecured(value = "This Service is to be used within other secured services", mustBeSecuredByOtherServices = true)
    ServiceResult<Void> sendNotification(Notification notification, NotificationMedium notificationMedium, NotificationMedium... otherNotificationMedia);
}
