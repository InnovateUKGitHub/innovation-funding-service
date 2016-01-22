package com.worth.ifs.notifications.resource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Represent the IFS System itself as the source of a Notification i.e. a message sent out to a User by the System
 */
@Component
public class SystemNotificationSource implements NotificationSource {

    @Value("${ifs.system.name}")
    private String name;

    @Value("${ifs.system.email.address}")
    private String emailAddress;

    private SystemNotificationSource() {
        // uninstantiable, apart from by Spring
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
