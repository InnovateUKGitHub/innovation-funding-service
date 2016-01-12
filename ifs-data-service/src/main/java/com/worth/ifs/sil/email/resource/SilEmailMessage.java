package com.worth.ifs.sil.email.resource;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.List;

import static java.util.Arrays.asList;

/**
 * The Email message envelope for the SIL API
 */
public class SilEmailMessage {

    @JsonProperty("From")
    private SilEmailAddress from;

    @JsonProperty("ToRecipients")
    private List<SilEmailAddress> to;

    @JsonProperty("Subject")
    private String subject;

    @JsonProperty("Body")
    private List<SilEmailBody> body;

    /**
     * For JSON marshalling only
     */
    SilEmailMessage() {

    }

    public SilEmailMessage(SilEmailAddress from, List<SilEmailAddress> to, String subject, SilEmailBody... bodyElements) {
        this.from = from;
        this.to = to;
        this.subject = subject;
        this.body = asList(bodyElements);
    }

    public SilEmailAddress getFrom() {
        return from;
    }

    public List<SilEmailAddress> getTo() {
        return to;
    }

    public String getSubject() {
        return subject;
    }

    public List<SilEmailBody> getBody() {
        return body;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        SilEmailMessage that = (SilEmailMessage) o;

        return new EqualsBuilder()
                .append(from, that.from)
                .append(to, that.to)
                .append(subject, that.subject)
                .append(body, that.body)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(from)
                .append(to)
                .append(subject)
                .append(body)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("from", from)
                .append("to", to)
                .append("subject", subject)
                .append("body", body)
                .toString();
    }
}
