package org.innovateuk.ifs.events;

import org.springframework.context.ApplicationEvent;

/**
 * This event is for tracking the creation of the user in ldap so that
 * it can be rolled back in the event of a sil outage and the email
 * is unable to be sent.
 */
public class UserCreationEvent extends ApplicationEvent {

    private String uuid;

    public UserCreationEvent(Object source, String uuid) {
        super(source);
        this.uuid = uuid;
    }

    public String getUuid() {
        return uuid;
    }
}
