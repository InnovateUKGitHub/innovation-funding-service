package org.innovateuk.ifs.notifications.service.senders;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.notifications.resource.Notification;
import org.innovateuk.ifs.notifications.resource.NotificationMedium;

/**
 * Represents a Component that knows how to send a NotificationResponse out as a message via a particular NotificationMedium
 */
public interface NotificationSender {

    NotificationMedium getNotificationMedium();

    ServiceResult<Notification> sendNotification(Notification notification);

    ServiceResult<Notification> sendNotificationWithFlush(Notification notification);
}
