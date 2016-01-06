package com.worth.ifs.notifications.service;

import com.worth.ifs.notifications.resource.NotificationMedium;
import com.worth.ifs.notifications.resource.NotificationResource;
import com.worth.ifs.security.NotSecured;

/**
 * Represents a Service that knows how to send a NotificationResponse out as a message via a particular NotificationMedium
 */
public interface NotificationSendingService {

    @NotSecured("NotificationSendingService to be used within the context of some other secured service (via NotificationService)")
    NotificationMedium getNotificationMedium();

    @NotSecured("NotificationSendingService to be used within the context of some other secured service (via NotificationService)")
    void sendNotification(NotificationResource notification);
}
