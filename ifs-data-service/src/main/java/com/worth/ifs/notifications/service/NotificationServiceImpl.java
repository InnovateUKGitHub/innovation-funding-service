package com.worth.ifs.notifications.service;

import com.worth.ifs.commons.error.Error;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.notifications.resource.Notification;
import com.worth.ifs.notifications.resource.NotificationMedium;
import com.worth.ifs.notifications.service.senders.NotificationSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.worth.ifs.commons.error.CommonFailureKeys.NOTIFICATIONS_UNABLE_TO_SEND_MULTIPLE;
import static com.worth.ifs.commons.error.CommonErrors.notFoundError;
import static com.worth.ifs.commons.service.ServiceResult.*;
import static com.worth.ifs.util.CollectionFunctions.*;

/**
 * Implementation of a generic NotificationService that will use appropriate NotificationSender implementations
 * to send notifications to users based upon a set of NotificationMediums
 */
@Service
public class NotificationServiceImpl implements NotificationService {

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
                getNotificationSender(medium).andOnSuccess(serviceForMedium ->
                        serviceForMedium.sendNotification(notification)));

        return processAnyFailuresOrSucceed(results, serviceFailure(new Error(NOTIFICATIONS_UNABLE_TO_SEND_MULTIPLE)), serviceSuccess(notification));
    }

    private ServiceResult<NotificationSender> getNotificationSender(NotificationMedium medium) {
        return getNonNullValue(servicesByMedia.get(medium), notFoundError(NotificationMedium.class, medium));
    }
}
