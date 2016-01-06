package com.worth.ifs.notifications.service;

import com.worth.ifs.notifications.resource.NotificationMedium;
import com.worth.ifs.notifications.resource.NotificationResource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

import static com.worth.ifs.notifications.resource.NotificationMedium.LOGGING;

/**
 * A Service that logs outgoing Notifications
 */
@Component
public class LoggingNotificationSender implements NotificationSender {

    private static final Log LOG = LogFactory.getLog(LoggingNotificationSender.class);

    @Override
    public NotificationMedium getNotificationMedium() {
        return LOGGING;
    }

    @Override
    public void sendNotification(NotificationResource notification) {
        LOG.debug("Sending Notification " + notification);
    }
}
