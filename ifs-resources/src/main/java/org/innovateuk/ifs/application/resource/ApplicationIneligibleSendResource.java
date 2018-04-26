package org.innovateuk.ifs.application.resource;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Resource for informing applicants their application is ineligible
 */
public class ApplicationIneligibleSendResource {

    private String subject;
    private String message;

    public ApplicationIneligibleSendResource() {
    }

    public ApplicationIneligibleSendResource(String subject, String message) {
        this.subject = subject;
        this.message = message;
    }

    public String getSubject() {
        return subject;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        ApplicationIneligibleSendResource that = (ApplicationIneligibleSendResource) o;

        return new EqualsBuilder()
                .append(subject, that.subject)
                .append(message, that.message)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(subject)
                .append(message)
                .toHashCode();
    }
}
