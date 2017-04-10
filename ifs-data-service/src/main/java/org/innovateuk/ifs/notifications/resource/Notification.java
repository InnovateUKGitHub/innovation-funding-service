package org.innovateuk.ifs.notifications.resource;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Collections.emptyMap;
import static org.innovateuk.ifs.util.MapFunctions.combineMaps;

/**
 * A DTO reporesenting a message that we wish to send out via one or more mediums.  The Notification itself holds the
 * wherewithalls with which to construct an appropriate message based on the mediums chosen to send the notification via.
 */
public class Notification {

    private NotificationSource from;

    private List<NotificationTarget> to;

    /**
     * A key with which the end "sending" services can use to find the appropriate message body for the medium they represent
     */
    private Enum<?> messageKey;

    /**
     * The arguments that are applicable to all Notification Targets that are available to use as replacement tokens in the message to be constructed by the end "sending" services
     */
    private Map<String, Object> globalArguments = emptyMap();

    /**
     * The arguments that are applicable per NotificationTarget that are available to use as replacement tokens in the message to be constructed by the end "sending" services
     */
    private Map<NotificationTarget, Map<String, Object>> perNotificationTargetArguments = emptyMap();

    /**
     * For builder use only
     */
    public Notification() {
    	// no-arg constructor
    }

    public Notification(NotificationSource from, List<NotificationTarget> to, Enum<?> messageKey, Map<String, Object> globalArguments) {
        this.from = from;
        this.to = to;
        this.messageKey = messageKey;
        this.globalArguments = globalArguments;
    }

    public Notification(NotificationSource from, List<NotificationTarget> to, Enum<?> messageKey, Map<String, Object> globalArguments, Map<NotificationTarget, Map<String, Object>> perNotificationTargetArguments) {
        this(from, to, messageKey, globalArguments);
        this.perNotificationTargetArguments = perNotificationTargetArguments;
    }

    public NotificationSource getFrom() {
        return from;
    }

    public List<NotificationTarget> getTo() {
        return to;
    }

    public Enum<?> getMessageKey() {
        return messageKey;
    }

    public Map<String, Object> getGlobalArguments() {
        return globalArguments;
    }

    public Map<String, Object> getTemplateArgumentsForRecipient(NotificationTarget recipient) {

        Map<String, Object> templateReplacements = new HashMap<>(getGlobalArguments());
        Map<String, Object> recipientSpecificTemplateReplacements = getPerNotificationTargetArguments().get(recipient);

        if (recipientSpecificTemplateReplacements != null) {
            return combineMaps(templateReplacements, recipientSpecificTemplateReplacements);
        }

        return templateReplacements;
    }

    public Map<NotificationTarget, Map<String, Object>> getPerNotificationTargetArguments() {
        return perNotificationTargetArguments;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        Notification that = (Notification) o;

        return new EqualsBuilder()
                .append(from, that.from)
                .append(to, that.to)
                .append(messageKey, that.messageKey)
                .append(globalArguments, that.globalArguments)
                .append(perNotificationTargetArguments, that.perNotificationTargetArguments)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(from)
                .append(to)
                .append(messageKey)
                .append(globalArguments)
                .append(perNotificationTargetArguments)
                .toHashCode();
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
