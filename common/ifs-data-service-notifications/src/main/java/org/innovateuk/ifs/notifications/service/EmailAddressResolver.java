package org.innovateuk.ifs.notifications.service;

import org.innovateuk.ifs.email.resource.EmailAddress;
import org.innovateuk.ifs.notifications.resource.NotificationSource;
import org.innovateuk.ifs.notifications.resource.NotificationTarget;

/**
 * A helper utility to resolve a set of Email Address details from given Notifications, for the "from" and "to" of the emails to be sent
 */
class EmailAddressResolver {

	private EmailAddressResolver() {}
	
    /**
     * Given a Notification Source, attempts to extract Email Address information from it
     *
     * @param notificationSource
     * @return
     */
    public static EmailAddress fromNotificationSource(NotificationSource notificationSource) {
        return new EmailAddress(notificationSource.getEmailAddress(), notificationSource.getName());
    }

    /**
     * Given a Notification Target, attempts to extract Email Address information from it
     *
     * @param notificationTarget
     * @return
     */
    public static EmailAddress fromNotificationTarget(NotificationTarget notificationTarget) {
        return new EmailAddress(notificationTarget.getEmailAddress(), notificationTarget.getName());
    }
}
