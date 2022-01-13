package org.innovateuk.ifs.interview.resource;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.time.ZonedDateTime;

public class InterviewApplicationSentInviteResource {
    private String subject;
    private String content;
    private ZonedDateTime assigned;

    public InterviewApplicationSentInviteResource() {}

    public InterviewApplicationSentInviteResource(String subject, String content, ZonedDateTime assigned) {
        this.subject = subject;
        this.content = content;
        this.assigned = assigned;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public ZonedDateTime getAssigned() {
        return assigned;
    }

    public void setAssigned(ZonedDateTime assigned) {
        this.assigned = assigned;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        InterviewApplicationSentInviteResource that = (InterviewApplicationSentInviteResource) o;

        return new EqualsBuilder()
                .append(subject, that.subject)
                .append(content, that.content)
                .append(assigned, that.assigned)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(subject)
                .append(content)
                .append(assigned)
                .toHashCode();
    }
}
