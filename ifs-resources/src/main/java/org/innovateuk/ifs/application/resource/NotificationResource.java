package org.innovateuk.ifs.application.resource;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.List;

/**
 * This is used for sending the subject and content of a notification,
 * e.g. when sending an email to notify of an application funding decision.
 */
public class NotificationResource {
    private String subject;
    private String messageBody;
    private List<Long> applicationIds;

    public NotificationResource(String subject, String messageBody, List<Long> applicationIds) {
        this.subject = subject;
        this.messageBody = messageBody;
        this.applicationIds = applicationIds;
    }

    public NotificationResource()
    {
        //default constructor
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getMessageBody() {
        return messageBody;
    }

    public void setMessageBody(String messageBody) {
        this.messageBody = messageBody;
    }

    public List<Long> getApplicationIds() {
        return applicationIds;
    }

    public void setApplicationIds(List<Long> applicationIds) {
        this.applicationIds = applicationIds;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        NotificationResource that = (NotificationResource) o;

        return new EqualsBuilder()
                .append(subject, that.subject)
                .append(messageBody, that.messageBody)
                .append(applicationIds, that.applicationIds)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(subject)
                .append(messageBody)
                .append(applicationIds)
                .toHashCode();
    }

}
