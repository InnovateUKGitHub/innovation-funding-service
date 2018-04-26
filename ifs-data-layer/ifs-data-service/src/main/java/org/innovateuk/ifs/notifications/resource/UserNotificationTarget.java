package org.innovateuk.ifs.notifications.resource;

import java.util.Objects;

/**
 * Represents a User as the target of a given Notification
 */
public class UserNotificationTarget implements NotificationTarget {

    private final String name;

    private final String emailAddress;

    public UserNotificationTarget(String name, String emailAddress) {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserNotificationTarget that = (UserNotificationTarget) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(emailAddress, that.emailAddress);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, emailAddress);
    }
}
