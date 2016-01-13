package com.worth.ifs.notifications.resource;

/**
 * Represents the information needed to send a Notification to a User outside of the system
 */
public class ExternalUserNotificationTarget implements NotificationTarget {

    private String name;
    private String emailAddress;

    public ExternalUserNotificationTarget(String name, String emailAddress) {
        this.name = name;
        this.emailAddress = emailAddress;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getEmailAddress() {
        return emailAddress;
    }
}
