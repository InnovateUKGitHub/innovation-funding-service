package com.worth.ifs.notifications.resource;

import com.worth.ifs.user.domain.User;

/**
 * Represents a User as the source of a given Notification
 */
public class UserNotificationSourceResource implements NotificationSource {

    private User user;

    public UserNotificationSourceResource(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }
}
