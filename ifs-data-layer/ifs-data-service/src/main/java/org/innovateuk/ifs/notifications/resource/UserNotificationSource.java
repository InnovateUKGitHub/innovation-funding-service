package org.innovateuk.ifs.notifications.resource;

/**
 * Represents a User as the source of a given Notification
 */
public class UserNotificationSource implements NotificationSource {

    private final String name;

    private final String emailAddress;

    public UserNotificationSource(String name, String emailAddress) {
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
