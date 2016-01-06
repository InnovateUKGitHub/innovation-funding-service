package com.worth.ifs.notifications.service;

import com.worth.ifs.notifications.resource.NotificationMedium;
import com.worth.ifs.notifications.resource.NotificationResource;
import com.worth.ifs.security.NotSecured;

/**
 * A service responsible for sending generic notifications out from the IFS application using various mediums
 */
public interface NotificationService {

    @NotSecured("NotificationService to be used within the context of some other secured service")
    void sendNotification(NotificationResource notification, NotificationMedium notificationMedium, NotificationMedium... otherNotificationMedia);
}
