package org.innovateuk.ifs.notifications.service;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.notifications.resource.Notification;
import org.innovateuk.ifs.notifications.resource.NotificationMedium;
import org.innovateuk.ifs.notifications.service.senders.NotificationSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.service.ServiceResult.aggregate;
import static org.innovateuk.ifs.commons.service.ServiceResult.getNonNullValue;
import static org.innovateuk.ifs.util.CollectionFunctions.*;

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
    public ServiceResult<Void> sendNotification(Notification notification, NotificationMedium notificationMedium, NotificationMedium... otherNotificationMedia) {

        Set<NotificationMedium> allMediaToSendNotificationBy = new LinkedHashSet<>(combineLists(notificationMedium, otherNotificationMedia));

        List<ServiceResult<Notification>> results = simpleMap(allMediaToSendNotificationBy, medium ->
                getNotificationSender(medium).andOnSuccess(serviceForMedium ->
                        serviceForMedium.sendNotification(notification)));

        return aggregate(results).andOnSuccessReturnVoid();
    }

    private ServiceResult<NotificationSender> getNotificationSender(NotificationMedium medium) {
        return getNonNullValue(servicesByMedia.get(medium), notFoundError(NotificationMedium.class, medium));
    }
}
