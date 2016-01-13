package com.worth.ifs.notifications.service;

import com.worth.ifs.notifications.resource.NotificationMedium;
import com.worth.ifs.notifications.resource.Notification;
import com.worth.ifs.transactional.ServiceResult;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.worth.ifs.notifications.service.NotificationServiceImpl.ServiceFailures.NOTIFICATION_SENDER_NOT_FOUND;
import static com.worth.ifs.notifications.service.NotificationServiceImpl.ServiceFailures.UNABLE_TO_SEND_NOTIFICATIONS;
import static com.worth.ifs.transactional.ServiceResult.*;
import static com.worth.ifs.util.CollectionFunctions.*;

/**
 * Implementation of a generic NotificationService that will use appropriate NotificationSender implementations
 * to send notifications to users based upon a set of NotificationMediums
 */
@Service
public class NotificationServiceImpl implements NotificationService {

    private static final Log LOG = LogFactory.getLog(NotificationServiceImpl.class);

    enum ServiceFailures {
        NOTIFICATION_SENDER_NOT_FOUND,
        UNABLE_TO_SEND_NOTIFICATIONS
    }

    @Autowired
    private List<NotificationSender> notificationSendingServices;

    private Map<NotificationMedium, NotificationSender> servicesByMedia;

    @PostConstruct
    void constructServicesByMediaMap() {
        servicesByMedia = simpleToMap(notificationSendingServices, NotificationSender::getNotificationMedium);
    }

    @Override
    public ServiceResult<Notification> sendNotification(Notification notification, NotificationMedium notificationMedium, NotificationMedium... otherNotificationMedia) {

        Set<NotificationMedium> allMediaToSendNotificationBy = new LinkedHashSet<>(combineLists(notificationMedium, otherNotificationMedia));

        List<ServiceResult<Notification>> results = simpleMap(allMediaToSendNotificationBy, medium ->
                getNotificationSender(medium).map(serviceForMedium ->
                        serviceForMedium.sendNotification(notification)));

        return anyFailures(results, failureSupplier(UNABLE_TO_SEND_NOTIFICATIONS), successSupplier(notification));
    }

    private ServiceResult<NotificationSender> getNotificationSender(NotificationMedium medium) {
        return nonNull(servicesByMedia.get(medium), NOTIFICATION_SENDER_NOT_FOUND);
    }
}
