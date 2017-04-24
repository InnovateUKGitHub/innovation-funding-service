package org.innovateuk.ifs.application.resource;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Resource for informing applicants their application is ineligible
 */
public class ApplicationIneligibleSendResource {

    private String subject;
    private String content;

    public ApplicationIneligibleSendResource() {
    }

    public ApplicationIneligibleSendResource(String subject, String content) {
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
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        ApplicationIneligibleSendResource that = (ApplicationIneligibleSendResource) o;

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
