package org.innovateuk.ifs.notifications.resource;

import org.innovateuk.ifs.user.domain.User;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Represents a User as the target of a given Notification
 */
public class UserNotificationTarget implements NotificationTarget {

    private String name;
    private String emailAddress;

    public UserNotificationTarget(User user) {
        this.name = user.getName();
        this.emailAddress = user.getEmail();
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

        return new EqualsBuilder()
                .append(name, that.name)
                .append(emailAddress, that.emailAddress)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(name)
                .append(emailAddress)
                .toHashCode();
    }
}
