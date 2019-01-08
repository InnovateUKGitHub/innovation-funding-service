package org.innovateuk.ifs.events;

import org.springframework.context.ApplicationEvent;

/**
 * This event is for tracking the creation of the user in ldap so that
 * it can be rolled back in the event of a sil outage and the email
 * is unable to be sent.
 */
public class UserCreationEvent extends ApplicationEvent {

    private String uuid;
    private String emailAddress;

    public UserCreationEvent(Object source,
                             String uuid,
                             String emailAddress) {
        super(source);
        this.uuid = uuid;
        this.emailAddress = emailAddress;
    }

    public String getUuid() {
        return uuid;
    }

    public String getEmailAddress() {
        return emailAddress;
    }
}
