package com.worth.ifs.notifications.service;

import com.worth.ifs.notifications.resource.NotificationMedium;
import com.worth.ifs.notifications.resource.NotificationResource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.worth.ifs.util.CollectionFunctions.combineLists;
import static com.worth.ifs.util.CollectionFunctions.simpleToMap;

/**
 * Implementation of a generic NotificationService that will use appropriate NotificationSendingService implementations
 * to send notifications to users based upon a set of NotificationMediums
 */
public class NotificationServiceImpl implements NotificationService {

    private static final Log LOG = LogFactory.getLog(NotificationServiceImpl.class);

    @Autowired
    private List<NotificationSendingService> notificationSendingServices;

    private Map<NotificationMedium, NotificationSendingService> servicesByMedia;

    @PostConstruct
    public void constructServicesByMediaMap() {
        servicesByMedia = simpleToMap(notificationSendingServices, NotificationSendingService::getNotificationMedium);
    }

    @Override
    public void sendNotification(NotificationResource notification, NotificationMedium notificationMedium, NotificationMedium... otherNotificationMedia) {

        Set<NotificationMedium> allMediaToSendNotificationBy = new LinkedHashSet<>(combineLists(notificationMedium, otherNotificationMedia));

        allMediaToSendNotificationBy.forEach(medium -> {

            NotificationSendingService serviceForMedium = servicesByMedia.get(medium);

            if (serviceForMedium == null) {
                LOG.error("No NotificationSendingService found that can send Notifications via the medium " + medium + " - not sending notitifaction this way");
            }

            serviceForMedium.sendNotification(notification);
        });
    }
}
