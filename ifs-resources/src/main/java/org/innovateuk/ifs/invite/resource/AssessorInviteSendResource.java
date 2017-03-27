package org.innovateuk.ifs.invite.resource;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Resource for invites to be sent
 */
public class AssessorInviteSendResource {

    private String subject;
    private String content;

    public AssessorInviteSendResource() {
    }

    public AssessorInviteSendResource(String subject, String content) {
        this.subject = subject;
        this.content = content;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        AssessorInviteSendResource that = (AssessorInviteSendResource) o;

        return new EqualsBuilder()
                .append(subject, that.subject)
                .append(content, that.content)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(subject)
                .append(content)
                .toHashCode();
    }
}
