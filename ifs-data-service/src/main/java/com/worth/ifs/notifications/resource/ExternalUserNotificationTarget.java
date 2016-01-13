package com.worth.ifs.notifications.resource;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        ExternalUserNotificationTarget that = (ExternalUserNotificationTarget) o;

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
