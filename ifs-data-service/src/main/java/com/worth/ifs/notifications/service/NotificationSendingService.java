package com.worth.ifs.notifications.service;

import com.worth.ifs.notifications.resource.NotificationMedium;
import com.worth.ifs.notifications.resource.NotificationResource;

/**
 * Represents a Service that knows how to send a NotificationResponse out as a message via a particular NotificationMedium
 */
public interface NotificationSendingService {

    NotificationMedium getNotificationMedium();

    void sendNotification(NotificationResource notification);
}
