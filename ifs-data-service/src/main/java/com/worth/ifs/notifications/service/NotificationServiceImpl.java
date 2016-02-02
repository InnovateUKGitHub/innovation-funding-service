package com.worth.ifs.notifications.service;

import com.worth.ifs.notifications.resource.Notification;
import com.worth.ifs.notifications.resource.NotificationMedium;
import com.worth.ifs.commons.error.Error;
import com.worth.ifs.commons.error.ErrorTemplate;
import com.worth.ifs.commons.service.ServiceResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.worth.ifs.notifications.service.NotificationServiceImpl.ServiceFailures.UNABLE_TO_SEND_NOTIFICATIONS;
import static com.worth.ifs.transactional.BaseTransactionalService.Failures.NOT_FOUND_ENTITY;
import static com.worth.ifs.commons.service.ServiceResult.*;
import static com.worth.ifs.util.CollectionFunctions.*;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

/**
 * Implementation of a generic NotificationService that will use appropriate NotificationSender implementations
 * to send notifications to users based upon a set of NotificationMediums
 */
@Service
public class NotificationServiceImpl implements NotificationService {

    public enum ServiceFailures implements ErrorTemplate {

        UNABLE_TO_SEND_NOTIFICATIONS("Unable to send the Notifications", INTERNAL_SERVER_ERROR)
        ;

        private String errorMessage;
        private HttpStatus category;

        ServiceFailures(String errorMessage, HttpStatus category) {
            this.errorMessage = errorMessage;
            this.category = category;
        }

        @Override
        public String getErrorKey() {
            return name();
        }

        @Override
        public String getErrorMessage() {
            return errorMessage;
        }

        @Override
        public HttpStatus getCategory() {
            return category;
        }
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

        return handlingErrors(new Error(UNABLE_TO_SEND_NOTIFICATIONS), () -> {

            Set<NotificationMedium> allMediaToSendNotificationBy = new LinkedHashSet<>(combineLists(notificationMedium, otherNotificationMedia));

            List<ServiceResult<Notification>> results = simpleMap(allMediaToSendNotificationBy, medium ->
                    getNotificationSender(medium).map(serviceForMedium ->
                            serviceForMedium.sendNotification(notification)));

            return anyFailures(results, serviceFailure(new Error(UNABLE_TO_SEND_NOTIFICATIONS)), serviceSuccess(notification));
        });
    }

    private ServiceResult<NotificationSender> getNotificationSender(NotificationMedium medium) {
        return nonNull(servicesByMedia.get(medium), new Error(NOT_FOUND_ENTITY, NotificationMedium.class, medium));
    }
}
