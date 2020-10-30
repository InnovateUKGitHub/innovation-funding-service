package org.innovateuk.ifs.notifications.resource;

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.Map;

import static java.util.Collections.emptyMap;

/**
 * A DTO representing a message that we wish to send out via one or more mediums.  The Notification itself holds the
 * wherewithalls with which to construct an appropriate message based on the mediums chosen to send the notification via.
 */
public class NotificationMessage {

    private NotificationTarget to;

    private Map<String, Object> arguments = emptyMap();

    private NotificationMessage() {
    }

    public NotificationMessage(NotificationTarget to) {
        this.to = to;
    }

    public NotificationMessage(NotificationTarget to, Map<String, Object> arguments) {
        this.to = to;
        this.arguments = arguments;
    }

    public NotificationTarget getTo() {
        return to;
    }

    public Map<String, Object> getArguments() {
        return arguments;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
