package org.innovateuk.ifs.notifications.resource;

/**
 * Marker interface to represent some target of a Notification, be it a registered user, an unregistered person's email address, an IM address etc
 */
public interface NotificationTarget {

    String getName();

    String getEmailAddress();
}
