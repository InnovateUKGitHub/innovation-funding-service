package com.worth.ifs.notifications.service.senders.email;

import com.worth.ifs.email.resource.EmailAddressResource;
import com.worth.ifs.notifications.resource.NotificationSource;
import com.worth.ifs.notifications.resource.NotificationTarget;
import com.worth.ifs.notifications.resource.UserNotificationSourceResource;
import com.worth.ifs.notifications.resource.UserNotificationTargetResource;
import com.worth.ifs.user.domain.User;

/**
 *
 */
public class EmailAddressResourceResolver {

    /**
     * Given a Notification Source, attempts to extract Email Address information from it
     *
     * @param notificationSource
     * @return
     */
    public static EmailAddressResource fromNotificationSource(NotificationSource notificationSource) {

        if (notificationSource instanceof UserNotificationSourceResource) {
            User user = ((UserNotificationSourceResource) notificationSource).getUser();
            return new EmailAddressResource(user.getEmail(), user.getName());
        }

        throw new IllegalArgumentException("Don't know how to resolve an Email Address from Notification Source of type " + notificationSource.getClass().getSimpleName());
    }

    /**
     * Given a Notification Target, attempts to extract Email Address information from it
     *
     * @param notificationTarget
     * @return
     */
    public static EmailAddressResource fromNotificationTarget(NotificationTarget notificationTarget) {

        if (notificationTarget instanceof UserNotificationTargetResource) {
            User user = ((UserNotificationTargetResource) notificationTarget).getUser();
            return new EmailAddressResource(user.getEmail(), user.getName());
        }

        throw new IllegalArgumentException("Don't know how to resolve an Email Address from Notification Target of type " + notificationTarget.getClass().getSimpleName());
    }
}
