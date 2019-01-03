package org.innovateuk.ifs.events;

import org.springframework.context.ApplicationEvent;

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
