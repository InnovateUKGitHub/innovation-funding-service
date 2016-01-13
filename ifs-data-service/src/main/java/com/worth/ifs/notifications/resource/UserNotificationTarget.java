package com.worth.ifs.notifications.resource;

import com.worth.ifs.user.domain.User;

/**
 * Represents a User as the target of a given Notification
 */
public class UserNotificationTarget implements NotificationTarget {

    private User user;

    public UserNotificationTarget(User user) {
        this.user = user;
    }

    @Override
    public String getName() {
        return user.getName();
    }

    @Override
    public String getEmailAddress() {
        return user.getEmail();
    }
}
