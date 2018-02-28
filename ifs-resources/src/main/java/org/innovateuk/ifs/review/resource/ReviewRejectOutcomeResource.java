package org.innovateuk.ifs.review.resource;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.innovateuk.ifs.commons.validation.constraints.WordCount;

import javax.validation.constraints.Size;

/**
 * DTO for rejecting invites to review Applications on Assessment Panel.
 */
public class ReviewRejectOutcomeResource {

    @Size(max = 5000, message = "{validation.field.too.many.characters}")
    @WordCount(max = 100, message = "{validation.field.max.word.count}")
    private String reason;

    public ReviewRejectOutcomeResource() {
    }

    public ReviewRejectOutcomeResource(String reason) {
        this.reason = reason;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        ReviewRejectOutcomeResource that = (ReviewRejectOutcomeResource) o;

        return new EqualsBuilder()
                .append(reason, that.reason)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(reason)
                .toHashCode();
    }
}