package com.worth.ifs.sil.email.resource;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * Represents the body of an email sent to the SIL API
 */
public class SilEmailBody {

    @JsonProperty("ContentType")
    private String contentType;

    @JsonProperty("Content")
    private String content;

    /**
     * For JSON marshalling only
     */
    public SilEmailBody() {

    }

    public SilEmailBody(String contentType, String content) {
        this.contentType = contentType;
        this.content = content;
    }

    public String getContentType() {
        return contentType;
    }

    public String getContent() {
        return content;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        SilEmailBody that = (SilEmailBody) o;

        return new EqualsBuilder()
                .append(contentType, that.contentType)
                .append(content, that.content)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(contentType)
                .append(content)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("contentType", contentType)
                .append("content", content)
                .toString();
    }
}
