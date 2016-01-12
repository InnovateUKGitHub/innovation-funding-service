package com.worth.ifs.notifications.service;

import com.worth.ifs.notifications.resource.NotificationMedium;
import com.worth.ifs.notifications.resource.NotificationResource;
import com.worth.ifs.security.NotSecured;
import com.worth.ifs.transactional.ServiceResult;

/**
 * A service responsible for sending generic notifications out from the IFS application using various mediums
 */
public interface NotificationService {

    @NotSecured("NotificationService to be used within the context of some other secured service")
    ServiceResult<NotificationResource> sendNotification(NotificationResource notification, NotificationMedium notificationMedium, NotificationMedium... otherNotificationMedia);
}
